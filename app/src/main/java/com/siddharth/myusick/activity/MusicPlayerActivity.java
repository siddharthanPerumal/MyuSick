package com.siddharth.myusick.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.siddharth.myusick.R;
import com.siddharth.myusick.activity.fragment.AllSongs;
import com.siddharth.myusick.activity.other.SongsDetails;
import com.siddharth.myusick.activity.service.PlayerService;

import java.util.ArrayList;

import static com.siddharth.myusick.activity.PlayList.MY_SHAREDPREFERENCE_NAME;


public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener{
    PlayerService serviceContext;
    String songUri = "";
    int currentSongPosition;
    SharedPreferences sharedPreferences;
    ImageView playAndStop;
    AnimationDrawable rocketAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        sharedPreferences = getSharedPreferences(MY_SHAREDPREFERENCE_NAME, MODE_PRIVATE);
        ImageView repeatSong = (ImageView) findViewById(R.id.repeatSong);
        ImageView skipToPrevious = (ImageView) findViewById(R.id.skipToPrevious);
        playAndStop = (ImageView) findViewById(R.id.playAndStop);
        ImageView skipToNext = (ImageView) findViewById(R.id.skipToNext);
        ImageView shuffled = (ImageView) findViewById(R.id.shuffled);
        ImageView favoriteSong = (ImageView) findViewById(R.id.favoriteSong);
        TextView songName = (TextView) findViewById(R.id.tvSongName);
        TextView albumName = (TextView) findViewById(R.id.albumName);
        TextView Signer = (TextView) findViewById(R.id.Signer);
        TextView songLyrics = (TextView) findViewById(R.id.songLyrics);

        favoriteSong.setImageResource(R.drawable.favorite_song_animation);
        rocketAnimation = (AnimationDrawable) favoriteSong.getDrawable();
        rocketAnimation.stop();
        rocketAnimation.selectDrawable(0);
        rocketAnimation.start();

        Intent intent = new Intent(MusicPlayerActivity.this, PlayerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);


        songName.setText(sharedPreferences.getString("CurrentSongName","SongName"));
        Signer.setText(sharedPreferences.getString("CurrentSongArtist","ArtiesName"));
        albumName.setText("<Unknow>");
        songLyrics.setText("<Unknow>");
        songUri = sharedPreferences.getString("CurrentSongUrl", "");
        boolean flag = sharedPreferences.getBoolean("CurrentSongPlayMode", false);
        if (flag) {
            playAndStop.setTag(1);
            playAndStop.setImageResource(R.drawable.ic_pause_black_32dp);
        } else {
            if (sharedPreferences.getBoolean("PlayResumeMode", false)) {
                playAndStop.setTag(2);
            } else {
                playAndStop.setTag(0);
            }
            playAndStop.setImageResource(R.drawable.ic_play_arrow_black_32dp);
        }


        repeatSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        skipToPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipPreviousSong();
            }
        });
        playAndStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int state = (int) playAndStop.getTag();
                switch (state) {
                    case 0:
                        playAndStop.setTag(1);
                        playAndStop.setImageResource(R.drawable.ic_pause_black_32dp);
                        if (serviceContext != null) {
                            serviceContext.setPlayerUri(songUri);
                        }
                        //AllSongs.adapter.setplayAndPauseImage(0);
                        sharedPreferences.edit().putBoolean("CurrentSongPlayMode", true).apply();
                        sharedPreferences.edit().putBoolean("PlayResumeMode", false).apply();
                        break;
                    case 1://on
                        sharedPreferences.edit().putBoolean("CurrentSongPlayMode", false).apply();
                        sharedPreferences.edit().putBoolean("PlayResumeMode", true).apply();
                        playAndStop.setTag(2);
                        playAndStop.setImageResource(R.drawable.ic_play_arrow_black_32dp);
                        serviceContext.pauseMedia();
                        //AllSongs.adapter.setplayAndPauseImage(1);
                        break;
                    case 2:
                        sharedPreferences.edit().putBoolean("CurrentSongPlayMode", true).apply();
                        sharedPreferences.edit().putBoolean("PlayResumeMode", false).apply();
                        playAndStop.setTag(1);
                        playAndStop.setImageResource(R.drawable.ic_pause_black_32dp);
                        if (serviceContext != null) {
                            serviceContext.resumeMedia();
                        }
                        //AllSongs.adapter.setplayAndPauseImage(0);
                        break;
                }
            }
        });
        skipToNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipNextSong();
            }
        });
        shuffled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        favoriteSong.setOnClickListener(this);
    }

    public void skipPreviousSong() {
        playAndStop.setTag(1);
        playAndStop.setImageResource(R.drawable.ic_pause_black_32dp);
        currentSongPosition = sharedPreferences.getInt("CurrentSongPosition", 0);
        if (currentSongPosition != 0) {
            currentSongPosition -= 1;
            SongsDetails songsDetails = AllSongs.arrayList.get(currentSongPosition);
            songUri = songsDetails.getAudioUrl();
            //songTitle.setText(arrayList.get(currentSongPosition).getSongName());
            //tvArties.setText(arrayList.get(currentSongPosition).getSingerName());
            sharedPreferences.edit().putString("CurrentSongName", songsDetails.getSongName()).apply();
            sharedPreferences.edit().putString("CurrentSongArtist", songsDetails.getSingerName()).apply();
            sharedPreferences.edit().putString("CurrentSongUrl", songsDetails.getAudioUrl()).apply();
            sharedPreferences.edit().putBoolean("CurrentSongPlayMode", true).apply();
            sharedPreferences.edit().putInt("CurrentSongPosition", currentSongPosition).apply();
            if (serviceContext != null) {
                serviceContext.setPlayerUri(songUri);
            }

        } else {
            //songTitle.setText(arrayList.get(currentSongPosition).getSongName());
            //tvArties.setText(arrayList.get(currentSongPosition).getSingerName());
            currentSongPosition = AllSongs.arrayList.size() - 1;
            SongsDetails songsDetails = AllSongs.arrayList.get(currentSongPosition);
            songUri = songsDetails.getAudioUrl();
            sharedPreferences.edit().putString("CurrentSongName", songsDetails.getSongName()).apply();
            sharedPreferences.edit().putString("CurrentSongArtist", songsDetails.getSingerName()).apply();
            sharedPreferences.edit().putString("CurrentSongUrl", songsDetails.getAudioUrl()).apply();
            sharedPreferences.edit().putBoolean("CurrentSongPlayMode", true).apply();
            sharedPreferences.edit().putInt("CurrentSongPosition", currentSongPosition).apply();
            if (serviceContext != null) {
                serviceContext.setPlayerUri(songUri);
            }
        }
    }

    public void skipNextSong() {
        playAndStop.setTag(1);
        playAndStop.setImageResource(R.drawable.ic_pause_black_32dp);
        currentSongPosition = sharedPreferences.getInt("CurrentSongPosition", 0);
        if (AllSongs.arrayList.size() - 1 >= currentSongPosition + 1) {
            currentSongPosition += 1;
            SongsDetails songsDetails = AllSongs.arrayList.get(currentSongPosition);
            songUri = songsDetails.getAudioUrl();
            //songTitle.setText(arrayList.get(currentSongPosition).getSongName());
            //tvArties.setText(arrayList.get(currentSongPosition).getSingerName());
            sharedPreferences.edit().putString("CurrentSongName", songsDetails.getSongName()).apply();
            sharedPreferences.edit().putString("CurrentSongArtist", songsDetails.getSingerName()).apply();
            sharedPreferences.edit().putString("CurrentSongUrl", songsDetails.getAudioUrl()).apply();
            sharedPreferences.edit().putBoolean("CurrentSongPlayMode", true).apply();
            sharedPreferences.edit().putInt("CurrentSongPosition", currentSongPosition).apply();
            if (serviceContext != null) {
                serviceContext.setPlayerUri(songUri);
            }

        } else {
            //songTitle.setText(arrayList.get(currentSongPosition).getSongName());
            //tvArties.setText(arrayList.get(currentSongPosition).getSingerName());
            currentSongPosition = 0;
            SongsDetails songsDetails = AllSongs.arrayList.get(currentSongPosition);
            songUri = songsDetails.getAudioUrl();
            sharedPreferences.edit().putString("CurrentSongName", songsDetails.getSongName()).apply();
            sharedPreferences.edit().putString("CurrentSongArtist", songsDetails.getSingerName()).apply();
            sharedPreferences.edit().putString("CurrentSongUrl", songsDetails.getAudioUrl()).apply();
            sharedPreferences.edit().putBoolean("CurrentSongPlayMode", true).apply();
            sharedPreferences.edit().putInt("CurrentSongPosition", currentSongPosition).apply();
            if (serviceContext != null) {
                serviceContext.setPlayerUri(songUri);
            }
        }

    }

    public void getNextSongUri() {
        currentSongPosition = sharedPreferences.getInt("CurrentSongPosition", 0);
        if (AllSongs.arrayList.size() - 1 >= currentSongPosition + 1) {
            currentSongPosition += 1;
            SongsDetails songsDetails = AllSongs.arrayList.get(currentSongPosition);
            songUri = songsDetails.getAudioUrl();
            //songTitle.setText(arrayList.get(currentSongPosition).getSongName());
            //tvArties.setText(arrayList.get(currentSongPosition).getSingerName());
            sharedPreferences.edit().putString("CurrentSongName", songsDetails.getSongName()).apply();
            sharedPreferences.edit().putString("CurrentSongArtist", songsDetails.getSingerName()).apply();
            sharedPreferences.edit().putString("CurrentSongUrl", songsDetails.getAudioUrl()).apply();
            sharedPreferences.edit().putBoolean("CurrentSongPlayMode", true).apply();
            sharedPreferences.edit().putInt("CurrentSongPosition", currentSongPosition).apply();

            playAndStop.setTag(1);
            playAndStop.setImageResource(R.drawable.ic_pause_black_32dp);
            if (serviceContext != null) {
                serviceContext.setPlayerUri(songUri);
            }

        } else {
            playAndStop.setTag(0);
            playAndStop.setImageResource(R.drawable.ic_play_arrow_black_32dp);
            //songTitle.setText(arrayList.get(currentSongPosition).getSongName());
            //tvArties.setText(arrayList.get(currentSongPosition).getSingerName());
            SongsDetails songsDetails = AllSongs.arrayList.get(currentSongPosition);
            sharedPreferences.edit().putString("CurrentSongName", songsDetails.getSongName()).apply();
            sharedPreferences.edit().putString("CurrentSongArtist", songsDetails.getSingerName()).apply();
            sharedPreferences.edit().putString("CurrentSongUrl", songsDetails.getAudioUrl()).apply();
            sharedPreferences.edit().putBoolean("CurrentSongPlayMode", true).apply();
            sharedPreferences.edit().putInt("CurrentSongPosition", currentSongPosition).apply();

            if (serviceContext != null) {
                serviceContext.stopMedia();
            }
        }
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PlayerService.LocalBinder localBinder = (PlayerService.LocalBinder) iBinder;
            serviceContext = localBinder.getServiceContext();
            serviceContext.musicPlayerActivity = MusicPlayerActivity.this;
            Log.e("MusicPlayer", "ServiceConnection " + "Service binded");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceContext.musicPlayerActivity = null;
            serviceContext = null;
            Log.e("MusicPlayer", "service unbinded");
        }
    };

    @Override
    public void onBackPressed() {
        if (serviceContext != null)
            unbindService(serviceConnection);
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.favoriteSong:
                //rocketAnimation.start();

                rocketAnimation.stop();
                rocketAnimation.selectDrawable(0);
                //rocketAnimation.start();*/
                break;
        }
    }
}
