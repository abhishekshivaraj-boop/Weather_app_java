package com.weatherapp.services;

import com.weatherapp.models.GeoLocation;
import com.weatherapp.models.WeatherData;
import com.weatherapp.models.ForecastDay;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WeatherService {

    // Step 1: Convert city name to coordinates
    public GeoLocation getCoordinates(String cityName) throws Exception {
        String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8);
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" 
                            + encodedCity + "&count=1&language=en&format=json";

        String response = makeApiCall(urlString);

        JsonObject json = JsonParser.parseString(response).getAsJsonObject();

        if (!json.has("results")) {
            throw new Exception("City not found: " + cityName);
        }

        JsonArray results = json.getAsJsonArray("results");
        JsonObject firstResult = results.get(0).getAsJsonObject();

        double lat = firstResult.get("latitude").getAsDouble();
        double lon = firstResult.get("longitude").getAsDouble();
        String name = firstResult.get("name").getAsString();
        String country = firstResult.has("country") ? firstResult.get("country").getAsString() : "";

        return new GeoLocation(lat, lon, name, country);
    }

    // Step 2: Get current weather + forecast using coordinates
    public WeatherData getCurrentWeather(GeoLocation location) throws Exception {
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude="
                + location.getLatitude() + "&longitude=" + location.getLongitude()
                + "&current=temperature_2m,apparent_temperature,relative_humidity_2m,weather_code,wind_speed_10m"
                + "&daily=weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset"
                + "&timezone=auto";

        String response = makeApiCall(urlString);

        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        JsonObject current = json.getAsJsonObject("current");
        JsonObject daily = json.getAsJsonObject("daily");

        double temperature = current.get("temperature_2m").getAsDouble();
        double feelsLike = current.get("apparent_temperature").getAsDouble();
        int humidity = current.get("relative_humidity_2m").getAsInt();
        double windSpeed = current.get("wind_speed_10m").getAsDouble();
        int weatherCode = current.get("weather_code").getAsInt();
        String description = getWeatherDescription(weatherCode);

        // Sunrise/sunset for today (first entry in daily arrays)
        String sunrise = daily.getAsJsonArray("sunrise").get(0).getAsString();
        String sunset = daily.getAsJsonArray("sunset").get(0).getAsString();

        // Build 5-day forecast (skip today, index 0, take next days)
        List<ForecastDay> forecast = new ArrayList<>();
        JsonArray dates = daily.getAsJsonArray("time");
        JsonArray maxTemps = daily.getAsJsonArray("temperature_2m_max");
        JsonArray minTemps = daily.getAsJsonArray("temperature_2m_min");
        JsonArray codes = daily.getAsJsonArray("weather_code");

        int daysToShow = Math.min(5, dates.size());
        for (int i = 0; i < daysToShow; i++) {
            String date = dates.get(i).getAsString();
            double maxT = maxTemps.get(i).getAsDouble();
            double minT = minTemps.get(i).getAsDouble();
            int code = codes.get(i).getAsInt();
            forecast.add(new ForecastDay(date, maxT, minT, getWeatherDescription(code)));
        }

        return new WeatherData(location.getLatitude(), location.getLongitude(),
                location.getName(), temperature, feelsLike, humidity,
                description, windSpeed, sunrise, sunset, forecast);
    }

    // Helper: Make HTTP GET request
    private String makeApiCall(String urlString) throws Exception {
        URL url = new URI(urlString).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }

    // Helper: Convert weather code to readable description
    private String getWeatherDescription(int code) {
        if (code == 0) return "Clear sky";
        if (code <= 3) return "Partly cloudy";
        if (code <= 48) return "Foggy";
        if (code <= 67) return "Rainy";
        if (code <= 77) return "Snowy";
        if (code <= 82) return "Rain showers";
        if (code <= 99) return "Thunderstorm";
        return "Unknown";
    }
}

        // Build 5-day forecast (skip today, index 0,
