package com.example.robo.tvshows.ui.episode.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.robo.tvshows.R;
import com.example.robo.tvshows.data.DatabaseCallback;
import com.example.robo.tvshows.data.models.Comment;
import com.example.robo.tvshows.data.models.DataWrapper;
import com.example.robo.tvshows.data.models.NewComment;
import com.example.robo.tvshows.data.models.TVShow;
import com.example.robo.tvshows.data.networking.APIClient;
import com.example.robo.tvshows.data.networking.ApiService;
import com.example.robo.tvshows.data.repositories.EpisodeRepositoryImpl;
import com.example.robo.tvshows.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EpisodeCommentsActivity extends BaseActivity {

    //Constants
    private static final String EPISODE_ID = "EPISODE_ID";

    //Views
    private CommentsAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyState;
    private Toolbar toolbar;
    private SwipeRefreshLayout refreshComments;
    private EditText commentText;
    private Button btnPostComment;

    //Others
    private String episodeId;
    private ApiService apiService;
    private Context context;
    private EpisodeRepositoryImpl episodeRepository;

    public static Intent buildCommentActivity(Context context, String episodeId){
        Intent commentIntent = new Intent(context, EpisodeCommentsActivity.class);
        commentIntent.putExtra(EPISODE_ID, episodeId);
        return commentIntent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_comments);

        //Get Episode ID from calling activity
        episodeId = getIntent().getStringExtra(EPISODE_ID);

        initRecycleView();
        initSwipeToRefresh();
        initViews();
        initPostCommentListener();

        context = this;
        apiService = APIClient.getApiService();
        episodeRepository = new EpisodeRepositoryImpl(this);

        //If internet is available fetch data from API, otherwise from database
        if(APIClient.isInternetAvailable(this)){
            fetchInternetDataComments(false);
        } else {
            fetchDatabaseData();
        }
    }

    private boolean isCommentValid(String newComment){
        //Comment is too long
        int commentNumOfChars = newComment.length();
        if(commentNumOfChars > 160){
            String longCommentMessage = getResources().getString(R.string.error_long_comment);
            String errorMessage = longCommentMessage + " " + String.valueOf(commentNumOfChars);
            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
            return false;
        }

        if(newComment.trim().equals("")){
            Toast.makeText(getApplicationContext(), "First write something, then post it!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initPostCommentListener() {
        btnPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Prevent double click
                btnPostComment.setEnabled(false);

                String text = commentText.getText().toString();

                //If comment is not valid do not post
                if(!isCommentValid(text)){
                    btnPostComment.setEnabled(true);
                    return;
                }

                final NewComment newComment = new NewComment(text, episodeId);

                if(APIClient.isInternetAvailable(context)){
                    //post new comment
                    apiService.postComment(newComment).enqueue(new Callback<DataWrapper<Comment>>() {
                        @Override
                        public void onResponse(Call<DataWrapper<Comment>> call, Response<DataWrapper<Comment>> response) {
                            if(response.isSuccessful()){

                                //get new comment with generated id
                                Comment comment = response.body().getData();

                                //Update comments list
                                adapter.addNewComment(comment);
                                updateListState();

                                //Clear input edit text
                                commentText.setText("");

                                //Scroll to the new item
                                recyclerView.scrollToPosition(adapter.getItemCount() - 1);

                                //Update comments database
                                insertCommentInDatabase(comment);

                                btnPostComment.setEnabled(true);

                            } else {

                                showError();
                                btnPostComment.setEnabled(true);
                            }
                        }
                        @Override
                        public void onFailure(Call<DataWrapper<Comment>> call, Throwable t) {
                            btnPostComment.setEnabled(true);
                            if(!call.isCanceled()){
                                //If user didn't cancel call show error
                                showError();
                            }
                        }
                    });

                } else {
                    //Display no internet connection error
                    btnPostComment.setEnabled(true);
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchInternetDataComments( boolean isScreenRefreshed) {
        //if screen is refreshed there is no need for progress dialog
        if(!isScreenRefreshed){
            showProgress();
        }

        apiService.getComments(episodeId).enqueue(new Callback<DataWrapper<List<Comment>>>() {
            @Override
            public void onResponse(Call<DataWrapper<List<Comment>>> call, Response<DataWrapper<List<Comment>>> response) {
                if(response.isSuccessful()){

                    List<Comment> comments = response.body().getData();
                    displayCommentsList(comments);
                    insertCommentsInDatabase(comments);

                } else {
                    if(!call.isCanceled()){
                        //try get data from database
                        Toast.makeText(getApplicationContext(), R.string.internet_error_fetching_from_database, Toast.LENGTH_SHORT).show();
                        fetchDatabaseData();
                    }
                }
                hideProgress();
            }

            @Override
            public void onFailure(Call<DataWrapper<List<Comment>>> call, Throwable t) {
                hideProgress();

                //try get data from database
                Toast.makeText(getApplicationContext(), R.string.internet_error_fetching_from_database, Toast.LENGTH_SHORT).show();
                fetchDatabaseData();
            }
        });

        refreshComments.setRefreshing(false);
    }

    private void fetchDatabaseData() {

        episodeRepository.getComments(episodeId, new DatabaseCallback<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> data) {
                if (data == null) {
                    Toast.makeText(EpisodeCommentsActivity.this, R.string.database_empty, Toast.LENGTH_LONG).show();
                } else {
                    displayCommentsList(data);
                }
            }

            @Override
            public void onError(Throwable t) {
                showError();
            }
        });
    }

    private void insertCommentsInDatabase(List<Comment> comments) {

        episodeRepository.insertComments(comments, new DatabaseCallback<Void>() {
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

    private void insertCommentInDatabase(Comment comment) {

        episodeRepository.insertComment(comment, new DatabaseCallback<Void>() {
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

    private void displayCommentsList(List<Comment> comments) {

        if (adapter == null) {
            initAdapter(comments);
        } else {
            adapter.setComments(comments);
        }

        updateListState();
    }

    private void updateListState(){
        //Update empty state placeholder
        boolean cond = adapter.getItemCount() == 0;

        emptyState.setVisibility((cond)? View.VISIBLE : View.INVISIBLE);

    }

    private void initSwipeToRefresh() {

        refreshComments= findViewById(R.id.swipe_to_refresh_comments);
        refreshComments.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if(APIClient.isInternetAvailable(context)){
                            fetchInternetDataComments(true);
                        } else {
                            refreshComments.setRefreshing(false);
                            Toast.makeText(context, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void initRecycleView(){
        recyclerView = findViewById(R.id.recycler_view_comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //init comments adapter
        ArrayList<Comment> comments = new ArrayList<>();
        if (adapter == null) {
            initAdapter(comments);
        }
    }

    private void initAdapter(List<Comment> comments) {
        adapter = new CommentsAdapter(comments);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews(){

        //set Toolbar
        toolbar = findViewById(R.id.toolbar_comments);
        toolbar.setTitle("Comments");
        setSupportActionBar(toolbar);

        emptyState = findViewById(R.id.text_view_comment_empty_state);
        btnPostComment = findViewById(R.id.btn_post_comment);
        commentText = findViewById(R.id.edit_text_comment_text);
    }
}
