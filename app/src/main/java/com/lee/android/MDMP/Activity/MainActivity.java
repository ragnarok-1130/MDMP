package com.lee.android.MDMP.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lee.android.MDMP.Fragment.SettingFragment;
import com.lee.android.MDMP.Service.MusicService;
import com.lee.android.MDMP.Adapter.MyListAdapter;
import com.lee.android.MDMP.R;
import com.lee.android.MDMP.Tool.MyDBHelper;
import com.lee.android.MDMP.Tool.Scanner;
import com.lee.android.MDMP.Model.Song;
import com.lee.android.MDMP.Tool.Utility;

import java.io.Serializable;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private Toolbar toolbar;
    private DrawerLayout drawerlayout;
    private NavigationView nav_view;
    private ListView songlist;
    private CircleImageView album_image;
    private CircleImageView user_image;
    private TextView tv_username;
    private TextView tv_account;
    private RelativeLayout headerlayout;
    private TextView tv_songname;
    private TextView tv_singer;
    private ProgressBar progressBar;
    private ImageButton ib_play_pause;
    private ImageButton ib_nextsong;
    private ImageButton ib_lastsong;
    private RelativeLayout player_layout;
    private ComponentName musicService;
    private boolean play_status = false;
    private int current_position = 0;
    private int count = 0;
    private List<Song> songs;
    private MyDBHelper dbHelper = new MyDBHelper(this, "Userinfo.db", null, 1);
    private SQLiteDatabase db;
    private boolean login_status = false;
    private int id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 动态注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.PLAY);
        filter.addAction(MusicService.PAUSE);
        filter.addAction(MusicService.CONTINUE);
        filter.addAction(MusicService.COMPLETE);
        filter.addAction(MusicService.PROGRESS);
        filter.addAction(PlayerActivity.POSITION);
        filter.addAction(SearchActivity.SONGINDEX);
        filter.addAction(LoginActivity.LOGIN_SUCCESS);
        filter.addAction(PersonalInfoActivity.UPDATE);
        filter.addAction(SettingFragment.LOGOUT);
        MyReceiver receiver = new MyReceiver();
        registerReceiver(receiver, filter);
        musicService = new ComponentName(this, MusicService.class);
        findView();
        setView();
        db = dbHelper.getReadableDatabase();
    }

    private void findView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        nav_view = (NavigationView) findViewById(R.id.nav_view);
        songlist = (ListView) findViewById(R.id.songlist);
        album_image = (CircleImageView) findViewById(R.id.album_image);
        tv_songname = (TextView) findViewById(R.id.tv_song);
        tv_singer = (TextView) findViewById(R.id.tv_singer);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ib_play_pause = (ImageButton) findViewById(R.id.ib_paly_pause);
        ib_lastsong = (ImageButton) findViewById(R.id.ib_lastsong);
        ib_nextsong = (ImageButton) findViewById(R.id.ib_nextsong);
        player_layout = (RelativeLayout) findViewById(R.id.player_layout);
        headerlayout = (RelativeLayout) nav_view.getHeaderView(0);       //header布局一般为0
        user_image = (CircleImageView) headerlayout.findViewById(R.id.user_image);
        tv_username = (TextView) headerlayout.findViewById(R.id.tv_username);
        tv_account = (TextView) headerlayout.findViewById(R.id.tv_account);
    }

    private void setView() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);    // 设置左上角的按钮可以点击
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }
        nav_view.setNavigationItemSelectedListener(this);
        ib_play_pause.setOnClickListener(this);
        ib_lastsong.setOnClickListener(this);
        ib_nextsong.setOnClickListener(this);
        album_image.setOnClickListener(this);
        player_layout.setOnClickListener(this);
        user_image.setOnClickListener(this);
        initList();
        initplayer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toobar, menu);    //实例化Menu目录下的Menu布局文件
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Intent intent = new Intent(this, SearchActivity.class);
                intent.putExtra("songlist", (Serializable) songs);
                startActivity(intent);
                break;
            case android.R.id.home:
                drawerlayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

    private void initList() {
        songs = Scanner.getSongList(this);
        count = songs.size();
        final MyListAdapter adapter = new MyListAdapter(this, songs);
        songlist.setAdapter(adapter);
        songlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                current_position = position;
                tv_songname.setText(((Song) songs.get(position)).getName());
                tv_singer.setText(((Song) songs.get(position)).getSinger());
                album_image.setImageBitmap(Utility.getAlbumArt(MainActivity.this, songs, position));
                Intent intent = new Intent(MusicService.PLAY);
                intent.setComponent(musicService);
                intent.putExtra("PATH", ((Song) songs.get(position)).getPath());
                startService(intent);
                ib_play_pause.setBackgroundResource(R.drawable.pause);
                play_status = true;
            }
        });
    }

    private void initplayer() {
        tv_songname.setText(((Song) songs.get(current_position)).getName());    //初始化播放器
        tv_songname.setSelected(true);
        tv_singer.setText(((Song) songs.get(current_position)).getSinger());
        tv_singer.setSelected(true);
        album_image.setImageBitmap(Utility.getAlbumArt(this, songs, current_position));
        Intent intent = new Intent(MusicService.INIT);
        intent.setComponent(musicService);
        intent.putExtra("PATH", ((Song) songs.get(current_position)).getPath());
        startService(intent);
    }

    public String getUsername(int id) {
        String sql = "select username from users where id = " + id;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex("username"));
        } else
            return "点击登录";
    }

    public String getEmail(int id) {
        String sql = "select email from users where id = " + id;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex("email"));
        } else
            return "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_paly_pause:
                if (play_status) {
                    ib_play_pause.setBackgroundResource(R.drawable.play);  //播放-->暂停
                    Intent intent = new Intent(MusicService.PAUSE);
                    intent.setComponent(musicService);
                    startService(intent);
                    play_status = false;
                } else {
                    ib_play_pause.setBackgroundResource(R.drawable.pause);   //暂停-->播放
                    Intent intent = new Intent(MusicService.CONTINUE);
                    intent.setComponent(musicService);
                    startService(intent);
                    play_status = true;
                }
                Toast.makeText(this, play_status ? "播放" : "暂停", Toast.LENGTH_LONG).show();
                break;
            case R.id.ib_nextsong:
                current_position = (current_position + 1) % count;
                songlist.performItemClick(null, current_position, 0);
                break;
            case R.id.ib_lastsong:
                if (current_position - 1 < 0) {
                    current_position = count - 1;
                } else {
                    current_position = (current_position - 1) % count;
                }
                songlist.performItemClick(null, current_position, 0);
                break;
            case R.id.album_image:
            case R.id.player_layout:
                Intent intent = new Intent(this, PlayerActivity.class);
                intent.putExtra("play_status", play_status);
                intent.putExtra("songlist", (Serializable) songs);
                intent.putExtra("position", current_position);
                startActivity(intent);
                break;
            case R.id.user_image:
                if (!login_status) {
                    intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(this, PersonalInfoActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.nav_setting:
                intent = new Intent(this, SettingActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("login_status",login_status);
                startActivity(intent);
                break;
            case R.id.nav_exit:
                System.exit(0);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {    //防止activity被destroy
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MusicService.PROGRESS)) {
                int progress = intent.getIntExtra("progress", 0);
                progressBar.setProgress(progress);
            } else if (intent.getAction().equals(MusicService.COMPLETE)) {
                ib_nextsong.callOnClick();
                Toast.makeText(MainActivity.this, "下一首", Toast.LENGTH_LONG).show();
            } else if (intent.getAction().equals(PlayerActivity.POSITION)) {
                songlist.setItemChecked(current_position, false);
                current_position = intent.getIntExtra("position", 0);
                tv_songname.setText(((Song) songs.get(current_position)).getName());    //初始化播放器
                tv_songname.setSelected(true);
                tv_singer.setText(((Song) songs.get(current_position)).getSinger());
                tv_singer.setSelected(true);
                songlist.setItemChecked(current_position, true);
                album_image.setImageBitmap(Utility.getAlbumArt(MainActivity.this, songs, current_position));
            } else if (intent.getAction().equals(MusicService.CONTINUE)) {
                play_status = true;
                ib_play_pause.setBackgroundResource(R.drawable.pause);
            } else if (intent.getAction().equals(MusicService.PAUSE)) {
                play_status = false;
                ib_play_pause.setBackgroundResource(R.drawable.play);
            } else if (intent.getAction().equals(MusicService.PLAY)) {
                play_status = true;
                ib_play_pause.setBackgroundResource(R.drawable.pause);
            } else if (intent.getAction().equals(SearchActivity.SONGINDEX)) {
                current_position = intent.getIntExtra("songindex", current_position);
                songlist.performItemClick(null, current_position, 0);
            } else if (intent.getAction().equals(LoginActivity.LOGIN_SUCCESS)) {
                id = intent.getIntExtra("id", -1);
                tv_username.setText(getUsername(id));
                tv_account.setText(getEmail(id));
                login_status = true;
                try {
                    Bitmap bitmap = Utility.getPictuBitmap(db, id);
                    if (bitmap != null)
                        user_image.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (intent.getAction().equals(PersonalInfoActivity.UPDATE)) {
                try {
                    Bitmap bitmap = Utility.getPictuBitmap(db, id);
                    if (bitmap != null)
                        user_image.setImageBitmap(bitmap);
                    tv_username.setText(getUsername(id));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(intent.getAction().equals(SettingFragment.LOGOUT)){
                user_image.setImageResource(R.drawable.head_image);
                tv_username.setText("点击登录");
                tv_account.setText("");
                login_status=false;
                id=-1;
            }
        }
    }
}
