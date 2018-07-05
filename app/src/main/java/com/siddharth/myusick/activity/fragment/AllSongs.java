package com.siddharth.myusick.activity.fragment;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.siddharth.myusick.R;
import com.siddharth.myusick.activity.adapter.SongListAdapter;
import com.siddharth.myusick.activity.other.SongsDetails;
import com.siddharth.myusick.activity.service.PlayerService;

import java.io.File;
import java.util.ArrayList;

public class AllSongs extends Fragment  {
    public static ArrayList<SongsDetails> arrayList;
    public static SongListAdapter adapter;
    RecyclerView recyclerView;

    public static AllSongs newInstance() {
        return new AllSongs();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_songs, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.allSongRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        arrayList = new ArrayList<>();
        adapter = new SongListAdapter(arrayList,getActivity());
        recyclerView.setAdapter(adapter);
        // Inflate the layout for this fragment
        if (getPlayList() != null) {
            adapter.notifyDataSetChanged();
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private ArrayList<SongsDetails> getPlayList() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, // Uri
                null,
                null,
                null,
                null
        );

        if (cursor == null) {

            Toast.makeText(getActivity(), "Something Went Wrong.", Toast.LENGTH_LONG).show();

        } else if (!cursor.moveToFirst()) {

            Toast.makeText(getActivity(), "No Music Found on SD Card.", Toast.LENGTH_LONG).show();

        } else {

            int Title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int album = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            do {
                String SongName = cursor.getString(Title);
                String artist1 = cursor.getString(artist);
                String sUri = cursor.getString(data);
                String album1 = cursor.getString(album);
                arrayList.add(new SongsDetails(SongName, artist1, sUri, "",album1));
            } while (cursor.moveToNext());
        }
        return arrayList;
    }

    public void updateRecyclerViewPlayAndPause(int position,boolean bFlag){
        View view = recyclerView.getLayoutManager().findViewByPosition(position);
        TextView  songName = (TextView) view.findViewById(R.id.tvSongName);
        TextView songArtist = (TextView) view.findViewById(R.id.tvSingerName);
        ImageView imageView = (ImageView) view.findViewById(R.id.ivPlayAndPause);
        adapter.setPlayAndPauseVisibility(songName,songArtist,imageView,bFlag);
    }
}
