import React from "react";
import WeatherAnimation from "./WeatherAnimation";

const WeatherCard = ({ data }) => {
  return (
    <div className="weather-card">
      <h2>City: {data.city || "Unknown"}</h2>
      <p>Temperature: {data.temperature.toFixed(2)}°C</p>
      <p>Condition: {data.condition || "N/A"}</p>
      <p>Wind Speed: {data.windSpeed.toFixed(2)} m/s</p>
      <WeatherAnimation condition={data.condition} />
    </div>
  );
};

export default WeatherCard;
