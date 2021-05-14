package com.ashtech.sahayak_updated.Common.LoginSignup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.ashtech.sahayak_updated.HelperClass.CheckInternet;
import com.ashtech.sahayak_updated.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetNewPassword extends AppCompatActivity {
    TextInputLayout newPassword,confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password);


        //Hooks
        newPassword.findViewById(R.id.newPassword);
        confirmPassword.findViewById(R.id.confirmPassword);


    }
    public void UpadatePassword(View view)
    {
        //check Internet
        CheckInternet checkInternet=new CheckInternet();
        if(!checkInternet.isConnected(this))
        {
            showCustomDialog();
            return;
        }

        //validate password and confirm password

        if(!validatePassword())
        {
            return;
        }



        // get data from fields
        String _newPassword= newPassword.getEditText().getText().toString().trim();
        String _confirmPassword= confirmPassword.getEditText().getText().toString().trim();
        String _phoneNum=getIntent().getStringExtra("phoneNum");

        //Update data in FireBase and in Session
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child(_phoneNum).child("password").setValue(_newPassword);


        startActivity(new Intent(getApplicationContext(),ForgetPasswordSuccessMsg.class));
        finish();
    }

    private boolean validatePassword() {
        String _newPassword= newPassword.getEditText().getText().toString().trim();
        String _confirmPassword= confirmPassword.getEditText().getText().toString().trim();


        if(_newPassword.equals(_confirmPassword))
        {
            confirmPassword.setError(null);
            confirmPassword.setErrorEnabled(false);
            return true;
        }
        else
        {
            confirmPassword.setError("Password does not match ");
            return false;
        }



    }

    private void showCustomDialog() {

        AlertDialog.Builder builder= new AlertDialog.Builder(SetNewPassword.this);
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
