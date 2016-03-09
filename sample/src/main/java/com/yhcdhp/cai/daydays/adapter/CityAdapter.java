package com.yhcdhp.cai.daydays.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yhcdhp.cai.R;
import com.yhcdhp.cai.daydays.entity.CityEntity;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caishengyan on 2016/2/15.
 */
public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private List<CityEntity> mlist = new ArrayList<>();
    private Context mContext;

    public CityAdapter(Context con,List<CityEntity> data) {
        mContext = con;
        mlist = data;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {


        @ViewInject(R.id.city)
        private TextView tvTitle;
        @ViewInject(R.id.tv_zh)
        private TextView tvNameZh;
        @ViewInject(R.id.tv_en)
        private TextView tvNameEn;
        @ViewInject(R.id.area_pro)
        private TextView tvAreaPro;
        @ViewInject(R.id.area_id)
        private TextView tvAreaId;

        public ViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public CityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        x.view().inject(vh, v);
        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        CityEntity entity = mlist.get(position);
        holder.tvTitle.setText(entity.getDistrict_cn());
        holder.tvNameZh.setText(entity.getName_cn());
        holder.tvNameEn.setText(entity.getName_en());
        holder.tvAreaPro.setText(entity.getProvince_cn());
        holder.tvAreaId.setText(entity.getArea_id());
    }


    @Override
    public int getItemCount() {
        return mlist.size();
    }
}
