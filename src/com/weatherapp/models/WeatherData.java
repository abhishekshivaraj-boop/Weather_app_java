package com.weatherapp.models;

import java.util.List;

public class WeatherData {
    private double latitude;
    private double longitude;
    private String cityName;
    private double temperature;
    private double feelsLike;
    private int humidity;
    private String weatherDescription;
    private double windSpeed;
    private String sunrise;
    private String sunset;
    private List<ForecastDay> forecast;
    private long timestamp;

    public WeatherData(double latitude, double longitude, String cityName,
                       double temperature, double feelsLike, int humidity,
                       String weatherDescription, double windSpeed,
                       String sunrise, String sunset, List<ForecastDay> forecast) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.cityName = cityName;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.weatherDescription = weatherDescription;
        this.windSpeed = windSpeed;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.forecast = forecast;
        this.timestamp = System.currentTimeMillis();
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getCityName() { return cityName; }
    public double getTemperature() { return temperature; }
    public double getFeelsLike() { return feelsLike; }
    public int getHumidity() { return humidity; }
    public String getWeatherDescription() { return weatherDescription; }
    public double getWindSpeed() { return windSpeed; }
    public String getSunrise() { return sunrise; }
    public String getSunset() { return sunset; }
    public List<ForecastDay> getForecast() { return forecast; }
    public long getTimestamp() { return timestamp; }
}