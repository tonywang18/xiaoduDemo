package com.gidisoft.sdk.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/****
 * 后台的Service 用于播放铃声
 *
 * @author zpaul
 *
 */
public class SoundService extends Service {

    private static final String TAG = "SoundService";
    private SoundBinder mBinder;
    private MediaPlayer mMediaPlayer;

    private int currentMusic;

    private int getCurrentMusic() {
        return currentMusic;
    }

    private void setCurrentMusic(int currentMusic) {
        this.currentMusic = currentMusic;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "--onBind--");
        if (mBinder == null) {
            mBinder = new SoundBinder();
        }
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "--onCreate--");
        initSchedule();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "--onStartCommand--");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeSchedule();
        releaseMediaPlayer();
        Log.d(TAG, "--onDestroy--");
    }

    public class SoundBinder extends Binder {
        /**
         * set bequiet status
         **/
        public void stopPlay(int music) {
            playMusic(SOUND_STOP);
        }

        /**
         * start play the alarm music
         **/
        public void startPlay(int music) {
            playMusic(music);
        }
    }

    /**
     * initialize the MediaPlayer
     */
    private void createMediaPlayer(AssetFileDescriptor fileDescriptor) {
        try {
        if (isPlayingMediaPlayer()) {
            stopMediaPlayer();
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
        mMediaPlayer.prepareAsync();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                startMediaPlayer();//当装载流媒体完毕的时候回调
            }
        });
        // 设置循环播放
        mMediaPlayer.setLooping(true);
        mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                //当流媒体播放完毕的时候回调
                startMediaPlayer();
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                     startMediaPlayer();
                     return false;
            }
        });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaPlayer getMediaPlayer(Context context) {
        MediaPlayer mediaplayer = new MediaPlayer();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return mediaplayer;
        }
        try {
            Class<?> cMediaTimeProvider = Class.forName("android.media.MediaTimeProvider");
            Class<?> cSubtitleController = Class.forName("android.media.SubtitleController");
            Class<?> iSubtitleControllerAnchor = Class.forName("android.media.SubtitleController$Anchor");
            Class<?> iSubtitleControllerListener = Class.forName("android.media.SubtitleController$Listener");
            Constructor constructor = cSubtitleController.getConstructor(
                    new Class[]{Context.class, cMediaTimeProvider, iSubtitleControllerListener});
            Object subtitleInstance = constructor.newInstance(context, null, null);
            Field f = cSubtitleController.getDeclaredField("mHandler");
            f.setAccessible(true);
            try {
                f.set(subtitleInstance, new Handler());
            } catch (IllegalAccessException e) {
                return mediaplayer;
            } finally {
                f.setAccessible(false);
            }
            Method setsubtitleanchor = mediaplayer.getClass().getMethod("setSubtitleAnchor",
                    cSubtitleController, iSubtitleControllerAnchor);
            setsubtitleanchor.invoke(mediaplayer, subtitleInstance, null);
        } catch (Exception e) {
            Log.w(TAG, "getMediaPlayer crash ,exception = " + e);
        }
        return mediaplayer;
    }

    private void startMediaPlayer() {
        if (null != mMediaPlayer) {
            mMediaPlayer.start();
        }
    }

    public static final int SOUND_PLAY = 1;
    public static final int SOUND_STOP = 0;
    private void stopMediaPlayer() {
        setCurrentMusic(SOUND_STOP);
        releaseMediaPlayer();
    }

    private void resetMediaPlayer() {
        if (null != mMediaPlayer) {
            mMediaPlayer.reset();
        }
    }

    private void releaseMediaPlayer() {
        if (null != mMediaPlayer) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
    private boolean isPlayingMediaPlayer() {
        if (mMediaPlayer == null) {
            return false;
        }
        return mMediaPlayer.isPlaying();
    }

    private void setMediaPlayer(int index) {
        try {
            switch (index) {
                case SOUND_PLAY:
                    AssetFileDescriptor fileDescriptor1 = getAssets().openFd("medium.mp3");
                    createMediaPlayer(fileDescriptor1);
                    setCurrentMusic(index);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /****
     * play music
     *
     * @param index
     */
    private void playMusic(int index) {
        switch (index) {
            case SOUND_PLAY:
                this.NOW_MUSIC = index;
                break;
            case SOUND_STOP:
                this.NOW_MUSIC = index;
                break;
        }
    }

    int NOW_MUSIC = SOUND_STOP;

    ScheduledExecutorService scheduledExecutorService;
    private void initSchedule() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                checkSound();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
    public void closeSchedule() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
            scheduledExecutorService=null;
        }
    }
    protected void checkSound(){
            switch (NOW_MUSIC) {
                case SOUND_PLAY:
                    if(getCurrentMusic()!=SOUND_PLAY) {
                        setMediaPlayer(NOW_MUSIC);
                    }
                    break;
                case SOUND_STOP:
                    if (isPlayingMediaPlayer()) {
                        stopMediaPlayer();
                    }
                    break;
            }
    }
}
