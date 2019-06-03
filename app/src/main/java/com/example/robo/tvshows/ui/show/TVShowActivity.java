package com.example.robo.tvshows.ui.show;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.robo.tvshows.R;
import com.example.robo.tvshows.ui.BaseActivity;
import com.example.robo.tvshows.ui.account.LoginActivity;
import com.example.robo.tvshows.ui.show.details.TVShowDetailsActivity;
import com.example.robo.tvshows.data.DatabaseCallback;
import com.example.robo.tvshows.data.models.DataWrapper;
import com.example.robo.tvshows.data.models.TVShow;
import com.example.robo.tvshows.data.networking.APIClient;
import com.example.robo.tvshows.data.networking.ApiService;
import com.example.robo.tvshows.data.repositories.TVShowRepositoryImpl;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TVShowActivity extends BaseActivity implements ITVShow{

    //Constants
    private static final int SPAN_COUNT = 2;

    //Views
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshTVShows;
    private TextView emptyState;
    private FloatingActionButton btnSwitchView;
    private TVShowAdapter adapter;

    //Other
    private Context context;
    private ApiService apiService;
    private TVShowRepositoryImpl showsRepository;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_shows);

        apiService = APIClient.getApiService();
        context = this;
        showsRepository = new TVShowRepositoryImpl(this);

        initViews();
        initSwitchViewButtonListener();
        initRecycleView();
        initSwipeToRefreshListener();

        //If internet is available fetch data from API, otherwise from database
        if(APIClient.isInternetAvailable(this)){
            fetchInternetData(false);
        } else {
            fetchDatabaseData();
        }
    }

    private void displayTVShows(List<TVShow> shows) {
        //Show empty state if list is empty
        emptyState.setVisibility((shows.size() == 0) ? View.VISIBLE : View.INVISIBLE);

        if (adapter == null) {
            initAdapter(shows);
        } else {
            adapter.setShows(shows);
        }

        if(adapter.getItemCount() != 0){
            //Make button for switching views (grid <-> list) visible
            btnSwitchView.setVisibility(View.VISIBLE);
        } else {
            btnSwitchView.setVisibility(View.GONE);
        }
    }

    @Override
    public void callShowDetailsActivity(String showID) {
        Intent episodesIntent = TVShowDetailsActivity.buildEpisodesActivity(getApplicationContext(), showID);
        startActivity(episodesIntent);
    }


    @Override
    public void onBackPressed() {
        //Make user press back twice to close application
        if (doubleBackToExitPressedOnce) {
            //exit app
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.exit_app, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_show_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.menu_log_out:

                new AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage(R.string.sign_out)
                        .setIcon(R.drawable.ic_logout)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //log out user
                                APIClient.logOutUser(getApplicationContext());

                                //Redirect to login activity
                                Intent login = new Intent(context, LoginActivity.class);
                                startActivity(login);

                                //Remove this activity
                                finish();
                            }})
                        .setNegativeButton(android.R.string.cancel, null).show();

                break;
            default:
                break;
        }
        return true;
    }

    private void fetchInternetData(boolean isScreenRefreshed) {
        //if screen is refreshed there is no need for progress dialog
        if(!isScreenRefreshed){
            showProgress();
        }

        apiService.getShows().enqueue(new Callback<DataWrapper<List<TVShow>>>() {
            @Override
            public void onResponse(Call<DataWrapper<List<TVShow>>> call, Response<DataWrapper<List<TVShow>>> response) {

                if(response.isSuccessful()){
                    List<TVShow> shows = response.body().getData();
                    displayTVShows(shows);
                    insertDatabaseTVShows(shows);
                } else {
                    //try get data from database
                    Toast.makeText(getApplicationContext(), R.string.internet_error_fetching_from_database, Toast.LENGTH_SHORT).show();
                    fetchDatabaseData();
                }
                hideProgress();
            }

            @Override
            public void onFailure(Call<DataWrapper<List<TVShow>>> call, Throwable t) {
                hideProgress();
                //If user didn't cancel API call, fetch data from database
                if(!call.isCanceled()){
                    //try get data from database
                    Toast.makeText(getApplicationContext(), R.string.internet_error_fetching_from_database, Toast.LENGTH_SHORT).show();
                    fetchDatabaseData();
                }
            }
        });

        refreshTVShows.setRefreshing(false);
    }


    private void fetchDatabaseData() {
        showsRepository.getTVShows(new DatabaseCallback<List<TVShow>>() {
            @Override
            public void onSuccess(List<TVShow> data) {
                if (data.isEmpty()) {
                    Toast.makeText(TVShowActivity.this, R.string.database_empty, Toast.LENGTH_LONG).show();
                } else {
                    displayTVShows(data);
                }
            }

            @Override
            public void onError(Throwable t) {
                showError();
            }
        });
    }

    private void insertDatabaseTVShows(List<TVShow> shows) {
        showsRepository.insertTVShows(shows, new DatabaseCallback<Void>() {
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

    private void initSwitchViewButtonListener() {
        btnSwitchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isSwitched = adapter.toggleItemViewType();
                recyclerView.setLayoutManager(isSwitched ? new LinearLayoutManager(context) : new GridLayoutManager(context, SPAN_COUNT));
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initRecycleView(){
        recyclerView = findViewById(R.id.tv_shows_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this,SPAN_COUNT));

        //Init adapter
        ArrayList<TVShow> shows = new ArrayList<>();
        if (adapter == null) {
            initAdapter(shows);
        }
    }

    private void initAdapter(List<TVShow> shows) {
        adapter = new TVShowAdapter(shows, this, btnSwitchView);
        recyclerView.setAdapter(adapter);
    }

    private void initViews(){

        //set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_shows_list);
        setSupportActionBar(toolbar);

        refreshTVShows = findViewById(R.id.swipe_to_refresh_shows);
        btnSwitchView = findViewById(R.id.fab_switch_list_grid);
        emptyState = findViewById(R.id.text_view_empty_state__shows_list);
    }

    private void initSwipeToRefreshListener( ) {
        refreshTVShows.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if(APIClient.isInternetAvailable(context)){
                            fetchInternetData(true);
                        } else {
                            refreshTVShows.setRefreshing(false);
                            Toast.makeText(context, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }
}

interface ITVShow {
    void callShowDetailsActivity(String showID);
}
