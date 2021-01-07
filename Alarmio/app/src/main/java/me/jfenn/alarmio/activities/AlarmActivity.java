package me.jfenn.alarmio.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.AestheticActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import io.reactivex.disposables.Disposable;
import me.jfenn.alarmio.Alarmio;
import me.jfenn.alarmio.R;
import me.jfenn.alarmio.data.AlarmData;
import me.jfenn.alarmio.data.PreferenceData;
import me.jfenn.alarmio.data.SoundData;
import me.jfenn.alarmio.data.TimerData;
import me.jfenn.alarmio.utils.FormatUtils;
import me.jfenn.slideactionview.SlideActionListener;
import me.jfenn.slideactionview.SlideActionView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AlarmActivity extends AestheticActivity implements SlideActionListener {

    public static final String EXTRA_ALARM = "james.alarmio.AlarmActivity.EXTRA_ALARM";
    public static final String EXTRA_TIMER = "james.alarmio.AlarmActivity.EXTRA_TIMER";


    private View overlay;
    private TextView date;
    private TextView time;
    private SlideActionView actionView;

    private Alarmio alarmio;
    private Vibrator vibrator;
    private AudioManager audioManager;

    private boolean isAlarm;
    private long triggerMillis;
    private AlarmData alarm;
    private TimerData timer;
    private SoundData sound;
    private boolean isVibrate;

    private boolean isSlowWake;
    private long slowWakeMillis;

    private int currentVolume;
    private int minVolume;
    private int originalVolume;
    private int volumeRange;

    private Handler handler;
    private Runnable runnable;
    private boolean isWoken;
    private PowerManager.WakeLock wakeLock;

    private Disposable textColorPrimaryInverseSubscription;
    private Disposable isDarkSubscription;

    private boolean isDark;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        alarmio = (Alarmio) getApplicationContext();

        overlay = findViewById(R.id.overlay);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        actionView = findViewById(R.id.slideView);
        sharedPreferences = Objects.requireNonNull(getSharedPreferences("USER_INFO", Context.MODE_MULTI_PROCESS));
        editor = sharedPreferences.edit();

        // Lock orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        textColorPrimaryInverseSubscription = Aesthetic.Companion.get()
                .textColorPrimaryInverse()
                .subscribe(integer -> overlay.setBackgroundColor(integer));

        isDarkSubscription = Aesthetic.Companion.get()
                .isDark()
                .subscribe(aBoolean -> isDark = aBoolean);

        actionView.setRightIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_close, getTheme()));
        actionView.setLeftIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_close, getTheme()));
        actionView.setListener(this);

        isSlowWake = PreferenceData.SLOW_WAKE_UP.getValue(this);
        slowWakeMillis = PreferenceData.SLOW_WAKE_UP_TIME.getValue(this);

        isAlarm = getIntent().hasExtra(EXTRA_ALARM);
        if (isAlarm) {
            alarm = getIntent().getParcelableExtra(EXTRA_ALARM);
            isVibrate = alarm.isVibrate;
            if (alarm.hasSound())
                sound = alarm.getSound();
        } else if (getIntent().hasExtra(EXTRA_TIMER)) {
            timer = getIntent().getParcelableExtra(EXTRA_TIMER);
            isVibrate = timer.isVibrate;
            if (timer.hasSound())
                sound = timer.getSound();
        } else finish();

        date.setText(FormatUtils.format(new Date(), FormatUtils.FORMAT_DATE + ", " + FormatUtils.getShortFormat(this)));

        if (sound != null && !sound.isSetVolumeSupported()) {
            // Use the backup method if it is not supported

            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
            if (isSlowWake) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    minVolume = audioManager.getStreamMinVolume(AudioManager.STREAM_ALARM);
                } else {
                    minVolume = 0;
                }
                volumeRange = originalVolume - minVolume;
                currentVolume = minVolume;

                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, minVolume, 0);
            }
        }

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        triggerMillis = System.currentTimeMillis();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                long elapsedMillis = System.currentTimeMillis() - triggerMillis;
                String text = FormatUtils.formatMillis(elapsedMillis);
                time.setText(String.format("-%s", text.substring(0, text.length() - 3)));

                if (isVibrate) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    else vibrator.vibrate(500);
                }

                if (sound != null && !sound.isPlaying(alarmio))
                    sound.play(alarmio);

                if (alarm != null && isSlowWake) {
                    float slowWakeProgress = (float) elapsedMillis / slowWakeMillis;

                    WindowManager.LayoutParams params = getWindow().getAttributes();
                    params.screenBrightness = Math.max(0.01f, Math.min(1f, slowWakeProgress));
                    getWindow().setAttributes(params);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAGS_CHANGED);

                    if (sound != null && sound.isSetVolumeSupported()) {
                        float newVolume = Math.min(1f, slowWakeProgress);

                        sound.setVolume(alarmio, newVolume);
                    } else if (currentVolume < originalVolume) {
                        // Backup volume setting behavior
                        int newVolume = minVolume + (int) Math.min(originalVolume, slowWakeProgress * volumeRange);
                        if (newVolume != currentVolume) {
                            audioManager.setStreamVolume(audioManager.STREAM_ALARM, newVolume, 0);
                            currentVolume = newVolume;
                        }
                    }
                }

                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);

        if (sound != null)
            sound.play(alarmio);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textColorPrimaryInverseSubscription != null && isDarkSubscription != null) {
            textColorPrimaryInverseSubscription.dispose();
            isDarkSubscription.dispose();
        }
        stopAnnoyingness();

    }

    private void stopAnnoyingness() {
        if (handler != null)
            handler.removeCallbacks(runnable);

        if (sound != null && sound.isPlaying(alarmio)) {
            sound.stop(alarmio);

            if (isSlowWake && !sound.isSetVolumeSupported()) {
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, originalVolume, 0);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        finish();
        startActivity(new Intent(intent));
    }

    @Override
    public void onSlideLeft() {
        overlay.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        doAfterClose(this);
    }

    @Override
    public void onSlideRight() {
        overlay.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        doAfterClose(this);
    }

    private void doAfterClose(AlarmActivity alarmActivity) {
        // TODO daka
        if (alarm.isReport) {
            alarmio.getHealthReport().Report(alarmio, alarmActivity);
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//设置日期格式
        //System.out.println(dateFormat.format(new Date()));// new Date()为获取当前系统时间
        String format = dateFormat.format(new Date());
        String username = sharedPreferences.getString("USER_NAME", null);
        String url = "http://47.111.80.33:8092/user/update?header=" + username + "&time=" + format;
        System.out.println(url);
        if (username != null) {
            new Thread(() -> {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                try {
                    client.newCall(request).execute();
                    Response response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            alarmio.getAdapter().notifyDataSetChanged();
        }


        //个人最近七次起床时间
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        editor.putString(TimerData.MONDAY, sharedPreferences.getString(TimerData.TUESDAY, "00:00"));
        editor.putString(TimerData.TUESDAY, sharedPreferences.getString(TimerData.WEDNESDAY, "00:00"));
        editor.putString(TimerData.WEDNESDAY, sharedPreferences.getString(TimerData.THURSDAY, "00:00"));
        editor.putString(TimerData.THURSDAY, sharedPreferences.getString(TimerData.FRIDAY, "00:00"));
        editor.putString(TimerData.FRIDAY, sharedPreferences.getString(TimerData.SATURDAY, "00:00"));
        editor.putString(TimerData.SATURDAY, sharedPreferences.getString(TimerData.SUNDAY, "00:00"));
        editor.putString(TimerData.SUNDAY, simpleDateFormat.format(new Date()));
        editor.commit();

        alarmActivity.finish();


//        FragmentManager fragmentManager = alarmio.getFragmentManager();
//        if (fragmentManager != null) {
//            TestUnlockDialog dialog = new TestUnlockDialog(alarmActivity);
//            dialog.show(fragmentManager, null);
//        }
    }
}
