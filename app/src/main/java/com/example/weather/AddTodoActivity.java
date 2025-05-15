package com.example.weather;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.weather.api.WeatherCallback;
import com.example.weather.api.WeatherService;
import com.example.weather.manager.TodoManager;
import com.example.weather.model.Todo;
import com.example.weather.util.AlarmScheduler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.qweather.sdk.response.weather.WeatherHourlyResponse;
import com.qweather.sdk.response.weather.WeatherHourly;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddTodoActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener
{
    private static final String TAG = "AddTodoActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
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
    private Calendar startTimeCalendar;
    private Calendar endTimeCalendar;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    
    // 位置服务
    private LocationManager locationManager;
    private double latitude = 0;
    private double longitude = 0;
    
    // 24小时天气预报数据
    private WeatherHourlyResponse hourlyWeatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);
        
        // 底部导航栏
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.nav_add_todo);
        
        // 初始化UI组件
        initViews();
        
        // 初始化Calendar对象，并设置秒和毫秒为0
        startTimeCalendar = Calendar.getInstance();
        startTimeCalendar.set(Calendar.SECOND, 0);
        startTimeCalendar.set(Calendar.MILLISECOND, 0);
        
        endTimeCalendar = Calendar.getInstance();
        endTimeCalendar.set(Calendar.SECOND, 0);
        endTimeCalendar.set(Calendar.MILLISECOND, 0);
        
        // 设置监听器
        setupListeners();
        
        // 初始化位置服务
        initLocationService();
    }
    
    private void initViews() 
    {
        etTodoTitle = findViewById(R.id.etTodoTitle);
        etTodoDescription = findViewById(R.id.etTodoDescription);
        btnStartTime = findViewById(R.id.btnStartTime);
        btnEndTime = findViewById(R.id.btnEndTime);
        btnAddEvent = findViewById(R.id.btnAddEvent);
        
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
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
     
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
    
    @Override
    protected void onResume() 
    {
        super.onResume();
        
        // 如果已有位置信息，则获取天气预报
        if (latitude != 0 && longitude != 0) 
        {
            get24HourWeatherForecast();
        } 
        else 
        {
            // 获取位置
            getLocation();
        }
    }
    
    @Override
    protected void onPause() 
    {
        super.onPause();
        
        // 停止位置更新
        if (locationManager != null) 
        {
            locationManager.removeUpdates(locationListener);
        }
    }
    
    /**
     * 初始化位置服务
     */
    private void initLocationService() 
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }
    
    /**
     * 获取位置信息
     */
    private void getLocation() 
    {
        // 检查是否有位置权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) 
        {
            // 请求位置权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        
        // 获取最后已知位置
        Location lastLocation = null;
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) 
        {
            lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        
        if (lastLocation == null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) 
        {
            lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        
        if (lastLocation != null) 
        {
            latitude = lastLocation.getLatitude();
            longitude = lastLocation.getLongitude();
            get24HourWeatherForecast();
        }
        
        // 请求位置更新
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) 
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }
        
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) 
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);
        }
    }
    
    /**
     * 获取24小时天气预报
     */
    private void get24HourWeatherForecast() 
    {
        if (latitude == 0 || longitude == 0) 
        {
            Log.e(TAG, "无法获取天气预报：位置信息不可用");
            return;
        }
        
        WeatherService.getInstance(this).getWeatherHourlyForecast(latitude, longitude, new WeatherCallback<WeatherHourlyResponse>() 
        {
            @Override
            public void onSuccess(WeatherHourlyResponse response) 
            {
                if (response.getCode() == null || !response.getCode().equals("200")) 
                {
                    Log.e(TAG, "获取24小时天气预报失败: " + response.getCode());
                    return;
                }
                
                Log.d(TAG, "获取24小时天气预报成功");
                hourlyWeatherData = response;
            }
            
            @Override
            public void onError(String message) 
            {
                Log.e(TAG, "获取24小时天气预报出错: " + message);
            }
        });
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
        if (cbSunny.isChecked()) weatherTypes.add("晴");
        if (cbCloudy.isChecked()) weatherTypes.add("多云");
        if (cbOvercast.isChecked()) weatherTypes.add("阴");
        if (cbRainy.isChecked()) weatherTypes.add("雨");
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
                    
                    // 添加成功后，设置正确的ID并安排提醒
                    if (todo.isRemindable() && result != null && result > 0) 
                    {
                        // 设置数据库返回的ID
                        todo.setId(result);
                        
                        // 检查是否为当天事项
                        Calendar todoCalendar = Calendar.getInstance();
                        todoCalendar.setTime(todo.getStartTime());
                        
                        Calendar todayCalendar = Calendar.getInstance();
                        boolean isSameDay = todoCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR) &&
                                            todoCalendar.get(Calendar.DAY_OF_YEAR) == todayCalendar.get(Calendar.DAY_OF_YEAR);
                        
                        if (isSameDay) 
                        {
                            // 是当天事项，立即安排提醒
                            AlarmScheduler alarmScheduler = new AlarmScheduler(AddTodoActivity.this);
                            alarmScheduler.scheduleTodo(todo);
                            Log.d(TAG, "已为新添加的待办事项安排提醒，ID: " + todo.getId());
                        }
                    }
                    
                    // 重置表单
                    resetForm();
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
    
    /**
     * 位置变化监听器
     */
    private final LocationListener locationListener = new LocationListener() 
    {
        @Override
        public void onLocationChanged(@NonNull Location location) 
        {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            
            // 获取到位置后，获取天气预报
            get24HourWeatherForecast();
            
            // 获取位置后停止更新
            locationManager.removeUpdates(this);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) 
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) 
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) 
            {
                // 权限获取成功，获取位置
                getLocation();
            } 
            else 
            {
                // 权限被拒绝
                Toast.makeText(this, "需要位置权限才能获取天气数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 在重置表单时也确保设置秒和毫秒为0
    private void resetForm() {
        // 清空表单，准备添加新事项
        etTodoTitle.setText("");
        etTodoDescription.setText("");
        
        // 重置时间选择器
        startTimeCalendar = Calendar.getInstance();
        startTimeCalendar.set(Calendar.SECOND, 0);
        startTimeCalendar.set(Calendar.MILLISECOND, 0);
        
        endTimeCalendar = Calendar.getInstance();
        endTimeCalendar.set(Calendar.SECOND, 0);
        endTimeCalendar.set(Calendar.MILLISECOND, 0);
        
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
    }
} 