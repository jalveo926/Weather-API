package com.jalveo.weatherAPI.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jalveo.weatherAPI.DTOs.Response.MonthWeatherDTO;
import com.jalveo.weatherAPI.DTOs.Response.TodayWeatherDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class WeatherService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private final RestTemplate restTemplate;
    private final CacheService cacheServiceObj;

    @Value("${weather.api.key}")
    private String apiKey;
    @Value("${weather.api.base-url}")
    private String baseUrl;
    private String responseString;


    public WeatherService(RestTemplate restTemplate, CacheService cacheService) {
        this.restTemplate = restTemplate;
        this.cacheServiceObj = cacheService; // Inicializamos el servicio de cache
    }


    private void createResponseString(String city, String country) {
        // Validación de parámetros de entrada
        if (city == null || city.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La ciudad no puede ser nula o vacía");
        }
        if (country == null || country.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El país no puede ser nulo o vacío");
        }

        String ubi = city + "," + country; //Esto va a ser nuestra Key para la cache
        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .pathSegment(ubi)
                .queryParam("unitGroup", "metric")
                .queryParam("key", apiKey)
                .queryParam("contentType", "json")
                .encode()
                .toUriString();

        logger.debug("URL construida: {}", url);

        this.responseString = restTemplate.getForObject(url, String.class);
    }

    public TodayWeatherDTO getWeather(String city, String country) {
        try {
            // Creamos la URL y obtenemos la respuesta de la API externa
            createResponseString(city, country);

            // Validamos que la respuesta no sea nula o vacía
            if (responseString == null || responseString.trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NO_CONTENT, "La API no devolvió ningún dato");
            }

            //Verificamos si la información ya está en cache, si no, se guarda la respuesta obtenida de la API externa

            String keyCache = "weather:today:" + city.toLowerCase() + ":" + country.toLowerCase();

            Optional<TodayWeatherDTO> cacheado =
                    cacheServiceObj.get(keyCache, TodayWeatherDTO.class);
            if (cacheado.isPresent()) return cacheado.get();

            // Procesamos la respuesta JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseString);

            // Validamos que el JSON contenga los datos esperados
            if (root == null || root.isMissingNode()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Respuesta JSON inválida");
            }

            TodayWeatherDTO responseDTO = new TodayWeatherDTO();

            // Extraemos la información del clima con validación
            responseDTO.setCity(root.path("resolvedAddress").asText("Desconocido"));
            responseDTO.setCountry(country);
            responseDTO.setDescription(root.path("description").asText("Sin descripción"));

            // Información del día de hoy
            JsonNode today = root.path("days").get(0);

            if (today == null || today.isMissingNode()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se encontró información del clima para hoy");
            }

            responseDTO.setTemp(today.path("temp").asDouble(0.0));
            responseDTO.setTempMax(today.path("tempmax").asDouble(0.0));
            responseDTO.setTempMin(today.path("tempmin").asDouble(0.0));
            responseDTO.setHumidity(today.path("humidity").asDouble(0.0));
            responseDTO.setConditions(today.path("conditions").asText("Desconocido"));
            responseDTO.setIcon(today.path("icon").asText(""));
            responseDTO.setSunrise(today.path("sunrise").asText(""));
            responseDTO.setSunset(today.path("sunset").asText(""));

            cacheServiceObj.save(keyCache, responseDTO, 3600); //guardamos en cache por 1 hora (3600 segundos)
            logger.debug("Se guarda en cache: {}", responseDTO);
            return responseDTO;

        } catch (ResponseStatusException e) {
            // Re-lanzamos las excepciones de tipo ResponseStatusException
            throw e;
        } catch (HttpClientErrorException e) {
            // Errores 4xx de la API externa
            if (e.getStatusCode().value() == 404) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ciudad o ubicación no encontrada", e);
            } else if (e.getStatusCode().value() == 401 || e.getStatusCode().value() == 403) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error de autenticación con la API de clima", e);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la solicitud: " + e.getMessage(), e);
            }
        } catch (HttpServerErrorException e) {
            // Errores 5xx de la API externa
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de clima está temporalmente no disponible", e);
        } catch (JsonProcessingException e) {
            // Errores de procesamiento JSON
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al procesar los datos del clima", e);
        } catch (Exception e) {
            // Cualquier otro error
            logger.error("Error inesperado en WeatherService: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado al obtener el clima: " + e.getMessage(), e);
        }

    }

    public List<MonthWeatherDTO> getWeatherMonth(String city, String country) {
        try {
            // Validación de parámetros de entrada
            if (city == null || city.trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La ciudad no puede ser nula o vacía");
            }
            if (country == null || country.trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El país no puede ser nulo o vacío");
            }

            String keyCache = "weather:month:" + city.toLowerCase() + ":" + country.toLowerCase();
            Optional<List<MonthWeatherDTO>> cacheado =
                    cacheServiceObj.getList(keyCache, MonthWeatherDTO.class);
            if (cacheado.isPresent()) return cacheado.get();


            // Creamos la URL y obtenemos la respuesta de la API externa
            createResponseString(city, country);

            // Validamos que la respuesta no sea nula o vacía
            if (responseString == null || responseString.trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NO_CONTENT, "La API no devolvió ningún dato");
            }

            // Procesamos la respuesta JSON
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            JsonNode root = objectMapper.readTree(responseString);

            // Validamos que el JSON contenga los datos esperados
            if (root == null || root.isMissingNode()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Respuesta JSON inválida");
            }

            JsonNode daysNode = root.path("days");

            if (daysNode == null || !daysNode.isArray()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se encontró información diaria del clima");
            }

            if (daysNode.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No hay datos de días disponibles");
            }

            // Convertimos el JsonNode de días a una lista de MonthWeatherDTO
            List<MonthWeatherDTO> days = objectMapper.convertValue(daysNode, new TypeReference<>() {
            });

            cacheServiceObj.save(keyCache, days, 3600); //guardamos en cache por 1 hora (3600 segundos)
            logger.debug("Días obtenidos: {}", days);
            return days;

        } catch (ResponseStatusException e) {
            // Re-lanzamos las excepciones de tipo ResponseStatusException
            throw e;
        } catch (HttpClientErrorException e) {
            // Errores 4xx de la API externa
            if (e.getStatusCode().value() == 404) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ciudad o ubicación no encontrada", e);
            } else if (e.getStatusCode().value() == 401 || e.getStatusCode().value() == 403) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error de autenticación con la API de clima", e);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la solicitud: " + e.getMessage(), e);
            }
        } catch (HttpServerErrorException e) {
            // Errores 5xx de la API externa
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de clima está temporalmente no disponible", e);
        } catch (JsonProcessingException e) {
            // Errores de procesamiento JSON
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al procesar los datos del clima", e);
        } catch (Exception e) {
            // Cualquier otro error
            logger.error("Error inesperado en WeatherService.getWeatherPerDay: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado al obtener el clima por días: " + e.getMessage(), e);
        }
    }

    public void clearCache(String city, String country) {
        try {
            if (city == null || city.trim().isEmpty() || country == null || country.trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La ciudad y el país no pueden ser nulos o vacíos");
            }
            String keyCache = city + "," + country;
            cacheServiceObj.delete(keyCache);
        } catch (ResponseStatusException e) {
            throw e; // Re-lanzamos las excepciones de validación
        } catch (Exception e) {
            // Si Redis está caído, logueamos pero no lanzamos error para que la app continúe
            logger.error("Error al limpiar el caché para {}, {}: {}", city, country, e.getMessage());
        }
    }
}
