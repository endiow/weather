package com.example.weather.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.model.WeatherType;

import java.util.ArrayList;
import java.util.List;

/**
 * 天气类型选择适配器
 */
public class WeatherTypeAdapter extends RecyclerView.Adapter<WeatherTypeAdapter.WeatherTypeViewHolder> 
{
    private List<WeatherType> weatherTypes;
    
    public WeatherTypeAdapter(List<WeatherType> weatherTypes) 
    {
        this.weatherTypes = weatherTypes;
    }
    
    @NonNull
    @Override
    public WeatherTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) 
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weather_type, parent, false);
        return new WeatherTypeViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull WeatherTypeViewHolder holder, int position) 
    {
        WeatherType weatherType = weatherTypes.get(position);
        
        // 设置天气类型名称和图标
        holder.tvWeatherTypeName.setText(weatherType.getName());
        holder.ivWeatherTypeIcon.setImageResource(weatherType.getIconResourceId());
        
        // 设置选中状态
        holder.updateSelectedState(weatherType.isSelected());
        
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> 
        {
            // 直接切换当前项的选中状态，支持多选
            weatherType.setSelected(!weatherType.isSelected());
            
            // 通知适配器刷新
            notifyItemChanged(position);
        });
    }
    
    @Override
    public int getItemCount() 
    {
        return weatherTypes.size();
    }
    
    /**
     * 获取当前选中的天气类型列表
     */
    public List<WeatherType> getSelectedWeatherTypes() 
    {
        List<WeatherType> selectedTypes = new ArrayList<>();
        for (WeatherType weatherType : weatherTypes) 
        {
            if (weatherType.isSelected()) 
            {
                selectedTypes.add(weatherType);
            }
        }
        return selectedTypes;
    }
    
    /**
     * 获取所有选中的天气类型名称
     */
    public List<String> getSelectedWeatherTypeNames() 
    {
        List<String> selectedNames = new ArrayList<>();
        for (WeatherType weatherType : weatherTypes) 
        {
            if (weatherType.isSelected()) 
            {
                selectedNames.add(weatherType.getName());
            }
        }
        return selectedNames;
    }
    
    /**
     * 天气类型ViewHolder
     */
    class WeatherTypeViewHolder extends RecyclerView.ViewHolder 
    {
        private ImageView ivWeatherTypeIcon;
        private TextView tvWeatherTypeName;
        private CardView cardView;
        
        public WeatherTypeViewHolder(@NonNull View itemView) 
        {
            super(itemView);
            ivWeatherTypeIcon = itemView.findViewById(R.id.ivWeatherTypeIcon);
            tvWeatherTypeName = itemView.findViewById(R.id.tvWeatherTypeName);
            cardView = itemView.findViewById(R.id.cardWeatherType);
        }
        
        /**
         * 更新选中状态的UI
         */
        public void updateSelectedState(boolean isSelected) 
        {
            // 根据是否选中修改卡片样式
            if (isSelected) 
            {
                cardView.setCardBackgroundColor(itemView.getContext().getResources().getColor(R.color.selected_bg));
                tvWeatherTypeName.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
            } 
            else 
            {
                cardView.setCardBackgroundColor(itemView.getContext().getResources().getColor(R.color.white));
                tvWeatherTypeName.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
            }
        }
    }
}