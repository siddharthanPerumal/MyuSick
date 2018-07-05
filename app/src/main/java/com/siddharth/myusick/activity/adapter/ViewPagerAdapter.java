package com.siddharth.myusick.activity.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.siddharth.myusick.activity.fragment.Albums;
import com.siddharth.myusick.activity.fragment.AllSongs;
import com.siddharth.myusick.activity.fragment.PlayList;


public class ViewPagerAdapter extends FragmentPagerAdapter {
    String[] title = {"All Songs", "Playlist", "Albums"};
    private static int NUM_ITEMS = 3;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return AllSongs.newInstance();
            case 1:
                return PlayList.newInstance();
            case 2:
                return Albums.newInstance();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    @Override
    public int getCount() {
        return title.length;
    }
}
