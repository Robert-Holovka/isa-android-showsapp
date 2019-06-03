package com.example.robo.tvshows.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.robo.tvshows.R;
import com.example.robo.tvshows.ui.show.TVShowActivity;
import com.example.robo.tvshows.data.networking.APIClient;
import com.example.robo.tvshows.ui.account.LoginActivity;

public class SplashActivity extends AppCompatActivity{

    //Constants
    private final int LOGO_ANIMATION_DURATION = 1000;
    private final int TEXT_ANIMATION_DURATION = 500;
    private final int NAVIGATE_FORWARD_DELAY = 2000;

    private ViewTreeObserver viewObserver;
    private ImageView animationLogo;
    private TextView animationText;
    private RelativeLayout root;
    private int rootHeight;
    private Context context;

    //Animators
    private ViewPropertyAnimator textAnimator;
    private ViewPropertyAnimator logoAnimator;
    private boolean isAnimationCanceled;
    private Handler navigateForward;

    @Override
    protected void onPause() {
        super.onPause();
        //On home button press, act same if back is pressed
        onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        context = this;
        initViews();
        isAnimationCanceled = false;

        //Fetch root height
        viewObserver.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                rootHeight = root.getMeasuredHeight();
                //Start animation after dimension is calculated
                startLogoAnimation();
            }
        });
    }

    private void startLogoAnimation(){

        //Set animation Logo on the top of screen
        animationLogo.setY(-(rootHeight/2));

        //Move animation logo to the center of the screen
        logoAnimator = animationLogo.animate()
                .translationY(0)
                .setInterpolator(new BounceInterpolator())
                .setDuration(LOGO_ANIMATION_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        if(!isAnimationCanceled){
                            startTextAnimation();
                        }
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        animationLogo.setVisibility(View.VISIBLE);
                    }
                });

                logoAnimator.start();
    }

    private void startTextAnimation(){

        //Scaling text from 0 to full size
        animationText.setScaleX(0);
        animationText.setScaleY(0);

        textAnimator = animationText.animate()
                .scaleX(1)
                .scaleY(1)
                .setDuration(TEXT_ANIMATION_DURATION)
                .setInterpolator(new OvershootInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        if(!isAnimationCanceled){
                            //After animation is finished, wait 2 seconds to redirect activity
                            navigateForward = new Handler();
                            navigateForward.postDelayed(new Runnable(){
                                @Override
                                public void run() {
                                        //Redirect activity
                                        //Check if user is already logged in (remember me option)
                                        if(APIClient.isUserLoggedIn(context)){
                                            startTVShowActivity();
                                        } else {
                                            startLoginActivity();
                                        }
                                        finish();

                                }
                            }, NAVIGATE_FORWARD_DELAY);
                        }
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        animationText.setVisibility(View.VISIBLE);
                    }
                });

                textAnimator.start();
    }

    public void startTVShowActivity() {
        Intent showsIntent = new Intent(this, TVShowActivity.class);
        startActivity(showsIntent);
    }

    public void startLoginActivity(){
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void initViews(){
        root = findViewById(R.id.animation_root);
        animationLogo = findViewById(R.id.animation_logo);
        animationText = findViewById(R.id.animation_text);
        viewObserver  = root.getViewTreeObserver();
    }

    @Override
    public void onBackPressed() {

        //Prevent on-end listeners to call other actions
        isAnimationCanceled = true;

        //Cancel all active animations
        if(logoAnimator != null){
            logoAnimator.cancel();
        }

        if(textAnimator != null){
            textAnimator.cancel();
        }
        //Cancel background tasks
        if(navigateForward != null){
            navigateForward.removeCallbacksAndMessages(null);
        }

        super.onBackPressed();
    }
}
