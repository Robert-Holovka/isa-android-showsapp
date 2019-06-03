package com.example.robo.tvshows.ui.account;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.robo.tvshows.R;
import com.example.robo.tvshows.data.models.User;
import com.example.robo.tvshows.data.networking.APIClient;
import com.example.robo.tvshows.data.networking.ApiService;
import com.example.robo.tvshows.ui.BaseActivity;
import com.example.robo.tvshows.ui.show.TVShowActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity {

    //Login activity methods
    private static IValidation validation;
    private static ICancellation killLogin;

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private TextInputLayout textInputConfirmPassword;
    private Button btnRegister;
    private ApiService apiService;


    public static Intent buildRegisterActivity(Context context, IValidation validationMethods, ICancellation killLoginActivity) {
        Intent registerIntent = new Intent(context, RegisterActivity.class);
        //Get implemented validation methods from base activity
        validation = validationMethods;
        killLogin = killLoginActivity;
        return registerIntent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

        //fetch api client
        apiService = APIClient.getApiService();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void registerButtonListener(View v) {

        //prevent double click
        btnRegister.setEnabled(false);

        boolean isEmailValid = validation.validateEmail(textInputEmail);
        boolean isPasswordValid = validation.validatePassword(textInputPassword);
        boolean matchingPasswords = validateConfirmPassword();

        if (!isEmailValid || !isPasswordValid || !matchingPasswords) {
            //Show errors, do nothing, enable btn register
            btnRegister.setEnabled(true);
            return;
        }

        if (APIClient.isInternetAvailable(this)) {

            String email = textInputEmail.getEditText().getText().toString().trim();
            String password = textInputPassword.getEditText().getText().toString().trim();

            User user = new User(email, password);
            //Pass new user to api
            Call<Void> registerUser = apiService.register(user);

            showProgress();

            registerUser.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 201) {
                        hideProgress();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.acc_created), Toast.LENGTH_SHORT).show();

                        //registration is successful, log in automatically
                        startTVShowActivity();
                        //Remove login activity from stack
                        killLogin.killLoginActivity();

                        //Prevent memory leak
                        killLogin = null;
                        validation = null;

                        //Remove this activity from stack
                        finish();
                    } else if (response.code() == 200) {
                        //Specified email already exists
                        hideProgress();
                        textInputEmail.setError(getResources().getString(R.string.email_exists));
                    } else {
                        hideProgress();
                        showError();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    hideProgress();

                    if(!call.isCanceled()){
                        //If user didn't cancel API Call, show error
                        showError();
                    }
                }
            });
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }

        //enable register button again
        btnRegister.setEnabled(true);
    }

    public void startTVShowActivity() {
        Intent showsIntent = new Intent(this, TVShowActivity.class);
        startActivity(showsIntent);
    }

    private boolean validateConfirmPassword() {

        //fetch initial password
        String pass1 = textInputPassword.getEditText().getText().toString().trim();
        //fetch confirm password
        String pass2 = textInputConfirmPassword.getEditText().getText().toString().trim();

        if (!pass1.equals(pass2)) {
            //set error message if passwords are not equal
            textInputConfirmPassword.setError(getResources().getString(R.string.confirm_password_error));
            return false;
        } else {
            //remove error message
            textInputConfirmPassword.setError(null);
            return true;
        }
    }

    //Should app alert user about losing inputs
    private boolean alertUser() {

        //Check if title is not empty
        boolean cond1 = textInputEmail.getEditText().getText().toString().trim().length() > 0;
        //Check if description is not empty
        boolean cond2 = textInputPassword.getEditText().getText().toString().trim().length() > 0;
        //Check if Season-Episode textview is not empty
        boolean cond3 = textInputConfirmPassword.getEditText().getText().toString().trim().length() > 0;

        //Alert dialog if user has entered some input
        if (cond1 || cond2 || cond3) {
            return true;
        } else {
            return false;
        }
    }

    private void initViews(){

        //Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);

        textInputEmail = findViewById(R.id.text_input_register_email);
        textInputPassword = findViewById(R.id.text_input_register_password);
        textInputConfirmPassword = findViewById(R.id.text_input_register_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
    }

    @Override
    public void onBackPressed() {
        if (alertUser()) {
            //warn user that he will lose his inputs if he goes back
            new AlertDialog.Builder(this)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.warning_text)
                    .setIcon(R.drawable.ic_warning)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        } else {
            super.onBackPressed();
        }
    }
}

