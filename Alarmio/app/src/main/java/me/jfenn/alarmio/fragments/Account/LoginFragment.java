package me.jfenn.alarmio.fragments.Account;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import me.jfenn.alarmio.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginFragment extends DialogFragment implements View.OnClickListener, KeyboardWatcher.SoftKeyboardStateListener, View.OnFocusChangeListener {

    private DrawableTextView mTopImageView;
    private EditText mMobileEditText;
    private EditText mPasswordEditText;
    private ImageView mCleanPhoneImageView;
    private ImageView mCleanPasswordImageView;
    private ImageView mShowPasswordImageView;
    private ImageView background;
    private String userName;
    private String passWord;
    private String repeatPassword;
    private EditText verifyPassword;
    private Button button;
    private View mSlideContent;
    private int mRealScreenHeight = 0;
    private float scaleRatio = 0.8f;
    private KeyboardWatcher keyboardWatcher;
    private int mSlideViewY = 0;

    private FragmentManager fragmentManager;
    private TextView register;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initView(view);
        initListener();
        return view;
    }

    @SuppressLint("CommitPrefEdits")
    private void initView(View view) {
        mTopImageView = view.findViewById(R.id.image_logo1);
        mMobileEditText = view.findViewById(R.id.et_mobile1);
        mPasswordEditText = view.findViewById(R.id.et_password1);
        mCleanPhoneImageView = view.findViewById(R.id.iv_clean_phone1);
        mCleanPasswordImageView = view.findViewById(R.id.clean_password1);
        mShowPasswordImageView = view.findViewById(R.id.iv_show_pwd1);
        mSlideContent = view.findViewById(R.id.slide_content1);
        button = view.findViewById(R.id.btn_register);
        mRealScreenHeight = ScreenUtils.getRealScreenHeight(getContext());

        verifyPassword = view.findViewById(R.id.verify_password);

        fragmentManager = requireActivity().getSupportFragmentManager();
        background = view.findViewById(R.id.imageView_fragment_notifications1);
        register = view.findViewById(R.id.register);
    }

    private void initListener() {
        mCleanPhoneImageView.setOnClickListener(this);
        mCleanPasswordImageView.setOnClickListener(this);
        mShowPasswordImageView.setOnClickListener(this);
        mMobileEditText.setOnFocusChangeListener(this);
        mPasswordEditText.setOnFocusChangeListener(this);
        button.setOnClickListener(this);
        background.setOnClickListener(this);
        register.setOnClickListener(this);

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

    //注册信息显示
    private boolean flag = false;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"NonConstantResourceId", "ShowToast"})
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_clean_phone1:
                mMobileEditText.setText("");
                break;
            case R.id.clean_password1:
                mPasswordEditText.setText("");
                break;
            case R.id.iv_show_pwd1:
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
            case R.id.btn_register:
                hideKeyboard(v);
                userName = mMobileEditText.getText().toString();
                passWord = mPasswordEditText.getText().toString();
                repeatPassword = verifyPassword.getText().toString();
                if(repeatPassword.equals(mPasswordEditText.getText().toString())) {
                    new Thread(() -> {
                        try {
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url("http://47.111.80.33:8092/user/register?username=" + userName + "&password=" + passWord)
                                    .build();
                            Response response = client.newCall(request).execute();
                            String responseData = response.body().string();
                            System.out.println(responseData);
                            JSONTokener jsonTokener = new JSONTokener(responseData);
                            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                            boolean isRegisterSuccess = "success".equals(jsonObject.getString("status"));

                            if(isRegisterSuccess){
                                Looper.prepare();
                                Toast.makeText(getActivity(), "注册成功！", Toast.LENGTH_SHORT).show();
                            }else{
                                String errMessage = jsonObject.getString("data");
                                Looper.prepare();
                                Toast.makeText(getActivity(), errMessage.substring(errMessage.indexOf("\"errMessage\":\"")+14, errMessage.lastIndexOf("\"")), Toast.LENGTH_LONG).show();
                            }
                            Looper.loop();
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }else{
                    Toast.makeText(getActivity(), "两次输入的密码不一致！", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.imageView_fragment_notifications1:
                hideKeyboard(v);
                break;

            case R.id.register:
                hideKeyboard(v);
                fragmentManager
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_register_welcome, new AccountFragment())
                        .commit();
                break;
        }
    }

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

        mAnimatorSet.setDuration(AccountFragment.duration);
        mAnimatorSet.start();
    }


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

        mAnimatorSet.setDuration(AccountFragment.duration);
        mAnimatorSet.start();

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
        keyboardWatcher.setIsSoftKeyboardOpened(hasFocus);
    }


    public static void hideKeyboard(View v){
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()){
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }


}
