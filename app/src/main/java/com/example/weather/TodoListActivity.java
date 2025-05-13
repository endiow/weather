package com.example.weather;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.adapter.TodoAdapter;
import com.example.weather.manager.TodoManager;
import com.example.weather.model.Todo;
import com.example.weather.util.TodoComparator;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TodoListActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener
{
    private RecyclerView rvTodayTodos;
    private RecyclerView rvOtherTodos;
    private TodoAdapter todayTodoAdapter;
    private TodoAdapter otherTodoAdapter;
    private BottomNavigationView bottomNavigationView;
    private TodoManager todoManager;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private TextView tvTodayEmpty;
    private TextView tvOtherEmpty;
    private CardView cardTodayTodos;
    private CardView cardOtherTodos;
    private ImageButton btnDelete;
    private String currentWeatherType = "晴"; // 默认值，实际应从MainActivity获取
    private boolean isDeleteMode = false; // 是否处于删除模式

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        // 获取当前天气类型
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("current_weather_type")) 
        {
            currentWeatherType = intent.getStringExtra("current_weather_type");
        }
        
        // 初始化视图
        initViews();
        
        // 加载待办事项列表
        loadTodoList();
        
        // 设置底部导航栏选中项
        bottomNavigationView.setSelectedItemId(R.id.nav_todo_list);
    }
    
    private void initViews() 
    {
        // 获取主要组件
        rvTodayTodos = findViewById(R.id.rvTodayTodos);
        rvOtherTodos = findViewById(R.id.rvOtherTodos);
        cardTodayTodos = findViewById(R.id.cardTodayTodos);
        cardOtherTodos = findViewById(R.id.cardOtherTodos);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvTodayEmpty = findViewById(R.id.tvTodayEmpty);
        tvOtherEmpty = findViewById(R.id.tvOtherEmpty);
        btnDelete = findViewById(R.id.btnDelete);
        
        // 设置RecyclerView布局管理器
        rvTodayTodos.setLayoutManager(new LinearLayoutManager(this));
        rvOtherTodos.setLayoutManager(new LinearLayoutManager(this));
        
        // 底部导航
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(this);
        
        // 初始化待办事项管理器
        todoManager = TodoManager.getInstance(this);
        
        // 初始化适配器，设置今日事项标志
        todayTodoAdapter = new TodoAdapter(this, new ArrayList<>(), true);
        otherTodoAdapter = new TodoAdapter(this, new ArrayList<>(), false);
        
        rvTodayTodos.setAdapter(todayTodoAdapter);
        rvOtherTodos.setAdapter(otherTodoAdapter);
        
        // 设置删除按钮点击事件
        btnDelete.setOnClickListener(v -> {
            if (isDeleteMode) 
            {
                // 已经处于删除模式，执行删除操作
                int todaySelected = todayTodoAdapter.getSelectedTodos().size();
                int otherSelected = otherTodoAdapter.getSelectedTodos().size();
                int totalSelected = todaySelected + otherSelected;
                
                if (totalSelected > 0) 
                {
                    // 有选中的项目，弹出确认对话框
                    new AlertDialog.Builder(this)
                            .setTitle("确认删除")
                            .setMessage("确定要删除选中的 " + totalSelected + " 个待办事项吗？")
                            .setPositiveButton("确定", (dialog, which) -> {
                                // 执行删除
                                if (todaySelected > 0) 
                                {
                                    todayTodoAdapter.deleteSelectedItems();
                                }
                                if (otherSelected > 0) 
                                {
                                    otherTodoAdapter.deleteSelectedItems();
                                }
                                // 退出删除模式
                                toggleDeleteMode();
                            })
                            .setNegativeButton("取消", null)
                            .show();
                } 
                else 
                {
                    // 没有选中项目，直接退出删除模式
                    toggleDeleteMode();
                }
            } 
            else 
            {
                // 进入删除模式
                toggleDeleteMode();
            }
        });
    }
    
    /**
     * 切换删除模式
     */
    private void toggleDeleteMode() 
    {
        isDeleteMode = !isDeleteMode;
        todayTodoAdapter.setDeleteMode(isDeleteMode);
        otherTodoAdapter.setDeleteMode(isDeleteMode);
        
        if (isDeleteMode) 
        {
            // 进入删除模式，改变图标为"完成"
            btnDelete.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            // 设置为绿色
            btnDelete.setColorFilter(getResources().getColor(android.R.color.holo_green_dark));
        } 
        else 
        {
            // 退出删除模式，恢复图标为"删除"
            btnDelete.setImageResource(R.drawable.ic_delete);
            // 保持绿色
            btnDelete.clearColorFilter(); // 使用图标本身的绿色
        }
    }
    
    /**
     * 更新删除按钮状态，根据选中的项目数量变化
     */
    public void updateDeleteButtonState() 
    {
        // 不再根据选中状态改变颜色，始终保持绿色
        if (isDeleteMode) 
        {
            int todaySelected = todayTodoAdapter.getSelectedTodos().size();
            int otherSelected = otherTodoAdapter.getSelectedTodos().size();
            int totalSelected = todaySelected + otherSelected;
            
            // 只根据选中项目数量决定是否显示不同图标，但颜色保持绿色
            if (totalSelected > 0) 
            {
                // 有选中的项目时使用确认图标
                btnDelete.setImageResource(android.R.drawable.ic_menu_send);
            } 
            else 
            {
                // 没有选中项目时使用关闭图标
                btnDelete.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            }
            // 始终保持绿色
            btnDelete.setColorFilter(getResources().getColor(android.R.color.holo_green_dark));
        }
    }
    
    /**
     * 删除待办事项
     */
    public void deleteTodos(List<Todo> todos) 
    {
        if (todos == null || todos.isEmpty()) 
        {
            return;
        }
        
        // 显示加载进度条
        progressBar.setVisibility(View.VISIBLE);
        
        // 删除所有选中的待办事项
        todoManager.deleteTodos(todos, new TodoManager.TodoCallback<Boolean>() 
        {
            @Override
            public void onSuccess(Boolean result) 
            {
                // 删除成功，刷新列表
                progressBar.setVisibility(View.GONE);
                if (result) 
                {
                    Toast.makeText(TodoListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    loadTodoList();
                } 
                else 
                {
                    Toast.makeText(TodoListActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMsg) 
            {
                // 删除失败
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TodoListActivity.this, "删除失败: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadTodoList() 
    {
        // 显示加载中
        progressBar.setVisibility(View.VISIBLE);
        cardTodayTodos.setVisibility(View.GONE);
        cardOtherTodos.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        
        // 获取待办事项列表
        todoManager.getAllTodos(new TodoManager.TodoCallback<List<Todo>>() 
        {
            @Override
            public void onSuccess(List<Todo> result) 
            {
                progressBar.setVisibility(View.GONE);
                
                if (result != null && !result.isEmpty()) 
                {
                    // 分离今天事项和其他事项
                    List<Todo> todayTodos = new ArrayList<>();
                    List<Todo> otherTodos = new ArrayList<>();
                    
                    Calendar calendar = Calendar.getInstance();
                    int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Calendar.SUNDAY是1
                    if (currentDayOfWeek == 0) 
                    {
                        currentDayOfWeek = 6; // 我们的数组使用0-6表示周一到周日，所以要转换
                    } 
                    else 
                    {
                        currentDayOfWeek--; // 调整为0-6
                    }
                    
                    for (Todo todo : result) 
                    {
                        boolean isTodayTodo = false;
                        
                        // 检查是否匹配当天
                        boolean[] daysOfWeek = todo.getDaysOfWeek();
                        if (daysOfWeek != null && daysOfWeek.length > currentDayOfWeek && 
                            daysOfWeek[currentDayOfWeek]) 
                        {
                            isTodayTodo = true;
                        }
                        
                        // 检查是否匹配当前天气
                        List<String> weatherTypes = todo.getWeatherTypes();
                        boolean weatherMatches = false;
                        if (weatherTypes != null && !weatherTypes.isEmpty() && currentWeatherType != null) 
                        {
                            for (String weatherType : weatherTypes) 
                            {
                                if (currentWeatherType.contains(weatherType) || weatherType.contains(currentWeatherType)) 
                                {
                                    weatherMatches = true;
                                    break;
                                }
                            }
                        }
                        
                        if (isTodayTodo && weatherMatches) 
                        {
                            todayTodos.add(todo);
                        } 
                        else 
                        {
                            otherTodos.add(todo);
                        }
                    }
                    
                    // 对今天的事项进行排序：先按未完成/已完成分组，再按时间排序
                    Collections.sort(todayTodos, (todo1, todo2) -> {
                        // 首先按照完成状态排序
                        if (todo1.isCompleted() != todo2.isCompleted()) 
                        {
                            return todo1.isCompleted() ? 1 : -1; // 未完成的排在前面
                        }
                        
                        // 对于完成状态相同的事项，按开始时间排序
                        Date startTime1 = todo1.getStartTime();
                        Date startTime2 = todo2.getStartTime();
                        
                        // 如果两者都有开始时间，按开始时间升序排列
                        if (startTime1 != null && startTime2 != null) 
                        {
                            return startTime1.compareTo(startTime2);
                        } 
                        // 如果只有一个有开始时间，没有开始时间的排在后面
                        else if (startTime1 != null) 
                        {
                            return -1;
                        } 
                        else if (startTime2 != null) 
                        {
                            return 1;
                        }
                        
                        // 如果都没有开始时间，按创建时间排序
                        Date createdAt1 = todo1.getCreatedAt();
                        Date createdAt2 = todo2.getCreatedAt();
                        if (createdAt1 != null && createdAt2 != null) 
                        {
                            return createdAt1.compareTo(createdAt2);
                        }
                        
                        return 0;
                    });
                    
                    // 对其他事项仅按时间排序，不考虑完成状态
                    Collections.sort(otherTodos, (todo1, todo2) -> {
                        // 按开始时间排序
                        Date startTime1 = todo1.getStartTime();
                        Date startTime2 = todo2.getStartTime();
                        
                        // 如果两者都有开始时间，按开始时间升序排列
                        if (startTime1 != null && startTime2 != null) 
                        {
                            return startTime1.compareTo(startTime2);
                        } 
                        // 如果只有一个有开始时间，没有开始时间的排在后面
                        else if (startTime1 != null) 
                        {
                            return -1;
                        } 
                        else if (startTime2 != null) 
                        {
                            return 1;
                        }
                        
                        // 如果都没有开始时间，按创建时间排序
                        Date createdAt1 = todo1.getCreatedAt();
                        Date createdAt2 = todo2.getCreatedAt();
                        if (createdAt1 != null && createdAt2 != null) 
                        {
                            return createdAt1.compareTo(createdAt2);
                        }
                        
                        return 0;
                    });
                    
                    // 更新UI
                    updateTodayTodosUI(todayTodos);
                    updateOtherTodosUI(otherTodos);
                    
                    // 显示卡片，隐藏空提示
                    tvEmpty.setVisibility(View.GONE);
                } 
                else 
                {
                    // 无待办事项
                    cardTodayTodos.setVisibility(View.GONE);
                    cardOtherTodos.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String errorMsg) 
            {
                progressBar.setVisibility(View.GONE);
                cardTodayTodos.setVisibility(View.GONE);
                cardOtherTodos.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("加载失败: " + errorMsg);
                Toast.makeText(TodoListActivity.this, "加载待办事项失败: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateTodayTodosUI(List<Todo> todayTodos) 
    {
        if (todayTodos.isEmpty()) 
        {
            tvTodayEmpty.setVisibility(View.VISIBLE);
            rvTodayTodos.setVisibility(View.GONE);
        } 
        else 
        {
            tvTodayEmpty.setVisibility(View.GONE);
            rvTodayTodos.setVisibility(View.VISIBLE);
            todayTodoAdapter.updateData(todayTodos);
            
            // 动态计算并设置RecyclerView的高度
            rvTodayTodos.post(() -> {
                if (todayTodoAdapter.getItemCount() > 0) {
                    // 获取或估算每个待办事项的高度
                    int itemHeight = calculateItemHeight(rvTodayTodos);
                    
                    // 计算RecyclerView的总高度 = 项目数量 × 单个项目高度
                    int totalHeight = itemHeight * todayTodoAdapter.getItemCount();
                    
                    // 设置高度
                    android.view.ViewGroup.LayoutParams params = rvTodayTodos.getLayoutParams();
                    params.height = totalHeight;
                    rvTodayTodos.setLayoutParams(params);
                }
            });
        }
        
        cardTodayTodos.setVisibility(View.VISIBLE);
    }
    
    private void updateOtherTodosUI(List<Todo> otherTodos) 
    {
        if (otherTodos.isEmpty()) 
        {
            tvOtherEmpty.setVisibility(View.VISIBLE);
            rvOtherTodos.setVisibility(View.GONE);
        } 
        else 
        {
            tvOtherEmpty.setVisibility(View.GONE);
            rvOtherTodos.setVisibility(View.VISIBLE);
            otherTodoAdapter.updateData(otherTodos);
            
            // 动态计算并设置RecyclerView的高度
            rvOtherTodos.post(() -> {
                if (otherTodoAdapter.getItemCount() > 0) {
                    // 获取或估算每个待办事项的高度
                    int itemHeight = calculateItemHeight(rvOtherTodos);
                    
                    // 计算RecyclerView的总高度 = 项目数量 × 单个项目高度
                    int totalHeight = itemHeight * otherTodoAdapter.getItemCount();
                    
                    // 设置高度
                    android.view.ViewGroup.LayoutParams params = rvOtherTodos.getLayoutParams();
                    params.height = totalHeight;
                    rvOtherTodos.setLayoutParams(params);
                }
            });
        }
        
        cardOtherTodos.setVisibility(View.VISIBLE);
    }
    
    /**
     * 计算列表项的高度
     * @param recyclerView 要计算项目高度的RecyclerView
     * @return 项目估计高度
     */
    private int calculateItemHeight(RecyclerView recyclerView) 
    {
        // 尝试测量第一个可见项目的高度
        View firstChild = recyclerView.getChildAt(0);
        if (firstChild != null) {
            // 测量获得子项实际高度
            int measuredHeight = firstChild.getHeight();
            if (measuredHeight > 0) {
                // 为每个项目添加一点额外间距
                return measuredHeight + (int)(2 * getResources().getDisplayMetrics().density);
            }
        }
        
        // 如果无法获取实际高度，使用估算值
        float density = getResources().getDisplayMetrics().density;
        return (int) (85 * density); // 较扁平的估计高度
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) 
    {
        int itemId = item.getItemId();
        
        if (itemId == R.id.nav_weather) 
        {
            // 跳转到天气界面
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
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
            // 已经在清单界面，无需操作
            return true;
        }
        
        return false;
    }
    
    @Override
    protected void onResume() 
    {
        super.onResume();
        // 每次恢复活动时刷新列表
        loadTodoList();
    }
    
    /**
     * 刷新待办事项列表
     * 在完成状态变化后调用此方法重新排序列表
     */
    public void refreshTodoList() 
    {
        loadTodoList();
    }

    /**
     * 检查待办事项是否匹配当前天气类型
     */
    private boolean matchesWeather(Todo todo) 
    {
        if (todo.getWeatherTypes() == null || todo.getWeatherTypes().isEmpty() || currentWeatherType == null) 
        {
            return false;
        }
        
        // 判断当前天气是否在待办事项的天气类型列表中
        for (String weatherType : todo.getWeatherTypes()) 
        {
            if (currentWeatherType.contains(weatherType) || weatherType.contains(currentWeatherType)) 
            {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检查待办事项是否匹配当前星期几
     */
    private boolean matchesDay(Todo todo) 
    {
        boolean[] daysOfWeek = todo.getDaysOfWeek();
        
        // 获取当前星期几
        Calendar calendar = Calendar.getInstance();
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Calendar.SUNDAY是1
        if (currentDayOfWeek == 0) 
        {
            currentDayOfWeek = 6; // 我们的数组使用0-6表示周一到周日，所以要转换
        } 
        else 
        {
            currentDayOfWeek--; // 调整为0-6
        }
        
        if (daysOfWeek == null || daysOfWeek.length <= currentDayOfWeek) 
        {
            return false;
        }
        
        return daysOfWeek[currentDayOfWeek];
    }
} 