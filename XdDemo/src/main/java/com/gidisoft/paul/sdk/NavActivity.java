package com.gidisoft.paul.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gidisoft.paul.sdk.alarm.AlarmActivity;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.mscv5plusdemo.AsrDemo;
import com.iflytek.mscv5plusdemo.TtsDemo;


@Route(path = "/xfxx/xx/NavDemo")

public class NavActivity extends Activity {

    @Autowired(name = "text")
    String text;

    Button mqtt_btn;
    TextView user_name2;

    private Switch mSwitch;
    private Switch mSwitch2;
    private Switch mSwitch3;
    private Switch mSwitch4;
    private final static String TAG = AsrDemo.class.getSimpleName();


    String text1 = "nihao";

    String user_name3;
    MqttManager mqttManager = new MqttManager(this);


    //首页天气
    private String temperature;
    private String humidity;
    private String pm25;
    private TextView tempeatureText, humidityText, pm25Text;
    private WeatherService weatherService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);


        ARouter.getInstance().inject(this);

        ((Button)findViewById(R.id.nav_back_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavActivity.this.finish();
            }
        });

        Bundle bundle = new Bundle();
        mqttManager = new com.gidisoft.paul.sdk.MqttManager(this);

        mqttManager.connect();

        text1 = text;


        user_name3 = bundle.getString("something");   //获取Bundle里面的字


        user_name2 = (TextView) findViewById(R.id.user_name1);
        user_name2.setText("你好，" + "欢迎回来");

        mqtt_btn = findViewById(R.id.alarm_btn);
        mqtt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavActivity.this, AlarmActivity.class);
                startActivity(intent);
            }
        });


        mqtt_btn = findViewById(R.id.huanxing_btn);
        mqtt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发起路由跳转
                ARouter.getInstance().build("/xf/WakeDemo").navigation();
            }
        });
        // 天气跳转
        mqtt_btn = findViewById(R.id.tianqi_btn);
        mqtt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavActivity.this, WeatherActivity.class);
                startActivity(intent);
            }
        });


        mqttManager.subscribe("testPublish", 2);
        // 灯光控制
        mSwitch = (Switch) findViewById(R.id.lantern);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Toast.makeText(NavActivity.this, "打开灯", Toast.LENGTH_SHORT).show();
                    mqttManager.publish("testPublish", "打开灯", false, 2);

                } else {
                    Toast.makeText(NavActivity.this, "关闭灯", Toast.LENGTH_SHORT).show();
                    mqttManager.publish("testPublish", "关闭灯", false, 2);

                }
            }
        });

        // 窗帘控制
        mSwitch2 = (Switch) findViewById(R.id.curtain);
        mSwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Toast.makeText(NavActivity.this, "打开窗帘", Toast.LENGTH_SHORT).show();
                    mqttManager.publish("testPublish", "打开窗帘", false, 2);


                } else {
                    Toast.makeText(NavActivity.this, "关闭窗帘", Toast.LENGTH_SHORT).show();
                    mqttManager.publish("testPublish", "关闭窗帘", false, 2);

                }
            }
        });

        // 空调控制
        mSwitch3 = (Switch) findViewById(R.id.conditioning);
        mSwitch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Toast.makeText(NavActivity.this, "打开空调", Toast.LENGTH_SHORT).show();
                    mqttManager.publish("testPublish", "打开空调", false, 2);

                } else {
                    Toast.makeText(NavActivity.this, "关闭空调", Toast.LENGTH_SHORT).show();
                    mqttManager.publish("testPublish", "关闭空调", false, 2);

                }
            }
        });

        // 电视控制
        mSwitch4 = (Switch) findViewById(R.id.tv);
        mSwitch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Toast.makeText(NavActivity.this, "打开电视", Toast.LENGTH_SHORT).show();
                    mqttManager.publish("testPublish", "打开电视", false, 2);

                } else {
                    Toast.makeText(NavActivity.this, "关闭电视", Toast.LENGTH_SHORT).show();
                    mqttManager.publish("testPublish", "关闭电视", false, 2);

                }
            }
        });


        // mqttManager.subscribe("testPublish", 0);


        if (text1 != null) {
            if (text1.contains("打开窗帘")) {

                mqttManager.publish("testPublish", "打开窗帘", false, 2);
                mSwitch2.setChecked(true);
                Toast.makeText(NavActivity.this, text1, Toast.LENGTH_SHORT).show();

            } else if (text1.contains("关闭窗帘")) {
                mqttManager.publish("testPublish", "关闭窗帘", false, 2);
                mSwitch2.setChecked(false);
                Toast.makeText(NavActivity.this, text1, Toast.LENGTH_SHORT).show();


            } else if (text1.contains("打开空调")) {
                mqttManager.publish("testPublish", "打开空调", false, 2);
                mSwitch3.setChecked(true);
                Toast.makeText(NavActivity.this, text1, Toast.LENGTH_SHORT).show();


            } else if (text1.contains("关闭空调")) {
                mSwitch3.setChecked(false);
                mqttManager.publish("testPublish", "关闭空调", false, 2);
                Toast.makeText(NavActivity.this, text1, Toast.LENGTH_SHORT).show();

            } else if (text1.contains("打开灯光")) {
                mqttManager.publish("testPublish", "打开灯光", false, 2);
                mSwitch.setChecked(true);
                Toast.makeText(NavActivity.this, text1, Toast.LENGTH_SHORT).show();

            } else if (text1.contains("关闭灯光")) {
                mSwitch.setChecked(false);
                mqttManager.publish("testPublish", "关闭灯光", false, 2);
                Toast.makeText(NavActivity.this, text1, Toast.LENGTH_SHORT).show();

            } else if (text1.contains("打开电视")) {

                Toast.makeText(NavActivity.this, text1, Toast.LENGTH_SHORT).show();
                mqttManager.publish("testPublish", "打开电视", false, 2);
                mSwitch4.setChecked(true);
            } else if (text1.contains("关闭电视")) {
                Toast.makeText(NavActivity.this, text1, Toast.LENGTH_SHORT).show();
                mSwitch4.setChecked(false);
                mqttManager.publish("testPublish", "关闭电视", false, 2);
            }


            // Toast.makeText(NavActivity.this, text1, Toast.LENGTH_SHORT).show();
            // mSwitch4.setChecked(true);
        }
    }


    protected void onActivityResult(int resultCode, Intent data, int requestCode) {
        super.onActivityResult(requestCode, resultCode, data);


    }

//    //首页天气
//    @SuppressLint("SetTextI18n")
//    private void initWeatherData() {
//        //切换到主线程,ui界⾯的更改不能出现在⼦线程,否则app会崩溃
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                weatherService = new WeatherService();
//                weatherService.getWeatherDataBySeniverseApi();
//                NavActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        tempeatureText.setText(weatherService.getTemperature() + "℃");
//                        humidityText.setText(weatherService.getHumidity() + "%");
//                        pm25Text.setText(weatherService.getPm25());
//                    }
//                });
//            }
//        }).start();
//    }





}


