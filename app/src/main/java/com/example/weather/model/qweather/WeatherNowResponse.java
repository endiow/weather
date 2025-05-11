package com.example.weather.model.qweather;

import com.google.gson.annotations.SerializedName;

/**
 * 实时天气响应
 */
public class WeatherNowResponse extends QWeatherResponse 
{
    @SerializedName("now")
    private WeatherNow now;
    
    public WeatherNow getNow() 
    {
        return now;
    }
    
    /**
     * 实时天气数据
     */
    public static class WeatherNow 
    {
        @SerializedName("temp")
        private String temperature; // 温度
        
        @SerializedName("feelsLike")
        private String feelsLike; // 体感温度
        
        @SerializedName("icon")
        private String icon; // 天气图标代码
        
        @SerializedName("text")
        private String text; // 天气状况文字描述
        
        @SerializedName("wind360")
        private String wind360; // 风向360角度
        
        @SerializedName("windDir")
        private String windDir; // 风向
        
        @SerializedName("windScale")
        private String windScale; // 风力等级
        
        @SerializedName("windSpeed")
        private String windSpeed; // 风速
        
        @SerializedName("humidity")
        private String humidity; // 相对湿度
        
        @SerializedName("precip")
        private String precip; // 降水量
        
        @SerializedName("pressure")
        private String pressure; // 大气压强
        
        @SerializedName("vis")
        private String visibility; // 能见度
        
        @SerializedName("cloud")
        private String cloud; // 云量
        
        @SerializedName("dew")
        private String dew; // 露点温度
        
        // Getters
        public String getTemperature() 
        {
            return temperature;
        }
        
        public String getFeelsLike() 
        {
            return feelsLike;
        }
        
        public String getIcon() 
        {
            return icon;
        }
        
        public String getText() 
        {
            return text;
        }
        
        public String getWindDir() 
        {
            return windDir;
        }
        
        public String getWindScale() 
        {
            return windScale;
        }
        
        public String getHumidity() 
        {
            return humidity;
        }
        
        public String getVisibility() 
        {
            return visibility;
        }
    }
} 