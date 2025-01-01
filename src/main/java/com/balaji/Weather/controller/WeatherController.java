package com.balaji.Weather.controller;

import java.util.stream.Collectors;
import com.balaji.Weather.service.WeatherService;
import com.balaji.Weather.model.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    // Endpoint to fetch weather for a given city
    @GetMapping("/{city}")
    public ResponseEntity<?> getWeather(@PathVariable String city) {
        try {
            WeatherData weatherData = weatherService.getWeatherData(city);
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Weather data unavailable");
        }
    }

    // Endpoint to fetch weather for current location based on latitude and longitude
    @GetMapping("/current-location")
    public ResponseEntity<?> getWeatherForCurrentLocation(@RequestParam double latitude, @RequestParam double longitude) {
        try {
            WeatherData weatherData = weatherService.getWeatherForLocation(latitude, longitude);
            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to fetch weather for the current location");
        }
    }

    // Endpoint to fetch weather data for top cities
    @GetMapping("/top-cities")
    public ResponseEntity<List<WeatherData>> getWeatherForTopCities() {
        try {
            List<WeatherData> weatherDataList = weatherService.getTopCitiesWeather();
            return ResponseEntity.ok(weatherDataList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
