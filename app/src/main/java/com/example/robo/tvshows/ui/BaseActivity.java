package com.example.robo.tvshows.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.robo.tvshows.R;
import com.example.robo.tvshows.data.networking.APIClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseActivity extends AppCompatActivity {

    private static Dialog progressDialog;

    public BaseActivity(){

    }

    @Override
    protected void onPause() {
        super.onPause();
        //Cancel all API calls if user leaves current screen
        APIClient.cancelAPICalls();
    }

    protected void showError() {
        new AlertDialog.Builder(this)
                .setTitle("Something went wrong!")
                .setMessage(getResources().getString(R.string.something_went_wrong))
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    protected void showProgress() {
        progressDialog = ProgressDialog.show(this, "", "Loading", true, false);
    }

    protected void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        progressDialog = null;
    }

    protected boolean isInteger(String s) {

        if(s == null || s.equals("")){
            return false;
        }

        //Declare integer pattern
        String integerPattern = "[0-9]{1,9}";
        Pattern pattern = Pattern.compile(integerPattern, Pattern.CASE_INSENSITIVE);
        //Match entered String with valid integer pattern
        Matcher matcher = pattern.matcher(s);

        if(matcher.matches()){
            return true;
        }

        return false;
    }
}
