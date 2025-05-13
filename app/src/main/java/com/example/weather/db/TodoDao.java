package com.example.weather.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.weather.model.Todo;

import java.util.List;

/**
 * 待办事项数据访问对象
 */
@Dao
public interface TodoDao 
{
    /**
     * 插入一个新的待办事项
     * @param todo 待办事项
     * @return 插入后的ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Todo todo);

    /**
     * 批量插入待办事项
     * @param todos 待办事项列表
     * @return 插入后的ID列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<Todo> todos);

    /**
     * 更新待办事项
     * @param todo 待办事项
     * @return 更新的行数
     */
    @Update
    int update(Todo todo);

    /**
     * 删除待办事项
     * @param todo 待办事项
     * @return 删除的行数
     */
    @Delete
    int delete(Todo todo);

    /**
     * 根据ID删除待办事项
     * @param todoId 待办事项ID
     * @return 删除的行数
     */
    @Query("DELETE FROM todos WHERE id = :todoId")
    int deleteById(long todoId);

    /**
     * 获取所有待办事项，按创建时间降序排列
     * @return 待办事项列表
     */
    @Query("SELECT * FROM todos ORDER BY createdAt DESC")
    List<Todo> getAll();

    /**
     * 获取所有未完成的待办事项，按创建时间降序排列
     * @return 未完成的待办事项列表
     */
    @Query("SELECT * FROM todos WHERE completed = 0 ORDER BY createdAt DESC")
    List<Todo> getIncomplete();

    /**
     * 获取所有已完成的待办事项，按完成时间降序排列
     * @return 已完成的待办事项列表
     */
    @Query("SELECT * FROM todos WHERE completed = 1 ORDER BY updatedAt DESC")
    List<Todo> getCompleted();

    /**
     * 根据ID获取待办事项
     * @param todoId 待办事项ID
     * @return 待办事项
     */
    @Query("SELECT * FROM todos WHERE id = :todoId")
    Todo getById(long todoId);

    /**
     * 根据天气类型获取相关待办事项
     * @param weatherType 天气类型
     * @return 相关待办事项列表
     */
    @Query("SELECT * FROM todos WHERE weatherTypes LIKE '%' || :weatherType || '%' AND completed = 0")
    List<Todo> getByWeatherType(String weatherType);

    /**
     * 根据空气质量获取相关待办事项
     * @param airQuality 空气质量
     * @return 相关待办事项列表
     */
    @Query("SELECT * FROM todos WHERE airQuality = :airQuality AND completed = 0")
    List<Todo> getByAirQuality(String airQuality);

    /**
     * 根据星期几获取相关待办事项（SQL不能直接查询数组，实际查询需要在Java中进行）
     * @return 所有未完成的待办事项
     */
    @Query("SELECT * FROM todos WHERE completed = 0")
    List<Todo> getAllForDayOfWeekFiltering();

    /**
     * 删除所有已完成的待办事项
     * @return 删除的行数
     */
    @Query("DELETE FROM todos WHERE completed = 1")
    int deleteAllCompleted();
} 