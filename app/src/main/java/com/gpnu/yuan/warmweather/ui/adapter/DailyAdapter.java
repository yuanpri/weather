package com.gpnu.yuan.warmweather.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gpnu.yuan.warmweather.db.bean.DailyResponse;
import com.gpnu.yuan.warmweather.databinding.ItemDailyRvBinding;
import com.gpnu.yuan.warmweather.utils.EasyDate;
import com.gpnu.yuan.warmweather.utils.WeatherUtil;

import java.util.List;

public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.ViewHolder> {

    private final List<DailyResponse.DailyBean> dailyBeans;
    private OnClickItemCallback onClickItemCallback;
    public void setOnClickItemCallback(OnClickItemCallback onClickItemCallback) {
        this.onClickItemCallback = onClickItemCallback;
    }

    public DailyAdapter(List<DailyResponse.DailyBean> dailyBeans) {
        this.dailyBeans = dailyBeans;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDailyRvBinding binding = ItemDailyRvBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder holder = new ViewHolder(binding);
        //添加点击回调
        binding.getRoot().setOnClickListener(v -> {
            if (onClickItemCallback != null) {
                onClickItemCallback.onItemClick(holder.getAdapterPosition());
            }
        });
        return holder;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyResponse.DailyBean dailyBean = dailyBeans.get(position);
        holder.binding.tvDate.setText(EasyDate.dateSplit(dailyBean.getFxDate()) + EasyDate.getDayInfo(dailyBean.getFxDate()));
        WeatherUtil.changeIcon(holder.binding.ivStatus, Integer.parseInt(dailyBean.getIconDay()));
        holder.binding.tvLow.setText(dailyBean.getTempMin() + "℃  ");
        // 计算温度差值
        int tempDiff = (Integer.parseInt(dailyBean.getTempMax()) - Integer.parseInt(dailyBean.getTempMin()));
        // 获取进度层
        LayerDrawable layerDrawable = (LayerDrawable) holder.binding.progressBar.getProgressDrawable();
        Drawable progressDrawable = layerDrawable.findDrawableByLayerId(android.R.id.progress);
        Log.d("TempDiffValue", "tempDiff: " + tempDiff);
        // 设置进度条颜色
        if (progressDrawable instanceof ClipDrawable) {
            switch (tempDiff) {
                case 0:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#004D40"), PorterDuff.Mode.SRC_IN);
                    break;
                case 1:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#00695C"), PorterDuff.Mode.SRC_IN);
                    break;
                case 2:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#00796B"), PorterDuff.Mode.SRC_IN);
                    break;
                case 3:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#00897B"), PorterDuff.Mode.SRC_IN);
                    break;
                case 4:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#009688"), PorterDuff.Mode.SRC_IN);
                    break;
                case 5:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#26A69A"), PorterDuff.Mode.SRC_IN);
                    break;
                case 6:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#4DB6AC"), PorterDuff.Mode.SRC_IN);
                    break;
                case 7:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#80CBC4"), PorterDuff.Mode.SRC_IN);
                    break;
                case 8:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#AED581"), PorterDuff.Mode.SRC_IN);
                    break;
                case 9:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#C5E1A5"), PorterDuff.Mode.SRC_IN);
                    break;
                case 10:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#DCE775"), PorterDuff.Mode.SRC_IN);
                    break;
                case 11:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#FFF176"), PorterDuff.Mode.SRC_IN);
                    break;
                case 12:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#FFD54F"), PorterDuff.Mode.SRC_IN);
                    break;
                case 13:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#FFB74D"), PorterDuff.Mode.SRC_IN);
                    break;
                case 14:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#FF8A65"), PorterDuff.Mode.SRC_IN);
                    break;
                case 15:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#FF7043"), PorterDuff.Mode.SRC_IN);
                    break;
                case 16:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#FF5722"), PorterDuff.Mode.SRC_IN);
                    break;
                case 17:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#F4511E"), PorterDuff.Mode.SRC_IN);
                    break;
                case 18:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#E64A19"), PorterDuff.Mode.SRC_IN);
                    break;
                case 19:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#D84315"), PorterDuff.Mode.SRC_IN);
                    break;
                case 20:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#FF5722"), PorterDuff.Mode.SRC_IN);
                    break;
                default:
                    ((ClipDrawable) progressDrawable).setColorFilter(Color.parseColor("#F4511E"), PorterDuff.Mode.SRC_IN);
                    // 设置一个默认颜色，或者不设置颜色
            }
        }
        // 设置进度条的长度
        holder.binding.progressBar.setProgress(tempDiff*5);
        holder.binding.progressBar.setTooltipText(EasyDate.getDayInfo(dailyBean.getFxDate())+"日温差："+String.valueOf(tempDiff*5)+"℃");
        holder.binding.tvHeight.setText("  " + dailyBean.getTempMax() + "℃");

    }

    @Override
    public int getItemCount() {
        return dailyBeans.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ItemDailyRvBinding binding;

        public ViewHolder(@NonNull ItemDailyRvBinding itemTextRvBinding) {
            super(itemTextRvBinding.getRoot());
            binding = itemTextRvBinding;
        }
    }
}
