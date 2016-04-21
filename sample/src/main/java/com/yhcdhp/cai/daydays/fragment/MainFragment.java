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
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.yhcdhp.cai.BaseFragment;
import com.yhcdhp.cai.R;
import com.yhcdhp.cai.daydays.adapter.CityAdapter;
import com.yhcdhp.cai.daydays.config.UMConstants;
import com.yhcdhp.cai.daydays.entity.CityEntity;
import com.yhcdhp.cai.daydays.utils.BaiduLocationUtils;
import com.yhcdhp.cai.daydays.utils.Utils;
import com.yhcdhp.cai.daydays.widget.diliver.DividerItemDecoration;

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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @ViewInject(R.id.SliderLayout)
    private SliderLayout mSliderLayout;

//    @ViewInject(R.id.custom_indicator)
//    private PagerIndicator custom_indicator;//自定义的指示器


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Log.i(TAG, "onCreateView_1");
//        View view = inflater.inflate(R.layout.fragment_one, null);
//        return view;
//    }

    private String[] urls = {
            "http://f.hiphotos.baidu.com/zhidao/pic/item/a9d3fd1f4134970aed3ef2a594cad1c8a6865def.jpg",
            "http://d.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=603e37439313b07ebde8580c39e7bd15/a8014c086e061d9591b7875a7bf40ad163d9cadb.jpg",
            "http://b.hiphotos.baidu.com/zhidao/pic/item/63d9f2d3572c11dfb068871a612762d0f703c249.jpg",
            "http://img.hb.aicdn.com/d2024a8a998c8d3e4ba842e40223c23dfe1026c8bbf3-OudiPA_fw580"
    };

    private HashMap<String, String> map = new HashMap<>();


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        con = MainFragment.this.getActivity();
        mAdapter = new CityAdapter(con, citise);
        //添加分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(con, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(con));
        recyclerView.setAdapter(mAdapter);
        /**
         * 开启百度定位
         */
        openBaiduLocation(con);

        initImages(con);
    }

    private void initImages(Context context) {

        map.put("第一个", urls[0]);
        map.put("第二个", urls[1]);
        map.put("第三个", urls[2]);
        map.put("第四个", urls[3]);

        for (String name : map.keySet()) {
            final TextSliderView tsv = new TextSliderView(con);

            //设置描述区域为不可见可以在xml不居中设置gone

            tsv.image(map.get(name));
            tsv.setScaleType(BaseSliderView.ScaleType.Fit);
            tsv.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    Toast.makeText(con, tsv.getDescription(), Toast.LENGTH_SHORT).show();
                }
            });

            mSliderLayout.addSlider(tsv);
            mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        }


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
//                getVolleyData();
                break;
            default:
                break;
        }

    }

    private String url = "http://apis.baidu.com/apistore/weatherservice/citylist";

    public void getData() {

        RequestParams params = new RequestParams(url);
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
        } else if (bdLocation.getLocType() == 62) {
            Toast.makeText(con, "定位失败,请检查网络" + bdLocation.getLocType(), Toast.LENGTH_SHORT).show();
            mBaiduLocationUtils.stop();
        } else {
            Toast.makeText(con, "定位失败" + bdLocation.getLocType(), Toast.LENGTH_SHORT).show();
            ;
            mBaiduLocationUtils.stop();
        }
    }

    /**
     * 有计时器的定位更新回调
     */
    @Override
    public void updateLocation() {

    }

    public void getVolleyData() {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringBuffer sb = new StringBuffer(url);
        sb.append("?cityname=");
        try {
            sb.append(URLEncoder.encode("北京", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        url = url + "?cityname=%E6%9C%9D%E9%98%B3";
        StringRequest request = new StringRequest(Request.Method.GET, sb.toString(), new Listener<String>() {

            private String errNum;
            private String errMsg;

            @Override
            public void onResponse(String response) {


                try {
                    Toast.makeText(con, "onSuccess=" + response.toString(), Toast.LENGTH_SHORT).show();
                    JSONObject obj = new JSONObject(response);
                    errNum = obj.getString("errNum");
                    if (errNum.equals("-1")) {//失败
                        errMsg = obj.getString("errMsg");
                        button1.setText(errMsg);
                        return;
                    }
                    if (errNum.equals("0")) {//成功
                        loading_layout.setVisibility(View.GONE);
                        errMsg = obj.getString("errMsg");
                        LogUtil.d("apistore===" + "result=" + response);
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(con, "onError=" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("apikey", "5ce6b537347a871dcebf6af0a1cf87c8");
                return headers;

            }
        };

        queue.add(request);
    }
}
