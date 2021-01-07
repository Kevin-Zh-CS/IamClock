package me.jfenn.alarmio.dialogs;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.afollestad.aesthetic.Aesthetic;

import java.util.Calendar;
import java.util.Locale;

import me.jfenn.alarmio.Alarmio;
import me.jfenn.alarmio.R;
import me.jfenn.alarmio.data.AlarmData;
import me.jfenn.alarmio.data.PreferenceData;
import me.jfenn.alarmio.data.SoundData;

public class TimerDialog extends AestheticDialog implements View.OnClickListener {

    private ImageView ringtoneImage;
    private TextView ringtoneText;
    private ImageView vibrateImage;

    private SoundData ringtone;
    private boolean isVibrate = true;

    private TextView time;
    private ImageView backspace;

    private Calendar duration;

    private Alarmio alarmio;
    private FragmentManager manager;

    public TimerDialog(Context context, FragmentManager manager) {
        super(context);
        alarmio = (Alarmio) context.getApplicationContext();
        this.manager = manager;
        ringtone = SoundData.fromString(PreferenceData.DEFAULT_ALARM_RINGTONE.getValue(context, ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_timer);

        ringtoneImage = findViewById(R.id.ringtoneImage);
        ringtoneText = findViewById(R.id.ringtoneText);
        vibrateImage = findViewById(R.id.vibrateImage);

        time = findViewById(R.id.time);
        backspace = findViewById(R.id.backspace);
        duration = Calendar.getInstance();
        duration.set(Calendar.HOUR_OF_DAY, 0);
        duration.set(Calendar.MINUTE, 0);

        time.setText(getTime());

        backspace.setOnClickListener(this);
        findViewById(R.id.one).setOnClickListener(this);
        findViewById(R.id.two).setOnClickListener(this);
        findViewById(R.id.three).setOnClickListener(this);
        findViewById(R.id.four).setOnClickListener(this);
        findViewById(R.id.five).setOnClickListener(this);
        findViewById(R.id.six).setOnClickListener(this);
        findViewById(R.id.seven).setOnClickListener(this);
        findViewById(R.id.eight).setOnClickListener(this);
        findViewById(R.id.nine).setOnClickListener(this);

        ringtoneImage.setImageResource(ringtone != null ? R.drawable.ic_ringtone : R.drawable.ic_ringtone_disabled);
        ringtoneImage.setAlpha(ringtone != null ? 1f : 0.333f);

        if (ringtone != null)
            ringtoneText.setText(ringtone.getName());
        else ringtoneText.setText(R.string.title_sound_none);

        findViewById(R.id.ringtone).setOnClickListener(v -> {
            SoundChooserDialog dialog = new SoundChooserDialog();
            dialog.setListener(sound -> {
                ringtone = sound;
                ringtoneImage.setImageResource(sound != null ? R.drawable.ic_ringtone : R.drawable.ic_ringtone_disabled);
                ringtoneImage.setAlpha(sound != null ? 1f : 0.333f);

                if (sound != null)
                    ringtoneText.setText(sound.getName());
                else ringtoneText.setText(R.string.title_sound_none);
            });
            dialog.show(manager, "");
        });

        findViewById(R.id.vibrate).setOnClickListener(v -> {
            isVibrate = !isVibrate;

            AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(v.getContext(), isVibrate ? R.drawable.ic_none_to_vibrate : R.drawable.ic_vibrate_to_none);
            if (drawable != null) {
                vibrateImage.setImageDrawable(drawable);
                drawable.start();
            } else
                vibrateImage.setImageResource(isVibrate ? R.drawable.ic_vibrate : R.drawable.ic_none);

            vibrateImage.animate().alpha(isVibrate ? 1f : 0.333f).start();

            if (isVibrate)
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        });

        findViewById(R.id.start).setOnClickListener(view -> {
            int hour = duration.get(Calendar.HOUR_OF_DAY);
            int minute = duration.get(Calendar.MINUTE);
            if (hour != 0 || minute != 0) {
                AlarmData quick = alarmio.newQuick();

                quick.setVibrate(view.getContext(), isVibrate);
                quick.setSound(view.getContext(), ringtone);
                Calendar current = Calendar.getInstance();
                current.add(Calendar.HOUR_OF_DAY, duration.get(Calendar.HOUR_OF_DAY));
                current.add(Calendar.MINUTE, duration.get(Calendar.MINUTE));

                AlarmManager manager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

                quick.set(alarmio, manager);
                quick.setTime(alarmio, manager, current.getTimeInMillis());
                quick.setEnabled(getContext(), manager, true);
                alarmio.onAlarmsChanged();

                dismiss();
            }
        });

        findViewById(R.id.cancel).setOnClickListener(view -> dismiss());

        Aesthetic.Companion.get()
                .textColorPrimary()
                .take(1)
                .subscribe(integer -> {
                    ringtoneImage.setColorFilter(integer);
                    vibrateImage.setColorFilter(integer);
                    backspace.setColorFilter(integer);
                });
    }

    private void input(String character) {
        switch (character) {
            case "+1m":
                duration.add(Calendar.MINUTE, 1);
                break;
            case "+5m":
                duration.add(Calendar.MINUTE, 5);
                break;
            case "+10m":
                duration.add(Calendar.MINUTE, 10);
                break;
            case "+15m":
                duration.add(Calendar.MINUTE, 15);
                break;
            case "+30m":
                duration.add(Calendar.MINUTE, 30);
                break;
            case "+45m":
                duration.add(Calendar.MINUTE, 45);
                break;
            case "+1h":
                duration.add(Calendar.HOUR_OF_DAY, 1);
                break;
            case "+2h":
                duration.add(Calendar.HOUR_OF_DAY, 2);
                break;
            case "+5h":
                duration.add(Calendar.HOUR_OF_DAY, 5);
                break;
        }
        time.setText(getTime());
    }

    private void backspace() {
        duration.set(Calendar.HOUR_OF_DAY, 0);
        duration.set(Calendar.MINUTE, 0);
        time.setText(getTime());
    }

    private String getTime() {
        int hours = duration.get(Calendar.HOUR_OF_DAY);
        int minutes = duration.get(Calendar.MINUTE);

        backspace.setVisibility(hours == 0 && minutes == 0 ? View.GONE : View.VISIBLE);

        if (hours > 0)
            return String.format(Locale.getDefault(), "%dh %02dm", hours, minutes);
        else return String.format(Locale.getDefault(), "%dm", minutes);
    }

    @Override
    public void onClick(View view) {
        if (view instanceof TextView)
            input(((TextView) view).getText().toString());
        else backspace();
    }
}
