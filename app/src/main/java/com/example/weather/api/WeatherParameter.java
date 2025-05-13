package com.example.weather.api;

/**
 * 天气API请求参数
 */
public class WeatherParameter 
{
    private String location;
    private String lang = "zh";
    private String unit = "m"; // 公制单位

    /**
     * 构造函数
     * @param location 位置ID或经纬度坐标
     */
    public WeatherParameter(String location) 
    {
        this.location = location;
    }

    /**
     * 构造函数
     * @param latitude 纬度
     * @param longitude 经度
     */
    public WeatherParameter(double latitude, double longitude) 
    {
        // 经度,纬度格式，最多保留两位小数
        this.location = String.format("%.2f,%.2f", longitude, latitude);
    }

    /**
     * 获取位置参数
     */
    public String getLocation() 
    {
        return location;
    }

    /**
     * 设置语言
     */
    public WeatherParameter setLang(String lang) 
    {
        this.lang = lang;
        return this;
    }

    /**
     * 获取语言参数
     */
    public String getLang() 
    {
        return lang;
    }

    /**
     * 设置单位
     */
    public WeatherParameter setUnit(String unit) 
    {
        this.unit = unit;
        return this;
    }

    /**
     * 获取单位参数
     */
    public String getUnit() 
    {
        return unit;
    }
} 