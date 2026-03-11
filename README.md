# 🚦 RateLimit Service

Servicio de rate limiting multi-tenant de alto rendimiento construido con **Java 21 + Spring Boot 4.x**, usando el algoritmo **Token Bucket** respaldado por Redis para estado en tiempo real y PostgreSQL para configuración y autenticación.

---

## 📐 Arquitectura General

```
Backend del Cliente
        │
        ▼
POST /v1/api/check
        │
        ├─► L1 Cache (Caffeine) ──► API Key → Plan (capacity/rate)
        │         │ miss
        │         ▼
        │      PostgreSQL ──► api_keys + plans
        │
        └─► L2 Cache (Redis) ──► Token Bucket (Lua script, atómico)
```

### Stack Tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 21+ / Spring Boot 4.x |
| Base de Datos | PostgreSQL |
| Estado en Tiempo Real | Redis (Token Bucket) |
| Caché Local | Caffeine Cache (L1) |
| Versionado de Esquema | Flyway |
| Contenedores | Docker & Docker Compose |


---

## 🗄️ Modelo de Datos (PostgreSQL)

### Tablas

**`tenants`** — Empresas o servicios que usan la infraestructura.
- `id` (UUID, PK), `name`, `created_at`, `updated_at`, `status`, `plan_id` (FK)

**`plans`** — Definición de límites de rate.
- `id` (UUID, PK), `name`, `capacity` (ráfaga máxima de tokens), `refill_rate` (tokens/seg), `created_at`, `updated_at`

**`api_keys`** — Credenciales de acceso para los backends clientes.
- `id` (UUID, PK), `key_hash` (SHA-256, indexado), `tenant_id` (FK), `revoked` (Boolean), `created_at`, `updated_at`

**`users`** — Usuarios del sistema por tenant.
- `id` (UUID, PK), `tenant_id` (FK), `email`, `password_hash`, `role`, `created_at`, `updated_at` ,`verified_at`, `deleted_at`

**`refresh_tokens`** — Tokens de larga duración para gestión de sesiones.
- `id` (PK), `user_id` (FK), `token_hash`, `expires_at`, `revoked`, `created_at`, `updated_at`

**`user_tokens`** — Tokens de un solo uso para verificación de email, reset de contraseña y magic login.
- `id`, `user_id` (FK), `type` (`EMAIL_VERIFICATION` | `PASSWORD_RESET` | `MAGIC_LOGIN`), `token_hash`, `expires_at`, `used_at`, `revoked`, `created_at`, `updated_at`

**`invitations`** — Invitaciones de equipo pendientes.
- `id` (UUID), `email`, `tenant_id` (FK), `status` ,`role_to_assign`, `token`, `expires_at`, `version`, `created_at`, `updated_at`

### Índices Creados

```sql
CREATE UNIQUE INDEX idx_users_email_active ON users(email) WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX idx_user_tokens_active_hash
    ON user_tokens(token_hash)
    WHERE revoked = FALSE AND used_at IS NULL;

CREATE INDEX idx_refresh_tokens_user_id
    ON refresh_tokens(user_id);

CREATE UNIQUE INDEX ux_invitation_pending
    ON invitations (email, tenant_id)
    WHERE status = 'PENDING';
```

---

## ⚙️ Estrategia de Caché (L1 & L2)

### L1 — Caffeine (In-Memory)
- **Qué guarda:** `API_KEY → Plan (Capacity / RefillRate)`
- **TTL:** 5 minutos
- **Beneficio:** Elimina consultas SQL repetitivas en el path crítico

### L2 — Redis
- **Qué guarda:** Estado del bucket por usuario final (`tokens` + `lastRefill`)
- **Operación:** Script Lua atómico (evita race conditions)


---

## 🪣 Algoritmo Token Bucket (Lua en Redis)

El script se ejecuta atómicamente en Redis para evitar condiciones de carrera.

**Flujo del script:**

```
1. Recibir: Key, Capacity, RefillRate, Now
2. Calcular tiempo transcurrido desde lastRefill
3. Sumar tokens nuevos sin superar Capacity
4. Si tokens >= 1  →  restar 1, actualizar Redis, retornar ALLOWED
5. Si no           →  retornar BLOCKED
```

---

## 🌐 Definición de API

### Endpoint Principal

#### `POST /v1/api/check`
Evalúa si una petición debe ser permitida o bloqueada.

**Headers:** `X-API-KEY: <YOUR_API_KEY>`

**Respuestas:**

| Código | Significado |
|---|---|
| `200 OK` | Petición permitida. Header: `X-RateLimit-Remaining` |
| `429 Too Many Requests` | Límite excedido. Header: `Retry-After` |
| `401 Unauthorized` | API Key inválida o revocada |

```json
// 200 OK
{ "allowed": true, "remainingTokens": 89, "retryAfterSeconds": 12 }

// 429 Too Many Requests
{ "allowed": false, "remainingTokens": 0, "retry_after_seconds": 5,  }
```

---

### Endpoints de Autenticación

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/v1/api/auth/register` | Crear tenant + usuario, enviar email de verificación |
| `GET` | `/v1/auth/verify?token=…` | Verificar cuenta por email |
| `POST` | `/v1/auth/login` | Retorna Access Token + Refresh Token |
| `POST` | `/v1/auth/refresh` | Intercambia Refresh Token por un par nuevo |

### Endpoints Administrativos *(requieren `ROLE_SYSTEM_ADMIN`)*

| Método | Ruta                        | Descripción                          |
|---|-----------------------------|--------------------------------------|
| `GET` | `/v1/api/admin/tenants`     | Listar todos los tenants             |
| `POST` | `/v1/api/admin/tenants/{tenantId}/subscribe`    | Asigna plan a un tenant              |

### Endpoints de Invitaciones *(requieren `ROLE_TENANT_ADMIN`)*

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/v1/api/tenants/{tenantId}/invitations` | Invitar usuario al equipo |
| `POST` | `/v1/api/invitations/{token}/accept` | Aceptar invitación y crear cuenta |

---

## 👥 Roles de Usuario

| Rol | Permisos |
|---|---|
| `ROLE_SYSTEM_ADMIN` | Modo dios: crear planes, suspender tenants, gestionar todo |
| `ROLE_TENANT_ADMIN` | Invitar/eliminar usuarios, rotar API Keys. Solo actúa sobre su `tenant_id` |
| `ROLE_TENANT_DEVELOPER` | Crear/editar API Keys y reglas de rate limit. . Sin acceso a usuarios ni pagos |
| `ROLE_TENANT_VIEWER` | Solo lectura: dashboards de métricas y estado de keys |

---

## 🔐 Seguridad y Autenticación (JWT)

### Tokens

- **Access Token:** Duración corta (15–60 min). Contiene `roles` y `tenant_id`.
- **Refresh Token:** Duración larga (7–30 días). Almacenado en BD para control de revocación.

### Rotación de Refresh Tokens

Previene el robo de sesiones mediante **Refresh Token Rotation**:

```
1. Cliente envía Refresh Token  →  servidor invalida el token usado
2. Servidor emite nuevo par  →  Access Token + Refresh Token frescos
3. Si se detecta reutilización de un token viejo  →  se revocan TODAS las sesiones del usuario
```

---

## 🗑️ Estrategia de Borrado

| Entidad | Estrategia | Campo |
|---|---|---|
| `users` | Soft delete | `deleted_at` |
| `api_keys` | Soft delete | `revoked` |
| `tenants` | Soft delete | `status = INACTIVE` |
| `invitations` | Hard delete | Al expirar o aceptarse |
| `user_tokens` | Hard delete | Al usarse o revocarse |

---


## 🛡️ Consideraciones de Producción

- **Fail Open:** Si Redis cae, el servicio retorna `allowed: true` para no bloquear a los clientes. Se prioriza disponibilidad sobre restricción.
- **Sin texto plano:** Las API Keys nunca se almacenan en texto plano. Siempre se guarda el hash SHA-256.

---

## 🗺️ Roadmap de Implementación

### Fase 1 — Infraestructura & Dominio
- [ ] Configurar `docker-compose` con Postgres y Redis
- [ ] Crear entidades JPA (`Tenant`, `Plan`, `ApiKey`, `User`)
- [ ] Crear repositorios con búsqueda por hash de key
- [ ] Configurar Flyway con `V1__initial_schema.sql`

### Fase 2 — Service Layer & Redis
- [ ] Configurar `RedisTemplate` en Spring
- [ ] Crear `RateLimitService` cargando el script Lua al arrancar
- [ ] Implementar lógica: L1 Cache → DB → Script Lua en Redis
- [ ] Configurar `RedisMessageListenerContainer` para invalidación de caché
- [ ] Crear `CacheInvalidationMessageListener`

### Fase 3 — Controlador & Seguridad
- [ ] Crear `RateLimitController`
- [ ] Implementar filtro/interceptor para extraer `Authorization: Bearer <key>`
- [ ] Manejo global de excepciones (Fail Open para fallos de Redis)
- [ ] Implementar JWT (Access + Refresh Token con rotación)

### Fase 4 — Testing & Calidad
- [ ] Tests unitarios del algoritmo Token Bucket
- [ ] Tests de integración con `Testcontainers` (Postgres + Redis)
- [ ] Prueba de carga para verificar latencia < 5ms

---

## 🐳 Levantar el Proyecto

```bash
# Levantar infraestructura
docker-compose up -d

# La app aplica las migraciones de Flyway automáticamente al iniciar
./mvnw spring-boot:run
```

---

## 📁 Estructura del Proyecto 

```
src/
├── main/
│   ├── java/com/ratelimit/
│   │   ├── common/        # Configuración de excepciones, seguridad, colas y JWT
│   │   ├── communication/ # Email consumer y producer
│   │   ├── core/          # Controller y servicio del endpoint central
│   │   ├── identity/      # Implementación del usuario y todo lo relacionado a auth
│   │   ├── organization/  # Implementación de la organización y todo lo relacionado
│   └── resources/
│       ├── db/migration/    # Flyway SQL scripts
│       ├── ratelimit.lua/         
└── test/
    ├── service/
```
---
## Infraestuctura
- Despliegue en instancia EC2 de AWS