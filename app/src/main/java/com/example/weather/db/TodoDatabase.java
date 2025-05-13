package com.example.weather.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.weather.model.Todo;

/**
 * 待办事项数据库
 */
@Database(entities = {Todo.class}, version = 1, exportSchema = false)
public abstract class TodoDatabase extends RoomDatabase 
{
    private static final String DATABASE_NAME = "todo_db";
    private static TodoDatabase instance;

    /**
     * 获取TodoDao
     * @return TodoDao实例
     */
    public abstract TodoDao todoDao();

    /**
     * 获取数据库实例
     * @param context 上下文
     * @return TodoDatabase实例
     */
    public static synchronized TodoDatabase getInstance(Context context) 
    {
        if (instance == null) 
        {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    TodoDatabase.class,
                    DATABASE_NAME)
                    .fallbackToDestructiveMigration() // 当版本升级时，直接删除旧表，创建新表
                    .build();
        }
        return instance;
    }
} 