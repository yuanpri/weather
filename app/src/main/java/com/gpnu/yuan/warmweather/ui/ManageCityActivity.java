package com.gpnu.yuan.warmweather.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gpnu.yuan.library.base.NetworkActivity;
import com.gpnu.yuan.warmweather.Constant;
import com.gpnu.yuan.warmweather.R;
import com.gpnu.yuan.warmweather.databinding.ActivityManageCityBinding;
import com.gpnu.yuan.warmweather.db.bean.MyCity;
import com.gpnu.yuan.warmweather.ui.adapter.MyCityAdapter;
import com.gpnu.yuan.warmweather.utils.AddCityDialog;
import com.gpnu.yuan.warmweather.viewmodel.ManageCityViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManageCityActivity extends NetworkActivity<ActivityManageCityBinding> {

    private ManageCityViewModel viewModel;
    private final List<MyCity> myCityList = new ArrayList<>();
    private final MyCityAdapter myCityAdapter = new MyCityAdapter(myCityList);

    @Override
    protected void onCreate() {
        initView();
        viewModel = new ViewModelProvider(this).get(ManageCityViewModel.class);
        viewModel.getAllCityData();
    }

    private void initView() {
        backAndFinish(binding.toolbar);
        setStatusBar(true);
        myCityAdapter.setOnClickItemCallback(position -> setPageResult(myCityList.get(position).getCityName()));

        binding.rvCity.setLayoutManager(new LinearLayoutManager(ManageCityActivity.this));
        binding.rvCity.setAdapter(myCityAdapter);

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                //控制快速滑动的方向
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(0, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //显示提示弹窗
                showDeleteCity(viewHolder.getAdapterPosition());
            }

        });
        //关联recyclerView
        helper.attachToRecyclerView(binding.rvCity);

        binding.btnAddCity.setOnClickListener(v ->
                AddCityDialog.show(ManageCityActivity.this, Arrays.asList(Constant.CITY_ARRAY), cityName -> {
                    //保存到数据库中
                    viewModel.addMyCityData(cityName);
                    //设置页面返回数据
                    setPageResult(cityName);
                }));


    }

    private void showDeleteCity(int position) {
        // 声明对象
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("删除城市")
                .setIcon(R.drawable.ic_round_delete_forever_24)
                .setMessage("您确定要删除吗？")
                .setPositiveButton("确定", (dialog1, which) -> {
                    MyCity myCity=myCityList.get(position);
                    myCityList.remove(position);
                    myCityAdapter.notifyItemRemoved(position);
                    viewModel.deleteMyCityData(myCity);

                    dialog1.dismiss();
                }).setNegativeButton("取消", (dialog12, which) -> {
                    myCityAdapter.notifyItemChanged(position);

                    dialog12.dismiss();
                });
        dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }


    /**
     * 设置页面返回数据
     * @param cityName 城市名
     */
    private void setPageResult(String cityName) {
        Intent intent = new Intent();
        intent.putExtra(Constant.CITY_RESULT, cityName);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onObserveData() {
        viewModel.listMutableLiveData.observe(this, myCities -> {
            if (myCities != null && myCities.size() > 0) {
                myCityList.clear();
                myCityList.addAll(myCities);
                myCityAdapter.notifyDataSetChanged();
            } else {
                showMsg("空空如也");
            }
        });
    }
}

