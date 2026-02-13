# Changelog

Todos los cambios notables en este proyecto se documentarán en este archivo.

El formato se basa en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-02-12

### Agregado
- Endpoint para obtener clima actual `/api/weather/{city}/{country}`
- Endpoint para pronóstico de 15 días `/api/weather/{city}/{country}/days`
- Endpoint para limpiar caché `/api/weather/{city}/{country}/clear-cache`
- Integración con Redis para caché inteligente
- Logging estructurado con SLF4J
- Documentación Swagger/OpenAPI
- Manejo de errores HTTP robusto
- DTOs para respuestas tipadas

### Características
- Soporte para deserialization de tipos Java 8 (LocalDate, LocalDateTime)
- Serialización/deserialization automática con Jackson
- Caché TTL configurable (por defecto 1 hora)
- Validación de parámetros de entrada
- Mensajes de error descriptivos

### Documentación
- README con guía de inicio rápido
- Ejemplos de integración en diferentes lenguajes
- Configuración de logging
- Guía de seguridad para credenciales

## [Próximas versiones]

### Planeado para v1.1.0
- Autenticación por API key para usuarios
- Rate limiting
- Historial de búsquedas
- Métricas y monitoreo

### Planeado para v1.2.0
- Alertas meteorológicas
- Notificaciones por email
- Soporte para múltiples idiomas
- Geolocalización automática

---

**Última actualización:** Febrero 2026

