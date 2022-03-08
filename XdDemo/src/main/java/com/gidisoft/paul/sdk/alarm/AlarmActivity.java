package com.gidisoft.paul.sdk.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.gidisoft.paul.sdk.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AlarmActivity extends Activity {
    private ListView mListView;
    private MyAdpter mAdapter;
    ArrayList<Beanitem>  mListItems;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        ((Button)findViewById(R.id.add_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlarmActivity.this, TimeActivity.class);
                startActivityForResult(intent,REQUEST_CODE_TIME);
            }
        });


        ((Button)findViewById(R.id.remove_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListItems!=null) {
                    mListItems.remove(mAdapter.selected);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        ((Button)findViewById(R.id.back_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmActivity.this.finish();
            }
        });

        mp = new MediaPlayer();
        AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.alarm);
        try {
            mp.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
                    file.getLength());
            mp.prepare();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.setVolume(0.5f, 0.5f);
        mp.setLooping(true);
        //mp.start();
        //alarmOialog();

        mListView = (ListView)findViewById(R.id.listview);
        //String [] index = {"1","2","3"};
       // String [] name = {"起床","午休","睡觉"};
        //String [] time = {"08:00","12:00","23:00"};

        mListItems = new ArrayList<Beanitem>();
//        for(int i = 0; i< name.length; i++){
//            Beanitem item  = new Beanitem();
//            item.index=index[i];
//            item.name=name[i];
//            item.time=time[i];
//            mListItems.add(item);
//        }
        mAdapter = new MyAdpter(this, mListItems);
        mListView.setAdapter(mAdapter);
    }

    class Beanitem{
        String index;String name;String time;
    }
    class ViewHolder{
        LinearLayout ll;
        TextView index;
        TextView name;
        TextView time;
    }
    class MyAdpter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context mContext;
        private ArrayList<Beanitem> mList;
        MyAdpter(Context context,ArrayList<Beanitem> beanitems){
            this.mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.mList = beanitems;
        }
        int selected=0;

        public void setSelected(int selected) {
            this.selected = selected;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup arg2) {
            ViewHolder mHolder = new ViewHolder();
            Beanitem enity = mList.get(position);
            if (null == convertView) {
                convertView = mInflater.inflate(R.layout.simple_item, null);
                mHolder.ll = (LinearLayout) convertView
                        .findViewById(R.id.ll);
                mHolder.index = (TextView) convertView
                        .findViewById(R.id.index_txt);
                mHolder.name = (TextView) convertView
                        .findViewById(R.id.name_txt);
                mHolder.time = (TextView) convertView
                        .findViewById(R.id.score_txt);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            mHolder.index.setText(enity.index);
            mHolder.name.setText(enity.name);
            mHolder.time.setText(enity.time);
            if (selected == position) {
                mHolder.ll.setBackgroundColor(Color.parseColor("#C9C9C9"));
            } else {
                mHolder.ll.setBackgroundColor(Color.TRANSPARENT);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelected(position);
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public Object getItem(int arg0) {
            return mList.get(arg0);
        }

        @Override
        public int getCount() {
            return mList.size();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMedia();
    }
    private void stopMedia(){
        stop();
//        if (mp != null) {
//            if (mp.isPlaying()) {
//                mp.stop();
//            }
//            mp.release();
//        }
    }
    public void stop() {
        if (mp != null) {
            try {
                mp.stop();
            } catch (IllegalStateException e) {
                // TODO 如果当前java状态和jni里面的状态不一致，
                //e.printStackTrace();
                mp = null;
                mp = new MediaPlayer();
            }
            mp.release();
            mp = null;
        }
    }
    public static String getDateTime(long data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(data);
    }
    public static final int REQUEST_CODE_TIME = 3;
    public static final int RESPONSE_CODE_TIME = 7;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TIME && resultCode == RESPONSE_CODE_TIME) {
            Beanitem beanitem = new Beanitem();
            beanitem.index = ""+mListItems.size();
            beanitem.name = ""+data.getStringExtra("name");
            long time = data.getLongExtra("time",0);
            beanitem.time = ""+getDateTime(time);
            mListItems.add(beanitem);
            mAdapter.notifyDataSetChanged();
            Toast.makeText(this, "闹钟设置成功！", Toast.LENGTH_SHORT).show();
        }
    }
    public void alarmOialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("你有未处理的事件");
        builder.setPositiveButton("稍后提醒",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        stopMedia();
                        alarm();
                    }
                });

        builder.setNegativeButton("停止", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancleAlarm();
                stopMedia();
            }
        });
        builder.show().setCanceledOnTouchOutside(false);

    }


    private void alarm() {
        // 获取系统的闹钟服务
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // 触发闹钟的时间（毫秒）
        long triggerTime = System.currentTimeMillis() + 10000;

        Intent intent = new Intent(this, RepeatingAlarm.class);
        intent.setAction("com.gidi.xiaodu.alarm");
        PendingIntent op = PendingIntent.getBroadcast(this, 0, intent, 0);
        // 启动一次只会执行一次的闹钟
        am.set(AlarmManager.RTC, triggerTime, op);
        // 指定时间重复执行闹钟
//         am.setRepeating(AlarmManager.RTC,triggerTime,2000,op);
    }
    private void setAlarm(Calendar c){
        Intent intent = new Intent(this, RepeatingAlarm.class);
        intent.setAction("com.gidi.xiaodu.alarm");
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        // Schedule the alarm!
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC, c.getTimeInMillis(), sender);//c为设置闹钟的时间的Calendar对象
    }
    /**
     * 取消闹钟
     */
    private void cancleAlarm(){
        Intent intent = new Intent(AlarmActivity.this,RepeatingAlarm.class);
        intent.setAction("com.gidi.xiaodu.alarm");
        PendingIntent sender = PendingIntent.getBroadcast(AlarmActivity.this, 0, intent, 0);
        // And cancel the alarm.
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.cancel(sender);//取消闹钟
    }

}
