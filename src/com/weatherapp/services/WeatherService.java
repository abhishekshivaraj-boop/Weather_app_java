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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherService {

    private String getApiKey() throws Exception {
        String key = System.getenv("OPENWEATHER_API_KEY");
        if (key == null || key.isEmpty()) {
            throw new Exception("API key not configured");
        }
        return key;
    }

    // Step 1: Convert city name to coordinates using OpenWeatherMap Geocoding API
    public GeoLocation getCoordinates(String cityName) throws Exception {
        String apiKey = getApiKey();
        String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8);
        String urlString = "https://api.openweathermap.org/geo/1.0/direct?q="
                + encodedCity + "&limit=1&appid=" + apiKey;

        String response = makeApiCall(urlString);
        JsonArray results = JsonParser.parseString(response).getAsJsonArray();

        if (results.size() == 0) {
            throw new Exception("City not found: " + cityName);
        }

        JsonObject firstResult = results.get(0).getAsJsonObject();
        double lat = firstResult.get("lat").getAsDouble();
        double lon = firstResult.get("lon").getAsDouble();
        String name = firstResult.get("name").getAsString();
        String country = firstResult.has("country") ? firstResult.get("country").getAsString() : "";

        return new GeoLocation(lat, lon, name, country);
    }

    // Step 2: Get current weather + forecast using OpenWeatherMap
    public WeatherData getCurrentWeather(GeoLocation location) throws Exception {
        String apiKey = getApiKey();

        // Current weather
        String currentUrl = "https://api.openweathermap.org/data/2.5/weather?lat="
                + location.getLatitude() + "&lon=" + location.getLongitude()
                + "&units=metric&appid=" + apiKey;

        String currentResponse = makeApiCall(currentUrl);
        JsonObject currentJson = JsonParser.parseString(currentResponse).getAsJsonObject();

        JsonObject main = currentJson.getAsJsonObject("main");
        double temperature = main.get("temp").getAsDouble();
        double feelsLike = main.get("feels_like").getAsDouble();
        int humidity = main.get("humidity").getAsInt();

        JsonObject wind = currentJson.getAsJsonObject("wind");
        double windSpeed = wind.get("speed").getAsDouble();

        JsonArray weatherArr = currentJson.getAsJsonArray("weather");
        String description = weatherArr.get(0).getAsJsonObject().get("description").getAsString();
        description = capitalize(description);

        JsonObject sys = currentJson.getAsJsonObject("sys");
        long sunriseUnix = sys.get("sunrise").getAsLong();
        long sunsetUnix = sys.get("sunset").getAsLong();
        String sunrise = formatTime(sunriseUnix);
        String sunset = formatTime(sunsetUnix);

        // 5-day forecast (uses the free /forecast endpoint, 3-hour intervals)
        String forecastUrl = "https://api.openweathermap.org/data/2.5/forecast?lat="
                + location.getLatitude() + "&lon=" + location.getLongitude()
                + "&units=metric&appid=" + apiKey;

        String forecastResponse = makeApiCall(forecastUrl);
        JsonObject forecastJson = JsonParser.parseString(forecastResponse).getAsJsonObject();
        JsonArray list = forecastJson.getAsJsonArray("list");

        List<ForecastDay> forecast = buildDailyForecast(list);

        return new WeatherData(location.getLatitude(), location.getLongitude(),
                location.getName(), temperature, feelsLike, humidity,
                description, windSpeed, sunrise, sunset, forecast);
    }

    // Helper: Group 3-hour forecast entries into daily summaries (max 5 days)
    private List<ForecastDay> buildDailyForecast(JsonArray list) {
        Map<String, List<Double>> tempsByDate = new HashMap<>();
        Map<String, String> descByDate = new HashMap<>();
        List<String> orderedDates = new ArrayList<>();

        SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < list.size(); i++) {
            JsonObject entry = list.get(i).getAsJsonObject();
            long unixTime = entry.get("dt").getAsLong();
            String date = dateFmt.format(new Date(unixTime * 1000));

            double temp = entry.getAsJsonObject("main").get("temp").getAsDouble();

            tempsByDate.computeIfAbsent(date, k -> {
                orderedDates.add(date);
                return new ArrayList<>();
            }).add(temp);

            if (!descByDate.containsKey(date)) {
                String desc = entry.getAsJsonArray("weather").get(0).getAsJsonObject()
                        .get("description").getAsString();
                descByDate.put(date, capitalize(desc));
            }
        }

        List<ForecastDay> result = new ArrayList<>();
        int daysToShow = Math.min(5, orderedDates.size());
        for (int i = 0; i < daysToShow; i++) {
            String date = orderedDates.get(i);
            List<Double> temps = tempsByDate.get(date);
            double max = temps.stream().max(Double::compareTo).orElse(0.0);
            double min = temps.stream().min(Double::compareTo).orElse(0.0);
            result.add(new ForecastDay(date, max, min, descByDate.get(date)));
        }

        return result;
    }

    private String formatTime(long unixSeconds) {
        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm");
        return timeFmt.format(new Date(unixSeconds * 1000));
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    // Helper: Make HTTP GET request
    private String makeApiCall(String urlString) throws Exception {
        URL url = new URI(urlString).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "WeatherApp-Portfolio/1.0");

        int responseCode = conn.getResponseCode();
        if (responseCode == 404) {
            throw new Exception("City not found");
        }
        if (responseCode == 401) {
            throw new Exception("Invalid API key");
        }
        if (responseCode == 429) {
            throw new Exception("429 rate limit");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }
}

        // Build 5-day forecast (skip today, index 0,
