package com.balaji.Weather.model;

public class WeatherData {
    private String city;
    private double temperature;
    private String condition;
    private double windSpeed;
    private String windSpeedUnit = "km/h";
    public String getWindSpeedUnit() {
        return windSpeedUnit;
    }

    public String getCity(){
        return city;
    }

    public void setCity(String city){
        this.city = city;
    }



    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }
}
