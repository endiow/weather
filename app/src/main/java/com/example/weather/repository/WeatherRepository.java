package com.example.weather.repository;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.weather.api.WeatherService;
import com.example.weather.model.WeatherInfo;
import com.qweather.sdk.response.air.AirNowResponse;
import com.qweather.sdk.response.weather.WeatherDailyResponse;
import com.qweather.sdk.response.weather.WeatherNowResponse;

/**
 * 天气数据仓库
 */
public class WeatherRepository 
{
    private static final String TAG = "WeatherRepository";
    private static WeatherRepository instance;
    private final WeatherService weatherService;
    private Context context;
    
    /**
     * 单例模式获取实例
     */
    public static WeatherRepository getInstance(Context context) 
    {
        if (instance == null) 
        {
            synchronized (WeatherRepository.class) 
            {
                if (instance == null) 
                {
                    instance = new WeatherRepository(context);
                }
            }
        }
        return instance;
    }
    
    /**
     * 私有构造函数
     */
    private WeatherRepository(Context context) 
    {
        this.context = context.getApplicationContext();
        weatherService = WeatherService.getInstance(this.context);
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
        weatherService.getWeatherNow(locationString, new WeatherService.WeatherCallback<WeatherNowResponse>() 
        {
            @Override
            public void onSuccess(WeatherNowResponse response) 
            {
                // 填充天气信息
                weatherInfo.setTemperature(response.getNow().getTemp() + "°C");
                weatherInfo.setWeatherDescription(response.getNow().getText());
                weatherInfo.setHumidity(response.getNow().getHumidity() + "%");
                weatherInfo.setWindDirection(response.getNow().getWindDir());
                weatherInfo.setWindPower(response.getNow().getWindScale() + "级");
                
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
        weatherService.getAirNow(location, new WeatherService.WeatherCallback<AirNowResponse>() 
        {
            @Override
            public void onSuccess(AirNowResponse response) 
            {
                // 填充空气质量信息
                weatherInfo.setAirQuality(response.getNow().getCategory());
                weatherInfo.setAqi(response.getNow().getAqi());
                
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
        weatherService.getWeather3d(location, new WeatherService.WeatherCallback<WeatherDailyResponse>() 
        {
            @Override
            public void onSuccess(WeatherDailyResponse response) 
            {
                if (response.getDaily() != null && !response.getDaily().isEmpty()) 
                {
                    // 添加天气预报信息（这里只获取第一天的预报作为示例）
                    weatherInfo.setMaxTemperature(response.getDaily().get(0).getTempMax() + "°C");
                    weatherInfo.setMinTemperature(response.getDaily().get(0).getTempMin() + "°C");
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