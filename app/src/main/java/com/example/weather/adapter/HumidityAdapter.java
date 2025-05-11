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
import com.example.weather.model.Humidity;

import java.util.ArrayList;
import java.util.List;

/**
 * 空气湿度选择适配器
 */
public class HumidityAdapter extends RecyclerView.Adapter<HumidityAdapter.HumidityViewHolder> 
{
    private List<Humidity> humidities;
    
    public HumidityAdapter(List<Humidity> humidities) 
    {
        this.humidities = humidities;
    }
    
    @NonNull
    @Override
    public HumidityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) 
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_humidity, parent, false);
        return new HumidityViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull HumidityViewHolder holder, int position) 
    {
        Humidity humidity = humidities.get(position);
        
        // 设置空气湿度名称和图标
        holder.tvHumidityName.setText(humidity.getName());
        holder.ivHumidityIcon.setImageResource(humidity.getIconResId());
        
        // 设置选中状态
        holder.updateSelectedState(humidity.isSelected());
        
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> 
        {
            // 直接切换当前项的选中状态，支持多选
            humidity.setSelected(!humidity.isSelected());
            
            // 通知适配器刷新
            notifyItemChanged(position);
        });
    }
    
    @Override
    public int getItemCount() 
    {
        return humidities.size();
    }
    
    /**
     * 获取当前选中的空气湿度列表
     */
    public List<Humidity> getSelectedHumidities() 
    {
        List<Humidity> selectedHumidities = new ArrayList<>();
        for (Humidity humidity : humidities) 
        {
            if (humidity.isSelected()) 
            {
                selectedHumidities.add(humidity);
            }
        }
        return selectedHumidities;
    }
    
    /**
     * 获取所有选中的空气湿度名称
     */
    public List<String> getSelectedHumidityNames() 
    {
        List<String> selectedNames = new ArrayList<>();
        for (Humidity humidity : humidities) 
        {
            if (humidity.isSelected()) 
            {
                selectedNames.add(humidity.getName());
            }
        }
        return selectedNames;
    }
    
    /**
     * 空气湿度ViewHolder
     */
    class HumidityViewHolder extends RecyclerView.ViewHolder 
    {
        private ImageView ivHumidityIcon;
        private TextView tvHumidityName;
        private CardView cardView;
        
        public HumidityViewHolder(@NonNull View itemView) 
        {
            super(itemView);
            ivHumidityIcon = itemView.findViewById(R.id.ivHumidityIcon);
            tvHumidityName = itemView.findViewById(R.id.tvHumidityName);
            cardView = itemView.findViewById(R.id.cardHumidity);
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
                tvHumidityName.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
            } 
            else 
            {
                cardView.setCardBackgroundColor(itemView.getContext().getResources().getColor(R.color.white));
                tvHumidityName.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
            }
        }
    }
} 