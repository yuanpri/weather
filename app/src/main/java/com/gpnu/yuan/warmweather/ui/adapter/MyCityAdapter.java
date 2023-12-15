package com.gpnu.yuan.warmweather.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gpnu.yuan.warmweather.Constant;
import com.gpnu.yuan.warmweather.databinding.ItemMyCityRvBinding;
import com.gpnu.yuan.warmweather.db.bean.MyCity;
import com.gpnu.yuan.warmweather.utils.MVUtils;

import java.util.List;

public class MyCityAdapter extends RecyclerView.Adapter<MyCityAdapter.ViewHolder> {

    private final List<MyCity> cities;

    private OnClickItemCallback onClickItemCallback;//视图点击

    public MyCityAdapter(List<MyCity> cities) {
        this.cities = cities;
    }

    public void setOnClickItemCallback(OnClickItemCallback onClickItemCallback) {
        this.onClickItemCallback = onClickItemCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMyCityRvBinding binding = ItemMyCityRvBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder viewHolder = new ViewHolder(binding);
        //添加视图点击事件
        binding.getRoot().setOnClickListener(v -> {
            if (onClickItemCallback != null) {
                onClickItemCallback.onItemClick(viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String cityName = cities.get(position).getCityName();
        String locationCity = MVUtils.getString(Constant.LOCATION_CITY);
        holder.binding.tvCityName.setText(cityName);
        holder.binding.ivLocation.setVisibility(cityName.equals(locationCity) ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ItemMyCityRvBinding binding;

        public ViewHolder(@NonNull ItemMyCityRvBinding itemMyCityRvBinding) {
            super(itemMyCityRvBinding.getRoot());
            binding = itemMyCityRvBinding;
        }
    }
}
