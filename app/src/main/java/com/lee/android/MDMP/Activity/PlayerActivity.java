package com.lee.android.MDMP.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lee.android.MDMP.Service.MusicService;
import com.lee.android.MDMP.R;
import com.lee.android.MDMP.Model.Song;
import com.lee.android.MDMP.Tool.Utility;

import java.util.List;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private TextView tv_player_name;
    private TextView tv_player_singer;
    private TextView tv_player_time;
    private TextView tv_player_duration;
    private ImageButton ib_albumart;
    private ImageButton ib_play_pause;
    private ImageButton ib_player_next;
    private ImageButton ib_player_last;
    private SeekBar seekBar;
    private List<Song> songs = null;
    private int count = 0;
    private int current_position;
    private boolean play_status = false;

    public static final String POSITION = "com.lee.android.MDMP.POSITION";
    public static final String PROGRESS = "com.lee.android.MDMP.ADJUSTPROGRESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        // 动态注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.COMPLETE);
        filter.addAction(MusicService.PROGRESS);
        MyReceiver receiver = new MyReceiver();
        registerReceiver(receiver, filter);
        findView();
        setView();
        Intent intent = getIntent();
        play_status = intent.getBooleanExtra("play_status", false);
        if (!play_status) {
            init();
        } else {
            songs = (List<Song>) intent.getSerializableExtra("songlist");
            count = songs.size();
            current_position = intent.getIntExtra("position", 0);
            tv_player_name.setText(songs.get(current_position).getName());
            tv_player_singer.setText(songs.get(current_position).getSinger());
            String time=intent.getStringExtra("playtime");
            tv_player_time.setText(time);
            tv_player_duration.setText(songs.get(current_position).getTime());
            Bitmap bm = Utility.getAlbumArt(this, songs, current_position);
            Drawable drawable = new BitmapDrawable(getResources(), bm);
            ib_albumart.setBackground(drawable);
            ib_play_pause.setBackgroundResource(R.drawable.player_pause);
        }
    }

    private void findView() {
        tv_player_time=(TextView)findViewById(R.id.tv_player_time);
        tv_player_duration=(TextView)findViewById(R.id.tv_player_duration);
        tv_player_name=(TextView)findViewById(R.id.tv_player_name);
        tv_player_singer=(TextView)findViewById(R.id.tv_player_singer);
        ib_albumart = (ImageButton) findViewById(R.id.ib_albumart);
        ib_play_pause = (ImageButton) findViewById(R.id.ib_player_play);
        ib_player_next = (ImageButton) findViewById(R.id.ib_player_next);
        ib_player_last = (ImageButton) findViewById(R.id.ib_player_last);
        seekBar = (SeekBar) findViewById(R.id.sb_player);
    }

    private void setView() {
        tv_player_name.setSelected(true);
        tv_player_singer.setSelected(true);
        ib_play_pause.setOnClickListener(this);
        ib_player_next.setOnClickListener(this);
        ib_player_last.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
    }

    public void init() {
        Intent intent = getIntent();
        songs = (List<Song>) intent.getSerializableExtra("songlist");
        count = songs.size();
        current_position = intent.getIntExtra("position", 0);
        tv_player_time.setText("0:00");
        tv_player_duration.setText(songs.get(current_position).getTime());
        tv_player_name.setText(songs.get(current_position).getName());
        tv_player_singer.setText(songs.get(current_position).getSinger());
        Bitmap bm = Utility.getAlbumArt(this, songs, current_position);
        Drawable drawable = new BitmapDrawable(getResources(), bm);
        ib_albumart.setBackground(drawable);
        intent = new Intent(MusicService.INIT);
        intent.putExtra("PATH", ((Song) songs.get(current_position)).getPath());
        sendBroadcast(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ib_player_play:
                if (play_status) {
                    ib_play_pause.setBackgroundResource(R.drawable.player_play);  //播放-->暂停
                    intent = new Intent(MusicService.PAUSE);
                    sendBroadcast(intent);
                    play_status = false;
                } else {
                    ib_play_pause.setBackgroundResource(R.drawable.player_pause);   //暂停-->播放
                    intent = new Intent(MusicService.CONTINUE);
                    sendBroadcast(intent);
                    play_status = true;
                }
                Toast.makeText(this, play_status ? "播放" : "暂停", Toast.LENGTH_LONG).show();
                break;
            case R.id.ib_player_next:
                current_position = (current_position + 1) % count;
                intent = new Intent(PlayerActivity.POSITION);
                intent.putExtra("position", current_position);
                sendBroadcast(intent);
                intent = new Intent(MusicService.PLAY);
                intent.putExtra("PATH", ((Song) songs.get(current_position)).getPath());
                sendBroadcast(intent);
                tv_player_name.setText(songs.get(current_position).getName());
                tv_player_singer.setText(songs.get(current_position).getSinger());
                tv_player_time.setText("0:00");
                tv_player_duration.setText(songs.get(current_position).getTime());
                ib_albumart.setBackground(new BitmapDrawable(getResources(), Utility.getAlbumArt(this, songs, current_position)));
                ib_play_pause.setBackgroundResource(R.drawable.player_pause);
                play_status = true;
                break;
            case R.id.ib_player_last:
                if (current_position - 1 < 0) {
                    current_position = count - 1;
                } else {
                    current_position = (current_position - 1) % count;
                }
                intent = new Intent(PlayerActivity.POSITION);
                intent.putExtra("position", current_position);
                sendBroadcast(intent);
                intent = new Intent(MusicService.PLAY);
                intent.putExtra("PATH", ((Song) songs.get(current_position)).getPath());
                sendBroadcast(intent);
                tv_player_name.setText(songs.get(current_position).getName());
                tv_player_singer.setText(songs.get(current_position).getSinger());
                tv_player_time.setText("0:00");
                tv_player_duration.setText(songs.get(current_position).getTime());
                ib_albumart.setBackground(new BitmapDrawable(getResources(), Utility.getAlbumArt(this, songs, current_position)));
                ib_play_pause.setBackgroundResource(R.drawable.player_pause);
                play_status = true;
                break;
            case R.id.ib_albumart:
                break;
            default:
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        String time =Utility.formatTime((int)(songs.get(current_position).getDuration()*progress/100f));
        tv_player_time.setText(time);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int max = songs.get(current_position).getDuration();
        int time = (int) (seekBar.getProgress() * 0.01 * max);
        Intent intent = new Intent(PROGRESS);
        intent.putExtra("time", time);
        sendBroadcast(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MusicService.PROGRESS)) {
                int progress = intent.getIntExtra("progress", 0);
                String time=intent.getStringExtra("playtime");
                seekBar.setProgress(progress);
                tv_player_time.setText(time);
            } else if (intent.getAction().equals(MusicService.COMPLETE)) {
                ib_player_next.callOnClick();
                Toast.makeText(PlayerActivity.this, "下一首", Toast.LENGTH_LONG).show();
            }
        }
    }
}
