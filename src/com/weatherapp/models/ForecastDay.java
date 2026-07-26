package com.weatherapp.models;

public class ForecastDay {
    private String date;
    private double maxTemp;
    private double minTemp;
    private String description;

    public ForecastDay(String date, double maxTemp, double minTemp, String description) {
        this.date = date;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.description = description;
    }

    public String getDate() { return date; }
    public double getMaxTemp() { return maxTemp; }
    public double getMinTemp() { return minTemp; }
    public String getDescription() { return description; }
}
