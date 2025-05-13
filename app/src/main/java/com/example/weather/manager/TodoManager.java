package com.example.weather.manager;

import android.content.Context;

import com.example.weather.model.Todo;

import java.util.ArrayList;
import java.util.List;

/**
 * 待办事项管理器
 * 负责管理所有待办事项相关操作
 */
public class TodoManager 
{
    private static TodoManager instance;
    private final Context context;
    private final List<Todo> todoList;

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
        this.todoList = new ArrayList<>();
        // 在实际应用中，这里应该从数据库加载待办事项
    }

    /**
     * 获取所有待办事项
     * @return 待办事项列表
     */
    public List<Todo> getAllTodos() 
    {
        // 实际应用中，这里应该从数据库查询所有待办事项
        // 现在仅返回内存中的列表
        return todoList;
    }

    /**
     * 添加新的待办事项
     * @param todo 待办事项
     * @return 是否添加成功
     */
    public boolean addTodo(Todo todo) 
    {
        // 实际应用中，这里应该将待办事项保存到数据库
        return todoList.add(todo);
    }

    /**
     * 更新待办事项
     * @param todo 待办事项
     * @return 是否更新成功
     */
    public boolean updateTodo(Todo todo) 
    {
        // 实际应用中，这里应该更新数据库中的待办事项
        for (int i = 0; i < todoList.size(); i++) 
        {
            if (todoList.get(i).getId() == todo.getId()) 
            {
                todoList.set(i, todo);
                return true;
            }
        }
        return false;
    }

    /**
     * 删除待办事项
     * @param todoId 待办事项ID
     * @return 是否删除成功
     */
    public boolean deleteTodo(long todoId) 
    {
        // 实际应用中，这里应该从数据库删除待办事项
        for (int i = 0; i < todoList.size(); i++) 
        {
            if (todoList.get(i).getId() == todoId) 
            {
                todoList.remove(i);
                return true;
            }
        }
        return false;
    }
} 