package com.ashtech.sahayak_updated.Common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashtech.sahayak_updated.Common.LoginSignup.StartUpScreen;
import com.ashtech.sahayak_updated.HelperClass.SliderAdapterClass;
import com.ashtech.sahayak_updated.R;
import com.ashtech.sahayak_updated.Users.UserDashboard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karan.churi.PermissionManager.PermissionManager;

import java.security.Permission;
import java.util.ArrayList;

public class Onboarding extends AppCompatActivity {
    ViewPager viewPager;
    LinearLayout dotslayout;
    TextView[] dots;
    Button getstrtbtn;
    Animation animation;

    int currentPos;
    SliderAdapterClass sliderAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Hooks

        viewPager=(ViewPager)findViewById(R.id.sliderpager);
        dotslayout=(LinearLayout)findViewById(R.id.slider_dots);
        getstrtbtn=(Button)findViewById(R.id.letsGetStartedbtn);



        // Call adapter
        sliderAdapter =new SliderAdapterClass(this);
        viewPager.setAdapter(sliderAdapter);
        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);



    }



    public void DirDashboard(View view)
    {
        Intent intent=new Intent( Onboarding.this, StartUpScreen.class);
        startActivity(intent);
        finish();
    }

    public  void next(View view)
    {
        viewPager.setCurrentItem(currentPos+1);
    }

    private void addDots(int pos)
    {
        dots=new TextView[3];
        dotslayout.removeAllViews();
        for(int i=0;i<3;i++)
        {
            dots[i]=new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dotslayout.addView(dots[i]);
        }
        if(dots.length>0)
        {
            dots[pos].setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }
    ViewPager.OnPageChangeListener changeListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            currentPos=position;
            if(position==0||position==1)
                getstrtbtn.setVisibility(View.INVISIBLE);
            else
            {   animation= AnimationUtils.loadAnimation(Onboarding.this,R.anim.bottom_animation);
                animation.setDuration(500);
                getstrtbtn.setAnimation(animation);
                getstrtbtn.setVisibility(View.VISIBLE);

            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
