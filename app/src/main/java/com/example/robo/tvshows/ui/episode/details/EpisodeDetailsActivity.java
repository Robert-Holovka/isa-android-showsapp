package com.example.robo.tvshows.ui.episode.details;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.robo.tvshows.R;
import com.example.robo.tvshows.data.DatabaseCallback;
import com.example.robo.tvshows.data.models.DataWrapper;
import com.example.robo.tvshows.data.models.EpisodeDetails;
import com.example.robo.tvshows.data.networking.APIClient;
import com.example.robo.tvshows.data.networking.ApiService;
import com.example.robo.tvshows.data.repositories.EpisodeRepositoryImpl;
import com.example.robo.tvshows.ui.BaseActivity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EpisodeDetailsActivity extends BaseActivity {

    //Constants
    private static final String EPISODE_ID = "EPISODE_ID";

    //Views
    private ImageView image;
    private TextView title;
    private TextView seasonEpisode;
    private TextView description;
    private Toolbar toolbar;
    private SwipeRefreshLayout refreshEpisodeDetails;
    private TextView comments;
    private RelativeLayout episodeInfo;

    //Other
    private String episodeId;
    private Context context;
    private ApiService apiService;
    private EpisodeRepositoryImpl episodeRepository;


    public static Intent buildEpisodeDetailsActivity(Context context, String episodeId){
        Intent episodeIntent = new Intent(context, EpisodeDetailsActivity.class);
        episodeIntent.putExtra(EPISODE_ID, episodeId);
        return episodeIntent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_details);

        //Get Episode ID from calling activity
        episodeId = getIntent().getStringExtra(EPISODE_ID);

        context = this;
        apiService = APIClient.getApiService();
        episodeRepository = new EpisodeRepositoryImpl(this);

        initViews();
        initSwipeToRefresh();
        initShowCommentsListener();

        //If internet is available fetch data from API, otherwise from database
        if(APIClient.isInternetAvailable(this)){
            fetchInternetEpisodeDetails(false);
        } else {
            fetchDatabaseData();
        }
    }

    private void initShowCommentsListener() {
        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commentsIntent = EpisodeCommentsActivity.buildCommentActivity(context, episodeId);
                startActivity(commentsIntent);
            }
        });
    }

    private void fetchInternetEpisodeDetails(boolean isScreenRefreshed) {
        //if screen is refreshed there is no need for progress dialog
        if(!isScreenRefreshed){
            showProgress();
        }

        apiService.getEpisodeDetails(episodeId).enqueue(new Callback<DataWrapper<EpisodeDetails>>() {
            @Override
            public void onResponse(Call<DataWrapper<EpisodeDetails>> call, Response<DataWrapper<EpisodeDetails>> response) {
                if(response.isSuccessful()){

                    EpisodeDetails ep = response.body().getData();
                    displayEpisodeDetails(ep);
                    insertDatabaseData(ep);

                } else {
                    //try get data from database
                    Toast.makeText(getApplicationContext(), R.string.internet_error_fetching_from_database, Toast.LENGTH_SHORT).show();
                    fetchDatabaseData();
                }
                hideProgress();
            }

            @Override
            public void onFailure(Call<DataWrapper<EpisodeDetails>> call, Throwable t) {
                //If user didn't cancel call, fetch data from try fetch data from database
                if(!call.isCanceled()){
                    //try get data from database
                    Toast.makeText(getApplicationContext(), R.string.internet_error_fetching_from_database, Toast.LENGTH_SHORT).show();
                    fetchDatabaseData();
                }
                hideProgress();
            }
        });

        refreshEpisodeDetails.setRefreshing(false);
    }

    private void displayEpisodeDetails(EpisodeDetails ep) {

        //Make episode details view elements visible
        episodeInfo.setVisibility(View.VISIBLE);

        if(ep.getImageURL() != null || ep.getImageURL().equals("") ){
            Glide.with(getApplicationContext())
                    .load(APIClient.getFullImageUrl(ep.getImageURL()))
                    .apply(new RequestOptions().override(1920, 1080))
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            image.setImageDrawable(resource);
                        }
                    });
        }

        title.setText(ep.getTitle());
        description.setText(ep.getDescription());

        //If season or episode is valid integer then display it, otherwise display 0
        String episodeNumber = "Ep" + (isInteger(ep.getEpisodeNumber()) ? ep.getEpisodeNumber() : "0");
        String season = "S" + (isInteger(ep.getSeason()) ? ep.getSeason(): "0");

        seasonEpisode.setText(season  + " " + episodeNumber);
    }

    private void insertDatabaseData(EpisodeDetails episode) {
        episodeRepository.insertDetails(episode, new DatabaseCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                //Data inserted
            }

            @Override
            public void onError(Throwable t) {
                showError();
            }
        });
    }

    private void fetchDatabaseData() {

        episodeRepository.getDetails(episodeId, new DatabaseCallback<EpisodeDetails>() {
            @Override
            public void onSuccess(EpisodeDetails data) {

                if (data == null) {
                    Toast.makeText(EpisodeDetailsActivity.this, R.string.database_empty, Toast.LENGTH_LONG).show();
                } else {
                    displayEpisodeDetails(data);
                }
            }

            @Override
            public void onError(Throwable t) {
                showError();
            }
        });
    }

    private void initSwipeToRefresh() {

        refreshEpisodeDetails = findViewById(R.id.swipe_to_refresh_episode_details);
        refreshEpisodeDetails.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if(APIClient.isInternetAvailable(context)){
                            fetchInternetEpisodeDetails(true);
                        } else {
                            refreshEpisodeDetails.setRefreshing(false);
                            Toast.makeText(context, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void initViews() {
        //set Toolbar
        toolbar = findViewById(R.id.transparent_toolbar_episode_details);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        image = findViewById(R.id.image_episode_details);
        description = findViewById(R.id.text_view_episode_details_description);
        title = findViewById(R.id.text_view_episode_details_title);
        seasonEpisode = findViewById(R.id.text_view_episode_details_season_ep);
        comments = findViewById(R.id.text_view_show_comments);
        episodeInfo = findViewById(R.id.layout_episode_details);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
