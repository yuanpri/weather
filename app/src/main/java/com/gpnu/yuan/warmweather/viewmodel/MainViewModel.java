package com.gpnu.yuan.warmweather.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.gpnu.yuan.library.base.BaseViewModel;
import com.gpnu.yuan.warmweather.db.bean.AirResponse;
import com.gpnu.yuan.warmweather.db.bean.DailyResponse;
import com.gpnu.yuan.warmweather.db.bean.HourlyResponse;
import com.gpnu.yuan.warmweather.db.bean.LifestyleResponse;
import com.gpnu.yuan.warmweather.db.bean.MinutePrecResponse;
import com.gpnu.yuan.warmweather.db.bean.MyCity;
import com.gpnu.yuan.warmweather.db.bean.NowResponse;
import com.gpnu.yuan.warmweather.db.bean.Province;
import com.gpnu.yuan.warmweather.db.bean.SearchCityResponse;
import com.gpnu.yuan.warmweather.db.bean.SunMoonResponse;
import com.gpnu.yuan.warmweather.repository.CityRepository;
import com.gpnu.yuan.warmweather.repository.SearchCityRepository;
import com.gpnu.yuan.warmweather.repository.WeatherRepository;

import java.util.List;

/**
 * 主页面ViewModel
 *
 */
public class MainViewModel extends BaseViewModel {

    public MutableLiveData<SearchCityResponse> searchCityResponseMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<NowResponse> nowResponseMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<DailyResponse> dailyResponseMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<LifestyleResponse> lifestyleResponseMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<List<Province>> cityMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<HourlyResponse> hourlyResponseMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<HourlyResponse> hourlyResponseMutableLiveData1 = new MutableLiveData<>();

    public MutableLiveData<AirResponse> airResponseMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<MinutePrecResponse> minutePrecResponseMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<SunMoonResponse> sunMoonResponseMutableLiveData = new MutableLiveData<>();


    /**
     * 搜索城市
     *
     * @param cityName 城市名称
     */
    public void searchCity(String cityName) {
        new SearchCityRepository().searchCity(searchCityResponseMutableLiveData, failed, cityName);
    }

    /**
     * 实况天气
     *
     * @param cityId 城市ID
     */
    public void nowWeather(String cityId) {
        new WeatherRepository().nowWeather(nowResponseMutableLiveData, failed, cityId);
    }

    /**
     * 天气预报
     * @param cityId 城市ID
     */
    public void dailyWeather(String cityId) {
        new WeatherRepository().dailyWeather(dailyResponseMutableLiveData, failed, cityId);
    }


    /**
     * 生活指数
     *
     * @param cityId 城市ID
     */
    public void lifestyle(String cityId) {
        WeatherRepository.getInstance().lifestyle(lifestyleResponseMutableLiveData, failed, cityId);
    }

    /**
     * 获取行政区数据
     */
    public void getAllCity() {
        CityRepository.getInstance().getCityData(cityMutableLiveData);
    }

    /**
     * 逐小时天气预报
     *
     * @param cityId 城市ID
     */
    public void hourlyWeather(String cityId) {
        WeatherRepository.getInstance().hourlyWeather(hourlyResponseMutableLiveData, failed, cityId);
        WeatherRepository.getInstance().hourlyWeather(hourlyResponseMutableLiveData1, failed, cityId);
    }

    /**
     * 空气质量预报
     *
     * @param cityId 城市ID
     */
    public void airWeather(String cityId) {
        WeatherRepository.getInstance().airWeather(airResponseMutableLiveData, failed, cityId);
    }

    /**
     * 分钟降雨预测
     *
     */
    public void minutePrecWeather(String cityId){
        WeatherRepository.getInstance().minutePrecWeather(minutePrecResponseMutableLiveData,failed,cityId);
    }

    /**
     * 分钟降雨预测
     *
     */
    public void sunMoonWeather(String cityId,String date){
        WeatherRepository.getInstance().sunMoonWeather(sunMoonResponseMutableLiveData,failed,cityId,date);
    }


    /**
     * 添加我的城市数据，在定位之后添加数据
     */
    public void addMyCityData(String cityName) {
        MyCity myCity = new MyCity(cityName);
        CityRepository.getInstance().addMyCityData(myCity);
    }

}
