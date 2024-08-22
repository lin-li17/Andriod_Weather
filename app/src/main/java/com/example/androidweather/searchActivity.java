package com.example.androidweather;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class searchActivity extends AppCompatActivity {
    //搜索框实例
    private EditText searchCity;

    private GridLayout gridLayout;//网格布局实例
    private TextView searchTV;

    //用户输入
    private String searchCityStr;
    //api
    private String searchUrl = "https://api.seniverse.com/v3/location/search.json?key=your_key&q=";

    private String searchCityUrl;//用来搜索的最终api

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        searchCity = findViewById(R.id.searchCity);
        gridLayout = findViewById(R.id.gridLayout);
        searchTV = findViewById(R.id.searchTV);
    }

    //搜索的点击事件
    public void searchStar(View view){
        searchCityStr = searchCity.getText().toString();
        searchCityUrl = searchUrl + searchCityStr;
        sendRequestWithOkHttp();
    }
    //网络访问
    private void sendRequestWithOkHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    searchTV.setText("");
                    HashMap<String,String> map = new HashMap<>();
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(searchCityUrl)
                            .build();
                    Response response1 = client.newCall(request).execute();
                    String responseData = response1.body().string();
                    //处理json文件
                    JSONObject jsonObject = new JSONObject(responseData);
                    if (jsonObject.has("status")){
                        unSearch();
                    }else {
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("results"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String str = jsonArray.getJSONObject(i).getString("path");
                            int a = str.indexOf(',', 0);
                            str = str.substring(a + 1);
                            if (str.substring(str.length() - 2).equals("中国")) {
                                String city = str.substring(0, str.indexOf(',', 0));
                                if (!map.containsKey(str)) {
                                    //不存在
                                    //添加
                                    map.put(str, city);
                                }
                            }
                        }
                        gridLayoutAdd(map);
                    }
                } catch (IOException e) {
                    toastUi();
                    e.printStackTrace();
                } catch (JSONException e) {
                    toastJSONUi();
                    Log.d("TAG", "run: 处理文件异常:" + e.getMessage());
                    e.printStackTrace();
                }

            }
        }).start();
    }
    private void unSearch(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchTV.setText("无搜索结果");
            }
        });
    }
    private void toastJSONUi(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(searchActivity.this, "搜索频繁,请尝试更换关键词", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //城市选择
    private void gridLayoutAdd(HashMap<String,String> map){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                gridLayout.removeAllViews();
                for (Map.Entry<String,String> entry : map.entrySet()){
                    GradientDrawable gradientDrawable = new GradientDrawable();
                    gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                    gradientDrawable.setColor(0X15555555);
                    gradientDrawable.setCornerRadius(50);
                    Button button = new Button(searchActivity.this);
                    button.setText(entry.getValue());
                    GridLayout.LayoutParams param= new GridLayout.LayoutParams(
                            GridLayout.spec(GridLayout.UNDEFINED,1f),
                            GridLayout.spec(GridLayout.UNDEFINED,1f));
                    param.setMargins(10,10,10,10);
                    button.setLayoutParams(param);
                    button.setBackground(gradientDrawable);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //按钮点击
                            Intent intent = new Intent(searchActivity.this, MainActivity.class);
                            intent.putExtra ("stringKey", button.getText());
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
                    gridLayout.addView(button);
                }
            }
        });
    }

    //结束当前页面
    public void finishSearchActivity(View view){
        searchActivity.this.finish();
    }

    //网络错误弹窗
    private void toastUi(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(searchActivity.this,"网络错误,请检查网络连接",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
