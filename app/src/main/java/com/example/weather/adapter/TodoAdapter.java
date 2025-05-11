package com.example.weather.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.model.Todo;

import java.util.ArrayList;
import java.util.List;

/**
 * 待办事项RecyclerView适配器
 */
public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> 
{
    private List<Todo> todoList;
    private OnTodoClickListener listener;
    
    /**
     * 待办事项点击监听器接口
     */
    public interface OnTodoClickListener 
    {
        void onTodoClick(Todo todo, int position);
        void onTodoCompleteChanged(Todo todo, int position, boolean isCompleted);
    }
    
    public TodoAdapter() 
    {
        this.todoList = new ArrayList<>();
    }
    
    public TodoAdapter(List<Todo> todoList) 
    {
        this.todoList = todoList;
    }
    
    public void setOnTodoClickListener(OnTodoClickListener listener) 
    {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) 
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) 
    {
        Todo todo = todoList.get(position);
        
        // 设置数据
        holder.tvTitle.setText(todo.getTitle());
        holder.tvDescription.setText(todo.getDescription());
        holder.tvWeatherType.setText(todo.getWeatherType());
        
        // 添加时间段显示
        String timeInfo = todo.getTimeRangeDisplay();
        holder.tvTimeRange.setText(timeInfo);
        holder.tvTimeRange.setVisibility(View.VISIBLE);
        
        holder.cbComplete.setChecked(todo.isCompleted());
        
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTodoClick(todo, position);
            }
        });
        
        // 设置完成状态变更监听
        holder.cbComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed() && listener != null) {
                todo.setCompleted(isChecked);
                listener.onTodoCompleteChanged(todo, position, isChecked);
            }
        });
    }
    
    @Override
    public int getItemCount() 
    {
        return todoList.size();
    }
    
    /**
     * 更新数据列表
     * @param newTodoList 新的待办事项列表
     */
    public void updateTodoList(List<Todo> newTodoList) 
    {
        this.todoList = newTodoList;
        notifyDataSetChanged();
    }
    
    /**
     * 添加单个待办事项
     * @param todo 待添加的待办事项
     */
    public void addTodo(Todo todo) 
    {
        this.todoList.add(todo);
        notifyItemInserted(todoList.size() - 1);
    }
    
    /**
     * 根据当前天气过滤待办事项
     * @param allTodos 所有待办事项
     * @param currentWeather 当前天气类型
     * @return 与天气匹配的待办事项列表
     */
    public static List<Todo> filterByWeather(List<Todo> allTodos, String currentWeather) 
    {
        List<Todo> filteredList = new ArrayList<>();
        for (Todo todo : allTodos) {
            if (todo.matchesWeather(currentWeather)) {
                filteredList.add(todo);
            }
        }
        return filteredList;
    }
    
    /**
     * 待办事项ViewHolder
     */
    public static class TodoViewHolder extends RecyclerView.ViewHolder 
    {
        TextView tvTitle;
        TextView tvDescription;
        TextView tvWeatherType;
        TextView tvTimeRange;
        CheckBox cbComplete;
        
        public TodoViewHolder(@NonNull View itemView) 
        {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTodoTitle);
            tvDescription = itemView.findViewById(R.id.tvTodoDescription);
            tvWeatherType = itemView.findViewById(R.id.tvTodoWeatherType);
            tvTimeRange = itemView.findViewById(R.id.tvTodoTimeRange);
            cbComplete = itemView.findViewById(R.id.cbTodoComplete);
        }
    }
} 