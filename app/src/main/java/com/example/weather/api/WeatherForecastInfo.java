package com.example.weather.api;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 天气预报信息类
 */
public class WeatherForecastInfo 
{
    private static final String TAG = "WeatherForecastInfo";
    
    // 基本信息
    public String code = "";           // 状态码
    public String updateTime = "";     // 更新时间
    public String fxLink = "";         // 数据页面链接
    public String error = null;        // 错误信息，null表示无错误
    
    // 预报数据列表
    public List<DailyForecast> dailyForecasts = new ArrayList<>();
    
    /**
     * 是否有错误
     */
    public boolean hasError() 
    {
        return error != null;
    }
    
    /**
     * 添加日预报
     */
    public void addDailyForecast(DailyForecast forecast) 
    {
        if (forecast != null) 
        {
            dailyForecasts.add(forecast);
        }
    }
    
    /**
     * 获取指定天数的预报
     */
    public List<DailyForecast> getForecasts(int days) 
    {
        int count = Math.min(days, dailyForecasts.size());
        return dailyForecasts.subList(0, count);
    }
    
    /**
     * 日预报信息类
     */
    public static class DailyForecast 
    {
        // 日期和时间
        public String fxDate = "";     // 预报日期
        public String sunrise = "";    // 日出时间
        public String sunset = "";     // 日落时间
        
        // 天气状况
        public String iconDay = "";    // 白天天气图标代码
        public String textDay = "";    // 白天天气状况文字
        public String iconNight = "";  // 夜间天气图标代码
        public String textNight = "";  // 夜间天气状况文字
        
        // 温度
        public String tempMax = "";    // 最高温度
        public String tempMin = "";    // 最低温度
        
        // 风况
        public String windDirDay = "";     // 白天风向
        public String windScaleDay = "";   // 白天风力等级
        public String windDirNight = "";   // 夜间风向
        public String windScaleNight = ""; // 夜间风力等级
        
        // 其他指标
        public String precip = "";     // 降水量
        public String uvIndex = "";    // 紫外线强度
        public String humidity = "";   // 相对湿度
        
        /**
         * 获取天气描述
         */
        public String getWeatherDescription() 
        {
            return textDay;
        }
        
        /**
         * 获取温度范围
         */
        public String getTemperatureRange() 
        {
            return tempMin + "° / " + tempMax + "°";
        }
        
        /**
         * 获取简要描述
         */
        @Override
        public String toString() 
        {
            return fxDate + ": " + textDay + ", " + tempMin + "°-" + tempMax + "°";
        }
    }
    
    @Override
    public String toString() 
    {
        if (hasError()) 
        {
            return "错误: " + error;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("天气预报: ").append(updateTime).append("\n");
        
        for (DailyForecast forecast : dailyForecasts) 
        {
            sb.append(forecast.toString()).append("\n");
        }
        
        return sb.toString();
    }
} 