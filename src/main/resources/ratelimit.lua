-- KEYS[1]: El ID del bucket (ej: "rl:tenant_id:key_hash")
-- ARGV[1]: Capacidad máxima del bucket (Burst/Capacity del Plan)
-- ARGV[2]: Tasa de recarga (Tokens por segundo del Plan)
-- ARGV[3]: Timestamp actual en segundos (Enviado desde Java)
-- ARGV[4]: Cantidad de tokens pedidos (Usualmente 1)

local key = KEYS[1]
local capacity = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local requested = tonumber(ARGV[4])

-- 1. Recuperar el estado actual del bucket desde un Hash de Redis
-- 'tokens': cantidad disponible, 'last_refill': última vez que se actualizó
local last_state = redis.call('HMGET', key, 'tokens', 'last_refill')
local tokens = tonumber(last_state[1]) or capacity
local last_refill = tonumber(last_state[2]) or now

-- 2. Calcular cuántos tokens se han generado por el paso del tiempo
local elapsed = math.max(0, now - last_refill)
local generated = elapsed * refill_rate

-- 3. Actualizar el contador sin superar la capacidad máxima (Burst)
tokens = math.min(capacity, tokens + generated)

-- 4. Determinar si la petición es permitida
local allowed = tokens >= requested
if allowed then
    tokens = tokens - requested
end

-- 5. Guardar el nuevo estado en Redis
redis.call('HMSET', key, 'tokens', tokens, 'last_refill', now)

-- 6. Configurar expiración automática para no llenar la RAM de Redis con llaves viejas
-- Expiramos la llave si no se usa en el tiempo que tardaría en llenarse el balde x 2
local ttl = math.ceil(capacity / refill_rate) * 2
redis.call('EXPIRE', key, math.max(ttl, 60))

-- Retornamos: [1 si es permitido / 0 si no, tokens restantes]
return {allowed and 1 or 0, math.floor(tokens)}