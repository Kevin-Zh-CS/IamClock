package me.jfenn.alarmio;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.multidex.MultiDexApplication;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.AutoSwitchMode;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import me.jfenn.alarmio.adapters.SimplePagerAdapter;
import me.jfenn.alarmio.data.AlarmData;
import me.jfenn.alarmio.data.HealthReportData;
import me.jfenn.alarmio.data.PreferenceData;
import me.jfenn.alarmio.data.SoundData;
import me.jfenn.alarmio.data.TimerData;
import me.jfenn.alarmio.utils.DebugUtils;

public class Alarmio extends MultiDexApplication implements Player.EventListener {

    public static final String NOTIFICATION_CHANNEL_TIMERS = "timers";

    private SharedPreferences prefs;

    private Ringtone currentRingtone;

    private List<AlarmData> alarms;
    private List<TimerData> timers;

    private List<AlarmioListener> listeners;
    private ActivityListener listener;

    private SimpleExoPlayer player;
    private HlsMediaSource.Factory hlsMediaSourceFactory;
    private ProgressiveMediaSource.Factory progressiveMediaSourceFactory;
    private String currentStream;
    private HealthReportData health_report;

    private SimplePagerAdapter simplePagerAdapter;

    public void setAdapter(SimplePagerAdapter simplePagerAdapter) {
        this.simplePagerAdapter = simplePagerAdapter;
    }

    public SimplePagerAdapter getAdapter() {
        return this.simplePagerAdapter;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DebugUtils.setup(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        listeners = new ArrayList<>();
        alarms = new ArrayList<>();
        timers = new ArrayList<>();
        health_report = new HealthReportData();

        player = new SimpleExoPlayer.Builder(this).build();
        player.addListener(this);

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), null);
        hlsMediaSourceFactory = new HlsMediaSource.Factory(dataSourceFactory);
        progressiveMediaSourceFactory = new ProgressiveMediaSource.Factory(dataSourceFactory);

        int alarmLength = PreferenceData.ALARM_LENGTH.getValue(this);
        for (int id = 0; id < alarmLength; id++) {
            alarms.add(new AlarmData(id, this));
        }
    }

    public List<AlarmData> getAlarms() {
        return alarms;
    }

    public List<TimerData> getTimers() {
        return timers;
    }

    /**
     * Create a new alarm, assigning it an unused preference id.
     *
     * @return The newly instantiated [AlarmData](./data/AlarmData).
     */
    public AlarmData newAlarm() {
        AlarmData alarm = new AlarmData(alarms.size(), Calendar.getInstance(), false);
        alarm.sound = SoundData.fromString(PreferenceData.DEFAULT_ALARM_RINGTONE.getValue(this, ""));
        alarms.add(alarm);
        onAlarmCountChanged();
        return alarm;
    }


    /**
     * Remove an alarm and all of its its preferences.
     *
     * @param alarm The alarm to be removed.
     */
    public void removeAlarm(AlarmData alarm) {
        alarm.onRemoved(this);

        int index = alarms.indexOf(alarm);
        alarms.remove(index);
        for (int i = index; i < alarms.size(); i++) {
            alarms.get(i).onIdChanged(i, this);
        }

        onAlarmCountChanged();
        onAlarmsChanged();
    }

    /**
     * Update preferences to show that the alarm count has been changed.
     */
    public void onAlarmCountChanged() {
        PreferenceData.ALARM_LENGTH.setValue(this, alarms.size());
    }

    /**
     * Notify the application of changes to the current alarms.
     */
    public void onAlarmsChanged() {
        for (AlarmioListener listener : listeners) {
            listener.onAlarmsChanged();
        }
    }

    /**
     * Create a new timer, assigning it an unused preference id.
     *
     * @return The newly instantiated [TimerData](./data/TimerData).
     */
    public AlarmData newQuick() {
        AlarmData alarm = new AlarmData(alarms.size(), Calendar.getInstance(), true);
        alarm.setQuick(this, true);
        boolean[] days = new boolean[7];
        Arrays.fill(days, Boolean.FALSE);
        alarm.setDays(this, days);
        alarm.sound = SoundData.fromString(PreferenceData.DEFAULT_ALARM_RINGTONE.getValue(this, ""));
        alarms.add(alarm);
        onAlarmCountChanged();
        return alarm;
    }

    /**
     * Remove a timer and all of its preferences.
     *
     * @param timer The timer to be removed.
     */
    public void removeTimer(TimerData timer) {
        timer.onRemoved(this);

        int index = timers.indexOf(timer);
        timers.remove(index);
        for (int i = index; i < timers.size(); i++) {
            timers.get(i).onIdChanged(i, this);
        }

        onTimerCountChanged();
        onTimersChanged();
    }

    /**
     * Update the preferences to show that the timer count has been changed.
     */
    public void onTimerCountChanged() {
        PreferenceData.TIMER_LENGTH.setValue(this, timers.size());
    }

    /**
     * Notify the application of changes to the current timers.
     */
    public void onTimersChanged() {
        for (AlarmioListener listener : listeners) {
            listener.onTimersChanged();
        }
    }

    /**
     * Update the application theme.
     */
    public void updateTheme() {
        Aesthetic.Companion.get()
                .isDark(false)
                .lightStatusBarMode(AutoSwitchMode.ON)
                .colorPrimary(ContextCompat.getColor(this, R.color.colorPrimary))
                .colorStatusBar(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? Color.TRANSPARENT : ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .colorNavigationBar(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .colorAccent(ContextCompat.getColor(this, R.color.colorAccent))
                .colorCardViewBackground(ContextCompat.getColor(this, R.color.colorForeground))
                .colorWindowBackground(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .textColorPrimary(ContextCompat.getColor(this, R.color.textColorPrimary))
                .textColorSecondary(ContextCompat.getColor(this, R.color.textColorSecondary))
                .textColorPrimaryInverse(ContextCompat.getColor(this, R.color.textColorPrimaryNight))
                .textColorSecondaryInverse(ContextCompat.getColor(this, R.color.textColorSecondaryNight))
                .apply();
    }

    /**
     * Determine if a ringtone is currently playing.
     *
     * @return True if a ringtone is currently playing.
     */
    public boolean isRingtonePlaying() {
        return currentRingtone != null && currentRingtone.isPlaying();
    }

    /**
     * Get the currently playing ringtone.
     *
     * @return The currently playing ringtone, or null.
     */
    @Nullable
    public Ringtone getCurrentRingtone() {
        return currentRingtone;
    }

    public void playRingtone(Ringtone ringtone) {
        if (!ringtone.isPlaying()) {
            stopCurrentSound();
            ringtone.play();
        }

        currentRingtone = ringtone;
    }

    /**
     * Play a stream ringtone.
     *
     * @param url The URL of the stream to be passed to ExoPlayer.
     * @see [ExoPlayer Repo](https://github.com/google/ExoPlayer)
     */
    private void playStream(String url, String type, MediaSourceFactory factory) {
        stopCurrentSound();

        // Error handling, including when this is a progressive stream
        // rather than a HLS stream, is in onPlayerError
        player.prepare(factory.createMediaSource(Uri.parse(url)));
        player.setPlayWhenReady(true);

        currentStream = url;
    }

    /**
     * Play a stream ringtone.
     *
     * @param url The URL of the stream to be passed to ExoPlayer.
     * @see [ExoPlayer Repo](https://github.com/google/ExoPlayer)
     */
    public void playStream(String url, String type) {
        playStream(url, type, hlsMediaSourceFactory);
    }

    /**
     * Play a stream ringtone.
     *
     * @param url        The URL of the stream to be passed to ExoPlayer.
     * @param attributes The attributes to play the stream with.
     * @see [ExoPlayer Repo](https://github.com/google/ExoPlayer)
     */
    public void playStream(String url, String type, AudioAttributes attributes) {
        player.stop();
        player.setAudioAttributes(attributes);
        playStream(url, type);
    }

    /**
     * Stop the currently playing stream.
     */
    public void stopStream() {
        player.stop();
        currentStream = null;
    }

    /**
     * Sets the player volume to the given float.
     *
     * @param volume The volume between 0 and 1
     */
    public void setStreamVolume(float volume) {
        player.setVolume(volume);
    }

    /**
     * Determine if the passed url matches the stream that is currently playing.
     *
     * @param url The URL to match the current stream to.
     * @return True if the URL matches that of the currently playing
     * stream.
     */
    public boolean isPlayingStream(String url) {
        return currentStream != null && currentStream.equals(url);
    }

    /**
     * Stop the currently playing sound, regardless of whether it is a ringtone
     * or a stream.
     */
    public void stopCurrentSound() {
        if (isRingtonePlaying())
            currentRingtone.stop();

        stopStream();
    }

    public void addListener(AlarmioListener listener) {
        listeners.add(listener);
    }

    public void removeListener(AlarmioListener listener) {
        listeners.remove(listener);
    }

    public void setListener(ActivityListener listener) {
        this.listener = listener;

        if (listener != null)
            updateTheme();
    }


    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case Player.STATE_BUFFERING:
            case Player.STATE_READY:
                // We are idle while switching from HLS to Progressive streaming
            case Player.STATE_IDLE:
                break;
            default:
                currentStream = null;
                break;
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        String lastStream = currentStream;
        currentStream = null;
        Exception exception;
        switch (error.type) {
            case ExoPlaybackException.TYPE_RENDERER:
                exception = error.getRendererException();
                break;
            case ExoPlaybackException.TYPE_SOURCE:
                if (lastStream != null && error.getSourceException().getMessage().contains("does not start with the #EXTM3U header")) {
                    playStream(lastStream, SoundData.TYPE_RADIO, progressiveMediaSourceFactory);
                    return;
                }
                exception = error.getSourceException();
                break;
            case ExoPlaybackException.TYPE_UNEXPECTED:
                exception = error.getUnexpectedException();
                break;
            default:
                return;
        }

        exception.printStackTrace();
        Toast.makeText(this, exception.getClass().getName() + ": " + exception.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    @Override
    public void onSeekProcessed() {
    }

    public void requestPermissions(String... permissions) {
        if (listener != null)
            listener.requestPermissions(permissions);
    }

    public FragmentManager getFragmentManager() {
        if (listener != null)
            return listener.gettFragmentManager();
        else return null;
    }

    public interface AlarmioListener {
        void onAlarmsChanged();

        void onTimersChanged();
    }

    public interface ActivityListener {
        void requestPermissions(String... permissions);

        FragmentManager gettFragmentManager(); //help
    }

    public HealthReportData getHealthReport() {
        return health_report;
    }

}
