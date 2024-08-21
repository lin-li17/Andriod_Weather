package com.example.androidweather;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements ContentFragment.OnDataPass{
    private String city;

    private static final int REQUEST_CODE = 1;

    private ImageView backgroundView;//背景图片

    public LocationClient mLocationClient;//位置监听器


    private TextView city1;

    private String backgroundImageUrl = "https://cn.bing.com/";//必应每日一图


    //vierpager2
    private ViewPager2 viewPager2;
    private ContentPagerAdapter pagerAdapter;

    File file = new File("/data/data/com.example.androidweather/files","city");

    private List<String> strArray;






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

        //viewpager2
        viewPager2 = findViewById(R.id.vp2);

        backgroundView = findViewById(R.id.backgroundView);

        city1 = findViewById(R.id.city1);


        //viewPager2添加页面
        pagerAdapter = new ContentPagerAdapter(this);

        LocationClient.setAgreePrivacy(true);
        try {
            mLocationClient = new LocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mLocationClient.registerLocationListener(new MyLocationListener());
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
            starActivity();
        }
    }
    //用来请求图片
    private void sendRequestWithOkHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //网络请求
                    OkHttpClient client = new OkHttpClient();
                    //背景图
                    Request backgroundImageRt = new Request.Builder()
                            .url("https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=zh-CN")
                            .build();
                    Response backgroundImageRtData = client.newCall(backgroundImageRt).execute();
                    String backgroundImageStr = backgroundImageRtData.body().string();
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
    //修改图片
    private void setBackgroundImageView(JSONObject jsonObject1){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Picasso.get()
                            .load(backgroundImageUrl + jsonObject1.getString("url"))
                            .into(backgroundView);
                } catch (JSONException e) {
                    Log.d("TAG", "url:" + e.getMessage());
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
                    starActivity();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
    private void starActivity(){
        if (file.exists()){
            //读取文件生成fragment
            String cityFile = readFromFile();
            String[] strings = cityFile.split(",");
            strArray = new ArrayList<>(Arrays.asList(strings));
            city = strings[0];
            for (String c : strings){
                pagerAdapter.addData(new ContentFragment(c));
            }
            viewPager2.setAdapter(pagerAdapter);
            saveToFile(String.join(",",strArray));
            setCity1(strings[0]);
        }else strArray = new ArrayList<>();
        //设置默认背景
        Picasso.get()
                .load(R.drawable.bing)
                .into(backgroundView);
        //联网改背景
        sendRequestWithOkHttp();
        //处理定位
        mLocationClientOption();
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
                if(!file.exists()){
                    //不存在
                    city = "北京";
                    Toast.makeText(MainActivity.this, "未能成功定位,默认显示北京", Toast.LENGTH_SHORT).show();
                }else city = strArray.get(0);
                locationTV.setText("请打开位置服务");
            }else {
                //定位到了该city
                city = bdLocation.getCity().substring(0,bdLocation.getCity().length() - 1);
                locationTV.setText(" ");
            }
            if (!strArray.get(0).equals(city)){
                city1.setText(city);
                if (pagerAdapter.getItemCount() < 1){
                    pagerAdapter.addData(new ContentFragment(city));
                    strArray.add(city);
                }else {
                    pagerAdapter.setDatas(0,new ContentFragment(city));
                    strArray.set(0,city);
                }
                saveToFile(String.join(",",strArray));
                viewPager2.setAdapter(pagerAdapter);
            }
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

    //添加城市页面跳转
    public void addTvClick(View view){
        if (strArray.size() >= 10){
            Toast.makeText(this, "当前最大数量为10,不可在添加", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(MainActivity.this, searchActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    //搜索回来的city
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                String result = data.getStringExtra("stringKey");
                // 执行你自定义的方法
                setCity(result);
            }
        }
    }
    private void setCity(String str){
        for (int i = 0; i < strArray.size(); i++) {
            if (strArray.get(i).equals(str)){
                viewPager2.setCurrentItem(i,true);
                return;
            }
        }
        //添加一个Fragment,将str传入
        strArray.add(str);
        saveToFile(String.join(",",strArray));

        ContentFragment newFragment = new ContentFragment(str);
        pagerAdapter.addData(newFragment);
        viewPager2.setCurrentItem(pagerAdapter.getItemCount() - 1,true);
    }
    private void setCity1(String str){
        city1.setText(str);
    }

    private void saveToFile(String data) {
        // 获取文件路径
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //读文件
    private String readFromFile() {
        // 获取文件路径
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
    //删除当前页面
    public void removeList(View view){
        int i = viewPager2.getCurrentItem();
        if (pagerAdapter.removeData(i)){
            viewPager2.setCurrentItem(i - 1);
            strArray.remove(i);
            saveToFile(String.join(",",strArray));
        }else Toast.makeText(this,"禁止删除定位城市",Toast.LENGTH_SHORT).show();
    }

    //立即进行定位
    public void setRequestLocation(View view){
        setFragmentHome(view);
        mLocationClient.requestLocation();
    }

    public void setFragmentHome(View view){
        viewPager2.setCurrentItem(0,true);
    }
    //在这里处理传过来的字符
    @Override
    public void onDataPass(String data) {
        setCity1(data);
    }

    //活动结束时需要结束的一切都要写
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }
}