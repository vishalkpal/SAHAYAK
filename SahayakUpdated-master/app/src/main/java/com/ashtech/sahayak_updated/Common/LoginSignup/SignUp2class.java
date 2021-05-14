package com.ashtech.sahayak_updated.Common.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;

import com.ashtech.sahayak_updated.R;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

public class SignUp2class extends AppCompatActivity {

    ScrollView scrollView;
    TextInputLayout phoneNum;
    CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lgnsgn_sign_up2class);



        // hooks
        scrollView =findViewById(R.id.signup2_screen_scroll_view);
        ccp=findViewById(R.id.country_Code_picker);
        phoneNum=findViewById(R.id.signup_phonenum);

    }
    public void callverifyOTP(View view)
    {
        String Sname=getIntent().getStringExtra("fullname");
        String Susername=getIntent().getStringExtra("username");
        String Semail=getIntent().getStringExtra("email");
        String Spassword=getIntent().getStringExtra("password");
        String userEnteredPhoneNo=phoneNum.getEditText().getText().toString().trim();
        String  SphoneNum= "+"+ccp.getFullNumber()+userEnteredPhoneNo;

        Intent intent =new Intent(getApplicationContext(),VerifyOtp.class);

        intent.putExtra("fullname",Sname);
        intent.putExtra("username",Susername);
        intent.putExtra("password",Spassword);
        intent.putExtra("email",Semail);
        intent.putExtra("phoneNum",SphoneNum);

        startActivity(intent);
        finish();






    }
}
