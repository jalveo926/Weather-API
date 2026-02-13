package com.jalveo.weatherAPI.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CacheService {

    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
        // Registrar el módulo JavaTimeModule para soportar LocalDate, LocalDateTime, etc.
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    //Obtenemos la información cacheada
    public <T> Optional<T> get(String key, Class<T> type) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return Optional.empty();
            }
            // Usar ObjectMapper para deserializar correctamente
            T result = objectMapper.convertValue(value, type);
            return Optional.ofNullable(result);
        } catch (Exception e) {
            logger.error("Redis error para la llave '{}': {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    // Método para obtener listas de objetos desde el caché
    public <T> Optional<List<T>> getList(String key, Class<T> elementType) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return Optional.empty();
            }
            // Usar ObjectMapper con TypeReference para deserializar correctamente listas
            List<T> result = objectMapper.convertValue(value, new TypeReference<>() {});
            return Optional.ofNullable(result);
        } catch (Exception e) {
            logger.error("Redis error al obtener lista para la llave '{}': {}", key, e.getMessage());
            return Optional.empty();
        }
    }


    //Guardamos el cache
    public void save(String key, Object value, long seconds){
        try {
            if (key == null || key.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La clave no puede ser nula o vacía");
            }
            if (value == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El valor no puede ser nulo");
            }
            if (seconds <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tiempo de expiración debe ser mayor a 0");
            }
            redisTemplate.opsForValue().set(key, value, seconds, java.util.concurrent.TimeUnit.SECONDS);
        } catch (ResponseStatusException e) {
            throw e; // Re-lanzamos las excepciones de validación
        } catch (Exception e) {
            // Si Redis está caído, logueamos pero no lanzamos error para que la app continúe
            logger.error("Error al guardar en cache: '{}': {}  ", e.getMessage(),key);
        }
    }

    //Borramos el cache
    public void delete(String key) {
        try {
            if (key == null || key.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La clave no puede ser nula o vacía");
            }
            redisTemplate.delete(key);
        } catch (ResponseStatusException e) {
            throw e; // Re-lanzamos las excepciones de validación
        } catch (Exception e) {
            // Si Redis está caído, logueamos pero no lanzamos error
            logger.error("Error al eliminar del caché: '{}': {} " , e.getMessage(),key);
        }
    }
}
