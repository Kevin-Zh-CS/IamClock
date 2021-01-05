package me.jfenn.alarmio.dialogs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.jfenn.alarmio.R;
import me.jfenn.alarmio.data.PreferenceData;
import me.jfenn.alarmio.data.preference.CustomPreferenceData;

public class AfterLoginDialog extends CustomPreferenceData {

    private String username;

    public AfterLoginDialog(int name) {
        super(name);
    }

    public AfterLoginDialog(PreferenceData loginInfo, String string) {
        super(R.string.login);
        this.username = string;
    }

    @Override
    public void bindViewHolder(@NotNull ViewHolder holder) {
        super.bindViewHolder(holder);
        holder.getNameView().setText(username);
    }

    @Nullable
    @Override
    public String getValueName(@NotNull ViewHolder holder) {
        return null;
    }

    @Override
    public void onClick(@NotNull ViewHolder holder) {
        // TODO after login click listener
    }
}
