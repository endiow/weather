package com.example.weather.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.model.DayOfWeek;

import java.util.ArrayList;
import java.util.List;

/**
 * 星期几选择适配器
 */
public class DayOfWeekAdapter extends RecyclerView.Adapter<DayOfWeekAdapter.DayOfWeekViewHolder> 
{
    private List<DayOfWeek> daysOfWeek;
    
    public DayOfWeekAdapter(List<DayOfWeek> daysOfWeek) 
    {
        this.daysOfWeek = daysOfWeek;
    }
    
    @NonNull
    @Override
    public DayOfWeekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) 
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day_of_week, parent, false);
        return new DayOfWeekViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull DayOfWeekViewHolder holder, int position) 
    {
        DayOfWeek dayOfWeek = daysOfWeek.get(position);
        
        // 设置星期几名称
        holder.tvDayOfWeek.setText(dayOfWeek.getName());
        
        // 设置选中状态
        holder.updateSelectedState(dayOfWeek.isSelected());
        
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> 
        {
            // 更新选中状态
            dayOfWeek.setSelected(!dayOfWeek.isSelected());
            
            // 更新UI
            holder.updateSelectedState(dayOfWeek.isSelected());
        });
    }
    
    @Override
    public int getItemCount() 
    {
        return daysOfWeek.size();
    }
    
    /**
     * 获取所有选中的星期几
     */
    public List<String> getSelectedDayNames() 
    {
        List<String> selectedDayNames = new ArrayList<>();
        for (DayOfWeek dayOfWeek : daysOfWeek) 
        {
            if (dayOfWeek.isSelected()) 
            {
                selectedDayNames.add(dayOfWeek.getName());
            }
        }
        return selectedDayNames;
    }
    
    /**
     * 星期几ViewHolder
     */
    class DayOfWeekViewHolder extends RecyclerView.ViewHolder 
    {
        private TextView tvDayOfWeek;
        private CardView cardView;
        
        public DayOfWeekViewHolder(@NonNull View itemView) 
        {
            super(itemView);
            tvDayOfWeek = itemView.findViewById(R.id.tvDayOfWeek);
            cardView = itemView.findViewById(R.id.cardDayOfWeek);
        }
        
        /**
         * 更新选中状态的UI
         */
        public void updateSelectedState(boolean isSelected) 
        {
            // 根据是否选中修改卡片样式
            if (isSelected) 
            {
                tvDayOfWeek.setBackgroundResource(R.drawable.circle_day_bg);
                tvDayOfWeek.setSelected(true);
                tvDayOfWeek.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
            } 
            else 
            {
                tvDayOfWeek.setBackgroundResource(R.drawable.circle_day_bg);
                tvDayOfWeek.setSelected(false);
                tvDayOfWeek.setTextColor(itemView.getContext().getResources().getColor(R.color.text_primary));
            }
        }
    }
} 