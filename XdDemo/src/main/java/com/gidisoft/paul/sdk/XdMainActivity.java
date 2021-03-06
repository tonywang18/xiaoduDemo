package com.gidisoft.paul.sdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gidisoft.paul.sdk.alarm.AlarmActivity;
import com.gidisoft.paul.sdk.face.DetecterActivity;
import com.gidisoft.paul.sdk.face.RegisterActivity;
import com.gidisoft.paul.sdk.orm.UserBean;
import com.gidisoft.paul.sdk.orm.UserinfoDao;

import java.util.ArrayList;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.mscv5plusdemo.TtsDemo;
import com.iflytek.mscv5plusdemo.WakeDemo;

import org.json.JSONException;
import org.json.JSONObject;

public class XdMainActivity extends Activity implements OnClickListener {
	private final String TAG = this.getClass().toString();

	private static final int REQUEST_CODE_IMAGE_CAMERA = 1;
	private static final int REQUEST_CODE_IMAGE_OP = 2;
	private static final int REQUEST_CODE_OP = 3;
	Button zuxiao_btn;


	TextView user_name;
	boolean login_status = false;
	String chuan_name;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);

		user_name = this.findViewById(R.id.user_name);

		View v = this.findViewById(R.id.tv_face_login);
		v.setOnClickListener(this);

		v = this.findViewById(R.id.tv_face_register);
		v.setOnClickListener(this);

		v = this.findViewById(R.id.tv_face_enter);
		v.setOnClickListener(this);

		v = this.findViewById(R.id.zuxiao_btn);
		v.setOnClickListener(this);    //xie???????????????????????????

		uiHandler = new UIHandler();
		mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);

		SpeechUtility.createUtility(getBaseContext(), SpeechConstant.APPID +"="+ getResources().getString(com.iflytek.mscv5plusdemo.R.string.app_id));
		// ?????????????????????
		mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
		// ?????????????????????
		mIvw = VoiceWakeuper.createWakeuper(this, null);


		initWake();
		//??????


	}


	/**
	 * ??????????????????
	 */
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(TAG, "InitListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("???????????????,????????????"+code+",???????????????https://www.xfyun.cn/document/error-code??????????????????");

			} else {
				// ????????????????????????????????????startSpeaking??????
				// ????????????????????????onCreate???????????????????????????????????????????????????startSpeaking???????????????
				// ?????????????????????onCreate??????startSpeaking??????????????????
			}
		}
	};
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		if( null != mTts ){
			mTts.stopSpeaking();
			// ?????????????????????
			mTts.destroy();
		}
		// ??????????????????
		mIvw = VoiceWakeuper.getWakeuper();
		if (mIvw != null) {
			mIvw.destroy();
		}
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_OP && resultCode == RESPONSE_CODE_REGISTER_SUCCESS) {
			String name = data.getStringExtra("name");
			Message msg = Message.obtain();
			msg.what = MSG_QUERY;
			msg.obj = name;
			uiHandler.handleMessage(msg);
			Toast.makeText(this, "??????"+name+"???????????????????????????", Toast.LENGTH_SHORT).show();
		}else if (requestCode == REQUEST_CODE_OP && resultCode == RESPONSE_CODE_REGISTER_FAIL) {
			Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show();
		}else if (requestCode == REQUEST_CODE_OP && resultCode == RESPONSE_CODE_LOGIN_SUCCESS) {
			String name = data.getStringExtra("name");
			chuan_name =name;
			Message msg = Message.obtain();
			msg.what = MSG_LOGIN;
			msg.obj = name;
			uiHandler.handleMessage(msg);
			Toast.makeText(this, "????????????"+name+"??????", Toast.LENGTH_SHORT).show();


		}else if (requestCode == REQUEST_CODE_OP && resultCode == RESPONSE_CODE_LOGIN_FAIL) {
			Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show();
			login_status = false;
			//???????????????????????????


		}else if (requestCode == REQUEST_CODE_LOGIN && resultCode == RESPONSE_CODE_LOGIN_SUCCESS) {
			String name = data.getStringExtra("name");
			setLoginok(name);
//            Toast.makeText(this, "???????????????????????????---"+name, Toast.LENGTH_SHORT).show();
		}else
		if (requestCode == REQUEST_CODE_IMAGE_OP && resultCode == RESULT_OK) {
			Uri mPath = data.getData();
			String file = getPath(mPath);
			Bitmap bmp = XdApplication.decodeImage(file);
			if (bmp == null || bmp.getWidth() <= 0 || bmp.getHeight() <= 0 ) {
				Log.e(TAG, "error");
			} else {
				Log.i(TAG, "bmp [" + bmp.getWidth() + "," + bmp.getHeight());
			}
			startRegister(bmp, file);
		} else if (requestCode == REQUEST_CODE_OP) {
			Log.i(TAG, "RESULT =" + resultCode);
			if (data == null) {
				return;
			}
			Bundle bundle = data.getExtras();
			String path = bundle.getString("imagePath");
			Log.i(TAG, "path="+path);
		} else if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == RESULT_OK) {
			Uri mPath = ((XdApplication)(XdMainActivity.this.getApplicationContext())).getCaptureImage();
			String file = getPath(mPath);
			Bitmap bmp = XdApplication.decodeImage(file);
			startRegister(bmp, file);
		}
	}







	UIHandler uiHandler;
	final int MSG_QUERY=1;
	final int MSG_LOGIN=2;
	final int MSG_WAKE=3;
	class UIHandler extends android.os.Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == MSG_QUERY) {
				String name = (String)msg.obj;
				showtts("??????"+name+"???????????????????????????");
//				ArrayList<UserBean> ublist = UserinfoDao.getInstance(XdMainActivity.this).query(name);
//				if(ublist!=null&& ublist.size()>=1){
//					user_name.append("name:"+name + "/n password:"+ublist.get(0).getPassword());
//				}
				login_status = true;
			}else if (msg.what == MSG_LOGIN){
				String name = (String)msg.obj;
				showtts("????????????"+name+"??????");
				setLoginok(name);
				//????????????????????????

				Intent intententer = new Intent(XdMainActivity.this, NavActivity.class);
				Bundle bundle = new Bundle();                          //??????Bundle??????
				bundle.putString("something", "xiefeng");    //????????????
				intententer.putExtras(bundle);
				startActivity(intententer);
				//finish();   //???????????????????????????????????????





			}
			else if (msg.what == MSG_WAKE){
				String name = (String)msg.obj;
				showtts( "?????????");
			}
		}
	}
	@Override
	public void onClick(View paramView) {
		// TODO Auto-generated method stub
		switch (paramView.getId()) {
			case R.id.tv_face_login:
				facelogin();
				break;
			case R.id.tv_face_register:
				faceregister();
				break;
//			case R.id.tv_login:
//				Intent intent = new Intent(XdMainActivity.this, LoginActivity.class);
//				startActivityForResult(intent,REQUEST_CODE_LOGIN);
//				break;
			case R.id.zuxiao_btn:
				zhuxiaodenglu();
				break;				//xie???????????????

			case R.id.tv_face_enter:
				if(login_status){
					Intent intententer = new Intent(XdMainActivity.this, NavActivity.class);
					startActivity(intententer);
					finish();
				}else{
					Toast.makeText(XdMainActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}
	private void setLoginok(final String name){
		login_status = true;
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if(user_name!=null){
					user_name.setText(""+name);
				}
			}
		});

	}
	private void facelogin(){
		if( ((XdApplication)getApplicationContext()).mFaceDB.mRegister.isEmpty() ) {
			Toast.makeText(this, "????????????????????????????????????", Toast.LENGTH_SHORT).show();
		} else {

				startDetector(0);

		}
	}
	private void faceregister(){
		new AlertDialog.Builder(this)
				.setTitle("?????????????????????")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setItems(new String[]{"????????????", "????????????"}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which){
							case 1:
								Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
								ContentValues values = new ContentValues(1);
								values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
								Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
								((XdApplication)(XdMainActivity.this.getApplicationContext())).setCaptureImage(uri);
								intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
								startActivityForResult(intent, REQUEST_CODE_IMAGE_CAMERA);
								break;
							case 0:
								Intent getImageByalbum = new Intent(Intent.ACTION_GET_CONTENT);
								getImageByalbum.addCategory(Intent.CATEGORY_OPENABLE);
								getImageByalbum.setType("image/jpeg");
								startActivityForResult(getImageByalbum, REQUEST_CODE_IMAGE_OP);
								break;
							default:;
						}
					}
				})
				.show();
	}
	private void zhuxiaodenglu(){
		//??????????????????
		login_status = false;
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				user_name.setText("");   //???????????????????????????????????????????????????????????????????????????
			}
		});


	}  //xie???????????????????????????????????????

	/**
	 * @param uri
	 * @return
	 */
	private String getPath(Uri uri) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (DocumentsContract.isDocumentUri(this, uri)) {
				// ExternalStorageProvider
				if (isExternalStorageDocument(uri)) {
					final String docId = DocumentsContract.getDocumentId(uri);
					final String[] split = docId.split(":");
					final String type = split[0];

					if ("primary".equalsIgnoreCase(type)) {
						return Environment.getExternalStorageDirectory() + "/" + split[1];
					}

					// TODO handle non-primary volumes
				} else if (isDownloadsDocument(uri)) {

					final String id = DocumentsContract.getDocumentId(uri);
					final Uri contentUri = ContentUris.withAppendedId(
							Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

					return getDataColumn(this, contentUri, null, null);
				} else if (isMediaDocument(uri)) {
					final String docId = DocumentsContract.getDocumentId(uri);
					final String[] split = docId.split(":");
					final String type = split[0];

					Uri contentUri = null;
					if ("image".equals(type)) {
						contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					} else if ("video".equals(type)) {
						contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
					} else if ("audio".equals(type)) {
						contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
					}

					final String selection = "_id=?";
					final String[] selectionArgs = new String[] {
							split[1]
					};

					return getDataColumn(this, contentUri, selection, selectionArgs);
				}
			}
		}
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualimagecursor = this.getContentResolver().query(uri, proj, null, null, null);
		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		String img_path = actualimagecursor.getString(actual_image_column_index);
		String end = img_path.substring(img_path.length() - 4);
		if (0 != end.compareToIgnoreCase(".jpg") && 0 != end.compareToIgnoreCase(".png")) {
			return null;
		}
		return img_path;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
									   String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param mBitmap
	 */
	private void startRegister(Bitmap mBitmap, String file) {
		Intent it = new Intent(XdMainActivity.this, RegisterActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("imagePath", file);
		it.putExtras(bundle);
		startActivityForResult(it, REQUEST_CODE_OP);
	}

	private void startDetector(int camera) {
		Intent it = new Intent(XdMainActivity.this, DetecterActivity.class);
		it.putExtra("Camera", camera);
		startActivityForResult(it, REQUEST_CODE_OP);
	}
	private static final int REQUEST_CODE_LOGIN = 8;
	public static final int RESPONSE_CODE_REGISTER_SUCCESS = 4;
	public static final int RESPONSE_CODE_REGISTER_FAIL = 5;
	public static final int RESPONSE_CODE_LOGIN_SUCCESS = 6;
	public static final int RESPONSE_CODE_LOGIN_FAIL = 7;
	private Toast mToast;
	private void showTip(final String str){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mToast.setText(str);
				mToast.show();
			}
		});
	}
	protected void showtts(String text){
		// ????????????
		setParam();
		Log.d(TAG, "??????????????? " + System.currentTimeMillis());
		int code = mTts.startSpeaking(text, new SynthesizerListener(){

			@Override
			public void onSpeakBegin() {

			}

			@Override
			public void onBufferProgress(int i, int i1, int i2, String s) {

			}

			@Override
			public void onSpeakPaused() {

			}

			@Override
			public void onSpeakResumed() {

			}

			@Override
			public void onSpeakProgress(int i, int i1, int i2) {

			}

			@Override
			public void onCompleted(SpeechError speechError) {

			}

			@Override
			public void onEvent(int i, int i1, int i2, Bundle bundle) {

			}
		});
//			/**
//			 * ????????????????????????????????????,????????????????????????startSpeaking??????
//			 * text:?????????????????????uri:?????????????????????????????????listener:????????????
//			*/
//			String path = Environment.getExternalStorageDirectory()+"/tts.pcm";
//			int code = mTts.synthesizeToUri(text, path, mTtsListener);

		if (code != ErrorCode.SUCCESS) {
			showTip("??????????????????,?????????: " + code + ",???????????????https://www.xfyun.cn/document/error-code??????????????????");
		}
	}

	// ??????????????????
	private SpeechSynthesizer mTts;
	// ????????????
	private String mEngineType = SpeechConstant.TYPE_LOCAL;
	// ?????????????????????
	public static String voicerLocal="xiaoyan";
	/**
	 * ????????????
	 */
	private void setParam(){
		// ????????????
		mTts.setParameter(SpeechConstant.PARAMS, null);

			//????????????????????????
			mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
			//???????????????????????????
			mTts.setParameter(ResourceUtil.TTS_RES_PATH,getResourcePath());
			//???????????????
			mTts.setParameter(SpeechConstant.VOICE_NAME,voicerLocal);

		//mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY,"1");//????????????????????????????????????synthesizeToUri???????????????
		//??????????????????
		mTts.setParameter(SpeechConstant.SPEED, "50");
		//??????????????????
		mTts.setParameter(SpeechConstant.PITCH, "50");
		//??????????????????
		mTts.setParameter(SpeechConstant.VOLUME,  "50");
		//??????????????????????????????
		mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
		//	mTts.setParameter(SpeechConstant.STREAM_TYPE, AudioManager.STREAM_MUSIC+"");

		// ??????????????????????????????????????????????????????true
		mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

		// ???????????????????????????????????????????????????pcm???wav??????????????????sd????????????WRITE_EXTERNAL_STORAGE??????
		mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");

		mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");


	}
	//???????????????????????????
	private String getResourcePath(){
		StringBuffer tempBuffer = new StringBuffer();
		String type= "tts";
		if(mEngineType.equals(SpeechConstant.TYPE_XTTS)){
			type="xtts";
		}
		//??????????????????
		tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, type+"/common.jet"));
		tempBuffer.append(";");
		//???????????????
		if(mEngineType.equals(SpeechConstant.TYPE_XTTS)){
			tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, type+"/"+ TtsDemo.voicerXtts+".jet"));
		}else {
			tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, type + "/" + TtsDemo.voicerLocal + ".jet"));
		}

		return tempBuffer.toString();
	}
	private int curThresh = 1450;
	private String keep_alive = "0";
	private String ivwNetMode = "0";
	// ??????????????????
	private VoiceWakeuper mIvw;
	private void initWake(){
		mIvw = VoiceWakeuper.getWakeuper();
		if (mIvw != null) {
			// ????????????
			mIvw.setParameter(SpeechConstant.PARAMS, null);
			// ???????????????????????????????????????????????????????????????id:??????;id:????????????????????????
			mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:" + curThresh);
			// ??????????????????
			mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
			// ????????????????????????
			mIvw.setParameter(SpeechConstant.KEEP_ALIVE, keep_alive);
			// ??????????????????????????????
			mIvw.setParameter(SpeechConstant.IVW_NET_MODE, ivwNetMode);
			// ????????????????????????
			mIvw.setParameter(SpeechConstant.IVW_RES_PATH, getResource());
			// ???????????????????????????????????????????????????????????????
			mIvw.setParameter(SpeechConstant.IVW_AUDIO_PATH, Environment.getExternalStorageDirectory().getPath() + "/msc/ivw.wav");
			mIvw.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
			// ????????????????????? NOTIFY_RECORD_DATA ??????????????? onEvent ???????????????????????????
			//mIvw.setParameter( SpeechConstant.NOTIFY_RECORD_DATA, "1" );
			// ????????????
			/*	mIvw.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");*/

			//mIvw.startListening(mWakeuperListener);     ??????  ?????????????????????????????????????????????


			/*File file = new File(Environment.getExternalStorageDirectory().getPath() + "/msc/ivw1.wav");
				byte[] byetsFromFile = getByetsFromFile(file);
				mIvw.writeAudio(byetsFromFile,0,byetsFromFile.length);*/
			//	mIvw.stopListening();
		} else {
			showTip("??????????????????");
		}
	}
	private String getResource() {
		final String resPath = ResourceUtil.generateResourcePath(XdMainActivity.this, ResourceUtil.RESOURCE_TYPE.assets, "ivw/"+getString(com.iflytek.mscv5plusdemo.R.string.app_id)+".jet");
		Log.d( TAG, "resPath: "+resPath );
		return resPath;
	}
	private WakeuperListener mWakeuperListener = new WakeuperListener() {

		@Override
		public void onResult(WakeuperResult result) {
			Log.d(TAG, "onResult");
			try {
				String text = result.getResultString();
				JSONObject object;
				object = new JSONObject(text);
				StringBuffer buffer = new StringBuffer();
				buffer.append("???RAW??? "+text);
				buffer.append("\n");
				buffer.append("??????????????????"+ object.optString("sst"));
				buffer.append("\n");
				buffer.append("????????????id???"+ object.optString("id"));
				buffer.append("\n");
				buffer.append("????????????" + object.optString("score"));
				buffer.append("\n");
				buffer.append("???????????????" + object.optString("bos"));
				buffer.append("\n");
				buffer.append("???????????????" + object.optString("eos"));
				Message msg = Message.obtain();
				msg.what = MSG_WAKE;
				uiHandler.handleMessage(msg);
			} catch (JSONException e) {
				Log.d(TAG,"??????????????????");
				e.printStackTrace();
			}

		}

		@Override
		public void onError(SpeechError error) {
			showTip(error.getPlainDescription(true));

		}

		@Override
		public void onBeginOfSpeech() {
		}

		@Override
		public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
			switch( eventType ){
				// EVENT_RECORD_DATA ???????????? NOTIFY_RECORD_DATA ???????????? ??? ?????????
				case SpeechEvent.EVENT_RECORD_DATA:
					final byte[] audio = obj.getByteArray( SpeechEvent.KEY_EVENT_RECORD_DATA );
					Log.i( TAG, "ivw audio length: "+audio.length );
					break;
			}
		}

		@Override
		public void onVolumeChanged(int volume) {

		}
	};
}

