package com.jalveo.weatherAPI.controllers;


import com.jalveo.weatherAPI.DTOs.Response.MonthWeatherDTO;
import com.jalveo.weatherAPI.DTOs.Response.TodayWeatherDTO;
import com.jalveo.weatherAPI.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/{city}/{country}")
    public TodayWeatherDTO getWeather(@PathVariable("city") String city, @PathVariable("country") String country) {
        try {
            // Validación de parámetros
            if (city == null || city.isEmpty() || country == null || country.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requiere la ciudad y el país para obtener el clima");
            }

            return weatherService.getWeather(city, country);

        } catch (ResponseStatusException e) {
            // Re-lanzamos las excepciones de tipo ResponseStatusException
            throw e;
        } catch (HttpClientErrorException e) {
            // Errores 4xx de la API externa (ciudad no encontrada, etc.)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ciudad o país no encontrado: " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            // Errores 5xx de la API externa
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de clima no está disponible temporalmente", e);
        } catch (Exception e) {
            // Cualquier otro error no previsto
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado al obtener el clima: " + e.getMessage(), e);
        }

    }
    @GetMapping("/{city}/{country}/days")
    public List<MonthWeatherDTO> getWeatherPerDay(@PathVariable("city") String city, @PathVariable("country") String country) {
        try {
            // Validación de parámetros
            if (city == null || city.isEmpty() || country == null || country.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requiere la ciudad y el país para obtener el clima");
            }

            return weatherService.getWeatherMonth(city, country);

        } catch (ResponseStatusException e) {
            // Re-lanzamos las excepciones de tipo ResponseStatusException
            throw e;
        } catch (HttpClientErrorException e) {
            // Errores 4xx de la API externa (ciudad no encontrada, etc.)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ciudad o país no encontrado: " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            // Errores 5xx de la API externa
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de clima no está disponible temporalmente", e);
        } catch (Exception e) {
            // Cualquier otro error no previsto
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado al obtener el clima: " + e.getMessage(), e);
        }
    }

    @GetMapping("/{city}/{country}/clear-cache")
    public void clearCache(@PathVariable("city") String city, @PathVariable("country") String country) {

        try {
            // Validación de parámetros
            if (city == null || city.isEmpty() || country == null || country.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requiere la ciudad y el país para eliminar el cache");
            }

            weatherService.clearCache(city, country);

        } catch (ResponseStatusException e) {
            // Re-lanzamos las excepciones de tipo ResponseStatusException
            throw e;
        } catch (Exception e) {
            // Cualquier otro error no previsto
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado al eliminar el cache: " + e.getMessage(), e);
        }
    }
}

