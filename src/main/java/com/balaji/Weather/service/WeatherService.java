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
import java.util.Arrays;
import java.util.Objects;

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

    @Value("${weather.api3.url}")
    private String api3Url;

    private final OkHttpClient httpClient = new OkHttpClient();

    public WeatherData getWeather(String city) {
        WeatherData tempData = fetchTemperatureAndConditionFromOpenWeather(city);
        WeatherData windData = fetchWindFromWeatherAPI(city);

        return reconcileWeatherData(tempData, windData);
    }

    private WeatherData fetchTemperatureAndConditionFromOpenWeather(String city) {
        String url = api1Url + "?q=" + city + "&appid=" + api1Key;
        return fetchWeatherData(url, "OpenWeatherMap");
    }

    private WeatherData fetchWindFromWeatherAPI(String city) {
        String url = api2Url + "?key=" + api2Key + "&q=" + city;
        return fetchWeatherData(url, "WeatherAPI");
    }

    private WeatherData fetchWeatherData(String url, String apiName) {
        try {
            Request request = new Request.Builder().url(url).build();
            Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                String jsonResponse = response.body().string();
                System.out.println("API Response from " + apiName + ": " + jsonResponse);
                return parseWeatherResponse(jsonResponse, apiName);
            } else {
                System.err.println("Error fetching data from " + apiName + ": " + response.message());
            }
        } catch (IOException e) {
            System.err.println("Failed to fetch data from " + apiName + ": " + e.getMessage());
        }
        return null;
    }

    private WeatherData parseWeatherResponse(String jsonResponse, String apiName) {
        ObjectMapper mapper = new ObjectMapper();
        WeatherData weatherData = new WeatherData();

        try {
            JsonNode root = mapper.readTree(jsonResponse);
            if ("OpenWeatherMap".equals(apiName)) {
                // Temperature: Convert from Kelvin to Celsius
                double tempKelvin = root.path("main").path("temp").asDouble();
                weatherData.setTemperature(tempKelvin - 273.15);

                // Condition: Use description
                JsonNode weatherNode = root.path("weather").get(0);
                if (weatherNode != null) {
                    weatherData.setCondition(weatherNode.path("description").asText());
                }
            } else if ("WeatherAPI".equals(apiName)) {
                // Temperature: Directly use Celsius value
                double tempCelsius = root.path("current").path("temp_c").asDouble();
                weatherData.setTemperature(tempCelsius);

                // Wind Speed: Convert from kph to m/s
                double windKph = root.path("current").path("wind_kph").asDouble();
                weatherData.setWindSpeed(windKph / 3.6);

                // Condition: Use text if available
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

    private WeatherData reconcileWeatherData(WeatherData... dataSources) {
        WeatherData result = new WeatherData();

        // Prioritize the temperature from WeatherAPI (more direct)
        result.setTemperature(Arrays.stream(dataSources)
                .filter(Objects::nonNull)
                .map(WeatherData::getTemperature)
                .findFirst()
                .orElse(0.0));

        // Use the most accurate condition available
        result.setCondition(Arrays.stream(dataSources)
                .filter(Objects::nonNull)
                .map(WeatherData::getCondition)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("Unknown"));

        // Average the wind speed from available data
        result.setWindSpeed(Arrays.stream(dataSources)
                .filter(Objects::nonNull)
                .mapToDouble(WeatherData::getWindSpeed)
                .average()
                .orElse(0.0));

        return result;
    }

}
