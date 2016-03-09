package com.yhcdhp.cai.daydays.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yhcdhp.cai.R;
import com.yhcdhp.cai.daydays.entity.CityEntity;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;


public class MyAdapter extends BaseAdapter {

    private Context context;
    private List<CityEntity> ci;

    public MyAdapter(Context con, List<CityEntity> ls) {
        context = con;
        ci = ls;
    }

    @Override
    public int getCount() {
        return ci.size();
    }

    @Override
    public Object getItem(int position) {
        return ci.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CityEntity entity = (CityEntity) getItem(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_city, null);
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvTitle.setText(entity.getDistrict_cn());
        holder.tvNameZh.setText(entity.getName_cn());
        holder.tvNameEn.setText(entity.getName_en());
        holder.tvAreaPro.setText(entity.getProvince_cn());
        holder.tvAreaId.setText(entity.getArea_id());
        return convertView;
    }

    class ViewHolder {
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

    }

}
