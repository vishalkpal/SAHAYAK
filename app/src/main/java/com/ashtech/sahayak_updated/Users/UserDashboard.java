package com.ashtech.sahayak_updated.Users;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ashtech.sahayak_updated.Databases.SessionManager;
import com.ashtech.sahayak_updated.Maps.MapsDashboardNavigation;
import com.ashtech.sahayak_updated.R;
import com.google.firebase.auth.FirebaseUser;
import com.karan.churi.PermissionManager.PermissionManager;

import java.util.HashMap;

public class UserDashboard extends AppCompatActivity {

    // mera yha se start ho rha h

    PermissionManager manager;
    FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_dashboard);
        TextView textView =findViewById(R.id.textView3);



            manager= new PermissionManager() {};
            manager.checkAndRequestPermissions(this);




        SessionManager sessionManager=new SessionManager(this,SessionManager.SESSION_USERSESSION);
        sessionManager.getUserDetailfromSession();
        HashMap<String,String > userDetails=sessionManager.getUserDetailfromSession();
        String fullName=userDetails.get(SessionManager.KEY_FULLNAME);
        String phoneNum=userDetails.get(SessionManager.KEY_PHONENUMBER);

        textView.setText(fullName+"\n"+phoneNum);
    }

    //mera vala


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        manager.checkResult(requestCode,permissions,grantResults);
//
//        ArrayList<String> denied_permission=manager.getStatus().get(0).denied;
//        if(denied_permission.isEmpty())
//        {
//            Toast.makeText(this, "Permission Enabled", Toast.LENGTH_SHORT).show();
//        }
//
//    }

    public void userMapDashboard(View view)
    {
        startActivity(new Intent(getApplicationContext(), MapsDashboardNavigation.class));
    }

}
