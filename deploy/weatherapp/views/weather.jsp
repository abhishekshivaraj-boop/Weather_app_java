<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.weatherapp.models.WeatherData" %>
<%@ page import="com.weatherapp.models.ForecastDay" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>Weather Result</title>
    <style>
        body {
            font-family: 'Segoe UI', Arial, sans-serif;
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            margin: 0;
            padding: 40px 20px;
            min-height: 100vh;
        }
        .card {
            max-width: 500px;
            margin: 0 auto;
            background: white;
            border-radius: 16px;
            padding: 30px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
        }
        h1 {
            margin: 0 0 5px 0;
            font-size: 28px;
            color: #222;
        }
        .condition {
            color: #666;
            font-size: 16px;
            margin-bottom: 20px;
        }
        .temp-main {
            font-size: 64px;
            font-weight: 300;
            color: #222;
            margin: 10px 0;
        }
        .details-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 12px;
            margin: 20px 0;
            padding: 20px 0;
            border-top: 1px solid #eee;
            border-bottom: 1px solid #eee;
        }
        .detail-item {
            font-size: 14px;
            color: #555;
        }
        .detail-item strong {
            display: block;
            color: #222;
            font-size: 16px;
        }
        .forecast-title {
            font-size: 18px;
            margin: 20px 0 10px 0;
            color: #222;
        }
        .forecast-row {
            display: flex;
            justify-content: space-between;
            padding: 10px 0;
            border-bottom: 1px solid #f0f0f0;
            font-size: 14px;
        }
        .forecast-date {
            color: #666;
            width: 90px;
        }
        .forecast-desc {
            flex: 1;
            text-align: center;
            color: #444;
        }
        .forecast-temps {
            width: 80px;
            text-align: right;
        }
        a.back-link {
            display: inline-block;
            margin-top: 20px;
            color: #4facfe;
            text-decoration: none;
            font-weight: 600;
        }
        a.back-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="card">
        <%
            WeatherData weather = (WeatherData) request.getAttribute("weather");
        %>

        <h1><%= weather.getCityName() %></h1>
        <div class="condition"><%= weather.getWeatherDescription() %></div>

        <div class="temp-main"><%= String.format("%.1f", weather.getTemperature()) %>°C</div>

        <div class="details-grid">
            <div class="detail-item">Feels like<strong><%= String.format("%.1f", weather.getFeelsLike()) %>°C</strong></div>
            <div class="detail-item">Humidity<strong><%= weather.getHumidity() %>%</strong></div>
            <div class="detail-item">Wind Speed<strong><%= String.format("%.1f", weather.getWindSpeed()) %> km/h</strong></div>
            <div class="detail-item">Sunrise / Sunset<strong><%= weather.getSunrise().substring(11) %> / <%= weather.getSunset().substring(11) %></strong></div>
        </div>

        <div class="forecast-title">5-Day Forecast</div>
        <%
            List<ForecastDay> forecast = weather.getForecast();
            for (ForecastDay day : forecast) {
        %>
            <div class="forecast-row">
                <span class="forecast-date"><%= day.getDate() %></span>
                <span class="forecast-desc"><%= day.getDescription() %></span>
                <span class="forecast-temps"><%= String.format("%.0f", day.getMaxTemp()) %>° / <%= String.format("%.0f", day.getMinTemp()) %>°</span>
            </div>
        <%
            }
        %>

        <a class="back-link" href="/weatherapp/views/index.jsp">← Search again</a>
    </div>
</body>
</html>