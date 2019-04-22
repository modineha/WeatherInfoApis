package com.anaek.assignment.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anaek.assignment.service.WeatherService;

@RestController
@RequestMapping("/metar")
public class WeatherController {
	
	@Autowired
	public WeatherService weatherService;
	
	@GetMapping(path = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object getWeatherInfo(@RequestParam String scode, @RequestParam(required=false) String nocache) {
		if(nocache != null && nocache.equals("1")) {
			return weatherService.getWeatherInfoWithoutCache(scode);
		}else {
			return weatherService.getWeatherInfo(scode);
		}
	}
	
	@GetMapping(path = "/ping", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object getPingResponse() {
		JSONObject response = new JSONObject();
		response.put("data", "pong");
		return response.toString();
	}
	
}
