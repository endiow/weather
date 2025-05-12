package com.example.weather.api;

import android.util.Log;

/**
 * 城市信息类
 */
public class CityInfo 
{
    private static final String TAG = "CityInfo";
    
    public String code = "";     // 状态码
    public String name = "";     // 城市名称
    public String id = "";       // 城市ID
    public String lat = "";      // 纬度
    public String lon = "";      // 经度
    public String adm2 = "";     // 上级行政区划
    public String adm1 = "";     // 所属一级行政区
    public String country = "";  // 所属国家
    public String error = null;  // 错误信息，null表示无错误
    
    /**
     * 是否有错误
     */
    public boolean hasError() 
    {
        return error != null;
    }
    
    /**
     * 获取显示名称（只显示两级：上级行政区划 + 城市名，中间无逗号）
     */
    public String getDisplayName() 
    {
        if (name.isEmpty()) 
        {
            return "未知位置";
        }
        
        // 只显示两级：上级行政区划 + 城市名，顺序颠倒
        if (!adm2.isEmpty() && !adm2.equals(name)) 
        {
            // 地区 + 城市
            return adm2 + " " + name;
        }
        else if (!adm1.isEmpty() && !adm1.equals(name)) 
        {
            // 省份 + 城市
            return adm1 + " " + name;
        }
        else 
        {
            // 只有城市名
            return name;
        }
    }
    
    @Override
    public String toString() 
    {
        if (hasError()) 
        {
            return "错误: " + error;
        }
        
        return "城市信息: " + getDisplayName() + ", ID: " + id;
    }
} 