package com.example.weather.model;

/**
 * 空气质量数据模型
 */
public class AirQuality 
{
    private String name;
    private int iconResId;
    private boolean selected;
    
    public AirQuality(String name, int iconResId, boolean selected) 
    {
        this.name = name;
        this.iconResId = iconResId;
        this.selected = selected;
    }
    
    public String getName() 
    {
        return name;
    }
    
    public void setName(String name) 
    {
        this.name = name;
    }
    
    public int getIconResId() 
    {
        return iconResId;
    }
    
    public void setIconResId(int iconResId) 
    {
        this.iconResId = iconResId;
    }
    
    public boolean isSelected() 
    {
        return selected;
    }
    
    public void setSelected(boolean selected) 
    {
        this.selected = selected;
    }
} 