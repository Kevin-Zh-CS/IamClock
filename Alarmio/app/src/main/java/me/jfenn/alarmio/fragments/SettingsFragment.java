package me.jfenn.alarmio.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.aesthetic.Aesthetic;

import java.util.ArrayList;
import java.util.Arrays;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.jfenn.alarmio.R;
import me.jfenn.alarmio.adapters.PreferenceAdapter;
import me.jfenn.alarmio.data.PreferenceData;
import me.jfenn.alarmio.data.preference.AboutPreferenceData;
import me.jfenn.alarmio.data.preference.AlertWindowPreferenceData;
import me.jfenn.alarmio.data.preference.BasePreferenceData;
import me.jfenn.alarmio.data.preference.BatteryOptimizationPreferenceData;
import me.jfenn.alarmio.data.preference.BooleanPreferenceData;
import me.jfenn.alarmio.data.preference.HealthPreferenceData;
import me.jfenn.alarmio.data.preference.LoginPreferenceData;
import me.jfenn.alarmio.data.preference.RingtonePreferenceData;
import me.jfenn.alarmio.data.preference.TimePreferenceData;
import me.jfenn.alarmio.interfaces.ContextFragmentInstantiator;

public class SettingsFragment extends BasePagerFragment implements Consumer {

    private RecyclerView recyclerView;

    private PreferenceAdapter preferenceAdapter;

    private Disposable colorPrimarySubscription;
    private Disposable textColorPrimarySubscription;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recycler, container, false);
        recyclerView = v.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        ArrayList<BasePreferenceData> list = new ArrayList<BasePreferenceData>(Arrays.asList(
                new BooleanPreferenceData(PreferenceData.HEALTH, R.string.title_health, R.string.desc_health),
                new HealthPreferenceData(PreferenceData.HEALTH_SUBMIT, R.string.title_submit),
                new RingtonePreferenceData(PreferenceData.DEFAULT_ALARM_RINGTONE, R.string.title_default_alarm_ringtone),
                new RingtonePreferenceData(PreferenceData.DEFAULT_TIMER_RINGTONE, R.string.title_default_timer_ringtone),
                new BooleanPreferenceData(PreferenceData.SLOW_WAKE_UP, R.string.title_slow_wake_up, R.string.desc_slow_wake_up),
                new TimePreferenceData(PreferenceData.SLOW_WAKE_UP_TIME, R.string.title_slow_wake_up_time)
        ));

        if (Build.VERSION.SDK_INT >= 23) {
            list.add(0, new BatteryOptimizationPreferenceData());
            list.add(0, new AlertWindowPreferenceData());
            list.add(0, new LoginPreferenceData(PreferenceData.LOGIN_INFO, R.string.title_login));
        }

        list.add(new AboutPreferenceData());

        preferenceAdapter = new PreferenceAdapter(list);
        recyclerView.setAdapter(preferenceAdapter);

        colorPrimarySubscription = Aesthetic.Companion.get()
                .colorPrimary()
                .subscribe(this);

        textColorPrimarySubscription = Aesthetic.Companion.get()
                .textColorPrimary()
                .subscribe(this);

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        colorPrimarySubscription.dispose();
        textColorPrimarySubscription.dispose();
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.title_settings);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (recyclerView != null && preferenceAdapter != null) {
            recyclerView.post(() -> preferenceAdapter.notifyDataSetChanged());
        }
    }

    @Override
    public void accept(Object o) throws Exception {
        if (recyclerView != null && preferenceAdapter != null) {
            recyclerView.post(() -> preferenceAdapter.notifyDataSetChanged());
        }
    }

    public static class Instantiator extends ContextFragmentInstantiator {

        public Instantiator(Context context) {
            super(context);
        }

        @Override
        public String getTitle(Context context, int position) {
            return context.getString(R.string.title_settings);
        }

        @Nullable
        @Override
        public BasePagerFragment newInstance(int position) {
            return new SettingsFragment();
        }
    }

}
