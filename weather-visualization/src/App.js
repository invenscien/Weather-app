import React, { useState, useEffect } from "react";
import WeatherAnimation from "./components/WeatherAnimation"; // Animation for searched city
import "./App.css";

const App = () => {
  const [weatherData, setWeatherData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [city, setCity] = useState("");
  const [topCitiesWeather, setTopCitiesWeather] = useState([]);

  // Determine which animation to show based on the condition
  const getWeatherAnimation = (condition) => {
    if (!condition) return "default"; // Handle case when there is no condition

    const normalizedCondition = condition.toLowerCase(); // Normalize condition to lowercase for easier matching
    console.log("Normalized Condition: ", normalizedCondition); // Debugging line to check condition

    // Match substrings to the weather condition and return the appropriate animation
    if (normalizedCondition.includes("rain")) {
      return "rainy"; // Match any condition with "rain"
    }
    if (normalizedCondition.includes("sun")) {
      return "sunny"; // Match any condition with "sun"
    }
    if (normalizedCondition.includes("cloud")) {
      return "cloudy"; // Match any condition with "cloud"
    }
    if (normalizedCondition.includes("haze")) {
      return "haze"; // Match any condition with "haze"
    }
    if (normalizedCondition.includes("mist")) {
      return "mist"; // Match any condition with "mist"
    }
    return "default"; // Default animation if no match is found
  };

  useEffect(() => {
    const fetchTopCitiesWeather = async () => {
      try {
        const response = await fetch(
          "http://localhost:8080/api/weather/top-cities"
        );
        if (!response.ok)
          throw new Error("Unable to fetch top cities weather data");
        const data = await response.json();
        setTopCitiesWeather(data);
      } catch (err) {
        setError("Failed to fetch top cities weather data. Please try again.");
      }
    };

    fetchTopCitiesWeather();
  }, []);

  // Fetch weather data for the specific city
  const fetchWeather = async () => {
    if (!city.trim()) {
      setError("Please enter a valid city name.");
      setWeatherData(null); // Ensure no data is displayed
      return;
    }

    setLoading(true);
    setError(null); // Clear previous error
    try {
      const response = await fetch(`http://localhost:8080/api/weather/${city}`);

      if (!response.ok) {
        // Handle invalid city case (e.g., 404 Not Found)
        setWeatherData(null);
        setError("Not a valid city");
        return;
      }

      const data = await response.json();

      // If the response data is empty or invalid, show error
      if (!data || !data.condition || !data.temperature) {
        setWeatherData(null);
        setError("Not a valid city");
        return;
      }

      setWeatherData(data); // Set valid weather data
    } catch (err) {
      setWeatherData(null); // Clear weather data on error
      setError("Not a valid city"); // Generic error message for invalid city
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="app-container">
      <h1 className="app-title">Weather Visualization App</h1>
      {/* Search for a specific city */}
      <div className="input-container">
        <input
          type="text"
          placeholder="Enter City"
          value={city}
          onChange={(e) => setCity(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && fetchWeather()}
          className="city-input"
        />
        <button onClick={fetchWeather} className="fetch-button">
          Get Weather
        </button>
      </div>
      {/* Display loading message */}
      {loading && (
        <div className="loading-message">Fetching weather data...</div>
      )}
      {/* Display error message */}
      {error && <div className="error-message">{error}</div>}
      {weatherData && (
        <div className="weather-card">
          {/* Display the animation based on the weather condition */}
          <WeatherAnimation
            condition={getWeatherAnimation(weatherData.condition)}
          />
          <div className="weather-header">{weatherData.condition}</div>
          <div className="weather-detail">
            Temperature: <span>{weatherData.temperature.toFixed(1)}°C</span>
          </div>
          <div className="weather-detail">
            Wind Speed:{" "}
            <span>
              {weatherData.windSpeed.toFixed(1)} {weatherData.windSpeedUnit}
            </span>
          </div>
        </div>
      )}
      {/* Display weather data for top 4 cities of India */}
      <h3 className="top-cities-title">Weather in Cities of India</h3>
      <div className="top-cities-container">
        {topCitiesWeather.length > 0 ? (
          topCitiesWeather.slice(0, 4).map((data, index) => (
            <div key={index} className="top-city-card">
              <h4>{data.city}</h4>
              <div className="weather-detail">
                Temperature: <span>{data.temperature.toFixed(1)}°C</span>
              </div>
              <div className="weather-detail">
                Wind Speed: <span>{data.windSpeed.toFixed(1)} km/h</span>
              </div>
            </div>
          ))
        ) : (
          <div>Loading top cities weather...</div>
        )}
      </div>
    </div>
  );
};

export default App;
