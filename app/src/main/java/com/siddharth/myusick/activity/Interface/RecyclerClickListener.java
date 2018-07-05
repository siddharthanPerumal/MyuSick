package com.siddharth.myusick.activity.Interface;


import com.siddharth.myusick.activity.adapter.SongListAdapter;
import com.siddharth.myusick.activity.other.SongsDetails;

import java.util.ArrayList;

public interface RecyclerClickListener {
     void onClickEvent(String imageLink, String songTitle, String songArtiest, String songPath, int position, ArrayList<SongsDetails> arrayList);
     void updateMusicPlayer(boolean flag);
}
