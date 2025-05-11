package com.example.weather.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 待办事项数据模型
 */
public class Todo 
{
    private long id;
    private String title;
    private String description;
    private String weatherType;
    private boolean isCompleted;
    private boolean isRemindable;
    private String startTime; // 开始时间，格式: HH:mm
    private String endTime;   // 结束时间，格式: HH:mm
    private List<String> daysOfWeek; // 星期几
    private String airQuality; // 空气质量
    private String humidity; // 空气湿度
    
    public Todo() 
    {
        this.daysOfWeek = new ArrayList<>();
    }
    
    public Todo(String title, String description, String weatherType, boolean isRemindable) 
    {
        this.title = title;
        this.description = description;
        this.weatherType = weatherType;
        this.isRemindable = isRemindable;
        this.isCompleted = false;
        this.startTime = "08:00";
        this.endTime = "18:00";
        this.daysOfWeek = new ArrayList<>();
        this.airQuality = "良好";
        this.humidity = "适中";
    }
    
    public Todo(String title, String description, String weatherType, 
               boolean isRemindable, String startTime, String endTime) 
    {
        this.title = title;
        this.description = description;
        this.weatherType = weatherType;
        this.isRemindable = isRemindable;
        this.isCompleted = false;
        this.startTime = startTime;
        this.endTime = endTime;
        this.daysOfWeek = new ArrayList<>();
        this.airQuality = "良好";
        this.humidity = "适中";
    }
    
    public Todo(String title, String description, String weatherType, 
               boolean isRemindable, String startTime, String endTime, 
               String airQuality, String humidity) 
    {
        this.title = title;
        this.description = description;
        this.weatherType = weatherType;
        this.isRemindable = isRemindable;
        this.isCompleted = false;
        this.startTime = startTime;
        this.endTime = endTime;
        this.daysOfWeek = new ArrayList<>();
        this.airQuality = airQuality;
        this.humidity = humidity;
    }
    
    public Todo(long id, String title, String description, String weatherType, 
               boolean isCompleted, boolean isRemindable, String startTime, String endTime) 
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.weatherType = weatherType;
        this.isCompleted = isCompleted;
        this.isRemindable = isRemindable;
        this.startTime = startTime;
        this.endTime = endTime;
        this.daysOfWeek = new ArrayList<>();
        this.airQuality = "良好";
        this.humidity = "适中";
    }
    
    public Todo(long id, String title, String description, String weatherType, 
               boolean isCompleted, boolean isRemindable, String startTime, String endTime,
               String airQuality, String humidity) 
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.weatherType = weatherType;
        this.isCompleted = isCompleted;
        this.isRemindable = isRemindable;
        this.startTime = startTime;
        this.endTime = endTime;
        this.daysOfWeek = new ArrayList<>();
        this.airQuality = airQuality;
        this.humidity = humidity;
    }
    
    // Getters and Setters
    public long getId() 
    {
        return id;
    }
    
    public void setId(long id) 
    {
        this.id = id;
    }
    
    public String getTitle() 
    {
        return title;
    }
    
    public void setTitle(String title) 
    {
        this.title = title;
    }
    
    public String getDescription() 
    {
        return description;
    }
    
    public void setDescription(String description) 
    {
        this.description = description;
    }
    
    public String getWeatherType() 
    {
        return weatherType;
    }
    
    public void setWeatherType(String weatherType) 
    {
        this.weatherType = weatherType;
    }
    
    public boolean isCompleted() 
    {
        return isCompleted;
    }
    
    public void setCompleted(boolean completed) 
    {
        isCompleted = completed;
    }
    
    public boolean isRemindable() 
    {
        return isRemindable;
    }
    
    public void setRemindable(boolean remindable) 
    {
        isRemindable = remindable;
    }
    
    public String getStartTime() 
    {
        return startTime;
    }
    
    public void setStartTime(String startTime) 
    {
        this.startTime = startTime;
    }
    
    public String getEndTime() 
    {
        return endTime;
    }
    
    public void setEndTime(String endTime) 
    {
        this.endTime = endTime;
    }
    
    public List<String> getDaysOfWeek() 
    {
        return daysOfWeek;
    }
    
    public void setDaysOfWeek(List<String> daysOfWeek) 
    {
        this.daysOfWeek = daysOfWeek;
    }
    
    public String getAirQuality() 
    {
        return airQuality;
    }
    
    public void setAirQuality(String airQuality) 
    {
        this.airQuality = airQuality;
    }
    
    public String getHumidity() 
    {
        return humidity;
    }
    
    public void setHumidity(String humidity) 
    {
        this.humidity = humidity;
    }
    
    /**
     * 获取时间段显示字符串
     */
    public String getTimeRangeDisplay() 
    {
        return startTime + " - " + endTime;
    }
    
    /**
     * 检查此待办事项是否与指定的天气类型匹配
     * @param currentWeather 当前天气类型
     * @return 是否匹配
     */
    public boolean matchesWeather(String currentWeather) 
    {
        return this.weatherType.equals(currentWeather) && this.isRemindable && !this.isCompleted;
    }
    
    /**
     * 检查当前时间是否在此待办事项的时间段内
     * @param currentTime 当前时间，格式: HH:mm
     * @return 是否在时间段内
     */
    public boolean isInTimeRange(String currentTime) 
    {
        return currentTime.compareTo(startTime) >= 0 && currentTime.compareTo(endTime) <= 0;
    }
    
    /**
     * 检查此待办事项是否与指定的天气类型匹配且在时间段内
     * @param currentWeather 当前天气类型
     * @param currentTime 当前时间，格式: HH:mm
     * @return 是否匹配且在时间段内
     */
    public boolean matchesWeatherAndTime(String currentWeather, String currentTime) 
    {
        return matchesWeather(currentWeather) && isInTimeRange(currentTime);
    }
    
    /**
     * 检查此待办事项是否与指定的天气状况（包括空气质量和湿度）匹配
     * @param currentWeather 当前天气类型
     * @param currentAirQuality 当前空气质量
     * @param currentHumidity 当前空气湿度
     * @return 是否匹配
     */
    public boolean matchesWeatherConditions(String currentWeather, String currentAirQuality, String currentHumidity) 
    {
        boolean weatherMatches = this.weatherType.equals(currentWeather);
        boolean airQualityMatches = this.airQuality.equals(currentAirQuality) || this.airQuality.equals("任意");
        boolean humidityMatches = this.humidity.equals(currentHumidity) || this.humidity.equals("任意");
        
        return weatherMatches && airQualityMatches && humidityMatches && this.isRemindable && !this.isCompleted;
    }
} 