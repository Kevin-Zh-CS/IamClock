package me.jfenn.alarmio.dialogs;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.afollestad.aesthetic.Aesthetic;
import com.google.android.material.textfield.TextInputEditText;

import me.jfenn.alarmio.R;
import me.jfenn.alarmio.data.HealthReportData;
import me.jfenn.alarmio.data.PreferenceData;

public class HealthConfigDialog extends DialogFragment {
    private View view;
    private Button button_cancel, button_confirm;
    private TextInputEditText text_username, text_password;

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_health_config, container, false);

        Aesthetic.Companion.get()
                .colorPrimary()
                .take(1)
                .subscribe(integer -> view.setBackgroundColor(integer));

        button_cancel = view.findViewById(R.id.button_config_cancel);
        button_confirm = view.findViewById(R.id.button_config_confirm);
        text_username = view.findViewById(R.id.material_input_username);
        text_password = view.findViewById(R.id.material_input_password);

        String username = PreferenceData.HEALTH_REPORT_USERNAME.getValue(view.getContext(), null);
        String password = PreferenceData.HEALTH_REPORT_PASSWORD.getValue(view.getContext(), null);
        if (username != null && !username.isEmpty()) {
            text_username.setText(username);
        }
        if (password != null && !password.isEmpty()) {
            text_password.setText(password);
        }


        button_confirm.setOnClickListener(button_view -> {
            String save_username = text_username.getEditableText().toString();
            String save_password = text_password.getEditableText().toString();
            PreferenceData.HEALTH_REPORT_USERNAME.setValue(view.getContext(), save_username);
            PreferenceData.HEALTH_REPORT_PASSWORD.setValue(view.getContext(), save_password);
            dismiss();
        });

        button_cancel.setOnClickListener(button_view -> {
            dismiss();
        });

        return view;
    }
}