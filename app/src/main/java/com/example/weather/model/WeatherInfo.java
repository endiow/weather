package com.example.weather.model;

/**
 * 天气信息数据模型
 */
public class WeatherInfo 
{
    private String cityName;
    private String temperature;
    private String minTemperature;
    private String maxTemperature;
    private String weatherType;
    private String weatherDescription;
    private String airQuality;
    private String updateTime;
    private String humidity;
    private String windDirection;
    private String windPower;
    private String aqi;
    private String visibility;
    
    public WeatherInfo() 
    {
    }
    
    public WeatherInfo(String cityName, String temperature, String minTemperature, 
                      String maxTemperature, String weatherType, 
                      String weatherDescription, String airQuality, String updateTime,
                      String humidity, String windDirection, String windPower,
                      String aqi, String visibility) 
    {
        this.cityName = cityName;
        this.temperature = temperature;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.weatherType = weatherType;
        this.weatherDescription = weatherDescription;
        this.airQuality = airQuality;
        this.updateTime = updateTime;
        this.humidity = humidity;
        this.windDirection = windDirection;
        this.windPower = windPower;
        this.aqi = aqi;
        this.visibility = visibility;
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
    
    public String getTemperature() 
    {
        return temperature;
    }
    
    public void setTemperature(String temperature) 
    {
        this.temperature = temperature;
    }
    
    public String getMinTemperature() 
    {
        return minTemperature;
    }
    
    public void setMinTemperature(String minTemperature) 
    {
        this.minTemperature = minTemperature;
    }
    
    public String getMaxTemperature() 
    {
        return maxTemperature;
    }
    
    public void setMaxTemperature(String maxTemperature) 
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
    
    public String getWeatherDescription() 
    {
        return weatherDescription;
    }
    
    public void setWeatherDescription(String weatherDescription) 
    {
        this.weatherDescription = weatherDescription;
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
    
    public String getHumidity() 
    {
        return humidity;
    }
    
    public void setHumidity(String humidity) 
    {
        this.humidity = humidity;
    }
    
    public String getWindDirection() 
    {
        return windDirection;
    }
    
    public void setWindDirection(String windDirection) 
    {
        this.windDirection = windDirection;
    }
    
    public String getWindPower() 
    {
        return windPower;
    }
    
    public void setWindPower(String windPower) 
    {
        this.windPower = windPower;
    }
    
    public String getAqi() 
    {
        return aqi;
    }
    
    public void setAqi(String aqi) 
    {
        this.aqi = aqi;
    }
    
    public String getVisibility() 
    {
        return visibility;
    }
    
    public void setVisibility(String visibility) 
    {
        this.visibility = visibility;
    }
    
    public String getTemperatureRange() 
    {
        if (minTemperature != null && maxTemperature != null) 
        {
            return minTemperature + " / " + maxTemperature;
        }
        return "N/A";
    }
    
    /**
     * 获取适合显示的天气信息文本
     */
    public String getWeatherInfoText() 
    {
        StringBuilder builder = new StringBuilder();
        if (weatherDescription != null && !weatherDescription.isEmpty()) 
        {
            builder.append(weatherDescription);
        }
        if (humidity != null && !humidity.isEmpty()) 
        {
            builder.append(" · 湿度").append(humidity);
        }
        if (windDirection != null && !windDirection.isEmpty() && windPower != null && !windPower.isEmpty()) 
        {
            builder.append(" · ").append(windDirection).append(windPower);
        }
        return builder.toString();
    }
} 