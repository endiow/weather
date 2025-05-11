package com.example.weather.repository;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.weather.api.QWeatherClient;
import com.example.weather.model.WeatherInfo;
import com.example.weather.model.qweather.AirNowResponse;
import com.example.weather.model.qweather.WeatherForecastResponse;
import com.example.weather.model.qweather.WeatherNowResponse;

/**
 * 天气数据仓库
 */
public class WeatherRepository 
{
    private static final String TAG = "WeatherRepository";
    private static WeatherRepository instance;
    private final QWeatherClient weatherClient;
    
    /**
     * 单例模式获取实例
     */
    public static WeatherRepository getInstance() 
    {
        if (instance == null) 
        {
            synchronized (WeatherRepository.class) 
            {
                if (instance == null) 
                {
                    instance = new WeatherRepository();
                }
            }
        }
        return instance;
    }
    
    /**
     * 私有构造函数
     */
    private WeatherRepository() 
    {
        weatherClient = QWeatherClient.getInstance();
    }
    
    /**
     * 获取实时天气信息
     * @param location 位置对象
     * @param callback 回调
     */
    public void getWeatherData(Location location, final WeatherDataCallback callback) 
    {
        if (location == null) 
        {
            callback.onError("位置信息为空");
            return;
        }
        
        // 位置格式：经度,纬度
        String locationString = location.getLongitude() + "," + location.getLatitude();
        Log.d(TAG, "获取位置信息: " + locationString);
        
        // 创建天气信息对象
        final WeatherInfo weatherInfo = new WeatherInfo();
        
        // 获取实时天气
        weatherClient.getWeatherNow(locationString, new QWeatherClient.WeatherCallback<WeatherNowResponse>() 
        {
            @Override
            public void onSuccess(WeatherNowResponse response) 
            {
                WeatherNowResponse.WeatherNow now = response.getNow();
                
                // 填充天气信息
                weatherInfo.setTemperature(now.getTemperature() + "°C");
                weatherInfo.setWeatherDescription(now.getText());
                weatherInfo.setHumidity(now.getHumidity() + "%");
                weatherInfo.setWindDirection(now.getWindDir());
                weatherInfo.setWindPower(now.getWindScale() + "级");
                
                // 获取空气质量
                getAirQualityData(locationString, weatherInfo, callback);
            }
            
            @Override
            public void onError(String error) 
            {
                callback.onError("获取实时天气失败: " + error);
            }
        });
    }
    
    /**
     * 获取空气质量数据
     */
    private void getAirQualityData(String location, final WeatherInfo weatherInfo, final WeatherDataCallback callback) 
    {
        weatherClient.getAirNow(location, new QWeatherClient.WeatherCallback<AirNowResponse>() 
        {
            @Override
            public void onSuccess(AirNowResponse response) 
            {
                AirNowResponse.AirNow now = response.getNow();
                
                // 填充空气质量信息
                weatherInfo.setAirQuality(now.getCategory());
                weatherInfo.setAqi(now.getAqi());
                
                // 获取天气预报
                getWeatherForecastData(location, weatherInfo, callback);
            }
            
            @Override
            public void onError(String error) 
            {
                // 空气质量获取失败，继续获取天气预报
                Log.e(TAG, "获取空气质量失败: " + error);
                getWeatherForecastData(location, weatherInfo, callback);
            }
        });
    }
    
    /**
     * 获取天气预报数据
     */
    private void getWeatherForecastData(String location, final WeatherInfo weatherInfo, final WeatherDataCallback callback) 
    {
        weatherClient.getWeatherForecast(location, new QWeatherClient.WeatherCallback<WeatherForecastResponse>() 
        {
            @Override
            public void onSuccess(WeatherForecastResponse response) 
            {
                if (response.getDaily() != null && !response.getDaily().isEmpty()) 
                {
                    // 添加天气预报信息（这里只获取第一天的预报作为示例）
                    WeatherForecastResponse.DailyForecast forecast = response.getDaily().get(0);
                    weatherInfo.setMaxTemperature(forecast.getTempMax() + "°C");
                    weatherInfo.setMinTemperature(forecast.getTempMin() + "°C");
                }
                
                // 所有数据获取完成，回调成功
                callback.onSuccess(weatherInfo);
            }
            
            @Override
            public void onError(String error) 
            {
                Log.e(TAG, "获取天气预报失败: " + error);
                // 即使天气预报获取失败，也返回已获取的天气和空气质量数据
                callback.onSuccess(weatherInfo);
            }
        });
    }
    
    /**
     * 天气数据回调接口
     */
    public interface WeatherDataCallback 
    {
        void onSuccess(WeatherInfo weatherInfo);
        void onError(String error);
    }
} 