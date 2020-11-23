package com.github.ppartisan.simplealarms.service;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.github.ppartisan.simplealarms.R;

public class lockscreen extends AppCompatActivity {
    MediaPlayer mMediaPlayer;
    Button stopring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockscreen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        stopring= (Button) findViewById(R.id.stopring);

        startRingtone();

        stopring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.stop();
                Snackbar.make(v, "Alarm Stopped!", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

    }

    private void startRingtone() {
        //play ringtone
        Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mMediaPlayer = MediaPlayer.create(this, ringtone);
        mMediaPlayer.setLooping(true);
        //mMediaPlayer.createVolumeShaper();
        mMediaPlayer.start();
    }
}
