package com.example.robo.tvshows.ui.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import com.example.robo.tvshows.R;
import com.example.robo.tvshows.ui.BaseActivity;
import com.example.robo.tvshows.ui.show.TVShowActivity;
import com.example.robo.tvshows.data.models.DataWrapper;
import com.example.robo.tvshows.data.models.Token;
import com.example.robo.tvshows.data.models.User;
import com.example.robo.tvshows.data.networking.APIClient;
import com.example.robo.tvshows.data.networking.ApiService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements IValidation, ICancellation {

    //Views
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private Button btnLogin;
    private CheckBox boxRememberMe;

    //Other
    private ApiService apiService;
    private Context context;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;
        initViews();

        //fetch api client
        apiService = APIClient.getApiService();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //this activity is now visible, enable btn login again (preventing double click)
        btnLogin.setEnabled(true);
    }

    public void createNewAccountListener(View v){
        //Call register activity to create new account
        Intent registerIntent = RegisterActivity.buildRegisterActivity(getApplicationContext(), this, this);
        startActivity(registerIntent);
    }

    public void loginButtonListener(View v){

        //Prevent double click
        btnLogin.setEnabled(false);

        boolean isEmailValid = validateEmail(textInputEmail);
        boolean isPasswordValid =  validatePassword(textInputPassword);

        if(!isEmailValid ||!isPasswordValid){
            //show errors, do nothing, enable login button again
            btnLogin.setEnabled(true);
            return;
        }

        if(APIClient.isInternetAvailable(this)){

            String email = textInputEmail.getEditText().getText().toString().trim();
            String password = textInputPassword.getEditText().getText().toString().trim();

            User user = new User(email, password);

            Call<DataWrapper<Token>> userLogin = apiService.login(user);
            //Show loading dialog
            showProgress();

            //Pass user login info to api
            userLogin.enqueue(new Callback<DataWrapper<Token>>() {
                @Override
                public void onResponse(Call<DataWrapper<Token>> call, Response<DataWrapper<Token>> response) {
                    if(response.isSuccessful()){

                        String token = response.body().getData().getToken();

                        //Save token in APIClient
                        APIClient.setToken(token);
                        //Remember me, save token to shared preferences
                        if(boxRememberMe.isChecked()){
                            APIClient.rememberUser(context, token);
                        }

                        hideProgress();
                        //If login is successful redirect to the tv shows list
                        startTVShowActivity();
                        //Kill this activity
                        finish();

                    } else {
                        hideProgress();
                        //Wrong email or password
                        textInputEmail.setError(getResources().getString(R.string.check_email));
                        textInputPassword.setError(getResources().getString(R.string.check_password));
                        btnLogin.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<DataWrapper<Token>> call, Throwable t) {
                    hideProgress();

                    if(!call.isCanceled()){
                        //If user didn't cancel API call, show error
                        showError();
                    }

                    btnLogin.setEnabled(true);
                }
            });

        } else {
            Toast.makeText(this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            btnLogin.setEnabled(true);
        }
    }

    @Override
    public boolean validateEmail(TextInputLayout emailInput) {

        //fetch user input in email field
        String email = emailInput.getEditText().getText().toString().trim();

        //Declare valid email pattern word@word.(word 2-4 chars length)
        String validEmailPattern = "^[A-Z0-9._+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(validEmailPattern, Pattern.CASE_INSENSITIVE);
        //Match entered email with valid email pattern
        Matcher matcher = pattern.matcher(email);

        //If email does not match pattern, set error message
        if(!matcher.matches()){
            emailInput.setError(getResources().getString(R.string.invalid_email));
            return false;
        } else {
            //remove error message
            emailInput.setError(null);
            return true;
        }
    }

    @Override
    public boolean validatePassword(TextInputLayout passwordInput) {

        //fetch user input in password field
        String password = passwordInput.getEditText().getText().toString().trim();

        if(password.length() < 5){
            //set error message
            passwordInput.setError(getResources().getString(R.string.invalid_password));
            return false;
        } else {
            //remove error message
            passwordInput.setError(null);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
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

    public void startTVShowActivity() {
        Intent showsIntent = new Intent(this, TVShowActivity.class);
        startActivity(showsIntent);
    }

    @Override
    public void killLoginActivity() {
        //Kill this activity from register or tv shows activity
        finish();
    }

    private void initViews(){
        textInputEmail = findViewById(R.id.text_input_login_email);
        textInputPassword = findViewById(R.id.text_input_login_password);
        boxRememberMe = findViewById(R.id.checkbox_remember_me);
        btnLogin = findViewById(R.id.btn_login);
    }
}

interface IValidation {
    boolean validateEmail(TextInputLayout emailInput);
    boolean validatePassword(TextInputLayout passwordInput);
}

interface ICancellation {
    void killLoginActivity();
}