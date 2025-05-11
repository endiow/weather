package com.example.weather;

import android.Manifest;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.adapter.TodoAdapter;
import com.example.weather.model.Todo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TodoAdapter.OnTodoClickListener {

    private static final String TAG = "MainActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int ADD_TODO_REQUEST_CODE = 101;
    
    // UI 组件
    private TextView tvCityName;
    private TextView tvWeatherType;
    private TextView tvTemperature;
    private TextView tvTemperatureRange;
    private TextView tvWeatherDescription;
    private TextView tvUpdateTime;
    private TextView tvAirQuality;
    private TextView tvCurrentWeatherBadge;
    private TextView tvNoWeatherTodos;
    private TextView tvNoAllTodos;
    private TextView tvSwipeHint;
    private RecyclerView rvAllTodos;
    private RecyclerView rvWeatherTodos;
    private Button btnAddTodo;
    private LinearLayout bottomSheetLayout;
    
    // 底部滑动行为
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    
    // 适配器
    private TodoAdapter allTodosAdapter;
    private TodoAdapter weatherTodosAdapter;
    
    // 数据
    private List<Todo> allTodosList;
    private String currentWeatherType = "晴";
    private String currentTime;
    
    // 位置服务
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // 初始化数据
        allTodosList = new ArrayList<>();
        currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        
        // 初始化UI组件
        initUI();
        
        // 设置当前时间（更新时间）
        updateCurrentTime();
        
        // 初始化RecyclerView
        setupRecyclerViews();
        
        // 初始化底部滑动面板
        setupBottomSheet();
        
        // 初始化位置服务
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        // 请求位置权限
        requestLocationPermission();
        
        // 设置按钮点击事件
        setupClickListeners();
        
        // TODO: 初始化ViewModel
        
        // TODO: 观察天气数据变化
        
        // TODO: 观察待办事项数据变化
        
        // 添加一些模拟数据
        addMockTodos();
    }
    
    private void setupClickListeners() {
        // 设置添加待办事项按钮点击事件
        btnAddTodo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTodoActivity.class);
            startActivityForResult(intent, ADD_TODO_REQUEST_CODE);
        });
    }
    
    /**
     * 添加模拟待办事项数据
     */
    private void addMockTodos() {
        // 添加几个不同天气类型的待办事项
        allTodosList.add(new Todo("晴天去公园", "带上帽子和防晒霜，去公园散步", "晴", true, "09:00", "17:00"));
        allTodosList.add(new Todo("雨天整理衣物", "下雨天在家整理冬季衣物", "雨", true, "10:00", "16:00"));
        allTodosList.add(new Todo("购买日用品", "购买洗发水、沐浴露等日用品", "晴", true, "08:00", "20:00"));
        allTodosList.add(new Todo("处理文件", "整理和归档重要文件", "多云", true, "14:00", "18:00"));
        
        // 更新适配器
        allTodosAdapter.updateTodoList(allTodosList);
        
        // 过滤与当前天气匹配的待办事项
        updateWeatherMatchedTodos();
    }
    
    /**
     * 更新与当前天气和时间匹配的待办事项列表
     */
    private void updateWeatherMatchedTodos() {
        List<Todo> weatherMatchedTodos = new ArrayList<>();
        for (Todo todo : allTodosList) {
            if (todo.matchesWeatherAndTime(currentWeatherType, currentTime)) {
                weatherMatchedTodos.add(todo);
            }
        }
        weatherTodosAdapter.updateTodoList(weatherMatchedTodos);
        updateEmptyStateVisibility(weatherMatchedTodos.isEmpty(), allTodosList.isEmpty());
    }
    
    /**
     * 初始化UI组件
     */
    private void initUI() {
        tvCityName = findViewById(R.id.tvCityName);
        tvWeatherType = findViewById(R.id.tvWeatherType);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvTemperatureRange = findViewById(R.id.tvTemperatureRange);
        tvWeatherDescription = findViewById(R.id.tvWeatherDescription);
        tvUpdateTime = findViewById(R.id.tvUpdateTime);
        tvAirQuality = findViewById(R.id.tvAirQuality);
        tvCurrentWeatherBadge = findViewById(R.id.tvCurrentWeatherBadge);
        tvNoWeatherTodos = findViewById(R.id.tvNoWeatherTodos);
        tvNoAllTodos = findViewById(R.id.tvNoAllTodos);
        tvSwipeHint = findViewById(R.id.tvSwipeHint);
        rvAllTodos = findViewById(R.id.rvAllTodos);
        rvWeatherTodos = findViewById(R.id.rvWeatherTodos);
        btnAddTodo = findViewById(R.id.btnAddTodo);
        bottomSheetLayout = findViewById(R.id.bottomSheetLayout);
    }
    
    /**
     * 设置底部滑动面板
     */
    private void setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        
        // 设置回调
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // 根据底部面板的状态更新UI
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    // 当面板完全展开时，隐藏上滑提示
                    tvSwipeHint.setVisibility(View.GONE);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    // 当面板收起时，显示上滑提示
                    tvSwipeHint.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // 根据滑动的位置调整上滑提示的透明度
                float alpha = 1 - slideOffset;
                tvSwipeHint.setAlpha(alpha);
            }
        });
        
        // 设置初始状态为收起
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        
        // 设置上滑提示的点击事件
        tvSwipeHint.setOnClickListener(v -> {
            // 点击上滑提示时展开底部面板
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
    }
    
    /**
     * 更新当前时间
     */
    private void updateCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        currentTime = dateFormat.format(new Date());
        tvUpdateTime.setText(currentTime + " 更新");
    }
    
    /**
     * 设置RecyclerView
     */
    private void setupRecyclerViews() {
        // 初始化适配器
        allTodosAdapter = new TodoAdapter();
        weatherTodosAdapter = new TodoAdapter();
        
        // 设置点击监听器
        allTodosAdapter.setOnTodoClickListener(this);
        weatherTodosAdapter.setOnTodoClickListener(this);
        
        // 设置所有待办事项RecyclerView
        rvAllTodos.setLayoutManager(new LinearLayoutManager(this));
        rvAllTodos.setHasFixedSize(true);
        rvAllTodos.setAdapter(allTodosAdapter);
        
        // 设置天气匹配待办事项RecyclerView
        rvWeatherTodos.setLayoutManager(new LinearLayoutManager(this));
        rvWeatherTodos.setHasFixedSize(true);
        rvWeatherTodos.setAdapter(weatherTodosAdapter);
    }
    
    /**
     * 更新空状态视图的可见性
     * @param weatherTodosEmpty 天气相关待办事项是否为空
     * @param allTodosEmpty 所有待办事项是否为空
     */
    private void updateEmptyStateVisibility(boolean weatherTodosEmpty, boolean allTodosEmpty) {
        tvNoWeatherTodos.setVisibility(weatherTodosEmpty ? View.VISIBLE : View.GONE);
        tvNoAllTodos.setVisibility(allTodosEmpty ? View.VISIBLE : View.GONE);
        
        rvWeatherTodos.setVisibility(weatherTodosEmpty ? View.GONE : View.VISIBLE);
        rvAllTodos.setVisibility(allTodosEmpty ? View.GONE : View.VISIBLE);
    }

    /**
     * 请求位置权限
     */
    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "需要位置权限来获取天气数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 获取最后已知位置
     */
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // 获取到位置信息，请求天气数据
                        fetchWeatherData(location.getLatitude(), location.getLongitude());
                    } else {
                        Toast.makeText(MainActivity.this, "无法获取当前位置", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 获取天气数据
     */
    private void fetchWeatherData(double latitude, double longitude) {
        // TODO: 使用ViewModel获取天气数据
        Log.d(TAG, "获取天气数据: 纬度=" + latitude + ", 经度=" + longitude);
        
        // 模拟天气数据
        simulateWeatherData();
    }
    
    /**
     * 模拟天气数据（临时方法，后续会替换为真实API数据）
     */
    private void simulateWeatherData() {
        // 模拟城市名称
        tvCityName.setText("北京市");
        
        // 模拟温度
        tvTemperature.setText("26°");
        tvTemperatureRange.setText("22° / 28°");
        
        // 模拟天气类型
        currentWeatherType = "晴";
        tvWeatherType.setText(currentWeatherType);
        tvCurrentWeatherBadge.setText(currentWeatherType);
        
        // 模拟天气描述
        tvWeatherDescription.setText("今日天气晴朗，适合外出活动");
        
        // 模拟空气质量
        tvAirQuality.setText("空气优 19");
        
        // 更新时间
        updateCurrentTime();
        
        // 如果已有待办事项数据，更新天气匹配的待办事项
        if (allTodosList != null && !allTodosList.isEmpty()) {
            updateWeatherMatchedTodos();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // 处理添加待办事项的结果
        if (requestCode == ADD_TODO_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // 从Intent中获取数据
            String title = data.getStringExtra("todo_title");
            String description = data.getStringExtra("todo_description");
            String weatherTypes = data.getStringExtra("todo_weather_types");
            String airQualities = data.getStringExtra("todo_air_qualities");
            String humidities = data.getStringExtra("todo_humidities");
            String startTime = data.getStringExtra("todo_start_time");
            String endTime = data.getStringExtra("todo_end_time");
            boolean isRemindable = data.getBooleanExtra("todo_remindable", true);
            
            // 解析选中的天气类型、空气质量和湿度
            String[] weatherTypeArray = weatherTypes.split(",");
            String[] airQualityArray = airQualities.split(",");
            String[] humidityArray = humidities.split(",");
            
            // 如果返回了星期数据
            String[] days = data.getStringArrayExtra("todo_days");
            List<String> daysList = new ArrayList<>();
            if (days != null && days.length > 0) {
                Collections.addAll(daysList, days);
            }
            
            // 为每个天气类型、空气质量和湿度组合创建待办事项
            for (String weatherType : weatherTypeArray) {
                for (String airQuality : airQualityArray) {
                    for (String humidity : humidityArray) {
                        // 创建新的待办事项
                        Todo newTodo = new Todo(
                                title,
                                description,
                                weatherType,
                                isRemindable,
                                startTime,
                                endTime,
                                airQuality,
                                humidity
                        );
                        newTodo.setDaysOfWeek(daysList);
                        
                        // 添加到全局列表
                        allTodosList.add(newTodo);
                    }
                }
            }
            
            // 更新适配器
            allTodosAdapter.updateTodoList(allTodosList);
            
            // 更新天气匹配的待办事项列表
            updateWeatherMatchedTodos();
            
            // 显示添加成功提示并展开底部面板
            Toast.makeText(MainActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }
    
    /**
     * 显示添加待办事项对话框
     */
    private void showAddTodoDialog() {
        // 使用自定义对话框布局
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_todo, null);
        builder.setView(dialogView);
        
        // 创建对话框
        AlertDialog dialog = builder.create();
        
        // 设置对话框全屏宽度并取消标题
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // 获取对话框中的控件
        EditText etTitle = dialogView.findViewById(R.id.etTodoTitle);
        EditText etDescription = dialogView.findViewById(R.id.etTodoDescription);
        RadioGroup rgWeatherType = dialogView.findViewById(R.id.rgWeatherType);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);
        SwitchCompat switchRemindable = dialogView.findViewById(R.id.cbRemindable);
        
        // 时间选择相关控件
        Button btnStartTime = dialogView.findViewById(R.id.btnStartTime);
        Button btnEndTime = dialogView.findViewById(R.id.btnEndTime);
        
        // 设置默认时间
        String defaultStartTime = "08:00";
        String defaultEndTime = "18:00";
        btnStartTime.setText(defaultStartTime);
        btnEndTime.setText(defaultEndTime);
        
        // 设置时间选择器点击事件
        btnStartTime.setOnClickListener(v -> showTimePickerDialog(btnStartTime, defaultStartTime));
        btnEndTime.setOnClickListener(v -> showTimePickerDialog(btnEndTime, defaultEndTime));
        
        // 默认选中第一个天气类型
        if (rgWeatherType.getChildCount() > 0) {
            ((RadioButton) rgWeatherType.getChildAt(0)).setChecked(true);
        }
        
        // 设置取消按钮点击事件
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        // 设置添加按钮点击事件
        btnAdd.setOnClickListener(v -> {
            // 获取输入的数据
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            
            // 获取选中的天气类型
            int selectedRadioButtonId = rgWeatherType.getCheckedRadioButtonId();
            if (selectedRadioButtonId == -1) {
                Toast.makeText(this, "请选择天气类型", Toast.LENGTH_SHORT).show();
                return;
            }
            
            RadioButton selectedRadioButton = dialogView.findViewById(selectedRadioButtonId);
            String weatherType = selectedRadioButton.getText().toString();
            
            // 获取时间段
            String startTime = btnStartTime.getText().toString();
            String endTime = btnEndTime.getText().toString();
            
            // 验证时间段
            if (startTime.compareTo(endTime) >= 0) {
                Toast.makeText(this, "开始时间必须早于结束时间", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 获取是否需要提醒
            boolean isRemindable = switchRemindable.isChecked();
            
            // 验证输入
            if (title.isEmpty()) {
                etTitle.setError("请输入标题");
                return;
            }
            
            // 创建新待办事项并保存
            addTodo(title, description, weatherType, isRemindable, startTime, endTime);
            
            // 关闭对话框
            dialog.dismiss();
            
            // 显示添加成功提示并展开底部面板
            Toast.makeText(MainActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        
        // 显示对话框
        dialog.show();
        
        // 设置对话框宽度和背景
        Window window = dialog.getWindow();
        if (window != null) {
            // 设置圆角背景
            window.setBackgroundDrawableResource(R.drawable.dialog_rounded_bg);
            
            // 设置窗口尺寸
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }
    }
    
    /**
     * 显示时间选择对话框
     */
    private void showTimePickerDialog(Button timeButton, String defaultTime) {
        // 解析默认时间
        String[] timeParts = defaultTime.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        
        // 创建时间选择器
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minuteOfHour) -> {
                    // 格式化时间并设置到按钮上
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    timeButton.setText(formattedTime);
                },
                hour, minute, true);
        
        timePickerDialog.show();
    }
    
    /**
     * 添加新待办事项
     */
    private void addTodo(String title, String description, String weatherType, 
                        boolean isRemindable, String startTime, String endTime) {
        // 创建新的待办事项
        Todo newTodo = new Todo(title, description, weatherType, isRemindable, startTime, endTime);
        
        // 添加到全局列表
        allTodosList.add(newTodo);
        allTodosAdapter.updateTodoList(allTodosList);
        
        // 更新天气匹配的待办事项列表
        updateWeatherMatchedTodos();
        
        Toast.makeText(this, "已添加待办事项", Toast.LENGTH_SHORT).show();
    }
    
    // 实现TodoAdapter.OnTodoClickListener接口方法
    
    @Override
    public void onTodoClick(Todo todo, int position) {
        // 点击待办事项时的处理逻辑
        Toast.makeText(this, "点击了：" + todo.getTitle() + "（" + todo.getTimeRangeDisplay() + "）", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onTodoCompleteChanged(Todo todo, int position, boolean isCompleted) {
        // 待办事项完成状态变更时的处理逻辑
        // 更新两个列表的显示
        allTodosAdapter.notifyItemChanged(position);
        
        // 更新天气匹配列表
        updateWeatherMatchedTodos();
        
        String message = isCompleted ? "已完成：" : "取消完成：";
        Toast.makeText(this, message + todo.getTitle(), Toast.LENGTH_SHORT).show();
    }
}