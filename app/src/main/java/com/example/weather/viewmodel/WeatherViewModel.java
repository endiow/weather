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
    private final MutableLiveData<WeatherInfo> weatherInfoLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    
    public WeatherViewModel(@NonNull Application application) 
    {
        super(application);
        weatherRepository = WeatherRepository.getInstance();
    }
    
    /**
     * 获取天气信息LiveData
     */
    public LiveData<WeatherInfo> getWeatherInfoLiveData() 
    {
        return weatherInfoLiveData;
    }
    
    /**
     * 获取错误消息LiveData
     */
    public LiveData<String> getErrorMessageLiveData() 
    {
        return errorMessageLiveData;
    }
    
    /**
     * 获取加载状态LiveData
     */
    public LiveData<Boolean> getIsLoadingLiveData() 
    {
        return isLoadingLiveData;
    }
    
    /**
     * 加载天气数据
     * @param location 位置对象
     */
    public void loadWeatherData(Location location) 
    {
        if (location == null) 
        {
            errorMessageLiveData.setValue("位置信息为空");
            return;
        }
        
        isLoadingLiveData.setValue(true);
        Log.d(TAG, "开始加载天气数据: " + location.getLatitude() + ", " + location.getLongitude());
        
        weatherRepository.getWeatherData(location, new WeatherRepository.WeatherDataCallback() 
        {
            @Override
            public void onSuccess(WeatherInfo weatherInfo) 
            {
                Log.d(TAG, "天气数据加载成功");
                isLoadingLiveData.postValue(false);
                weatherInfoLiveData.postValue(weatherInfo);
            }
            
            @Override
            public void onError(String error) 
            {
                Log.e(TAG, "天气数据加载失败: " + error);
                isLoadingLiveData.postValue(false);
                errorMessageLiveData.postValue("加载天气数据失败: " + error);
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