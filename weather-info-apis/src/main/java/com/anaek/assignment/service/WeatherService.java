package com.anaek.assignment.service;

public interface WeatherService {
	
	Object getWeatherInfo(String scode);

	Object getWeatherInfoWithoutCache(String scode);
}
