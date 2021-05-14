package com.ashtech.sahayak_updated.Common.LoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.ashtech.sahayak_updated.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.Queue;

public class ForgetPassword extends AppCompatActivity {

    TextInputLayout phoneNum;
    CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);


        //Hooks
        phoneNum=findViewById(R.id.forgetMobNum);
        ccp=findViewById(R.id.ForgetPass_country_Code_picker);




    }
    public void verfyPhoneNum(View view)
    {
        //check Internet Connection
        if(!isConnected(this)){
            showCustomDialog();
        }

        String SphoneNum = phoneNum.getEditText().getText().toString().trim();
        if (SphoneNum.charAt(0) == 0) {
            SphoneNum = SphoneNum.substring(1);
        }

        final String completePhoneNum = "+" + ccp.getFullNumber() + SphoneNum;

        Query  checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNum").equalTo(completePhoneNum);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    phoneNum.setError(null);
                    phoneNum.setErrorEnabled(false);

                    Intent intent = new Intent(getApplicationContext(),VerifyOtpForgetPassword.class);
                    intent.putExtra("phoneNum",completePhoneNum);

                    startActivity(intent);
                    finish();
                }
                else
                {
                    phoneNum.setError("No Such User Exist");
                    phoneNum.requestFocus();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ForgetPassword.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



    }
    private boolean isConnected(ForgetPassword forgetpassword) {

        ConnectivityManager connectivityManager= (ConnectivityManager) forgetpassword.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn= connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo dataConn= connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        if((wifiConn!=null&&( wifiConn.isConnected())) || (dataConn!=null && (dataConn.isConnected())))
        {
            return true;
        }
        else
            return false;
    }
    private void showCustomDialog() {

        AlertDialog.Builder builder= new AlertDialog.Builder(ForgetPassword.this);
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



}
