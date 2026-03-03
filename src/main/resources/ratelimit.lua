-- KEYS[1]: bucket key
-- ARGV[1]: capacity (burst)
-- ARGV[2]: refill_rate (tokens per second)
-- ARGV[3]: requested tokens

local key = KEYS[1]
local capacity = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])
local requested = tonumber(ARGV[3])

-- Usamos el tiempo de Redis (fuente única de verdad)
local time = redis.call("TIME")
local now_sec = tonumber(time[1])
local now_usec = tonumber(time[2])
local now = now_sec + (now_usec / 1000000)

-- Leer estado actual
local state = redis.call("HMGET", key, "tokens", "last_refill")

local tokens = tonumber(state[1])
local last_refill = tonumber(state[2])

-- Inicialización si no existe
if not tokens then
    tokens = capacity
    last_refill = now
end

-- Calcular tiempo transcurrido en segundos
local elapsed = now - last_refill
if elapsed < 0 then
    elapsed = 0
end

-- Refill basado en tiempo absoluto
local new_tokens = tokens + (elapsed * refill_rate)
if new_tokens > capacity then
    new_tokens = capacity
end

-- Actualizar referencia temporal SOLO después del cálculo
tokens = new_tokens
last_refill = now

-- Decidir si se permite
local allowed = 0
if tokens >= requested then
    allowed = 1
    tokens = tokens - requested
end

-- Guardar estado actualizado
redis.call("HMSET", key,
    "tokens", tokens,
    "last_refill", last_refill
)

-- TTL dinámico (2x tiempo de llenado completo)
local ttl = math.ceil((capacity / refill_rate) * 2)
redis.call("EXPIRE", key, math.max(ttl, 60))

return {allowed, math.floor(tokens)}