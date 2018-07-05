package com.siddharth.myusick.activity.other;


public class SongsDetails {
    private String songName,singerName,audioUrl,imageLink,album;
    public SongsDetails(String songName,String singerName,String audioUrl,String imageLink,String album){
        this.songName = songName;
        this.singerName = singerName;
        this.audioUrl = audioUrl;
        this.imageLink = imageLink;
        this.album = album;
    }

    public String getSongName() {
        return songName;
    }

    public String getSingerName() {
        return singerName;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getAlbum() {
        return album;
    }
}
