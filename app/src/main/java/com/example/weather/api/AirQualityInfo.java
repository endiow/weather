package com.example.weather.api;

import android.util.Log;

/**
 * 空气质量信息类
 */
public class AirQualityInfo 
{
    private static final String TAG = "AirQualityInfo";
    
    // 基本信息
    public String code = "";           // 状态码
    public String error = null;        // 错误信息，null表示无错误
    
    // 空气质量数据
    public String tag = "";            // 数据标签
    public String aqiCode = "";        // 空气质量指数Code
    public String aqiName = "";        // 空气质量指数的名字
    public String aqi = "";            // 空气质量指数的值
    public String aqiDisplay = "";     // 空气质量指数的值的文本显示
    public String level = "";          // 空气质量指数等级
    public String category = "";       // 空气质量指数类别
    
    // 颜色信息
    public String colorRed = "";       // RGBA中的red
    public String colorGreen = "";     // RGBA中的green
    public String colorBlue = "";      // RGBA中的blue
    public String colorAlpha = "";     // RGBA中的alpha
    
    // 首要污染物
    public String pollutantCode = "";  // 首要污染物的Code
    public String pollutantName = "";  // 首要污染物的名字
    public String pollutantFullName = ""; // 首要污染物的全称
    
    /**
     * 是否有错误
     */
    public boolean hasError() 
    {
        return error != null;
    }
    
    /**
     * 获取可显示的空气质量文本
     */
    public String getDisplayText() 
    {
        if (aqiDisplay.isEmpty()) 
        {
            if (!category.isEmpty()) 
            {
                return category + " " + aqi;
            }
            return aqi;
        }
        return aqiDisplay;
    }
    
    @Override
    public String toString() 
    {
        if (hasError()) 
        {
            return "错误: " + error;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("空气质量: ").append(getDisplayText());
        
        if (!pollutantName.isEmpty()) 
        {
            sb.append(", 首要污染物: ").append(pollutantName);
        }
        
        return sb.toString();
    }
} 