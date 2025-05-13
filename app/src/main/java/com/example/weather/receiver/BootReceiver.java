package com.example.weather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.weather.api.WeatherService;
import com.example.weather.api.WeatherCallback;
import com.example.weather.manager.TodoManager;
import com.example.weather.model.Todo;
import com.example.weather.util.AlarmScheduler;
import com.qweather.sdk.response.weather.WeatherHourlyResponse;
import com.qweather.sdk.response.weather.WeatherHourly;

import java.util.List;

/**
 * 启动接收器
 * 用于设备重启后重新注册提醒
 */
public class BootReceiver extends BroadcastReceiver 
{
    private static final String TAG = "BootReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) 
    {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) 
        {
            Log.d(TAG, "设备已重启，重新安排所有提醒");
            
            // 获取未完成的待办事项
            TodoManager.getInstance(context).getIncompleteTodos(new TodoManager.TodoCallback<List<Todo>>() 
            {
                @Override
                public void onSuccess(List<Todo> todoList) 
                {
                    if (todoList == null || todoList.isEmpty()) 
                    {
                        Log.d(TAG, "没有未完成的待办事项，无需重新安排提醒");
                        return;
                    }
                    
                    Log.d(TAG, "找到 " + todoList.size() + " 个未完成的待办事项");
                    
                    // 获取最新的天气预报
                    refreshWeatherForecast(context, todoList);
                }
                
                @Override
                public void onError(String errorMsg) 
                {
                    Log.e(TAG, "获取未完成的待办事项失败: " + errorMsg);
                }
            });
        }
    }
    
    /**
     * 刷新天气预报并重新安排提醒
     * 
     * @param context 上下文
     * @param todoList 待办事项列表
     */
    private void refreshWeatherForecast(Context context, List<Todo> todoList) 
    {
        // 这里使用固定的位置，实际应用中应该获取用户的实际位置
        double latitude = 39.9042; // 北京的纬度
        double longitude = 116.4074; // 北京的经度
        
        // 获取24小时天气预报
        WeatherService.getInstance(context).getWeatherHourlyForecast(latitude, longitude, new WeatherCallback<WeatherHourlyResponse>() 
        {
            @Override
            public void onSuccess(WeatherHourlyResponse response) 
            {
                if (response.getCode() == null || !response.getCode().equals("200")) 
                {
                    Log.e(TAG, "获取天气预报出错: " + response.getCode());
                    // 即使获取天气失败，也重新安排提醒
                    rescheduleReminders(context, todoList);
                    return;
                }
                
                Log.d(TAG, "获取24小时天气预报成功，重新安排提醒");
                
                // 重新安排提醒
                rescheduleReminders(context, todoList);
            }
            
            @Override
            public void onError(String errorMsg) 
            {
                Log.e(TAG, "获取天气预报失败: " + errorMsg);
                // 即使获取天气失败，也重新安排提醒
                rescheduleReminders(context, todoList);
            }
        });
    }
    
    /**
     * 重新安排所有提醒
     * 
     * @param context 上下文
     * @param todoList 待办事项列表
     */
    private void rescheduleReminders(Context context, List<Todo> todoList) 
    {
        // 创建闹钟调度器并安排提醒
        AlarmScheduler alarmScheduler = new AlarmScheduler(context);
        alarmScheduler.scheduleTodos(todoList);
        
        Log.d(TAG, "已重新安排 " + todoList.size() + " 个待办事项的提醒");
    }
} 