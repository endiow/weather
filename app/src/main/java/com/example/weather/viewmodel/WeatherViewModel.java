package com.example.weather.viewmodel;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weather.model.WeatherInfo;
import com.example.weather.repository.WeatherRepository;

/**
 * 天气ViewModel
 */
public class WeatherViewModel extends AndroidViewModel 
{
    private static final String TAG = "WeatherViewModel";
    private final WeatherRepository weatherRepository;
    private final MutableLiveData<WeatherInfo> weatherLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    public WeatherViewModel(@NonNull Application application) 
    {
        super(application);
        weatherRepository = WeatherRepository.getInstance(application);
    }
    
    /**
     * 获取天气数据
     */
    public LiveData<WeatherInfo> getWeatherData() 
    {
        return weatherLiveData;
    }
    
    /**
     * 获取加载状态
     */
    public LiveData<Boolean> getIsLoading() 
    {
        return isLoading;
    }
    
    /**
     * 获取错误信息
     */
    public LiveData<String> getErrorMessage() 
    {
        return errorMessage;
    }
    
    /**
     * 根据位置获取天气数据
     */
    public void loadWeatherData(Location location) 
    {
        if (location == null) 
        {
            errorMessage.setValue("位置信息为空");
            return;
        }
        
        isLoading.setValue(true);
        
        weatherRepository.getWeatherData(location, new WeatherRepository.WeatherDataCallback() 
        {
            @Override
            public void onSuccess(WeatherInfo weatherInfo) 
            {
                Log.d(TAG, "天气数据获取成功: " + weatherInfo.toString());
                weatherLiveData.postValue(weatherInfo);
                isLoading.postValue(false);
            }
            
            @Override
            public void onError(String error) 
            {
                Log.e(TAG, "天气数据获取失败: " + error);
                errorMessage.postValue(error);
                isLoading.postValue(false);
            }
        });
    }
    
    /**
     * 根据天气描述获取适合的天气类型
     * @param description 天气描述
     * @return 天气类型
     */
    public String mapWeatherTypeFromDescription(String description) 
    {
        if (description == null || description.isEmpty()) 
        {
            return "未知";
        }
        
        if (description.contains("晴")) 
        {
            return "晴天";
        } 
        else if (description.contains("多云") || description.contains("阴")) 
        {
            return "多云";
        } 
        else if (description.contains("雨")) 
        {
            if (description.contains("雷")) 
            {
                return "雷雨";
            }
            return "雨天";
        } 
        else if (description.contains("雪")) 
        {
            return "雪天";
        } 
        else if (description.contains("雾") || description.contains("霾") || description.contains("沙尘")) 
        {
            return "雾霾";
        } 
        else if (description.contains("风")) 
        {
            return "风";
        } 
        else 
        {
            return "其他";
        }
    }
} 