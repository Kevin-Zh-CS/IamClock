package me.jfenn.alarmio.data;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.jfenn.alarmio.utils.AlarmException;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HealthReportData {

    private final String HEALTH_REPORT_TAG = "HEALTH_REPORT_WORKER";
    private final String TAG = "FUCK";

    private static String reverse(String s) {
        return new StringBuilder(s).reverse().toString();
    }


    public void Report(Context context) {
        String username = PreferenceData.HEALTH_REPORT_USERNAME.getValue(context, "");
        String password = PreferenceData.HEALTH_REPORT_PASSWORD.getValue(context, "");

        if (!username.isEmpty() && !password.isEmpty()) {
            String login_url = "https://zjuam.zju.edu.cn/cas/login?service=https%3A%2F%2Fhealthreport.zju.edu.cn%2Fa_zju%2Fapi%2Fsso%2Findex%3Fredirect%3Dhttps%253A%252F%252Fhealthreport.zju.edu.cn%252Fncov%252Fwap%252Fdefault%252Findex";
            String base_url = "https://healthreport.zju.edu.cn/ncov/wap/default/index";
            String save_url = "https://healthreport.zju.edu.cn/ncov/wap/default/save";

            new Thread(() -> {
                try {
                    Log.d(TAG, "Start Health Report");

                    HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
                    // create client
                    OkHttpClient client = new OkHttpClient.Builder()
                            .cookieJar(new CookieJar() {
                                @Override
                                public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                                    List<Cookie> oldList = cookieStore.get(httpUrl.host());
                                    ArrayList<Cookie> newList = new ArrayList<>();
                                    if (oldList != null) {
                                        newList.addAll(oldList);
                                    }
                                    if (list != null) {
                                        newList.addAll(list);
                                    }
                                    cookieStore.put(httpUrl.host(), newList);
                                }

                                @Override
                                public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                                    List<Cookie> cookies = cookieStore.get(httpUrl.host());
                                    return cookies != null ? cookies : new ArrayList<>();
                                }
                            })
                            .build();

                    Request req1 = new Request.Builder().url(login_url).build();
                    Response res1 = client.newCall(req1).execute();

                    // get execution
                    Document doc1 = Jsoup.parse(res1.body().string());
                    Element input = doc1.select("input[name=execution]").first();
                    String execution = input.attr("value");

                    Request req2 = new Request.Builder().url("https://zjuam.zju.edu.cn/cas/v2/getPubKey").build();
                    Response res2 = client.newCall(req2).execute();

                    JSONObject obj = new JSONObject(res2.body().string());
                    String modulus = obj.getString("modulus");
                    String exponent = obj.getString("exponent");


                    byte[] password_bytes = password.getBytes();

                    BigInteger password_int = new BigInteger(password_bytes);

                    BigInteger exponent_int = new BigInteger(exponent, 16);
                    BigInteger modulus_int = new BigInteger(modulus, 16);

                    password_int = password_int.pow(exponent_int.intValue()).mod(modulus_int);
                    String final_pass = password_int.toString(16);

                    // second post
                    RequestBody formBody = new FormBody.Builder()
                            .add("username", username)
                            .add("password", final_pass)
                            .add("authcode", "")
                            .add("execution", execution)
                            .add("_eventId", "submit")
                            .build();

                    Request req3 = new Request.Builder()
                            .url(login_url)
                            .post(formBody)
                            .build();

                    Response res3 = client.newCall(req3).execute();
                    String res3_html = res3.body().string();

                    if (res3_html.contains("统一身份认证平台"))
                        throw new AlarmException("Fail to login!");

                    Request req4 = new Request.Builder()
                            .url(base_url)
                            .build();

                    Response res4 = client.newCall(req4).execute();
                    String res4_html = res4.body().string();

                    Pattern pattern_old = Pattern.compile("oldInfo: (\\{[^\\n]+\\})");
                    Matcher matcher_old = pattern_old.matcher(res4_html);

                    if (!matcher_old.find())
                        throw new AlarmException("Fail to get old data!");

                    Pattern pattern_def = Pattern.compile("def = (\\{[^\\n]+\\})");
                    Matcher matcher_def = pattern_def.matcher(res4_html);

                    if (!matcher_def.find())
                        throw new AlarmException("Fail to get defined data!");


                    JSONObject old_info = new JSONObject(matcher_old.group(1));
                    JSONObject def_info = new JSONObject(matcher_def.group(1));

                    String new_id = def_info.getString("id");
                    Pattern pattern_name = Pattern.compile("realname: \"([^\"]+)\",");
                    Matcher matcher_name = pattern_name.matcher(res4_html);
                    Pattern pattern_number = Pattern.compile("number: '([^']+)',");
                    Matcher matcher_number = pattern_number.matcher(res4_html);
                    if (!matcher_name.find() || !matcher_number.find())
                        throw new AlarmException("Cannot decode json data");

                    String name = matcher_name.group(1);
                    String number = matcher_number.group(1);

                    JSONObject new_info = new JSONObject(old_info.toString());
                    new_info.put("id", new_id);
                    new_info.put("name", name);
                    new_info.put("number", number);
                    Date today = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                    String date = format.format(today);
                    new_info.put("date", date);
                    new_info.put("created", System.currentTimeMillis() / 1000);
                    new_info.put("jrdqtlqk[]", 0);
                    new_info.put("jrdqjcqk[]", 0);
                    new_info.put("sfsqhzjkk", 1);
                    new_info.put("sqhzjkkys", 1);
                    new_info.put("sfqrxxss", 1);
                    new_info.put("jcqzrq", "");
                    new_info.put("gwszdd", "");
                    new_info.put("szgjcs", "");
//                    new_info.put("address", "浙江省杭州市西湖区灵隐街道浙江大学玉泉校区");
//                    new_info.put("area", "浙江省 杭州市 西湖区");
//                    new_info.put("province", ((String) new_info.get("area")).split(" ")[0]);
//                    new_info.put("city", ((String) new_info.get("area")).split(" ")[1]);

                    // ready to post
                    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    FormBody.Builder healthFormBuilder = new FormBody.Builder();
                    Iterator<String> keys = new_info.keys();

                    while (keys.hasNext()) {
                        String key = keys.next();
                        if (new_info.get(key) instanceof String || new_info.get(key) instanceof Integer || new_info.get(key) instanceof Long) {
                            healthFormBuilder.add(key, new_info.get(key).toString());
                        } else if (new_info.get(key) instanceof JSONArray) {
                            healthFormBuilder.add(key + "[]", "0");
                        }
                    }
                    Request req5 = new Request.Builder()
                            .url(save_url)
                            .post(healthFormBuilder.build())
                            .build();

                    Response res5 = client.newCall(req5).execute();
                    Log.d(TAG, res5.body().string());
                } catch (AlarmException ae) {
                    Log.d(TAG, "Fatal: " + ae);
                } catch (Exception e) {
                    Log.d(TAG, "Report ERROR: " + e.toString());
                }
            }).start();
        } else {
            // TODO warning
        }
    }
}