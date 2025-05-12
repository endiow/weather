package com.example.weather.api;

import android.content.Context;
import android.util.Log;

import com.qweather.sdk.Callback;
import com.qweather.sdk.JWTGenerator;
import com.qweather.sdk.QWeather;
import com.qweather.sdk.basic.Lang;
import com.qweather.sdk.basic.Unit;
import com.qweather.sdk.parameter.air.AirParameter;
import com.qweather.sdk.parameter.geo.GeoCityLookupParameter;
import com.qweather.sdk.parameter.weather.WeatherParameter;
import com.qweather.sdk.response.air.AirNowResponse;
import com.qweather.sdk.response.error.ErrorResponse;
import com.qweather.sdk.response.geo.GeoCityLookupResponse;
import com.qweather.sdk.response.weather.WeatherDailyResponse;
import com.qweather.sdk.response.weather.WeatherNowResponse;

import java.lang.reflect.Method;

/**
 * 和风天气服务类
 * 用于与和风天气API交互获取天气数据
 */
public class WeatherService 
{
    private static final String TAG = "WeatherService";
    private static final String STATUS_OK = "200";
    
    // 私钥和认证信息（注意：实际应用中应该从配置文件或安全存储中获取）
    private static final String PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n" +
            "MC4CAQAwBQYDK2VwBCIEIJpJnhXIHSsR8NYubIzCQ5ZUkC8u72PCJkWB4kruKtGy\n" +
            "-----END PRIVATE KEY-----";
    private static final String PUBLIC_ID = "2E882PQGPD";
    private static final String USER_ID = "KHPN4BJ7N2";
    
    // 和风天气API服务器地址
    private static final String SERVER_URL = "k7436g4haq.re.qweatherapi.com";
    
    private QWeather qWeather;
    private static WeatherService instance;
    private boolean isInitialized = false;

    /**
     * 获取WeatherService单例实例
     * 
     * @param context 应用上下文
     * @return WeatherService实例
     */
    public static synchronized WeatherService getInstance(Context context) 
    {
        if (instance == null) 
        {
            instance = new WeatherService(context);
        }
        
        // 如果初始化失败，尝试重新初始化
        if (instance != null && !instance.isInitialized) 
        {
            instance.initQWeather(context);
        }
        
        return instance;
    }

    /**
     * 私有构造方法
     * 
     * @param context 应用上下文
     */
    private WeatherService(Context context) 
    {
        initQWeather(context);
    }
    
    /**
     * 初始化和风天气SDK
     * 
     * @param context 应用上下文
     */
    private void initQWeather(Context context) 
    {
        try 
        {
            // 使用JWT认证方式初始化QWeather
            JWTGenerator jwt = new JWTGenerator(PRIVATE_KEY, PUBLIC_ID, USER_ID);
            
            // 初始化SDK实例
            qWeather = QWeather.getInstance(context, SERVER_URL)
                    .setTokenGenerator(jwt)
                    .setLogEnable(true);
                    
            isInitialized = true;
            Log.d(TAG, "和风天气SDK初始化成功");
        } 
        catch (Throwable e) 
        {
            Log.e(TAG, "初始化和风天气SDK失败，请检查JWT认证参数", e);
            isInitialized = false;
        }
        
        // 如果初始化失败，确保调用者知道，不要让应用崩溃
        if (!isInitialized) 
        {
            Log.e(TAG, "和风天气SDK初始化失败，应用可能无法获取天气数据");
        }
    }
    
    /**
     * 检查SDK是否已初始化
     * 
     * @param callback 回调接口
     * @return 是否继续执行
     */
    private boolean checkInitialized(WeatherCallback<?> callback) 
    {
        if (!isInitialized || qWeather == null) 
        {
            Log.e(TAG, "和风天气SDK未初始化");
            if (callback != null) 
            {
                callback.onError("天气服务未初始化");
            }
            return false;
        }
        return true;
    }

    /**
     * 获取实时天气数据
     * 
     * @param location 位置坐标，格式：经度,纬度 或 城市ID
     * @param callback 回调接口
     */
    public void getWeatherNow(String location, final WeatherCallback<WeatherNowResponse> callback) 
    {
        if (!checkInitialized(callback)) return;
        
        try 
        {
            WeatherParameter parameter = new WeatherParameter(location)
                    .lang(Lang.ZH_HANS)
                    .unit(Unit.METRIC);
    
            qWeather.weatherNow(parameter, new Callback<WeatherNowResponse>() 
            {
                @Override
                public void onSuccess(WeatherNowResponse response) 
                {
                    Log.i(TAG, "获取实时天气成功: " + response.toString());
                    callback.onSuccess(response);
                }
    
                @Override
                public void onFailure(ErrorResponse errorResponse) 
                {
                    String errorMsg = "错误: " + errorResponse.toString();
                    Log.i(TAG, errorMsg);
                    callback.onError(errorMsg);
                }
    
                @Override
                public void onException(Throwable e) 
                {
                    e.printStackTrace();
                    String errorMsg = "网络异常: " + e.getMessage();
                    Log.e(TAG, errorMsg, e);
                    callback.onError(errorMsg);
                }
            });
        } 
        catch (Exception e) 
        {
            String errorMsg = "发送天气请求异常: " + e.getMessage();
            Log.e(TAG, errorMsg, e);
            callback.onError(errorMsg);
        }
    }

    /**
     * 获取三日天气预报
     * 
     * @param location 位置坐标，格式：经度,纬度 或 城市ID
     * @param callback 回调接口
     */
    public void getWeather3d(String location, final WeatherCallback<WeatherDailyResponse> callback) 
    {
        if (!checkInitialized(callback)) return;
        
        try 
        {
            WeatherParameter parameter = new WeatherParameter(location)
                    .lang(Lang.ZH_HANS)
                    .unit(Unit.METRIC);
    
            qWeather.weather3d(parameter, new Callback<WeatherDailyResponse>() 
            {
                @Override
                public void onSuccess(WeatherDailyResponse response) 
                {
                    Log.i(TAG, "获取三日天气预报成功: " + response.toString());
                    callback.onSuccess(response);
                }
    
                @Override
                public void onFailure(ErrorResponse errorResponse) 
                {
                    String errorMsg = "错误: " + errorResponse.toString();
                    Log.i(TAG, errorMsg);
                    callback.onError(errorMsg);
                }
    
                @Override
                public void onException(Throwable e) 
                {
                    e.printStackTrace();
                    String errorMsg = "网络异常: " + e.getMessage();
                    Log.e(TAG, errorMsg, e);
                    callback.onError(errorMsg);
                }
            });
        } 
        catch (Exception e) 
        {
            String errorMsg = "发送天气预报请求异常: " + e.getMessage();
            Log.e(TAG, errorMsg, e);
            callback.onError(errorMsg);
        }
    }

    /**
     * 获取空气质量数据
     * 
     * @param location 位置坐标，格式：经度,纬度 或 城市ID
     * @param callback 回调接口
     */
    public void getAirNow(String location, final WeatherCallback<AirNowResponse> callback) 
    {
        if (!checkInitialized(callback)) return;
        
        try 
        {
            AirParameter parameter = new AirParameter(location)
                    .lang(Lang.ZH_HANS);
    
            qWeather.airNow(parameter, new Callback<AirNowResponse>() 
            {
                @Override
                public void onSuccess(AirNowResponse response) 
                {
                    Log.i(TAG, "获取空气质量成功: " + response.toString());
                    callback.onSuccess(response);
                }
    
                @Override
                public void onFailure(ErrorResponse errorResponse) 
                {
                    String errorMsg = "错误: " + errorResponse.toString();
                    Log.i(TAG, errorMsg);
                    callback.onError(errorMsg);
                }
    
                @Override
                public void onException(Throwable e) 
                {
                    e.printStackTrace();
                    String errorMsg = "网络异常: " + e.getMessage();
                    Log.e(TAG, errorMsg, e);
                    callback.onError(errorMsg);
                }
            });
        } 
        catch (Exception e) 
        {
            String errorMsg = "发送空气质量请求异常: " + e.getMessage();
            Log.e(TAG, errorMsg, e);
            callback.onError(errorMsg);
        }
    }
    
    /**
     * 根据经纬度获取城市名称
     * 
     * @param location 位置坐标，格式：经度,纬度
     * @param callback 回调接口
     */
    public void getCityName(String location, final WeatherCallback<GeoCityLookupResponse> callback) 
    {
        if (!checkInitialized(callback)) return;
        
        try 
        {
            GeoCityLookupParameter parameter = new GeoCityLookupParameter(location)
                    .lang(Lang.ZH_HANS);
    
            qWeather.geoCityLookup(parameter, new Callback<GeoCityLookupResponse>() 
            {
                @Override
                public void onSuccess(GeoCityLookupResponse response) 
                {
                    Log.i(TAG, "获取城市信息成功: " + response.toString());
                    callback.onSuccess(response);
                }
    
                @Override
                public void onFailure(ErrorResponse errorResponse) 
                {
                    String errorMsg = "错误: " + errorResponse.toString();
                    Log.i(TAG, errorMsg);
                    callback.onError(errorMsg);
                }
    
                @Override
                public void onException(Throwable e) 
                {
                    e.printStackTrace();
                    String errorMsg = "网络异常: " + e.getMessage();
                    Log.e(TAG, errorMsg, e);
                    callback.onError(errorMsg);
                }
            });
        } 
        catch (Exception e) 
        {
            String errorMsg = "发送城市查询请求异常: " + e.getMessage();
            Log.e(TAG, errorMsg, e);
            callback.onError(errorMsg);
        }
    }
    
    /**
     * 解析城市信息响应
     * 
     * @param response GeoCityLookupResponse响应对象
     * @return 城市信息对象
     */
    public CityInfo parseCityInfo(GeoCityLookupResponse response) 
    {
        CityInfo cityInfo = new CityInfo();
        
        try 
        {
            // 确保响应有效且有数据
            if (response.getCode().equals(STATUS_OK) && response.getLocation() != null && !response.getLocation().isEmpty()) 
            {
                // 获取第一个城市结果
                Object locationObj = response.getLocation().get(0);
                
                try 
                {
                    // 尝试使用反射获取字段值
                    Method getNameMethod = locationObj.getClass().getMethod("getName");
                    Method getAdm1Method = locationObj.getClass().getMethod("getAdm1");
                    Method getAdm2Method = locationObj.getClass().getMethod("getAdm2");
                    Method getCountryMethod = locationObj.getClass().getMethod("getCountry");
                    Method getLatMethod = locationObj.getClass().getMethod("getLat");
                    Method getLonMethod = locationObj.getClass().getMethod("getLon");
                    
                    cityInfo.name = (String) getNameMethod.invoke(locationObj);
                    cityInfo.adm1 = (String) getAdm1Method.invoke(locationObj);
                    cityInfo.adm2 = (String) getAdm2Method.invoke(locationObj);
                    cityInfo.country = (String) getCountryMethod.invoke(locationObj);
                    cityInfo.lat = (String) getLatMethod.invoke(locationObj);
                    cityInfo.lon = (String) getLonMethod.invoke(locationObj);
                } 
                catch (Exception e) 
                {
                    Log.e(TAG, "反射获取位置信息失败: " + e.getMessage());
                    
                    // 如果反射失败，尝试从toString()解析
                    String locationStr = locationObj.toString();
                    Log.d(TAG, "位置对象字符串: " + locationStr);
                    
                    // 简单解析字符串以获取所需字段
                    if (locationStr.contains("name=")) {
                        cityInfo.name = extractField(locationStr, "name=", ",");
                    }
                    if (locationStr.contains("adm1=")) {
                        cityInfo.adm1 = extractField(locationStr, "adm1=", ",");
                    }
                    if (locationStr.contains("adm2=")) {
                        cityInfo.adm2 = extractField(locationStr, "adm2=", ",");
                    }
                    if (locationStr.contains("country=")) {
                        cityInfo.country = extractField(locationStr, "country=", ",");
                    }
                    if (locationStr.contains("lat=")) {
                        cityInfo.lat = extractField(locationStr, "lat=", ",");
                    }
                    if (locationStr.contains("lon=")) {
                        cityInfo.lon = extractField(locationStr, "lon=", ",");
                    }
                }
                
                // 构建显示名称
                cityInfo.displayName = cityInfo.name;
                if (cityInfo.adm2 != null && !cityInfo.adm2.isEmpty() && !cityInfo.adm2.equals(cityInfo.name)) 
                {
                    cityInfo.displayName = cityInfo.adm2 + " " + cityInfo.name;
                }
                
                Log.d(TAG, "解析的城市信息 - 城市: " + cityInfo.name + 
                        ", 地区: " + cityInfo.adm2 + 
                        ", 省份: " + cityInfo.adm1 + 
                        ", 国家: " + cityInfo.country +
                        ", 纬度: " + cityInfo.lat +
                        ", 经度: " + cityInfo.lon);
            }
            else 
            {
                Log.w(TAG, "和风天气API返回状态码: " + response.getCode() + "，无有效数据");
                cityInfo.error = true;
                cityInfo.errorMessage = "API返回错误或无数据: " + response.getCode();
            }
        }
        catch (Exception e) 
        {
            Log.e(TAG, "解析城市信息异常: " + e.getMessage(), e);
            cityInfo.error = true;
            cityInfo.errorMessage = "解析异常: " + e.getMessage();
        }
        
        return cityInfo;
    }
    
    /**
     * 从字符串中提取字段值
     * 
     * @param source 源字符串
     * @param startStr 起始标记
     * @param endStr 结束标记
     * @return 提取的字段值
     */
    private String extractField(String source, String startStr, String endStr) 
    {
        try 
        {
            int startIndex = source.indexOf(startStr) + startStr.length();
            if (startIndex < startStr.length()) return "";
            
            int endIndex = source.indexOf(endStr, startIndex);
            if (endIndex < 0) endIndex = source.length();
            
            return source.substring(startIndex, endIndex).trim();
        } 
        catch (Exception e) 
        {
            return "";
        }
    }

    /**
     * 城市信息类
     */
    public static class CityInfo 
    {
        public String name = "";       // 城市名称
        public String adm1 = "";       // 省份
        public String adm2 = "";       // 地区
        public String country = "";    // 国家
        public String lat = "";        // 纬度
        public String lon = "";        // 经度
        public String displayName = "当前位置";  // 显示名称
        public boolean error = false;  // 是否发生错误
        public String errorMessage = ""; // 错误信息
        
        /**
         * 获取纬度值
         * @param defaultValue 默认值
         * @return 纬度数值
         */
        public double getLatitude(double defaultValue) 
        {
            try 
            {
                return Double.parseDouble(lat);
            } 
            catch (Exception e) 
            {
                return defaultValue;
            }
        }
        
        /**
         * 获取经度值
         * @param defaultValue 默认值
         * @return 经度数值
         */
        public double getLongitude(double defaultValue) 
        {
            try 
            {
                return Double.parseDouble(lon);
            } 
            catch (Exception e) 
            {
                return defaultValue;
            }
        }
    }

    /**
     * 天气回调接口
     */
    public interface WeatherCallback<T> 
    {
        void onSuccess(T response);
        void onError(String message);
    }
} 