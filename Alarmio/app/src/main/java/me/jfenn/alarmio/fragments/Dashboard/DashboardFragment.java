package me.jfenn.alarmio.fragments.Dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import io.reactivex.functions.Consumer;
import me.jfenn.alarmio.R;
import me.jfenn.alarmio.fragments.BasePagerFragment;
import me.jfenn.alarmio.interfaces.ContextFragmentInstantiator;
import me.jfenn.alarmio.pojo.UserView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DashboardFragment extends BasePagerFragment implements Consumer, View.OnClickListener {

    private BarChart chart1;
    private BarChart chart2;
    private Description description;
    private SimpleDateFormat dateFormat;
    private OkHttpClient client;
    private Request request;
    private SharedPreferences sharedPreferences;
    String username;

    @SuppressLint("SimpleDateFormat")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_dashboard, container, false);

        chart1 = v.findViewById(R.id.barchart1);
        chart1.setOnClickListener(this);
        chart2 = v.findViewById(R.id.barchart2);
        chart2.setOnClickListener(this);

        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("USER_INFO", Context.MODE_MULTI_PROCESS);
        username = sharedPreferences.getString("USER_NAME", null);
        if (username != null) {//已登录
            drawFriendChart();
            drawSelfChart();

        }else{
            drawSelfChartOnTop();
            chart2.setVisibility(View.GONE);
        }
        return v;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onClick(View v) {
        username = sharedPreferences.getString("USER_NAME", null);
        if(username != null){//点击的时候已登录
            if(v.getId() == R.id.barchart1){
                drawFriendChart();
                drawSelfChart();
                chart2.setVisibility(View.VISIBLE);
            }
            if(v.getId() == R.id.barchart2){
                drawSelfChart();
            }

        }else{//点击的时候未登录
            if(v.getId() == R.id.barchart1){
                drawSelfChartOnTop();
            }
        }

    }



    @SuppressLint("SimpleDateFormat")
    private void drawFriendChart(){
        description = chart1.getDescription();
//        System.out.println("===================================================");
//        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
//        BarChart chart1 = v.findViewById(R.id.barchart1);
//        Description description = chart1.getDescription();
        description.setEnabled(false);
        dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        new Thread(() -> {
            try {
                // 创建一个OkHttpClient的实例
                client = new OkHttpClient();
                // 如果要发送一条HTTP请求，就需要创建一个Request对象
                // 可在最终的build()方法之前连缀很多其他方法来丰富这个Request对象
                request = new Request.Builder()
                        .url("http://47.111.80.33:8092/user/list")
                        .build();
                // 调用OkHttpClient的newCall()方法来创建一个Call对象，并调用execute()方法来发送请求并获取服务器的返回数据
                Response response = client.newCall(request).execute();
                // 其中Response对象就是服务器返回的数据，将数据转换成字符串
                String responseData = response.body().string();
                //System.out.println(responseData);
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<UserView>>() {
                }.getType();
                List<UserView> userViewList = gson.fromJson(responseData.substring(responseData.indexOf("["), responseData.indexOf("]") + 1), type);


                List<String> x1Data = new ArrayList<>();
                List<String> y1Data = new ArrayList<>();
                if (userViewList != null) {
                    for (UserView userView : userViewList) {
                        x1Data.add(userView.getUserName());
                        y1Data.add(dateFormat.format(userView.getGetupDate()));
                    }
                }
                XAxis xAxis1 = chart1.getXAxis();
                xAxis1.setDrawAxisLine(false); //设置显示x轴的线
                xAxis1.setDrawGridLines(false); //设置是否显示网格
                xAxis1.setGranularity(1f);//设置最小的区间，避免标签的迅速增多
                xAxis1.setCenterAxisLabels(false);//设置标签居中
                xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);//数据位于底部
                xAxis1.setValueFormatter(new MyXFormatter(x1Data));//设置显示样式（自定义）
                xAxis1.setTextSize(14);//设置字体大小


                ArrayList<BarEntry> entries = new ArrayList<>();
                for (int i = 0; i < x1Data.size(); i++) {
                    String s = y1Data.get(i);
                    int min = Integer.parseInt(s.substring(0, 2));
                    int sec = Integer.parseInt(s.substring(3, 5));
                    entries.add(new BarEntry(i, (float) (min * 60 + sec)));
                }

                BarDataSet barDataSet1 = new BarDataSet(entries, "Your friends");
                barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);
                barDataSet1.setValueFormatter(new MyYFormatter());
                barDataSet1.setValueTextSize(13);
                BarData barData1 = new BarData();
                barData1.addDataSet(barDataSet1);
                chart1.setData(barData1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        chart1.getAxisRight().setDrawLabels(false);
        chart1.getAxisLeft().setDrawLabels(false);
        chart1.animateY(1000);
        chart1.invalidate();
    }


    private void drawSelfChart(){
        BarDataSet barDataSet2 = new BarDataSet(getValues(), "DataSet 2");
        barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData2 = new BarData();
        barData2.addDataSet(barDataSet2);
        chart2.animateY(2000);
        chart2.setData(barData2);
    }


    private void drawSelfChartOnTop(){
        BarDataSet barDataSet2 = new BarDataSet(getValues(), "DataSet 2");
        barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData2 = new BarData();
        barData2.addDataSet(barDataSet2);
        chart1.animateY(2000);
        chart1.setData(barData2);
    }




//    使用ViewPager不会调用onActivityCreated()
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        BarChart chart1 = getView().findViewById(R.id.barchart1);
//        Description description = chart1.getDescription();
//        description.setEnabled(false);
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
//        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
//        new Thread(() -> {
//            try {
//                // 创建一个OkHttpClient的实例
//                OkHttpClient client = new OkHttpClient();
//                // 如果要发送一条HTTP请求，就需要创建一个Request对象
//                // 可在最终的build()方法之前连缀很多其他方法来丰富这个Request对象
//                Request request = new Request.Builder()
//                        .url("http://47.111.80.33:8092/user/list")
//                        .build();
//                // 调用OkHttpClient的newCall()方法来创建一个Call对象，并调用execute()方法来发送请求并获取服务器的返回数据
//                Response response = client.newCall(request).execute();
//                // 其中Response对象就是服务器返回的数据，将数据转换成字符串
//                String responseData = response.body().string();
//                //System.out.println(responseData);
//                Gson gson = new Gson();
//                Type type = new TypeToken<ArrayList<UserView>>() {
//                }.getType();
//                List<UserView> userViewList = gson.fromJson(responseData.substring(responseData.indexOf("["), responseData.indexOf("]") + 1), type);
//
//
//                List<String> x1Data = new ArrayList<>();
//                List<String> y1Data = new ArrayList<>();
//                if (userViewList != null) {
//                    for (UserView userView : userViewList) {
//                        x1Data.add(userView.getUserName());
//                        y1Data.add(dateFormat.format(userView.getGetupDate()));
//                    }
//                }
//                XAxis xAxis1 = chart1.getXAxis();
//                xAxis1.setDrawAxisLine(false); //设置显示x轴的线
//                xAxis1.setDrawGridLines(false); //设置是否显示网格
//                xAxis1.setGranularity(1f);//设置最小的区间，避免标签的迅速增多
//                xAxis1.setCenterAxisLabels(false);//设置标签居中
//                xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);//数据位于底部
//                xAxis1.setValueFormatter(new MyXFormatter(x1Data));//设置显示样式（自定义）
//                xAxis1.setTextSize(14);//设置字体大小
//
//
//                ArrayList<BarEntry> entries = new ArrayList<>();
//                for (int i = 0; i < x1Data.size(); i++) {
//                    String s = y1Data.get(i);
//                    int min = Integer.parseInt(s.substring(0, 2));
//                    int sec = Integer.parseInt(s.substring(3, 5));
//                    entries.add(new BarEntry(i, (float) (min * 60 + sec)));
//                }
//
//                BarDataSet barDataSet1 = new BarDataSet(entries, "Your friends");
//                barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);
//                barDataSet1.setValueFormatter(new MyYFormatter());
//                barDataSet1.setValueTextSize(13);
//                BarData barData1 = new BarData();
//                barData1.addDataSet(barDataSet1);
//                chart1.setData(barData1);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
//        chart1.getAxisRight().setDrawLabels(false);
//        chart1.getAxisLeft().setDrawLabels(false);
//        chart1.animateY(1000);
//        chart1.invalidate();
//
//
//        BarChart chart2 = getView().findViewById(R.id.barchart2);
//        BarDataSet barDataSet2 = new BarDataSet(getValues(), "DataSet 2");
//        barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);
//        BarData barData2 = new BarData();
//        barData2.addDataSet(barDataSet2);
//        chart2.animateY(2000);
//        chart2.setData(barData2);
//
//    }

    private ArrayList<BarEntry> getValues() {
        ArrayList<BarEntry> values = new ArrayList<>();
        values.add(new BarEntry(0, 4));
        values.add(new BarEntry(1, 2));
        values.add(new BarEntry(3, 6));
        values.add(new BarEntry(4, 4));
        return values;
    }

    @Override
    public void accept(Object o) throws Exception {
    }

    @Override
    public String getTitle(Context context) {
        return null;
    }




    public static class Instantiator extends ContextFragmentInstantiator {

        public Instantiator(Context context) {
            super(context);
        }

        @Override
        public String getTitle(Context context, int position) {
            return context.getString(R.string.title_dashboard);
        }

        @Nullable
        @Override
        public BasePagerFragment newInstance(int position) {
            Fragment fragment = new DashboardFragment();
            return new DashboardFragment();
        }
    }


}