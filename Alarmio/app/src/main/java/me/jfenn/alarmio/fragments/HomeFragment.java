package me.jfenn.alarmio.fragments;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.aesthetic.Aesthetic;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;

import io.reactivex.disposables.Disposable;
import jahirfiquitiva.libs.fabsmenu.FABsMenu;
import jahirfiquitiva.libs.fabsmenu.FABsMenuListener;
import jahirfiquitiva.libs.fabsmenu.TitleFAB;
import me.jfenn.alarmio.Alarmio;
import me.jfenn.alarmio.R;
import me.jfenn.alarmio.adapters.SimplePagerAdapter;
import me.jfenn.alarmio.data.AlarmData;
import me.jfenn.alarmio.dialogs.AestheticTimeSheetPickerDialog;
import me.jfenn.alarmio.dialogs.TimerDialog;
import me.jfenn.alarmio.fragments.Dashboard.DashboardFragment;
import me.jfenn.timedatepickers.dialogs.PickerDialog;
import me.jfenn.timedatepickers.views.LinearTimePickerView;

public class HomeFragment extends BaseFragment {

    public static final String INTENT_ACTION = "me.jfenn.alarmio.HomeFragment.INTENT_ACTION";

    private View view;
    private FABsMenu menu;
    private TitleFAB timerFab;
    private TitleFAB alarmFab;


    private Disposable colorAccentSubscription;
    private Disposable textColorPrimarySubscription;
    private Disposable textColorPrimaryInverseSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        menu = view.findViewById(R.id.fabsMenu);
        timerFab = view.findViewById(R.id.timerFab);
        alarmFab = view.findViewById(R.id.alarmFab);


        SimplePagerAdapter pagerAdapter = new SimplePagerAdapter(
                getContext(), getChildFragmentManager(),
                new AlarmsFragment.Instantiator(getContext()),
                new DashboardFragment.Instantiator(getContext()),
                new SettingsFragment.Instantiator(getContext())
        );


        assert getAlarmio() != null;
        getAlarmio().setAdapter(pagerAdapter);

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    System.out.println("选中了dashboard");
                }
                if (tab.getPosition() > 0) {
                    menu.hide();
                } else {
                    menu.show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


        colorAccentSubscription = Aesthetic.Companion.get()
                .colorAccent()
                .subscribe(integer -> {
                    menu.setMenuButtonColor(integer);

                    int color = ContextCompat.getColor(getContext(), R.color.textColorPrimary);
                    menu.getMenuButton().setColorFilter(color);
                    timerFab.setColorFilter(color);
                    alarmFab.setColorFilter(color);

                    timerFab.setBackgroundColor(integer);
                    alarmFab.setBackgroundColor(integer);
                });

        textColorPrimarySubscription = Aesthetic.Companion.get()
                .textColorPrimary()
                .subscribe(integer -> {
                    timerFab.setTitleTextColor(integer);
                    alarmFab.setTitleTextColor(integer);
                });

        textColorPrimaryInverseSubscription = Aesthetic.Companion.get()
                .textColorPrimaryInverse()
                .subscribe(integer -> {
                    alarmFab.setTitleBackgroundColor(integer);
                    timerFab.setTitleBackgroundColor(integer);
                });

        timerFab.setOnClickListener(view -> {
            invokeTimerScheduler();
            menu.collapse();
        });

        alarmFab.setOnClickListener(view -> {
            invokeAlarmScheduler();
            menu.collapse();
        });

        menu.setMenuListener(new FABsMenuListener() {
            @Override
            public void onMenuExpanded(FABsMenu fabsMenu) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED)
                        requestPermissions(new String[]{Manifest.permission.FOREGROUND_SERVICE}, 0);
                    else fabsMenu.collapseImmediately();
                }
            }
        });

        // check actions passed from MainActivity; open timer/alarm schedulers if necessary
        Bundle args = getArguments();
        String action = args != null ? args.getString(INTENT_ACTION, null) : null;
        if (AlarmClock.ACTION_SET_ALARM.equals(action)) {
            view.post(() -> invokeAlarmScheduler());
        } else if (AlarmClock.ACTION_SET_TIMER.equals(action)) {
            view.post(() -> invokeTimerScheduler());
        }

        return view;
    }

    /**
     * Open the alarm scheduler dialog to allow the user to create
     * a new alarm.
     */
    private void invokeAlarmScheduler() {
        new AestheticTimeSheetPickerDialog(view.getContext())
                .setListener(new PickerDialog.OnSelectedListener<LinearTimePickerView>() {
                    @Override
                    public void onSelect(PickerDialog<LinearTimePickerView> dialog, LinearTimePickerView view) {
                        AlarmManager manager = (AlarmManager) view.getContext().getSystemService(Context.ALARM_SERVICE);
                        AlarmData alarm = getAlarmio().newAlarm();
                        alarm.time.set(Calendar.HOUR_OF_DAY, view.getHourOfDay());
                        alarm.time.set(Calendar.MINUTE, view.getMinute());
                        alarm.setTime(getAlarmio(), manager, alarm.time.getTimeInMillis());
                        alarm.setEnabled(getContext(), manager, true);

                        getAlarmio().onAlarmsChanged();
                    }

                    @Override
                    public void onCancel(PickerDialog<LinearTimePickerView> dialog) {
                    }
                })
                .show();
    }

    /**
     * Open the timer scheduler dialog to allow the user to start
     * a timer.
     */
    private void invokeTimerScheduler() {
        new TimerDialog(getContext(), getFragmentManager())
                .show();
    }


    @Override
    public void onDestroyView() {
        colorAccentSubscription.dispose();
        textColorPrimarySubscription.dispose();
        textColorPrimaryInverseSubscription.dispose();
        super.onDestroyView();
    }

}
