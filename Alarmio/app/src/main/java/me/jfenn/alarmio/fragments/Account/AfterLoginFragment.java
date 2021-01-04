package me.jfenn.alarmio.fragments.Account;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import me.jfenn.alarmio.R;

public class AfterLoginFragment extends Fragment implements View.OnClickListener{

    private TextView text_say_hello;
    private Button btn_logout;
    private SharedPreferences sharedPreferences; //用于获取USER_INFO文件中的内容
    private SharedPreferences.Editor editor;
    private FragmentManager fragmentManager; //用于fragment跳转

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_after_login, container, false);
        initView(view);
        initListener();
        return view;
    }

    @SuppressLint("CommitPrefEdits")
    private void initView(View view) {
        text_say_hello = view.findViewById(R.id.text_view_hello);
        btn_logout = view.findViewById(R.id.btn_logout);
        sharedPreferences = getActivity().getSharedPreferences(AccountFragment.FILE_NAME, Context.MODE_MULTI_PROCESS);
        editor = sharedPreferences.edit();
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    private void initListener(){
        btn_logout.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        text_say_hello.setText("Hello   " + sharedPreferences.getString(AccountFragment.USER_NAME, ""));
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_logout:
                editor.putString(AccountFragment.USER_NAME, AccountFragment.NOT_LOGIN_IN);
                editor.putString(AccountFragment.ENCRYPT_PASSWORD, AccountFragment.NOT_LOGIN_IN);
                editor.commit();
                Toast.makeText(getActivity(), "已登出", Toast.LENGTH_LONG).show();
                fragmentManager
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_register_welcome, new AccountFragment())
                        .commit();
                break;
        }
    }
}
