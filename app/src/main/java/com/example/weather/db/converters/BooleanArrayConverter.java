package com.example.weather.db.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

/**
 * 布尔数组类型转换器
 * 用于Room数据库将boolean[]类型与String类型互相转换
 */
public class BooleanArrayConverter 
{
    private static final Gson gson = new Gson();

    @TypeConverter
    public static boolean[] fromString(String value) 
    {
        if (value == null) 
        {
            return new boolean[7]; // 默认7天
        }
        
        return gson.fromJson(value, boolean[].class);
    }

    @TypeConverter
    public static String fromArray(boolean[] array) 
    {
        if (array == null) 
        {
            return null;
        }
        
        return gson.toJson(array);
    }
} 