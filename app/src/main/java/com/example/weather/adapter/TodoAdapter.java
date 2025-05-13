package com.example.weather.adapter;

import android.content.Context;
import android.graphics.Color;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.TodoListActivity;
import com.example.weather.manager.TodoManager;
import com.example.weather.model.Todo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 待办事项列表适配器
 */
public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> 
{
    private final Context context;
    private final List<Todo> todoList;
    private final SimpleDateFormat timeFormat;
    private TodoManager todoManager;
    private boolean isTodayTodos; // 是否是今日事项
    private boolean isDeleteMode = false; // 是否处于删除模式
    private Set<Integer> selectedItems = new HashSet<>(); // 已选中项目的位置集合
    
    /**
     * 构造函数
     * @param context 上下文
     * @param todoList 待办事项列表
     */
    public TodoAdapter(Context context, List<Todo> todoList) 
    {
        this(context, todoList, false);
    }
    
    /**
     * 构造函数
     * @param context 上下文
     * @param todoList 待办事项列表
     * @param isTodayTodos 是否是今日事项
     */
    public TodoAdapter(Context context, List<Todo> todoList, boolean isTodayTodos) 
    {
        this.context = context;
        this.todoList = todoList;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        this.todoManager = TodoManager.getInstance(context);
        this.isTodayTodos = isTodayTodos;
    }
    
    /**
     * 设置是否为今日事项
     * @param isTodayTodos 是否是今日事项
     */
    public void setTodayTodos(boolean isTodayTodos) 
    {
        this.isTodayTodos = isTodayTodos;
    }
    
    /**
     * 切换删除模式
     * @param deleteMode 是否进入删除模式
     */
    public void setDeleteMode(boolean deleteMode) 
    {
        if (this.isDeleteMode != deleteMode) 
        {
            this.isDeleteMode = deleteMode;
            // 清空选中项
            if (!deleteMode) 
            {
                selectedItems.clear();
            }
            notifyDataSetChanged();
        }
    }
    
    /**
     * 获取当前是否处于删除模式
     */
    public boolean isDeleteMode() 
    {
        return isDeleteMode;
    }
    
    /**
     * 获取已选中的待办事项
     */
    public List<Todo> getSelectedTodos() 
    {
        List<Todo> selected = new ArrayList<>();
        for (Integer position : selectedItems) 
        {
            if (position < todoList.size()) 
            {
                selected.add(todoList.get(position));
            }
        }
        return selected;
    }
    
    /**
     * 全选或全不选
     */
    public void selectAll(boolean isSelected) 
    {
        selectedItems.clear();
        if (isSelected) 
        {
            for (int i = 0; i < todoList.size(); i++) 
            {
                selectedItems.add(i);
            }
        }
        notifyDataSetChanged();
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
        List<String> weatherTypes = todo.getWeatherTypes();
        if (weatherTypes != null && !weatherTypes.isEmpty()) 
        {
            holder.tvConditions.setText(TextUtils.join("、", weatherTypes));
            holder.tvConditions.setVisibility(View.VISIBLE);
        } 
        else 
        {
            holder.tvConditions.setVisibility(View.GONE);
        }
        
        // 设置完成状态
        holder.cbCompleted.setChecked(todo.isCompleted());
        
        // 设置删除复选框颜色为绿色
        int greenColor = context.getResources().getColor(android.R.color.holo_green_dark);
        holder.cbDelete.setButtonTintList(ColorStateList.valueOf(greenColor));
        
        // 设置删除模式下的UI
        if (isDeleteMode) 
        {
            // 删除模式：显示左侧删除复选框，隐藏右侧完成状态复选框
            holder.cbDelete.setVisibility(View.VISIBLE);
            holder.cbDelete.setChecked(selectedItems.contains(position));
            holder.cbCompleted.setVisibility(View.GONE);
        } 
        else 
        {
            // 正常模式：隐藏左侧删除复选框，显示右侧完成状态复选框
            holder.cbDelete.setVisibility(View.GONE);
            holder.cbCompleted.setVisibility(View.VISIBLE);
        }
        
        // 确保每个项目有统一的最小高度，但保持较扁平的外观
        float density = context.getResources().getDisplayMetrics().density;
        holder.itemView.setMinimumHeight((int)(80 * density));
        
        // 设置点击事件：切换完成状态并刷新列表
        holder.cbCompleted.setOnClickListener(v -> {
            if (!isDeleteMode) {
                todo.setCompleted(holder.cbCompleted.isChecked());
                
                // 更新数据库
                todoManager.updateTodo(todo, new TodoManager.TodoCallback<Boolean>() 
                {
                    @Override
                    public void onSuccess(Boolean result) 
                    {
                        // 更新成功，通知Activity刷新列表以重新排序
                        if (context instanceof TodoListActivity && result) 
                        {
                            ((TodoListActivity) context).refreshTodoList();
                        }
                    }

                    @Override
                    public void onError(String errorMsg) 
                    {
                        // 更新失败，恢复UI状态
                        holder.cbCompleted.setChecked(!holder.cbCompleted.isChecked());
                    }
                });
            }
        });
        
        // 设置删除复选框点击事件
        holder.cbDelete.setOnClickListener(v -> {
            final int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                if (holder.cbDelete.isChecked()) {
                    selectedItems.add(pos);
                } else {
                    selectedItems.remove(pos);
                }
                
                // 通知Activity更新删除按钮状态
                if (context instanceof TodoListActivity) {
                    ((TodoListActivity) context).updateDeleteButtonState();
                }
            }
        });
        
        // 设置项目点击事件（用于在删除模式下快速选择）
        holder.itemView.setOnClickListener(v -> {
            if (isDeleteMode) {
                final int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    boolean newState = !holder.cbDelete.isChecked();
                    holder.cbDelete.setChecked(newState);
                    
                    if (newState) {
                        selectedItems.add(pos);
                    } else {
                        selectedItems.remove(pos);
                    }
                    
                    // 通知Activity更新删除按钮状态
                    if (context instanceof TodoListActivity) {
                        ((TodoListActivity) context).updateDeleteButtonState();
                    }
                }
            }
        });
    }
    
    @Override
    public int getItemCount() 
    {
        return todoList.size();
    }
    
    /**
     * 更新数据
     * @param newTodoList 新的待办事项列表
     */
    public void updateData(List<Todo> newTodoList) 
    {
        todoList.clear();
        if (newTodoList != null) 
        {
            todoList.addAll(newTodoList);
        }
        selectedItems.clear(); // 清空选中项
        notifyDataSetChanged();
    }
    
    /**
     * 删除选中的项目
     */
    public void deleteSelectedItems() 
    {
        if (selectedItems.size() > 0 && context instanceof TodoListActivity) 
        {
            List<Todo> toDelete = getSelectedTodos();
            ((TodoListActivity) context).deleteTodos(toDelete);
        }
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
        final CheckBox cbDelete;
        
        ViewHolder(@NonNull View itemView) 
        {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTodoTitle);
            tvDescription = itemView.findViewById(R.id.tvTodoDescription);
            tvTime = itemView.findViewById(R.id.tvTodoTimeRange);
            tvConditions = itemView.findViewById(R.id.tvTodoWeatherType);
            cbCompleted = itemView.findViewById(R.id.cbTodoComplete);
            cbDelete = itemView.findViewById(R.id.cbDelete);
        }
    }
} 