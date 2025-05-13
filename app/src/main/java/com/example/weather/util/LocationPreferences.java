package com.example.weather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * 位置信息存储工具类
 * 用于在SharedPreferences中存储和获取最后已知的位置
 */
public class LocationPreferences 
{
    private static final String TAG = "LocationPreferences";
    private static final String PREF_NAME = "location_preferences";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    
    // 默认位置 - 北京（作为备选方案）
    private static final double DEFAULT_LATITUDE = 39.9042;
    private static final double DEFAULT_LONGITUDE = 116.4074;
    
    /**
     * 保存位置信息
     * 
     * @param context 上下文
     * @param latitude 纬度
     * @param longitude 经度
     */
    public static void saveLocation(Context context, double latitude, double longitude) 
    {
        if (context == null) return;
        
        try 
        {
            SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            
            editor.putFloat(KEY_LATITUDE, (float) latitude);
            editor.putFloat(KEY_LONGITUDE, (float) longitude);
            
            boolean success = editor.commit();
            if (success) 
            {
                Log.d(TAG, "位置信息已保存 - 纬度: " + latitude + ", 经度: " + longitude);
            }
            else 
            {
                Log.e(TAG, "保存位置信息失败");
            }
        }
        catch (Exception e) 
        {
            Log.e(TAG, "保存位置信息时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 获取位置信息
     * 
     * @param context 上下文
     * @return 包含经纬度的数组 [纬度, 经度]
     */
    public static double[] getLocation(Context context) 
    {
        double[] location = new double[2];
        
        // 默认使用北京坐标
        location[0] = DEFAULT_LATITUDE;
        location[1] = DEFAULT_LONGITUDE;
        
        if (context == null) return location;
        
        try 
        {
            SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            
            float latitude = preferences.getFloat(KEY_LATITUDE, (float) DEFAULT_LATITUDE);
            float longitude = preferences.getFloat(KEY_LONGITUDE, (float) DEFAULT_LONGITUDE);
            
            // 检查是否有有效值（非0值）
            if (latitude != 0 && longitude != 0) 
            {
                location[0] = latitude;
                location[1] = longitude;
                Log.d(TAG, "已获取存储的位置 - 纬度: " + latitude + ", 经度: " + longitude);
            }
            else 
            {
                Log.d(TAG, "未找到有效位置信息，使用默认位置");
            }
        }
        catch (Exception e) 
        {
            Log.e(TAG, "获取位置信息时发生异常: " + e.getMessage());
        }
        
        return location;
    }
} 