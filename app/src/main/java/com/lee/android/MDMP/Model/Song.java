package com.lee.android.MDMP.Model;

import java.io.Serializable;

public class Song implements Serializable{
    private String singer;
    private String name;
    private String path;
    private String time;
    private int duration;
    private long song_size;
    private long album_id;

    private static final long serialVersionUID = 1L;

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getSong_size() {
        return song_size;
    }

    public void setSong_size(long song_size) {
        this.song_size = song_size;
    }

    public long getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(long album_id) {
        this.album_id = album_id;
    }
}