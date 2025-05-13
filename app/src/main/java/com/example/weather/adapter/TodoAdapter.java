package com.example.weather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.model.Todo;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * 待办事项列表适配器
 */
public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> 
{
    private final Context context;
    private final List<Todo> todoList;
    private final SimpleDateFormat timeFormat;
    
    /**
     * 构造函数
     * @param context 上下文
     * @param todoList 待办事项列表
     */
    public TodoAdapter(Context context, List<Todo> todoList) 
    {
        this.context = context;
        this.todoList = todoList;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) 
    {
        View view = LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) 
    {
        Todo todo = todoList.get(position);
        
        // 设置标题
        holder.tvTitle.setText(todo.getTitle());
        
        // 设置描述
        if (todo.getDescription() != null && !todo.getDescription().isEmpty()) 
        {
            holder.tvDescription.setText(todo.getDescription());
            holder.tvDescription.setVisibility(View.VISIBLE);
        } 
        else 
        {
            holder.tvDescription.setVisibility(View.GONE);
        }
        
        // 设置时间范围
        StringBuilder timeRange = new StringBuilder();
        if (todo.getStartTime() != null && todo.getEndTime() != null) 
        {
            timeRange.append(timeFormat.format(todo.getStartTime()))
                    .append(" - ")
                    .append(timeFormat.format(todo.getEndTime()));
        } 
        else if (todo.getStartTime() != null) 
        {
            timeRange.append("从 ").append(timeFormat.format(todo.getStartTime()));
        } 
        else if (todo.getEndTime() != null) 
        {
            timeRange.append("到 ").append(timeFormat.format(todo.getEndTime()));
        }
        
        if (timeRange.length() > 0) 
        {
            holder.tvTime.setText(timeRange.toString());
            holder.tvTime.setVisibility(View.VISIBLE);
        } 
        else 
        {
            holder.tvTime.setVisibility(View.GONE);
        }
        
        // 设置天气条件
        StringBuilder conditions = new StringBuilder();
        if (todo.getWeatherType() != null && !todo.getWeatherType().isEmpty()) 
        {
            conditions.append(todo.getWeatherType());
        }
        
        if (todo.getAirQuality() != null && !todo.getAirQuality().isEmpty()) 
        {
            if (conditions.length() > 0) conditions.append("，");
            conditions.append("空气").append(todo.getAirQuality());
        }
        
        if (todo.getHumidity() != null && !todo.getHumidity().isEmpty()) 
        {
            if (conditions.length() > 0) conditions.append("，");
            conditions.append("湿度").append(todo.getHumidity());
        }
        
        if (conditions.length() > 0) 
        {
            holder.tvConditions.setText(conditions.toString());
            holder.tvConditions.setVisibility(View.VISIBLE);
        } 
        else 
        {
            holder.tvConditions.setVisibility(View.GONE);
        }
        
        // 设置完成状态
        holder.cbCompleted.setChecked(todo.isCompleted());
        
        // 设置点击事件：切换完成状态
        holder.cbCompleted.setOnClickListener(v -> {
            todo.setCompleted(holder.cbCompleted.isChecked());
            // 在实际应用中，这里应该调用TodoManager更新数据库
        });
    }
    
    @Override
    public int getItemCount() 
    {
        return todoList.size();
    }
    
    /**
     * 待办事项列表ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder 
    {
        final TextView tvTitle;
        final TextView tvDescription;
        final TextView tvTime;
        final TextView tvConditions;
        final CheckBox cbCompleted;
        
        ViewHolder(@NonNull View itemView) 
        {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTodoTitle);
            tvDescription = itemView.findViewById(R.id.tvTodoDescription);
            tvTime = itemView.findViewById(R.id.tvTodoTimeRange);
            tvConditions = itemView.findViewById(R.id.tvTodoWeatherType);
            cbCompleted = itemView.findViewById(R.id.cbTodoComplete);
        }
    }
} 