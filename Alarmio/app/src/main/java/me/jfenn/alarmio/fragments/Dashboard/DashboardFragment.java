package me.jfenn.alarmio.fragments.Dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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
import me.jfenn.alarmio.data.TimerData;
import me.jfenn.alarmio.fragments.BasePagerFragment;
import me.jfenn.alarmio.interfaces.ContextFragmentInstantiator;
import me.jfenn.alarmio.pojo.UserView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DashboardFragment extends BasePagerFragment implements Consumer, View.OnClickListener {

    private BarChart chart1;
    private LineChart chart2;
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
        chart2 = v.findViewById(R.id.linechart1);
        chart2.setOnClickListener(this);

        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("USER_INFO", Context.MODE_MULTI_PROCESS);
        username = sharedPreferences.getString("USER_NAME", null);
        if (username != null) {//已登录
            drawFriendChart();
            drawSelfChart();

        }else{
            chart1.setVisibility(View.GONE);
            drawSelfChart();
//            drawSelfChartOnTop();
//            chart2.setVisibility(View.GONE);
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
            if(v.getId() == R.id.linechart1){

                chart1.setVisibility(View.VISIBLE);
                drawFriendChart();
                drawSelfChart();
            }

        }else{//点击的时候未登录

            if(v.getId() == R.id.barchart1){
                //drawSelfChartOnTop();
                drawSelfChart();
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
                        if(!userView.getUserName().equals(username)) {
                            x1Data.add(userView.getUserName());
                            y1Data.add(dateFormat.format(userView.getGetupDate()));
                        }
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
                    int hour = Integer.parseInt(s.substring(0, 2));
                    int min = Integer.parseInt(s.substring(3, 5));
                    entries.add(new BarEntry(i, (float) (hour * 60 + min)));
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
        LineDataSet lineDataSet = new LineDataSet(getValues(), "Get up");
        lineDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        LineData lineData = new LineData();
        lineData.addDataSet(lineDataSet);

        //设置折线为虚线
        lineDataSet.enableDashedLine(10f, 5f, 0f);
        lineDataSet.enableDashedHighlightLine(10f, 5f, 0f);

        //设置折线交点的圆点颜色
        lineDataSet.setCircleColor(Color.parseColor("#3C89FF"));
        //设置折线的宽度
        lineDataSet.setLineWidth(3f);
        //设置折线交点的圆点半径
        lineDataSet.setCircleRadius(3f);
        //设置折线交点的圆点是否空心
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setValueTextSize(9f);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setValueFormatter(new MyYFormatter());
        lineDataSet.setValueTextSize(13);
        chart2.getAxisRight().setDrawLabels(false);
        chart2.getAxisLeft().setDrawLabels(false);
        chart2.setScaleEnabled(true);
        chart2.setData(lineData);
        description = chart2.getDescription();
        description.setEnabled(false);
        chart1.invalidate();
    }


//    private void drawSelfChartOnTop(){
//        BarDataSet barDataSet2 = new BarDataSet(getValues(), "DataSet 2");
//        barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);
//        BarData barData2 = new BarData();
//        barData2.addDataSet(barDataSet2);
//        chart1.animateY(2000);
//        chart1.setData(barData2);
//    }


    private ArrayList<Entry> getValues() {
        ArrayList<Entry> values = new ArrayList<>();
        //System.out.println(sharedPreferences2.getString(TimerData.SUNDAY, "没有"));
        String mondey = sharedPreferences.getString(TimerData.MONDAY, "00:00");
        values.add(new Entry(0, (float) (Integer.parseInt(mondey.substring(0, 2))) * 60 + Integer.parseInt(mondey.substring(3, 5))));
        String tuesday = sharedPreferences.getString(TimerData.TUESDAY, "00:00");
        values.add(new Entry(1, (float) (Integer.parseInt(tuesday.substring(0, 2))) * 60 + Integer.parseInt(tuesday.substring(3, 5))));
        String wednesday = sharedPreferences.getString(TimerData.WEDNESDAY, "00:00");
        values.add(new Entry(2, (float) (Integer.parseInt(wednesday.substring(0, 2))) * 60 + Integer.parseInt(wednesday.substring(3, 5))));
        String thursday = sharedPreferences.getString(TimerData.THURSDAY, "00:00");
        values.add(new Entry(3, (float) (Integer.parseInt(thursday.substring(0, 2))) * 60 + Integer.parseInt(thursday.substring(3, 5))));
        String friday = sharedPreferences.getString(TimerData.FRIDAY, "00:00");
        values.add(new Entry(4, (float) (Integer.parseInt(friday.substring(0, 2))) * 60 + Integer.parseInt(friday.substring(3, 5))));
        String saturday = sharedPreferences.getString(TimerData.SATURDAY, "00:00");
        values.add(new Entry(5, (float) (Integer.parseInt(saturday.substring(0, 2))) * 60 + Integer.parseInt(saturday.substring(3, 5))));
        String sunday = sharedPreferences.getString(TimerData.SUNDAY, "00:00");
        values.add(new Entry(6, (float) (Integer.parseInt(sunday.substring(0, 2))) * 60 + Integer.parseInt(sunday.substring(3, 5))));

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