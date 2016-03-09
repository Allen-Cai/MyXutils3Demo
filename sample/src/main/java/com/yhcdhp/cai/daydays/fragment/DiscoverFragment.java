package com.yhcdhp.cai.daydays.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.yhcdhp.cai.R;

/**
 * Created by caishengyan on 2016/2/16.
 */

public class DiscoverFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "xutils";
    private BaiduMap mBaiduMap;
    private MapView mMapView;
    private Button btn_nornal;
    private Button btn_satellite;
    private CheckBox btn_trafica;
    private CheckBox btn_heat;
    private UiSettings mUiSettings;
    private Marker mMarkerA;
    private LocationClient locationClient;
    private MyLocationListener myLocationListener = new MyLocationListener();

    private Context context;
    private boolean isFirstLocation = true;
    private BitmapDescriptor bitmapDescriptor;//自定义定位图标
    private BitmapDescriptor bdGround = BitmapDescriptorFactory
            .fromResource(R.drawable.ground_overlay);
    private LocationMode mCurrentMode;//普通，跟随，罗盘

    private InfoWindow mInfoWindow;//放置弹出的pop内容

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView_3");
        context = DiscoverFragment.this.getActivity();
        View view = inflater.inflate(R.layout.fragment_discover, null);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mMapView = (MapView) view.findViewById(R.id.mMapView);
        btn_nornal = (Button) view.findViewById(R.id.btn_normal);
        btn_satellite = (Button) view.findViewById(R.id.btn_satellite);
        btn_trafica = (CheckBox) view.findViewById(R.id.btn_trafica);
        btn_heat = (CheckBox) view.findViewById(R.id.btn_heat);

        btn_nornal.setOnClickListener(this);
        btn_satellite.setOnClickListener(this);
        btn_trafica.setOnCheckedChangeListener(this);
        btn_heat.setOnCheckedChangeListener(this);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBaiduMap = mMapView.getMap();
        mUiSettings = mBaiduMap.getUiSettings();


        //设置模式
        mCurrentMode = LocationMode.NORMAL;
        bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
        //设置自定义定位图标
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, null));//默认的定位图标

        //缩放
        MapStatusUpdate mapstatusupdate = MapStatusUpdateFactory.zoomTo(14.5f);
        mBaiduMap.animateMapStatus(mapstatusupdate);


        //增加覆盖物
        LatLng llA = new LatLng(39.963175, 116.400244);
        MarkerOptions overlay = new MarkerOptions().position(llA)
                .zIndex(9).draggable(true).icon(bitmapDescriptor);
        overlay.animateType(MarkerOptions.MarkerAnimateType.drop);
        mMarkerA = (Marker) mBaiduMap.addOverlay(overlay);


        LatLng southwest = new LatLng(39.92235, 116.380338);
        LatLng northeast = new LatLng(39.947246, 116.414977);
        LatLngBounds bounds = new LatLngBounds.Builder().include(northeast)
                .include(southwest).build();

        OverlayOptions ooGround = new GroundOverlayOptions()
                .positionFromBounds(bounds).image(bdGround).transparency(0.8f);
        mBaiduMap.addOverlay(ooGround);

        //设置是否可以缩放
//        mUiSettings.setZoomGesturesEnabled(false);
/*        //是否启用平移手势
        mUiSettings.setScrollGesturesEnabled(false);
        //是否启用旋转手势
        mUiSettings.setRotateGesturesEnabled(false);
        //是否启用俯视手势
        mUiSettings.setOverlookingGesturesEnabled(false);
        //是否启用指南针图层
        mUiSettings.setCompassEnabled(false);
        //是否禁用所有手势
        mUiSettings.setAllGesturesEnabled(false);
        //是否显示底图默认标注
        mBaiduMap.showMapPoi(false);*/

        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        locationClient = new LocationClient(context);
        locationClient.registerLocationListener(myLocationListener);
        LocationClientOption options = new LocationClientOption();
        options.setOpenGps(true);
        options.setCoorType("bd09ll"); // 设置坐标类型
        options.setScanSpan(1000);
        locationClient.setLocOption(options);
        locationClient.start();

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Button button = new Button(context);

                if (marker == mMarkerA) {
                    button.setText("哈哈");
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "点击了哈哈", Toast.LENGTH_SHORT).show();
                            mBaiduMap.hideInfoWindow();
                        }
                    });
                    LatLng ll = marker.getPosition();
                    mInfoWindow = new InfoWindow(button, ll, -47);
                    mBaiduMap.showInfoWindow(mInfoWindow);
                }

                return true;
            }
        });

    }


    @Override
    public void onDestroyView() {

        Log.i(TAG, "onDestroyView_3");
        super.onDestroyView();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_normal:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//设置地图类型
                break;
            case R.id.btn_satellite:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);//设置地图类型
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.btn_trafica:
                mBaiduMap.setTrafficEnabled(isChecked);
                break;
            case R.id.btn_heat:
                mBaiduMap.setBaiduHeatMapEnabled(isChecked);
                break;
            default:
                break;
        }
    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            if (null == bdLocation || null == mMapView) {
                return;
            }

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);

            if (isFirstLocation) {
                isFirstLocation = false;
                LatLng ll = new LatLng(bdLocation.getLatitude(),
                        bdLocation.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        locationClient.stop();
        mMapView = null;

        bdGround.recycle();
        bitmapDescriptor.recycle();
    }
}
