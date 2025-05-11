package com.example.weather.api;

import com.example.weather.model.qweather.AirNowResponse;
import com.example.weather.model.qweather.WeatherForecastResponse;
import com.example.weather.model.qweather.WeatherNowResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 和风天气API接口
 */
public interface QWeatherService 
{
    /**
     * 获取实时天气
     * @param location 位置（经纬度，例如：116.41,39.92）
     * @param key API密钥
     * @return 实时天气响应
     */
    @GET(QWeatherConfig.WEATHER_NOW)
    Call<WeatherNowResponse> getWeatherNow(
            @Query(QWeatherConfig.PARAM_LOCATION) String location,
            @Query(QWeatherConfig.PARAM_KEY) String key
    );

    /**
     * 获取空气质量
     * @param location 位置（经纬度，例如：116.41,39.92）
     * @param key API密钥
     * @return 空气质量响应
     */
    @GET(QWeatherConfig.AIR_NOW)
    Call<AirNowResponse> getAirNow(
            @Query(QWeatherConfig.PARAM_LOCATION) String location,
            @Query(QWeatherConfig.PARAM_KEY) String key
    );

    /**
     * 获取三日天气预报
     * @param location 位置（经纬度，例如：116.41,39.92）
     * @param key API密钥
     * @return 天气预报响应
     */
    @GET(QWeatherConfig.WEATHER_FORECAST)
    Call<WeatherForecastResponse> getWeatherForecast(
            @Query(QWeatherConfig.PARAM_LOCATION) String location,
            @Query(QWeatherConfig.PARAM_KEY) String key
    );
} 