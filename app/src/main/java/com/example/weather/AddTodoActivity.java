package com.example.weather;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weather.manager.TodoManager;
import com.example.weather.model.Todo;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddTodoActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener
{
    private TextInputEditText etTodoTitle;
    private TextInputEditText etTodoDescription;
    private MaterialButton btnStartTime;
    private MaterialButton btnEndTime;
    private MaterialButton btnAddEvent;
    private BottomNavigationView bottomNavigationView;
    private SwitchMaterial switchRemindable;
    private TextView tvRemindableStatus;
    
    // 天气类型CheckBox
    private CheckBox cbSunny, cbCloudy, cbOvercast, cbRainy, cbOther;
    
    // 星期几CheckBox
    private CheckBox cbMonday, cbTuesday, cbWednesday, cbThursday, cbFriday, cbSaturday, cbSunday;
    
    // 时间选择器变量
    private Calendar startTimeCalendar = Calendar.getInstance();
    private Calendar endTimeCalendar = Calendar.getInstance();
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        // 初始化视图
        initViews();
        
        // 设置事件监听
        setupListeners();
        
        // 设置底部导航栏选中项
        bottomNavigationView.setSelectedItemId(R.id.nav_add_todo);
    }
    
    private void initViews() 
    {
        etTodoTitle = findViewById(R.id.etTodoTitle);
        etTodoDescription = findViewById(R.id.etTodoDescription);
        btnStartTime = findViewById(R.id.btnStartTime);
        btnEndTime = findViewById(R.id.btnEndTime);
        btnAddEvent = findViewById(R.id.btnAddEvent);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(this);
        
        // 初始化天气类型选择框
        cbSunny = findViewById(R.id.cbSunny);
        cbCloudy = findViewById(R.id.cbCloudy);
        cbOvercast = findViewById(R.id.cbOvercast);
        cbRainy = findViewById(R.id.cbRainy);
        cbOther = findViewById(R.id.cbOther);
        
        // 初始化星期几选择框
        cbMonday = findViewById(R.id.cbMonday);
        cbTuesday = findViewById(R.id.cbTuesday);
        cbWednesday = findViewById(R.id.cbWednesday);
        cbThursday = findViewById(R.id.cbThursday);
        cbFriday = findViewById(R.id.cbFriday);
        cbSaturday = findViewById(R.id.cbSaturday);
        cbSunday = findViewById(R.id.cbSunday);
        
        // 初始化提醒开关相关视图
        switchRemindable = findViewById(R.id.switchRemindable);
        tvRemindableStatus = findViewById(R.id.tvRemindableStatus);
    }
    
    private void setupListeners() 
    {
        // 设置开始时间选择器
        btnStartTime.setOnClickListener(v -> showTimePickerDialog(true));
        
        // 设置结束时间选择器
        btnEndTime.setOnClickListener(v -> showTimePickerDialog(false));
        
        // 提醒开关监听器
        switchRemindable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tvRemindableStatus.setText(isChecked ? "是" : "否");
        });
        
        // 添加事项按钮监听器
        btnAddEvent.setOnClickListener(v -> saveAndAddNew());
    }
    
    /**
     * 显示时间选择对话框
     */
    private void showTimePickerDialog(boolean isStartTime) 
    {
        Calendar calendar = isStartTime ? startTimeCalendar : endTimeCalendar;
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    
                    // 更新按钮文本
                    String timeText = timeFormat.format(calendar.getTime());
                    if (isStartTime) 
                    {
                        btnStartTime.setText("开始时间: " + timeText);
                    } 
                    else 
                    {
                        btnEndTime.setText("结束时间: " + timeText);
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }
    
    /**
     * 保存当前待办事项并准备添加新的事项
     */
    private void saveAndAddNew() 
    {
        // 获取标题和描述
        String title = etTodoTitle.getText() != null ? etTodoTitle.getText().toString().trim() : "";
        String description = etTodoDescription.getText() != null ? etTodoDescription.getText().toString().trim() : "";
        
        // 验证必要信息
        if (title.isEmpty()) 
        {
            Toast.makeText(this, "请输入待办事项标题", Toast.LENGTH_SHORT).show();
            etTodoTitle.requestFocus();
            return;
        }
        
        // 验证是否选择了至少一个天气类型
        boolean hasWeatherType = cbSunny.isChecked() || cbCloudy.isChecked() || 
                                 cbOvercast.isChecked() || cbRainy.isChecked() || 
                                 cbOther.isChecked();
        if (!hasWeatherType)
        {
            Toast.makeText(this, "请至少选择一种天气类型", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 验证是否选择了至少一天
        boolean hasDay = cbMonday.isChecked() || cbTuesday.isChecked() || 
                         cbWednesday.isChecked() || cbThursday.isChecked() || 
                         cbFriday.isChecked() || cbSaturday.isChecked() || 
                         cbSunday.isChecked();
        if (!hasDay)
        {
            Toast.makeText(this, "请至少选择一天", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 禁用按钮，防止重复提交
        btnAddEvent.setEnabled(false);
        btnAddEvent.setText("保存中...");
        
        // 创建Todo对象
        Todo todo = new Todo(title, description);
        
        // 设置时间
        Date startTime = startTimeCalendar.getTime();
        Date endTime = endTimeCalendar.getTime();
        todo.setStartTime(startTime);
        todo.setEndTime(endTime);
        
        // 设置天气类型
        List<String> weatherTypes = new ArrayList<>();
        if (cbSunny.isChecked()) weatherTypes.add("晴天");
        if (cbCloudy.isChecked()) weatherTypes.add("多云");
        if (cbOvercast.isChecked()) weatherTypes.add("阴天");
        if (cbRainy.isChecked()) weatherTypes.add("下雨");
        if (cbOther.isChecked()) weatherTypes.add("其他");
        todo.setWeatherTypes(weatherTypes);
        
        // 设置星期几
        boolean[] daysOfWeek = new boolean[7]; // 周一到周日对应索引0-6
        daysOfWeek[0] = cbMonday.isChecked();
        daysOfWeek[1] = cbTuesday.isChecked();
        daysOfWeek[2] = cbWednesday.isChecked();
        daysOfWeek[3] = cbThursday.isChecked();
        daysOfWeek[4] = cbFriday.isChecked();
        daysOfWeek[5] = cbSaturday.isChecked();
        daysOfWeek[6] = cbSunday.isChecked();
        todo.setDaysOfWeek(daysOfWeek);
        
        // 设置空气质量和湿度为null（由于字段存在于数据库中）
        todo.setAirQuality(null);
        todo.setHumidity(null);
        
        // 设置提醒状态
        todo.setRemindable(switchRemindable.isChecked());
        
        // 保存到数据库
        TodoManager.getInstance(this).addTodo(todo, new TodoManager.TodoCallback<Long>() 
        {
            @Override
            public void onSuccess(Long result) 
            {
                runOnUiThread(() -> {
                    Toast.makeText(AddTodoActivity.this, "待办事项添加成功", Toast.LENGTH_SHORT).show();
                    
                    // 清空表单，准备添加新事项
                    etTodoTitle.setText("");
                    etTodoDescription.setText("");
                    
                    // 重置时间选择器
                    startTimeCalendar = Calendar.getInstance();
                    endTimeCalendar = Calendar.getInstance();
                    btnStartTime.setText("开始时间");
                    btnEndTime.setText("结束时间");
                    
                    // 重置天气类型选择
                    cbSunny.setChecked(false);
                    cbCloudy.setChecked(false);
                    cbOvercast.setChecked(false);
                    cbRainy.setChecked(false);
                    cbOther.setChecked(false);
                    
                    // 重置星期几选择
                    cbMonday.setChecked(false);
                    cbTuesday.setChecked(false);
                    cbWednesday.setChecked(false);
                    cbThursday.setChecked(false);
                    cbFriday.setChecked(false);
                    cbSaturday.setChecked(false);
                    cbSunday.setChecked(false);
                    
                    // 将焦点设置回标题输入框
                    etTodoTitle.requestFocus();
                    
                    // 重新启用按钮
                    btnAddEvent.setEnabled(true);
                    btnAddEvent.setText("添加事项");
                });
            }

            @Override
            public void onError(String errorMsg) 
            {
                runOnUiThread(() -> {
                    Toast.makeText(AddTodoActivity.this, "添加失败: " + errorMsg, Toast.LENGTH_SHORT).show();
                    // 重新启用按钮
                    btnAddEvent.setEnabled(true);
                    btnAddEvent.setText("添加事项");
                });
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
            // 已经在添加事项界面，无需操作
            return true;
        } 
        else if (itemId == R.id.nav_todo_list) 
        {
            // 跳转到清单界面
            Intent intent = new Intent(this, TodoListActivity.class);
            // 使用默认的天气类型，实际使用时应从MainActivity获取
            intent.putExtra("current_weather_type", "晴");
            startActivity(intent);
            finish();
            return true;
        }
        
        return false;
    }
} 