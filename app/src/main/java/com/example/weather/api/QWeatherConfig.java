package com.example.weather.api;

/**
 * 和风天气API配置
 */
public class QWeatherConfig 
{
    // 和风天气API密钥
    public static final String API_KEY = "39460e8f56a1455a99d1832df2a6d29a";
    
    // 和风天气API基础URL
    public static final String BASE_URL = "https://devapi.qweather.com/v7/";
    
    // 天气实况API路径
    public static final String WEATHER_NOW = "weather/now";
    
    // 空气质量API路径
    public static final String AIR_NOW = "air/now";
    
    // 三日天气预报API路径
    public static final String WEATHER_FORECAST = "weather/3d";
    
    // 用于构建请求参数
    public static final String PARAM_KEY = "key";
    public static final String PARAM_LOCATION = "location";
    
    // 成功状态码
    public static final String STATUS_OK = "200";
} 