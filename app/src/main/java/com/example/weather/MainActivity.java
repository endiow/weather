package com.example.weather;

import android.Manifest;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.IOException;
import java.lang.reflect.Method;
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
    private LocationManager locationManager;

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
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
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
    private void requestLocationPermission() 
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocationByAndroidProvider();
        }
    }

    /**
     * 使用Android原生位置服务获取城市定位
     */
    private void getLocationByAndroidProvider() 
    {
        if (ActivityCompat.checkSelfPermission(this, 
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "位置权限未授予，使用备用方法");
            useBackupLocationMethod();
            return;
        }
        
        // 记录开始获取位置的时间
        long startTime = System.currentTimeMillis();
        Log.d(TAG, "开始获取位置信息...");
        
        // 尝试先使用网络定位，更节省电量
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        
        Log.d(TAG, "位置提供者状态 - 网络: " + isNetworkEnabled + ", GPS: " + isGPSEnabled);
        
        if (!isNetworkEnabled && !isGPSEnabled) 
        {
            // 所有定位方式都未开启
            Log.e(TAG, "所有位置提供者都未开启");
            Toast.makeText(this, "请开启定位服务", Toast.LENGTH_SHORT).show();
            useBackupLocationMethod();
            return;
        }
        
        // 设置定位监听器
        LocationListener locationListener = new LocationListener() 
        {
            @Override
            public void onLocationChanged(@NonNull Location location) 
            {
                // 计算获取位置所需时间
                long timeUsed = System.currentTimeMillis() - startTime;
                Log.d(TAG, "位置已更新 - 提供者: " + location.getProvider() 
                        + ", 纬度: " + location.getLatitude() 
                        + ", 经度: " + location.getLongitude()
                        + ", 精度: " + location.getAccuracy() + "米"
                        + ", 用时: " + timeUsed + "ms");
                
                // 获取位置成功，解析城市名
                getCityNameFromLocation(location.getLatitude(), location.getLongitude());
                // 移除监听器，不再接收位置更新
                locationManager.removeUpdates(this);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) 
            {
                Log.w(TAG, "位置提供者已禁用: " + provider);
                // 位置提供者被禁用
                if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                        && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.e(TAG, "所有位置提供者都被禁用，使用备用方法");
                    useBackupLocationMethod();
                }
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) 
            {
                Log.d(TAG, "位置提供者已启用: " + provider);
                // 位置提供者被启用
            }
        };
        
        try 
        {
            // 先尝试使用网络定位
            if (isNetworkEnabled) 
            {
                Log.d(TAG, "请求网络位置更新");
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        0, 0, locationListener);
                
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (lastKnownLocation != null) 
                {
                    Log.d(TAG, "使用最后已知网络位置 - 纬度: " + lastKnownLocation.getLatitude() 
                            + ", 经度: " + lastKnownLocation.getLongitude()
                            + ", 精度: " + lastKnownLocation.getAccuracy() + "米"
                            + ", 时间: " + new java.util.Date(lastKnownLocation.getTime()));
                    
                    // 使用最后已知位置
                    getCityNameFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    locationManager.removeUpdates(locationListener);
                    return;
                } else {
                    Log.d(TAG, "无最后已知网络位置");
                }
            }
            
            // 如果网络定位失败或无最后已知位置，尝试GPS定位
            if (isGPSEnabled) 
            {
                Log.d(TAG, "请求GPS位置更新");
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0, 0, locationListener);
                
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) 
                {
                    Log.d(TAG, "使用最后已知GPS位置 - 纬度: " + lastKnownLocation.getLatitude() 
                            + ", 经度: " + lastKnownLocation.getLongitude()
                            + ", 精度: " + lastKnownLocation.getAccuracy() + "米"
                            + ", 时间: " + new java.util.Date(lastKnownLocation.getTime()));
                    
                    // 使用最后已知位置
                    getCityNameFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    locationManager.removeUpdates(locationListener);
                    return;
                } else {
                    Log.d(TAG, "无最后已知GPS位置");
                }
            }
            
            // 设置超时处理
            new Handler().postDelayed(() -> 
            {
                // 30秒后检查是否已获取位置
                if (tvCityName.getText().toString().equals("正在定位...") 
                        || tvCityName.getText().toString().isEmpty()) 
                {
                    Log.e(TAG, "位置获取超时（30秒），使用备用方法");
                    locationManager.removeUpdates(locationListener);
                    useBackupLocationMethod();
                }
            }, 30000); // 30秒超时
            
        } 
        catch (Exception e) 
        {
            Log.e(TAG, "获取位置失败: " + e.getMessage(), e);
            useBackupLocationMethod();
        }
    }

    /**
     * 使用经纬度获取城市信息
     */
    private void getCityNameFromLocation(double latitude, double longitude) 
    {
        Log.d(TAG, "使用和风天气API获取城市信息 - 纬度: " + latitude + ", 经度: " + longitude);
        
        // 构建位置字符串（格式：经度,纬度）
        String location = String.format(Locale.US, "%.2f,%.2f", longitude, latitude);
        Log.d(TAG, "位置参数: " + location);
        
        // 获取WeatherService实例
        com.example.weather.api.WeatherService weatherService = com.example.weather.api.WeatherService.getInstance(this);
        
        // 调用和风天气API获取城市信息
        weatherService.getCityName(location, new com.example.weather.api.WeatherService.WeatherCallback<com.qweather.sdk.response.geo.GeoCityLookupResponse>() 
        {
            @Override
            public void onSuccess(com.qweather.sdk.response.geo.GeoCityLookupResponse response) 
            {
                Log.d(TAG, "获取城市信息成功: " + response.toString());
                
                // 使用WeatherService解析城市信息
                com.example.weather.api.WeatherService.CityInfo cityInfo = weatherService.parseCityInfo(response);
                
                if (!cityInfo.error) 
                {
                    // 解析成功，使用解析出的城市名称和坐标
                    runOnUiThread(() -> 
                    {
                        tvCityName.setText(cityInfo.displayName);
                        
                        // 使用解析出的经纬度获取天气数据
                        double cityLat = cityInfo.getLatitude(latitude);
                        double cityLon = cityInfo.getLongitude(longitude);
                        fetchWeatherData(cityLat, cityLon);
                    });
                } 
                else 
                {
                    // 解析失败，使用原始坐标
                    Log.w(TAG, "解析城市信息失败: " + cityInfo.errorMessage);
                    runOnUiThread(() -> 
                    {
                        tvCityName.setText("当前位置");
                        fetchWeatherData(latitude, longitude);
                    });
                }
            }
            
            @Override
            public void onError(String message) 
            {
                // API调用失败，使用原始坐标
                Log.e(TAG, "获取城市信息失败: " + message);
                runOnUiThread(() -> 
                {
                    tvCityName.setText("当前位置");
                    fetchWeatherData(latitude, longitude);
                });
            }
        });
    }

    // 从字符串中提取字段值
    private String extractField(String source, String startStr, String endStr) {
        try {
            int startIndex = source.indexOf(startStr) + startStr.length();
            if (startIndex < startStr.length()) return "";
            
            int endIndex = source.indexOf(endStr, startIndex);
            if (endIndex < 0) endIndex = source.length();
            
            return source.substring(startIndex, endIndex).trim();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 当定位失败时的备用方法
     */
    private void useBackupLocationMethod() 
    {
        // 显示城市选择对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择城市");
        builder.setMessage("我们无法获取您的位置信息。请选择一个城市：");
        
        final String[] cities = {"北京", "上海", "广州", "深圳", "杭州", "成都", "武汉", "重庆", "长沙"};
        builder.setItems(cities, (dialog, which) -> {
            String selectedCity = cities[which];
            Toast.makeText(this, "已选择城市: " + selectedCity, Toast.LENGTH_SHORT).show();
            
            // 更新UI显示
            tvCityName.setText(selectedCity);
            
            // 根据城市名称获取位置坐标（模拟）
            double[] coordinates = getCityCoordinates(selectedCity);
            fetchWeatherData(coordinates[0], coordinates[1]);
        });
        
        builder.setNegativeButton("取消", (dialog, which) -> {
            // 用户取消，使用默认值（北京）
            tvCityName.setText("北京");
            double[] coordinates = getCityCoordinates("北京");
            fetchWeatherData(coordinates[0], coordinates[1]);
        });
        
        builder.show();
    }

    /**
     * 根据城市名称获取坐标（简化版，实际应用中可使用地理编码服务）
     * @param cityName 城市名称
     * @return 坐标数组[纬度, 经度]
     */
    private double[] getCityCoordinates(String cityName) 
    {
        // 简单的城市坐标映射（纬度,经度）
        switch (cityName) 
        {
            case "北京":
                return new double[]{39.9042, 116.4074};
            case "上海":
                return new double[]{31.2304, 121.4737};
            case "广州":
                return new double[]{23.1291, 113.2644};
            case "深圳":
                return new double[]{22.5431, 114.0579};
            case "杭州":
                return new double[]{30.2741, 120.1551};
            case "成都":
                return new double[]{30.5728, 104.0668};
            case "武汉":
                return new double[]{30.5928, 114.3055};
            case "重庆":
                return new double[]{29.5630, 106.5530};
            case "长沙":
                return new double[]{28.2278, 112.9388};
            default:
                // 默认返回北京坐标
                return new double[]{39.9042, 116.4074};
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) 
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) 
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) 
            {
                getLocationByAndroidProvider();
            } 
            else 
            {
                Toast.makeText(this, "需要位置权限来获取天气数据", Toast.LENGTH_SHORT).show();
                useBackupLocationMethod();
            }
        }
    }

    /**
     * 获取天气数据
     */
    private void fetchWeatherData(double latitude, double longitude) 
    {
        Log.d(TAG, "获取天气数据: 纬度=" + latitude + ", 经度=" + longitude);
        
        // 构建位置字符串（格式：经度,纬度）
        String location = longitude + "," + latitude;
        
        try 
        {
            // 获取WeatherService实例
            com.example.weather.api.WeatherService weatherService = com.example.weather.api.WeatherService.getInstance(this);
            
            // 标记是否已获取天气数据
            final boolean[] weatherDataObtained = {false};
            
            // 设置超时处理，如果10秒内没有获取到数据，则使用模拟数据
            new Handler().postDelayed(() -> {
                if (!weatherDataObtained[0]) {
                    Log.w(TAG, "获取天气数据超时，使用模拟数据");
                    simulateWeatherData();
                }
            }, 10000); // 10秒超时
            
            // 获取实时天气数据
            weatherService.getWeatherNow(location, new com.example.weather.api.WeatherService.WeatherCallback<com.qweather.sdk.response.weather.WeatherNowResponse>() 
            {
                @Override
                public void onSuccess(com.qweather.sdk.response.weather.WeatherNowResponse response) 
                {
                    weatherDataObtained[0] = true;
                    
                    try 
                    {
                        // 在UI线程中更新UI
                        runOnUiThread(() -> {
                            try 
                            {
                                // 更新天气类型
                                String text = response.getNow().getText();
                                currentWeatherType = convertWeatherText(text);
                                tvWeatherType.setText(text);
                                tvCurrentWeatherBadge.setText(currentWeatherType);
                                
                                // 更新温度
                                String temp = response.getNow().getTemp() + "°";
                                tvTemperature.setText(temp);
                                
                                // 更新时间
                                updateCurrentTime();
                                
                                // 更新天气描述
                                tvWeatherDescription.setText("当前天气" + text + "，风力" + response.getNow().getWindScale() + "级");
                                
                                // 更新匹配的待办事项
                                updateWeatherMatchedTodos();
                            } 
                            catch (Exception e) 
                            {
                                Log.e(TAG, "更新天气UI失败", e);
                                simulateWeatherData();
                            }
                        });
                        
                        // 获取三日天气预报
                        getWeatherForecast(location);
                    } 
                    catch (Exception e) 
                    {
                        Log.e(TAG, "处理实时天气数据失败", e);
                        runOnUiThread(() -> simulateWeatherData());
                    }
                }
                
                @Override
                public void onError(String message) 
                {
                    Log.e(TAG, "获取实时天气失败: " + message);
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "获取天气数据失败: " + message, Toast.LENGTH_SHORT).show();
                        // 使用模拟数据作为备用
                        simulateWeatherData();
                    });
                }
            });
            
            // 获取空气质量数据
            getAirQuality(location);
        } 
        catch (Exception e) 
        {
            Log.e(TAG, "获取天气服务失败", e);
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "天气服务初始化失败，使用模拟数据", Toast.LENGTH_SHORT).show();
                simulateWeatherData();
            });
        }
    }
    
    /**
     * 获取三日天气预报
     */
    private void getWeatherForecast(String location) 
    {
        com.example.weather.api.WeatherService weatherService = com.example.weather.api.WeatherService.getInstance(this);
        weatherService.getWeather3d(location, new com.example.weather.api.WeatherService.WeatherCallback<com.qweather.sdk.response.weather.WeatherDailyResponse>() 
        {
            @Override
            public void onSuccess(com.qweather.sdk.response.weather.WeatherDailyResponse response) 
            {
                if (response.getDaily() != null && !response.getDaily().isEmpty()) 
                {
                    // 获取今天的最高和最低温度
                    String tempMax = response.getDaily().get(0).getTempMax();
                    String tempMin = response.getDaily().get(0).getTempMin();
                    
                    // 在UI线程中更新温度范围
                    runOnUiThread(() -> {
                        tvTemperatureRange.setText(tempMin + "° / " + tempMax + "°");
                    });
                }
            }
            
            @Override
            public void onError(String message) 
            {
                Log.e(TAG, "获取三日天气预报失败: " + message);
            }
        });
    }
    
    /**
     * 获取空气质量数据
     */
    private void getAirQuality(String location) 
    {
        com.example.weather.api.WeatherService weatherService = com.example.weather.api.WeatherService.getInstance(this);
        weatherService.getAirNow(location, new com.example.weather.api.WeatherService.WeatherCallback<com.qweather.sdk.response.air.AirNowResponse>() 
        {
            @Override
            public void onSuccess(com.qweather.sdk.response.air.AirNowResponse response) 
            {
                // 在UI线程中更新空气质量
                runOnUiThread(() -> {
                    String category = response.getNow().getCategory(); // 空气质量级别
                    String aqi = response.getNow().getAqi();           // 空气质量指数
                    tvAirQuality.setText("空气" + category + " " + aqi);
                });
            }
            
            @Override
            public void onError(String message) 
            {
                Log.e(TAG, "获取空气质量失败: " + message);
            }
        });
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

    /**
     * 转换和风天气API返回的天气文本为应用中使用的天气类型
     * 
     * @param text 和风天气API返回的天气文本
     * @return 应用中使用的天气类型
     */
    private String convertWeatherText(String text) 
    {
        // 根据和风天气API文档中的天气状况代码，简化为应用中使用的几种天气类型
        if (text.contains("晴")) {
            return "晴";
        } else if (text.contains("多云") || text.contains("阴")) {
            return "多云";
        } else if (text.contains("雨")) {
            return "雨";
        } else if (text.contains("雪")) {
            return "雪";
        } else if (text.contains("雾") || text.contains("霾")) {
            return "雾";
        } else {
            return "晴"; // 默认
        }
    }
}