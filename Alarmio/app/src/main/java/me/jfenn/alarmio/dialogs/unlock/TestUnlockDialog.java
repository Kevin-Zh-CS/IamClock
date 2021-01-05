package me.jfenn.alarmio.dialogs.unlock;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;

import me.jfenn.alarmio.R;
import me.jfenn.alarmio.activities.AlarmActivity;

public class TestUnlockDialog extends DialogFragment {
    private View view;
    AlarmActivity alarmActivity;
    MaterialButton unlock_button;

    public TestUnlockDialog(AlarmActivity alarmActivity) {
        this.alarmActivity = alarmActivity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                if (params != null) {
                    params.windowAnimations = R.style.SlideDialogAnimation;
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.dialog_test_unlock, container, false);
        unlock_button = view.findViewById(R.id.unlock_button);

        unlock_button.setOnClickListener(button_view -> {
            alarmActivity.finish();
            dismiss();
        });
        return view;
    }
}
