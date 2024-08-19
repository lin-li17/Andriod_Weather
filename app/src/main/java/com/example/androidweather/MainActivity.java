package com.example.androidweather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private ImageView backgroundView;//背景图片

    public LocationClient mLocationClient;//位置监听器

    private SwipeRefreshLayout swipeRefreshLayout;//刷新监听

    private String city;

    private TextView textView;
    private TextView precipitation;
    private TextView textView4;//Life紫外线
    private TextView nowWeatherAndNumber;
    private TextView wind;
    private TextView humidity;
    private TextView t3;
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


    private TextView city1;
    private String weatherNow1 = "https://api.seniverse.com/v3/weather/now.json?key=SX0YI9aO7VGVSFm0c&location=";
    private String weatherNow2 = "&language=zh-Hans&unit=c";
    private String weatherDaily1 = "https://api.seniverse.com/v3/weather/daily.json?key=SX0YI9aO7VGVSFm0c&location=";
    private String weatherDaily2 = "&language=zh-Hans&unit=c&start=0&days=5";
    private String weatherLife1 = "https://api.seniverse.com/v3/life/suggestion.json?key=SX0YI9aO7VGVSFm0c&location=";
    private String weatherLife2 = "&language=zh-Hans&days=5";

    private String backgroundImageUrl = "https://cn.bing.com/";

    private File f;
    private SharedPreferences.Editor weatherNowData;
    private SharedPreferences.Editor weatherDailyData;
    private SharedPreferences.Editor weatherLifeData;


    private GradientDrawable gradientDrawable;//圆角设置
    //需要设置圆角的组件
    private GridLayout gridLayout;
    private LinearLayout linearLayout;





    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        textView = findViewById(R.id.tv);
        precipitation = findViewById(R.id.precipitation);
        textView4 = findViewById(R.id.textView4);
        nowWeatherAndNumber = findViewById(R.id.nowWeatherAndNumber);
        t3 = findViewById(R.id.textView2);
        wind = findViewById(R.id.wind);
        humidity = findViewById(R.id.humidity);
        temperature = findViewById(R.id.temperature);
        umbrella = findViewById(R.id.umbrella);
        motion = findViewById(R.id.motion);
        fishing = findViewById(R.id.fishing);
        carWash = findViewById(R.id.carWash);
        allergic = findViewById(R.id.allergic);
        flu = findViewById(R.id.flu);
        //三日天气12组件
        todayDateTV = findViewById(R.id.todayDateTV);
        todayWeatherTV = findViewById(R.id.todayWeatherTV);
        todayLowTV = findViewById(R.id.todayLowTV);
        todayHighTV = findViewById(R.id.todayHighTV);

        todayDateTV2 = findViewById(R.id.todayDateTV2);
        todayWeatherTV2 = findViewById(R.id.todayWeatherTV2);
        todayLowTV2 = findViewById(R.id.todayLowTV2);
        todayHighTV2 = findViewById(R.id.todayHighTV2);

        todayDateTV3 = findViewById(R.id.todayDateTV3);
        todayWeatherTV3 = findViewById(R.id.todayWeatherTV3);
        todayLowTV3 = findViewById(R.id.todayLowTV3);
        todayHighTV3 = findViewById(R.id.todayHighTV3);

        //下拉刷新
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.sprBlue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshWeathers();
            }
        });

        backgroundView = findViewById(R.id.backgroundView);

        //圆角设置
        gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(0Xe0ffffff);
        gradientDrawable.setCornerRadius(40);
        //需要设置圆角的组件
        gridLayout = findViewById(R.id.threeDay);
        gridLayout.setBackground(gradientDrawable);

        GradientDrawable gradientDrawable1 = new GradientDrawable();
        gradientDrawable1.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable1.setColor(0Xe0ffffff);
        gradientDrawable1.setCornerRadius(24);

        linearLayout = findViewById(R.id.linearLayout);
        linearLayout.setBackground(gradientDrawable1);

        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable2.setColor(0Xf9ffffff);
        gradientDrawable2.setCornerRadius(50);

        GradientDrawable gradientDrawable3 = new GradientDrawable();
        gradientDrawable3.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable3.setColor(0Xf9ffffff);
        gradientDrawable3.setCornerRadius(50);

        temperature.setBackground(gradientDrawable3);
        umbrella.setBackground(gradientDrawable3);
        motion.setBackground(gradientDrawable2);
        fishing.setBackground(gradientDrawable3);
        carWash.setBackground(gradientDrawable2);
        allergic.setBackground(gradientDrawable3);
        flu.setBackground(gradientDrawable2);
        textView4.setBackground(gradientDrawable2);


        city1 = findViewById(R.id.city1);

        f = new File("/data/data/com.example.androidweather/shared_prefs/weatherNowData.xml");
        LocationClient.setAgreePrivacy(true);
        try {
            mLocationClient = new LocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mLocationClient.registerLocationListener(new MyLocationListener());
        weatherNowData = getSharedPreferences("weatherNowData",MODE_PRIVATE).edit();
        weatherDailyData = getSharedPreferences("weatherDailyData",MODE_PRIVATE).edit();
        weatherLifeData = getSharedPreferences("weatherLifeData",MODE_PRIVATE).edit();
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.
                permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.
                permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.
                    size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            //处理定位，并在定位成功或失败后进行ui处理
            mLocationClientOption();
        }
    }
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

                    //背景图
                    Request backgroundImageRt = new Request.Builder()
                            .url("https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=zh-CN")
                            .build();
                    Response backgroundImageRtData = client.newCall(backgroundImageRt).execute();
                    String backgroundImageStr = backgroundImageRtData.body().string();

                    //写入文件
                    weatherNowData.putString("weatherNowData",responseData);
                    weatherDailyData.putString("weatherDailyData",responseData2);
                    weatherLifeData.putString("weatherLifeData",responseData3);
                    //确认生成文件
                    weatherNowData.apply();
                    weatherDailyData.apply();
                    weatherLifeData.apply();
                    //读取文件
                    SharedPreferences weatherNowDataSP = getSharedPreferences("weatherNowData",MODE_PRIVATE);
                    SharedPreferences weatherDailyDataSP = getSharedPreferences("weatherDailyData",MODE_PRIVATE);
                    SharedPreferences weatherLifeDataSP = getSharedPreferences("weatherLifeData",MODE_PRIVATE);
                    //进行ui修改
                    jsonReading(weatherNowDataSP.getString("weatherNowData",""),textView);
                    jsonReading(weatherDailyDataSP.getString("weatherDailyData",""),todayDateTV);
                    jsonReading(weatherLifeDataSP.getString("weatherLifeData",""),textView4);

                    //对背景图进行修改
                    JSONObject imageUrlJSON = new JSONObject(backgroundImageStr);
                    JSONArray jsonArray1 = new JSONArray(imageUrlJSON.getString("images"));
                    setBackgroundImageView(jsonArray1.getJSONObject(0));
                } catch (Exception e) {
                    //如果没网
                    toastUi();
                    Log.d("TAG", "run: "+e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

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
    private void setBackgroundImageView(JSONObject jsonObject1){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Picasso.get().load(backgroundImageUrl + jsonObject1.getString("url"))
                            .into(backgroundView);
                } catch (JSONException e) {
                    Log.d("TAG", "url:" + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    private void weatherLift(JSONObject jsonObject1){
        runOnUiThread(new Runnable() {
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
        runOnUiThread(new Runnable() {
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
                    windSB.append(jsonObject.getString("wind_direction")).append("\n").append(jsonObject.getString("wind_scale")).append("级");

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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    StringBuilder stringBuilder = new StringBuilder();
//                    stringBuilder.append("天气:").append(jsonObject.getJSONObject("now").getString("text")).append("\n");
                    stringBuilder.append(jsonObject.getJSONObject("now").getString("temperature"));
                    textView.setText(stringBuilder);
                    t3.setText("数据由心知天气提供");
                } catch (JSONException e) {
                    Log.d("TAG", "2" + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "开发初期您必须同意所有权限才可使用",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    mLocationClientOption();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
    private void mLocationClientOption(){
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            TextView locationTV = findViewById(R.id.locationTV);
            if (bdLocation.getCity() == "" || bdLocation.getCity() == null){
                if(f.exists()){
                    //存在
                    SharedPreferences weatherNowDataSP = getSharedPreferences("weatherNowData",MODE_PRIVATE);
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(weatherNowDataSP.getString("weatherNowData",""));
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("results"));
                        city = jsonArray.getJSONObject(0).getJSONObject("location").getString("name");
                        Toast.makeText(MainActivity.this,"请打开定位服务",Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Log.d("TAG", "onReceiveLocation: 更改城市错误");
                        e.printStackTrace();
                    }

                    //存在
                    //先读文件
                    //读取文件
                    SharedPreferences weatherNowDataSP1 = getSharedPreferences("weatherNowData",MODE_PRIVATE);
                    SharedPreferences weatherDailyDataSP = getSharedPreferences("weatherDailyData",MODE_PRIVATE);
                    SharedPreferences weatherLifeDataSP = getSharedPreferences("weatherLifeData",MODE_PRIVATE);
                    //修改UI
                    jsonReading(weatherNowDataSP1.getString("weatherNowData",""),textView);
                    jsonReading(weatherDailyDataSP.getString("weatherDailyData",""),todayDateTV);
                    jsonReading(weatherLifeDataSP.getString("weatherLifeData",""),textView4);
                    //在发送网络请求
                }else {
                    city = "北京";
                    Toast.makeText(MainActivity.this, "未能成功定位,默认显示北京", Toast.LENGTH_SHORT).show();
                }
                locationTV.setText("请打开位置服务");
            }else {
                city = bdLocation.getCity().substring(0,bdLocation.getCity().length() - 1);
                locationTV.setText(" ");
            }
            sendRequestWithOkHttp();
            city1.setText(city);
        }


    }
    private void toastUi(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,"网络错误,请检查网络连接",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void refreshWeathers(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLocationClient.requestLocation();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }
}