package com.example.weather;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

public class AddTodoActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener
{
    private TextInputEditText etTodoTitle;
    private TextInputEditText etTodoDescription;
    private MaterialButton btnStartTime;
    private MaterialButton btnEndTime;
    private RecyclerView rvWeatherTypes;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        // 初始化视图
        initViews();
        
        // 设置导航栏
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        // 设置底部导航栏选中项
        bottomNavigationView.setSelectedItemId(R.id.nav_add_todo);
    }
    
    private void initViews() 
    {
        toolbar = findViewById(R.id.toolbar);
        etTodoTitle = findViewById(R.id.etTodoTitle);
        etTodoDescription = findViewById(R.id.etTodoDescription);
        btnStartTime = findViewById(R.id.btnStartTime);
        btnEndTime = findViewById(R.id.btnEndTime);
        rvWeatherTypes = findViewById(R.id.rvWeatherTypes);
        
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(this);
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
            // 已经在添加事项界面，无需操作
            return true;
        } 
        else if (itemId == R.id.nav_todo_list) 
        {
            // 跳转到清单界面
            Intent intent = new Intent(this, TodoListActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean onSupportNavigateUp() 
    {
        onBackPressed();
        return true;
    }
} 