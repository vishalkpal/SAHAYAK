package com.ashtech.sahayak_updated.Common.LoginSignup;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.ashtech.sahayak_updated.R;

public class StartUpScreen extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start_up_screen);
    }

    public void callSignUpScreen(View view)
    {
        Intent intent =new Intent(getApplicationContext(),SignUp.class);

        Pair[] pairs= new Pair[1];
        pairs[0]=new Pair<View,String>(findViewById(R.id.strtup_login_btn),"trans_sgn_btn");

        ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(StartUpScreen.this,pairs);
        startActivity(intent,options.toBundle());

    }

    public void callLoginScreen(View view)
    {
        Intent intent =new Intent(getApplicationContext(),Login.class);
        Pair[] pairs= new Pair[1];
        pairs[0]=new Pair<View,String>(findViewById(R.id.strtup_login_btn),"trans_lgn_btn");

        ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(StartUpScreen.this,pairs);



        startActivity(intent,options.toBundle());
    }
}
