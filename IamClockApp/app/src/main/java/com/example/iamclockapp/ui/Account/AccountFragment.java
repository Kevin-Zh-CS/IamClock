package com.example.iamclockapp.ui.Account;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.iamclockapp.R;
import com.example.iamclockapp.pojo.UserView;
import com.example.iamclockapp.ui.Statistics.MyXFormatter;
import com.example.iamclockapp.ui.Statistics.MyYFormatter;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AccountFragment extends Fragment implements View.OnClickListener, KeyboardWatcher.SoftKeyboardStateListener, View.OnFocusChangeListener {


    private static final int duration = 800;

    private DrawableTextView mTopImageView;

    private EditText mMobileEditText;
    private EditText mPasswordEditText;
    private ImageView mCleanPhoneImageView;
    private ImageView mCleanPasswordImageView;
    private ImageView mShowPasswordImageView;
    private String userName;
    private String encryptPassword;
    private Button button;

    //view for slide animation
    private View mSlideContent;

    private int mRealScreenHeight = 0;

    //logo scaleRatio ratio
    private float scaleRatio = 0.8f;

    private KeyboardWatcher keyboardWatcher;

    //the position of mSlideContent on screen Y
    private int mSlideViewY = 0;

    public AccountFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        initView(view);
        initListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        keyboardWatcher = new KeyboardWatcher(getActivity().findViewById(Window.ID_ANDROID_CONTENT));
        keyboardWatcher.addSoftKeyboardStateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        keyboardWatcher.removeSoftKeyboardStateListener(this);
    }

    private void initView(View view) {
        mTopImageView = view.findViewById(R.id.image_logo);
        mMobileEditText = view.findViewById(R.id.et_mobile);
        mPasswordEditText = view.findViewById(R.id.et_password);
        mCleanPhoneImageView = view.findViewById(R.id.iv_clean_phone);
        mCleanPasswordImageView = view.findViewById(R.id.clean_password);
        mShowPasswordImageView = view.findViewById(R.id.iv_show_pwd);
        mSlideContent = view.findViewById(R.id.slide_content);
        button = view.findViewById(R.id.btn_login);
        view.findViewById(R.id.iv_close).setOnClickListener(this);
        mRealScreenHeight = ScreenUtils.getRealScreenHeight(getContext());
        view.findViewById(R.id.root).setBackgroundResource(R.drawable.four_screen_bg);
    }


    private void initListener() {
        mCleanPhoneImageView.setOnClickListener(this);
        mCleanPasswordImageView.setOnClickListener(this);
        mShowPasswordImageView.setOnClickListener(this);
        //TODO mMobileEditText and mPasswordEditText have different InputType, which will lead to the difference of KeyboardSize
        //TODO if they have same InputType, you can delete setOnFocusChangeListener
        mMobileEditText.setOnFocusChangeListener(this);
        mPasswordEditText.setOnFocusChangeListener(this);
        button.setOnClickListener(this);
        mMobileEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && mCleanPhoneImageView.getVisibility() == View.GONE) {
                    mCleanPhoneImageView.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s)) {
                    mCleanPhoneImageView.setVisibility(View.GONE);
                }
            }
        });

        mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && mCleanPasswordImageView.getVisibility() == View.GONE) {
                    mCleanPasswordImageView.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(s)) {
                    mCleanPasswordImageView.setVisibility(View.GONE);
                }
                if (s.toString().isEmpty())
                    return;
                if (!s.toString().matches("[A-Za-z0-9]+")) {
                    String temp = s.toString();
                    Toast.makeText(getActivity(), R.string.please_input_limit_pwd, Toast.LENGTH_SHORT).show();
                    s.delete(temp.length() - 1, temp.length());
                    mPasswordEditText.setSelection(s.length());
                }
            }
        });
    }

    /**
     * play animator when softkeyboard opened
     *
     * @param logoImage
     * @param mSlideContent
     * @param logoSlideDist
     */
    public void setViewAnimatorWhenKeyboardOpened(View logoImage, View mSlideContent, float logoSlideDist) {
        logoImage.setPivotY(logoImage.getHeight());
        logoImage.setPivotX(0);

        AnimatorSet mAnimatorSet = new AnimatorSet();
        ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat(logoImage, View.SCALE_X, 1.0f, scaleRatio);
        ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat(logoImage, View.SCALE_Y, 1.0f, scaleRatio);
        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(logoImage, View.TRANSLATION_Y, 0.0f, -logoSlideDist);
        ObjectAnimator mContentAnimatorTranslateY = ObjectAnimator.ofFloat(mSlideContent, View.TRANSLATION_Y, 0.0f, -logoSlideDist);

        mAnimatorSet.play(mContentAnimatorTranslateY)
                .with(mAnimatorTranslateY)
                .with(mAnimatorScaleX)
                .with(mAnimatorScaleY);

        mAnimatorSet.setDuration(duration);
        mAnimatorSet.start();
    }

    /**
     * play animator when softkeyboard closed
     *
     * @param logoImage
     * @param mSlideContent
     */
    public void setViewAnimatorWhenKeyboardClosed(View logoImage, View mSlideContent) {
        if (logoImage.getTranslationY() == 0) {
            return;
        }
        logoImage.setPivotY(logoImage.getHeight());
        logoImage.setPivotX(0);

        AnimatorSet mAnimatorSet = new AnimatorSet();
        ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat(logoImage, View.SCALE_X, scaleRatio, 1.0f);
        ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat(logoImage, View.SCALE_Y, scaleRatio, 1.0f);
        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(logoImage, View.TRANSLATION_Y, logoImage.getTranslationY(), 0);
        ObjectAnimator mContentAnimatorTranslateY = ObjectAnimator.ofFloat(mSlideContent, View.TRANSLATION_Y, mSlideContent.getTranslationY(), 0);

        mAnimatorSet.play(mContentAnimatorTranslateY)
                .with(mAnimatorTranslateY)
                .with(mAnimatorScaleX)
                .with(mAnimatorScaleY);

        mAnimatorSet.setDuration(duration);
        mAnimatorSet.start();

    }

    //登录信息显示
    private boolean flag = false;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_clean_phone:
                mMobileEditText.setText("");
                break;
            case R.id.clean_password:
                mPasswordEditText.setText("");
                break;
            case R.id.iv_close:
                getActivity().finish();
                break;
            case R.id.iv_show_pwd:
                if (flag) {
                    mPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mShowPasswordImageView.setImageResource(R.drawable.ic_pass_gone);
                    flag = false;
                } else {
                    mPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mShowPasswordImageView.setImageResource(R.drawable.ic_pass_visuable);
                    flag = true;
                }
                String pwd = mPasswordEditText.getText().toString();
                if (!TextUtils.isEmpty(pwd))
                    mPasswordEditText.setSelection(pwd.length());
                break;

                //点击登录之后，查数据库
            case R.id.btn_login:
                //System.out.println("BTN OK ");
                userName = mMobileEditText.getText().toString();
                try {
                    encryptPassword = encodeByMd5(mPasswordEditText.getText().toString());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                new Thread(() -> {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url("http://47.111.80.33:8092/user/login?username="+userName+"&encryptpassword="+encryptPassword)
                                .build();
                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();
                        System.out.println(responseData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                break;

        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        keyboardWatcher.removeSoftKeyboardStateListener(this);
    }

    @Override
    public void onSoftKeyboardOpened(int keyboardSize) {
        int[] location = new int[2];
        //get mSlideContent's location on screen
        mSlideContent.getLocationOnScreen(location);
        if (mSlideViewY == 0) {
            mSlideViewY = location[1];
        }
        int bottom = mRealScreenHeight - (mSlideViewY + mSlideContent.getHeight());

        if (keyboardSize > bottom) {
            int slideDist = keyboardSize - bottom;
            setViewAnimatorWhenKeyboardOpened(mTopImageView, mSlideContent, slideDist);
        }
    }

    @Override
    public void onSoftKeyboardClosed() {
        setViewAnimatorWhenKeyboardClosed(mTopImageView, mSlideContent);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            if (hasFocus) {
                keyboardWatcher.setIsSoftKeyboardOpened(keyboardWatcher.isSoftKeyboardOpened());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String encodeByMd5(String str) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(md5.digest(str.getBytes(StandardCharsets.UTF_8)));
    }
}