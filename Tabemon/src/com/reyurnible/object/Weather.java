package com.reyurnible.object;

import android.animation.AnimatorSet.Builder;

public class Weather {
	public double temp;
	public double pressure;
	public int humidity;
	public double wind_speed;
	public int clouds;
	public String weather;
	
	public Weather(double temp,double pressure,int humidity,double wind_speed,int clouds,String weather){
		this.temp = temp-273.15;
		this.pressure = pressure;
		this.humidity = humidity;
		this.wind_speed = wind_speed;
		this.clouds = clouds;
		this.weather = weather;
	}
	
	public String toString(String sepalater){
		StringBuilder builder = new StringBuilder();
		builder.append("Temp:");
		builder.append(temp);
		builder.append(sepalater);
		builder.append("Pressure:");
		builder.append(pressure);
		builder.append(sepalater);
		builder.append("Humidity:");
		builder.append(humidity);
		builder.append(sepalater);
		builder.append("WindSpeed:");
		builder.append(wind_speed);
		builder.append(sepalater);
		builder.append("Clouds:");
		builder.append(clouds);
		builder.append(sepalater);
		builder.append("Weather:");
		builder.append(weather);
		return builder.toString();
	}
	
}
