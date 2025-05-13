package com.example.weather.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.weather.MainActivity;
import com.example.weather.R;

/**
 * 通知助手类
 * 负责创建和发送通知
 */
public class NotificationHelper 
{
    private static final String TAG = "NotificationHelper";
    private static final String CHANNEL_ID = "weather_todo_channel";
    private static final String CHANNEL_NAME = "天气待办事项通知";
    private static final String CHANNEL_DESCRIPTION = "显示与天气相关的待办事项提醒";
    
    private final Context context;
    
    /**
     * 构造函数
     * 
     * @param context 上下文
     */
    public NotificationHelper(Context context) 
    {
        this.context = context;
        createNotificationChannel();
    }
    
    /**
     * 创建通知渠道
     * 仅在Android 8.0（API级别26）及更高版本上需要
     */
    private void createNotificationChannel() 
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) 
        {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            
            // 注册通知渠道
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) 
            {
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "已创建通知渠道: " + CHANNEL_ID);
            }
        }
    }
    
    /**
     * 发送待办事项通知
     * 
     * @param todoId 待办事项ID
     * @param title 通知标题
     * @param content 通知内容
     */
    public void sendTodoNotification(long todoId, String title, String content) 
    {
        // 创建点击通知时打开的Intent
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("TODO_ID", todoId);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                (int) todoId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );
        
        // 构建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_rain) // 使用现有的rain图标
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        
        // 发送通知
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try 
        {
            notificationManager.notify((int) todoId, builder.build());
            Log.d(TAG, "已发送通知: " + title);
        } 
        catch (SecurityException e) 
        {
            // 处理缺少权限的情况
            Log.e(TAG, "发送通知失败，缺少权限: " + e.getMessage());
        }
    }
} 