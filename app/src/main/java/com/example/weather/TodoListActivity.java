package com.example.weather;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.adapter.TodoAdapter;
import com.example.weather.manager.TodoManager;
import com.example.weather.model.Todo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class TodoListActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener, TodoAdapter.ItemClickListener
{
    private RecyclerView rvTodoList;
    private TodoAdapter todoAdapter;
    private BottomNavigationView bottomNavigationView;
    private TodoManager todoManager;
    private ProgressBar progressBar;
    private TextView tvEmpty;

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
        
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(this);
        
        // 初始化待办事项管理器
        todoManager = TodoManager.getInstance(this);
        
        // 初始化适配器
        todoAdapter = new TodoAdapter(this, new ArrayList<>());
        todoAdapter.setItemClickListener(this);
        rvTodoList.setAdapter(todoAdapter);
    }
    
    private void loadTodoList() 
    {
        // 显示加载中
        progressBar.setVisibility(View.VISIBLE);
        rvTodoList.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        
        // 获取待办事项列表
        todoManager.getAllTodos(new TodoManager.TodoCallback<List<Todo>>() 
        {
            @Override
            public void onSuccess(List<Todo> result) 
            {
                progressBar.setVisibility(View.GONE);
                
                if (result != null && !result.isEmpty()) 
                {
                    todoAdapter.updateData(result);
                    rvTodoList.setVisibility(View.VISIBLE);
                    tvEmpty.setVisibility(View.GONE);
                } 
                else 
                {
                    rvTodoList.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String errorMsg) 
            {
                progressBar.setVisibility(View.GONE);
                rvTodoList.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("加载失败: " + errorMsg);
                Toast.makeText(TodoListActivity.this, "加载待办事项失败: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
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
            // 跳转到添加事项界面
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

    @Override
    public void onItemClick(Todo todo, int position) 
    {
        // 跳转到编辑待办事项界面
        Intent intent = new Intent(this, AddTodoActivity.class);
        intent.putExtra("todo_id", todo.getId());
        startActivity(intent);
    }
    
    @Override
    protected void onResume() 
    {
        super.onResume();
        // 每次恢复活动时刷新列表
        loadTodoList();
    }
} 