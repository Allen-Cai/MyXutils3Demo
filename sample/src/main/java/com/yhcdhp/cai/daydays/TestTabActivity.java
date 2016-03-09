package com.yhcdhp.cai.daydays;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import com.yhcdhp.cai.BaseActivity;
import com.yhcdhp.cai.R;
import com.yhcdhp.cai.daydays.adapter.CityAdapter;
import com.yhcdhp.cai.daydays.entity.CityEntity;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caishengyan on 2016/1/20.
 */
@ContentView(R.layout.activity_test)
public class TestTabActivity extends BaseActivity {

    @ViewInject(R.id.button1)
    private Button button1;

    private List<CityEntity> citise = new ArrayList<>();
    //        private MyAdapter mAdapter;
    private CityAdapter mAdapter;
    @ViewInject(R.id.recyclerView)
    private RecyclerView recyclerView;
    //            private ListView listview;
    @ViewInject(R.id.loading_layout)
    private RelativeLayout loading_layout;

    private Context con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con = this;
        mAdapter = new CityAdapter(con, citise);
//        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(con));
        recyclerView.setAdapter(mAdapter);

//        mAdapter = new MyAdapter(con, citise);
//        listview.setAdapter(mAdapter);

    }

    @Event(value = R.id.button1, type = View.OnClickListener.class)
    private void setEvent(View view) {
        switch (view.getId()) {
            case R.id.button1:
                loading_layout.setVisibility(View.VISIBLE);
                getData();
                break;
            default:
                break;
        }

    }

    public Object getData() {

        RequestParams params = new RequestParams("http://apis.baidu.com/apistore/weatherservice/citylist");
        params.addHeader("apikey", "5ce6b537347a871dcebf6af0a1cf87c8");
        params.addBodyParameter("cityname", "北京");
        String url = params.getUri();
        LogUtil.d("apistore===" + "url=" + url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            private String errNum;
            private String errMsg;

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    errNum = obj.getString("errNum");
                    if (errNum.equals("-1")) {//失败
                        errMsg = obj.getString("errMsg");
                        button1.setText(errMsg);
                        return;
                    }
                    if (errNum.equals("0")) {//成功
                        loading_layout.setVisibility(View.GONE);
                        errMsg = obj.getString("errMsg");
                        LogUtil.d("apistore===" + "result=" + result);
                        JSONArray array = obj.getJSONArray("retData");
                        List<CityEntity> list = new ArrayList<CityEntity>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject entity = (JSONObject) array.get(i);
                            //使用fastjson解析
                            CityEntity city = JSON.parseObject(entity.toString(), CityEntity.class);

//                            CityEntity city = new CityEntity();
//                            city.setProvince_cn(entity.getString("province_cn"));
//                            city.setDistrict_cn(entity.getString("district_cn"));
//                            city.setName_cn(entity.getString("name_cn"));
//                            city.setName_en(entity.getString("name_en"));
//                            city.setArea_id(entity.getString("area_id"));
                            list.add(city);
                        }
                        citise.addAll(list);
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

        return null;
    }


}
