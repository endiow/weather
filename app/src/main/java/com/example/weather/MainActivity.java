package com.example.weather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.adapter.ForecastAdapter;
import com.example.weather.api.AirQualityInfo;
import com.example.weather.api.CityInfo;
import com.example.weather.api.MinutelyRainInfo;
import com.example.weather.api.WeatherCallback;
import com.example.weather.api.WeatherForecastInfo;
import com.example.weather.api.WeatherInfo;
import com.example.weather.api.WeatherService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.weather.manager.TodoManager;
import com.example.weather.model.Todo;
import com.example.weather.util.AlarmScheduler;
import com.example.weather.util.LocationPreferences;
import com.qweather.sdk.response.weather.WeatherHourlyResponse;
import com.qweather.sdk.response.weather.WeatherHourly;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.example.weather.util.WeatherTypeUtil;
import com.example.weather.util.WeatherTypeUtil.WeatherType;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener
{
    private static final String TAG = "MainActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    // 定时更新相关常量
    private static final long WEATHER_UPDATE_INTERVAL = 30 * 60 * 1000; // 30分钟更新一次天气
    private static final long AIR_QUALITY_UPDATE_INTERVAL = 60 * 60 * 1000; // 1小时更新一次空气质量
    private static final long RAIN_FORECAST_UPDATE_INTERVAL = 10 * 60 * 1000; // 10分钟更新一次降水预报
    
    // UI 组件
    private TextView tvCityName;
    private TextView tvWeatherType;
    private TextView tvTemperature;
    private TextView tvTemperatureRange;
    private TextView tvWeatherDescription;
    private TextView tvUpdateTime;
    private TextView tvAirQuality;
    private RecyclerView rvForecast;
    private CoordinatorLayout mainLayout;
    
    // 数据
    private String currentWeatherType = "晴";
    private String currentTime;
    
    // 位置服务
    private LocationManager locationManager;
    private double latitude = 0;
    private double longitude = 0;
    
    // 城市ID
    private String currentCityId = "";
    
    // 定时器处理器
    private Handler weatherUpdateHandler;
    private Handler airQualityUpdateHandler;
    private Handler rainForecastUpdateHandler;
    
    // 更新任务
    private Runnable weatherUpdateRunnable;
    private Runnable airQualityUpdateRunnable;
    private Runnable rainForecastUpdateRunnable;

    // 天气背景映射
    private Map<String, Integer> weatherBackgrounds;

    // 天气预报适配器
    private ForecastAdapter forecastAdapter;
    
    // 天气预报数据
    private WeatherForecastInfo weatherForecastInfo;

    // 雨水预报UI组件
    private View rainForecastView;
    private ImageView ivRainIcon;
    private TextView tvRainTitle;
    private TextView tvRainForecast;
    private ProgressBar progressRain;

    // 底部导航栏
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> 
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // 初始化当前时间
        currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        
        // 初始化UI组件
        initUI();
        
        // 初始化天气背景映射
        initWeatherBackgrounds();
        
        // 初始化天气预报列表
        initForecastRecyclerView();
        
        // 设置当前时间（更新时间）
        updateCurrentTime();
        
        // 初始化定时更新
        initPeriodicUpdates();
        
        // 初始化位置服务
        initLocationService();
        
        // 设置底部导航栏选中项
        bottomNavigationView.setSelectedItemId(R.id.nav_weather);
    }
    
    /**
     * 初始化天气背景映射
     */
    private void initWeatherBackgrounds() 
    {
        weatherBackgrounds = new HashMap<>();
        
        // 晴天
        weatherBackgrounds.put("晴", R.drawable.bg_weather_sunny);
        weatherBackgrounds.put("CLEAR_DAY", R.drawable.bg_weather_sunny);
        
        // 多云
        weatherBackgrounds.put("多云", R.drawable.bg_weather_cloudy);
        weatherBackgrounds.put("CLOUDY", R.drawable.bg_weather_cloudy);
        weatherBackgrounds.put("阴", R.drawable.bg_weather_cloudy);
        weatherBackgrounds.put("PARTLY_CLOUDY", R.drawable.bg_weather_cloudy);
        
        // 雨天
        weatherBackgrounds.put("小雨", R.drawable.bg_weather_rainy);
        weatherBackgrounds.put("中雨", R.drawable.bg_weather_rainy);
        weatherBackgrounds.put("大雨", R.drawable.bg_weather_rainy);
        weatherBackgrounds.put("暴雨", R.drawable.bg_weather_rainy);
        weatherBackgrounds.put("RAIN", R.drawable.bg_weather_rainy);
        
        // 雪天
        weatherBackgrounds.put("小雪", R.drawable.bg_weather_snowy);
        weatherBackgrounds.put("中雪", R.drawable.bg_weather_snowy);
        weatherBackgrounds.put("大雪", R.drawable.bg_weather_snowy);
        weatherBackgrounds.put("暴雪", R.drawable.bg_weather_snowy);
        weatherBackgrounds.put("SNOW", R.drawable.bg_weather_snowy);
        
        // 雷雨
        weatherBackgrounds.put("雷阵雨", R.drawable.bg_weather_thunder);
        weatherBackgrounds.put("雷电", R.drawable.bg_weather_thunder);
        weatherBackgrounds.put("THUNDER", R.drawable.bg_weather_thunder);
        
        // 雾天
        weatherBackgrounds.put("雾", R.drawable.bg_weather_foggy);
        weatherBackgrounds.put("霾", R.drawable.bg_weather_foggy);
        weatherBackgrounds.put("FOG", R.drawable.bg_weather_foggy);
    }
    
    /**
     * 根据天气类型更新背景
     * 
     * @param weatherType 天气类型
     */
    private void updateBackgroundByWeather(String weatherType) 
    {
        if (weatherType == null || weatherType.isEmpty()) 
        {
            return;
        }
        
        // 使用WeatherTypeUtil获取标准化的天气类型
        WeatherType type = WeatherTypeUtil.getWeatherType(weatherType);
        String backgroundKey;
        
        // 根据分类的天气类型选择背景
        switch (type) 
        {
            case SUNNY:
                backgroundKey = "晴";
                break;
            case CLOUDY:
                backgroundKey = "多云";
                break;
            case OVERCAST:
                backgroundKey = "阴";
                break;
            case RAINY:
                backgroundKey = "小雨";
                break;
            case OTHER:
            default:
                // 尝试直接用原始文本匹配，如果没有则使用多云作为默认
                if (weatherBackgrounds.containsKey(weatherType)) 
                {
                    backgroundKey = weatherType;
                } 
                else 
                {
                    backgroundKey = "多云";  // 默认背景
                }
                break;
        }
        
        Integer backgroundResId = weatherBackgrounds.get(backgroundKey);
        if (backgroundResId != null) 
        {
            mainLayout.setBackgroundResource(backgroundResId);
            
            // 更新当前天气类型
            currentWeatherType = weatherType;
            
            Log.d(TAG, "已更新天气背景为: " + backgroundKey);
        }
    }
    
    /**
     * 初始化定时更新
     */
    private void initPeriodicUpdates() 
    {
        weatherUpdateHandler = new Handler(Looper.getMainLooper());
        airQualityUpdateHandler = new Handler(Looper.getMainLooper());
        rainForecastUpdateHandler = new Handler(Looper.getMainLooper());
        
        // 天气更新任务
        weatherUpdateRunnable = new Runnable() 
        {
            @Override
            public void run() 
            {
                Log.d(TAG, "执行天气定时更新");
                if (currentCityId != null && !currentCityId.isEmpty()) 
                {
                    getWeatherInfo(currentCityId);
                    getWeatherForecast(currentCityId);
                }
                weatherUpdateHandler.postDelayed(this, WEATHER_UPDATE_INTERVAL);
            }
        };
        
        // 空气质量更新任务
        airQualityUpdateRunnable = new Runnable() 
        {
            @Override
            public void run() 
            {
                Log.d(TAG, "执行空气质量定时更新");
                if (latitude != 0 && longitude != 0) 
                {
                    getAirQualityInfo(latitude, longitude);
                }
                airQualityUpdateHandler.postDelayed(this, AIR_QUALITY_UPDATE_INTERVAL);
            }
        };
        
        // 降水预报更新任务
        rainForecastUpdateRunnable = new Runnable() 
        {
            @Override
            public void run() 
            {
                Log.d(TAG, "执行降水预报定时更新");
                if (latitude != 0 && longitude != 0) 
                {
                    getRainForecast(latitude, longitude);
                }
                rainForecastUpdateHandler.postDelayed(this, RAIN_FORECAST_UPDATE_INTERVAL);
            }
        };
    }
    
    /**
     * 开始定时更新
     */
    private void startPeriodicUpdates() 
    {
        // 停止可能已存在的更新
        stopPeriodicUpdates();
        
        // 启动新的定时更新
        weatherUpdateHandler.postDelayed(weatherUpdateRunnable, WEATHER_UPDATE_INTERVAL);
        airQualityUpdateHandler.postDelayed(airQualityUpdateRunnable, AIR_QUALITY_UPDATE_INTERVAL);
        rainForecastUpdateHandler.postDelayed(rainForecastUpdateRunnable, RAIN_FORECAST_UPDATE_INTERVAL);
        
        Log.d(TAG, "已启动定时更新任务");
    }
    
    /**
     * 停止定时更新
     */
    private void stopPeriodicUpdates() 
    {
        weatherUpdateHandler.removeCallbacks(weatherUpdateRunnable);
        airQualityUpdateHandler.removeCallbacks(airQualityUpdateRunnable);
        rainForecastUpdateHandler.removeCallbacks(rainForecastUpdateRunnable);
        
        Log.d(TAG, "已停止定时更新任务");
    }
    
    @Override
    protected void onResume() 
    {
        super.onResume();
        
        // 开始定时更新
        startPeriodicUpdates();
        
        // 更新提醒计划
        updateTodoReminders();
        
        // 如果已有位置信息，则直接获取天气
        if (latitude != 0 && longitude != 0) 
        {
            getCityInfo(latitude, longitude);
            getAirQualityInfo(latitude, longitude);
            getRainForecast(latitude, longitude);
            get24HourWeatherForecast(latitude, longitude);
        } 
        else 
        {
            // 否则，尝试获取位置
            getLocation();
        }
    }
    
    @Override
    protected void onPause() 
    {
        super.onPause();
        
        // 当应用进入后台时，停止定时更新以节省资源
        stopPeriodicUpdates();
    }
    
    /**
     * 初始化位置服务
     */
    private void initLocationService() 
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        // 检查位置权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != 
                PackageManager.PERMISSION_GRANTED && 
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != 
                PackageManager.PERMISSION_GRANTED) 
        {
            // 请求位置权限
            ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } 
        else 
        {
            // 已有权限，开始获取位置
            getLocation();
        }
    }
    
    /**
     * 获取位置
     */
    private void getLocation() 
    {
        // 检查权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != 
                PackageManager.PERMISSION_GRANTED && 
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != 
                PackageManager.PERMISSION_GRANTED) 
        {
            return;
        }
        
        // 显示正在定位
        tvCityName.setText("正在获取位置...");
        
        // 检查哪些位置提供者可用
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        
        // 先尝试网络定位（更省电）
        if (isNetworkEnabled) 
        {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000,    // 最小时间间隔（毫秒）
                    10,      // 最小距离变化（米）
                    locationListener);
            
            // 尝试获取最后已知位置
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lastKnownLocation != null) 
            {
                latitude = lastKnownLocation.getLatitude();
                longitude = lastKnownLocation.getLongitude();
                
                Log.d(TAG, "使用最后已知网络位置 - 纬度: " + latitude + ", 经度: " + longitude);
                
                // 保存位置信息
                LocationPreferences.saveLocation(this, latitude, longitude);
                
                // 获取城市信息
                getCityInfo(latitude, longitude);
                
                // 获取空气质量
                getAirQualityInfo(latitude, longitude);
                
                // 获取降水预报
                getRainForecast(latitude, longitude);
            }
        }
        
        // 如果网络定位不可用，尝试GPS定位
        else if (isGPSEnabled) 
        {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,    // 最小时间间隔（毫秒）
                    10,      // 最小距离变化（米）
                    locationListener);
            
            // 尝试获取最后已知位置
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) 
            {
                latitude = lastKnownLocation.getLatitude();
                longitude = lastKnownLocation.getLongitude();
                
                Log.d(TAG, "使用最后已知GPS位置 - 纬度: " + latitude + ", 经度: " + longitude);
                
                // 保存位置信息
                LocationPreferences.saveLocation(this, latitude, longitude);
                
                // 获取城市信息
                getCityInfo(latitude, longitude);
                
                // 获取空气质量
                getAirQualityInfo(latitude, longitude);
                
                // 获取降水预报
                getRainForecast(latitude, longitude);
            }
        }
        else 
        {
            tvCityName.setText("无法获取位置");
            Toast.makeText(this, "请开启位置服务", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 通过经纬度获取城市信息
     */
    private void getCityInfo(double latitude, double longitude) 
    {
        // 获取WeatherService实例
        WeatherService weatherService = WeatherService.getInstance(this);
        
        // 调用城市查询API
        weatherService.getCityInfo(latitude, longitude, new WeatherCallback<CityInfo>() 
        {
            @Override
            public void onSuccess(CityInfo cityInfo) 
            {
                runOnUiThread(() -> {
                    if (!cityInfo.hasError()) 
                    {
                        // 显示城市名称（行政区 + 城市名格式）
                        tvCityName.setText(cityInfo.getDisplayName());
                        Log.d(TAG, "城市信息: " + 
                                ((!cityInfo.adm2.isEmpty() && !cityInfo.adm2.equals(cityInfo.name)) ? 
                                    (cityInfo.adm2 + " " + cityInfo.name) : 
                                        ((!cityInfo.adm1.isEmpty() && !cityInfo.adm1.equals(cityInfo.name)) ? 
                                            (cityInfo.adm1 + " " + cityInfo.name) : cityInfo.name)) + 
                                " (ID: " + cityInfo.id + ")");
                        
                        // 保存城市ID
                        currentCityId = cityInfo.id;
                        
                        // 获取天气信息
                        if (!currentCityId.isEmpty()) 
                        {
                            getWeatherInfo(currentCityId);
                            
                            // 获取3天天气预报
                            getWeatherForecast(currentCityId);
                        }
                        
                        // 获取空气质量信息
                        getAirQualityInfo(latitude, longitude);
                        
                        // 获取降水预报
                        getRainForecast(latitude, longitude);
                        
                        // 获取到位置和城市信息后，启动定时更新
                        startPeriodicUpdates();
                    } 
                    else 
                    {
                        // 显示错误
                        tvCityName.setText("位置: " + latitude + ", " + longitude);
                        Log.e(TAG, "获取城市信息失败: " + cityInfo.error);
                    }
                });
            }

            @Override
            public void onError(String message) 
            {
                runOnUiThread(() -> {
                    tvCityName.setText("位置: " + latitude + ", " + longitude);
                    Log.e(TAG, "获取城市信息API错误: " + message);
                });
            }
        });
    }
    
    /**
     * 获取天气信息
     * 
     * @param cityId 城市ID
     */
    private void getWeatherInfo(String cityId) 
    {
        // 显示正在获取天气信息
        tvWeatherType.setText("正在获取天气...");
        
        // 获取WeatherService实例
        WeatherService weatherService = WeatherService.getInstance(this);
        
        // 调用天气查询API
        weatherService.getWeatherNow(cityId, new WeatherCallback<WeatherInfo>() 
        {
            @Override
            public void onSuccess(WeatherInfo weatherInfo) 
            {
                runOnUiThread(() -> {
                    if (!weatherInfo.hasError()) 
                    {
                        // 更新UI
                        updateWeatherUI(weatherInfo);
                    } 
                    else 
                    {
                        // 显示错误
                        tvWeatherType.setText("获取天气失败");
                        Log.e(TAG, "获取天气信息失败: " + weatherInfo.error);
                        Toast.makeText(MainActivity.this, "获取天气失败: " + weatherInfo.error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String message) 
            {
                runOnUiThread(() -> {
                    tvWeatherType.setText("获取天气失败");
                    Log.e(TAG, "获取天气信息API错误: " + message);
                    Toast.makeText(MainActivity.this, "获取天气失败，请稍后重试", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    /**
     * 获取空气质量信息
     */
    private void getAirQualityInfo(double latitude, double longitude) 
    {
        // 获取WeatherService实例
        WeatherService weatherService = WeatherService.getInstance(this);
        
        // 调用空气质量查询API
        weatherService.getAirQuality(latitude, longitude, new WeatherCallback<AirQualityInfo>() 
        {
            @Override
            public void onSuccess(AirQualityInfo airInfo) 
            {
                runOnUiThread(() -> {
                    if (!airInfo.hasError()) 
                    {
                        // 更新UI
                        updateAirQualityUI(airInfo);
                    } 
                    else 
                    {
                        // 显示错误
                        tvAirQuality.setText("空气质量数据获取失败");
                        Log.e(TAG, "获取空气质量信息失败: " + airInfo.error);
                    }
                });
            }

            @Override
            public void onError(String message) 
            {
                runOnUiThread(() -> {
                    tvAirQuality.setText("空气质量数据获取失败");
                    Log.e(TAG, "获取空气质量API错误: " + message);
                });
            }
        });
    }
    
    /**
     * 更新天气UI
     * 
     * @param weatherInfo 天气信息对象
     */
    private void updateWeatherUI(WeatherInfo weatherInfo) 
    {
        // 更新天气类型
        tvWeatherType.setText(weatherInfo.text);
        currentWeatherType = weatherInfo.text;
        
        // 更新背景
        updateBackgroundByWeather(weatherInfo.text);
        
        // 更新温度
        tvTemperature.setText(weatherInfo.getTemperature());
        
        // 更新更新时间
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm+08:00", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try 
        {
            Date date = inputFormat.parse(weatherInfo.obsTime);
            if (date != null) 
            {
                String formattedTime = outputFormat.format(date);
                tvUpdateTime.setText(formattedTime + " 更新");
            } 
            else 
            {
                updateCurrentTime();
            }
        } 
        catch (Exception e) 
        {
            // 时间解析失败，使用当前时间
            updateCurrentTime();
        }
        
        // 更新天气描述
        updateWeatherDescription(weatherInfo);
    }
    
    /**
     * 根据天气情况更新天气描述
     * 
     * @param weatherInfo 天气信息对象
     */
    private void updateWeatherDescription(WeatherInfo weatherInfo) 
    {
        StringBuilder description = new StringBuilder();
        
        // 基本天气描述
        description.append("当前");
        description.append(weatherInfo.text);
        
        // 添加温度信息
        description.append("，体感");
        description.append(weatherInfo.getFeelsLikeTemperature());
        
        // 添加风速信息
        if (!weatherInfo.windDir.isEmpty() && !weatherInfo.windScale.isEmpty()) 
        {
            description.append("，");
            description.append(weatherInfo.windDir);
            description.append(weatherInfo.windScale);
            description.append("级");
        }
        
        // 添加湿度信息
        if (!weatherInfo.humidity.isEmpty()) 
        {
            description.append("，湿度");
            description.append(weatherInfo.humidity);
            description.append("%");
        }
        
        // 设置描述文本
        tvWeatherDescription.setText(description.toString());
    }
    
    /**
     * 更新空气质量UI
     */
    private void updateAirQualityUI(AirQualityInfo airInfo) 
    {
        // 显示空气质量等级和指数
        StringBuilder airQualityText = new StringBuilder();
        
        if (!airInfo.category.isEmpty()) 
        {
            airQualityText.append("空气");
            airQualityText.append(airInfo.category);
            
            if (!airInfo.aqi.isEmpty()) 
            {
                airQualityText.append(" ");
                airQualityText.append(airInfo.aqi);
            }
        } 
        else if (!airInfo.aqi.isEmpty()) 
        {
            airQualityText.append("AQI ");
            airQualityText.append(airInfo.aqi);
        }
        else if (!airInfo.aqiDisplay.isEmpty()) 
        {
            airQualityText.append(airInfo.aqiDisplay);
        }
        else 
        {
            airQualityText.append("AQI数据获取中");
        }
        
        tvAirQuality.setText(airQualityText);
        
        Log.d(TAG, "空气质量数据已更新: " + airInfo.toString());
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) 
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) 
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) 
            {
                // 权限获取成功，开始获取位置
                getLocation();
            } 
            else 
            {
                // 权限被拒绝
                tvCityName.setText("无法获取位置");
                Toast.makeText(this, "需要位置权限来获取当前位置", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    /**
     * 初始化UI组件
     */
    private void initUI() 
    {
        tvCityName = findViewById(R.id.tvCityName);
        tvWeatherType = findViewById(R.id.tvWeatherType);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvTemperatureRange = findViewById(R.id.tvTemperatureRange);
        tvWeatherDescription = findViewById(R.id.tvWeatherDescription);
        tvUpdateTime = findViewById(R.id.tvUpdateTime);
        tvAirQuality = findViewById(R.id.tvAirQuality);
        rvForecast = findViewById(R.id.rvForecast);
        mainLayout = findViewById(R.id.main);
        
        // 初始化降水预报UI组件
        rainForecastView = findViewById(R.id.rainForecastView);
        if (rainForecastView != null) 
        {
            ivRainIcon = rainForecastView.findViewById(R.id.ivRainIcon);
            tvRainTitle = rainForecastView.findViewById(R.id.tvRainTitle);
            tvRainForecast = rainForecastView.findViewById(R.id.tvRainForecast);
            progressRain = rainForecastView.findViewById(R.id.progressRain);
            
            // 默认隐藏降水预报视图，直到获取到数据
            rainForecastView.setVisibility(View.GONE);
        }
        
        // 底部导航栏
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(this);
    }
    
    /**
     * 更新当前时间
     */
    private void updateCurrentTime() 
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        currentTime = dateFormat.format(new Date());
        tvUpdateTime.setText(currentTime + " 更新");
    }

    /**
     * 初始化天气预报RecyclerView
     */
    private void initForecastRecyclerView() 
    {
        // 创建空适配器
        forecastAdapter = new ForecastAdapter(this, new ArrayList<>());
        
        // 设置垂直布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvForecast.setLayoutManager(layoutManager);
        
        rvForecast.setAdapter(forecastAdapter);
    }

    /**
     * 获取天气预报
     */
    private void getWeatherForecast(String cityId) 
    {
        // 获取WeatherService实例
        WeatherService weatherService = WeatherService.getInstance(this);
        
        // 调用天气预报API
        weatherService.getWeatherForecast3Days(cityId, new WeatherCallback<WeatherForecastInfo>() 
        {
            @Override
            public void onSuccess(WeatherForecastInfo forecastInfo) 
            {
                runOnUiThread(() -> {
                    if (!forecastInfo.hasError()) 
                    {
                        // 保存天气预报数据
                        weatherForecastInfo = forecastInfo;
                        
                        // 更新UI显示3天预报
                        updateForecastUI(forecastInfo);
                    } 
                    else 
                    {
                        Log.e(TAG, "获取天气预报失败: " + forecastInfo.error);
                        Toast.makeText(MainActivity.this, "获取天气预报失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String message) 
            {
                runOnUiThread(() -> {
                    Log.e(TAG, "获取天气预报API错误: " + message);
                    Toast.makeText(MainActivity.this, "获取天气预报失败，请稍后重试", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    /**
     * 更新天气预报UI
     */
    private void updateForecastUI(WeatherForecastInfo forecastInfo) 
    {
        if (forecastInfo != null && !forecastInfo.dailyForecasts.isEmpty()) 
        {
            // 获取未来3天的预报
            List<WeatherForecastInfo.DailyForecast> threeDayForecast = forecastInfo.getForecasts(3);
            
            // 更新天气预报适配器
            forecastAdapter.updateData(threeDayForecast);
            
            // 显示预报列表
            rvForecast.setVisibility(View.VISIBLE);
            
            // 如果有第一天的预报，更新温度范围
            if (!threeDayForecast.isEmpty()) 
            {
                WeatherForecastInfo.DailyForecast today = threeDayForecast.get(0);
                tvTemperatureRange.setText(today.getTemperatureRange());
            }
            
            Log.d(TAG, "天气预报UI已更新：显示" + threeDayForecast.size() + "天预报");
        } 
        else 
        {
            // 无预报数据，隐藏预报列表
            rvForecast.setVisibility(View.GONE);
            Log.d(TAG, "无天气预报数据可显示");
        }
    }

    /**
     * 获取当前位置的分钟级降水预报
     */
    private void getRainForecast(double latitude, double longitude) 
    {
        Log.d(TAG, "获取降水预报 - 经度: " + longitude + ", 纬度: " + latitude);
        
        WeatherService.getInstance(this).getMinutelyRainForecast(latitude, longitude, 
            new WeatherCallback<MinutelyRainInfo>() 
            {
                @Override
                public void onSuccess(MinutelyRainInfo rainInfo) 
                {
                    if (rainInfo != null) 
                    {
                        Log.d(TAG, "获取降水预报成功: " + rainInfo.summary);
                        runOnUiThread(() -> {
                            updateRainForecastUI(rainInfo);
                        });
                    }
                }

                @Override
                public void onError(String message) 
                {
                    Log.e(TAG, "获取降水预报失败: " + message);
                    runOnUiThread(() -> {
                        showToast("获取降水预报失败: " + message);
                        
                        // 隐藏降水预报视图
                        if (rainForecastView != null) 
                        {
                            rainForecastView.setVisibility(View.GONE);
                        }
                    });
                }
            });
    }
    
    /**
     * 更新降水预报UI
     */
    private void updateRainForecastUI(MinutelyRainInfo rainInfo) 
    {
        if (rainForecastView == null || rainInfo == null) return;
        
        // 显示降水预报视图
        rainForecastView.setVisibility(View.VISIBLE);
        
        // 更新降水预报文本
        tvRainForecast.setText(rainInfo.getRainForecastDescription());
        
        // 设置图标和标题
        if (rainInfo.willRainIn2Hours()) 
        {
            // 有降水预报，显示雨滴图标
            ivRainIcon.setImageResource(R.drawable.ic_rain);
            tvRainTitle.setText("即将降水");
        } 
        else 
        {
            // 无降水预报，显示晴天图标
            ivRainIcon.setImageResource(R.drawable.ic_rain);
            ivRainIcon.setColorFilter(getResources().getColor(R.color.colorAccentLight));
            tvRainTitle.setText("未来无降水");
        }
        
        // 始终隐藏进度条，不论是否有降水
        progressRain.setVisibility(View.GONE);
    }

    /**
     * 处理位置更新事件
     */
    private final LocationListener locationListener = new LocationListener() 
    {
        @Override
        public void onLocationChanged(@NonNull Location location) 
        {
            Log.d(TAG, "位置已更新 - 经度: " + location.getLongitude() + ", 纬度: " + location.getLatitude());
            
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            
            // 保存位置信息
            LocationPreferences.saveLocation(MainActivity.this, latitude, longitude);
            
            // 停止位置更新以节省电量
            if (locationManager != null) 
            {
                locationManager.removeUpdates(this);
            }
            
            // 获取城市信息
            getCityInfo(latitude, longitude);
            
            // 获取空气质量
            getAirQualityInfo(latitude, longitude);
            
            // 获取降水预报
            getRainForecast(latitude, longitude);
        }
        
        // ... other LocationListener methods ...
    };

    /**
     * 显示Toast消息
     */
    private void showToast(String message) 
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) 
    {
        int itemId = item.getItemId();
        
        if (itemId == R.id.nav_weather) 
        {
            // 已经在天气界面，无需操作
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
            // 跳转到清单界面
            Intent intent = new Intent(this, TodoListActivity.class);
            // 传递当前天气类型
            intent.putExtra("current_weather_type", currentWeatherType);
            startActivity(intent);
            finish();
            return true;
        }
        
        return false;
    }

    /**
     * 获取24小时天气预报
     * 
     * @param latitude 纬度
     * @param longitude 经度
     */
    private void get24HourWeatherForecast(double latitude, double longitude) 
    {
        // 显示加载进度
        // 如果需要，可以在这里添加进度条显示
        
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
                
                // 更新提醒计划
                updateTodoReminders();
            }
            
            @Override
            public void onError(String message) 
            {
                Log.e(TAG, "获取24小时天气预报出错: " + message);
            }
        });
    }
    
    /**
     * 更新待办事项提醒
     * 获取当天的待办事项并重新安排提醒
     */
    private void updateTodoReminders() 
    {
        TodoManager.getInstance(this).getTodosForToday(new TodoManager.TodoCallback<List<Todo>>() 
        {
            @Override
            public void onSuccess(List<Todo> todoList) 
            {
                if (todoList == null || todoList.isEmpty()) 
                {
                    Log.d(TAG, "今天没有待办事项需要提醒");
                    return;
                }
                
                Log.d(TAG, "找到 " + todoList.size() + " 个今天的待办事项，重新安排提醒");
                
                // 创建提醒调度器
                AlarmScheduler alarmScheduler = new AlarmScheduler(MainActivity.this);
                
                // 先取消所有提醒，然后重新安排
                alarmScheduler.cancelAll(todoList);
                alarmScheduler.scheduleTodos(todoList);
                
                Log.d(TAG, "已更新今天的待办事项提醒");
            }
            
            @Override
            public void onError(String errorMsg) 
            {
                Log.e(TAG, "获取今天的待办事项失败: " + errorMsg);
            }
        });
    }
}