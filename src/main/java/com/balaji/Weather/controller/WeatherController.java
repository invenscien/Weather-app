package com.balaji.Weather.controller;

import com.balaji.Weather.model.WeatherData;
import com.balaji.Weather.service.WeatherService;
import com.balaji.Weather.exception.WeatherServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/{city}")
    public ResponseEntity<Object> getWeather(@PathVariable String city) {
        try {
            WeatherData weatherData = weatherService.getWeather(city);
            return ResponseEntity.ok(weatherData);
        } catch (WeatherServiceException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Custom error response format
    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @ExceptionHandler(WeatherServiceException.class)
    public ResponseEntity<ErrorResponse> handleWeatherServiceException(WeatherServiceException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
