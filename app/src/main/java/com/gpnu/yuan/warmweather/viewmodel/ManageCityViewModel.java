package com.gpnu.yuan.warmweather.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.gpnu.yuan.library.base.BaseViewModel;
import com.gpnu.yuan.warmweather.db.bean.MyCity;
import com.gpnu.yuan.warmweather.repository.CityRepository;

import java.util.List;

public class ManageCityViewModel extends BaseViewModel {

    public MutableLiveData<List<MyCity>> listMutableLiveData = new MutableLiveData<>();

    /**
     * 获取所有城市数据
     */
    public void getAllCityData() {
        CityRepository.getInstance().getMyCityData(listMutableLiveData);
    }

    /**
     * 添加我的城市数据，在定位之后添加数据
     */
    public void addMyCityData(String cityName) {
        CityRepository.getInstance().addMyCityData(new MyCity(cityName));
    }

    /**
     * 删除我的城市数据
     */
    public void deleteMyCityData(MyCity myCity) {
        CityRepository.getInstance().deleteMyCityData(myCity);
    }

    /**
     * 删除我的城市数据
     */
    public void deleteMyCityData(String cityName) {
        CityRepository.getInstance().deleteMyCityData(cityName);
    }



}
