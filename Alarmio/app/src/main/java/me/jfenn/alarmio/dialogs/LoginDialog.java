package me.jfenn.alarmio.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLOutput;
import java.util.Base64;
import java.util.Objects;

import me.jfenn.alarmio.R;
import me.jfenn.alarmio.fragments.Account.AfterLoginFragment;
import me.jfenn.alarmio.fragments.Account.DrawableTextView;
import me.jfenn.alarmio.fragments.Account.KeyboardWatcher;
import me.jfenn.alarmio.fragments.Account.LoginFragment;
import me.jfenn.alarmio.fragments.Account.ScreenUtils;
import me.jfenn.alarmio.fragments.SettingsFragment;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginDialog extends DialogFragment implements View.OnClickListener, KeyboardWatcher.SoftKeyboardStateListener, View.OnFocusChangeListener{


    public static final int duration = 800;
    public static final String NOT_LOGIN_IN = "NULL";
    public static final String USER_NAME = "USER_NAME";
    public static final String ENCRYPT_PASSWORD = "ENCRYPT_PASSWORD";
    public static final String FILE_NAME = "USER_INFO";


    private EditText mMobileEditText;
    private EditText mPasswordEditText;
    private ImageView mCleanPhoneImageView;
    private ImageView mCleanPasswordImageView;
    private ImageView mShowPasswordImageView;
    private ImageView background;
    private String userName;
    private String encryptPassword;
    private Button button;

    //view for slide animation
    private View mSlideContent;
    private int mRealScreenHeight = 0;
    private KeyboardWatcher keyboardWatcher;
    private int mSlideViewY = 0;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //private FragmentManager fragmentManager;
    private TextView register;
    private FragmentManager alarmioFragmentManager;

    public LoginDialog(FragmentManager fragmentManager) {
        this.alarmioFragmentManager = fragmentManager;
    }


    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        initView(view);
//        if(!sharedPreferences.getString(USER_NAME, "").equals(NOT_LOGIN_IN)){
//            fragmentManager
//                    .beginTransaction()
//                    .addToBackStack(null)
//                    .replace(R.id.fragment_register_welcome, new AfterLoginFragment())
//                    .commit();
//        }
        //System.out.println("-------------------------------"+sharedPreferences.getString(USER_NAME, ""));
        initListener();
        return view;
    }

    @SuppressLint("CommitPrefEdits")
    private void initView(View view) {
        //mTopImageView = view.findViewById(R.id.image_logo);
        mMobileEditText = view.findViewById(R.id.et_mobile);
        mPasswordEditText = view.findViewById(R.id.et_password);
        mCleanPhoneImageView = view.findViewById(R.id.iv_clean_phone);
        mCleanPasswordImageView = view.findViewById(R.id.clean_password);
        mShowPasswordImageView = view.findViewById(R.id.iv_show_pwd);
        mSlideContent = view.findViewById(R.id.slide_content);
        button = view.findViewById(R.id.btn_login);
        mRealScreenHeight = ScreenUtils.getRealScreenHeight(getContext());
        //view.findViewById(R.id.fragment_register_welcome).setBackgroundResource(R.drawable.bg_rain);

        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(FILE_NAME, Context.MODE_MULTI_PROCESS);
        editor = sharedPreferences.edit();
        //fragmentManager = getActivity().getSupportFragmentManager();
        //background = view.findViewById(R.id.imageView_fragment_notifications);
        register = view.findViewById(R.id.register);
    }


    private void initListener() {
        mCleanPhoneImageView.setOnClickListener(this);
        mCleanPasswordImageView.setOnClickListener(this);
        mShowPasswordImageView.setOnClickListener(this);
        mMobileEditText.setOnFocusChangeListener(this);
        mPasswordEditText.setOnFocusChangeListener(this);
        button.setOnClickListener(this);
        //background.setOnClickListener(this);
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


    //登录信息显示
    private boolean flag = false;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"NonConstantResourceId", "ShowToast"})
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
                    encryptPassword = encodeByMd5(mPasswordEditText.getText().toString())
                            .replaceAll("\\+","%2B")
                            .replaceAll("#","%23");
                    System.out.println(userName);
                    System.out.println(encryptPassword);
                    System.out.println("http://47.111.80.33:8092/user/login?username="+userName+"&encryptpassword="+encryptPassword);
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
                        String responseData = Objects.requireNonNull(response.body()).string();
                        System.out.println(responseData);
                        JSONTokener jsonTokener = new JSONTokener(responseData);
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        boolean isLoginSuccess = "success".equals(jsonObject.getString("status"));
                        if(isLoginSuccess) {
                            editor.putString(USER_NAME, userName);
                            editor.putString(ENCRYPT_PASSWORD, encryptPassword);
                            editor.commit();
                            hideKeyboard(v);

//                            fragmentManager
//                                    .beginTransaction()
//                                    .addToBackStack(null)
//                                    .replace(R.id.fragment_register_welcome, new AfterLoginFragment())
//                                    .commit();

                            Objects.requireNonNull(getDialog()).dismiss();
                            Looper.prepare();
                            Toast.makeText(getActivity(), "登录成功！", Toast.LENGTH_SHORT).show();

                        }else{
                            editor.putString(USER_NAME, NOT_LOGIN_IN);
                            editor.putString(ENCRYPT_PASSWORD, NOT_LOGIN_IN);
                            String errMessage = jsonObject.getString("data");
                            Looper.prepare();
                            Toast.makeText(getActivity(), errMessage.substring(errMessage.indexOf("\"errMessage\":\"")+14, errMessage.lastIndexOf("\"")), Toast.LENGTH_LONG).show();
                        }
                        Looper.loop();
                        editor.commit();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }).start();
                break;

//            case R.id.imageView_fragment_notifications:
//                hideKeyboard(v);
//                break;

            case R.id.register:
                hideKeyboard(v);
                //页面跳转到注册
                RegisterDialog registerDialog = new RegisterDialog(alarmioFragmentManager);
                registerDialog.show(alarmioFragmentManager, "registerTag");
                Objects.requireNonNull(getDialog()).dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        keyboardWatcher = new KeyboardWatcher(Objects.requireNonNull(getActivity()).findViewById(Window.ID_ANDROID_CONTENT));
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

//        if (keyboardSize > bottom) {
//            int slideDist = keyboardSize - bottom;
//            setViewAnimatorWhenKeyboardOpened(mTopImageView, mSlideContent, slideDist);
//        }
    }

    @Override
    public void onSoftKeyboardClosed() {
        //setViewAnimatorWhenKeyboardClosed(mTopImageView, mSlideContent);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        keyboardWatcher.setIsSoftKeyboardOpened(hasFocus);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String encodeByMd5(String str) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(md5.digest(str.getBytes(StandardCharsets.UTF_8)));
    }

    public static void hideKeyboard(View v){
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()){
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

}
