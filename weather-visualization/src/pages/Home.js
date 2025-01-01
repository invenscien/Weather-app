import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles.css";

function Home() {
  const [city, setCity] = useState("");
  const navigate = useNavigate();

  const handleSearch = () => {
    if (city) {
      navigate(`/weather/${city}`);
    }
  };

  return (
    <div className="home-container">
      <h1>Weather Visualization App</h1>
      <input
        type="text"
        placeholder="Enter city name"
        value={city}
        onChange={(e) => setCity(e.target.value)}
      />
      <button onClick={handleSearch}>Search Weather</button>
    </div>
  );
}

export default Home;
