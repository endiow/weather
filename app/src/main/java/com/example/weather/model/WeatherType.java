package com.example.weather.model;

/**
 * 天气类型数据模型
 */
public class WeatherType 
{
    private String name;
    private int iconResourceId;
    private boolean isSelected;
    
    public WeatherType(String name, int iconResourceId, boolean isSelected) 
    {
        this.name = name;
        this.iconResourceId = iconResourceId;
        this.isSelected = isSelected;
    }
    
    public String getName() 
    {
        return name;
    }
    
    public void setName(String name) 
    {
        this.name = name;
    }
    
    public int getIconResourceId() 
    {
        return iconResourceId;
    }
    
    public void setIconResourceId(int iconResourceId) 
    {
        this.iconResourceId = iconResourceId;
    }
    
    public boolean isSelected() 
    {
        return isSelected;
    }
    
    public void setSelected(boolean selected) 
    {
        isSelected = selected;
    }
} 