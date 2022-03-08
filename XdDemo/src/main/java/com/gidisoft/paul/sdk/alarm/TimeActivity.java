package com.gidisoft.paul.sdk.alarm;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.gidisoft.paul.sdk.R;
import com.gidisoft.sdk.tool.CustomDialog;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimeActivity extends Activity {

	private Context mContext;
	private LinearLayout setTimeLayout;
	private LinearLayout setDateLayout;

	private TextView dateTxt;
	private TextView timeTxt;
	EditText edit_name;
	private CustomDialog mDatePickerDialog;
	private CustomDialog mTimePickerDialog;

	private UIHandler mUIHandler;
	private ScheduledExecutorService scheduleThreadPool;

	private Calendar calendarDate;

	private int mYear, mMonth, mDay;// 获取的月份值比实际月份少一,o开始
	private int mHour, mMinute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time);
		mContext = this;
		initCalendar();
		setTimeLayout = (LinearLayout) findViewById(R.id.time_set_time_layout);
		setDateLayout = (LinearLayout) findViewById(R.id.time_set_date_layout);
		edit_name= (EditText) findViewById(R.id.edit_name);
		dateTxt = (TextView) findViewById(R.id.time_set_date_txt);
		timeTxt = (TextView) findViewById(R.id.time_set_time_txt);
		((Button)findViewById(R.id.btn_ok)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backtoAlarm(true);
			}
		});
		((Button)findViewById(R.id.btn_back)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backtoAlarm(false);
			}
		});

		mUIHandler = new UIHandler();

		initListener();
		initUpdateSysTime();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mUIHandler) {
			mUIHandler.removeCallbacksAndMessages(null);
			mUIHandler = null;
		}
		if (null != scheduleThreadPool) {
			scheduleThreadPool.shutdown();
			scheduleThreadPool = null;
		}
		cancelDialog();
		calendarDate = null;
	}

	protected void initListener() {
		setTimeLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showTimePickerDialog(mHour, mMinute);
			}
		});
		setDateLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDatePickerDialog(mYear, mMonth, mDay);
			}
		});


	}

	private void initCalendar() {
		calendarDate = Calendar.getInstance();
		mYear = calendarDate.get(Calendar.YEAR);
		mMonth = calendarDate.get(Calendar.MONTH);// 获取的值比实际月份少一
		mDay = calendarDate.get(Calendar.DAY_OF_MONTH);
		mHour = calendarDate.get(Calendar.HOUR_OF_DAY);
		mMinute = calendarDate.get(Calendar.MINUTE);
		Log.d("TimeActivity", "initData" + " mYear---" + mYear
				+ " mMonth---" + mMonth + " mDay---" + mDay);
		Log.d("TimeActivity", "initData" + " mHour---" + mHour
				+ " mMinute---" + mMinute);
	}

	private void initUpdateSysTime() {
		scheduleThreadPool = new ScheduledThreadPoolExecutor(1);
		scheduleThreadPool.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if(mUIHandler!=null) {
					// 更新时间
					mUIHandler.sendEmptyMessage(MSG_UPDATE_TIME);
				}
			}
		}, 0, 1000, TimeUnit.MILLISECONDS);
	}
	public static final int MSG_UPDATE_TIME = 38;
	private void cancelDialog() {
		if (null != mDatePickerDialog) {
			mDatePickerDialog.dismiss();
			mDatePickerDialog = null;
		}
		if (null != mTimePickerDialog) {
			mTimePickerDialog.dismiss();
			mTimePickerDialog = null;
		}
	}

	private void updateDate(int year, int monthOfYear, int dayOfMonth) {
		mYear = year;
		mMonth = monthOfYear;
		mDay = dayOfMonth;
		calendarDate.set(Calendar.YEAR, year);
		calendarDate.set(Calendar.MONTH, monthOfYear);
		calendarDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		long when_data = calendarDate.getTimeInMillis();
//		if ((when_data / 1000) < Integer.MAX_VALUE) {
//			SystemClock.setCurrentTimeMillis(when_data);
//		}
		Log.d("TimeActivity", "updateDate---" + "year---" + year
				+ " :" + " monthOfYear----" + monthOfYear + " : "
				+ "dayOfMonth---" + dayOfMonth);
	}

	private void updateTime(int hourOfDay, int minute) {
		mHour = hourOfDay;
		mMinute = minute;
		calendarDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendarDate.set(Calendar.MINUTE, minute);
		long when_time = calendarDate.getTimeInMillis();
//		if ((when_time / 1000) < Integer.MAX_VALUE) {
//			SystemClock.setCurrentTimeMillis(when_time);
//		}
		Log.d("TimeActivity", hourOfDay + ":" + minute);
	}

	private void showDatePickerDialog(int year, int month, int dayOfMonth) {
		if (null != mDatePickerDialog) {
			mDatePickerDialog.dismiss();
			mDatePickerDialog = null;
		}
		if (null == mDatePickerDialog) {
			mDatePickerDialog = new CustomDialog(this);
			LayoutInflater mInflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View pickView = mInflater.inflate(
					R.layout.dialog_datepicker_dialog, null);
			DatePicker datePicker = (DatePicker) pickView
					.findViewById(R.id.date_picker);
			datePicker.init(year, month, dayOfMonth,
					new OnDateChangedListener() {
						@Override
						public void onDateChanged(DatePicker view, int year,
                                                  int month, int day) {
							// 月份从0开始
							// calendarDate.set(year, month, day);
							mYear = year;
							mMonth = month;
							mDay = day;
							Log.i("TimeActivity",
									"showDatePickerDialog---" + "year--" + year
											+ " month---" + month + " day---"
											+ day);
						}
					});
			CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
			builder.setContentView(pickView);
			builder.setTitle("日期");
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							Log.i("TimeSettingActivity", "Year()--" + mYear
									+ " Month()---" + mMonth + " Day---" + mDay);
							updateDate(mYear, mMonth, mDay);
							mDatePickerDialog.dismiss();
						}
					});
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							mDatePickerDialog.dismiss();
							mDatePickerDialog = null;
						}
					});
			mDatePickerDialog = builder.create();
			mDatePickerDialog.show();
			mDatePickerDialog.setCancelable(false);
		}
	}

	private void showTimePickerDialog(int hour, int minute) {
		if (null != mTimePickerDialog) {
			mTimePickerDialog.dismiss();
			mTimePickerDialog = null;
		}
		if (null == mTimePickerDialog) {
			mTimePickerDialog = new CustomDialog(this);
			LayoutInflater mInflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View pickView = mInflater.inflate(
					R.layout.dialog_timepicker_dialog, null);
			TimePicker timePicker = (TimePicker) pickView
					.findViewById(R.id.timer_picker);
			timePicker.setIs24HourView(true);
			timePicker.setCurrentHour(calendarDate.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(calendarDate.get(Calendar.MINUTE));
			timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {

				@Override
				public void onTimeChanged(TimePicker view, int hourOfDay,
                                          int minute) {
					mHour = hourOfDay;
					mMinute = minute;
				}
			});
			CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
			builder.setContentView(pickView);
			builder.setTitle("时间");
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							updateTime(mHour, mMinute);
							mTimePickerDialog.dismiss();
						}
					});
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							mTimePickerDialog.dismiss();
							mTimePickerDialog = null;
						}
					});
			mTimePickerDialog = builder.create();
			mTimePickerDialog.show();
			mTimePickerDialog.setCancelable(false);
		}
	}
	private void backtoAlarm(boolean flag){
		if(flag) {
			Intent intent = new Intent(TimeActivity.this, AlarmActivity.class);
			intent.putExtra("time", calendarDate.getTimeInMillis());
			intent.putExtra("name", edit_name.getText().toString());
			//登录成功
			setResult(AlarmActivity.RESPONSE_CODE_TIME, intent);
		}
		finish();
	}
	public class UIHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_UPDATE_TIME:
				long date = calendarDate.getTimeInMillis();
				timeTxt.setText(getTime(date));
				dateTxt.setText(getDate(date));
				break;
			}
		}

	}
	public static String getDate(long data) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(data);
	}

	public static String getTime(long data) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(data);
	}

}
