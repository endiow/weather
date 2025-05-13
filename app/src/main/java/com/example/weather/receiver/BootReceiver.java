package com.example.weather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.weather.api.WeatherService;
import com.example.weather.api.WeatherInfo;
import com.example.weather.api.WeatherCallback;
import com.example.weather.manager.TodoManager;
import com.example.weather.model.Todo;
import com.example.weather.util.AlarmScheduler;
import com.example.weather.util.LocationPreferences;

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
        if (intent != null && intent.getAction() != null) 
        {
            Log.d(TAG, "接收到行为: " + intent.getAction());
            
            // 系统启动完成
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
                intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON") ||
                intent.getAction().equals("com.htc.intent.action.QUICKBOOT_POWERON")) 
            {
                Log.d(TAG, "设备启动完成，重新注册提醒");
                
                // 获取所有带提醒的待办事项
                TodoManager.getInstance(context).getAllTodos(new TodoManager.TodoCallback<List<Todo>>() 
                {
                    @Override
                    public void onSuccess(List<Todo> todoList) 
                    {
                        if (todoList == null || todoList.isEmpty()) 
                        {
                            Log.d(TAG, "没有待办事项需要重新设置提醒");
                            return;
                        }
                        
                        Log.d(TAG, "找到 " + todoList.size() + " 个待办事项，重新获取天气信息并设置提醒");
                        
                        // 获取天气信息并重设提醒
                        refreshWeatherData(context, todoList);
                    }
                    
                    @Override
                    public void onError(String errorMsg) 
                    {
                        Log.e(TAG, "获取待办事项列表失败: " + errorMsg);
                    }
                });
            }
        }
    }
    
    /**
     * 刷新天气数据
     * 
     * @param context 上下文
     * @param todoList 待办事项列表
     */
    private void refreshWeatherData(Context context, List<Todo> todoList) 
    {
        // 获取保存的位置信息
        double[] location = LocationPreferences.getLocation(context);
        double latitude = location[0];
        double longitude = location[1];
        
        Log.d(TAG, "使用位置 - 纬度: " + latitude + ", 经度: " + longitude);
        
        // 改为获取实时天气预报
        WeatherService.getInstance(context).getWeatherNow(String.format("%.2f,%.2f", longitude, latitude), new WeatherCallback<WeatherInfo>() 
        {
            @Override
            public void onSuccess(WeatherInfo weatherInfo) 
            {
                if (weatherInfo.hasError()) 
                {
                    Log.e(TAG, "获取实时天气出错: " + weatherInfo.error);
                    // 即使获取天气失败，也重新安排提醒
                    rescheduleReminders(context, todoList);
                    return;
                }
                
                Log.d(TAG, "获取实时天气成功，重新安排提醒");
                
                // 重新安排提醒
                rescheduleReminders(context, todoList);
            }
            
            @Override
            public void onError(String errorMsg) 
            {
                Log.e(TAG, "获取实时天气失败: " + errorMsg);
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
        AlarmScheduler alarmScheduler = new AlarmScheduler(context);
        
        for (Todo todo : todoList) 
        {
            // 只为未完成且有提醒的待办事项设置提醒
            if (!todo.isCompleted() && todo.isRemindable() && todo.getStartTime() != null) 
            {
                alarmScheduler.scheduleTodo(todo);
                Log.d(TAG, "为待办事项 " + todo.getId() + " 重新设置提醒: " + todo.getTitle());
            }
        }
        
        Log.d(TAG, "所有待办事项的提醒已重新安排");
    }
} 