package com.ashtech.sahayak_updated.Users;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.ashtech.sahayak_updated.Databases.SessionManager;
import com.ashtech.sahayak_updated.R;

import java.util.HashMap;

public class MyProfile extends AppCompatActivity {


    EditText name,username,pass,mobno,Eemail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        SessionManager sessionManager=new SessionManager(this,SessionManager.SESSION_USERSESSION);
        sessionManager.getUserDetailfromSession();
        HashMap<String,String > userDetails=sessionManager.getUserDetailfromSession();
        String fullName=userDetails.get(SessionManager.KEY_FULLNAME);
        String phoneNum=userDetails.get(SessionManager.KEY_PHONENUMBER);
        String password=userDetails.get(SessionManager.KEY_PASSWORD);
        String email=userDetails.get(SessionManager.KEY_EMAIL);
        String userName=userDetails.get(SessionManager.KEY_USERNAME);

        name=findViewById(R.id.profileName);
        username=findViewById(R.id.profileMobNo);
        Eemail=findViewById(R.id.profileEmail);
        mobno=findViewById(R.id.profileUserName);
        pass=findViewById(R.id.profilePassword);

        name.setText(fullName);
        mobno.setText(phoneNum);
        Eemail.setText(email);
        username.setText(userName);
        pass.setText(password);


    }
}