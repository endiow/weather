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
import com.qweather.sdk.parameter.minutely.MinutelyParameter;
import com.qweather.sdk.response.air.AirNowResponse;
import com.qweather.sdk.response.error.ErrorResponse;
import com.qweather.sdk.response.geo.GeoCityLookupResponse;
import com.qweather.sdk.response.weather.WeatherDailyResponse;
import com.qweather.sdk.response.weather.WeatherNowResponse;
import com.qweather.sdk.response.weather.WeatherHourlyResponse;
import com.qweather.sdk.response.minutely.MinutelyResponse;

import java.lang.reflect.Method;
import java.util.List;

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
     * 通过经纬度获取城市信息
     * 
     * @param latitude 纬度
     * @param longitude 经度
     * @param callback 回调接口
     */
    public void getCityInfo(double latitude, double longitude, final WeatherCallback<CityInfo> callback) 
    {
        if (!checkInitialized(callback)) return;
        
        try 
        {
            // 构建格式：经度,纬度
            String location = String.format(java.util.Locale.US, "%.4f,%.4f", longitude, latitude);
            Log.d(TAG, "查询城市信息: " + location);
            
            // 创建参数，仅使用位置信息
            GeoCityLookupParameter parameter = new GeoCityLookupParameter(location);
            
            // 调用API
            qWeather.geoCityLookup(parameter, new Callback<GeoCityLookupResponse>() 
            {
                @Override
                public void onSuccess(GeoCityLookupResponse response) 
                {
                    Log.d(TAG, "获取城市信息成功: " + response.toString());
                    
                    // 创建返回数据
                    CityInfo cityInfo = new CityInfo();
                    
                    if (STATUS_OK.equals(response.getCode())) 
                    {
                        if (response.getLocation() != null && !response.getLocation().isEmpty()) 
                        {
                            // 获取第一个结果
                            Object location = response.getLocation().get(0);
                            
                            try 
                            {
                                // 通过反射读取属性
                                cityInfo.code = response.getCode();
                                
                                // 获取各个字段
                                cityInfo.name = getStringProperty(location, "getName");
                                cityInfo.id = getStringProperty(location, "getId");
                                cityInfo.lat = getStringProperty(location, "getLat");
                                cityInfo.lon = getStringProperty(location, "getLon");
                                cityInfo.adm2 = getStringProperty(location, "getAdm2");
                                cityInfo.adm1 = getStringProperty(location, "getAdm1");
                                cityInfo.country = getStringProperty(location, "getCountry");
                                
                                // 记录成功
                                Log.d(TAG, "城市信息解析成功: " + cityInfo.name + ", " + cityInfo.adm1);
                            } 
                            catch (Exception e) 
                            {
                                Log.e(TAG, "解析城市信息失败", e);
                                cityInfo.error = "解析城市数据失败: " + e.getMessage();
                            }
                        } 
                        else 
                        {
                            cityInfo.error = "未找到城市信息";
                        }
                    } 
                    else 
                    {
                        cityInfo.error = "API返回错误: " + response.getCode();
                    }
                    
                    callback.onSuccess(cityInfo);
                }

                @Override
                public void onFailure(ErrorResponse errorResponse) 
                {
                    String error = "获取城市信息失败: " + errorResponse.toString();
                    Log.e(TAG, error);
                    callback.onError(error);
                }

                @Override
                public void onException(Throwable e) 
                {
                    String error = "获取城市信息异常: " + e.getMessage();
                    Log.e(TAG, error, e);
                    callback.onError(error);
                }
            });
        } 
        catch (Exception e) 
        {
            String error = "发送城市查询请求异常: " + e.getMessage();
            Log.e(TAG, error, e);
            callback.onError(error);
        }
    }
    
    /**
     * 通过反射获取对象属性值
     */
    private String getStringProperty(Object obj, String methodName) 
    {
        try 
        {
            Method method = obj.getClass().getMethod(methodName);
            Object result = method.invoke(obj);
            return result != null ? result.toString() : "";
        } 
        catch (Exception e) 
        {
            Log.w(TAG, "获取属性失败: " + methodName);
            return "";
        }
    }
    
    /**
     * 通过经纬度获取实时空气质量信息
     * 
     * @param latitude 纬度
     * @param longitude 经度
     * @param callback 回调接口
     */
    public void getAirQuality(double latitude, double longitude, final WeatherCallback<AirQualityInfo> callback) 
    {
        if (!checkInitialized(callback)) return;
        
        try 
        {
            Log.d(TAG, "获取位置(" + latitude + "," + longitude + ")的空气质量");
            
            // 构建位置字符串: 经度,纬度
            String location = String.format(java.util.Locale.US, "%.4f,%.4f", longitude, latitude);
            
            // 创建空气质量查询参数
            AirParameter parameter = new AirParameter(location);
            
            // 调用API
            qWeather.airNow(parameter, new Callback<AirNowResponse>() 
            {
                @Override
                public void onSuccess(AirNowResponse response) 
                {
                    Log.d(TAG, "获取空气质量成功: " + response.toString());
                    
                    // 创建返回数据
                    AirQualityInfo airInfo = new AirQualityInfo();
                    
                    if (STATUS_OK.equals(response.getCode())) 
                    {
                        try 
                        {
                            // 基本信息
                            airInfo.code = response.getCode();
                            
                            // 空气质量数据
                            if (response.getNow() != null) 
                            {
                                Object now = response.getNow();
                                
                                // 通过反射获取各个字段
                                airInfo.aqi = getStringProperty(now, "getAqi");
                                airInfo.category = getStringProperty(now, "getCategory");
                                airInfo.level = getStringProperty(now, "getLevel");
                                airInfo.pollutantName = getStringProperty(now, "getPrimary");
                                
                                // 设置AQI显示文本
                                if (!airInfo.category.isEmpty()) 
                                {
                                    airInfo.aqiDisplay = airInfo.category + " " + airInfo.aqi;
                                }
                                
                                Log.d(TAG, "解析空气质量数据成功: " + airInfo.getDisplayText());
                            }
                        } 
                        catch (Exception e) 
                        {
                            Log.e(TAG, "解析空气质量数据失败", e);
                            airInfo.error = "解析空气质量数据失败: " + e.getMessage();
                        }
                    } 
                    else 
                    {
                        airInfo.error = "API返回错误: " + response.getCode();
                    }
                    
                    callback.onSuccess(airInfo);
                }

                @Override
                public void onFailure(ErrorResponse errorResponse) 
                {
                    String error = "获取空气质量失败: " + errorResponse.toString();
                    Log.e(TAG, error);
                    callback.onError(error);
                }

                @Override
                public void onException(Throwable e) 
                {
                    String error = "获取空气质量异常: " + e.getMessage();
                    Log.e(TAG, error, e);
                    callback.onError(error);
                }
            });
        } 
        catch (Exception e) 
        {
            String error = "发送空气质量请求异常: " + e.getMessage();
            Log.e(TAG, error, e);
            callback.onError(error);
        }
    }
    
    /**
     * 通过城市ID获取实时天气信息
     * 
     * @param cityId 城市ID
     * @param callback 回调接口
     */
    public void getWeatherNow(String cityId, final WeatherCallback<WeatherInfo> callback) 
    {
        if (!checkInitialized(callback)) return;
        
        try 
        {
            Log.d(TAG, "获取城市" + cityId + "的实时天气");
            
            // 创建天气查询参数
            WeatherParameter parameter = new WeatherParameter(cityId);
            
            // 调用API
            qWeather.weatherNow(parameter, new Callback<WeatherNowResponse>() 
            {
                @Override
                public void onSuccess(WeatherNowResponse response) 
                {
                    Log.d(TAG, "获取天气成功: " + response.toString());
                    
                    // 创建返回数据
                    WeatherInfo weatherInfo = new WeatherInfo();
                    
                    if (STATUS_OK.equals(response.getCode())) 
                    {
                        try 
                        {
                            // 基本信息
                            weatherInfo.code = response.getCode();
                            weatherInfo.updateTime = response.getUpdateTime();
                            weatherInfo.fxLink = response.getFxLink();
                            
                            // 获取now对象
                            if (response.getNow() != null) 
                            {
                                Object now = response.getNow();
                                
                                // 通过反射获取各个字段
                                weatherInfo.obsTime = getStringProperty(now, "getObsTime");
                                weatherInfo.temp = getStringProperty(now, "getTemp");
                                weatherInfo.feelsLike = getStringProperty(now, "getFeelsLike");
                                weatherInfo.icon = getStringProperty(now, "getIcon");
                                weatherInfo.text = getStringProperty(now, "getText");
                                weatherInfo.wind360 = getStringProperty(now, "getWind360");
                                weatherInfo.windDir = getStringProperty(now, "getWindDir");
                                weatherInfo.windScale = getStringProperty(now, "getWindScale");
                                weatherInfo.windSpeed = getStringProperty(now, "getWindSpeed");
                                weatherInfo.humidity = getStringProperty(now, "getHumidity");
                                weatherInfo.precip = getStringProperty(now, "getPrecip");
                                weatherInfo.pressure = getStringProperty(now, "getPressure");
                                weatherInfo.vis = getStringProperty(now, "getVis");
                                weatherInfo.cloud = getStringProperty(now, "getCloud");
                                weatherInfo.dew = getStringProperty(now, "getDew");
                            }
                            
                            Log.d(TAG, "解析天气数据成功: " + weatherInfo.text + ", " + weatherInfo.temp + "°C");
                        } 
                        catch (Exception e) 
                        {
                            Log.e(TAG, "解析天气数据失败", e);
                            weatherInfo.error = "解析天气数据失败: " + e.getMessage();
                        }
                    } 
                    else 
                    {
                        weatherInfo.error = "API返回错误: " + response.getCode();
                    }
                    
                    callback.onSuccess(weatherInfo);
                }

                @Override
                public void onFailure(ErrorResponse errorResponse) 
                {
                    String error = "获取天气失败: " + errorResponse.toString();
                    Log.e(TAG, error);
                    callback.onError(error);
                }

                @Override
                public void onException(Throwable e) 
                {
                    String error = "获取天气异常: " + e.getMessage();
                    Log.e(TAG, error, e);
                    callback.onError(error);
                }
            });
        } 
        catch (Exception e) 
        {
            String error = "发送天气请求异常: " + e.getMessage();
            Log.e(TAG, error, e);
            callback.onError(error);
        }
    }
    
    /**
     * 通过反射获取对象属性对象
     */
    private Object getObjectProperty(Object obj, String methodName) 
    {
        try 
        {
            Method method = obj.getClass().getMethod(methodName);
            return method.invoke(obj);
        } 
        catch (Exception e) 
        {
            Log.w(TAG, "获取对象属性失败: " + methodName);
            return null;
        }
    }

    /**
     * 获取15天天气预报
     * 
     * @param cityId 城市ID
     * @param callback 回调接口
     */
    public void getWeatherForecast15Days(String cityId, final WeatherCallback<WeatherForecastInfo> callback) 
    {
        if (!checkInitialized(callback)) return;
        
        try 
        {
            Log.d(TAG, "获取城市" + cityId + "的15天天气预报");
            
            // 创建天气查询参数
            WeatherParameter parameter = new WeatherParameter(cityId);
            
            // 调用API
            qWeather.weather15d(parameter, new Callback<WeatherDailyResponse>() 
            {
                @Override
                public void onSuccess(WeatherDailyResponse response) 
                {
                    Log.d(TAG, "获取15天天气预报成功: " + response.toString());
                    
                    // 创建返回数据
                    WeatherForecastInfo forecastInfo = new WeatherForecastInfo();
                    
                    if (STATUS_OK.equals(response.getCode())) 
                    {
                        try 
                        {
                            // 基本信息
                            forecastInfo.code = response.getCode();
                            forecastInfo.updateTime = response.getUpdateTime();
                            forecastInfo.fxLink = response.getFxLink();
                            
                            // 获取每日预报
                            if (response.getDaily() != null && !response.getDaily().isEmpty()) 
                            {
                                List<?> dailyList = response.getDaily();
                                
                                for (Object daily : dailyList) 
                                {
                                    WeatherForecastInfo.DailyForecast forecast = new WeatherForecastInfo.DailyForecast();
                                    
                                    // 日期和时间
                                    forecast.fxDate = getStringProperty(daily, "getFxDate");
                                    
                                    // 天气状况
                                    forecast.textDay = getStringProperty(daily, "getTextDay");
                                    forecast.textNight = getStringProperty(daily, "getTextNight");
                                    
                                    // 温度
                                    forecast.tempMax = getStringProperty(daily, "getTempMax");
                                    forecast.tempMin = getStringProperty(daily, "getTempMin");
                                    
                                    // 风况
                                    forecast.windDirDay = getStringProperty(daily, "getWindDirDay");
                                    forecast.windScaleDay = getStringProperty(daily, "getWindScaleDay");
                                    
                                    // 其他指标
                                    forecast.humidity = getStringProperty(daily, "getHumidity");
                                    
                                    // 添加到预报列表
                                    forecastInfo.addDailyForecast(forecast);
                                }
                            }
                            
                            Log.d(TAG, "解析天气预报数据成功: " + forecastInfo.dailyForecasts.size() + "天");
                        } 
                        catch (Exception e) 
                        {
                            Log.e(TAG, "解析天气预报数据失败", e);
                            forecastInfo.error = "解析天气预报数据失败: " + e.getMessage();
                        }
                    } 
                    else 
                    {
                        forecastInfo.error = "API返回错误: " + response.getCode();
                    }
                    
                    callback.onSuccess(forecastInfo);
                }

                @Override
                public void onFailure(ErrorResponse errorResponse) 
                {
                    String error = "获取天气预报失败: " + errorResponse.toString();
                    Log.e(TAG, error);
                    callback.onError(error);
                }

                @Override
                public void onException(Throwable e) 
                {
                    String error = "获取天气预报异常: " + e.getMessage();
                    Log.e(TAG, error, e);
                    callback.onError(error);
                }
            });
        } 
        catch (Exception e) 
        {
            String error = "发送天气预报请求异常: " + e.getMessage();
            Log.e(TAG, error, e);
            callback.onError(error);
        }
    }

    /**
     * 获取3天天气预报
     * 
     * @param cityId 城市ID
     * @param callback 回调接口
     */
    public void getWeatherForecast3Days(String cityId, final WeatherCallback<WeatherForecastInfo> callback) 
    {
        if (!checkInitialized(callback)) return;
        
        try 
        {
            Log.d(TAG, "获取城市" + cityId + "的3天天气预报");
            
            // 创建天气查询参数
            WeatherParameter parameter = new WeatherParameter(cityId);
            
            // 调用API
            qWeather.weather3d(parameter, new Callback<WeatherDailyResponse>() 
            {
                @Override
                public void onSuccess(WeatherDailyResponse response) 
                {
                    Log.d(TAG, "获取3天天气预报成功: " + response.toString());
                    
                    // 创建返回数据
                    WeatherForecastInfo forecastInfo = new WeatherForecastInfo();
                    
                    if (STATUS_OK.equals(response.getCode())) 
                    {
                        try 
                        {
                            // 基本信息
                            forecastInfo.code = response.getCode();
                            forecastInfo.updateTime = response.getUpdateTime();
                            forecastInfo.fxLink = response.getFxLink();
                            
                            // 获取每日预报
                            if (response.getDaily() != null && !response.getDaily().isEmpty()) 
                            {
                                List<?> dailyList = response.getDaily();
                                
                                for (Object daily : dailyList) 
                                {
                                    WeatherForecastInfo.DailyForecast forecast = new WeatherForecastInfo.DailyForecast();
                                    
                                    // 日期和时间
                                    forecast.fxDate = getStringProperty(daily, "getFxDate");
                                    
                                    // 天气状况
                                    forecast.textDay = getStringProperty(daily, "getTextDay");
                                    forecast.textNight = getStringProperty(daily, "getTextNight");
                                    
                                    // 温度
                                    forecast.tempMax = getStringProperty(daily, "getTempMax");
                                    forecast.tempMin = getStringProperty(daily, "getTempMin");
                                    
                                    // 风况
                                    forecast.windDirDay = getStringProperty(daily, "getWindDirDay");
                                    forecast.windScaleDay = getStringProperty(daily, "getWindScaleDay");
                                    
                                    // 其他指标
                                    forecast.humidity = getStringProperty(daily, "getHumidity");
                                    
                                    // 添加到预报列表
                                    forecastInfo.addDailyForecast(forecast);
                                }
                            }
                            
                            Log.d(TAG, "解析天气预报数据成功: " + forecastInfo.dailyForecasts.size() + "天");
                        } 
                        catch (Exception e) 
                        {
                            Log.e(TAG, "解析天气预报数据失败", e);
                            forecastInfo.error = "解析天气预报数据失败: " + e.getMessage();
                        }
                    } 
                    else 
                    {
                        forecastInfo.error = "API返回错误: " + response.getCode();
                    }
                    
                    callback.onSuccess(forecastInfo);
                }

                @Override
                public void onFailure(ErrorResponse errorResponse) 
                {
                    String error = "获取天气预报失败: " + errorResponse.toString();
                    Log.e(TAG, error);
                    callback.onError(error);
                }

                @Override
                public void onException(Throwable e) 
                {
                    String error = "获取天气预报异常: " + e.getMessage();
                    Log.e(TAG, error, e);
                    callback.onError(error);
                }
            });
        } 
        catch (Exception e) 
        {
            String error = "发送天气预报请求异常: " + e.getMessage();
            Log.e(TAG, error, e);
            callback.onError(error);
        }
    }

    /**
     * 获取两小时内分钟级降水预报
     * 
     * @param latitude 纬度
     * @param longitude 经度
     * @param callback 回调接口
     */
    public void getMinutelyRainForecast(double latitude, double longitude, final WeatherCallback<MinutelyRainInfo> callback) 
    {
        if (!checkInitialized(callback)) return;
        
        try 
        {
            Log.d(TAG, "获取位置(" + latitude + "," + longitude + ")的分钟级降水预报");
            
            // 创建分钟级降水预报查询参数 - 直接传入经度和纬度
            MinutelyParameter parameter = new MinutelyParameter(longitude, latitude);
            
            // 调用API
            qWeather.minutely(parameter, new Callback<MinutelyResponse>() 
            {
                @Override
                public void onSuccess(MinutelyResponse response) 
                {
                    Log.d(TAG, "获取分钟级降水预报成功: " + response.toString());
                    
                    // 创建返回数据
                    MinutelyRainInfo rainInfo = new MinutelyRainInfo();
                    
                    if (STATUS_OK.equals(response.getCode())) 
                    {
                        try 
                        {
                            // 基本信息
                            rainInfo.code = response.getCode();
                            rainInfo.updateTime = response.getUpdateTime();
                            rainInfo.fxLink = response.getFxLink();
                            rainInfo.summary = response.getSummary();
                            
                            // 获取分钟级降水数据
                            if (response.getMinutely() != null && !response.getMinutely().isEmpty()) 
                            {
                                List<?> minutelyList = response.getMinutely();
                                
                                for (Object minutely : minutelyList) 
                                {
                                    MinutelyRainInfo.MinutelyData data = new MinutelyRainInfo.MinutelyData();
                                    
                                    // 获取各个字段
                                    data.fxTime = getStringProperty(minutely, "getFxTime");
                                    data.precip = getStringProperty(minutely, "getPrecip");
                                    data.type = getStringProperty(minutely, "getType");
                                    
                                    // 添加到预报列表
                                    rainInfo.addMinutelyData(data);
                                }
                            }
                            
                            Log.d(TAG, "解析分钟级降水预报成功: " + rainInfo.minutelyList.size() + "个数据点");
                            Log.d(TAG, "降水预报摘要: " + rainInfo.summary);
                        } 
                        catch (Exception e) 
                        {
                            Log.e(TAG, "解析分钟级降水预报失败", e);
                            rainInfo.error = "解析分钟级降水预报失败: " + e.getMessage();
                        }
                    } 
                    else 
                    {
                        rainInfo.error = "API返回错误: " + response.getCode();
                    }
                    
                    callback.onSuccess(rainInfo);
                }

                @Override
                public void onFailure(ErrorResponse errorResponse) 
                {
                    String error = "获取分钟级降水预报失败: " + errorResponse.toString();
                    Log.e(TAG, error);
                    callback.onError(error);
                }

                @Override
                public void onException(Throwable e) 
                {
                    String error = "获取分钟级降水预报异常: " + e.getMessage();
                    Log.e(TAG, error, e);
                    callback.onError(error);
                }
            });
        } 
        catch (Exception e) 
        {
            String error = "发送分钟级降水预报请求异常: " + e.getMessage();
            Log.e(TAG, error, e);
            callback.onError(error);
        }
    }

    /**
     * 获取24小时天气预报
     * 
     * @param latitude 纬度
     * @param longitude 经度
     * @param callback 回调接口
     */
    public void getWeatherHourlyForecast(double latitude, double longitude, final WeatherCallback<WeatherHourlyResponse> callback) 
    {
        if (!checkInitialized(callback)) return;
        
        try 
        {
            Log.d(TAG, "获取位置(" + latitude + "," + longitude + ")的24小时天气预报");
            
            // 构建位置字符串: 经度,纬度
            String location = String.format(java.util.Locale.US, "%.4f,%.4f", longitude, latitude);
            
            // 创建天气查询参数
            com.qweather.sdk.parameter.weather.WeatherParameter parameter = new com.qweather.sdk.parameter.weather.WeatherParameter(location);
            
            // 调用API获取24小时天气预报
            qWeather.weather24h(parameter, new com.qweather.sdk.Callback<com.qweather.sdk.response.weather.WeatherHourlyResponse>() 
            {
                @Override
                public void onSuccess(com.qweather.sdk.response.weather.WeatherHourlyResponse response) 
                {
                    Log.d(TAG, "获取24小时天气预报成功: " + response.toString());
                    
                    if (STATUS_OK.equals(response.getCode())) 
                    {
                        callback.onSuccess(response);
                    } 
                    else 
                    {
                        String error = "API返回错误: " + response.getCode();
                        Log.e(TAG, error);
                        callback.onError(error);
                    }
                }

                @Override
                public void onFailure(com.qweather.sdk.response.error.ErrorResponse errorResponse) 
                {
                    String error = "获取24小时天气预报失败: " + errorResponse.toString();
                    Log.e(TAG, error);
                    callback.onError(error);
                }

                @Override
                public void onException(Throwable e) 
                {
                    String error = "获取24小时天气预报异常: " + e.getMessage();
                    Log.e(TAG, error, e);
                    callback.onError(error);
                }
            });
        } 
        catch (Exception e) 
        {
            String error = "发送24小时天气预报请求异常: " + e.getMessage();
            Log.e(TAG, error, e);
            callback.onError(error);
        }
    }
} 