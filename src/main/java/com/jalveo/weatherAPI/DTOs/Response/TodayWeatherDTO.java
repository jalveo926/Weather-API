package com.jalveo.weatherAPI.DTOs.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TodayWeatherDTO {
    private String city;
    private String country;
    private String description;
    private double temp;       // temperatura promedio
    private double tempMax;
    private double tempMin;
    private double humidity;
    private String conditions; // descripción del clima
    private String icon;       // icono representativo
    private String sunrise;
    private String sunset;

}
