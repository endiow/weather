package com.example.weather.model;

import java.util.Date;

/**
 * 待办事项模型类
 */
public class Todo 
{
    private long id;
    private String title;
    private String description;
    private Date startTime;
    private Date endTime;
    private String weatherType;
    private String airQuality;
    private String humidity;
    private boolean[] daysOfWeek; // 周一到周日，对应0-6索引
    private boolean remindable;
    private boolean completed;

    public Todo() 
    {
        this.daysOfWeek = new boolean[7];
    }

    public Todo(String title, String description) 
    {
        this.title = title;
        this.description = description;
        this.daysOfWeek = new boolean[7];
        this.remindable = true;
        this.completed = false;
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

    public Date getStartTime() 
    {
        return startTime;
    }

    public void setStartTime(Date startTime) 
    {
        this.startTime = startTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }

    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public String getWeatherType() 
    {
        return weatherType;
    }

    public void setWeatherType(String weatherType) 
    {
        this.weatherType = weatherType;
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

    public boolean[] getDaysOfWeek() 
    {
        return daysOfWeek;
    }

    public void setDaysOfWeek(boolean[] daysOfWeek) 
    {
        this.daysOfWeek = daysOfWeek;
    }

    public boolean isRemindable() 
    {
        return remindable;
    }

    public void setRemindable(boolean remindable) 
    {
        this.remindable = remindable;
    }

    public boolean isCompleted() 
    {
        return completed;
    }

    public void setCompleted(boolean completed) 
    {
        this.completed = completed;
    }
} 