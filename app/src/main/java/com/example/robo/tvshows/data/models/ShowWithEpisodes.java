package com.example.robo.tvshows.data.models;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.example.robo.tvshows.data.models.Episode;
import com.example.robo.tvshows.data.models.TVShowDetails;

import java.util.List;


public class ShowWithEpisodes {

    @Embedded
    public TVShowDetails show;

    @Relation(parentColumn = "ID", entityColumn = "showID", entity = Episode.class)
    public List<Episode> episodes;

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public TVShowDetails getShow(){
        return show;
    }
}
