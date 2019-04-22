package com.anaek.assignment.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.anaek.assignment.dto.FileInformation;
import com.anaek.assignment.service.WeatherService;
import com.anaek.constants.UtilConstants;

@Service
public class WeatherServiceImpl implements WeatherService{

	@Cacheable(cacheNames = "MetarInfoCache", key = "#scode")
	public Object getWeatherInfo(String scode) {
		HashMap<String, FileInformation> map = new HashMap<>(); 
		JSONObject output = new JSONObject();
		try {
			map = readAllStationData();
			 DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
			   LocalDateTime now = LocalDateTime.now();  
			 	System.out.println(dtf.format(now));
			 	URL url = new URL(UtilConstants.ALL_METAR_STATION_API + scode);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod(UtilConstants.GET_METHOD_TYPE);
				connection.connect();
				ArrayList<String> data =readStationData(url);
				if(data.size() != 0) {
					 output = createJsonObject(data,scode);
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
		 DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		   LocalDateTime x = LocalDateTime.now();  
		 	System.out.println(dtf.format(x));
		return output.toString();
	}
	
	public JSONObject createJsonObject(ArrayList<String> line,String scode) {
		JSONObject ans = new JSONObject();
		ans = addStationResponseObject(line,ans,scode);
		ans = addDateAndTimeInResponseObject(line,ans);
		ans = addTempAndWindInResponseObject(line,ans,scode);
		JSONObject response = new JSONObject();
		response.put("data", ans);
		return response;
	}
	
	public JSONObject addDateAndTimeInResponseObject(ArrayList<String> line,JSONObject response) {
		String[] line0 = line.get(0).split(" ");
		response.put("last_observation", line0[0] + " at " + line0[1] + " GMT ");
		return response;
	}
	
	public JSONObject addStationResponseObject(ArrayList<String> line,JSONObject response,String scode) {
		String[] x = line.get(1).split(" ");
		if(scode.contains(x[0])) {
			response.put("station", x[0]);
		}
		return response;
	}
	
	public JSONObject addTempAndWindInResponseObject(ArrayList<String> line,JSONObject response,String scode) {
		String[] x = line.get(1).split(" ");
		for(String l : x) {
			int size = l.length();
			if(size > 2) {
				if(l.substring(size-2).equals("KT")) {
					char[] wind = l.toCharArray();
					String degree = UtilConstants.BLANK_STRING;
					String speed = UtilConstants.BLANK_STRING;
					String knot = UtilConstants.BLANK_STRING;
					if(Character.isDigit(wind[0]) && Character.isDigit(wind[1]) && Character.isDigit(wind[2])) {
						 degree = l.substring(0,3);
					}
					if(Character.isDigit(wind[3]) && Character.isDigit(wind[4])) {
						 speed = l.substring(3,5);
					}
					if(wind[5]  != 'G') {
						 knot = "0";
					}else {
						 knot = l.substring(6);
					}
					response.put("wind", degree + " degree at " + speed + " mph (" +knot + " knots)"  );
				}
			}
			if(l.contains("/")) {
				int count =0;
				char[] arr = l.toCharArray();
				for(Character c : arr) {
					if(c == '/') {
						count++;
					}
				}
				if (count == 1) {
					char[] a = l.toCharArray();
					boolean flag = true;
					for (int i = 0; i < a.length; i++) {
						if (a[i] == 'M' || a[i] == '1' || a[i] == '2' || a[i] == '3' || a[i] == '4' || a[i] == '5'
								|| a[i] == '6' || a[i] == '7' || a[i] == '8' || a[i] == '9' || a[i] == '0'
								|| a[i] == '/') {
							flag = true;
						} else {
							flag = false;
							break;
						}
					}
					if (flag == true) {
						String[] temp = l.split("/");
						if (temp[0].length() <= 3) {
							if (temp[0].charAt(0) == 'M') {
								String f = temp[0].substring(1);
								int val = Integer.parseInt(f);
								int far = (val * 9) / 5;
								far = 32 - far;
								response.put("temperature", "-" + f + " C (" + far + ") F");
							} else {
								String f = temp[0];
								int val = Integer.parseInt(f);
								int far = (val * 9) / 5;
								far = far + 32;
								response.put("temperature", temp[0] + " C (" + far + ") F");
							}
						}
					}
				}
			}
		}
		return response;
	}
	
	
	
	public HashMap<String, FileInformation> readAllStationData(){
		 HashMap<String, FileInformation> map = new HashMap<>();
		Document doc;
		try {
			doc = Jsoup.connect(UtilConstants.ALL_METAR_STATION_API).maxBodySize(0).get();
			Elements trs = doc.select(UtilConstants.HTML_ELEMENT_TABLE_ROW);
			 for (int i =3;i<(trs.size()-1);i++) {
				 Elements tds = trs.get(i).getElementsByTag(UtilConstants.HTML_ELEMENT_TD);
				 String fileName = tds.get(0).text();
				 String lastUpdatedDate = tds.get(1).text();
				 String size = tds.get(2).text();
				 map.put(fileName,new FileInformation(fileName, lastUpdatedDate, size));
			 }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public ArrayList<String> readStationData(URL url) {
		ArrayList<String> data = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String inputLine;
			while((inputLine = reader.readLine()) != null){
				data.add(inputLine);
			}
			reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return data;
		
	}
	
	@CacheEvict(value = "MetarInfoCache", key="#scode", beforeInvocation = true) 
	@Cacheable(cacheNames = "MetarInfoCache", key = "#scode")
	public Object getWeatherInfoWithoutCache(String scode) {
		return getWeatherInfo(scode);
	}

}
