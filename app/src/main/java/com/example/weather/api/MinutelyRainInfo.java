package com.example.weather.api;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 分钟级降水信息类
 */
public class MinutelyRainInfo 
{
    private static final String TAG = "MinutelyRainInfo";
    
    // 基本信息
    public String code = "";           // 状态码
    public String updateTime = "";     // 更新时间
    public String fxLink = "";         // 数据页面链接
    public String summary = "";        // 降水概况描述
    public String error = null;        // 错误信息，null表示无错误
    
    // 预报数据列表
    public List<MinutelyData> minutelyList = new ArrayList<>();
    
    /**
     * 是否有错误
     */
    public boolean hasError() 
    {
        return error != null;
    }
    
    /**
     * 添加分钟级降水数据
     */
    public void addMinutelyData(MinutelyData data) 
    {
        if (data != null) 
        {
            minutelyList.add(data);
        }
    }
    
    /**
     * 获取最近一次降水开始的时间（分钟）
     * 如果短时间内没有降水，返回-1
     */
    public int getTimeToRain() 
    {
        if (minutelyList.isEmpty()) return -1;
        
        // 遍历预报数据，找到第一个有降水的时间点
        for (int i = 0; i < minutelyList.size(); i++) 
        {
            MinutelyData data = minutelyList.get(i);
            if (data.isRaining()) 
            {
                // 每5分钟一个数据点，所以返回索引乘以5
                return i * 5;
            }
        }
        
        return -1;  // 未来两小时内无降水
    }
    
    /**
     * 检查未来两小时是否有降水
     */
    public boolean willRainIn2Hours() 
    {
        return getTimeToRain() >= 0;
    }
    
    /**
     * 获取降水预报描述
     */
    public String getRainForecastDescription() 
    {
        if (hasError()) 
        {
            return "无法获取降水预报: " + error;
        }
        
        if (summary != null && !summary.isEmpty()) 
        {
            return summary;
        }
        
        int timeToRain = getTimeToRain();
        if (timeToRain < 0) 
        {
            return "未来两小时内无降水";
        } 
        else if (timeToRain == 0) 
        {
            return "当前正在降水";
        } 
        else 
        {
            return timeToRain + "分钟后开始降水";
        }
    }
    
    /**
     * 分钟级降水数据类
     */
    public static class MinutelyData 
    {
        public String fxTime = "";     // 预报时间
        public String precip = "";     // 5分钟累计降水量，单位毫米
        public String type = "";       // 降水类型：rain = 雨，snow = 雪
        
        /**
         * 检查是否有降水
         */
        public boolean isRaining() 
        {
            try 
            {
                float precipValue = Float.parseFloat(precip);
                return precipValue > 0;
            } 
            catch (NumberFormatException e) 
            {
                return false;
            }
        }
        
        /**
         * 获取降水类型描述
         */
        public String getRainTypeDesc() 
        {
            if ("rain".equals(type)) 
            {
                return "雨";
            } 
            else if ("snow".equals(type)) 
            {
                return "雪";
            }
            return "未知";
        }
        
        @Override
        public String toString() 
        {
            return fxTime + ": " + precip + "mm/" + getRainTypeDesc();
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
        sb.append("降水预报: ").append(summary).append("\n");
        
        for (MinutelyData data : minutelyList) 
        {
            sb.append(data.toString()).append("\n");
        }
        
        return sb.toString();
    }
} 