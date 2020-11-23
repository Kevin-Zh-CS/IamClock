package com.iamclock.iamclockapp.Fragments.Clock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iamclock.iamclockapp.R;
import com.iamclock.iamclockapp.Utils.Default;

import java.util.ArrayList;

public class ClockFragment extends Fragment {

    private RecyclerView clock_recycler_view;
    private RecyclerView.LayoutManager clock_layout_manager;
    private ClockAdapter clock_adpter;
    private ArrayList<Clock> clock_list;

    private FloatingActionButton add_button;
    private FloatingActionButton add_clock_button;
    private FloatingActionButton add_flash_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get clock data
        InitClockList();
    }

    private void InitClockList() {
        clock_list = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_clock, container, false);

        // get button handles
        add_button = root.findViewById(R.id.fab_add);
        add_clock_button = root.findViewById(R.id.fab_add_clock);
        add_flash_button = root.findViewById(R.id.fab_add_flash);

        clock_recycler_view = root.findViewById(R.id.rv_clock);
        clock_adpter = new ClockAdapter(this, clock_list);
        clock_layout_manager = new LinearLayoutManager(getContext());
        clock_recycler_view.setLayoutManager(clock_layout_manager);
        clock_recycler_view.setAdapter(clock_adpter);

        FloatingActionButton fab = root.findViewById(R.id.fab_add);
        fab.setOnClickListener(view -> {

            AlphaAnimation fade_in = new AlphaAnimation(0, 1);
            fade_in.setDuration(Default.fade_time);
            fade_in.setFillAfter(true);

            AlphaAnimation fade_out = new AlphaAnimation(1, 0);
            fade_out.setDuration(Default.fade_time);
            fade_out.setFillAfter(true);

            add_button.setAnimation(fade_out);
            add_flash_button.setAnimation(fade_in);
            add_clock_button.setAnimation(fade_in);

            add_button.setVisibility(View.GONE);
            add_clock_button.setVisibility(View.VISIBLE);
            add_flash_button.setVisibility(View.VISIBLE);
        });
        return root;
    }


}