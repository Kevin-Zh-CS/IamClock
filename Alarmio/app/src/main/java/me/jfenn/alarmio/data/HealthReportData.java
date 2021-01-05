package me.jfenn.alarmio.data;

import android.content.Context;
import android.util.Log;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.spec.RSAPrivateKeySpec;
import java.util.List;
import java.util.concurrent.TimeUnit;


import javax.crypto.Cipher;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HealthReportData {

    private final String HEALTH_REPORT_TAG = "HEALTH_REPORT_WORKER";
    private final String TAG = "FUCK";

    public void Report(Context context) {
        Log.d(TAG, "Report: " + "this alarm want to auto report");
//        String username = PreferenceData.HEALTH_REPORT_USERNAME.getValue(context, "");
//        String password = PreferenceData.HEALTH_REPORT_PASSWORD.getValue(context, "");
//
//        if (!username.isEmpty() && !password.isEmpty()) {
//            String login_url = "https://zjuam.zju.edu.cn/cas/login?service=https%3A%2F%2Fhealthreport.zju.edu.cn%2Fa_zju%2Fapi%2Fsso%2Findex%3Fredirect%3Dhttps%253A%252F%252Fhealthreport.zju.edu.cn%252Fncov%252Fwap%252Fdefault%252Findex";
//            String base_url = "https://healthreport.zju.edu.cn/ncov/wap/default/index";
//            String save_url = "https://healthreport.zju.edu.cn/ncov/wap/default/save";
//
//            new Thread(() -> {
//                try {
//                    Log.d("FUCK", "Start Health Report");
//
//                    OkHttpClient client = new OkHttpClient.Builder()
//                            .addInterceptor(new LoggingInterceptor())
//                            .build();
//
//                    Request request = new Request.Builder()
//                            .url(login_url)
//                            .build();
//
//                    Response response = client.newCall(request).execute();
//                    Document doc = Jsoup.parse(response.body().string());
//                    Element input = doc.select("input[name=execution]").first();
//                    String execution = input.attr("value");
//
//                    String json = Jsoup.connect("https://zjuam.zju.edu.cn/cas/v2/getPubKey").ignoreContentType(true).execute().body();
//                    JSONObject obj = new JSONObject(json);
//                    String modulus = obj.getString("modulus");
//                    String exponent = obj.getString("exponent");
//
//                    byte[] password_bytes = password.getBytes();
//                    BigInteger password_int = new BigInteger(password_bytes);
//
//                    BigInteger exponent_int = new BigInteger(exponent, 16);
//                    BigInteger modulus_int = new BigInteger(modulus, 16);
//
//                    password_int = password_int.pow(exponent_int.intValue()).mod(modulus_int);
//                    String final_pass = password_int.toString(16);
//
//                    Headers headers = response.headers();
//                    List<String> cookies = headers.values("Set-Cookie");
//                    String session = cookies.get(0);
//                    String s = session.substring(0, session.indexOf(";"));
//
//                    FormBody login_body = new FormBody.Builder()
//                            .add("username", username)
//                            .add("password", final_pass)
//                            .add("execution", execution)
//                            .add("_eventId", "submit")
//                            .build();
//                    Log.d(TAG, String.valueOf(execution.length()));
//                    Request login_request = new Request.Builder()
//                            .addHeader("cookie", s)
//                            .url(login_url)
//                            .post(login_body)
//                            .build();
//
//                    Response response2 = client.newCall(login_request).execute();
//
//                    Log.d(TAG, response2.body().string());
//
//                } catch (Exception e) {
//                    Log.d("FUCK", "Report ERROR: " + e.toString());
//                }
//            }).start();
//        } else {
//            // TODO warning
//        }
    }
}

class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        return response;
    }
}
