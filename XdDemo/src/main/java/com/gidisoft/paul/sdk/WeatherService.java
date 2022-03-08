package com.gidisoft.paul.sdk;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherService {
    private String temperature;
    private String humidity;
    private String pm25;

    public String getTemperature() {
        return temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getPm25() {
        return pm25;
    }

    public void getWeatherDataBySeniverseApi() {
        try {
            OkHttpClient client = new OkHttpClient();//创建一个OkHttp实例

            Request request = new Request.Builder().url("https://api.seniverse.com/v3/weather/now.json?key=SnZApsFFw0O_dJZSV&location=foshan&language=zh-Hans&unit=c").build();//创建Request对象发起请求,记得替换成你自己的key
            Request request1 = new Request.Builder().url("http://api.seniverse.com/v3/air/now.json?key=SnZApsFFw0O_dJZSV&location=foshan&language=zh-Hans&scope=city").build();//创建Request对象发起请求,记得替换成你自己的key


            Response response = client.newCall(request).execute();//创建call对象并调用execute获取返回的数据
            Response response1 = client.newCall(request1).execute();

            String responseData = response.body().string();
            String responseData1 = response1.body().string();

            parseJSONWithJSONObject(responseData, responseData1);//解析SSON数据
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseJSONWithJSONObject(String jsonData, String jsonData1) {//用JSONObect解析JSON数据
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray results = jsonObject.getJSONArray("results");   //得到键为results的JSONArray
            JSONObject now = results.getJSONObject(0).getJSONObject("now");//得到键值为"now"的JSONObject
            JSONObject location = results.getJSONObject(0).getJSONObject("location");   //得到键值为location的JSONObject

            temperature = now.getString("temperature"); //获取温度
            humidity = now.getString("humidity");

            jsonObject = new JSONObject(jsonData1);
            results = jsonObject.getJSONArray("results");   //得到键为results的JSONArray
            JSONObject city = results.getJSONObject(0).getJSONObject("air").getJSONObject("city");
            pm25 = city.getString("pm25");

            Log.d("气温", temperature);
            Log.d("湿度", humidity);
            Log.d("pm2.5", pm25);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
