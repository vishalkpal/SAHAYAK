package com.ashtech.sahayak_updated.Common.LoginSignup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ashtech.sahayak_updated.Databases.SessionManager;
import com.ashtech.sahayak_updated.Maps.UserLocationMainActivity;
import com.ashtech.sahayak_updated.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;


public class Login extends AppCompatActivity {
    CountryCodePicker ccp;
    TextInputLayout phoneNum, password;
    TextInputEditText mobnoText,passText;
    CheckBox remberme;

    @Override
    protected void onStart() {
        super.onStart();
        SessionManager sessionManager=new SessionManager(this,SessionManager.SESSION_USERSESSION);


        if(sessionManager.checkLogin())
        {
            startActivity(new Intent(Login.this, UserLocationMainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_lgnsgn_login);
        //Hooks
        ccp = findViewById(R.id.lgnin_country_Code_picker);
        phoneNum = findViewById(R.id.lgnin_phonenum);
        password = findViewById(R.id.lgnin_password);
        remberme=findViewById(R.id.remberMe);
        mobnoText=findViewById(R.id.lgnin_phonenumText);
        passText=findViewById(R.id.lgnin_passwordText);




        SessionManager sessionManager=new SessionManager(Login.this,SessionManager.SESSION_REMEMBERME);
        if(sessionManager.checkRememberMe())
        {
            HashMap<String,String> rememberMeDetails=sessionManager.getRememberMeDetailfromSession();
            mobnoText.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONPHONENUMBER));
            passText.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONPASSWORD));
        }
    }

    public void letTheUserLoggedIn(View view) {

        if(!isConnected(this)){
            showCustomDialog();
        }


        //get data
        String SphoneNum = phoneNum.getEditText().getText().toString().trim();
        final String Spassword = password.getEditText().getText().toString().trim();

        if (SphoneNum.charAt(0) == 0) {
            SphoneNum = SphoneNum.substring(1);
        }

        final String completePhoneNum = "+" + ccp.getFullNumber() + SphoneNum;

        if(remberme.isChecked()){
            SessionManager sessionManager=new SessionManager(Login.this,SessionManager.SESSION_REMEMBERME);
            sessionManager.createRememberMeSession(Spassword,SphoneNum);


        }



        //Databases
        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNum").equalTo(completePhoneNum);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    phoneNum.setErrorEnabled(false);
                    phoneNum.setError(null);

                    String systemPassword = dataSnapshot.child(completePhoneNum).child("password").getValue(String.class);


                    if (systemPassword.equals(Spassword)) {
                        password.setError(null);
                        password.setErrorEnabled(false);

                        // Get Users data from the fireBase database

                        String _fullname= dataSnapshot.child(completePhoneNum).child("fullname").getValue(String.class);
                        String _username= dataSnapshot.child(completePhoneNum).child("username").getValue(String.class);
                        String _password= dataSnapshot.child(completePhoneNum).child("password").getValue(String.class);
                        String _email= dataSnapshot.child(completePhoneNum).child("email").getValue(String.class);
                        String _phoneNum= dataSnapshot.child(completePhoneNum).child("phoneNum").getValue(String.class);

                        //create a Session

                        SessionManager sessionManager=new SessionManager(Login.this,SessionManager.SESSION_USERSESSION);
                        sessionManager.createLoginSession(_fullname,_username,_password,_phoneNum,_email);
                        startActivity(new Intent(getApplicationContext(), UserLocationMainActivity.class));

                    } else {
                        Toast.makeText(Login.this, "Password does not match!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login.this, "No such User exist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Login.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void showCustomDialog() {

        AlertDialog.Builder builder= new AlertDialog.Builder(Login.this);
        builder.setMessage("Please connect to the Internet to proceed further").setCancelable(false).setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(getApplicationContext(),StartUpScreen.class));
                finish();
            }
        });
    }

    private boolean isConnected(Login login) {

        ConnectivityManager connectivityManager= (ConnectivityManager) login.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn= connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo dataConn= connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        if((wifiConn!=null&&( wifiConn.isConnected())) || (dataConn!=null && (dataConn.isConnected())))
        {
            return true;
        }
        else
            return false;
    }

    public void  backbtn(View v)
    {
        Intent intent = new Intent(Login.this, StartUpScreen.class);
        startActivity(intent);
    }
    public void callForgetPassword(View view) {
        Intent intent = new Intent(Login.this, ForgetPassword.class);
        startActivity(intent);
    }
}
