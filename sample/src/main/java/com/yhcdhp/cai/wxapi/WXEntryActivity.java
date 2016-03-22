package com.yhcdhp.cai.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yhcdhp.cai.daydays.config.AppEnv;

/**
 * Created by Peter on 2015/7/1.
 */


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    IWXAPI mIWXAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIWXAPI = WXAPIFactory.createWXAPI(this, AppEnv.WEIXINAPPID, false);
        mIWXAPI.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {

        switch (baseResp.errCode) {

            case BaseResp.ErrCode.ERR_OK:
                Toast.makeText(this.getApplicationContext(), "分享成功", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Toast.makeText(this.getApplicationContext(), "分享取消", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Toast.makeText(this.getApplicationContext(), "拒绝分享", Toast.LENGTH_SHORT).show();
                finish();
                break;
            default:
                Toast.makeText(this.getApplicationContext(), "分享失败", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }

    }
}

//public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
//    private IWXAPI api;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        api = WXAPIFactory.createWXAPI(this, AppEnv.WEIXINAPPID, false);
//        api.handleIntent(getIntent(), this);
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public void onReq(BaseReq arg0) {
//    }
//
//    @Override
//    public void onResp(BaseResp resp) {
////        LogManager.show(TAG, "resp.errCode:" + resp.errCode + ",resp.errStr:"
////                + resp.errStr, 1);
//
//        switch (resp.errCode) {
//            case BaseResp.ErrCode.ERR_OK:
//                this.finish();
//                Toast.makeText(getApplicationContext(), "分享成功", Toast.LENGTH_SHORT).show();
//                break;
//            case BaseResp.ErrCode.ERR_USER_CANCEL:
//                this.finish();
//                Toast.makeText(getApplicationContext(), "取消分享", Toast.LENGTH_SHORT).show();
//                break;
//            case BaseResp.ErrCode.ERR_AUTH_DENIED:
//                this.finish();
//                Toast.makeText(getApplicationContext(), "拒绝分享", Toast.LENGTH_SHORT).show();
//                break;
//            default:
//                this.finish();
//                Toast.makeText(getApplicationContext(), "分享失败", Toast.LENGTH_SHORT).show();
//                break;
//        }
//    }
//}