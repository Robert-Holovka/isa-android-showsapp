package com.example.robo.tvshows.ui.episode;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.robo.tvshows.R;
import com.example.robo.tvshows.data.DatabaseCallback;
import com.example.robo.tvshows.data.models.DataWrapper;
import com.example.robo.tvshows.data.models.Episode;
import com.example.robo.tvshows.data.models.EpisodeDetails;
import com.example.robo.tvshows.data.models.MediaResponse;
import com.example.robo.tvshows.data.models.NewEpisode;
import com.example.robo.tvshows.data.networking.APIClient;
import com.example.robo.tvshows.data.networking.ApiService;
import com.example.robo.tvshows.data.repositories.EpisodeRepositoryImpl;
import com.example.robo.tvshows.ui.BaseActivity;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEpisodeActivity extends BaseActivity {

    //constants
    public static final int REQUEST_IMAGE_CAMERA = 300;
    public static final int REQUEST_IMAGE_GALLERY = 200;
    public final static String GET_EPISODE = "NewEpisode";
    private final static String SHOW_ID = "SHOW_ID";

    //Views
    private Button saveBtn;
    private TextInputLayout title;
    private TextInputLayout description;
    private RelativeLayout addImage;
    private ImageView imageView;
    private TextView addImageTextView;
    private TextView seasonEpisodeTextView;
    private RelativeLayout addSeasonEpisode;

    //Other
    private String episodeImageURI = null;
    private int seasonTag;
    private int episodeTag;
    private String mCurrentPhotoPath;
    private Context context;
    private ApiService apiService;
    private String showId;
    private EpisodeRepositoryImpl episodeRepository;

    public static Intent buildAddEpisodeActivity(Context context, String showId){
        Intent addEpisodeIntent = new Intent(context, AddEpisodeActivity.class);
        addEpisodeIntent.putExtra(SHOW_ID, showId);
        return addEpisodeIntent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_episode);

        //Get TVShow ID from calling activity
        showId = getIntent().getStringExtra(SHOW_ID);

        context = this;
        apiService = APIClient.getApiService();
        episodeRepository = new EpisodeRepositoryImpl(this);

        //init views and listeners
        initViews();
        initSaveButton();
        initAddImageAction();
        initAddSeasonEpisodeTag();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean shouldAlertUser(){
        //Should app alert user about losing inputs

        //Check if title is not empty
        boolean cond1 = title.getEditText().getText().toString().length() > 0;
        //Check if description is not empty
        boolean cond2 = description.getEditText().getText().length() > 0;
        //Check if Season-Episode is not empty
        boolean cond3 = !seasonEpisodeTextView.getText().toString().equals(getResources().getString(R.string.unknown_state))
                && !seasonEpisodeTextView.getText().toString().trim().equals(getResources().getString(R.string.error_season_episode_missing).trim());
        //Check if Image is set
        boolean cond4 = (episodeImageURI != null);

        //Alert dialog if user has entered some input
        if(cond1 || cond2 || cond3 || cond4) {
            return true;
        } else {
            return false;
        }
    }

    //Creates intent for accessing image gallery
    private void actionImageFromGallery(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , REQUEST_IMAGE_GALLERY);
    }

    //Creates intent for accessing user camera
    private void actionImageFromCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAMERA);
            }
        }
    }


    private void setNewImage(String newImage){
        //Remove default Image
        addImageTextView.setVisibility(View.INVISIBLE);
        Glide.with(this).load(newImage).into(imageView);
        episodeImageURI = newImage;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case REQUEST_IMAGE_GALLERY:
                if(resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    String imageUriString = getRealPathFromURI(this, selectedImage);
                    setNewImage(imageUriString);
                }
                break;

            case REQUEST_IMAGE_CAMERA:
                if(resultCode == RESULT_OK){
                    setNewImage(mCurrentPhotoPath);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case REQUEST_IMAGE_GALLERY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    actionImageFromGallery();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddEpisodeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.permission_read_external_explained), Toast.LENGTH_LONG).show();
                    } else{
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.permission_read_external_storage_denied), Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case REQUEST_IMAGE_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    actionImageFromCamera();
                } else {
                    if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                        if (ActivityCompat.shouldShowRequestPermissionRationale(AddEpisodeActivity.this, Manifest.permission.CAMERA)) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.permission_camera_explained), Toast.LENGTH_LONG).show();
                        } else{
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.permission_camera_denied), Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(!(grantResults[1] == PackageManager.PERMISSION_GRANTED)){
                        if (ActivityCompat.shouldShowRequestPermissionRationale(AddEpisodeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.permission_write_external_explained), Toast.LENGTH_LONG).show();
                        } else{
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.permission_write_external_storage_denied), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
    }

    private void initViews(){
        //set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_add_episode);
        setSupportActionBar(toolbar);

        saveBtn = findViewById(R.id.save_episode_btn);
        title = findViewById(R.id.add_episode_name);
        description = findViewById(R.id.add_episode_description);
        addImage = findViewById(R.id.add_episode_image);
        imageView = findViewById(R.id.episode_image);
        addImageTextView = findViewById(R.id.add_image_text_view);
        seasonEpisodeTextView = findViewById(R.id.episode_season_TextView);
        addSeasonEpisode = findViewById(R.id.open_dialog);
    }

    private void initAddImageAction(){
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Create dialog with options to fetch image
                final CharSequence[] items = {
                        "Camera", "Gallery"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(AddEpisodeActivity.this);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if(items[item].equals(items[0])) {
                            //Fetch image from camera
                            if (ActivityCompat.checkSelfPermission(AddEpisodeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                                    || ActivityCompat.checkSelfPermission(AddEpisodeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(AddEpisodeActivity.this,
                                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_CAMERA);
                            } else {
                                actionImageFromCamera();
                            }

                        } else if(items[item].equals(items[1])) {
                            //Fetch image from gallery
                            if (ActivityCompat.checkSelfPermission(AddEpisodeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(AddEpisodeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_IMAGE_GALLERY);
                            } else {
                                actionImageFromGallery();
                            }
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void initAddSeasonEpisodeTag(){
        //Dialog for entering season and episode value
        addSeasonEpisode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialogSeasonEpisode = new Dialog(AddEpisodeActivity.this);
                dialogSeasonEpisode.setContentView(R.layout.dialog_episode_season_number);

                Button btnSaveSeasonEpisode = dialogSeasonEpisode.findViewById(R.id.btn_save_episode_season);

                //NumberPicker for season
                final NumberPicker npSeason =  dialogSeasonEpisode.findViewById(R.id.numberPickerSeason);
                npSeason.setMaxValue(20);
                npSeason.setMinValue(1);
                npSeason.setValue((seasonTag != 0)? seasonTag : 1);

                //NumberPicker for episode
                final NumberPicker npEpisode =  dialogSeasonEpisode.findViewById(R.id.numberPickerEpisode);
                npEpisode.setMaxValue(99);
                npEpisode.setMinValue(1);
                npEpisode.setValue((episodeTag != 0)? episodeTag : 1);

                btnSaveSeasonEpisode.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        //Fetch season and episode number from NumberPicker object
                        seasonTag = npSeason.getValue();
                        episodeTag = npEpisode.getValue();

                        //Update season & episode TextView
                        seasonEpisodeTextView.setText("Season " + seasonTag + ", Ep " + episodeTag);
                        int color = getResources().getColor(R.color.colorPrimary);
                        seasonEpisodeTextView.setTextColor(color);
                        seasonEpisodeTextView.setTextSize(14);

                        dialogSeasonEpisode.dismiss();
                    }
                });

                dialogSeasonEpisode.show();
            }
        });
    }

    private boolean isTitleValid(){

        String epTitle = title.getEditText().getText().toString().trim();

        if(epTitle.equals("")){
            //set error message if title is empty
            title.setError(getResources().getString(R.string.error_empty_field));
            return false;
        } else {
            //remove error message
            title.setError(null);
            return true;
        }
    }

    private boolean isDescriptionValid(){

        String epDescription = description.getEditText().getText().toString().trim();

        if(epDescription.length() < 50){
            //set error message if description does not contain at least 50 characters
            description.setError(getResources().getString(R.string.error_min_50_char));
            return false;
        } else {
            //remove error message
            description.setError(null);
            return true;
        }
    }

    private boolean isImageUploaded(){

        if(episodeImageURI == null){

            //Set error message and color
            addImageTextView.setError("");
            addImageTextView.setTextColor(-765666);
            addImageTextView.setText(R.string.error_photo_missing);
            return false;
        }

        return true;

    }

    private boolean isSeasonEpisodeValid(){

        String seasonEpisode = seasonEpisodeTextView.getText().toString().trim();

        if (seasonEpisode.equals(getResources().getString(R.string.unknown_state).trim())){

            //Set error message and color
            seasonEpisodeTextView.setTextColor(-765666);
            seasonEpisodeTextView.setTextSize(12);
            seasonEpisodeTextView.setText(R.string.error_season_episode_missing);

            return false;
        }

        return true;
    }

    private void initSaveButton(){
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //prevent double click
                saveBtn.setEnabled(false);

                boolean cond1 = isTitleValid();
                boolean cond2 = isDescriptionValid();
                boolean cond3 = isSeasonEpisodeValid();
                boolean cond4 = isImageUploaded();

                if(!cond1 || ! cond2 || !cond3 || !cond4){
                    //show errors, do nothing, enable save button again
                    saveBtn.setEnabled(true);
                    return;
                }

                //If user entered valid data, post episode on API
                if(APIClient.isInternetAvailable(context)){
                    uploadNewEpisode();
                } else {
                    saveBtn.setEnabled(true);
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadNewEpisode(){

        showProgress();
        //First upload episode image so you can get image media Id from API

        File image = new File(episodeImageURI);

        apiService.uploadMedia(RequestBody.create(MediaType.parse("image/jpg"), image)).enqueue(new Callback<DataWrapper<MediaResponse>>() {
            @Override
            public void onResponse(Call<DataWrapper<MediaResponse>> call, Response<DataWrapper<MediaResponse>> response) {

                if(response.isSuccessful()){
                    //Image is successfully uploaded, save media info
                    MediaResponse media = response.body().getData();
                    //Upload episode details now once media id is available
                    uploadEpisodeDetails(media);

                }else {
                    hideProgress();
                    saveBtn.setEnabled(true);
                    showError();
                }
            }

            @Override
            public void onFailure(Call<DataWrapper<MediaResponse>> call, Throwable t) {
                hideProgress();
                saveBtn.setEnabled(true);
                if(!call.isCanceled()){
                    //If user didn't cancel call show error
                    showError();
                }
            }
        });
    }

    private void uploadEpisodeDetails(MediaResponse media){
        //Object for sending episode details to API
        NewEpisode newEpisode = new NewEpisode(showId,
                media.getId(),
                title.getEditText().getText().toString(),
                description.getEditText().getText().toString(),
                String.valueOf(episodeTag),
                String.valueOf(seasonTag));

        apiService.postEpisode(newEpisode).enqueue(new Callback<DataWrapper<EpisodeDetails>>() {
            @Override
            public void onResponse(Call<DataWrapper<EpisodeDetails>> call, Response<DataWrapper<EpisodeDetails>> response) {
                if(response.isSuccessful()){

                    //Insert episode details in database
                    EpisodeDetails episodeDetails = response.body().getData();
                    insertDatabaseData(episodeDetails);

                    //Cache full sized (-1, -1) image in glide
                    Glide.with(context)
                            .load(APIClient.getFullImageUrl(episodeDetails.getImageURL()))
                            .downloadOnly(-1, -1);

                    //Return new episode for adapter list
                    Episode newEpisode = new Episode(
                            episodeDetails.getID(),
                            episodeDetails.getTitle(),
                            episodeDetails.getDescription(),
                            episodeDetails.getImageURL(),
                            episodeDetails.getEpisodeNumber(),
                            episodeDetails.getSeason());

                    //Return new episode
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(GET_EPISODE, newEpisode);
                    setResult(Activity.RESULT_OK, returnIntent);
                    //Destroy this activity
                    finish();

                } else {
                    saveBtn.setEnabled(true);
                    showError();
                }
                hideProgress();
            }

            @Override
            public void onFailure(Call<DataWrapper<EpisodeDetails>> call, Throwable t) {
                hideProgress();
                saveBtn.setEnabled(true);
                if(!call.isCanceled()){
                    //If user didn't cancel call show error
                    showError();
                }
            }
        });

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

    @Override
    public void onBackPressed() {
        if(shouldAlertUser()){
            new AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setMessage(R.string.warning_text)
                    .setIcon(R.drawable.ic_warning)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        } else{
            super.onBackPressed();
        }
    }
}
