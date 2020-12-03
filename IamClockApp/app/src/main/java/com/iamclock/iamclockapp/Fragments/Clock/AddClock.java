package com.iamclock.iamclockapp.Fragments.Clock;

import android.os.Bundle;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.iamclock.iamclockapp.R;

import java.lang.annotation.Retention;

public class AddClock extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_clock);
    }

}
