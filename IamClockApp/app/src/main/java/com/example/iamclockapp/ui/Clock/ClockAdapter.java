package com.example.iamclockapp.ui.Clock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.iamclockapp.R;

import java.util.ArrayList;

public class ClockAdapter extends RecyclerView.Adapter<ClockAdapter.ViewHolder> {

    private Context context;
    private ClockFragment clock_fragment;
    private ArrayList<Clock> clock_list;

    public ClockAdapter(Context context, ArrayList<Clock> clock_list) {
        this.context = context;
        this.clock_list = clock_list;
    }

    public ClockAdapter(ClockFragment clock_fragment, ArrayList<Clock> clock_list) {
        this.clock_fragment = clock_fragment;
        this.clock_list = clock_list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView clock_text;
        public ViewHolder(View view) {
            super(view);
            clock_text = (TextView) view.findViewById(R.id.card_text);
        }

        public TextView GetClockText() {
            return clock_text;
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(clock_fragment.getContext())
                .inflate(R.layout.card_clock, parent, false);
        return new ViewHolder(v);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.GetClockText().setText(clock_list.get(position).tag);
    }

    public int getItemCount() {
        return clock_list.size();
    }
}
