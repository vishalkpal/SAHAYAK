package com.ashtech.sahayak_updated.Common.LoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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

import java.util.concurrent.TimeUnit;

public class VerifyOtpForgetPassword extends AppCompatActivity {

    PinView pinFromUser;
    String codebySystem;
    String  SphoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp_forget_password);
        //hooks
        pinFromUser = findViewById(R.id.pin_view_forgetPass);


        SphoneNum = getIntent().getStringExtra("phoneNum");
        sendVerificationCodeToUser(SphoneNum);

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

                    Toast.makeText(VerifyOtpForgetPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
// update password calling Set New Password Screen
                            Intent intent = new Intent(VerifyOtpForgetPassword.this, SetNewPassword.class);
                            intent.putExtra("phoneNum", SphoneNum);
                            startActivity(intent);
                            finish();


                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(VerifyOtpForgetPassword.this, "Verification not done", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }





    public void callSetNewPasswordfromOTP(View view) {
        String code = pinFromUser.getText().toString();
        if (!code.isEmpty())
            verifyCode(code);
        else {
            Toast.makeText(this, "Field can't be empty", Toast.LENGTH_SHORT).show();
        }
    }
}

