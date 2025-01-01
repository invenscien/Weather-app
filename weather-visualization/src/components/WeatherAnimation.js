import React from "react";
import Lottie from "react-lottie";
import sunny from "./animations/sunny.json";
import rainy from "./animations/rainy.json";
import cloudy from "./animations/cloudy.json";
import mist from "./animations/mist.json";
import haze from "./animations/haze.json";

const WeatherAnimation = ({ condition }) => {
  const getAnimationData = () => {
    switch (condition.toLowerCase()) {
      case "sunny":
        return sunny;
      case "rainy":
        return rainy;
      case "cloudy":
        return cloudy;
      case "mist":
        return mist;
      case "haze":
        return haze;
      default:
        return sunny; // Default animation for unrecognized conditions
    }
  };

  const animationOptions = {
    loop: true,
    autoplay: true,
    animationData: getAnimationData(),
    rendererSettings: {
      preserveAspectRatio: "xMidYMid slice",
    },
  };

  return <Lottie options={animationOptions} height={300} width={300} />;
};

export default WeatherAnimation;
