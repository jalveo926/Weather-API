# Weather API 🌤

API REST para obtener información del clima usando Visual Crossing.

## ⚠️ **IMPORTANTE - Configuración de Seguridad**

### Archivos sensibles

Los siguientes archivos **NO deben ser subidos a GitHub**:
- `application.properties` (contiene API keys)
- `.env` (variables de entorno)
- Cualquier archivo con credenciales

Estos archivos ya están ignorados en `.gitignore`.

### Configuración inicial

1. **Copia el archivo de ejemplo:**
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

2. **Edita `application.properties` con tus credenciales:**
   ```properties
   weather.api.key=YOUR_API_KEY_HERE
   ```

3. **Obtén tu API key:**
   - Ve a https://www.visualcrossing.com/
   - Regístrate y crea una API key
   - Reemplaza `YOUR_API_KEY_HERE` con tu key real

## Requisitos

- Java 11+
- Maven
- Redis (para caché)

## Instalación

```bash
mvn clean install
```

## Ejecución

```bash
mvn spring-boot:run
```

La API estará disponible en `http://localhost:8080`

## Endpoints

- `GET /api/weather/{city}/{country}` - Obtener clima actual
- `GET /api/weather/{city}/{country}/days` - Obtener clima de los próximos días
- `GET /api/weather/{city}/{country}/clear-cache` - Limpiar caché

## Documentación

Swagger UI: `http://localhost:8080/swagger-ui.html`

## Notas de seguridad

✅ **Nunca** comitas credenciales o API keys
✅ **Siempre** usa variables de entorno en producción
✅ **Mantén privado** el archivo `application.properties`

