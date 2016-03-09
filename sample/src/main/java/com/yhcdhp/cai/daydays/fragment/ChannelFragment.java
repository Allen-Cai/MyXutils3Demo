package com.yhcdhp.cai.daydays.fragment;

import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.yhcdhp.cai.R;

/**
 * Created by caishengyan on 2016/2/16.
 */
public class ChannelFragment extends Fragment {

    private WebView mWebView;
    private TextView tv_name;
    private static final String TAG = "xutils";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView_2");
        View view = inflater.inflate(R.layout.fragment_channel, null);
        initView(view);
        return view;
    }

    private void initView(View view) {

        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_name.setText("加载网页");
        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("file:///android_asset/test.html");
            }
        });
        mWebView = (WebView) view.findViewById(R.id.webview);

        WebSettings mWebSettings = mWebView.getSettings();
        //设置 缓存模式
        mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setAppCacheEnabled(false);
        mWebSettings.setDisplayZoomControls(false);//去掉缩放按钮
        //去掉滚动条
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //允许https://的访问
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                mWebView.stopLoading();
                mWebView.clearView();
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            //当WebView进度改变时更新窗口进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
            }
        });

        mWebView.addJavascriptInterface(new PlayViewInterface(), "control");


    }


    private class PlayViewInterface {
        //首页微课免费播放
        @JavascriptInterface
        public void toastMessage(final String str) {

            ChannelFragment.this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ChannelFragment.this.getActivity(), str, Toast.LENGTH_SHORT).show();
                    String call = "javascript:sayHello()";
                    mWebView.loadUrl(call);
                }
            });


        }
    }


    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView_2");
        super.onDestroyView();
    }


}
