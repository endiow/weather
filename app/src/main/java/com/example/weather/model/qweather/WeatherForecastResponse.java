package com.example.weather.model.qweather;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 天气预报响应
 */
public class WeatherForecastResponse extends QWeatherResponse 
{
    @SerializedName("daily")
    private List<DailyForecast> daily;
    
    public List<DailyForecast> getDaily() 
    {
        return daily;
    }
    
    /**
     * 每日天气预报
     */
    public static class DailyForecast 
    {
        @SerializedName("fxDate")
        private String fxDate; // 预报日期
        
        @SerializedName("sunrise")
        private String sunrise; // 日出时间
        
        @SerializedName("sunset")
        private String sunset; // 日落时间
        
        @SerializedName("moonrise")
        private String moonrise; // 月升时间
        
        @SerializedName("moonset")
        private String moonset; // 月落时间
        
        @SerializedName("moonPhase")
        private String moonPhase; // 月相名称
        
        @SerializedName("tempMax")
        private String tempMax; // 最高温度
        
        @SerializedName("tempMin")
        private String tempMin; // 最低温度
        
        @SerializedName("iconDay")
        private String iconDay; // 白天天气图标代码
        
        @SerializedName("textDay")
        private String textDay; // 白天天气状况文字描述
        
        @SerializedName("iconNight")
        private String iconNight; // 夜间天气图标代码
        
        @SerializedName("textNight")
        private String textNight; // 夜间天气状况文字描述
        
        @SerializedName("wind360Day")
        private String wind360Day; // 白天风向360角度
        
        @SerializedName("windDirDay")
        private String windDirDay; // 白天风向
        
        @SerializedName("windScaleDay")
        private String windScaleDay; // 白天风力等级
        
        @SerializedName("windSpeedDay")
        private String windSpeedDay; // 白天风速
        
        @SerializedName("wind360Night")
        private String wind360Night; // 夜间风向360角度
        
        @SerializedName("windDirNight")
        private String windDirNight; // 夜间风向
        
        @SerializedName("windScaleNight")
        private String windScaleNight; // 夜间风力等级
        
        @SerializedName("windSpeedNight")
        private String windSpeedNight; // 夜间风速
        
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
        
        @SerializedName("uvIndex")
        private String uvIndex; // 紫外线指数
        
        // Getters
        public String getFxDate() 
        {
            return fxDate;
        }
        
        public String getTempMax() 
        {
            return tempMax;
        }
        
        public String getTempMin() 
        {
            return tempMin;
        }
        
        public String getIconDay() 
        {
            return iconDay;
        }
        
        public String getTextDay() 
        {
            return textDay;
        }
        
        public String getIconNight() 
        {
            return iconNight;
        }
        
        public String getTextNight() 
        {
            return textNight;
        }
        
        public String getHumidity() 
        {
            return humidity;
        }
        
        public String getWindDirDay() 
        {
            return windDirDay;
        }
        
        public String getWindScaleDay() 
        {
            return windScaleDay;
        }
    }
} 