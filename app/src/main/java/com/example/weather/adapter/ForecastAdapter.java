package com.example.weather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.api.WeatherForecastInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 天气预报适配器
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> 
{
    private List<WeatherForecastInfo.DailyForecast> forecasts;
    private Context context;
    
    // 天气图标映射表
    private Map<String, Integer> weatherIconMap;
    
    public ForecastAdapter(Context context, List<WeatherForecastInfo.DailyForecast> forecasts) 
    {
        this.context = context;
        this.forecasts = forecasts;
        initWeatherIconMap();
    }
    
    /**
     * 初始化天气图标映射
     */
    private void initWeatherIconMap() 
    {
        weatherIconMap = new HashMap<>();
        
        // 添加常见天气类型对应的图标
        weatherIconMap.put("晴", R.drawable.ic_sunny);
        weatherIconMap.put("多云", R.drawable.ic_cloudy);
        weatherIconMap.put("阴", R.drawable.ic_cloudy);
        weatherIconMap.put("小雨", R.drawable.ic_rainy);
        weatherIconMap.put("中雨", R.drawable.ic_rainy);
        weatherIconMap.put("大雨", R.drawable.ic_rainy);
        weatherIconMap.put("暴雨", R.drawable.ic_rainy);
        weatherIconMap.put("小雪", R.drawable.ic_snowy);
        weatherIconMap.put("中雪", R.drawable.ic_snowy);
        weatherIconMap.put("大雪", R.drawable.ic_snowy);
        weatherIconMap.put("暴雪", R.drawable.ic_snowy);
        weatherIconMap.put("雷阵雨", R.drawable.ic_thunder);
        weatherIconMap.put("雾", R.drawable.ic_foggy);
    }
    
    /**
     * 获取天气图标资源ID
     */
    private int getWeatherIconResource(String weatherDesc) 
    {
        if (weatherIconMap.containsKey(weatherDesc)) 
        {
            return weatherIconMap.get(weatherDesc);
        }
        
        // 默认图标
        return R.drawable.ic_any_weather;
    }
    
    /**
     * 格式化日期为"今天/明天/周几"的形式
     */
    private String formatDateToDay(String dateStr) 
    {
        try 
        {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date forecastDate = inputFormat.parse(dateStr);
            if (forecastDate == null) 
            {
                return dateStr;
            }
            
            Calendar forecastCal = Calendar.getInstance();
            forecastCal.setTime(forecastDate);
            
            Calendar todayCal = Calendar.getInstance();
            
            // 判断是否是今天
            if (isSameDay(forecastCal, todayCal)) 
            {
                return "今天";
            }
            
            // 判断是否是明天
            Calendar tomorrowCal = Calendar.getInstance();
            tomorrowCal.add(Calendar.DAY_OF_YEAR, 1);
            if (isSameDay(forecastCal, tomorrowCal)) 
            {
                return "明天";
            }
            
            // 其他日期显示为"周几"
            String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
            int dayOfWeek = forecastCal.get(Calendar.DAY_OF_WEEK) - 1;
            if (dayOfWeek < 0) 
            {
                dayOfWeek = 0;
            }
            
            return weekDays[dayOfWeek];
        } 
        catch (ParseException e) 
        {
            e.printStackTrace();
            return dateStr;
        }
    }
    
    /**
     * 判断两个日历对象是否是同一天
     */
    private boolean isSameDay(Calendar cal1, Calendar cal2) 
    {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) 
    {
        View view = LayoutInflater.from(context).inflate(R.layout.item_forecast_day, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) 
    {
        WeatherForecastInfo.DailyForecast forecast = forecasts.get(position);
        
        // 设置日期（今天/明天/周几）
        holder.tvDate.setText(formatDateToDay(forecast.fxDate));
        
        // 设置天气描述
        holder.tvWeatherDesc.setText(forecast.textDay);
        
        // 设置最低温和最高温
        String minTemp = forecast.tempMin + "°";
        String maxTemp = forecast.tempMax + "°";
        
        holder.tvMinTemp.setText(minTemp);
        holder.tvMaxTemp.setText(maxTemp);
        
        // 设置天气图标
        holder.ivWeatherIcon.setImageResource(getWeatherIconResource(forecast.textDay));
    }

    @Override
    public int getItemCount() 
    {
        return forecasts != null ? forecasts.size() : 0;
    }
    
    /**
     * 更新数据
     */
    public void updateData(List<WeatherForecastInfo.DailyForecast> forecasts) 
    {
        this.forecasts = forecasts;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder类
     */
    public static class ForecastViewHolder extends RecyclerView.ViewHolder 
    {
        TextView tvDate;
        TextView tvWeatherDesc;
        TextView tvMinTemp;
        TextView tvMaxTemp;
        ImageView ivWeatherIcon;
        
        public ForecastViewHolder(@NonNull View itemView) 
        {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvForecastDate);
            tvWeatherDesc = itemView.findViewById(R.id.tvWeatherDesc);
            tvMinTemp = itemView.findViewById(R.id.tvMinTemp);
            tvMaxTemp = itemView.findViewById(R.id.tvMaxTemp);
            ivWeatherIcon = itemView.findViewById(R.id.ivWeatherIcon);
        }
    }
} 