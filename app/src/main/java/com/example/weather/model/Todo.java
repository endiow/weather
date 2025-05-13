package com.example.weather.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.weather.db.converters.DateConverter;
import com.example.weather.db.converters.StringListConverter;
import com.example.weather.db.converters.BooleanArrayConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 待办事项模型类
 */
@Entity(tableName = "todos")
@TypeConverters({DateConverter.class, StringListConverter.class, BooleanArrayConverter.class})
public class Todo 
{
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String title;
    private String description;
    private Date startTime;
    private Date endTime;
    
    // 存储多个天气类型
    private List<String> weatherTypes;
    
    // 存储空气质量类型
    private String airQuality;
    
    // 存储湿度范围
    private String humidity;
    
    // 周一到周日，对应0-6索引
    private boolean[] daysOfWeek;
    
    private boolean remindable;
    private boolean completed;
    private Date createdAt;
    private Date updatedAt;

    // 无参构造函数，供Room使用
    public Todo() 
    {
        this.daysOfWeek = new boolean[7];
        this.weatherTypes = new ArrayList<>();
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // 带参数的构造函数，添加@Ignore注解
    @Ignore
    public Todo(String title, String description) 
    {
        this();
        this.title = title;
        this.description = description;
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

    public List<String> getWeatherTypes() 
    {
        return weatherTypes;
    }

    public void setWeatherTypes(List<String> weatherTypes) 
    {
        this.weatherTypes = weatherTypes;
    }

    public void addWeatherType(String weatherType) 
    {
        if (this.weatherTypes == null) 
        {
            this.weatherTypes = new ArrayList<>();
        }
        if (!this.weatherTypes.contains(weatherType)) 
        {
            this.weatherTypes.add(weatherType);
        }
    }

    public void removeWeatherType(String weatherType) 
    {
        if (this.weatherTypes != null) 
        {
            this.weatherTypes.remove(weatherType);
        }
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
        this.updatedAt = new Date();
    }
    
    public Date getCreatedAt() 
    {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() 
    {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) 
    {
        this.updatedAt = updatedAt;
    }
} 