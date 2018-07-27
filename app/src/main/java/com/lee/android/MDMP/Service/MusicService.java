package com.lee.android.MDMP.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.lee.android.MDMP.Activity.PlayerActivity;
import com.lee.android.MDMP.Tool.Utility;


public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    private MediaPlayer player;
    private Thread updateprogress;
    public static final String INIT = "com.lee.android.MDMP.INIT";
    public static final String PLAY = "com.lee.android.MDMP.PLAY";
    public static final String CONTINUE = "com.lee.android.MDMP.CONTINUE";
    public static final String PAUSE = "com.lee.android.MDMP.PAUSE";
    public static final String PROGRESS = "com.lee.android.MDMP.PROGRESS";
    public static final String COMPLETE = "com.lee.android.MDMP.COMPLETE";

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(INIT);
        filter.addAction(PLAY);
        filter.addAction(PAUSE);
        filter.addAction(CONTINUE);
        filter.addAction(PlayerActivity.PROGRESS);
        serviceReceiver receiver = new serviceReceiver();
        registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        String path = intent.getStringExtra("PATH");
        if (action.equals(PLAY)) {
            play(path);
        } else if (action.equals(INIT)) {
            init(path);
        } else if (action.equals(CONTINUE)) {
            player.start();
            updateprogress = new ProgressUpdater();
            updateprogress.start();
        } else if (action.equals(PAUSE)) {
            player.pause();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        player.release();
        player.stop();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Intent intent = new Intent(MusicService.COMPLETE);
        sendBroadcast(intent);
    }

    public void play(String path) {
        player.reset();
        try {
            player.setDataSource(path);
            player.prepare();
            player.start();
            if (updateprogress == null || !updateprogress.isAlive()) {
                updateprogress = new ProgressUpdater();
                updateprogress.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(String path) {
        player.reset();
        try {
            player.setDataSource(path);
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ProgressUpdater extends Thread {
        @Override
        public void run() {
            try {
                while (player != null && player.isPlaying()) {
                    Intent intent = new Intent(MusicService.PROGRESS);
                    int progress = (int) ((double) player.getCurrentPosition() / player.getDuration() * 100);
                    intent.putExtra("progress", progress);
                    String time= Utility.formatTime(player.getCurrentPosition());
                    intent.putExtra("playtime",time);
                    sendBroadcast(intent);
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class serviceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String path = intent.getStringExtra("PATH");
            if (intent.getAction().equals(INIT)) {
                init(path);
            } else if (intent.getAction().equals(PLAY)) {
                play(path);
            } else if (intent.getAction().equals(CONTINUE)) {
                player.start();
                updateprogress = new ProgressUpdater();
                updateprogress.start();
            } else if (intent.getAction().equals(PAUSE)) {
                player.pause();
            } else if (intent.getAction().equals(PlayerActivity.PROGRESS)) {
                int time=intent.getIntExtra("time",0);
                player.seekTo(time);
            }
        }
    }
}

