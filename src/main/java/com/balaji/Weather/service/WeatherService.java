package com.balaji.Weather.service;

import com.balaji.Weather.model.WeatherData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeatherService {

    @Value("${weather.api1.url}")
    private String api1Url;

    @Value("${weather.api1.key}")
    private String api1Key;

    @Value("${weather.api2.url}")
    private String api2Url;

    @Value("${weather.api2.key}")
    private String api2Key;

    private final OkHttpClient httpClient = new OkHttpClient();

    // Method to fetch weather data for a given city
    public WeatherData getWeatherData(String city) {
        // Fetch temperature and condition from OpenWeatherMap
        WeatherData tempData = fetchTemperatureAndConditionFromOpenWeather(city);

        if (tempData == null) {
            tempData = new WeatherData();
            tempData.setCity(city);
        }

        // Fetch wind speed from WeatherAPI if available
        WeatherData windData = fetchWindFromWeatherAPI(city);
        if (windData != null) {
            tempData.setWindSpeed(windData.getWindSpeed()); // Ensure wind speed is set
        }

        return tempData;
    }

    // Method to fetch weather data for a given location based on latitude and longitude
    public WeatherData getWeatherForLocation(double latitude, double longitude) {
        // API URL for weather based on latitude and longitude (example for OpenWeatherMap)
        String url = api1Url + "?lat=" + latitude + "&lon=" + longitude + "&appid=" + api1Key;

        // Fetch the weather data for the location
        return fetchWeatherData(url, "OpenWeatherMap");
    }

    // Fetch temperature and condition from OpenWeatherMap API
    private WeatherData fetchTemperatureAndConditionFromOpenWeather(String city) {
        String url = api1Url + "?q=" + city + "&appid=" + api1Key;
        return fetchWeatherData(url, "OpenWeatherMap");
    }

    // Fetch wind data from WeatherAPI
    private WeatherData fetchWindFromWeatherAPI(String city) {
        String url = api2Url + "?key=" + api2Key + "&q=" + city;
        return fetchWeatherData(url, "WeatherAPI");
    }

    // Fetch weather data from a given URL
    private WeatherData fetchWeatherData(String url, String apiName) {
        try {
            Request request = new Request.Builder().url(url).build();
            Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                String jsonResponse = response.body().string();
                return parseWeatherResponse(jsonResponse, apiName);
            } else {
                System.err.println("Error fetching data from " + apiName + ": " + response.message());
            }
        } catch (IOException e) {
            System.err.println("Failed to fetch data from " + apiName + ": " + e.getMessage());
        }
        return null;
    }

    // Parse the weather API response to extract necessary details
    private WeatherData parseWeatherResponse(String jsonResponse, String apiName) {
        ObjectMapper mapper = new ObjectMapper();
        WeatherData weatherData = new WeatherData();

        try {
            JsonNode root = mapper.readTree(jsonResponse);
            if ("OpenWeatherMap".equals(apiName)) {
                // Get temperature in Celsius (from Kelvin)
                double tempKelvin = root.path("main").path("temp").asDouble();
                weatherData.setTemperature(tempKelvin - 273.15); // Convert to Celsius

                // Get weather condition
                JsonNode weatherNode = root.path("weather").get(0);
                if (weatherNode != null) {
                    weatherData.setCondition(weatherNode.path("description").asText());
                }

                // Get wind speed from OpenWeatherMap API (in meters per second)
                double windSpeedMps = root.path("wind").path("speed").asDouble();
                weatherData.setWindSpeed(windSpeedMps * 3.6); // Convert to km/h

            } else if ("WeatherAPI".equals(apiName)) {
                // Get wind speed (already in km/h)
                double windSpeedKph = root.path("current").path("wind_kph").asDouble();
                weatherData.setWindSpeed(windSpeedKph); // No conversion needed, already in km/h

                // Get weather condition
                String condition = root.path("current").path("condition").path("text").asText();
                if (condition != null && !condition.isEmpty()) {
                    weatherData.setCondition(condition);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing response from " + apiName + ": " + e.getMessage());
        }

        return weatherData;
    }

    // Method to fetch weather for the top cities
    public List<WeatherData> getTopCitiesWeather() {
        List<String> cities = List.of("Mumbai", "Delhi", "Bangalore", "Chennai", "Kolkata");
        return cities.stream().map(city -> {
            WeatherData weatherData = getWeatherData(city);
            weatherData.setCity(city);
            return weatherData;
        }).collect(Collectors.toList());
    }
}
