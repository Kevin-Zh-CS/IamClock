package com.iamclock.iamclockapp.Fragments.Clock;

import android.os.Bundle;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.iamclock.iamclockapp.R;

import java.lang.annotation.Retention;

public class AddClock extends AppCompatActivity {

//    public static final String CLOCK_EXTRA = "clock_extra";
//    public static final String MODE_EXTRA = "mode_extra";
//
//    @Retention(RetentionPolicy.SOURCE)
//    @IntDef({EDIT_CLOCK,ADD_CLOCK,UNKNOWN})
//    @interface Mode{}
//    public static final int EDIT_CLOCK = 1;
//    public static final int ADD_CLOCK = 2;
//    public static final int UNKNOWN = 0;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_clock);
//
//        //noinspection ConstantConditions
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle(getToolbarTitle());
//
//        final Clock clock = GetClock();
//
//        if(getSupportFragmentManager().findFragmentById(R.id.edit_clock_frag_container) == null) {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .add(R.id.edit_clock_frag_container, AddEditClockFragment.newInstance(clock))
//                    .commit();
//        }
//
//    }
//
//    private Clock GetClock() {
//        switch (GetMode()) {
//            case EDIT_CLOCK:
//                return getIntent().getParcelableExtra(CLOCK_EXTRA);
//            case ADD_CLOCK:
//                final long id = DatabaseHelper.getInstance(this).addClock();
//                LoadClocksService.launchLoadClocksService(this);
//                return new Clock(id);
//            case UNKNOWN:
//            default:
//                throw new IllegalStateException("Mode supplied as intent extra for " +
//                        AddEditClockActivity.class.getSimpleName() + " must match value in " +
//                        Mode.class.getSimpleName());
//        }
//    }
//
//    private @Mode int GetMode() {
//        final @Mode int mode = getIntent().getIntExtra(MODE_EXTRA, UNKNOWN);
//        return mode;
//    }
//
//    private String getToolbarTitle() {
//        int titleResId;
//        switch (GetMode()) {
//            case EDIT_CLOCK:
//                titleResId = R.string.edit_clock;
//                break;
//            case ADD_CLOCK:
//                titleResId = R.string.add_clock;
//                break;
//            case UNKNOWN:
//            default:
//                throw new IllegalStateException("Mode supplied as intent extra for " +
//                        AddEditClockActivity.class.getSimpleName() + " must match value in " +
//                        Mode.class.getSimpleName());
//        }
//        return getString(titleResId);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    public static Intent buildAddEditClockActivityIntent(Context context, @Mode int mode) {
//        final Intent i = new Intent(context, AddEditClockActivity.class);
//        i.putExtra(MODE_EXTRA, mode);
//        return i;
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }
}
