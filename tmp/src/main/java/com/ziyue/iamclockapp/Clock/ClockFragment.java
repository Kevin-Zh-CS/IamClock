package com.ziyue.iamclockapp.Clock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziyue.iamclockapp.R;

import java.util.ArrayList;

public class ClockFragment extends Fragment {

    private RecyclerView clock_recycler_view;
    private RecyclerView.LayoutManager clock_layout_manager;
    private ClockAdapter clock_adpter;
    private ArrayList<Clock> clock_list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitClockList();
    }

    private void InitClockList() {

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

//        FloatingActionButton fab = root.findViewById(R.id.fab_clock);
//        fab.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                //TODO: create a new clock
//            }
//        });
        return root;
    }


}