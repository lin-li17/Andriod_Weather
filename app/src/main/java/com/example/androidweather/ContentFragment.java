package com.example.androidweather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ContentFragment extends Fragment {
    View view;

    private String city;


    private SwipeRefreshLayout swipeRefreshLayout;//刷新监听


    private TextView textView;
    private TextView precipitation;
    private TextView textView4;//Life紫外线
    private TextView nowWeatherAndNumber;
    private TextView wind;
    private TextView humidity;
    private TextView temperature;
    private TextView umbrella;
    private TextView motion;
    private TextView fishing;
    private TextView carWash;
    private TextView allergic;
    private TextView flu;
    //三日天气共12个textview组件
    private TextView todayDateTV;
    private TextView todayWeatherTV;
    private TextView todayLowTV;
    private TextView todayHighTV;

    private TextView todayDateTV2;
    private TextView todayWeatherTV2;
    private TextView todayLowTV2;
    private TextView todayHighTV2;

    private TextView todayDateTV3;
    private TextView todayWeatherTV3;
    private TextView todayLowTV3;
    private TextView todayHighTV3;

    private String weatherNow1 = "https://api.seniverse.com/v3/weather/now.json?key=SX0YI9aO7VGVSFm0c&location=";
    private String weatherNow2 = "&language=zh-Hans&unit=c";
    private String weatherDaily1 = "https://api.seniverse.com/v3/weather/daily.json?key=SX0YI9aO7VGVSFm0c&location=";
    private String weatherDaily2 = "&language=zh-Hans&unit=c&start=0&days=5";
    private String weatherLife1 = "https://api.seniverse.com/v3/life/suggestion.json?key=SX0YI9aO7VGVSFm0c&location=";
    private String weatherLife2 = "&language=zh-Hans&days=5";


    private File f;



    private GradientDrawable gradientDrawable;//圆角设置
    //需要设置圆角的组件
    private GridLayout gridLayout;
    private LinearLayout linearLayout;


    public ContentFragment(String content) {
        this.city = content;
    }



    //记得在删除时删除对应文件
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_fragment, container, false);
        //设置id
        initFindID();
        //设置圆角
        setGradientDrawable();
        //下拉刷新的设置
        swipeRefreshLayout.setColorSchemeResources(R.color.sprBlue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshWeathers();
            }
        });

        if(f.exists()) {
            //存在读取文件
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(readFromFile(city + "WeatherNowData"));
                JSONArray jsonArray = new JSONArray(jsonObject.getString("results"));
                city = jsonArray.getJSONObject(0).getJSONObject("location").getString("name");
            } catch (JSONException e) {
                Log.d("TAG", "onReceiveLocation: 读取文件城市错误");
                e.printStackTrace();
            }
            //存在
            //读取文件
            //修改UI
            jsonReading(readFromFile(city + "WeatherNowData"), textView);
            jsonReading(readFromFile(city + "WeatherDailyData"), todayDateTV);
            jsonReading(readFromFile(city + "WeatherLifeData"), textView4);
        }
        //在发送网络请求
        sendRequestWithOkHttp();

        return view;
    }
    //写数据准备
    private void jsonReading(String jsonData,View tv) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = new JSONArray(jsonObject.getString("results"));
            if (tv == textView){
                weatherNowUi(jsonArray.getJSONObject(0));
            } else if (tv == todayDateTV){
                weatherDailyUi(jsonArray.getJSONObject(0));
            } else if (tv == textView4) {
                weatherLift(jsonArray.getJSONObject(0));
            }
        } catch (Exception e) {
            Log.d("TAG", "j:" + e.getMessage());
            e.printStackTrace();
        }
    }
    //开始写数据
    private void weatherLift(JSONObject jsonObject1){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray jsonArray = new JSONArray(jsonObject1.getString("suggestion"));

                    StringBuilder temperatureSB = new StringBuilder();
                    temperatureSB.append("气温" + "\n").append("\n").append(jsonArray.getJSONObject(0).getJSONObject("dressing").getString("brief"));
                    temperature.setText(temperatureSB);

                    StringBuilder UltravioletSB = new StringBuilder();
                    UltravioletSB.append("紫外线" + "\n").append("\n").append(jsonArray.getJSONObject(0).getJSONObject("sunscreen").getString("brief"));
                    textView4.setText(UltravioletSB);

                    StringBuilder umbrellaSB = new StringBuilder();
                    umbrellaSB.append("带伞" + "\n").append("\n").append(jsonArray.getJSONObject(0).getJSONObject("umbrella").getString("brief"));
                    umbrella.setText(umbrellaSB);

                    StringBuilder motionSB = new StringBuilder();
                    motionSB.append("运动" + "\n" + "\n").append(jsonArray.getJSONObject(0).getJSONObject("sport").getString("brief"));
                    motion.setText(motionSB);

                    StringBuilder fishingSB = new StringBuilder();
                    fishingSB.append("钓鱼" + "\n" + "\n").append(jsonArray.getJSONObject(0).getJSONObject("fishing").getString("brief"));
                    fishing.setText(fishingSB);

                    StringBuilder carWashSB = new StringBuilder();
                    carWashSB.append("洗车" + "\n" + "\n").append(jsonArray.getJSONObject(0).getJSONObject("car_washing").getString("brief"));
                    carWash.setText(carWashSB);

                    StringBuilder allergicSB = new StringBuilder();
                    allergicSB.append("过敏" + "\n" + "\n").append(jsonArray.getJSONObject(0).getJSONObject("allergy").getString("brief"));
                    allergic.setText(allergicSB);

                    StringBuilder fluSB = new StringBuilder();
                    fluSB.append("感冒" + "\n" + "\n").append(jsonArray.getJSONObject(0).getJSONObject("flu").getString("brief"));
                    flu.setText(fluSB);

                } catch (JSONException e) {
                    Log.d("TAG", "Left:" + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    private void weatherDailyUi(JSONObject jsonObject1){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray jsonArray = new JSONArray(jsonObject1.getString("daily"));
                    StringBuilder stringBuilder2 = new StringBuilder();

                    //只需要今天数据的在这里更新
                    //代表今天的数据
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    todayDateTV.setText(jsonObject.getString("date").substring(5));
                    todayWeatherTV.setText(jsonObject.getString("text_day"));
                    todayLowTV.setText(jsonObject.getString("low"));
                    todayHighTV.setText(jsonObject.getString("high"));


                    stringBuilder2.append("降水量").append("\n").append(jsonObject.getString("rainfall")).append("mm");

                    StringBuilder windSB = new StringBuilder();
                    windSB.append(jsonObject.getString("wind_direction"));
                    if (!jsonObject.getString("wind_direction").equals("无持续风向")){
                        windSB.append("风");
                    }
                    windSB.append("\n").append(jsonObject.getString("wind_scale")).append("级");

                    StringBuilder humiditySB = new StringBuilder();
                    humiditySB.append("湿度").append("\n").append(jsonObject.getString("humidity")).append("%");

                    StringBuilder nowWeatherAndNumberSB = new StringBuilder();
                    nowWeatherAndNumberSB.append(jsonObject.getString("text_day")).append("    ").append(jsonObject.getString("low")).append("~").append(jsonObject.getString("high"));


                    precipitation.setText(stringBuilder2);
                    nowWeatherAndNumber.setText(nowWeatherAndNumberSB);
                    wind.setText(windSB);
                    humidity.setText(humiditySB);

                    //明天的数据
                    JSONObject tomorrow = jsonArray.getJSONObject(1);
                    todayDateTV2.setText(tomorrow.getString("date").substring(5));
                    todayWeatherTV2.setText(tomorrow.getString("text_day"));
                    todayLowTV2.setText(tomorrow.getString("low"));
                    todayHighTV2.setText(tomorrow.getString("high"));

                    //后天的数据
                    JSONObject dayAfterTomorrow = jsonArray.getJSONObject(2);
                    todayDateTV3.setText(dayAfterTomorrow.getString("date").substring(5));
                    todayWeatherTV3.setText(dayAfterTomorrow.getString("text_day"));
                    todayLowTV3.setText(dayAfterTomorrow.getString("low"));
                    todayHighTV3.setText(dayAfterTomorrow.getString("high"));
                } catch (JSONException e) {
                    Log.d("TAG", "2" + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    private void weatherNowUi(JSONObject jsonObject){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    StringBuilder stringBuilder = new StringBuilder();
//                    stringBuilder.append("天气:").append(jsonObject.getJSONObject("now").getString("text")).append("\n");
                    stringBuilder.append(jsonObject.getJSONObject("now").getString("temperature"));
                    textView.setText(stringBuilder);
                } catch (JSONException e) {
                    Log.d("TAG", "2" + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    //读文件
    private String readFromFile(String name) {
        // 获取文件路径
        File file = new File(requireContext().getFilesDir(), name);
        StringBuilder stringBuilder = new StringBuilder();

        try (FileInputStream fis = new FileInputStream(file);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }
    //写文件
    private void saveToFile(String data,String name) {
        // 获取文件路径
        File file = new File(requireContext().getFilesDir(), name);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setGradientDrawable() {
        //圆角设置
        gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(0Xe0ffffff);
        gradientDrawable.setCornerRadius(40);
        //需要设置圆角的组件
        gridLayout = view.findViewById(R.id.threeDay);
        gridLayout.setBackground(gradientDrawable);

        GradientDrawable gradientDrawable1 = new GradientDrawable();
        gradientDrawable1.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable1.setColor(0Xe0ffffff);
        gradientDrawable1.setCornerRadius(24);

        linearLayout = view.findViewById(R.id.linearLayout);
        linearLayout.setBackground(gradientDrawable1);

        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable2.setColor(0Xf0f1f1f1);
        gradientDrawable2.setCornerRadius(50);

        GradientDrawable gradientDrawable3 = new GradientDrawable();
        gradientDrawable3.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable3.setColor(0Xf0f1f1f1);
        gradientDrawable3.setCornerRadius(50);

        temperature.setBackground(gradientDrawable3);
        umbrella.setBackground(gradientDrawable3);
        motion.setBackground(gradientDrawable2);
        fishing.setBackground(gradientDrawable3);
        carWash.setBackground(gradientDrawable2);
        allergic.setBackground(gradientDrawable3);
        flu.setBackground(gradientDrawable2);
        textView4.setBackground(gradientDrawable2);
    }

    private void refreshWeathers(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    // 回到主线程
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            // 更新 UI
                            sendRequestWithOkHttp();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                } catch (InterruptedException e) {
                    Log.d("TAG", "run: 刷新失败");
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void initFindID() {
        textView = view.findViewById(R.id.tv);
        precipitation = view.findViewById(R.id.precipitation);
        textView4 = view.findViewById(R.id.textView4);
        nowWeatherAndNumber = view.findViewById(R.id.nowWeatherAndNumber);
        wind = view.findViewById(R.id.wind);
        humidity = view.findViewById(R.id.humidity);
        temperature = view.findViewById(R.id.temperature);
        umbrella = view.findViewById(R.id.umbrella);
        motion = view.findViewById(R.id.motion);
        fishing = view.findViewById(R.id.fishing);
        carWash = view.findViewById(R.id.carWash);
        allergic = view.findViewById(R.id.allergic);
        flu = view.findViewById(R.id.flu);
        //三日天气12组件
        todayDateTV = view.findViewById(R.id.todayDateTV);
        todayWeatherTV = view.findViewById(R.id.todayWeatherTV);
        todayLowTV = view.findViewById(R.id.todayLowTV);
        todayHighTV = view.findViewById(R.id.todayHighTV);

        todayDateTV2 = view.findViewById(R.id.todayDateTV2);
        todayWeatherTV2 = view.findViewById(R.id.todayWeatherTV2);
        todayLowTV2 = view.findViewById(R.id.todayLowTV2);
        todayHighTV2 = view.findViewById(R.id.todayHighTV2);

        todayDateTV3 = view.findViewById(R.id.todayDateTV3);
        todayWeatherTV3 = view.findViewById(R.id.todayWeatherTV3);
        todayLowTV3 = view.findViewById(R.id.todayLowTV3);
        todayHighTV3 = view.findViewById(R.id.todayHighTV3);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);

        f = new File("/data/data/com.example.androidweather/files/" + city + "WeatherNowData");

    }

    //删文件
    public void deleteData(){
        File weatherNowData = new File("/data/data/com.example.androidweather/files/" + city + "WeatherNowData");
        File weatherDailyData = new File("/data/data/com.example.androidweather/files/" + city + "WeatherDailyData");
        File weatherLifeData = new File("/data/data/com.example.androidweather/files/" + city + "WeatherLifeData");
        weatherNowData.delete();
        weatherDailyData.delete();
        weatherLifeData.delete();
    }
    //联网
    private void sendRequestWithOkHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //网络请求
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(weatherNow1 + city + weatherNow2)
                            .build();
                    Response response1 = client.newCall(request).execute();
                    String responseData = response1.body().string();

                    Request request2 = new Request.Builder()
                            .url(weatherDaily1 + city + weatherDaily2)
                            .build();
                    Response response2 = client.newCall(request2).execute();
                    String responseData2 = response2.body().string();

                    Request request3 = new Request.Builder()
                            .url(weatherLife1 + city + weatherLife2)
                            .build();
                    Response response3 = client.newCall(request3).execute();
                    String responseData3 = response3.body().string();

                    //写入文件
                    saveToFile(responseData,city + "WeatherNowData");
                    saveToFile(responseData2,city + "WeatherDailyData");
                    saveToFile(responseData3,city +"WeatherLifeData");
                    //读取文件并修改UI
                    jsonReading(readFromFile(city + "WeatherNowData"), textView);
                    jsonReading(readFromFile(city + "WeatherDailyData"), todayDateTV);
                    jsonReading(readFromFile(city + "WeatherLifeData"), textView4);


                } catch (Exception e) {
                    //如果没网
                    Log.d("TAG", "run: "+e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //以下是向main传递参数的代码
    public interface OnDataPass {
        void onDataPass(String data);
    }
    private OnDataPass dataPassListener;
    //被附加到main时调用
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // 确保 Activity 实现了回调接口
        if (context instanceof OnDataPass) {
            dataPassListener = (OnDataPass) context;
        } else {
            Log.d("TAG", "onAttach: 传递参数时错误");
        }
    }
    //被分离时调用
    @Override
    public void onDetach() {
        super.onDetach();
        //将dataPassListener 设置为 null，以避免内存泄漏
        dataPassListener = null;
    }
    //变得可见时调用
    @Override
    public void onResume() {
        super.onResume();
        // 传递数据
        if (dataPassListener != null) {
            dataPassListener.onDataPass(city);
        }
    }
}
