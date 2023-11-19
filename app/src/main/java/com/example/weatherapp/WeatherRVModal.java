package com.example.weatherapp;

public class WeatherRVModal {
    private Long time;
    private String temperature;
    private String icon;
    private String windspeed;

    public WeatherRVModal(Long time, String temperature, String icon, String windspeed) {
        this.time = time;
        this.temperature = temperature;
        this.icon = icon;
        this.windspeed = windspeed;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getWindspeed() {
        return windspeed;
    }

    public void setWindspeed(String windspeed) {
        this.windspeed = windspeed;
    }
}
