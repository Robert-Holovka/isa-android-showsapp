package com.example.robo.tvshows.data.networking;

import com.example.robo.tvshows.data.models.Comment;
import com.example.robo.tvshows.data.models.DataWrapper;
import com.example.robo.tvshows.data.models.Episode;
import com.example.robo.tvshows.data.models.EpisodeDetails;
import com.example.robo.tvshows.data.models.MediaResponse;
import com.example.robo.tvshows.data.models.NewComment;
import com.example.robo.tvshows.data.models.NewEpisode;
import com.example.robo.tvshows.data.models.TVShow;
import com.example.robo.tvshows.data.models.TVShowDetails;
import com.example.robo.tvshows.data.models.Token;
import com.example.robo.tvshows.data.models.User;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    @POST("/api/users/sessions")
    Call<DataWrapper<Token>> login(@Body User user);

    @POST("/api/users")
    Call<Void> register(@Body User user);

    @GET("/api/shows")
    Call<DataWrapper<List<TVShow>>> getShows();

    @GET("/api/shows/{showId}")
    Call<DataWrapper<TVShowDetails>> getShowDetails(@Path("showId") String showId);


    @GET("/api/shows/{showId}/episodes")
    Call<DataWrapper<List<Episode>>> getEpisodes(@Path("showId") String showId);

    @GET("/api/episodes/{episodeId}")
    Call<DataWrapper<EpisodeDetails>> getEpisodeDetails(@Path("episodeId") String episodeId);

    @POST("/api/episodes")
    Call<DataWrapper<EpisodeDetails>> postEpisode(@Body NewEpisode episode);

    @GET("/api/episodes/{episodeId}/comments")
    Call<DataWrapper<List<Comment>>> getComments(@Path("episodeId") String episodeId);

    @POST("/api/comments")
    Call<DataWrapper<Comment>> postComment(@Body NewComment comment);

    @POST("/api/shows/{showId}/like")
    Call<TVShowDetails> likeShow(@Path("showId") String showId);

    @POST("/api/shows/{showId}/dislike")
    Call<TVShowDetails> dislikeShow(@Path("showId") String showId);

    /**
     * Not the @Part annotation and its value.
     * The value important part of the API call and i needs to be in the correct format as seen below file"; filename="image.jpg"
     * You can change image.jpg with exact file name of the file you are trying to upload or keep it hardcoded
     */
    @POST("/api/media")
    @Multipart
    Call<DataWrapper<MediaResponse>> uploadMedia(@Part("file\"; filename=\"image.jpg\"") RequestBody request);
}
