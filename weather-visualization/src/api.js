const API_BASE_URL = "http://localhost:8080/api/weather";

export const fetchWeather = (city) => `${API_BASE_URL}/${city}`;
