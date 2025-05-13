package com.example.weather.util;

/**
 * 天气类型工具类
 * 用于将和风天气API返回的天气文本分类为标准天气类型
 */
public class WeatherTypeUtil 
{
    /**
     * 天气类型枚举
     */
    public enum WeatherType 
    {
        SUNNY,    // 晴天
        CLOUDY,   // 多云
        OVERCAST, // 阴天
        RAINY,    // 雨天
        OTHER     // 其他
    }
    
    /**
     * 根据天气文本获取天气类型
     * 
     * @param weatherText 和风天气API返回的天气文本
     * @return 天气类型枚举值
     */
    public static WeatherType getWeatherType(String weatherText) 
    {
        if (weatherText == null || weatherText.isEmpty()) 
        {
            return WeatherType.OTHER;
        }
        
        // 转换为小写并去除空格，以便更好地匹配
        String text = weatherText.toLowerCase().trim();
        
        // 晴天
        if (text.equals("晴")) 
        {
            return WeatherType.SUNNY;
        }
        
        // 多云
        if (text.equals("多云")) 
        {
            return WeatherType.CLOUDY;
        }
        
        // 阴天
        if (text.equals("阴")) 
        {
            return WeatherType.OVERCAST;
        }
        
        // 雨天 - 包括各种类型的雨
        if (text.contains("雨")) 
        {
            return WeatherType.RAINY;
        }
        
        // 不符合以上任何类型，归为其他
        return WeatherType.OTHER;
    }
    
    /**
     * 获取天气类型的文本描述
     * 
     * @param type 天气类型枚举值
     * @return 天气类型的文本描述
     */
    public static String getWeatherTypeDescription(WeatherType type) 
    {
        switch (type) 
        {
            case SUNNY:
                return "晴天";
            case CLOUDY:
                return "多云";
            case OVERCAST:
                return "阴天";
            case RAINY:
                return "雨天";
            case OTHER:
            default:
                return "其他";
        }
    }
} 