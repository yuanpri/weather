package com.gpnu.yuan.warmweather.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.gpnu.yuan.library.base.BaseViewModel;
import com.gpnu.yuan.warmweather.db.bean.Province;
import com.gpnu.yuan.warmweather.repository.CityRepository;
import com.gpnu.yuan.warmweather.repository.SearchCityRepository;

import java.util.List;

public class SplashViewModel extends BaseViewModel {

    public MutableLiveData<List<Province>> listMutableLiveData = new MutableLiveData<>();

    /**
     * 添加城市数据
     */
    public void addCityData(List<Province> provinceList) {
        CityRepository.getInstance().addCityData(provinceList);
    }

    /**
     * 获取所有城市数据
     */
    public void getAllCityData() {
        CityRepository.getInstance().getCityData(listMutableLiveData);
    }
}
