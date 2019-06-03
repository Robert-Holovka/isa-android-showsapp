package com.example.robo.tvshows.ui.show.details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.robo.tvshows.R;
import com.example.robo.tvshows.data.DatabaseCallback;
import com.example.robo.tvshows.data.models.DataWrapper;
import com.example.robo.tvshows.data.models.Episode;
import com.example.robo.tvshows.data.models.ShowLikeStatus;
import com.example.robo.tvshows.data.models.ShowWithEpisodes;
import com.example.robo.tvshows.data.models.TVShow;
import com.example.robo.tvshows.data.models.TVShowDetails;
import com.example.robo.tvshows.data.networking.APIClient;
import com.example.robo.tvshows.data.networking.ApiService;
import com.example.robo.tvshows.data.repositories.EpisodeRepositoryImpl;
import com.example.robo.tvshows.data.repositories.ShowDetailsRepositoryImpl;
import com.example.robo.tvshows.ui.BaseActivity;
import com.example.robo.tvshows.ui.episode.AddEpisodeActivity;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TVShowDetailsActivity extends BaseActivity {

    //constants
    public final static int REQUEST_NEW_EPISODE = 100;
    public final static String SHOW_ID = "ShowID";
    public final static String GET_EPISODE = "NewEpisode";

    //Repositories
    private ShowDetailsRepositoryImpl showsRepository;
    private EpisodeRepositoryImpl episodeRepository;

    //Views
    private FloatingActionButton addEpisodeBtn;
    private TextView numOfEpisodes;
    private ImageView showImage;
    private TextView showTitle;
    private TextView showDescription;
    private TextView addEpisodeText;
    private TextView likesCountText;
    private ImageButton likeBtn;
    private ImageButton dislikeBtn;
    private TextView showMoreText;
    private ImageView showMoreIcon;

    //layouts
    private RecyclerView recyclerView;
    private CollapsingToolbarLayout collapsingLayout;
    private LinearLayout showMoreLayout;
    private LinearLayout likeStatusLayout;
    private NestedScrollView emptyStateLayout;

    //Other
    private Toolbar toolbar;
    private EpisodesAdapter adapter;
    private ApiService apiService;
    private Context context;
    private String showID;
    private ShowLikeStatus likeStatus;
    private boolean isShowMoreExpanded = false;
    private TVShowDetails currentShowState;

    public static Intent buildEpisodesActivity(Context context, String showID){
        Intent episodesIntent = new Intent(context, TVShowDetailsActivity.class);
        episodesIntent.putExtra(SHOW_ID, showID);
        return episodesIntent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_shows_details);

        //Get TVShow ID from calling activity
        showID = getIntent().getStringExtra(SHOW_ID);

        //init repositories
        showsRepository = new ShowDetailsRepositoryImpl(this);
        episodeRepository = new EpisodeRepositoryImpl(this);

        context = this;
        apiService = APIClient.getApiService();

        initViews();
        initRecycleView();
        initAddEpisodeListener();
        initLikeButton();
        initDislikeButton();
        initShowMoreListener();

        //enable like, dislike to user once after show data is displayed
        fetchLikeStatus();

        //If internet is available fetch data from API, otherwise from database
        if(APIClient.isInternetAvailable(this)){
            fetchInternetDataShowDetails();
        } else {
            fetchDatabaseData();
        }

    }

    private void initShowMoreListener() {

        showMoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isShowMoreExpanded){
                    //Show less is visible
                    showDescription.setMaxLines(4);
                    showMoreText.setText("Show more");
                    showMoreIcon.setImageResource(R.drawable.ic_keyboard_arrow_down);

                } else {
                    //Show more is visible
                    showDescription.setMaxLines(Integer.MAX_VALUE);
                    showMoreText.setText("Show less");
                    showMoreIcon.setImageResource(R.drawable.ic_keyboard_arrow_up);
                }

                isShowMoreExpanded = !isShowMoreExpanded;
            }
        });
    }

    private void initAddEpisodeListener() {

        //Set floating button for adding new episode
        addEpisodeBtn = findViewById(R.id.fab_addEpisode);

        addEpisodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAddEpisodeActivity();
            }
        });

        //Set listener on text view in empty state
        addEpisodeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAddEpisodeActivity();
            }
        });
    }

    private void callAddEpisodeActivity(){
        //Call activity for adding new episode
        Intent addEpisodeIntent = AddEpisodeActivity.buildAddEpisodeActivity(context, showID);
        startActivityForResult(addEpisodeIntent, REQUEST_NEW_EPISODE);
    }

    private void initViews() {

        //set Toolbar
        toolbar = findViewById(R.id.transparent_toolbar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);

        showMoreIcon = findViewById(R.id.image_show_more);
        showMoreText = findViewById(R.id.text_view_show_more);
        showMoreLayout = findViewById(R.id.layout_show_more);
        likesCountText = findViewById(R.id.text_view_likes_count);
        collapsingLayout = findViewById(R.id.collapse_toolbar_layout);
        addEpisodeText = findViewById(R.id.text_add_new_episodes);
        numOfEpisodes = findViewById(R.id.text_view_number_of_episodes);
        showImage = findViewById(R.id.image_show_details);
        showTitle = findViewById(R.id.text_view_show_title);
        showDescription = findViewById(R.id.text_view_show_description);
        likeBtn = findViewById(R.id.btn_like);
        dislikeBtn = findViewById(R.id.btn_dislike);
        likeStatusLayout = findViewById(R.id.layout_like_status);
        emptyStateLayout = findViewById(R.id.layout_empty_state_show_details);
    }

    private void initRecycleView(){
        recyclerView = findViewById(R.id.episodes_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //init adapter
        ArrayList<Episode> episodes = new ArrayList<>();
        if (adapter == null) {
            initAdapter(episodes);
        }
    }

    private void initAdapter(List<Episode> episodes) {
        adapter = new EpisodesAdapter(episodes);
        recyclerView.setAdapter(adapter);
    }

    private void fetchInternetDataShowDetails() {
        showProgress();

        //Get details of tv show, without episodes
        apiService.getShowDetails(showID).enqueue(new Callback<DataWrapper<TVShowDetails>>() {
            @Override
            public void onResponse(Call<DataWrapper<TVShowDetails>> call, Response<DataWrapper<TVShowDetails>> response) {

                if(response.isSuccessful()){
                    TVShowDetails show = response.body().getData();
                    displayShowDetails(show);
                    insertDatabaseTVShow(show);

                    //fetch and display episode list
                    fetchInternetDataShowEpisodes();
                } else {
                    hideProgress();
                    //try get data from database
                    Toast.makeText(getApplicationContext(), R.string.internet_error_fetching_from_database, Toast.LENGTH_SHORT).show();
                    fetchDatabaseData();
                }
            }

            @Override
            public void onFailure(Call<DataWrapper<TVShowDetails>> call, Throwable t) {
                hideProgress();

                //If user didn't cancel API call, fetch data from database
                if(!call.isCanceled()){
                    //try get data from database
                    Toast.makeText(getApplicationContext(), R.string.internet_error_fetching_from_database, Toast.LENGTH_SHORT).show();
                    fetchDatabaseData();
                }
            }
        });
    }

    private void fetchInternetDataShowEpisodes() {
        //get Episodes from tv show

        apiService.getEpisodes(showID).enqueue(new Callback<DataWrapper<List<Episode>>>() {
            @Override
            public void onResponse(Call<DataWrapper<List<Episode>>> call, Response<DataWrapper<List<Episode>>> response) {
                if(response.isSuccessful()){
                    List<Episode> episodes = response.body().getData();
                    displayEpisodeList(episodes);
                    insertDatabaseEpisodes(episodes);
                } else {
                    //try get data from database
                    Toast.makeText(getApplicationContext(), R.string.internet_error_fetching_from_database, Toast.LENGTH_SHORT).show();
                    fetchDatabaseData();
                }
                hideProgress();
            }

            @Override
            public void onFailure(Call<DataWrapper<List<Episode>>> call, Throwable t) {
                hideProgress();
                //If user didn't cancel API call, fetch data from database
                if(!call.isCanceled()){
                    //try get data from database
                    Toast.makeText(getApplicationContext(), R.string.internet_error_fetching_from_database, Toast.LENGTH_SHORT).show();
                    fetchDatabaseData();
                }
            }
        });
    }

    private void displayShowDetails(TVShowDetails show) {

        //save show instance
        currentShowState = show;

        //set show name as title
        collapsingLayout.setTitle(show.getTitle());
        //Display likes count
        likesCountText.setText(String.valueOf(show.getLikesCount()));

        //Load show image
        Glide.with(getApplicationContext())
                .load(APIClient.getFullImageUrl(show.getImageUrl()))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        showImage.setImageDrawable(resource);
                    }
                });

        showTitle.setText(show.getTitle());
        showDescription.setText(show.getDescription());

        //display "show more" functionality for expanding description if needed
        if(showDescription.getLineCount() > 4) {
            displayShowMore();
        }
    }

    private void displayShowMore(){
        showMoreLayout.setVisibility(View.VISIBLE);
        showMoreLayout.setClickable(true);
    }

    private void displayEpisodeList(List<Episode> episodes) {

        if (adapter == null) {
            initAdapter(episodes);
        } else {
            adapter.setEpisodeList(episodes);
        }

        updateListState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    private void updateListState(){
        //Update empty state placeholder
        boolean cond = adapter.getItemCount() == 0;
        emptyStateLayout.setVisibility((cond) ? View.VISIBLE : View.GONE);

        //Update episode counter
        numOfEpisodes.setText(String.valueOf(adapter.getItemCount()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Add new episode to the TVShow episode list
        if (requestCode == REQUEST_NEW_EPISODE) {
            if(resultCode == Activity.RESULT_OK){
                //Fetch new episode from child activity
                Episode newEpisode = data.getParcelableExtra(GET_EPISODE);
                adapter.addNewEpisode(newEpisode);

                //Scroll to the new item
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);

                updateListState();

                //add new episode to database
                ArrayList<Episode> newEpisodes = new ArrayList<>();
                newEpisodes.add(newEpisode);
                insertDatabaseEpisodes(newEpisodes);
            }
        }
    }

    private void fetchLikeStatus(){

        //Make like, dislike, likes Count visible
        likeStatusLayout.setVisibility(View.VISIBLE);

        //Get show like status
        showsRepository.getLikeStatus(showID, new DatabaseCallback<ShowLikeStatus>() {
            @Override
            public void onSuccess(ShowLikeStatus data) {

                if (data == null) {
                    //Show isn't liked nor disliked yet, enable both buttons
                    likeBtn.setClickable(true);
                    dislikeBtn.setClickable(true);
                } else {
                    //save Like status
                    likeStatus = data;

                    if(data.isLiked()){
                        //Set liked image, disable like button
                        likeBtn.setImageResource(R.drawable.ic_thumb_up);
                        //Disable like button
                        likeBtn.setClickable(false);
                    } else if(data.isDisliked()){
                        //Set disliked image, disable dislike button
                        dislikeBtn.setImageResource(R.drawable.ic_thumb_down);
                        dislikeBtn.setClickable(false);
                    } else {
                        //Show isn't liked nor disliked yet, enable both buttons
                        likeBtn.setClickable(true);
                        dislikeBtn.setClickable(true);
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                showError();
            }
        });
    }

    private void fetchDatabaseData() {

        //Get TVShowDetails with episodes
        showsRepository.getShowDetails(showID, new DatabaseCallback<ShowWithEpisodes>() {
            @Override
            public void onSuccess(ShowWithEpisodes data) {

                if (data == null) {
                    Toast.makeText(TVShowDetailsActivity.this, R.string.database_empty, Toast.LENGTH_LONG).show();
                } else {
                    displayShowDetails(data.getShow());
                    displayEpisodeList(data.getEpisodes());
                }
            }

            @Override
            public void onError(Throwable t) {
                showError();
            }
        });
    }

    private void insertDatabaseTVShow(TVShowDetails show) {

        showsRepository.insertShowDetails(show, new DatabaseCallback<Void>() {
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

    private void insertLikeStatus(ShowLikeStatus likeStatus) {

        showsRepository.insertLikeStatus(likeStatus, new DatabaseCallback<Void>() {
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

    private void initLikeButton(){
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If internet is available like, otherwise display internet connection error
                if (APIClient.isInternetAvailable(context)) {

                    //disable like button, prevent double click
                    likeBtn.setClickable(false);

                    showProgress();
                    //Post like to API
                    apiService.likeShow(showID).enqueue(new Callback<TVShowDetails>() {
                        @Override
                        public void onResponse(Call<TVShowDetails> call, Response<TVShowDetails> response) {
                            if(response.isSuccessful()){

                                TVShowDetails showDetails = response.body();
                                //Set correct image Url
                                showDetails.setImageUrl(currentShowState.getImageUrl());
                                //Insert show details with updated likesCount in database
                                insertDatabaseTVShow(showDetails);

                                //Update likes count text view
                                likesCountText.setText(String.valueOf(showDetails.getLikesCount()));


                                if (likeStatus == null) {
                                    likeStatus = new ShowLikeStatus(showID, null);
                                }

                                if (likeStatus.isDisliked()) {
                                    //User has previously disliked show
                                    //Enable dislike button, remove disliked state icon
                                    dislikeBtn.setImageResource(R.drawable.ic_thumb_down_outline);
                                    dislikeBtn.setClickable(true);
                                }

                                //Change like status to liked
                                likeStatus.like();
                                //Change like icon to liked state, disable like button
                                likeBtn.setImageResource(R.drawable.ic_thumb_up);

                                //Insert like status to database
                                insertLikeStatus(likeStatus);

                                hideProgress();
                            } else {
                                hideProgress();
                                showError();
                                likeBtn.setClickable(true);
                            }
                        }

                        @Override
                        public void onFailure(Call<TVShowDetails> call, Throwable t) {
                            hideProgress();
                            likeBtn.setClickable(true);
                            //If user didn't cancel API call, show error
                            if(!call.isCanceled()){
                                showError();
                            }
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initDislikeButton(){

        dislikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If internet is available dislike, otherwise display internet connection error
                if (APIClient.isInternetAvailable(context)) {

                    //disable dislike button, prevent double click
                    dislikeBtn.setClickable(false);

                    showProgress();
                    //Post dislike to API
                    apiService.dislikeShow(showID).enqueue(new Callback<TVShowDetails>() {
                        @Override
                        public void onResponse(Call<TVShowDetails> call, Response<TVShowDetails> response) {
                            if(response.isSuccessful()){

                                TVShowDetails showDetails = response.body();
                                //Set correct image Url
                                showDetails.setImageUrl(currentShowState.getImageUrl());
                                //Insert show details with updated likesCount in database
                                insertDatabaseTVShow(showDetails);

                                //Update likes count text view
                                likesCountText.setText(String.valueOf(showDetails.getLikesCount()));

                                if (likeStatus == null) {
                                    likeStatus = new ShowLikeStatus(showID, null);
                                }

                                if(likeStatus.isLiked()){
                                    //User has previously liked show
                                    //Enable like button, remove liked icon state
                                    likeBtn.setImageResource(R.drawable.ic_thumb_up_outline);
                                    likeBtn.setClickable(true);
                                }

                                //Change like status to disliked
                                likeStatus.dislike();
                                //Change icon to disliked state
                                dislikeBtn.setImageResource(R.drawable.ic_thumb_down);

                                //Insert like status to database
                                insertLikeStatus(likeStatus);

                                hideProgress();
                            } else {
                                hideProgress();
                                showError();
                                dislikeBtn.setClickable(true);
                            }
                        }

                        @Override
                        public void onFailure(Call<TVShowDetails> call, Throwable t) {
                            hideProgress();
                            dislikeBtn.setClickable(true);
                            //If user didn't cancel API call, show error
                            if(!call.isCanceled()){
                                showError();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void insertDatabaseEpisodes(List<Episode> episodes) {

        //Set show ID for each episode
        for(Episode ep : episodes){
            ep.setShowID(showID);
        }

        episodeRepository.insertEpisodes(episodes, new DatabaseCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                //data inserted
            }

            @Override
            public void onError(Throwable t) {
                showError();
            }
        });
    }
}