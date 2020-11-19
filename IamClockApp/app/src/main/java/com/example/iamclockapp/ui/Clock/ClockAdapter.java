package com.example.iamclockapp.ui.Clock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iamclockapp.R;

public class ClockCardAdapter extends RecyclerView.Adapter<ClockCardAdapter.ViewHolder> {

    private String[] local_data_set;

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

    public ClockCardAdapter(String[] data_set) {
        local_data_set = data_set;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_clock, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.GetClockText().setText(local_data_set[position]);
    }

    public int getItemCount() {
        return local_data_set.length;
    }
}
