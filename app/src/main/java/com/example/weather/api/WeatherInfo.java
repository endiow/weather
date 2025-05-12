package com.example.weather.api;

import android.util.Log;

/**
 * 天气信息类
 */
public class WeatherInfo 
{
    private static final String TAG = "WeatherInfo";
    
    // 基本信息
    public String code = "";           // 状态码
    public String updateTime = "";     // 更新时间
    public String fxLink = "";         // 数据页面链接
    
    // 实时天气数据
    public String obsTime = "";        // 观测时间
    public String temp = "";           // 温度
    public String feelsLike = "";      // 体感温度
    public String icon = "";           // 天气图标代码
    public String text = "";           // 天气状况文字描述
    public String wind360 = "";        // 风向360角度
    public String windDir = "";        // 风向
    public String windScale = "";      // 风力等级
    public String windSpeed = "";      // 风速
    public String humidity = "";       // 相对湿度
    public String precip = "";         // 降水量
    public String pressure = "";       // 大气压强
    public String vis = "";            // 能见度
    public String cloud = "";          // 云量
    public String dew = "";            // 露点温度
    
    public String error = null;        // 错误信息，null表示无错误
    
    /**
     * 是否有错误
     */
    public boolean hasError() 
    {
        return error != null;
    }
    
    /**
     * 获取温度（带单位）
     */
    public String getTemperature() 
    {
        return temp + "°";
    }
    
    /**
     * 获取体感温度（带单位）
     */
    public String getFeelsLikeTemperature() 
    {
        return feelsLike + "°";
    }
    
    @Override
    public String toString() 
    {
        if (hasError()) 
        {
            return "错误: " + error;
        }
        
        return "天气: " + text + ", " + temp + "°C, " + windDir + windScale + "级";
    }
} 