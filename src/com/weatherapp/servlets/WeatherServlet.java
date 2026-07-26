package com.weatherapp.servlets;

import com.weatherapp.models.GeoLocation;
import com.weatherapp.models.WeatherData;
import com.weatherapp.services.WeatherService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class WeatherServlet extends HttpServlet {

    private WeatherService weatherService = new WeatherService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String city = request.getParameter("city");

        if (city == null || city.trim().isEmpty()) {
            request.setAttribute("error", "Please enter a city name.");
            request.getRequestDispatcher("/views/index.jsp").forward(request, response);
            return;
        }

        try {
            GeoLocation location = weatherService.getCoordinates(city);
            WeatherData weather = weatherService.getCurrentWeather(location);

            request.setAttribute("weather", weather);
            request.getRequestDispatcher("/views/weather.jsp").forward(request, response);

        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("429")) {
                request.setAttribute("error", "Weather service is temporarily busy. Please try again in a minute.");
           }  else {
    request.setAttribute("error", "Could not find weather for that city. Please check the spelling and try again.");
}
            request.getRequestDispatcher("/views/index.jsp").forward(request, response);
        }
    }
}
