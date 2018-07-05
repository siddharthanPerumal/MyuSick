package com.siddharth.myusick.activity.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.siddharth.myusick.activity.MusicPlayerActivity;
import com.siddharth.myusick.activity.PlayList;
import com.siddharth.myusick.activity.fragment.AllSongs;

import java.io.IOException;


public class PlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer;
    private int resumePosition;
    public PlayList context;
    public MusicPlayerActivity musicPlayerActivity;

    public class LocalBinder extends Binder {
        public PlayerService getServiceContext() {
            return PlayerService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initMediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       /* if (intent.getExtras() != null) {
            String mediaFile = intent.getExtras().getString("media");
            setPlayerUri(mediaFile);

        }*/
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        //mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(PlayerService.this);
        //mediaPlayer.setOnBufferingUpdateListener(this);
        //mediaPlayer.setOnSeekCompleteListener(this);
        //mediaPlayer.setOnInfoListener(this);
        //Reset so that the MediaPlayer is not pointing to another data source


    }

    public void setPlayerUri(String songUri) {
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // Set the data source to the mediaFile location
            mediaPlayer.setDataSource(songUri);
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        mediaPlayer.prepareAsync();
    }

    public void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    public void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        playMedia();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(context !=null){
            context.getNextSongUri();
        }else if (musicPlayerActivity != null){
            musicPlayerActivity.getNextSongUri();

        }
    }
}
