import React from "react";
import { useSpring, animated } from "react-spring";

function Visualizations({ data }) {
  const weatherAnimation = useSpring({
    transform: `scale(${data.temperature > 25 ? 1.2 : 0.8})`,
    config: { tension: 170, friction: 26 },
  });

  return (
    <div className="visualizations-container">
      <animated.div style={weatherAnimation} className="weather-visual">
        {data.condition.includes("rain") ? "🌧️" : "☀️"}
      </animated.div>
    </div>
  );
}

export default Visualizations;
