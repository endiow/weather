package com.example.weather.util;

import com.example.weather.model.Todo;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

/**
 * 待办事项排序器
 * 按照以下优先级排序：
 * 1. 未完成的排在前面
 * 2. 匹配当前天气类型的排在前面
 * 3. 匹配当天星期几的排在前面
 */
public class TodoComparator implements Comparator<Todo> 
{
    private final String currentWeatherType;
    private final int currentDayOfWeek;
    
    /**
     * 构造函数
     * @param currentWeatherType 当前天气类型
     */
    public TodoComparator(String currentWeatherType) 
    {
        this.currentWeatherType = currentWeatherType;
        
        // 获取今天是星期几（0表示周一，1表示周二，以此类推）
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Calendar.SUNDAY是1
        if (dayOfWeek == 0) {
            dayOfWeek = 6; // 我们的数组使用0-6表示周一到周日，所以要转换
        } else {
            dayOfWeek--; // 调整为0-6
        }
        this.currentDayOfWeek = dayOfWeek;
    }
    
    @Override
    public int compare(Todo todo1, Todo todo2) 
    {
        // 1. 首先按照完成状态排序
        if (todo1.isCompleted() != todo2.isCompleted()) 
        {
            return todo1.isCompleted() ? 1 : -1; // 未完成的排在前面
        }
        
        // 对于已完成的项目，直接按完成时间倒序
        if (todo1.isCompleted() && todo2.isCompleted()) 
        {
            // 使用updatedAt（完成时间）进行比较，新完成的排前面
            Date date1 = todo1.getUpdatedAt();
            Date date2 = todo2.getUpdatedAt();
            if (date1 != null && date2 != null) 
            {
                return date2.compareTo(date1);
            }
            return 0;
        }
        
        // 2. 然后按照天气类型匹配排序
        boolean weather1Matches = matchesWeather(todo1);
        boolean weather2Matches = matchesWeather(todo2);
        if (weather1Matches != weather2Matches) 
        {
            return weather1Matches ? -1 : 1; // 匹配的排在前面
        }
        
        // 3. 然后按照星期几匹配排序
        boolean day1Matches = matchesDay(todo1);
        boolean day2Matches = matchesDay(todo2);
        if (day1Matches != day2Matches) 
        {
            return day1Matches ? -1 : 1; // 匹配的排在前面
        }
        
        // 4. 最后按照创建时间排序
        Date created1 = todo1.getCreatedAt();
        Date created2 = todo2.getCreatedAt();
        if (created1 != null && created2 != null) 
        {
            return created2.compareTo(created1); // 新创建的排在前面
        }
        
        return 0;
    }
    
    /**
     * 检查待办事项是否匹配当前天气类型
     */
    private boolean matchesWeather(Todo todo) 
    {
        if (todo.getWeatherTypes() == null || todo.getWeatherTypes().isEmpty() || currentWeatherType == null) 
        {
            return false;
        }
        
        // 判断当前天气是否在待办事项的天气类型列表中
        return todo.getWeatherTypes().stream().anyMatch(weatherType -> 
                currentWeatherType.contains(weatherType) || weatherType.contains(currentWeatherType));
    }
    
    /**
     * 检查待办事项是否匹配当前星期几
     */
    private boolean matchesDay(Todo todo) 
    {
        boolean[] daysOfWeek = todo.getDaysOfWeek();
        if (daysOfWeek == null || daysOfWeek.length <= currentDayOfWeek) 
        {
            return false;
        }
        
        return daysOfWeek[currentDayOfWeek];
    }
} 