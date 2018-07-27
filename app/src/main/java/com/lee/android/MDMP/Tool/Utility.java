package com.lee.android.MDMP.Tool;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.lee.android.MDMP.Model.Song;
import com.lee.android.MDMP.R;

import java.util.List;

public class Utility {
    public static Bitmap getAlbumArt(Context context, List<Song> songs, int position) {
        String uri_str = "content://media/external/audio/albums/";
        Uri uri = Uri.parse(uri_str + songs.get(position).getAlbum_id());
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"album_art"}, null, null, null);
        String album_art = null;
        if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
            cursor.moveToNext();
            album_art = cursor.getString(0);
        }
        cursor.close();
        Bitmap bm = null;
        if (album_art != null) {
            bm = BitmapFactory.decodeFile(album_art);
        } else {
            bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.disc);
        }
        return bm;
    }

    public static String formatTime(int time) {    //将时间格式化为mm:ss格式
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;

        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }

    }

    public static Boolean isEmailAddress(String email) {
        String REGEX_EMAIL = "^[a-zA-Z0-9][\\w\\._-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        return email.matches(REGEX_EMAIL);
    }

    public static Bitmap getPictuBitmap(SQLiteDatabase db, int id){
        Bitmap bitmap=null;
        Cursor cursor=db.query("users",new String[]{"user_image"},"id="+id,null,null,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                byte[] bytes=cursor.getBlob(cursor.getColumnIndex("user_image"));
                bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            }
        }
        return bitmap;
    }
}
