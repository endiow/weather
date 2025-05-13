package com.example.weather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.weather.api.WeatherService;
import com.example.weather.api.WeatherCallback;
import com.example.weather.manager.TodoManager;
import com.example.weather.model.Todo;
import com.example.weather.util.NotificationHelper;
import com.qweather.sdk.response.weather.WeatherHourlyResponse;
import com.qweather.sdk.response.weather.WeatherHourly;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        // 获取当前位置的天气预报
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
                    // 天气获取失败时也发送通知，以确保用户不会错过重要事项
                    sendNotification(todo, null);
                    return;
                }
                
                // 获取当前小时的预报
                WeatherHourly currentWeather = getCurrentHourWeather(response.getHourly());
                
                if (currentWeather == null) 
                {
                    Log.e(TAG, "找不到当前小时的天气预报");
                    // 找不到当前小时预报时也发送通知
                    sendNotification(todo, null);
                    return;
                }
                
                // 检查天气是否匹配
                boolean weatherMatches = checkWeatherMatches(todo, currentWeather);
                
                if (weatherMatches) 
                {
                    // 天气匹配，发送通知
                    sendNotification(todo, currentWeather);
                } 
                else 
                {
                    Log.d(TAG, "天气条件不匹配，不发送通知");
                }
            }
            
            @Override
            public void onError(String errorMsg) 
            {
                Log.e(TAG, "获取天气预报失败: " + errorMsg);
                // 获取天气失败时也发送通知
                sendNotification(todo, null);
            }
        });
    }
    
    /**
     * 获取当前小时的天气预报
     * 
     * @param hourlyList 小时级天气预报列表
     * @return 当前小时的天气预报
     */
    private WeatherHourly getCurrentHourWeather(List<WeatherHourly> hourlyList) 
    {
        if (hourlyList == null || hourlyList.isEmpty()) 
        {
            return null;
        }
        
        // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        
        // 日期格式化，用于比较日期
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = dateFormat.format(new Date());
        
        // 查找当前小时或最接近的未来小时的天气预报
        for (WeatherHourly hourly : hourlyList) 
        {
            try 
            {
                // 解析预报时间
                // 预报时间格式: 2023-05-08T15:00+08:00
                String fxTimeStr = hourly.getFxTime();
                
                // 提取日期和小时
                String[] parts = fxTimeStr.split("T");
                String date = parts[0];
                int hour = Integer.parseInt(parts[1].substring(0, 2));
                
                // 检查是否是今天的预报，且小时大于等于当前小时
                if (date.equals(today) && hour >= currentHour) 
                {
                    return hourly;
                }
            } 
            catch (Exception e) 
            {
                Log.e(TAG, "解析预报时间出错: " + e.getMessage());
            }
        }
        
        // 如果没有找到当前小时或未来小时的预报，返回第一个预报
        return hourlyList.get(0);
    }
    
    /**
     * 检查天气是否匹配待办事项的条件
     * 
     * @param todo 待办事项
     * @param weather 当前天气
     * @return 是否匹配
     */
    private boolean checkWeatherMatches(Todo todo, WeatherHourly weather) 
    {
        // 获取待办事项的天气类型列表
        List<String> todoWeatherTypes = todo.getWeatherTypes();
        
        // 如果没有设置天气类型，则匹配所有天气
        if (todoWeatherTypes == null || todoWeatherTypes.isEmpty()) 
        {
            return true;
        }
        
        // 获取当前天气状况
        String currentWeather = weather.getText();
        if (currentWeather == null) 
        {
            Log.e(TAG, "当前天气为null");
            return false;
        }
        
        Log.d(TAG, "当前天气: " + currentWeather + ", 待办事项天气类型: " + todoWeatherTypes);
        
        // 检查湿度是否匹配
        boolean humidityMatches = checkHumidityMatches(todo.getHumidity(), weather.getHumidity());
        
        // 检查天气状况是否匹配
        for (String weatherType : todoWeatherTypes) 
        {
            // 特殊情况处理
            if (weatherType.equals("晴天") && currentWeather.equals("晴")) 
            {
                return humidityMatches;
            }
            else if (weatherType.equals("多云") && (currentWeather.equals("多云") || currentWeather.contains("云"))) 
            {
                return humidityMatches;
            }
            else if (weatherType.equals("阴天") && (currentWeather.equals("阴") || currentWeather.contains("阴"))) 
            {
                return humidityMatches;
            }
            else if (weatherType.equals("下雨") && (currentWeather.contains("雨") || currentWeather.contains("雷"))) 
            {
                return humidityMatches;
            }
            // 常规匹配：检查当前天气是否包含设定的天气类型
            else if (currentWeather.contains(weatherType)) 
            {
                return humidityMatches;
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
     * @param weather 当前天气
     */
    private void sendNotification(Todo todo, WeatherHourly weather) 
    {
        String title = todo.getTitle();
        
        // 构建通知内容
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append(todo.getDescription());
        
        // 如果有天气信息，添加到通知内容
        if (weather != null) 
        {
            contentBuilder.append("\n当前天气: ").append(weather.getText());
            contentBuilder.append(", 温度: ").append(weather.getTemp()).append("°C");
            contentBuilder.append(", 湿度: ").append(weather.getHumidity()).append("%");
        }
        
        String content = contentBuilder.toString();
        
        // 使用NotificationHelper发送通知
        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.sendTodoNotification(todoId, title, content);
        
        Log.d(TAG, "已发送待办事项通知: " + title);
    }
} 