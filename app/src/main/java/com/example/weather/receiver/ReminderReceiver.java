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
import com.example.weather.util.NotificationHelper;
import com.example.weather.util.WeatherTypeUtil;
import com.example.weather.util.WeatherTypeUtil.WeatherType;
import com.example.weather.util.LocationPreferences;

import java.util.List;

/**
 * 提醒接收器
 * 接收提醒广播并根据天气条件决定是否发送通知
 */
public class ReminderReceiver extends BroadcastReceiver 
{
    private static final String TAG = "ReminderReceiver";
    private Context context;
    private long todoId;
    
    @Override
    public void onReceive(Context context, Intent intent) 
    {
        this.context = context;
        
        // 检查 intent 是否为空
        if (intent == null) 
        {
            Log.e(TAG, "接收到空的 intent");
            return;
        }
        
        // 获取待办事项ID
        todoId = intent.getLongExtra("TODO_ID", -1);
        if (todoId <= 0) 
        {
            Log.e(TAG, "接收到无效的待办事项ID: " + todoId);
            return;
        }
        
        Log.d(TAG, "接收到待办事项 " + todoId + " 的提醒广播");
        
        // 获取待办事项信息
        TodoManager.getInstance(context).getTodoById(todoId, new TodoManager.TodoCallback<Todo>() 
        {
            @Override
            public void onSuccess(Todo todo) 
            {
                if (todo == null) 
                {
                    Log.e(TAG, "找不到ID为 " + todoId + " 的待办事项");
                    return;
                }
                
                // 如果待办事项已完成或不可提醒，则不发送通知
                if (todo.isCompleted() || !todo.isRemindable()) 
                {
                    Log.d(TAG, "待办事项已完成或不可提醒，不发送通知");
                    return;
                }
                
                // 检查天气条件
                checkWeatherConditions(todo);
            }
            
            @Override
            public void onError(String errorMsg) 
            {
                Log.e(TAG, "获取待办事项出错: " + errorMsg);
            }
        });
    }
    
    /**
     * 检查天气条件是否匹配
     * 
     * @param todo 待办事项
     */
    private void checkWeatherConditions(Todo todo) 
    {
        // 获取保存的位置信息
        double[] location = LocationPreferences.getLocation(context);
        double latitude = location[0];
        double longitude = location[1];
        
        Log.d(TAG, "使用位置 - 纬度: " + latitude + ", 经度: " + longitude);
        
        // 修改为获取实时天气
        WeatherService.getInstance(context).getWeatherNow(String.format("%.2f,%.2f", longitude, latitude), new WeatherCallback<WeatherInfo>() 
        {
            @Override
            public void onSuccess(WeatherInfo weatherInfo) 
            {
                if (weatherInfo.hasError()) 
                {
                    Log.e(TAG, "获取实时天气出错: " + weatherInfo.error);
                    // 天气获取失败时也发送通知，以确保用户不会错过重要事项
                    sendNotification(todo, null);
                    return;
                }
                
                // 检查天气是否匹配
                boolean weatherMatches = checkWeatherMatches(todo, weatherInfo);
                
                if (weatherMatches) 
                {
                    // 天气匹配，发送通知
                    sendNotification(todo, weatherInfo);
                } 
                else 
                {
                    Log.d(TAG, "天气条件不匹配，不发送通知");
                }
            }
            
            @Override
            public void onError(String errorMsg) 
            {
                Log.e(TAG, "获取实时天气失败: " + errorMsg);
                // 获取天气失败时也发送通知
                sendNotification(todo, null);
            }
        });
    }
    
    /**
     * 检查天气是否匹配待办事项的条件
     * 
     * @param todo 待办事项
     * @param weatherInfo 当前天气信息
     * @return 是否匹配
     */
    private boolean checkWeatherMatches(Todo todo, WeatherInfo weatherInfo) 
    {
        // 获取待办事项的天气类型列表
        List<String> todoWeatherTypes = todo.getWeatherTypes();
        
        // 如果没有设置天气类型，则匹配所有天气
        if (todoWeatherTypes == null || todoWeatherTypes.isEmpty()) 
        {
            return true;
        }
        
        // 获取当前天气状况
        String currentWeatherText = weatherInfo.text;
        if (currentWeatherText == null || currentWeatherText.isEmpty()) 
        {
            Log.e(TAG, "当前天气文本为空");
            return false;
        }
        
        // 使用WeatherTypeUtil获取天气类型
        WeatherType currentWeatherType = WeatherTypeUtil.getWeatherType(currentWeatherText);
        String currentWeatherTypeDesc = WeatherTypeUtil.getWeatherTypeDescription(currentWeatherType);
        
        Log.d(TAG, "当前天气: " + currentWeatherText + 
              ", 分类: " + currentWeatherTypeDesc + 
              ", 待办事项天气类型: " + todoWeatherTypes);
        
        // 检查湿度是否匹配
        boolean humidityMatches = checkHumidityMatches(todo.getHumidity(), weatherInfo.humidity);
        
        // 检查天气状况是否匹配
        for (String weatherType : todoWeatherTypes) 
        {
            // 将待办事项天气类型与当前天气类型进行比较
            switch (currentWeatherType) 
            {
                case SUNNY:
                    if (weatherType.equals("晴天") || weatherType.equals("晴")) 
                    {
                        return humidityMatches;
                    }
                    break;
                    
                case CLOUDY:
                    if (weatherType.equals("多云")) 
                    {
                        return humidityMatches;
                    }
                    break;
                    
                case OVERCAST:
                    if (weatherType.equals("阴天") || weatherType.equals("阴")) 
                    {
                        return humidityMatches;
                    }
                    break;
                    
                case RAINY:
                    if (weatherType.equals("下雨") || weatherType.equals("雨天") || 
                        weatherType.contains("雨")) 
                    {
                        return humidityMatches;
                    }
                    break;
                    
                case OTHER:
                    // 对于其他类型，直接检查文本是否包含关键词
                    if (currentWeatherText.contains(weatherType)) 
                    {
                        return humidityMatches;
                    }
                    break;
            }
        }
        
        return false;
    }
    
    /**
     * 检查湿度是否匹配
     * 
     * @param todoHumidity 待办事项的湿度条件
     * @param currentHumidity 当前湿度
     * @return 是否匹配
     */
    private boolean checkHumidityMatches(String todoHumidity, String currentHumidity) 
    {
        // 如果没有设置湿度条件，则匹配所有湿度
        if (todoHumidity == null || todoHumidity.isEmpty()) 
        {
            return true;
        }
        
        try 
        {
            // 解析当前湿度
            int humidity = Integer.parseInt(currentHumidity);
            
            // 解析待办事项的湿度条件
            // 格式: 30-70 (表示湿度在30%到70%之间)
            String[] parts = todoHumidity.split("-");
            int minHumidity = Integer.parseInt(parts[0]);
            int maxHumidity = Integer.parseInt(parts[1]);
            
            // 检查当前湿度是否在范围内
            return humidity >= minHumidity && humidity <= maxHumidity;
        } 
        catch (Exception e) 
        {
            Log.e(TAG, "解析湿度条件出错: " + e.getMessage());
            return true; // 解析出错时默认匹配
        }
    }
    
    /**
     * 发送通知
     * 
     * @param todo 待办事项
     * @param weatherInfo 当前天气信息
     */
    private void sendNotification(Todo todo, WeatherInfo weatherInfo) 
    {
        String title = todo.getTitle();
        
        // 构建通知内容
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append(todo.getDescription());
        
        // 如果有天气信息，添加到通知内容
        if (weatherInfo != null) 
        {
            contentBuilder.append("\n当前天气: ").append(weatherInfo.text);
            contentBuilder.append(", 温度: ").append(weatherInfo.temp).append("°C");
            contentBuilder.append(", 湿度: ").append(weatherInfo.humidity).append("%");
        }
        
        String content = contentBuilder.toString();
        
        // 使用NotificationHelper发送通知
        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.sendTodoNotification(todoId, title, content);
        
        Log.d(TAG, "已发送待办事项通知: " + title);
    }
} 