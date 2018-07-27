package com.lee.android.MDMP.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.lee.android.MDMP.Adapter.MyListAdapter;
import com.lee.android.MDMP.R;
import com.lee.android.MDMP.Model.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, TextView.OnEditorActionListener {
    private EditText ed_search;
    private ImageButton ib_back;
    private ImageButton ib_search;
    private ListView lv_search;
    private List<Song> songs;
    private int count = 0;
    private List<Integer> songindex;

    public static final String SONGINDEX = "com.lee.android.MDMP.SONGINDEX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        songs = (List<Song>) (intent.getSerializableExtra("songlist"));
        count = songs.size();
        findView();
        setView();
    }

    private void findView() {
        ed_search = (EditText) findViewById(R.id.ed_search);
        ib_back = (ImageButton) findViewById(R.id.ib_player_back);
        ib_search = (ImageButton) findViewById(R.id.ib_search);
        lv_search = (ListView) findViewById(R.id.lv_search_list);
    }

    private void setView() {
        ib_search.setOnClickListener(this);
        ib_back.setOnClickListener(this);
        lv_search.setOnItemClickListener(this);
        ed_search.setOnEditorActionListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_search:
                songindex = searchSong(ed_search.getText().toString().trim());
                List<Song> searchresult = new ArrayList<Song>();
                for (Integer i : songindex) {
                    int index = i.intValue();
                    searchresult.add(songs.get(index));
                }
                MyListAdapter adapter = new MyListAdapter(this, searchresult);
                lv_search.setAdapter(adapter);
                break;
            case R.id.ib_player_back:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(SONGINDEX);
        intent.putExtra("songindex", songindex.get(position).intValue());
        sendBroadcast(intent);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || event.getKeyCode() == KeyEvent.ACTION_DOWN) {   //监听回车
            ib_search.callOnClick();
            return true;
        }
        return false;
    }

    public List<Integer> searchSong(String name) {
        List<Integer> songindex = new ArrayList<Integer>();
        Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
        for (int i = 0; i < songs.size(); i++) {
            Matcher matcher = pattern.matcher(songs.get(i).getName());
            if (matcher.find()) {      //matches用于精准查找，find用于模糊查找
                songindex.add(new Integer(i));
            }
        }
        return songindex;
    }
}
