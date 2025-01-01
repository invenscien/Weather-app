import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import WeatherCard from "../components/WeatherCard";
import LoadingState from "../components/LoadingState";
import ErrorState from "../components/ErrorState";
import Visualizations from "../components/Visualizations";

function WeatherDetails() {
  const { city } = useParams();
  const [weatherData, setWeatherData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    axios
      .get(`http://localhost:8080/api/weather/${city}`)
      .then((response) => {
        setWeatherData(response.data);
        setLoading(false);
      })
      .catch((err) => {
        setError("Failed to fetch weather data.");
        setLoading(false);
      });
  }, [city]);

  if (loading) return <LoadingState />;
  if (error) return <ErrorState message={error} />;

  return (
    <div>
      <WeatherCard data={weatherData} />
      <Visualizations data={weatherData} />
    </div>
  );
}

export default WeatherDetails;
