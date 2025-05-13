package com.example.weather.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.weather.model.Todo;
import com.example.weather.receiver.ReminderReceiver;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 闹钟调度器
 * 负责为待办事项安排提醒
 */
public class AlarmScheduler 
{
    private static final String TAG = "AlarmScheduler";
    private final Context context;
    private final AlarmManager alarmManager;
    
    /**
     * 构造函数
     * 
     * @param context 上下文
     */
    public AlarmScheduler(Context context) 
    {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }
    
    /**
     * 为待办事项列表安排提醒
     * 仅为当天的待办事项安排提醒
     * 
     * @param todoList 待办事项列表
     */
    public void scheduleTodos(List<Todo> todoList) 
    {
        if (todoList == null || todoList.isEmpty()) 
        {
            Log.d(TAG, "没有待办事项需要安排提醒");
            return;
        }
        
        // 获取今天的日期
        Calendar todayCalendar = Calendar.getInstance();
        
        for (Todo todo : todoList) 
        {
            // 如果待办事项不可提醒或已完成，则跳过
            if (!todo.isRemindable() || todo.isCompleted()) 
            {
                continue;
            }
            
            // 获取开始时间
            Date startTime = todo.getStartTime();
            if (startTime == null) 
            {
                continue;
            }
            
            // 检查是否是今天的待办事项
            Calendar todoCalendar = Calendar.getInstance();
            todoCalendar.setTime(startTime);
            
            boolean isSameDay = todayCalendar.get(Calendar.YEAR) == todoCalendar.get(Calendar.YEAR) &&
                                todayCalendar.get(Calendar.DAY_OF_YEAR) == todoCalendar.get(Calendar.DAY_OF_YEAR);
            
            // 只为当天的待办事项安排提醒
            if (isSameDay) 
            {
                // 如果开始时间已经过了，则跳过
                if (startTime.before(new Date())) 
                {
                    Log.d(TAG, "待办事项 " + todo.getTitle() + " 的开始时间已过，跳过提醒");
                    continue;
                }
                
                // 安排提醒
                scheduleTodo(todo);
            }
        }
    }
    
    /**
     * 为单个待办事项安排提醒
     * 
     * @param todo 待办事项
     */
    public void scheduleTodo(Todo todo) 
    {
        if (todo == null) 
        {
            Log.e(TAG, "待办事项为null，无法安排提醒");
            return;
        }
        
        if (!todo.isRemindable() || todo.isCompleted()) 
        {
            Log.d(TAG, "待办事项不可提醒或已完成，跳过提醒: ID=" + todo.getId());
            return;
        }
        
        // 确保ID大于0
        if (todo.getId() <= 0) 
        {
            Log.e(TAG, "待办事项ID无效，无法安排提醒: " + todo.getId());
            return;
        }
        
        Date startTime = todo.getStartTime();
        if (startTime == null) 
        {
            Log.e(TAG, "待办事项没有开始时间，无法安排提醒: ID=" + todo.getId());
            return;
        }
        
        // 如果开始时间已经过了，则跳过
        if (startTime.before(new Date())) 
        {
            Log.d(TAG, "待办事项 " + todo.getTitle() + " 的开始时间已过，跳过提醒: ID=" + todo.getId());
            return;
        }
        
        try 
        {
            // 创建提醒Intent
            Intent intent = new Intent(context, ReminderReceiver.class);
            intent.putExtra("TODO_ID", todo.getId());
            
            // 创建PendingIntent，确保每个待办事项有唯一的PendingIntent
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    (int) todo.getId(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
            );
            
            // 设置精确闹钟
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) 
            {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startTime.getTime(), pendingIntent);
            } 
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) 
            {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, startTime.getTime(), pendingIntent);
            } 
            else 
            {
                alarmManager.set(AlarmManager.RTC_WAKEUP, startTime.getTime(), pendingIntent);
            }
            
            Log.d(TAG, "已为待办事项 " + todo.getTitle() + " 安排提醒，ID=" + todo.getId() + "，时间：" + startTime);
        }
        catch (Exception e)
        {
            Log.e(TAG, "安排提醒时发生异常: ID=" + todo.getId(), e);
        }
    }
    
    /**
     * 取消待办事项的提醒
     * 
     * @param todoId 待办事项ID
     */
    public void cancelTodo(long todoId) 
    {
        Intent intent = new Intent(context, ReminderReceiver.class);
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) todoId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );
        
        alarmManager.cancel(pendingIntent);
        Log.d(TAG, "已取消待办事项 " + todoId + " 的提醒");
    }
    
    /**
     * 取消所有待办事项的提醒
     * 
     * @param todoList 待办事项列表
     */
    public void cancelAll(List<Todo> todoList) 
    {
        if (todoList == null || todoList.isEmpty()) 
        {
            return;
        }
        
        for (Todo todo : todoList) 
        {
            cancelTodo(todo.getId());
        }
        
        Log.d(TAG, "已取消所有待办事项的提醒");
    }
} 