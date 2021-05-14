package com.ashtech.sahayak_updated.Common.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashtech.sahayak_updated.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignUp extends AppCompatActivity {
    ImageView backbtn;
    Button next;
    TextView titletext;

    //Get data variable
    TextInputLayout fullname, username, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lgnsgn_sign_up);

        //HOOKS

        backbtn = (ImageView) findViewById(R.id.signup_backbtn);
        next = (Button) findViewById(R.id.signup_next_btn);
        titletext = (TextView) findViewById(R.id.signup_title);

        fullname = findViewById(R.id.sgnupName);
        username = findViewById(R.id.sgnupUserName);
        password = findViewById(R.id.sgnupPassword);
        email = findViewById(R.id.sgnupEmail);

    }

    public void callNextscreen(View view) {
        if (!validatefullname() | !validateusername() | !validateemail() | !validatepassword()) {
            return;
        }
        String Sname = fullname.getEditText().getText().toString().trim();
        String Susername = username.getEditText().getText().toString().trim();
        String Spassword = password.getEditText().getText().toString().trim();
        String Semail = email.getEditText().getText().toString().trim();

        Intent intent = new Intent(getApplicationContext(), SignUp2class.class);
        Pair[] pairs = new Pair[3];
        pairs[0] = new Pair<View, String>(backbtn, "back_btn_Transition");
        pairs[1] = new Pair<View, String>(next, "next_btn_Transition");
        pairs[2] = new Pair<View, String>(titletext, "title_Transition");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp.this, pairs);
        intent.putExtra("fullname", Sname);
        intent.putExtra("username", Susername);
        intent.putExtra("password", Spassword);
        intent.putExtra("email", Semail);
        intent.putExtra("whattodo","0");

        startActivity(intent, options.toBundle());

    }

    private boolean validatefullname() {
        String val = fullname.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            fullname.setError("Field can not be empty");
            return false;
        } else {
            fullname.setError(null);
            fullname.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateusername() {
        String val = username.getEditText().getText().toString().trim();
        String checkspaces = "\\A\\w{1,20}\\z";
        if (val.isEmpty()) {
            username.setError("Field can not be empty");
            return false;
        } else if (val.length() > 20) {
            username.setError("UserName is too large!");
            return false;
        } else if (!val.matches(checkspaces)) {
            username.setError("UserName cannot contain WhiteSpace");
            return false;
        } else {
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatepassword() {
        String val = password.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            password.setError("Field can not be empty");
            return false;
        } else if (val.length() > 20 || val.length() < 6) {
            password.setError("UserName is too large!");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateemail() {
        String val = email.getEditText().getText().toString().trim();
        String checkemail = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        //"[a-zA-Z0-9]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            email.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkemail)) {
            email.setError("Invalid Email");
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }
}
