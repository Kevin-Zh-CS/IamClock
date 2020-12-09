package com.iamclock.iamclockapp.Fragments.Clock;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.iamclock.iamclockapp.R;
import com.iamclock.iamclockapp.Utils.ClockUtils;
import com.iamclock.iamclockapp.Utils.CommonUtils;

public class AddEditClock extends AppCompatActivity {
    private boolean edit;

    private TimePicker time_picker;
    private CardView card_view_repeat;
    private CardView card_view_label;
    private Button button_cancel, button_confirm;
    private TextView add_clock_label_text;
    private TextView repeat_text;

    private final CharSequence[] repeat_items = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private boolean[] repeat_choice = {false, false, false, false, false, false, false};
    private boolean[] repeat_choice_tmp = {false, false, false, false, false, false, false};
    private String label = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        edit = getIntent().getBooleanExtra("INTENT_EDIT?", false);
        setContentView(R.layout.add_clock);

        InitTimePicker();
        if (edit)
            repeat_choice_tmp = getIntent().getBooleanArrayExtra("INTENT_REPEAT");
        InitCardViewRepeat();
        InitCancelConfirm();
        InitLabel();
    }


    private void InitTimePicker() {
        // get and set time picker
        time_picker = findViewById(R.id.time_picker);
        time_picker.setIs24HourView(true);

        if (edit) {
            Calendar calendar = Calendar.getInstance();
            int current_hour = calendar.get(Calendar.HOUR_OF_DAY);
            int current_minute = calendar.get(Calendar.MINUTE);
            time_picker.setHour(getIntent().getIntExtra("INTENT_HOUR", current_hour));
            time_picker.setMinute(getIntent().getIntExtra("INTENT_MINUTE", current_minute));
        }
    }

    private void InitCardViewRepeat() {
        // get the repeat card view
        repeat_text = findViewById(R.id.repeat_string);
        repeat_text.setText(ClockUtils.GetReadableDays(repeat_choice_tmp));
        card_view_repeat = findViewById(R.id.add_clock_repeat);
        card_view_repeat.setClickable(true);
        card_view_repeat.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Repeat")
                    .setMultiChoiceItems(repeat_items, repeat_choice_tmp, (dialog, which, isChecked) -> {
                        repeat_choice_tmp[which] = isChecked;
                    })
                    .setPositiveButton("Confirm", (dialogInterface, i) -> {
                        repeat_choice = repeat_choice_tmp.clone();
                        repeat_text.setText(ClockUtils.GetReadableDays(repeat_choice));
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> {
                        repeat_choice_tmp = repeat_choice.clone();
                    }).show();
        });
    }

    private void InitCancelConfirm() {
        button_cancel = findViewById(R.id.cancel_button);
        button_confirm = findViewById(R.id.confirm_button);

        button_cancel.setOnClickListener(v -> finish());

        button_confirm.setOnClickListener(v -> {
            int edited_clock_index = getIntent().getIntExtra("INTENT_INDEX", -1);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, time_picker.getHour());
            calendar.set(Calendar.MINUTE, time_picker.getMinute());

            if (edit && edited_clock_index != -1) {
                Clock edited_clock = ClockManager.clock_list.get(edited_clock_index);
                edited_clock.Set(
                        time_picker.getHour(),
                        time_picker.getMinute(),
                        calendar.getTimeInMillis(),
                        label,
                        repeat_choice,
                        edited_clock.GetEnable());
            } else {
                ClockManager.clock_list.add(new Clock(
                        time_picker.getHour(),
                        time_picker.getMinute(),
                        calendar.getTimeInMillis(),
                        label,
                        repeat_choice,
                        true));
            }

            ClockManager.Sort();
            finish();
        });
    }


    private void InitLabel() {
        card_view_label = findViewById(R.id.add_clock_label);
        add_clock_label_text = findViewById(R.id.add_clock_label_text);
        if (edit)
            add_clock_label_text.setText(getIntent().getStringExtra("INTENT_LABEL"));
        card_view_label.setClickable(true);
        card_view_label.setOnClickListener(v -> {
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setSingleLine(true);
            input.setText(label);

            new MaterialAlertDialogBuilder(this)
                    .setTitle("Set Label")
                    .setView(input)
                    .setPositiveButton("Confirm", (dialogInterface, i) -> {
                        label = input.getText().toString();
                        add_clock_label_text.setText(label.isEmpty() ? "None" : label);
                    }).show();
        });

    }

    // TODO set all label clickable
}