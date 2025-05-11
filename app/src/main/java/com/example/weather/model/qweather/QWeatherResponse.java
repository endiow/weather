package com.example.weather.model.qweather;

import com.google.gson.annotations.SerializedName;

/**
 * 和风天气API响应基类
 */
public class QWeatherResponse 
{
    @SerializedName("code")
    private String code;
    
    @SerializedName("updateTime")
    private String updateTime;
    
    @SerializedName("fxLink")
    private String fxLink;
    
    // Getters
    public String getCode() 
    {
        return code;
    }
    
    public String getUpdateTime() 
    {
        return updateTime;
    }
    
    public String getFxLink() 
    {
        return fxLink;
    }
} 