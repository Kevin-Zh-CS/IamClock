package com.ziyue.iamclockapp.Clock;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ClockViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ClockViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is clock fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}