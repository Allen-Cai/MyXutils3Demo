package com.yhcdhp.cai.daydays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;
import com.yhcdhp.cai.R;
import com.yhcdhp.cai.daydays.config.UMConstants;
import com.yhcdhp.cai.daydays.utils.Utils;

import org.xutils.x;

/**
 * Created by caishengyan on 2016/2/16.
 */
public class SplashScreenActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        /*// 闪屏的核心代码(方法1)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this,
                        MainTabActivity.class); // 从启动动画ui跳转到主ui
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
                SplashScreenActivity.this.finish(); // 结束启动动画界面

            }
        }, 4000); // 启动动画持续3秒钟*/


//        // 取消标题
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        // 取消状态栏
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        //umeng统计
        Utils.setUMengEvent(this, UMConstants.APPLICATION_OPEN);

        String imageurl = "http://img.ivsky.com/img/tupian/pre/201510/26/qiukui-001.jpg";
        setContentView(R.layout.activity_splash);
        ImageView imageView = (ImageView) this.findViewById(R.id.iv_splash);
        x.image().bind(imageView, imageurl);
// 需要另一个imageview并且控制显示图片的时间，
// 要是超过3秒就不显示网络图片，要是网络图片加载小于3秒并且有显示的时间限制，
// 则整体闪屏按照加载时间延长，本处目前不作处理
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(1000);
        imageView.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent();
                intent.setClass(SplashScreenActivity.this, MainTabActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
        MobclickAgent.onPause(this);
    }
}