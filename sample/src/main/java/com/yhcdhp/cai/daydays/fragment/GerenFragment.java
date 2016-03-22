package com.yhcdhp.cai.daydays.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.yhcdhp.cai.BaseFragment;
import com.yhcdhp.cai.R;
import com.yhcdhp.cai.daydays.config.AppEnv;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by caishengyan on 2016/2/16.
 */
@ContentView(R.layout.fragment_geren)
public class GerenFragment extends BaseFragment {

    IWXAPI mIWXAPI = null;


    String shareUrl = "http://www.clctrip.com/node/604";//分享的网页地址
    String imagePath = "http://www.clctrip.com/sites/default/files/travel/2776750645268600768_0.jpg";
    String description = "是否还在羡慕朋友圈中令人惊讶的玻璃栈道？"
            + "是否也想体验下脚踩玻璃，凌空微步的心跳一刻？"
            + "那就和小伙伴们一起来参加天云山的玻璃栈道活动吧。"
            + "徒步羊肠小道，赏怪石危峰奇异嶙峋；攀登陡峭天梯，"
            + "体验悬崖陡壁与山林幽谷；漫步最长玻璃栈道，体验“凌空微步”的惊妙。"
            + "虫子们，一起走出来吧，给自己一个难忘的周末之旅。";
    String shareTitle = "挑战最长玻璃栈道--天云山";


    private static final String TAG = "xutils";

    @ViewInject(R.id.tv_name)
    private TextView tv_name;
    @ViewInject(R.id.button_share)
    private Button button;

    private Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtil.d(TAG + "GerenFragment==" + "onCreateView");

        mIWXAPI = WXAPIFactory.createWXAPI(GerenFragment.this.getContext(), AppEnv.WEIXINAPPID);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.d(TAG + "GerenFragment==" + "onViewCreated");
        tv_name.setText("GerenFragment");
    }

    @Override
    public void onDestroyView() {

        Log.i(TAG, "GerenFragment==" + "onDestroyView");
        super.onDestroyView();
    }

    @Event(value = R.id.button_share)
    private void shareEvent(View view) {

        Toast.makeText(GerenFragment.this.getContext(), "点击了分享", Toast.LENGTH_SHORT).show();
        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = shareUrl;

        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = webpageObject;
        //标题
        mediaMessage.title = shareTitle;
        //描述
        mediaMessage.description = description;
        //图片
        if (null == bitmap) {
            Drawable dr = getResources().getDrawable(R.mipmap.ic_launcher);
            bitmap = ((BitmapDrawable) dr).getBitmap();
        }
        mediaMessage.setThumbImage(bitmap);

        SendMessageToWX.Req req = new SendMessageToWX.Req();

        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = mediaMessage;
        //分享类型
        req.scene = SendMessageToWX.Req.WXSceneSession;
        //  req.scene = SendMessageToWX.Req.WXSceneTimeline;
        //发送
        mIWXAPI.sendReq(req);

    }

}
