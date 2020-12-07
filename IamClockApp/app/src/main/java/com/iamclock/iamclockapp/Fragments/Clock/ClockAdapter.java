package com.iamclock.iamclockapp.Fragments.Clock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.iamclock.iamclockapp.R;
import com.iamclock.iamclockapp.Utils.ClockUtils;


public class ClockAdapter extends RecyclerView.Adapter<ClockAdapter.ViewHolder> {

    static final class ViewHolder extends RecyclerView.ViewHolder {

        final TextView label, time, days;
        final ImageView image;
        final Switch clock_switch;

        public ViewHolder(View view) {
            super(view);
            label = view.findViewById(R.id.clock_card_label);
            time = view.findViewById(R.id.clock_card_time);
            days = view.findViewById(R.id.clock_card_days);
            image = view.findViewById(R.id.clock_plate);
            clock_switch = view.findViewById(R.id.clock_enable);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context c = parent.getContext();
        final View v = LayoutInflater.from(c).inflate(R.layout.card_clock, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Context c = holder.itemView.getContext();

        final Clock clock = ClockManager.clock_list.get(position);

        holder.time.setText(ClockUtils.GetReadableTime(clock.GetTime()));
        holder.label.setText(clock.GetLabel());
        holder.days.setText(ClockUtils.GetReadableDays(clock.GetDays()));
        holder.clock_switch.setChecked(clock.GetEnable());
        holder.itemView.setOnClickListener(null);

        // TODO add click listener
    }

    @Override
    public int getItemCount() {
        return ClockManager.clock_list.size();
    }
}
