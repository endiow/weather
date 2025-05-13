package com.example.weather.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.weather.db.TodoDatabase;
import com.example.weather.model.Todo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 待办事项管理器
 * 负责管理所有待办事项相关操作
 */
public class TodoManager 
{
    private static final String TAG = "TodoManager";
    private static TodoManager instance;
    private final Context context;
    private final ExecutorService executor;
    private final Handler mainHandler;
    private final TodoDatabase database;
    
    /**
     * 获取TodoManager单例
     * @param context 上下文
     * @return TodoManager实例
     */
    public static synchronized TodoManager getInstance(Context context) 
    {
        if (instance == null) 
        {
            instance = new TodoManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * 私有构造函数
     * @param context 应用上下文
     */
    private TodoManager(Context context) 
    {
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.database = TodoDatabase.getInstance(context);
    }
    
    /**
     * 获取所有待办事项
     * 
     * @param callback 回调
     */
    public void getAllTodos(TodoCallback<List<Todo>> callback) 
    {
        executor.execute(() -> {
            try 
            {
                List<Todo> todoList = database.todoDao().getAll();
                returnOnMainThread(callback, todoList, null);
            } 
            catch (Exception e) 
            {
                Log.e(TAG, "获取所有待办事项出错", e);
                returnOnMainThread(callback, null, e.getMessage());
            }
        });
    }
    
    /**
     * 获取所有待办事项（同步方法，仅供内部使用）
     * 
     * @return 待办事项列表
     */
    public List<Todo> getAllTodos() 
    {
        try 
        {
            return database.todoDao().getAll();
        } 
        catch (Exception e) 
        {
            Log.e(TAG, "获取所有待办事项出错", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取未完成的待办事项
     * 
     * @param callback 回调
     */
    public void getIncompleteTodos(TodoCallback<List<Todo>> callback) 
    {
        executor.execute(() -> {
            try 
            {
                List<Todo> todoList = database.todoDao().getIncomplete();
                returnOnMainThread(callback, todoList, null);
            } 
            catch (Exception e) 
            {
                Log.e(TAG, "获取未完成待办事项出错", e);
                returnOnMainThread(callback, null, e.getMessage());
            }
        });
    }
    
    /**
     * 获取已完成的待办事项
     * 
     * @param callback 回调
     */
    public void getCompletedTodos(TodoCallback<List<Todo>> callback) 
    {
        executor.execute(() -> {
            try 
            {
                List<Todo> todoList = database.todoDao().getCompleted();
                returnOnMainThread(callback, todoList, null);
            } 
            catch (Exception e) 
            {
                Log.e(TAG, "获取已完成待办事项出错", e);
                returnOnMainThread(callback, null, e.getMessage());
            }
        });
    }
    
    /**
     * 根据ID获取待办事项
     * 
     * @param todoId 待办事项ID
     * @param callback 回调
     */
    public void getTodoById(long todoId, TodoCallback<Todo> callback) 
    {
        executor.execute(() -> {
            try 
            {
                Todo todo = database.todoDao().getById(todoId);
                returnOnMainThread(callback, todo, null);
            } 
            catch (Exception e) 
            {
                Log.e(TAG, "根据ID获取待办事项出错", e);
                returnOnMainThread(callback, null, e.getMessage());
            }
        });
    }
    
    /**
     * 添加新的待办事项
     * 
     * @param todo 待办事项
     * @param callback 回调
     */
    public void addTodo(Todo todo, TodoCallback<Long> callback) 
    {
        executor.execute(() -> {
            try 
            {
                long id = database.todoDao().insert(todo);
                returnOnMainThread(callback, id, null);
            } 
            catch (Exception e) 
            {
                Log.e(TAG, "添加待办事项出错", e);
                returnOnMainThread(callback, -1L, e.getMessage());
            }
        });
    }
    
    /**
     * 更新待办事项
     * 
     * @param todo 待办事项
     * @param callback 回调
     */
    public void updateTodo(Todo todo, TodoCallback<Boolean> callback) 
    {
        executor.execute(() -> {
            try 
            {
                int result = database.todoDao().update(todo);
                returnOnMainThread(callback, result > 0, null);
            } 
            catch (Exception e) 
            {
                Log.e(TAG, "更新待办事项出错", e);
                returnOnMainThread(callback, false, e.getMessage());
            }
        });
    }
    
    /**
     * 删除待办事项
     * 
     * @param todoId 待办事项ID
     * @param callback 回调
     */
    public void deleteTodo(long todoId, TodoCallback<Boolean> callback) 
    {
        executor.execute(() -> {
            try 
            {
                int result = database.todoDao().deleteById(todoId);
                returnOnMainThread(callback, result > 0, null);
            } 
            catch (Exception e) 
            {
                Log.e(TAG, "删除待办事项出错", e);
                returnOnMainThread(callback, false, e.getMessage());
            }
        });
    }
    
    /**
     * 根据天气类型获取待办事项
     * 
     * @param weatherType 天气类型
     * @param callback 回调
     */
    public void getTodosByWeatherType(String weatherType, TodoCallback<List<Todo>> callback) 
    {
        executor.execute(() -> {
            try 
            {
                List<Todo> todoList = database.todoDao().getByWeatherType(weatherType);
                returnOnMainThread(callback, todoList, null);
            } 
            catch (Exception e) 
            {
                Log.e(TAG, "根据天气类型获取待办事项出错", e);
                returnOnMainThread(callback, null, e.getMessage());
            }
        });
    }
    
    /**
     * 根据今天是星期几获取待办事项
     * 
     * @param callback 回调
     */
    public void getTodosForToday(TodoCallback<List<Todo>> callback) 
    {
        executor.execute(() -> {
            try 
            {
                // 获取今天是星期几
                Calendar calendar = Calendar.getInstance();
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Calendar.SUNDAY是1，我们的索引从0开始
                if (dayOfWeek < 0) dayOfWeek = 6; // 如果是-1，说明是周六
                
                final int todayIndex = dayOfWeek;
                
                // 获取所有未完成的待办事项
                List<Todo> allTodos = database.todoDao().getAllForDayOfWeekFiltering();
                List<Todo> todayTodos = new ArrayList<>();
                
                // 过滤出今天的待办事项
                for (Todo todo : allTodos) 
                {
                    boolean[] daysOfWeek = todo.getDaysOfWeek();
                    if (daysOfWeek != null && daysOfWeek.length > todayIndex && daysOfWeek[todayIndex]) 
                    {
                        todayTodos.add(todo);
                    }
                }
                
                returnOnMainThread(callback, todayTodos, null);
            } 
            catch (Exception e) 
            {
                Log.e(TAG, "获取今天的待办事项出错", e);
                returnOnMainThread(callback, null, e.getMessage());
            }
        });
    }
    
    /**
     * 私有方法：在主线程上返回结果
     */
    private <T> void returnOnMainThread(TodoCallback<T> callback, T result, String errorMsg) 
    {
        if (callback == null) return;
        
        mainHandler.post(() -> {
            if (errorMsg == null) 
            {
                callback.onSuccess(result);
            } 
            else 
            {
                callback.onError(errorMsg);
            }
        });
    }
    
    /**
     * 回调接口
     */
    public interface TodoCallback<T> 
    {
        void onSuccess(T result);
        void onError(String errorMsg);
    }
} 