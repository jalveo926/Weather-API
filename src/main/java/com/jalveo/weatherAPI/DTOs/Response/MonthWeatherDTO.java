package com.jalveo.weatherAPI.DTOs.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonthWeatherDTO {
    private LocalDate datetime;
    private double tempmax;
    private double tempmin;
    private double temp;

    private double humidity;
    private double windspeed;

    private String conditions;
    private String description;
    private String icon;

}
