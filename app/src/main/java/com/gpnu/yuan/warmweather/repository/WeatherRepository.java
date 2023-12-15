package com.gpnu.yuan.warmweather.repository;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.gpnu.yuan.warmweather.db.bean.AirResponse;
import com.gpnu.yuan.warmweather.db.bean.DailyResponse;
import com.gpnu.yuan.warmweather.db.bean.HourlyResponse;
import com.gpnu.yuan.warmweather.db.bean.LifestyleResponse;
import com.gpnu.yuan.warmweather.db.bean.MinutePrecResponse;
import com.gpnu.yuan.warmweather.db.bean.NowResponse;
import com.gpnu.yuan.library.network.ApiType;
import com.gpnu.yuan.library.network.NetworkApi;
import com.gpnu.yuan.library.network.observer.BaseObserver;
import com.gpnu.yuan.warmweather.api.ApiService;
import com.gpnu.yuan.warmweather.Constant;
import com.gpnu.yuan.warmweather.db.bean.SunMoonResponse;

@SuppressLint("CheckResult")
public class WeatherRepository {

    private static final String TAG = WeatherRepository.class.getSimpleName();

    private static final class WeatherRepositoryHolder {
        private static final WeatherRepository mInstance = new WeatherRepository();
    }

    public static WeatherRepository getInstance() {
        return WeatherRepositoryHolder.mInstance;
    }


    /**
     * 实况天气
     *
     * @param responseLiveData 成功数据
     * @param failed           错误信息
     * @param cityId           城市ID
     */
    public void nowWeather(MutableLiveData<NowResponse> responseLiveData,
                           MutableLiveData<String> failed, String cityId) {
        String type = "实时天气-->";
        NetworkApi.createService(ApiService.class, ApiType.WEATHER).nowWeather(cityId)
                .compose(NetworkApi.applySchedulers(new BaseObserver<>() {
                    @Override
                    public void onSuccess(NowResponse nowResponse) {
                        if (nowResponse == null) {
                            failed.postValue("实况天气数据为null，请检查城市ID是否正确。");
                            return;
                        }
                        //请求接口成功返回数据，失败返回状态码
                        if (Constant.SUCCESS.equals(nowResponse.getCode())) {
                            responseLiveData.postValue(nowResponse);
                        } else {
                            failed.postValue(type + nowResponse.getCode());
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                        failed.postValue(type + e.getMessage());
                    }
                }));
    }



    /**
     * 天气预报 7天
     *
     * @param responseLiveData 成功数据
     * @param failed           错误信息
     * @param cityId           城市ID
     */
    public void dailyWeather(MutableLiveData<DailyResponse> responseLiveData,
                             MutableLiveData<String> failed, String cityId) {
        String type = "天气预报-->";
        NetworkApi.createService(ApiService.class, ApiType.WEATHER).dailyWeather(cityId)
                .compose(NetworkApi.applySchedulers(new BaseObserver<>() {
                    @Override
                    public void onSuccess(DailyResponse dailyResponse) {
                        if (dailyResponse == null) {
                            failed.postValue("天气预报数据为null，请检查城市ID是否正确。");
                            return;
                        }
                        //请求接口成功返回数据，失败返回状态码
                        if (Constant.SUCCESS.equals(dailyResponse.getCode())) {
                            responseLiveData.postValue(dailyResponse);
                        } else {
                            failed.postValue(type + dailyResponse.getCode());
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                        failed.postValue(type + e.getMessage());
                    }
                }));
    }



    /**
     * 生活指数
     *
     * @param responseLiveData 成功数据
     * @param failed           错误信息
     * @param cityId           城市ID
     */
    public void lifestyle(MutableLiveData<LifestyleResponse> responseLiveData,
                          MutableLiveData<String> failed, String cityId) {
        String type = "生活指数-->";
        NetworkApi.createService(ApiService.class, ApiType.WEATHER).lifestyle("1,2,3,4,5,6,7,8,9", cityId)
                .compose(NetworkApi.applySchedulers(new BaseObserver<>() {
                    @Override
                    public void onSuccess(LifestyleResponse lifestyleResponse) {
                        if (lifestyleResponse == null) {
                            failed.postValue("生活指数数据为null，请检查城市ID是否正确。");
                            return;
                        }
                        //请求接口成功返回数据，失败返回状态码
                        if (Constant.SUCCESS.equals(lifestyleResponse.getCode())) {
                            responseLiveData.postValue(lifestyleResponse);
                        } else {
                            failed.postValue(type + lifestyleResponse.getCode());
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                        failed.postValue(type + e.getMessage());
                    }
                }));
    }

    /**
     * 逐小时天气预报
     *
     * @param responseLiveData 成功数据
     * @param failed           错误信息
     * @param cityId           城市ID
     */
    public void hourlyWeather(MutableLiveData<HourlyResponse> responseLiveData,
                              MutableLiveData<String> failed, String cityId) {
        String type = "逐小时天气预报-->";
        NetworkApi.createService(ApiService.class, ApiType.WEATHER).hourlyWeather(cityId)
                .compose(NetworkApi.applySchedulers(new BaseObserver<>() {
                    @Override
                    public void onSuccess(HourlyResponse hourlyResponse) {
                        if (hourlyResponse == null) {
                            failed.postValue("逐小时天气预报数据为null，请检查城市ID是否正确。");
                            return;
                        }
                        //请求接口成功返回数据，失败返回状态码
                        if (Constant.SUCCESS.equals(hourlyResponse.getCode())) {
                            responseLiveData.postValue(hourlyResponse);
                        } else {
                            failed.postValue(type + hourlyResponse.getCode());
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                        failed.postValue(type + e.getMessage());
                    }
                }));
    }






    /**
     * 空气质量天气预报
     *
     * @param responseLiveData 成功数据
     * @param failed           错误信息
     * @param cityId           城市ID
     */
    public void airWeather(MutableLiveData<AirResponse> responseLiveData,
                           MutableLiveData<String> failed, String cityId) {
        String type = "空气质量天气预报-->";
        NetworkApi.createService(ApiService.class, ApiType.WEATHER).airWeather(cityId)
                .compose(NetworkApi.applySchedulers(new BaseObserver<>() {
                    @Override
                    public void onSuccess(AirResponse airResponse) {
                        if (airResponse == null) {
                            failed.postValue("空气质量预报数据为null，请检查城市ID是否正确。");
                            return;
                        }
                        //请求接口成功返回数据，失败返回状态码
                        if (Constant.SUCCESS.equals(airResponse.getCode())) {
                            responseLiveData.postValue(airResponse);
                        } else {
                            failed.postValue(type + airResponse.getCode());
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                        failed.postValue(type + e.getMessage());
                    }
                }));
    }

    /**
     * 分钟降雨预测
     */
    public void minutePrecWeather(MutableLiveData<MinutePrecResponse> responseLiveData,
                                  MutableLiveData<String> failed, String cityId) {
        String type = "分钟降雨预测-->";
        NetworkApi.createService(ApiService.class, ApiType.WEATHER).minutePrecWeather(cityId)
                .compose(NetworkApi.applySchedulers(new BaseObserver<>() {
                    @Override
                    public void onSuccess(MinutePrecResponse minutePrecResponse) {
                        if (minutePrecResponse == null) {
                            failed.postValue("空气质量预报数据为null，请检查城市ID是否正确。");
                            return;
                        }
                        //请求接口成功返回数据，失败返回状态码
                        if (Constant.SUCCESS.equals(minutePrecResponse.getCode())) {
                            responseLiveData.postValue(minutePrecResponse);
                        } else {
                            failed.postValue(type + minutePrecResponse.getCode());
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                        failed.postValue(type + e.getMessage());
                    }
                }));
    }


    /**
     * 日出日落、月升月落
     * @param location   城市名
     */
    public void sunMoonWeather(MutableLiveData<SunMoonResponse> responseLiveData,
                           MutableLiveData<String> failed, String cityId,String date){
        String type = "日出日落月出月落-->";
        NetworkApi.createService(ApiService.class, ApiType.WEATHER).sunMoon(cityId,date)
                .compose(NetworkApi.applySchedulers(new BaseObserver<>() {
                    @Override
                    public void onSuccess(SunMoonResponse sunMoonResponse) {
                        if (sunMoonResponse == null) {
                            failed.postValue("日出日落数据为null，请检查城市ID是否正确。");
                            return;
                        }
                        //请求接口成功返回数据，失败返回状态码
                        if (Constant.SUCCESS.equals(sunMoonResponse.getCode())) {
                            responseLiveData.postValue(sunMoonResponse);
                        } else {
                            failed.postValue(type + sunMoonResponse.getCode());
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                        failed.postValue(type + e.getMessage());
                    }
                }));
    }




}
