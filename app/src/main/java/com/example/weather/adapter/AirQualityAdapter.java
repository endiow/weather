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
import com.example.weather.model.AirQuality;

import java.util.ArrayList;
import java.util.List;

/**
 * 空气质量选择适配器
 */
public class AirQualityAdapter extends RecyclerView.Adapter<AirQualityAdapter.AirQualityViewHolder> 
{
    private List<AirQuality> airQualities;
    
    public AirQualityAdapter(List<AirQuality> airQualities) 
    {
        this.airQualities = airQualities;
    }
    
    @NonNull
    @Override
    public AirQualityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) 
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_air_quality, parent, false);
        return new AirQualityViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AirQualityViewHolder holder, int position) 
    {
        AirQuality airQuality = airQualities.get(position);
        
        // 设置空气质量名称和图标
        holder.tvAirQualityName.setText(airQuality.getName());
        holder.ivAirQualityIcon.setImageResource(airQuality.getIconResId());
        
        // 设置选中状态
        holder.updateSelectedState(airQuality.isSelected());
        
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> 
        {
            // 直接切换当前项的选中状态，支持多选
            airQuality.setSelected(!airQuality.isSelected());
            
            // 通知适配器刷新
            notifyItemChanged(position);
        });
    }
    
    @Override
    public int getItemCount() 
    {
        return airQualities.size();
    }
    
    /**
     * 获取当前选中的空气质量列表
     */
    public List<AirQuality> getSelectedAirQualities() 
    {
        List<AirQuality> selectedQualities = new ArrayList<>();
        for (AirQuality airQuality : airQualities) 
        {
            if (airQuality.isSelected()) 
            {
                selectedQualities.add(airQuality);
            }
        }
        return selectedQualities;
    }
    
    /**
     * 获取所有选中的空气质量名称
     */
    public List<String> getSelectedAirQualityNames() 
    {
        List<String> selectedNames = new ArrayList<>();
        for (AirQuality airQuality : airQualities) 
        {
            if (airQuality.isSelected()) 
            {
                selectedNames.add(airQuality.getName());
            }
        }
        return selectedNames;
    }
    
    /**
     * 空气质量ViewHolder
     */
    class AirQualityViewHolder extends RecyclerView.ViewHolder 
    {
        private ImageView ivAirQualityIcon;
        private TextView tvAirQualityName;
        private CardView cardView;
        
        public AirQualityViewHolder(@NonNull View itemView) 
        {
            super(itemView);
            ivAirQualityIcon = itemView.findViewById(R.id.ivAirQualityIcon);
            tvAirQualityName = itemView.findViewById(R.id.tvAirQualityName);
            cardView = itemView.findViewById(R.id.cardAirQuality);
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
                tvAirQualityName.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
            } 
            else 
            {
                cardView.setCardBackgroundColor(itemView.getContext().getResources().getColor(R.color.white));
                tvAirQualityName.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
            }
        }
    }
} 