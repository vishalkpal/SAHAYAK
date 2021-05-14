package com.ashtech.sahayak_updated;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ashtech.sahayak_updated.Databases.SessionManager;


public class trial extends AppCompatActivity {

  private static final int REQUEST_CODE_LOCATION_PERMISSION=1;

    String latitude;
    String longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial_trial);

        findViewById(R.id.btnstartLocationUpdates).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                )!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            trial.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION
                    );
                } else {
                    startLocationService();
                }
            }
        });

        findViewById(R.id.btnstopLocationUpdates).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationService();
            }
        });


        // reciever
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        latitude = intent.getStringExtra(LocationServices.EXTRA_LATITUDE);
                        longitude = intent.getStringExtra(LocationServices.EXTRA_LONGITUDE);

                        if (latitude != null && longitude != null) {
                            Log.d("onReceive:",latitude+", log"+longitude);
                        }
                    }
                }, new IntentFilter(LocationServices.ACTION_LOCATION_BROADCAST)
        );
        SessionManager sessionManager=new SessionManager(this,SessionManager.SESSION_LOCATION);
        sessionManager.createLocationSession(latitude,longitude);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE_LOCATION_PERMISSION&& grantResults.length>0){
            startLocationService();;
        }else
        {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isLocationServiceRunning()
    {
        ActivityManager activityManager=(ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager!=null)
        {
            for(ActivityManager.RunningServiceInfo service:activityManager.getRunningServices(Integer.MAX_VALUE)){
                if(LocationServices.class.getName().equals(service.service.getClassName())){
                    if(service.foreground)
                    {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }
    private void startLocationService(){
        SessionManager sessionManager=new SessionManager(this,SessionManager.SESSION_USERSESSION);

        String phoneNum=sessionManager.getUserDetailfromSession().get(SessionManager.KEY_PHONENUMBER);
        if(!isLocationServiceRunning()){
            Intent intent= new Intent(getApplicationContext(),LocationServices.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);

            intent.putExtra("phoneNum",phoneNum);
            startService(intent);
            Toast.makeText(this, "Location service started", Toast.LENGTH_SHORT).show();
        }

    }
    private void stopLocationService(){
        if(isLocationServiceRunning()){
            Intent intent=new Intent(getApplicationContext(),LocationServices.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }
}
