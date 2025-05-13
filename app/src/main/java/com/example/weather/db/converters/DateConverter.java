package com.example.weather.db.converters;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * Date类型转换器
 * 用于Room数据库将Date类型与Long类型互相转换
 */
public class DateConverter 
{
    @TypeConverter
    public static Date fromTimestamp(Long value) 
    {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) 
    {
        return date == null ? null : date.getTime();
    }
} 