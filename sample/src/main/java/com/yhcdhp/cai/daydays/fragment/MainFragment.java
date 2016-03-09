package com.yhcdhp.cai.daydays.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.yhcdhp.cai.BaseFragment;
import com.yhcdhp.cai.R;
import com.yhcdhp.cai.daydays.adapter.CityAdapter;
import com.yhcdhp.cai.daydays.config.UMConstants;
import com.yhcdhp.cai.daydays.entity.CityEntity;
import com.yhcdhp.cai.daydays.utils.BaiduLocationUtils;
import com.yhcdhp.cai.daydays.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caishengyan on 2016/2/16.
 */
@ContentView(R.layout.activity_test)
public class MainFragment extends BaseFragment implements BaiduLocationUtils.IMyBdLocationListener {
    private static final String TAG = "xutils";

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

    private BaiduLocationUtils mBaiduLocationUtils;

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Log.i(TAG, "onCreateView_1");
//        View view = inflater.inflate(R.layout.fragment_one, null);
//        return view;
//    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        con = MainFragment.this.getActivity();
        mAdapter = new CityAdapter(con, citise);
        recyclerView.setLayoutManager(new LinearLayoutManager(con));
        recyclerView.setAdapter(mAdapter);
        /**
         * 开启百度定位
         */
        openBaiduLocation(con);
    }

    private void openBaiduLocation(Context con) {
        mBaiduLocationUtils = BaiduLocationUtils.getInstance(con.getApplicationContext());
        mBaiduLocationUtils.setiMyBdLocationListener(this);
        mBaiduLocationUtils.startLocation();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView_1");
        super.onDestroyView();
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

    public void getData() {

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
                    Toast.makeText(con, "onSuccess", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(con, "JSONException", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(con, "onError", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(con, "onCancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinished() {
                Toast.makeText(con, "onFinished", Toast.LENGTH_SHORT).show();
            }
        });

    }


    /**
     * 无计时器的定位结果回调
     *
     * @param bdLocation
     */
    @Override
    public void locationResult(BDLocation bdLocation) {

        if (bdLocation == null)
            return;
        LogUtil.i("bdLocation = " + bdLocation.toString());
        if (bdLocation.getLocType() == 161) {

            Utils.setUMengEvent(Utils.getAppContext(), UMConstants.LOCATION_SUCCESS);

            // 设置地址显示
            LogUtil.i("地址 = " + bdLocation.getAddrStr());
            mBaiduLocationUtils.stop();
            String locaAddtess = bdLocation.getCity();

            LogUtil.i("locaAddtess = " + locaAddtess);
            button1.setText(locaAddtess);
        }
    }

    /**
     * 有计时器的定位更新回调
     */
    @Override
    public void updateLocation() {

    }
}
