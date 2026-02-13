# 🌤️ Weather API

Una API REST moderna para obtener información meteorológica en tiempo real. Consume datos de Visual Crossing Weather API con caché implementado en Redis para optimizar el rendimiento.

## ✨ Características

- 🌡️ **Clima actual** - Obtén la temperatura, humedad y condiciones actuales
- 📅 **Pronóstico** - Accede al clima de los próximos 15 días
- ⚡ **Caché inteligente** - Respuestas rápidas con Redis
- 📊 **API RESTful** - Interfaz simple y limpia
- 📖 **Documentación automática** - Swagger UI integrado
## 🚀 Inicio rápido

### Requisitos previos

- Java 11 o superior
- Maven 3.6+
- Redis en ejecución (localhost:6379)
- Una API key de Visual Crossing (gratuita)

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/Weather-API.git
cd weatherAPI
```

### 2. Configurar credenciales

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Edita `application.properties` y reemplaza tu API key:

```properties
weather.api.key=TU_API_KEY_AQUI
```

**¿Cómo obtener una API key?**
1. Ve a https://www.visualcrossing.com/
2. Regístrate (opción gratuita disponible)
3. Copia tu API key desde el dashboard

### 3. Compilar y ejecutar

```bash
mvn clean install
mvn spring-boot:run
```

La API estará disponible en `http://localhost:8080`

## 📡 Endpoints

### Obtener clima actual

```bash
GET /api/weather/{city}/{country}
```

**Ejemplo:**
```bash
curl "http://localhost:8080/api/weather/New York/United States"
```

**Respuesta:**
```json
{
  "city": "New York, NY, United States",
  "country": "United States",
  "temp": 15.5,
  "tempMax": 18.2,
  "tempMin": 12.1,
  "humidity": 65.5,
  "conditions": "Partly cloudy",
  "sunrise": "06:45:00",
  "sunset": "19:30:00"
}
```

### Pronóstico de 15 días

```bash
GET /api/weather/{city}/{country}/days
```

**Ejemplo:**
```bash
curl "http://localhost:8080/api/weather/London/United Kingdom"
```

**Respuesta:**
```json
[
  {
    "datetime": "2026-02-12",
    "temp": 8.3,
    "tempMax": 10.5,
    "tempMin": 5.2,
    "humidity": 72.0,
    "windspeed": 12.5,
    "conditions": "Rainy",
    "icon": "rain"
  },
  {
    "datetime": "2026-02-13",
    "temp": 9.1,
    "tempMax": 11.2,
    "tempMin": 6.8,
    "humidity": 68.5,
    "windspeed": 10.3,
    "conditions": "Cloudy",
    "icon": "cloudy"
  }
]
```

### Limpiar caché

```bash
GET /api/weather/{city}/{country}/clear-cache
```

Elimina los datos en caché para una ubicación específica.

## 📚 Documentación interactiva

Accede a Swagger UI para explorar todos los endpoints:

```
http://localhost:8080/swagger-ui.html
```


## 🔌 Integración en tu proyecto

### Con Spring Boot

```java
@RestController
@RequestMapping("/weather")
public class MyController {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @GetMapping
    public TodayWeatherDTO getWeather() {
        String url = "http://localhost:8080/api/weather/Madrid/Spain";
        return restTemplate.getForObject(url, TodayWeatherDTO.class);
    }
}
```

### Con JavaScript/Fetch

```javascript
async function getWeather(city, country) {
    const response = await fetch(
        `http://localhost:8080/api/weather/${city}/${country}`
    );
    const data = await response.json();
    console.log(`Temperatura en ${data.city}: ${data.temp}°C`);
}

getWeather('Paris', 'France');
```

### Con cURL

```bash
# Clima actual
curl -X GET "http://localhost:8080/api/weather/Tokyo/Japan"

# Pronóstico
curl -X GET "http://localhost:8080/api/weather/Sydney/Australia/days"
```



## 🐛 Solución de problemas

### "Cannot connect to Redis"

Asegúrate de que Redis esté ejecutándose:

```bash
# En Windows (si usas WSL o similar)
redis-cli ping
# Debería responder: PONG
```

### "API key inválida"

Verifica que tu key esté correcta en `application.properties` y que tengas acceso a Visual Crossing.

### "Ciudad no encontrada"

Usa nombres de ciudad en inglés y nombres de país estándar. Ejemplos válidos:
*Preferiblemente nombres de país completos en inglés para evitar problemas de desambiguación.*
- ✅ `Barcelona, España`
- ✅ `New York, United States`
- ✅ `Madrid, ES`


## 📊 Rendimiento

Con caché en Redis:
- ⚡ Respuesta en ~5-10ms (desde caché)
- 📡 Primera solicitud ~500-800ms (desde API externa)
- 💾 Datos almacenados por 1 hora (ajustable en `application.properties`)

## 📦 Dependencias principales

- Spring Boot 3.x
- Spring Data Redis
- Jackson (JSON processing)
- Lombok
- Swagger/OpenAPI

## 🤝 Contribuir

Las contribuciones son bienvenidas. Para cambios mayores, abre primero un issue para discutir lo que te gustaría cambiar.

```bash
git checkout -b feature/AmazingFeature
git commit -m 'Add some AmazingFeature'
git push origin feature/AmazingFeature
```

## 📝 Licencia

Este proyecto está bajo licencia MIT. Ver `LICENSE` para más detalles.

## 💬 Soporte

¿Preguntas o problemas? 
- Abre un issue en GitHub
- Revisa la documentación de Visual Crossing: https://www.visualcrossing.com/resources/documentation/weather-api/

## 🎯 Roadmap

- [ ] Autenticación de usuarios
- [ ] Historial de búsquedas
- [ ] Alertas meteorológicas
- [ ] Soporte para múltiples idiomas
- [ ] Geolocalización automática

---

**Por Jesús Alveo**

Última actualización: Febrero 2026

