package com.gpnu.yuan.warmweather.ui;

import static com.gpnu.yuan.warmweather.utils.EasyDate.getTheYearMonthAndDayDelimiter;
import static com.gpnu.yuan.warmweather.utils.EasyDate.updateTime;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.gpnu.yuan.warmweather.Constant;
import com.gpnu.yuan.warmweather.R;
import com.gpnu.yuan.warmweather.databinding.DialogDailyDetailBinding;
import com.gpnu.yuan.warmweather.databinding.DialogHourlyDetailBinding;
import com.gpnu.yuan.warmweather.db.bean.AirResponse;
import com.gpnu.yuan.warmweather.db.bean.HourlyResponse;
import com.gpnu.yuan.warmweather.location.WarmLocation;
import com.gpnu.yuan.warmweather.ui.adapter.DailyAdapter;
import com.gpnu.yuan.warmweather.ui.adapter.HourlyAdapter;
import com.gpnu.yuan.warmweather.ui.adapter.LifestyleAdapter;
import com.gpnu.yuan.warmweather.db.bean.DailyResponse;
import com.gpnu.yuan.warmweather.db.bean.LifestyleResponse;
import com.gpnu.yuan.warmweather.db.bean.NowResponse;
import com.gpnu.yuan.warmweather.db.bean.SearchCityResponse;
import com.gpnu.yuan.warmweather.databinding.ActivityMainBinding;
import com.gpnu.yuan.warmweather.location.LocationCallback;
import com.gpnu.yuan.warmweather.utils.CityDialog;
import com.gpnu.yuan.warmweather.utils.EasyDate;
import com.gpnu.yuan.warmweather.utils.MVUtils;
import com.gpnu.yuan.warmweather.utils.music.MusicPlayer;
import com.gpnu.yuan.warmweather.viewmodel.MainViewModel;
import com.gpnu.yuan.library.base.NetworkActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends NetworkActivity<ActivityMainBinding> implements LocationCallback, CityDialog.SelectedCityCallback {

    //权限数组
    private final String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求权限意图
    private ActivityResultLauncher<String[]> requestPermissionIntent;
    //跳转页面Intent
    private ActivityResultLauncher<Intent> jumpActivityIntent;
    private MainViewModel viewModel;
    //天气预报数据和适配器
    private final List<DailyResponse.DailyBean> dailyBeanList = new ArrayList<>();
    private final DailyAdapter dailyAdapter = new DailyAdapter(dailyBeanList);
    //生活指数数据和适配器
    private final List<LifestyleResponse.DailyBean> lifestyleList = new ArrayList<>();
    private final LifestyleAdapter lifestyleAdapter = new LifestyleAdapter(lifestyleList);
    //逐小时天气预报数据和适配器
    private final List<HourlyResponse.HourlyBean> hourlyBeanList = new ArrayList<>();
    private final HourlyAdapter hourlyAdapter = new HourlyAdapter(hourlyBeanList);
    //城市弹窗
    private CityDialog cityDialog;
    //定位
    private WarmLocation warmLocation;
    //菜单
    private Menu mMenu;
    //城市信息来源标识  0: 定位， 1: 切换城市
    private int cityFlag = 0;
    //城市名称，定位和切换城市都会重新赋值。
    private String mCityName;
    //是否正在刷新
    private boolean isRefresh;

    private MusicPlayer musicPlayer;
    private Switch musicSwitch;

    private static boolean isMusic;



    /**
     * 注册意图
     */
    @Override
    public void onRegister() {
        //请求权限意图
        requestPermissionIntent = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean fineLocation = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION));
            boolean writeStorage = Boolean.TRUE.equals(result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE));
            if (fineLocation && writeStorage) {
                //权限已经获取到，开始定位
                startLocation();
            }
        });
        //城市管理页面返回数据
        jumpActivityIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                //获取上个页面返回的数据
                String city = result.getData().getStringExtra(Constant.CITY_RESULT);
                //检查返回的城市 , 如果返回的城市是当前定位城市，并且当前定位标志为0，则不需要请求
                if (city.equals(MVUtils.getString(Constant.LOCATION_CITY)) && cityFlag == 0) {
                    Log.d("TAG", "onRegister: 管理城市页面返回不需要进行天气查询");
                    return;
                }
                //反之就直接调用选中城市的方法进行城市天气搜索
                Log.d("TAG", "onRegister: 管理城市页面返回进行天气查询");
                selectedCity(city);
            }
        });

    }

    /**
     * 初始化
     */
    @Override
    protected void onCreate() {
        //沉浸式
        //setFullScreenImmersion();
        //初始化定位
        initLocation();
        //请求权限
        requestPermission();
        //初始化视图
        initView();
        //绑定ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        //获取城市数据
        startLocation();
        viewModel.getAllCity();
        isMusic=true;

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在 Activity 销毁时释放 MediaPlayer 资源
        musicPlayer.release();
    }

    /**
     * 初始化页面视图
     */
    private void initView() {
        //自定义Toolbar图标
        setToolbarMoreIconCustom(binding.materialToolbar);
        //天气预报列表
        binding.rvDaily.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDaily.setAdapter(dailyAdapter);
        dailyAdapter.setOnClickItemCallback(position -> showDailyDetailDialog(dailyBeanList.get(position)));
        //24小时天气
        binding.hsv.setToday24HourView(binding.hourly);//设置内容View
        //生活指数列表
        binding.rvLifestyle.setLayoutManager(new LinearLayoutManager(this));
        binding.rvLifestyle.setAdapter(lifestyleAdapter);
        //逐小时天气预报列表
        LinearLayoutManager hourlyLayoutManager = new LinearLayoutManager(this);
        hourlyLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.rvHourly.setLayoutManager(hourlyLayoutManager);
        binding.rvHourly.setAdapter(hourlyAdapter);
        hourlyAdapter.setOnClickItemCallback(position -> showHourlyDetailDialog(hourlyBeanList.get(position)));





        //下拉刷新监听
        binding.layRefresh.setOnRefreshListener(() -> {
            if (mCityName == null) {
                binding.layRefresh.setRefreshing(false);
                return;
            }
            //设置正在刷新
            isRefresh = true;
            //搜索城市
            viewModel.searchCity(mCityName);
        });
        //滑动监听
        binding.layScroll.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY) {
                //getMeasuredHeight() 表示控件的绘制高度
                if (scrollY > binding.layScrollHeight.getMeasuredHeight()) {
                    binding.tvTitle.setText((mCityName == null ? "城市天气" : mCityName));
                }
            } else if (scrollY < oldScrollY) {
                if (scrollY < binding.layScrollHeight.getMeasuredHeight()) {
                    //改回原来的
                    binding.tvTitle.setText("城市天气");
                }
            }
        });




    }
    private void showHourlyDetailDialog(HourlyResponse.HourlyBean hourlyBean) {
        BottomSheetDialog dialog = new BottomSheetDialog(MainActivity.this);
        DialogHourlyDetailBinding detailBinding = DialogHourlyDetailBinding.inflate(LayoutInflater.from(MainActivity.this), null, false);
        //关闭弹窗
        detailBinding.ivClose.setOnClickListener(v -> dialog.dismiss());
        //设置数据显示
        String time = updateTime(hourlyBean.getFxTime());
        detailBinding.toolbarHourly.setTitle(EasyDate.showTimeInfo(time) + time);
        detailBinding.toolbarHourly.setSubtitle("逐小时预报详情");
        detailBinding.tvTmp.setText(String.format("%s℃", hourlyBean.getTemp()));
        detailBinding.tvCondTxt.setText(hourlyBean.getText());
        detailBinding.tvWindDeg.setText(String.format("%s°", hourlyBean.getWind360()));
        detailBinding.tvWindDir.setText(hourlyBean.getWindDir());
        detailBinding.tvWindSc.setText(String.format("%s级", hourlyBean.getWindScale()));
        detailBinding.tvWindSpd.setText(String.format("公里/小时%s", hourlyBean.getWindSpeed()));
        detailBinding.tvHum.setText(String.format("%s%%", hourlyBean.getHumidity()));
        detailBinding.tvPres.setText(String.format("%shPa", hourlyBean.getPressure()));
        detailBinding.tvPop.setText(String.format("%s%%", hourlyBean.getPop()));
        detailBinding.tvDew.setText(String.format("%s℃", hourlyBean.getDew()));
        detailBinding.tvCloud.setText(String.format("%s%%", hourlyBean.getCloud()));
        dialog.setContentView(detailBinding.getRoot());
        dialog.show();
    }

    /**
     * 创建菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 菜单选项选中
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.item_switching_cities){
            if (cityDialog != null)
                cityDialog.show();
        } else if (item.getItemId()==R.id.item_manage_city) {
            //管理城市
            //jumpActivity(ManageCityActivity.class);
            jumpActivityIntent.launch(new Intent(mContext,ManageCityActivity.class));

        }else if(item.getItemId()==R.id.item_manage_background_music){
            // 获取Switch控件
            musicSwitch = (Switch) item.getActionView();
            // 设置Switch状态变化监听器
            musicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        isMusic=true;
                    } else {
                        isMusic=false;
                    }
                }
            });
        }
        else if(item.getItemId()==R.id.item_relocation){
            startLocation();//点击重新定位item时，再次定位一下。
            Toast.makeText(MainActivity.this, "重新定位中...", Toast.LENGTH_SHORT).show();
        }
        else if(item.getItemId()==R.id.item_cities){
            if (cityDialog != null)
                cityDialog.show();
        }
        return true;
    }


    /**
     * 自定义Toolbar的图标
     */
    public void setToolbarMoreIconCustom(Toolbar toolbar) {
        if (toolbar == null) return;
        toolbar.setTitle("");
        Drawable moreIcon = ContextCompat.getDrawable(toolbar.getContext(), R.drawable.ic_round_menu);
        if (moreIcon != null) toolbar.setOverflowIcon(moreIcon);
        setSupportActionBar(toolbar);
    }

    /**
     * 数据观察
     */
    @Override
    protected void onObserveData() {
        if (viewModel != null) {
            //城市数据返回
            viewModel.searchCityResponseMutableLiveData.observe(this, searchCityResponse -> {
                List<SearchCityResponse.LocationBean> location = searchCityResponse.getLocation();
                if (location != null && location.size() > 0) {
                    String id = location.get(0).getId();
                    //检测是否在刷新
                    if(isRefresh){
                        showMsg("刷新完成");
                        binding.layRefresh.setRefreshing(false);
                        isRefresh=false;
                        Log.i("dddddd","iiiiiiiiiiiii");
                    }

                    //获取到城市的ID
                    if (id != null) {
                        //通过城市ID查询城市实时天气
                        viewModel.nowWeather(id);
                        //通过城市ID查询天气预报
                        viewModel.dailyWeather(id);
                        //通过城市ID查询生活指数
                        viewModel.lifestyle(id);
                        //通过城市ID查询逐小时天气预报
                        viewModel.hourlyWeather(id);
                        //通过城市ID查询空气质量
                        viewModel.airWeather(id);
                        //通过城市ID查询日出日落
                        viewModel.sunMoonWeather(id,getTheYearMonthAndDayDelimiter(""));
                    }
                }
            });
            // 风速风车
            viewModel.nowResponseMutableLiveData.observe(this, nowResponse -> {
                NowResponse.NowBean now = nowResponse.getNow();
                if (now != null) {
                    binding.tvWeek.setText(EasyDate.getTodayOfWeek());//星期
                    binding.tvWeatherInfo.setText(now.getText());

                    // 获取开关视图
//                    musicSwitch = findViewById(R.id.item_manage_background_music);
                    setBackgroundMusic(now.getText(),isMusic);
//                    musicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        // 处理开关状态变化
//                        setBackgroundMusic(now.getText(),isChecked);
//                        if (isChecked) {
//                            setBackgroundMusic(now.getText(),true);
//                        }else {
//                            // 如果开关关闭，暂停音乐
//                            setBackgroundMusic(now.getText(),false);
//                        }
//                    });



                    binding.tvTemp.setText(now.getTemp());
                    //精简更新时间
                    String time = updateTime(nowResponse.getUpdateTime());
                    // 获取当前时间
                    String currentTime = updateTime(nowResponse.getUpdateTime());
                    // 根据天气和时间设置背景
                    setWeatherBackground(now.getText(), currentTime);

                    binding.tvUpdateTime.setText(String.format("最近更新时间：%s%s", EasyDate.showTimeInfo(time), time));
                    binding.tvWindDirection.setText(String.format("风向     %s", now.getWindDir()));//风向
                    binding.tvWindPower.setText(String.format("风力     %s级", now.getWindScale()));//风力
                    binding.wwBig.startRotate(Integer.parseInt(now.getWindScale()));//大风车开始转动
                    binding.wwSmall.startRotate(Integer.parseInt(now.getWindScale()));//小风车开始转动
//
//                    //测试
//                    int text = 12;
//                    binding.wwBig.startRotate(text);
//                    binding.wwSmall.startRotate(text);
                }
            });
            //当天温度+天气预报
            viewModel.dailyResponseMutableLiveData.observe(this, dailyResponse -> {
                List<DailyResponse.DailyBean> daily = dailyResponse.getDaily();
                if (daily != null) {
                    if (dailyBeanList.size() > 0) {
                        dailyBeanList.clear();
                    }
                    dailyBeanList.addAll(daily);
                    dailyAdapter.notifyDataSetChanged();



                    binding.tvLow.setText(String.format("%s℃ / ", daily.get(0).getTempMin()));
                    binding.tvHeight.setText(String.format("%s℃", daily.get(0).getTempMax()));


                }
            });
            //生活指数返回
            viewModel.lifestyleResponseMutableLiveData.observe(this, lifestyleResponse -> {
                dismissLoadingDialog();//加载完成...

                List<LifestyleResponse.DailyBean> daily = lifestyleResponse.getDaily();
                if (daily != null) {
                    if (lifestyleList.size() > 0) {
                        lifestyleList.clear();
                    }
                    lifestyleList.addAll(daily);
                    lifestyleAdapter.notifyDataSetChanged();
                }
            });
            //获取本地城市数据返回
            viewModel.cityMutableLiveData.observe(this, provinces -> {
                //城市弹窗初始化
                cityDialog = CityDialog.getInstance(MainActivity.this, provinces);
                cityDialog.setSelectedCityCallback(this);
            });
            //曲线逐小时天气预报数据返回
            viewModel.hourlyResponseMutableLiveData.observe(this, hourlyResponse -> {
                List<HourlyResponse.HourlyBean> hourlyWeatherList = hourlyResponse.getHourly();
                List<HourlyResponse.HourlyBean> data = new ArrayList<>();
                if (hourlyWeatherList.size() > 23) {
                    for (int i = 0; i < 24; i++) {
                        data.add(hourlyWeatherList.get(i));
                        String condCode = data.get(i).getIcon();
                        String time = data.get(i).getFxTime();
                        time = time.substring(time.length() - 11, time.length() - 9);
                        int hourNow = Integer.parseInt(time);
                        if (hourNow >= 6 && hourNow <= 19) {
                            data.get(i).setIcon(condCode + "d");
                        } else {
                            data.get(i).setIcon(condCode + "n");
                        }
                    }
                } else {
                    for (int i = 0; i < hourlyWeatherList.size(); i++) {
                        data.add(hourlyWeatherList.get(i));
                        String condCode = data.get(i).getIcon();
                        String time = data.get(i).getFxTime();
                        time = time.substring(time.length() - 11, time.length() - 9);
                        int hourNow = Integer.parseInt(time);
                        if (hourNow >= 6 && hourNow <= 19) {
                            data.get(i).setIcon(condCode + "d");
                        } else {
                            data.get(i).setIcon(condCode + "n");
                        }
                    }
                }

                int minTmp = Integer.parseInt(data.get(0).getTemp());
                int maxTmp = minTmp;
                for (int i = 0; i < data.size(); i++) {
                    int tmp = Integer.parseInt(data.get(i).getTemp());
                    minTmp = Math.min(tmp, minTmp);
                    maxTmp = Math.max(tmp, maxTmp);
                }
                //设置当天的最高最低温度
                binding.hourly.setHighestTemp(maxTmp);
                binding.hourly.setLowestTemp(minTmp);
                if (maxTmp == minTmp) {
                    binding.hourly.setLowestTemp(minTmp - 1);
                }
                binding.hourly.initData(data);
                binding.tvLineMaxTmp.setText(maxTmp + "°");
                binding.tvLineMinTmp.setText(minTmp + "°");



            });
            //逐小时天气预报数据返回
            viewModel.hourlyResponseMutableLiveData1.observe(this, hourlyResponse -> {
                List<HourlyResponse.HourlyBean> hourly = hourlyResponse.getHourly();

                if (hourly != null) {
                    if (hourlyBeanList.size() > 0) {
                        hourlyBeanList.clear();
                    }
                    hourlyBeanList.addAll(hourly);
                    hourlyAdapter.notifyDataSetChanged();
                }

            });
            //日出日落月出月落
            viewModel.sunMoonResponseMutableLiveData.observe(this, sunMoonResponse -> {
                if(sunMoonResponse==null) return;
                String sunRise = updateTime(sunMoonResponse.getSunrise());
                String moonRise = updateTime(sunMoonResponse.getMoonrise());
                String sunSet = updateTime(sunMoonResponse.getSunset());
                String moonSet = updateTime(sunMoonResponse.getMoonset());
                String currentTime = updateTime(null);

                binding.sunView.setTimes(sunRise, sunSet, currentTime);
                binding.moonView.setTimes(moonRise, moonSet, currentTime);
                if (sunMoonResponse.getMoonPhase() != null && sunMoonResponse.getMoonPhase().size() > 0) {
                    binding.tvMoonState.setText(sunMoonResponse.getMoonPhase().get(0).getName());
                }
            });
            //空气质量
            viewModel.airResponseMutableLiveData.observe(this, airResponse -> {
                AirResponse.NowBean now = airResponse.getNow();
                if (now == null) return;
                binding.rpbAqi.setMaxProgress(300);//最大进度，用于计算
                binding.rpbAqi.setMinText("0");//设置显示最小值
                binding.rpbAqi.setMinTextSize(32f);
                binding.rpbAqi.setMaxText("300");//设置显示最大值
                binding.rpbAqi.setMaxTextSize(32f);
                binding.rpbAqi.setProgress(Float.parseFloat(now.getAqi()));//当前进度
                binding.rpbAqi.setArcBgColor(getColor(R.color.arc_bg_color));//圆弧的颜色
                binding.rpbAqi.setProgressColor(getColor(R.color.arc_progress_color));//进度圆弧的颜色
                binding.rpbAqi.setFirstText(now.getCategory());//空气质量描述 取值范围：优，良，轻度污染，中度污染，重度污染，严重污染
                binding.rpbAqi.setFirstTextSize(44f);//第一行文本的字体大小
                binding.rpbAqi.setSecondText(now.getAqi());//空气质量值
                binding.rpbAqi.setSecondTextSize(64f);//第二行文本的字体大小
                binding.rpbAqi.setMinText("0");
                binding.rpbAqi.setMinTextColor(getColor(R.color.arc_progress_color));

                binding.tvAirInfo.setText(String.format("空气%s", now.getCategory()));

                binding.tvPm10.setText(now.getPm10());//PM10
                binding.tvPm25.setText(now.getPm2p5());//PM2.5
                binding.tvNo2.setText(now.getNo2());//二氧化氮
                binding.tvSo2.setText(now.getSo2());//二氧化硫
                binding.tvO3.setText(now.getO3());//臭氧
                binding.tvCo.setText(now.getCo());//一氧化碳
            });
            //错误信息返回
            viewModel.failed.observe(this, this::showLongMsg);
        }
    }

    private void showDailyDetailDialog(DailyResponse.DailyBean dailyBean) {
        BottomSheetDialog dialog = new BottomSheetDialog(MainActivity.this);
        DialogDailyDetailBinding detailBinding = DialogDailyDetailBinding.inflate(LayoutInflater.from(MainActivity.this), null, false);
        //关闭弹窗
        detailBinding.ivClose.setOnClickListener(v -> dialog.dismiss());
        //设置数据显示
        detailBinding.toolbarDaily.setTitle(String.format("%s   %s", dailyBean.getFxDate(), EasyDate.getWeek(dailyBean.getFxDate())));
        detailBinding.toolbarDaily.setSubtitle("天气预报详情");
        detailBinding.tvTmpMax.setText(String.format("%s℃", dailyBean.getTempMax()));
        detailBinding.tvTmpMin.setText(String.format("%s℃", dailyBean.getTempMin()));
        detailBinding.tvUvIndex.setText(dailyBean.getUvIndex());
        detailBinding.tvCondTxtD.setText(dailyBean.getTextDay());
        detailBinding.tvCondTxtN.setText(dailyBean.getTextNight());
        detailBinding.tvWindDeg.setText(String.format("%s°", dailyBean.getWind360Day()));
        detailBinding.tvWindDir.setText(dailyBean.getWindDirDay());
        detailBinding.tvWindSc.setText(String.format("%s级", dailyBean.getWindScaleDay()));
        detailBinding.tvWindSpd.setText(String.format("%s公里/小时", dailyBean.getWindSpeedDay()));
        detailBinding.tvCloud.setText(String.format("%s%%", dailyBean.getCloud()));
        detailBinding.tvHum.setText(String.format("%s%%", dailyBean.getHumidity()));
        detailBinding.tvPres.setText(String.format("%shPa", dailyBean.getPressure()));
        detailBinding.tvPcpn.setText(String.format("%smm", dailyBean.getPrecip()));
        detailBinding.tvVis.setText(String.format("%skm", dailyBean.getVis()));
        dialog.setContentView(detailBinding.getRoot());
        dialog.show();
    }

    /**
     * 设置天气背景的方法
      */
    private void setWeatherBackground(String weather, String time) {
        // 使用空格分割时间字符串
        String[] timeArray = time.split(":");
        int hour = Integer.parseInt(timeArray[0]);

        if (weather.contains("雨")) {
            // 如果天气包含"雨"，设置雨天背景
            binding.layRoot.setBackground(ContextCompat.getDrawable(this, R.drawable.img_4));
        } else if (hour >= 7 && hour <= 14) {
            // 7:00到14:00之间，设置白天背景
            binding.layRoot.setBackground(ContextCompat.getDrawable(this, R.drawable.img_1));
        } else if (hour > 14 && hour < 19) {
            // 14:00到19:00之间，设置傍晚背景
            binding.layRoot.setBackground(ContextCompat.getDrawable(this, R.drawable.img_2));
        } else {
            // 其他时间，设置晚上背景
            binding.layRoot.setBackground(ContextCompat.getDrawable(this, R.drawable.img_3));
        }
    }
    /**
     * 背景音乐
     */
    private void setBackgroundMusic(String weather,boolean isPlayMusic){

        if(weather.contains("雪")){
            // 创建 MusicPlayer 实例
            musicPlayer = new MusicPlayer(this, R.raw.serious_snow);
            // 调用 MusicPlayer 中的播放
            musicPlayer.playPauseToggle(isPlayMusic);
        }else if(weather.contains("雨")){
            musicPlayer = new MusicPlayer(this, R.raw.rain);
            // 调用 MusicPlayer 中的播放
            musicPlayer.playPauseToggle(isPlayMusic);
        }else if(weather.contains("晴")){
            musicPlayer = new MusicPlayer(this, R.raw.sunnyday);
            // 调用 MusicPlayer 中的播放
            musicPlayer.playPauseToggle(isPlayMusic);
        }else if(weather.contains("云")){
            musicPlayer = new MusicPlayer(this, R.raw.cloudy);
            // 调用 MusicPlayer 中的播放
            musicPlayer.playPauseToggle(isPlayMusic);
        }
    }



    /********************************************************************************************/
    /**
     * 请求权限
     */
    private void requestPermission() {
        //因为项目的最低版本API是23，所以肯定需要动态请求危险权限，只需要判断权限是否拥有即可
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //开始权限请求
            requestPermissionIntent.launch(permissions);
            return;
        }
        //开始定位
        startLocation();
    }

    /****************************************************************************************/
    /**
     * 初始化定位
     */
    private void initLocation() {
        warmLocation = WarmLocation.getInstance(this);
        warmLocation.setCallback(this);
        if (isOpenLocationServiceEnable()) {
            startLocation();//开始定位
        } else {
            showMsg("(((φ(◎ロ◎;)φ)))，你好像忘记打开定位功能了");
        }

    }
    /**
     * 开始定位
     */
    private void startLocation() {
        warmLocation.startLocation();
    }
    /**
     * 手机是否开启位置服务，如果没有开启那么App将不能使用定位功能
     */
    private boolean isOpenLocationServiceEnable() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    /**
     * 接收定位信息
     *
     * @param bdLocation 定位数据
     */
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        showLoadingDialog();//加载中...
        String city = bdLocation.getCity();             //获取城市
        String district = bdLocation.getDistrict();     //获取区县
        if (viewModel != null && district != null) {
            //定位后重新赋值，下拉刷新
            mCityName=district;
            //保存定位城市
            MVUtils.put(Constant.LOCATION_CITY, district);
            //保存到我的城市数据表中
            viewModel.addMyCityData(district);
            //显示当前定位城市
            binding.tvCity.setText(district);
            //搜索城市
            viewModel.searchCity(district);
        } else {
            Log.e("TAG", "district: " + district);
        }
    }

    /**
     * 选中城市
     *
     * @param cityName 城市名称
     */
    @Override
    public void selectedCity(String cityName) {
        cityFlag = 1;//切换城市
        mCityName = cityName;//切换城市后赋值
        //搜索城市
        viewModel.searchCity(cityName);
        //显示所选城市
        binding.tvCity.setText(cityName);
    }
}