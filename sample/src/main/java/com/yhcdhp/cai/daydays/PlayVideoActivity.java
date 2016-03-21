package com.yhcdhp.cai.daydays;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.MediaController;

import com.yhcdhp.cai.R;

/**
 * Created by caishengyan on 2016/3/21.
 */
public class PlayVideoActivity extends Activity {

    private String videoPath = "http://v1.jiyoutang.com/source/famousTeacher/teacherVideo/2016/01/07/1452165277544.mp4";
    private android.widget.VideoView VideoView;
    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playvideo);
        VideoView = (android.widget.VideoView) findViewById(R.id.VideoView);
        activity = this;
        init();
    }

    private void init() {

        VideoView.setZOrderOnTop(true);
        VideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                VideoView.start();
            }
        });
        VideoView.setMediaController(new MediaController(activity));
//        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/new1.3gp");
        VideoView.setVideoPath(videoPath);
//        VideoView.setVideoURI(uri);
    }
}
