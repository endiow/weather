package com.example.weather;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.adapter.AirQualityAdapter;
import com.example.weather.adapter.DayOfWeekAdapter;
import com.example.weather.adapter.HumidityAdapter;
import com.example.weather.adapter.WeatherTypeAdapter;
import com.example.weather.model.AirQuality;
import com.example.weather.model.DayOfWeek;
import com.example.weather.model.Humidity;
import com.example.weather.model.Todo;
import com.example.weather.model.WeatherType;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddTodoActivity extends AppCompatActivity {

    private TextInputEditText etTitle;
    private TextInputEditText etDescription;
    private MaterialButton btnStartTime;
    private MaterialButton btnEndTime;
    private RecyclerView rvWeatherTypes;
    private RecyclerView rvDaysOfWeek;
    private RecyclerView rvAirQualities;
    private RecyclerView rvHumidities;
    private SwitchMaterial switchRemindable;
    private MaterialButton btnSave;

    private WeatherTypeAdapter weatherTypeAdapter;
    private DayOfWeekAdapter dayOfWeekAdapter;
    private AirQualityAdapter airQualityAdapter;
    private HumidityAdapter humidityAdapter;
    
    private String startTime = "08:00";
    private String endTime = "18:00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);
        
        // 设置工具栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("添加新待办事项");
        }
        
        // 初始化视图
        initViews();
        
        // 设置天气类型列表
        setupWeatherTypesList();
        
        // 设置空气质量列表
        setupAirQualitiesList();
        
        // 设置空气湿度列表
        setupHumiditiesList();
        
        // 设置星期几列表
        setupDaysOfWeekList();
        
        // 设置时间选择
        btnStartTime.setOnClickListener(v -> showTimePickerDialog(btnStartTime, startTime));
        btnEndTime.setOnClickListener(v -> showTimePickerDialog(btnEndTime, endTime));
        
        // 设置保存按钮
        btnSave.setOnClickListener(v -> saveTodo());
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void initViews() {
        etTitle = findViewById(R.id.etTodoTitle);
        etDescription = findViewById(R.id.etTodoDescription);
        btnStartTime = findViewById(R.id.btnStartTime);
        btnEndTime = findViewById(R.id.btnEndTime);
        rvWeatherTypes = findViewById(R.id.rvWeatherTypes);
        rvDaysOfWeek = findViewById(R.id.rvDaysOfWeek);
        rvAirQualities = findViewById(R.id.rvAirQualities);
        rvHumidities = findViewById(R.id.rvHumidities);
        switchRemindable = findViewById(R.id.switchRemindable);
        btnSave = findViewById(R.id.btnSave);
        
        // 设置默认时间
        btnStartTime.setText(startTime);
        btnEndTime.setText(endTime);
    }
    
    private void setupWeatherTypesList() {
        List<WeatherType> weatherTypes = new ArrayList<>();
        weatherTypes.add(new WeatherType("晴天", R.drawable.ic_sunny, false));
        weatherTypes.add(new WeatherType("多云", R.drawable.ic_cloudy, false));
        weatherTypes.add(new WeatherType("雨天", R.drawable.ic_rainy, false));
        weatherTypes.add(new WeatherType("雪天", R.drawable.ic_snowy, false));
        weatherTypes.add(new WeatherType("雷雨", R.drawable.ic_thunder, false));
        weatherTypes.add(new WeatherType("雾霾", R.drawable.ic_foggy, false));
        weatherTypes.add(new WeatherType("任意", R.drawable.ic_any_weather, false));
        
        weatherTypeAdapter = new WeatherTypeAdapter(weatherTypes);
        rvWeatherTypes.setAdapter(weatherTypeAdapter);
        rvWeatherTypes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }
    
    private void setupAirQualitiesList() {
        List<AirQuality> airQualities = new ArrayList<>();
        airQualities.add(new AirQuality("优", R.drawable.ic_air_quality, false));
        airQualities.add(new AirQuality("良", R.drawable.ic_air_quality, false));
        airQualities.add(new AirQuality("轻度污染", R.drawable.ic_air_quality, false));
        airQualities.add(new AirQuality("中度污染", R.drawable.ic_air_quality, false));
        airQualities.add(new AirQuality("重度污染", R.drawable.ic_air_quality, false));
        airQualities.add(new AirQuality("任意", R.drawable.ic_any_weather, false));
        
        airQualityAdapter = new AirQualityAdapter(airQualities);
        rvAirQualities.setAdapter(airQualityAdapter);
        rvAirQualities.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }
    
    private void setupHumiditiesList() {
        List<Humidity> humidities = new ArrayList<>();
        humidities.add(new Humidity("干燥", R.drawable.ic_humidity, false));
        humidities.add(new Humidity("适中", R.drawable.ic_humidity, false));
        humidities.add(new Humidity("潮湿", R.drawable.ic_humidity, false));
        humidities.add(new Humidity("任意", R.drawable.ic_any_weather, false));
        
        humidityAdapter = new HumidityAdapter(humidities);
        rvHumidities.setAdapter(humidityAdapter);
        rvHumidities.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }
    
    private void setupDaysOfWeekList() {
        List<DayOfWeek> daysOfWeek = new ArrayList<>();
        daysOfWeek.add(new DayOfWeek("周一", false));
        daysOfWeek.add(new DayOfWeek("周二", false));
        daysOfWeek.add(new DayOfWeek("周三", false));
        daysOfWeek.add(new DayOfWeek("周四", false));
        daysOfWeek.add(new DayOfWeek("周五", false));
        daysOfWeek.add(new DayOfWeek("周六", false));
        daysOfWeek.add(new DayOfWeek("周日", false));
        
        dayOfWeekAdapter = new DayOfWeekAdapter(daysOfWeek);
        rvDaysOfWeek.setAdapter(dayOfWeekAdapter);
        rvDaysOfWeek.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }
    
    private void showTimePickerDialog(MaterialButton timeButton, String defaultTime) {
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
                    
                    // 更新对应的时间变量
                    if (timeButton.getId() == R.id.btnStartTime) {
                        startTime = formattedTime;
                    } else {
                        endTime = formattedTime;
                    }
                },
                hour, minute, true);
        
        timePickerDialog.show();
    }
    
    private void saveTodo() {
        // 获取数据
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        
        // 验证输入
        if (title.isEmpty()) {
            etTitle.setError("请输入标题");
            return;
        }
        
        // 获取选中的天气类型
        List<String> selectedWeatherTypes = weatherTypeAdapter.getSelectedWeatherTypeNames();
        if (selectedWeatherTypes.isEmpty()) {
            Toast.makeText(this, "请至少选择一种天气类型", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 获取选中的空气质量
        List<String> selectedAirQualities = airQualityAdapter.getSelectedAirQualityNames();
        if (selectedAirQualities.isEmpty()) {
            Toast.makeText(this, "请至少选择一种空气质量", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 获取选中的湿度
        List<String> selectedHumidities = humidityAdapter.getSelectedHumidityNames();
        if (selectedHumidities.isEmpty()) {
            Toast.makeText(this, "请至少选择一种湿度", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 验证时间段
        if (startTime.compareTo(endTime) >= 0) {
            Toast.makeText(this, "开始时间必须早于结束时间", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 获取选中的星期几
        List<String> selectedDays = dayOfWeekAdapter.getSelectedDayNames();
        if (selectedDays.isEmpty()) {
            Toast.makeText(this, "请至少选择一天", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 获取是否需要提醒
        boolean isRemindable = switchRemindable.isChecked();
        
        // 将选中的天气类型、空气质量和湿度合并为字符串，用于传递
        String weatherTypesStr = TextUtils.join(",", selectedWeatherTypes);
        String airQualitiesStr = TextUtils.join(",", selectedAirQualities);
        String humiditiesStr = TextUtils.join(",", selectedHumidities);
        
        // 将数据返回给主活动
        Intent resultIntent = new Intent();
        resultIntent.putExtra("todo_title", title);
        resultIntent.putExtra("todo_description", description);
        resultIntent.putExtra("todo_weather_types", weatherTypesStr);
        resultIntent.putExtra("todo_air_qualities", airQualitiesStr);
        resultIntent.putExtra("todo_humidities", humiditiesStr);
        resultIntent.putExtra("todo_start_time", startTime);
        resultIntent.putExtra("todo_end_time", endTime);
        resultIntent.putExtra("todo_days", selectedDays.toArray(new String[0]));
        resultIntent.putExtra("todo_remindable", isRemindable);
        
        setResult(RESULT_OK, resultIntent);
        finish();
        
        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
    }
} 