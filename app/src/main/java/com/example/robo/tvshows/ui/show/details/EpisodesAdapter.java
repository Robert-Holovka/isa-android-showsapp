package com.example.robo.tvshows.ui.show.details;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.robo.tvshows.R;
import com.example.robo.tvshows.data.models.Episode;
import com.example.robo.tvshows.data.models.EpisodeDetails;
import com.example.robo.tvshows.ui.episode.details.EpisodeDetailsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.ViewHolder> {

    private List<Episode> episodes;
    private Context context;

    public EpisodesAdapter(List<Episode> episodes) {
        this.episodes = episodes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_view_episode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        TextView title = holder.itemView.findViewById(R.id.text_view_episode_title);
        LinearLayout episodeDetails = holder.itemView.findViewById(R.id.btn_episode_details);
        TextView seasonEpisode = holder.itemView.findViewById(R.id.text_view_episode_season);

        final Episode ep = episodes.get(position);

        title.setText(ep.getTitle());

        String episodeNumber = "Ep" + (isInteger(ep.getEpisodeNumber()) ? ep.getEpisodeNumber() : "0");
        String season = "S" + (isInteger(ep.getSeason()) ? ep.getSeason(): "0");

        seasonEpisode.setText(season  + " " + episodeNumber);

        episodeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent episodeDetailsIntent = EpisodeDetailsActivity.buildEpisodeDetailsActivity(context, ep.getID());
                context.startActivity(episodeDetailsIntent);
            }
        });
    }

    private boolean isInteger(String s) {

        if(s == null || s.equals("")){
            return false;
        }

        //Declare integer pattern
        String integerPattern = "[0-9]{1,3}";
        Pattern pattern = Pattern.compile(integerPattern, Pattern.CASE_INSENSITIVE);
        //Match entered String with valid integer pattern
        Matcher matcher = pattern.matcher(s);

        if(matcher.matches()){
            return true;
        }

        return false;
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void setEpisodeList(List<Episode> episodes) {
        this.episodes = episodes;
        notifyDataSetChanged();
    }

    public void addNewEpisode(Episode episode){
        episodes.add(episode);
        //Change episode list in Episodes adapter
        notifyItemInserted(getItemCount());
    }
}
