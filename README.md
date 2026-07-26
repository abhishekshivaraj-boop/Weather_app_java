
```
# 🌤️ Weather App — Java, JSP & Servlets

A full-stack weather application built with core Java web technologies (no frameworks), demonstrating MVC architecture, REST API integration, and JSON parsing.

**🔗 Live Demo:** https://weather-app-java-m7k5.onrender.com/weatherapp/

> Note: Hosted on a free-tier instance — may take 30-60 seconds to wake up if inactive, and the weather API occasionally rate-limits shared hosting IPs.

---

## 📋 Overview

Users enter a city name and get real-time weather data — current conditions, temperature, humidity, wind speed, sunrise/sunset times, and a 5-day forecast — powered by the [Open-Meteo](https://open-meteo.com/) API.

---

## 🏗️ Architecture

This project follows the **MVC (Model-View-Controller)** pattern using core Java EE technologies:
```

┌─────────────────────────────┐
│ View Layer (JSP) │ index.jsp, weather.jsp
├─────────────────────────────┤
│ Controller (Servlet) │ WeatherServlet.java
├─────────────────────────────┤
│ Service Layer │ WeatherService.java
│ (Business logic + API) │ → Calls Open-Meteo, parses JSON
├─────────────────────────────┤
│ Model Layer (POJOs) │ WeatherData, GeoLocation, ForecastDay
└─────────────────────────────┘
│
▼
Open-Meteo REST API

```

**Flow:**
1. User submits a city name via the form on `index.jsp`
2. `WeatherServlet` receives the request, delegates to `WeatherService`
3. `WeatherService` calls Open-Meteo's Geocoding API to get coordinates, then the Forecast API for weather data
4. JSON response is parsed into Java model objects using **Gson**
5. Servlet forwards the data to `weather.jsp` for display

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Web Layer | Jakarta Servlets, JSP |
| Server | Apache Tomcat 10.1 |
| JSON Parsing | Gson |
| External API | Open-Meteo (Geocoding + Forecast) |
| Deployment | Docker, Render.com |
| Version Control | Git, GitHub |

---

## ✨ Features

- Real-time current weather (temperature, feels-like, humidity, wind speed)
- Sunrise & sunset times
- 5-day weather forecast
- Clean, responsive card-based UI
- Graceful error handling (invalid city names, API rate limits)

---

## 📂 Project Structure
```

weather_app/
├── src/com/weatherapp/
│ ├── models/ → WeatherData, GeoLocation, ForecastDay
│ ├── services/ → WeatherService (API integration)
│ └── servlets/ → WeatherServlet (request handling)
├── WebContent/
│ ├── views/ → index.jsp, weather.jsp
│ └── WEB-INF/ → web.xml
├── deploy/ → Production-ready deployable build
├── lib/ → Gson, Jakarta Servlet API
└── Dockerfile → Container config for deployment

```

---

## 🚀 Running Locally

**Prerequisites:** JDK 17+, Apache Tomcat 10.1

1. Clone the repo:
```

git clone https://github.com/abhishekshivaraj-boop/Weather_app_java.git

```
2. Compile the source:
```

javac --release 17 -cp "lib/gson-2.10.1.jar;lib/jakarta.servlet-api-5.0.0.jar" -d build src/com/weatherapp/models/.java src/com/weatherapp/services/.java src/com/weatherapp/servlets/*.java

```
3. Copy compiled classes, JSPs, and `web.xml` into Tomcat's `webapps/weatherapp` folder (matching the structure under `deploy/`)
4. Start Tomcat and visit `http://localhost:8080/weatherapp/`

---

## 🐳 Deployment

Deployed using Docker on Render.com. The `Dockerfile` packages a pre-built WAR-style directory with Tomcat 10.1, requiring no build step on the server — classes are compiled locally and committed to the `deploy/` folder for direct container deployment.

---

## 📌 Future Improvements

- Add unit tests (JUnit)
- Client-side caching to reduce redundant API calls
- Historical weather data view
- Migrate to Maven/Gradle for dependency management 
