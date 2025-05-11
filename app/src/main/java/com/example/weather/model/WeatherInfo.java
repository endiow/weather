package com.example.weather.model;

/**
 * 天气信息数据模型
 */
public class WeatherInfo 
{
    private String cityName;
    private double temperature;
    private double minTemperature;
    private double maxTemperature;
    private String weatherType;
    private String description;
    private String airQuality;
    private String updateTime;
    
    public WeatherInfo() {
    }
    
    public WeatherInfo(String cityName, double temperature, double minTemperature, 
                      double maxTemperature, String weatherType, 
                      String description, String airQuality, String updateTime) 
    {
        this.cityName = cityName;
        this.temperature = temperature;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.weatherType = weatherType;
        this.description = description;
        this.airQuality = airQuality;
        this.updateTime = updateTime;
    }
    
    // Getters and Setters
    public String getCityName() 
    {
        return cityName;
    }
    
    public void setCityName(String cityName) 
    {
        this.cityName = cityName;
    }
    
    public double getTemperature() 
    {
        return temperature;
    }
    
    public void setTemperature(double temperature) 
    {
        this.temperature = temperature;
    }
    
    public double getMinTemperature() 
    {
        return minTemperature;
    }
    
    public void setMinTemperature(double minTemperature) 
    {
        this.minTemperature = minTemperature;
    }
    
    public double getMaxTemperature() 
    {
        return maxTemperature;
    }
    
    public void setMaxTemperature(double maxTemperature) 
    {
        this.maxTemperature = maxTemperature;
    }
    
    public String getWeatherType() 
    {
        return weatherType;
    }
    
    public void setWeatherType(String weatherType) 
    {
        this.weatherType = weatherType;
    }
    
    public String getDescription() 
    {
        return description;
    }
    
    public void setDescription(String description) 
    {
        this.description = description;
    }
    
    public String getAirQuality() 
    {
        return airQuality;
    }
    
    public void setAirQuality(String airQuality) 
    {
        this.airQuality = airQuality;
    }
    
    public String getUpdateTime() 
    {
        return updateTime;
    }
    
    public void setUpdateTime(String updateTime) 
    {
        this.updateTime = updateTime;
    }
    
    public String getTemperatureRange() 
    {
        return (int)minTemperature + "° / " + (int)maxTemperature + "°";
    }
    
    public String getTemperatureWithUnit() 
    {
        return (int)temperature + "°";
    }
} 