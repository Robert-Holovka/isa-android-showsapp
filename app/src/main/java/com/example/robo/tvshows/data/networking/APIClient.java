package com.example.robo.tvshows.data.networking;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import static android.content.Context.MODE_PRIVATE;

public class APIClient {

    public static final String BASE_URL = "https://api.infinum.academy/";
    public static ApiService apiService;
    private static String token = "";
    public static final String TOKEN_PREFS = "MyToken";
    public static final String TOKEN_KEY = "token";
    public static OkHttpClient client;



    public static ApiService getApiService(){
        if(apiService == null){
            initApiService();
        }
        return apiService;
    }

    private static OkHttpClient createOkHttpClient() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client =  new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    //Insert token
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("Authorization", token).build();
                        return chain.proceed(request);
                    }
                })
                .build();
        return client;
    }

    private static void initApiService() {
        apiService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .client(createOkHttpClient())
                .build()
                .create(ApiService.class);
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isUserLoggedIn(Context context){
        SharedPreferences prefs = context.getSharedPreferences(TOKEN_PREFS, MODE_PRIVATE);
        String tokenString = prefs.getString(TOKEN_KEY, null);
        if(tokenString != null){
            //User is already logged in, save token
            setToken(tokenString);
            return true;
        }
        return false;
    }

    public static void rememberUser(Context context, String token){
        SharedPreferences.Editor editor = context.getSharedPreferences(TOKEN_PREFS, MODE_PRIVATE).edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    public static void logOutUser(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(TOKEN_PREFS, MODE_PRIVATE).edit();
        editor.putString(TOKEN_KEY, null);
        editor.apply();
    }

    public static void setToken(String t){
        token = t;
    }

    public static String getFullImageUrl(String extension){
        return BASE_URL + extension;
    }

    public static void cancelAPICalls(){
        //Cancel all API calls
        client.dispatcher().cancelAll();
    }
}
