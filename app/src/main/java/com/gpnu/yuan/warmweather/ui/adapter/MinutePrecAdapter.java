package com.gpnu.yuan.warmweather.ui.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gpnu.yuan.warmweather.databinding.ItemHourlyRvBinding;
import com.gpnu.yuan.warmweather.db.bean.HourlyResponse;
import com.gpnu.yuan.warmweather.db.bean.MinutePrecResponse;
import com.gpnu.yuan.warmweather.utils.EasyDate;

import java.util.List;


/**
 * 分钟级降水列表适配器
 * @author llw
 */
public class MinutePrecAdapter extends RecyclerView.Adapter<MinutePrecAdapter.ViewHolder> {

    private final List<MinutePrecResponse.MinutelyBean> minutelyBeans;
    private OnClickItemCallback onClickItemCallback;

    public void setOnClickItemCallback(OnClickItemCallback onClickItemCallback) {
        this.onClickItemCallback = onClickItemCallback;
    }
    public MinutePrecAdapter(List<MinutePrecResponse.MinutelyBean> minutelyBeans) {
        this.minutelyBeans = minutelyBeans;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MinutePrecAdapter.ViewHolder holder, int position) {

        MinutePrecResponse.MinutelyBean minutelyBean = minutelyBeans.get(position);
        String time = EasyDate.updateTime(minutelyBean.getFxTime());
        holder.binding.tvTime.setText(String.format("%s%s", EasyDate.showTimeInfo(time), time));
        holder.binding.tvTemperature.setText(String.format(minutelyBean.getPrecip())+"  "+String.format(minutelyBean.getType()));


    }



    @Override
    public int getItemCount() {
        return minutelyBeans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ItemHourlyRvBinding binding;

        public ViewHolder(@NonNull ItemHourlyRvBinding itemHourlyRvBinding) {
            super(itemHourlyRvBinding.getRoot());
            binding = itemHourlyRvBinding;
        }
    }
}

