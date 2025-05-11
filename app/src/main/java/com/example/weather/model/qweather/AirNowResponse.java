package com.example.weather.model.qweather;

import com.google.gson.annotations.SerializedName;

/**
 * 空气质量响应
 */
public class AirNowResponse extends QWeatherResponse 
{
    @SerializedName("now")
    private AirNow now;
    
    public AirNow getNow() 
    {
        return now;
    }
    
    /**
     * 空气质量数据
     */
    public static class AirNow 
    {
        @SerializedName("pubTime")
        private String pubTime; // 数据发布时间
        
        @SerializedName("aqi")
        private String aqi; // 空气质量指数
        
        @SerializedName("category")
        private String category; // 空气质量指数等级
        
        @SerializedName("primary")
        private String primary; // 主要污染物
        
        @SerializedName("pm10")
        private String pm10; // PM10
        
        @SerializedName("pm2p5")
        private String pm2p5; // PM2.5
        
        @SerializedName("no2")
        private String no2; // 二氧化氮
        
        @SerializedName("so2")
        private String so2; // 二氧化硫
        
        @SerializedName("co")
        private String co; // 一氧化碳
        
        @SerializedName("o3")
        private String o3; // 臭氧
        
        // Getters
        public String getAqi() 
        {
            return aqi;
        }
        
        public String getCategory() 
        {
            return category;
        }
        
        public String getPrimary() 
        {
            return primary;
        }
        
        public String getPm10() 
        {
            return pm10;
        }
        
        public String getPm2p5() 
        {
            return pm2p5;
        }
    }
} 