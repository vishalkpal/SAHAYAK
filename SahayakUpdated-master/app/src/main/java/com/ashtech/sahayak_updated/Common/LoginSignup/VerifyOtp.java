package com.ashtech.sahayak_updated.Common.LoginSignup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ashtech.sahayak_updated.Databases.UserHelperClass;
import com.ashtech.sahayak_updated.R;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class VerifyOtp extends AppCompatActivity {
    PinView pinFromUser;
    String codebySystem;
    String Sname, Susername, Spassword, Semail, SphoneNum;

    TextView otpsent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);



        //hooks
        pinFromUser = findViewById(R.id.pin_view);

        Sname = getIntent().getStringExtra("fullname");
        Susername = getIntent().getStringExtra("username");
        Spassword = getIntent().getStringExtra("password");
        Semail = getIntent().getStringExtra("email");
        SphoneNum = getIntent().getStringExtra("phoneNum");
        sendVerificationCodeToUser(SphoneNum);

        otpsent=findViewById(R.id.otpsentmobTxt);
        otpsent.setText("Enter OTP sent on\n"+SphoneNum);

    }

    private void sendVerificationCodeToUser(String phoneno) {


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneno,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codebySystem = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        pinFromUser.setText(code);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {

                    Toast.makeText(VerifyOtp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };

    private void verifyCode(String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codebySystem, code);
        signinUsingCredential(credential);
    }

    private void signinUsingCredential(PhoneAuthCredential credential) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            storeNewUserData();
//  Dikkat aa rhi h
//                            if (Swhattodo.equals("1")) {
//                                updatePassword();
//                            } else {
//                                storeNewUserData();
//                            }

                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(VerifyOtp.this, "Verification not done", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }

    private void updatePassword() {

        Intent intent = new Intent(getApplicationContext(), SetNewPassword.class);
        intent.putExtra("phoneNum", SphoneNum);
        startActivity(intent);
        finish();

    }

    private void storeNewUserData() {
        FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
        DatabaseReference ref = rootnode.getReference("Users");
        String code= generateCode();


        UserHelperClass addNewUser = new UserHelperClass(Sname, Susername, Spassword, Semail, SphoneNum,"NA","NA",false);

        ref.child(SphoneNum).setValue(addNewUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(VerifyOtp.this, "Verification Done and Registration is Successfull", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(VerifyOtp.this,StartUpScreen.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(VerifyOtp.this, "Could not able to register", Toast.LENGTH_SHORT).show();

                }
            }
        });



    }

    private String generateCode() {
        Random r =new Random();
        int n=100000+r.nextInt(900000);
        String code= String.valueOf(n);
        return code;
    }


    public void callnxtscreenfromOTP(View view) {
        String code = pinFromUser.getText().toString();
        if (!code.isEmpty())
            verifyCode(code);
        else {
            Toast.makeText(this, "Field can't be empty", Toast.LENGTH_SHORT).show();
        }
    }
}
