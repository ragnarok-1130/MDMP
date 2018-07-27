package com.lee.android.MDMP.Tool;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.Toast;

import com.lee.android.MDMP.Model.Song;

import java.util.ArrayList;

public class Scanner {
    public static ArrayList<Song> getSongList(Context context) {
        ArrayList<Song> songlist = new ArrayList<Song>();
        try {
            Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.IS_MUSIC);
            if (cursor != null) {
                Song song;
                while (cursor.moveToNext()) {
                    song = new Song();
                    song.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                    song.setSinger(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                    song.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                    song.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                    song.setTime(Utility.formatTime(song.getDuration()));
                    song.setSong_size(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
                    song.setAlbum_id(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
                    songlist.add(song);
                }
                Toast.makeText(context,"count："+songlist.size(),Toast.LENGTH_LONG).show();
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return songlist;
    }
}
