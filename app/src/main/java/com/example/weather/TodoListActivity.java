package com.example.weather;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.adapter.TodoAdapter;
import com.example.weather.manager.TodoManager;
import com.example.weather.model.Todo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class TodoListActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener
{
    private RecyclerView rvTodoList;
    private TodoAdapter todoAdapter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        // 初始化视图
        initViews();
        
        // 加载待办事项列表
        loadTodoList();
        
        // 设置底部导航栏选中项
        bottomNavigationView.setSelectedItemId(R.id.nav_todo_list);
    }
    
    private void initViews() 
    {
        rvTodoList = findViewById(R.id.rvTodoList);
        rvTodoList.setLayoutManager(new LinearLayoutManager(this));
        
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(this);
    }
    
    private void loadTodoList() 
    {
        // 获取待办事项列表
        List<Todo> todoList = TodoManager.getInstance(this).getAllTodos();
        
        // 初始化适配器
        todoAdapter = new TodoAdapter(this, todoList);
        rvTodoList.setAdapter(todoAdapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) 
    {
        int itemId = item.getItemId();
        
        if (itemId == R.id.nav_weather) 
        {
            // 跳转到天气界面
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        } 
        else if (itemId == R.id.nav_add_todo) 
        {
            // The AddTodoActivity class should be implemented and ready to use
            Intent intent = new Intent(this, AddTodoActivity.class);
            startActivity(intent);
            finish();
            return true;
        } 
        else if (itemId == R.id.nav_todo_list) 
        {
            // 已经在清单界面，无需操作
            return true;
        }
        
        return false;
    }
} 