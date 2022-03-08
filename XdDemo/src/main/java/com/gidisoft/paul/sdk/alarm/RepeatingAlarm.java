package com.gidisoft.paul.sdk.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

public class RepeatingAlarm extends BroadcastReceiver {

    //    @Override
////    public void onReceive(Context context, Intent intent) {
////        if (intent.getAction()!=null&&intent.getAction().equals("com.gidi.xiaodu.alarm")) {//自定义的action
////            intent = new Intent(context, AlarmActivity.class);
////            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////            context.startActivity(intent);
//        }
//    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Log.e("111", "开始震动");
//          接收到广播后响起闹铃,振动器震动 30 秒
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(3 * 10000);
        }
    }
}
