package com.iamclock.iamclockapp.Fragments.Clock;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.iamclock.iamclockapp.R;
import com.iamclock.iamclockapp.Utils.ClockUtils;

import java.util.ArrayList;


public class ClockAdapter extends RecyclerView.Adapter<ClockAdapter.ViewHolder> {

    // === data ===

    private ArrayList<Clock> clock_list;
    private String[] days;

    private Context context;
    private ClockFragment clock_fragment;




    // === constructor ===

    public ClockAdapter(Context context, ArrayList<Clock> clock_list) {
        this.context = context;
        this.clock_list = clock_list;
    }

    public ClockAdapter(ClockFragment clock_fragment, ArrayList<Clock> clock_list) {
        this.clock_fragment = clock_fragment;
        this.clock_list = clock_list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView label, time, days;

        public ViewHolder(View view) {
            super(view);

            label = view.findViewById(R.id.clock_card_label);
            time = view.findViewById(R.id.clock_card_time);
            days = view.findViewById(R.id.clock_card_days);
        }
    }


    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context c = parent.getContext();
        final View v = LayoutInflater.from(c).inflate(R.layout.card_clock, parent, false);
        return new ViewHolder(v);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        final Context c = holder.itemView.getContext();

        final Clock clock = clock_list.get(position);

        holder.time.setText(ClockUtils.GetReadableTime(clock.GetTime()));
        holder.label.setText(clock.GetLabel());
        holder.days.setText(ClockUtils.GetReadableDays(clock.GetDays()));

//        holder.itemView.setOnClickListener(view -> {
//            final Context c1 = view.getContext();
//            final Intent AddClockIntent =
//                    AddClock.buildAddEditAlarmActivityIntent(c1, AddEditAlarmActivity.EDIT_ALARM);
//            AddClockIntent.putExtra(AddClock.ALARM_EXTRA, clock);
//            c1.startActivity(AddClockIntent);
//        });
    }

    public int getItemCount() {
        return clock_list.size();
    }
}