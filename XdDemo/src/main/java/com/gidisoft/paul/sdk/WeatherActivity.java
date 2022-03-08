package com.gidisoft.paul.sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gidisoft.paul.sdk.alarm.AlarmActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherActivity extends Activity implements View.OnClickListener{

    private EditText putin;
    private TextView responseText;
    private EditText weather;
    private EditText city;
    private EditText temperature;
    private String Putin;
    private String Weather;
    private String CityName;
    private String Tempeature;

 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);


         Button sendRequest = (Button) findViewById(R.id.send_request);
         putin=(EditText)findViewById(R.id.putin);
         responseText = (TextView) findViewById(R.id.response);
         weather = (EditText) findViewById(R.id.Weather);
         city = (EditText) findViewById(R.id.City);
         temperature = (EditText) findViewById(R.id.Temperature);
         sendRequest.setOnClickListener(this);

         ((Button)findViewById(R.id.weather_back_btn)).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             WeatherActivity.this.finish();
         }
     });
        }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send_request) {
            sendRequestWithOkHttp();
        }
    }

    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建一个OkHttp实例
                    Putin=putin.getText().toString();
                    Request request = new Request.Builder().url("https://api.seniverse.com/v3/weather/now.json?key=SrvH71t8JeTOXNLJP&location="+Putin+"&language=zh-Hans&unit=c").build();//创建Request对象发起请求
                    Response response = client.newCall(request).execute();//创建call对象并调用execute获取返回的数据
                    String responseData = response.body().string();
                    showResPonse(responseData);
                    parseJSONWithJSONObject(responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseJSONWithJSONObject(String jsonData) {//用JSONObect解析JSON数据
        try {
            JSONObject jsonObject = new JSONObject(jsonData);   //response为返回的String型json数据
            JSONArray results = jsonObject.getJSONArray("results");      //得到键为results的JSONArray
            JSONObject now = results.getJSONObject(0).getJSONObject("now");
            JSONObject location = results.getJSONObject(0).getJSONObject("location");   //得到results数组第一个数据中键为location的JSONObject
            Weather = now.getString("text");
            CityName = location.getString("name");                      //获得城市名
            Tempeature = now.getString("temperature" );                    //获取温度
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showResPonse(final String response) {
        runOnUiThread(new Runnable() {//切换到主线程,ui界面的更改不能出现在子线程
            @Override
            public void run() {
                responseText.setText(response);
                city.setText(CityName);
                weather.setText(Weather);
                temperature.setText(Tempeature);
            }
        });

    }




    protected void onActivityResult(int resultCode, Intent data, int requestCode) {
        super.onActivityResult(requestCode, resultCode, data);


    }


}


