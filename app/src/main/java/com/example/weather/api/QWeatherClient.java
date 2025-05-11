package com.example.weather.api;

import android.util.Log;

import com.example.weather.model.qweather.AirNowResponse;
import com.example.weather.model.qweather.WeatherForecastResponse;
import com.example.weather.model.qweather.WeatherNowResponse;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 和风天气API客户端
 */
public class QWeatherClient 
{
    private static final String TAG = "QWeatherClient";
    private static QWeatherClient instance;
    private final QWeatherService service;
    
    /**
     * 单例模式获取实例
     */
    public static QWeatherClient getInstance() 
    {
        if (instance == null) 
        {
            synchronized (QWeatherClient.class) 
            {
                if (instance == null) 
                {
                    instance = new QWeatherClient();
                }
            }
        }
        return instance;
    }
    
    /**
     * 私有构造函数
     */
    private QWeatherClient() 
    {
        // 添加HTTP日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        // 创建OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        
        // 创建Retrofit实例
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(QWeatherConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        // 创建API服务
        service = retrofit.create(QWeatherService.class);
    }
    
    /**
     * 获取实时天气
     * @param location 位置（经纬度，例如：116.41,39.92）
     * @param listener 回调监听器
     */
    public void getWeatherNow(String location, final WeatherCallback<WeatherNowResponse> listener) 
    {
        service.getWeatherNow(location, QWeatherConfig.API_KEY)
                .enqueue(new Callback<WeatherNowResponse>() 
                {
                    @Override
                    public void onResponse(Call<WeatherNowResponse> call, Response<WeatherNowResponse> response) 
                    {
                        if (response.isSuccessful() && response.body() != null) 
                        {
                            WeatherNowResponse weatherResponse = response.body();
                            if (QWeatherConfig.STATUS_OK.equals(weatherResponse.getCode())) 
                            {
                                listener.onSuccess(weatherResponse);
                            } 
                            else 
                            {
                                listener.onError("错误代码: " + weatherResponse.getCode());
                            }
                        } 
                        else 
                        {
                            listener.onError("响应失败: " + response.message());
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<WeatherNowResponse> call, Throwable t) 
                    {
                        Log.e(TAG, "获取实时天气失败", t);
                        listener.onError("请求失败: " + t.getMessage());
                    }
                });
    }
    
    /**
     * 获取空气质量
     * @param location 位置（经纬度，例如：116.41,39.92）
     * @param listener 回调监听器
     */
    public void getAirNow(String location, final WeatherCallback<AirNowResponse> listener) 
    {
        service.getAirNow(location, QWeatherConfig.API_KEY)
                .enqueue(new Callback<AirNowResponse>() 
                {
                    @Override
                    public void onResponse(Call<AirNowResponse> call, Response<AirNowResponse> response) 
                    {
                        if (response.isSuccessful() && response.body() != null) 
                        {
                            AirNowResponse airResponse = response.body();
                            if (QWeatherConfig.STATUS_OK.equals(airResponse.getCode())) 
                            {
                                listener.onSuccess(airResponse);
                            } 
                            else 
                            {
                                listener.onError("错误代码: " + airResponse.getCode());
                            }
                        } 
                        else 
                        {
                            listener.onError("响应失败: " + response.message());
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<AirNowResponse> call, Throwable t) 
                    {
                        Log.e(TAG, "获取空气质量失败", t);
                        listener.onError("请求失败: " + t.getMessage());
                    }
                });
    }
    
    /**
     * 获取天气预报
     * @param location 位置（经纬度，例如：116.41,39.92）
     * @param listener 回调监听器
     */
    public void getWeatherForecast(String location, final WeatherCallback<WeatherForecastResponse> listener) 
    {
        service.getWeatherForecast(location, QWeatherConfig.API_KEY)
                .enqueue(new Callback<WeatherForecastResponse>() 
                {
                    @Override
                    public void onResponse(Call<WeatherForecastResponse> call, Response<WeatherForecastResponse> response) 
                    {
                        if (response.isSuccessful() && response.body() != null) 
                        {
                            WeatherForecastResponse forecastResponse = response.body();
                            if (QWeatherConfig.STATUS_OK.equals(forecastResponse.getCode())) 
                            {
                                listener.onSuccess(forecastResponse);
                            } 
                            else 
                            {
                                listener.onError("错误代码: " + forecastResponse.getCode());
                            }
                        } 
                        else 
                        {
                            listener.onError("响应失败: " + response.message());
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<WeatherForecastResponse> call, Throwable t) 
                    {
                        Log.e(TAG, "获取天气预报失败", t);
                        listener.onError("请求失败: " + t.getMessage());
                    }
                });
    }
    
    /**
     * 天气API回调接口
     */
    public interface WeatherCallback<T> 
    {
        void onSuccess(T response);
        void onError(String error);
    }
} 