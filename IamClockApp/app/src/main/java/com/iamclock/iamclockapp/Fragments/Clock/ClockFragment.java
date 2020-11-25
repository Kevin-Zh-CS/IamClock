package com.iamclock.iamclockapp.Fragments.Clock;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iamclock.iamclockapp.R;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;

public class ClockFragment extends Fragment {

    private RecyclerView clock_recycler_view;
    private RecyclerView.LayoutManager clock_layout_manager;
    private ClockAdapter clock_adpter;
    private ArrayList<Clock> clock_list;

    private SpeedDialView sdv;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        InitClockList();
    }

    // TODO get clock data
    private void InitClockList() {
        clock_list = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_clock, container, false);
        clock_recycler_view = root.findViewById(R.id.rv_clock);
        clock_adpter = new ClockAdapter(this, clock_list);
        clock_layout_manager = new LinearLayoutManager(getContext());
        clock_recycler_view.setLayoutManager(clock_layout_manager);
        clock_recycler_view.setAdapter(clock_adpter);

        sdv = root.findViewById(R.id.speed_dial);
        InitSpeedDial();


        return root;
    }

    // TODO SpeedDialView
    private void InitSpeedDial() {
        sdv.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_add_alarm, R.drawable.ic_add_alarm)
                .setFabBackgroundColor(getResources().getColor(R.color.white))
                .setLabel(getString(R.string.alarm))
                .setLabelColor(Color.BLACK)
                .setLabelBackgroundColor(getResources().getColor(R.color.white))
                .setFabImageTintColor(getResources().getColor(R.color.Accent))
                .create());
        sdv.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_add_bolt, R.drawable.ic_add_bolt)
                .setFabBackgroundColor(getResources().getColor(R.color.white))
                .setLabel(getString(R.string.quick_alarm))
                .setLabelColor(Color.BLACK)
                .setLabelBackgroundColor(getResources().getColor(R.color.white))
                .setFabImageTintColor(getResources().getColor(R.color.Accent))
                .create());


        sdv.setOnActionSelectedListener(actionItem -> {
            switch (actionItem.getId()) {
                case R.id.fab_add_alarm:
                    sdv.close(); // To close the Speed Dial with animation
                    System.out.println("add_alarm");
                    break;
                case R.id.fab_add_bolt:
                    sdv.close(); // To close the Speed Dial with animation
                    System.out.println("add_bolt");
                    break;

            }
            return false;
        });
    }



    // TODO no clock vector
    // TODO quick clock plate
}