package com.siddharth.myusick.activity.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.siddharth.myusick.R;
import com.siddharth.myusick.activity.Interface.RecyclerClickListener;
import com.siddharth.myusick.activity.PlayList;
import com.siddharth.myusick.activity.other.SongsDetails;

import java.util.ArrayList;


public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.MyViewHolder> implements Filterable {
    private ArrayList<SongsDetails> arrayList, filteredList;
    private RecyclerClickListener clickListener;
    private Context context;
    private View palyAndpauseView = null;
    private View songNameView = null;
    private View songArtistView = null;
    private int iPosition = 0;
    private CustomSearchFilter customSearchFilter;
    SharedPreferences sharedPreferences;

    public SongListAdapter(ArrayList<SongsDetails> arrayList, Context context) {
        this.arrayList = arrayList;
        this.filteredList = arrayList;
        clickListener = (RecyclerClickListener) context;
        sharedPreferences = context.getSharedPreferences(PlayList.MY_SHAREDPREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_song_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final SongsDetails songsDetails = arrayList.get(position);
        holder.tvSongName.setText(songsDetails.getSongName());
        holder.tvSongName.setTextColor(context.getResources().getColor(R.color.default_text_Color));
        holder.tvSingerName.setText(songsDetails.getSingerName());
        holder.tvSingerName.setTextColor(context.getResources().getColor(R.color.default_text_Color));
        holder.playAndPause.setTag(0);
        holder.playAndPause.setImageResource(R.drawable.ic_pause_1);
        holder.playAndPause.setVisibility(View.GONE);
       /* if (position == 0) {
            palyAndpauseView = holder.playAndPause;
        }*/
        String currentSongName = sharedPreferences.getString("CurrentSongName", "");
        int currentPosition = sharedPreferences.getInt("CurrentSongPosition", 0);
        if (currentSongName.equals(songsDetails.getSongName()) && currentPosition == position) {
            palyAndpauseView = holder.playAndPause;
            songNameView = holder.tvSongName;
            songArtistView = holder.tvSingerName;
            holder.tvSongName.setTextColor(context.getResources().getColor(R.color.toolbar));
            holder.tvSingerName.setTextColor(context.getResources().getColor(R.color.toolbar));
            if (sharedPreferences.getBoolean("CurrentSongPlayMode", false)) {
                holder.playAndPause.setImageResource(R.drawable.ic_pause_1);
                holder.playAndPause.setTag(1);
                holder.playAndPause.setVisibility(View.VISIBLE);
            } else {
                holder.playAndPause.setImageResource(R.drawable.ic_play_1);
                holder.playAndPause.setTag(0);
                holder.playAndPause.setVisibility(View.VISIBLE);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* if (palyAndpauseView == null) {
                    holder.playAndPause.setVisibility(View.VISIBLE);
                    holder.playAndPause.setTag(1);
                    palyAndpauseView = holder.playAndPause;
                } else {
                    palyAndpauseView.setVisibility(View.GONE);
                    ((ImageView)palyAndpauseView).setImageResource(R.drawable.ic_pause_1);
                    holder.playAndPause.setVisibility(View.VISIBLE);
                    holder.playAndPause.setTag(1);
                    palyAndpauseView = holder.playAndPause;
                }*/
                if (palyAndpauseView != null) {
                    palyAndpauseView.setVisibility(View.GONE);
                    palyAndpauseView.setTag(0);
                    ((TextView) songNameView).setTextColor(context.getResources().getColor(R.color.default_text_Color));
                    ((TextView) songArtistView).setTextColor(context.getResources().getColor(R.color.default_text_Color));
                    ((ImageView) palyAndpauseView).setImageResource(R.drawable.ic_pause_1);
                    palyAndpauseView = holder.playAndPause;
                    songNameView = holder.tvSongName;
                    songArtistView = holder.tvSingerName;
                    ((TextView) songNameView).setTextColor(context.getResources().getColor(R.color.toolbar));
                    ((TextView) songArtistView).setTextColor(context.getResources().getColor(R.color.toolbar));
                    holder.playAndPause.setVisibility(View.VISIBLE);
                    holder.playAndPause.setTag(1);
                    iPosition = holder.getAdapterPosition();
                    sharedPreferences.edit().putString("CurrentSongName", songsDetails.getSongName()).apply();
                    sharedPreferences.edit().putString("CurrentSongArtist", songsDetails.getSingerName()).apply();
                    sharedPreferences.edit().putString("CurrentSongUrl", songsDetails.getAudioUrl()).apply();
                    sharedPreferences.edit().putBoolean("CurrentSongPlayMode", true).apply();
                    sharedPreferences.edit().putInt("CurrentSongPosition", holder.getAdapterPosition()).apply();
                } else {
                    holder.playAndPause.setVisibility(View.VISIBLE);
                    holder.playAndPause.setTag(1);
                     palyAndpauseView = holder.playAndPause;
                    songNameView = holder.tvSongName;
                    songArtistView = holder.tvSingerName;
                    ((TextView) songNameView).setTextColor(context.getResources().getColor(R.color.toolbar));
                    ((TextView) songArtistView).setTextColor(context.getResources().getColor(R.color.toolbar));
                    iPosition = holder.getAdapterPosition();
                    sharedPreferences.edit().putString("CurrentSongName", songsDetails.getSongName()).apply();
                    sharedPreferences.edit().putString("CurrentSongArtist", songsDetails.getSingerName()).apply();
                    sharedPreferences.edit().putString("CurrentSongUrl", songsDetails.getAudioUrl()).apply();
                    sharedPreferences.edit().putBoolean("CurrentSongPlayMode", true).apply();
                    sharedPreferences.edit().putInt("CurrentSongPosition", holder.getAdapterPosition()).apply();

                }
                clickListener.onClickEvent(songsDetails.getImageLink(), songsDetails.getSongName(), songsDetails.getSingerName(), songsDetails.getAudioUrl(), iPosition, arrayList);
            }
        });
        holder.playAndPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.playAndPause.getVisibility() == View.VISIBLE) {
                    int flag = (int) holder.playAndPause.getTag();
                    if (flag == 1) {
                        holder.playAndPause.setTag(0);
                        clickListener.updateMusicPlayer(false);
                        holder.playAndPause.setImageResource(R.drawable.ic_play_1);
                        sharedPreferences.edit().putBoolean("CurrentSongPlayMode", false).apply();
                    } else {
                        holder.playAndPause.setTag(1);
                        clickListener.updateMusicPlayer(true);
                        holder.playAndPause.setImageResource(R.drawable.ic_pause_1);
                        sharedPreferences.edit().putBoolean("CurrentSongPlayMode", true).apply();
                    }
                }
            }
        });
    }

    public void setplayAndPauseImage(int viewState) {
        if (palyAndpauseView != null) {
            palyAndpauseView.setVisibility(View.VISIBLE);
            if (viewState == 1) {
                sharedPreferences.edit().putBoolean("CurrentSongPlayMode", false).apply();
                ((ImageView) palyAndpauseView).setImageResource(R.drawable.ic_play_1);
            } else {
                sharedPreferences.edit().putBoolean("CurrentSongPlayMode", true).apply();
                ((ImageView) palyAndpauseView).setImageResource(R.drawable.ic_pause_1);
            }
        }
    }

    public void setPlayAndPauseVisibility(View songView, View songArtist, View view, boolean bFlag) {
        ((TextView) songNameView).setTextColor(context.getResources().getColor(R.color.default_text_Color));
        ((TextView) songArtistView).setTextColor(context.getResources().getColor(R.color.default_text_Color));
        songNameView = songView;
        songArtistView = songArtist;
        ((TextView) songNameView).setTextColor(context.getResources().getColor(R.color.toolbar));
        ((TextView) songArtistView).setTextColor(context.getResources().getColor(R.color.toolbar));
        if (bFlag) {
            palyAndpauseView.setVisibility(View.GONE);
            ((ImageView) view).setImageResource(R.drawable.ic_pause_1);
            view.setVisibility(View.VISIBLE);
            palyAndpauseView = view;
            sharedPreferences.edit().putBoolean("CurrentSongPlayMode", true).apply();
        } else {
            //palyAndpauseView.setVisibility(View.GONE);
            ((ImageView) view).setImageResource(R.drawable.ic_play_1);
            view.setVisibility(View.VISIBLE);
            palyAndpauseView = view;
            sharedPreferences.edit().putBoolean("CurrentSongPlayMode", false).apply();
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (customSearchFilter == null) {
            customSearchFilter = new CustomSearchFilter();
        }
        return customSearchFilter;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvSongName, tvSingerName;
        ImageView playAndPause;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvSongName = (TextView) itemView.findViewById(R.id.tvSongName);
            tvSingerName = (TextView) itemView.findViewById(R.id.tvSingerName);
            playAndPause = (ImageView) itemView.findViewById(R.id.ivPlayAndPause);

        }
    }

    class CustomSearchFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();
            if (charSequence != null && charSequence.length() > 0) {
                ArrayList<SongsDetails> tempList = new ArrayList<>();
                // search content in friend list
                for (SongsDetails user : filteredList) {
                    if (user.getSongName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        tempList.add(user);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = filteredList.size();
                filterResults.values = filteredList;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            arrayList = (ArrayList<SongsDetails>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
