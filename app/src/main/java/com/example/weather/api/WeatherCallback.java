package com.example.weather.api;

/**
 * 天气回调接口
 */
public interface WeatherCallback<T> 
{
    /**
     * 成功回调
     * 
     * @param response 响应数据
     */
    void onSuccess(T response);
    
    /**
     * 错误回调
     * 
     * @param message 错误信息
     */
    void onError(String message);
} 