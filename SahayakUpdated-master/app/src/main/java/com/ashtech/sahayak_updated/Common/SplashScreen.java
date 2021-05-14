package com.ashtech.sahayak_updated.Common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashtech.sahayak_updated.Common.LoginSignup.StartUpScreen;
import com.ashtech.sahayak_updated.R;
import com.ashtech.sahayak_updated.Users.UserDashboard;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIMER=5000;

    ImageView logoImage;
    TextView logoname,cname,poweredname;
    SharedPreferences onBoardingScreen;

    //Animation
    Animation topanim,bottomanim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);

        //HOOKS

        logoImage=(ImageView)findViewById(R.id.splash_logo);
        logoname=(TextView)findViewById(R.id.LogoName);
        cname=(TextView)findViewById(R.id.AshTex);
        poweredname=(TextView)findViewById(R.id.PoweredBy);

        topanim= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomanim= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        //set Animation on element
        logoImage.setAnimation(topanim);
        logoname.setAnimation(topanim);
        cname.setAnimation(bottomanim);
        poweredname.setAnimation(bottomanim);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                onBoardingScreen= getSharedPreferences("onBoardingScreen",MODE_PRIVATE);

                boolean isFirsttime=onBoardingScreen.getBoolean("firstTime",true);
                if(isFirsttime)
                {
                    SharedPreferences.Editor editor=onBoardingScreen.edit();
                    editor.putBoolean("firstTime",false);
                    editor.commit();
                    Intent userintent=new Intent(SplashScreen.this, Onboarding.class);
                    startActivity(userintent);
                    finish();
                }
                else
                {
                    Intent userintent=new Intent(SplashScreen.this, StartUpScreen.class);
                    startActivity(userintent);
                    finish();

                }



            }
        },SPLASH_TIMER);
    }
}
