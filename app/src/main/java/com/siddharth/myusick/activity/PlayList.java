package com.siddharth.myusick.activity;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.siddharth.myusick.R;
import com.siddharth.myusick.activity.Interface.RecyclerClickListener;
import com.siddharth.myusick.activity.fragment.AllSongs;
import com.siddharth.myusick.activity.fragment.ViewPagerFragment;
import com.siddharth.myusick.activity.other.SongsDetails;
import com.siddharth.myusick.activity.service.PlayerService;

import java.util.ArrayList;
import java.util.List;

public class PlayList extends AppCompatActivity implements View.OnClickListener, RecyclerClickListener {
    DrawerLayout drawer;
    NavigationView navigationView;
    TextView tbHeading;
    ImageView songImage;
    TextView songTitle, tvArtist;
    ImageButton btnPlayAndPause;
    PlayerService serviceContext;
    Intent intent;
    String songUri = "";
    int currentSongPosition;
   // ArrayList<SongsDetails> arrayList;
    //SongListAdapter adapter;
    public static final String MY_SHAREDPREFERENCE_NAME = "MYUSICK";
    SharedPreferences sharedPreferences;
    List list;
    ArrayList<Object> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        sharedPreferences = getSharedPreferences(MY_SHAREDPREFERENCE_NAME, MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView drawerIcon = (ImageView) toolbar.findViewById(R.id.draewerIcon);
        tbHeading = (TextView) toolbar.findViewById(R.id.tvTitle);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        ConstraintLayout playerView = (ConstraintLayout) findViewById(R.id.playerView);
        songImage = (ImageView) findViewById(R.id.songImage);
        songTitle = (TextView) findViewById(R.id.songTitle);
        tvArtist = (TextView) findViewById(R.id.tvArtist);

        btnPlayAndPause = (ImageButton) findViewById(R.id.btnPlayAndPause);

        intent = new Intent(this, PlayerService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);


        drawerIcon.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawer.closeDrawers();
                return true;
            }
        });

        songTitle.setText(sharedPreferences.getString("CurrentSongName", "SongName"));
        tvArtist.setText(sharedPreferences.getString("CurrentSongArtist", "ArtiesName"));
        songUri = sharedPreferences.getString("CurrentSongUrl", "");
        sharedPreferences.edit().putBoolean("CurrentSongPlayMode", false).apply();
        songTitle.setSelected(true);
        tvArtist.setSelected(true);

        btnPlayAndPause.setTag(0);
        btnPlayAndPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int state = (int) btnPlayAndPause.getTag();
                switch (state) {
                    case 0:
                        btnPlayAndPause.setTag(1);
                        btnPlayAndPause.setImageResource(R.drawable.ic_pause_no_background);
                        if (serviceContext != null) {
                            serviceContext.setPlayerUri(songUri);
                        }
                        AllSongs.adapter.setplayAndPauseImage(0);
                        sharedPreferences.edit().putBoolean("CurrentSongPlayMode", true).apply();
                        sharedPreferences.edit().putBoolean("PlayResumeMode", false).apply();
                        break;
                    case 1://on
                        sharedPreferences.edit().putBoolean("CurrentSongPlayMode", false).apply();
                        sharedPreferences.edit().putBoolean("PlayResumeMode", true).apply();
                        btnPlayAndPause.setTag(2);
                        btnPlayAndPause.setImageResource(R.drawable.ic_play_no_background);
                        serviceContext.pauseMedia();
                        AllSongs.adapter.setplayAndPauseImage(1);
                        break;
                    case 2:
                        sharedPreferences.edit().putBoolean("PlayResumeMode", false).apply();
                        sharedPreferences.edit().putBoolean("CurrentSongPlayMode", true).apply();
                        btnPlayAndPause.setTag(1);
                        btnPlayAndPause.setImageResource(R.drawable.ic_pause_no_background);
                        if (serviceContext != null) {
                            serviceContext.resumeMedia();
                        }
                        AllSongs.adapter.setplayAndPauseImage(0);
                        break;
                }

            }
        });

        playerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View view1 = songImage;
                final Intent intent = new Intent(PlayList.this, MusicPlayerActivity.class);
                int flag = (int) btnPlayAndPause.getTag();
                intent.putExtra("PlayerMode", flag);
                ActivityOptions options = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    options = ActivityOptions.makeSceneTransitionAnimation(PlayList.this, view1, "SongImage");
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent, options.toBundle());
                }
            }
        });

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, ViewPagerFragment.newInstance());
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.draewerIcon:
                drawer.openDrawer(GravityCompat.START);
                break;
        }
    }

    @Override
    public void onClickEvent(String imageLink, String songTitle1, String songArtiest, String songPath, int position, ArrayList<SongsDetails> arrayList) {
        songTitle.setText(songTitle1);
        tvArtist.setText(songArtiest);
        songUri = songPath;
        btnPlayAndPause.setTag(1);
        btnPlayAndPause.setImageResource(R.drawable.ic_pause_no_background);
        if (serviceContext != null) {
            serviceContext.setPlayerUri(songUri);
        }
        //adapter.setplayAndPauseImage(0);
        //currentSongPosition = position;
        //this.arrayList = arrayList;
        sharedPreferences.edit().putInt("CurrentSongPosition", position).apply();
    }

    @Override
    public void updateMusicPlayer(boolean flag) {
        if (flag) {
            btnPlayAndPause.setTag(1);
            btnPlayAndPause.setImageResource(R.drawable.ic_pause_no_background);
            if (serviceContext != null) {
                serviceContext.resumeMedia();
            }
        } else {
            btnPlayAndPause.setTag(2);
            btnPlayAndPause.setImageResource(R.drawable.ic_play_no_background);
            serviceContext.pauseMedia();
        }
    }

    public void getNextSongUri() {
        currentSongPosition = sharedPreferences.getInt("CurrentSongPosition", 0);
        AllSongs allSongs = (AllSongs) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + 0);
        if (AllSongs.arrayList.size() - 1 >= currentSongPosition + 1) {
            currentSongPosition += 1;
            SongsDetails songsDetails = AllSongs.arrayList.get(currentSongPosition);
            songUri = songsDetails.getAudioUrl();
            songTitle.setText(songsDetails.getSongName());
            tvArtist.setText(songsDetails.getSingerName());
            btnPlayAndPause.setTag(1);
            btnPlayAndPause.setImageResource(R.drawable.ic_pause_no_background);
            allSongs.updateRecyclerViewPlayAndPause(currentSongPosition, true);
            sharedPreferences.edit().putString("CurrentSongName", songsDetails.getSongName()).apply();
            sharedPreferences.edit().putString("CurrentSongArtist", songsDetails.getSingerName()).apply();
            sharedPreferences.edit().putString("CurrentSongUrl", songUri).apply();
            sharedPreferences.edit().putBoolean("CurrentSongPlayMode", true).apply();
            sharedPreferences.edit().putInt("CurrentSongPosition", currentSongPosition).apply();

            if (serviceContext != null) {
                serviceContext.setPlayerUri(songUri);
            }

        } else {
            SongsDetails songsDetails = AllSongs.arrayList.get(currentSongPosition);
            btnPlayAndPause.setTag(0);
            btnPlayAndPause.setImageResource(R.drawable.ic_play_no_background);
            songTitle.setText(songsDetails.getSongName());
            tvArtist.setText(songsDetails.getSingerName());
            allSongs.updateRecyclerViewPlayAndPause(currentSongPosition, false);
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
            serviceContext.context = PlayList.this;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceContext.context = null;
            serviceContext = null;
            Log.e("PlayList", "service unbinded");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (serviceContext == null) {
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }

        if (sharedPreferences.getBoolean("CurrentSongPlayMode", false)) {
            btnPlayAndPause.setTag(1);
            btnPlayAndPause.setImageResource(R.drawable.ic_pause_no_background);

        } else {
            if (sharedPreferences.getBoolean("PlayResumeMode", false)) {
                btnPlayAndPause.setTag(2);
            } else {
                btnPlayAndPause.setTag(0);
            }
            btnPlayAndPause.setImageResource(R.drawable.ic_play_no_background);
        }
        songTitle.setText(sharedPreferences.getString("CurrentSongName","SongTitle"));
        tvArtist.setText(sharedPreferences.getString("CurrentSongArtist","Artist"));
        if(AllSongs.adapter != null)
            AllSongs.adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        if (serviceContext != null) {
            unbindService(serviceConnection);
            stopService(intent);
        }
        super.onDestroy();
    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
