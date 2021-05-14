package com.ashtech.sahayak_updated;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ashtech.sahayak_updated.Databases.SessionManager;
import com.ashtech.sahayak_updated.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static android.content.Intent.getIntent;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class LocationServices extends Service  {
    DatabaseReference databaseReference;
    String mob;
    public static final String ACTION_LOCATION_BROADCAST = LocationServices.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";

    private LocationCallback locationCallback=new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if(locationResult != null && locationResult.getLastLocation()!=null)
            {


                // Current Location
                double latitude=locationResult.getLastLocation().getLatitude();
                double longitude=locationResult.getLastLocation().getLongitude();
                String lat= Double.toString(latitude);
                String log= Double.toString(longitude);
                Log.d( "onLocation ",latitude+","+longitude+","+mob);
//
//                Map<String,String> locationobj=new HashMap<String,String>();
//                locationobj.put("lat",Double.toString(latitude) );
//                locationobj.put("log",Double.toString(longitude) );
                databaseReference=FirebaseDatabase.getInstance().getReference().child("Users").child(mob);
                databaseReference.child("lat").setValue(lat);
                databaseReference.child("log").setValue(log);

                //Geo Fire
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserLocations").child(mob);
                GeoFire geoFire = new GeoFire(ref);

                geoFire.setLocation("location", new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                    }
                });


                sendMessageToUI(lat,log);




            }
        }
    }   ;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet Implemented");
    }

    private void startLocationService()
    {
        String channelId="locatoin_notification_channel";
        NotificationManager notificationManager= (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent=new Intent();
        PendingIntent pendingIntent=PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationCompat.Builder builder= new NotificationCompat.Builder(
                getApplicationContext(),
                channelId
        );
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentText("Location Service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            if(notificationManager!=null&& notificationManager.getNotificationChannel(channelId)==null){
                NotificationChannel notificationChannel=new NotificationChannel(
                        channelId,
                        "Location Service",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        LocationRequest locationRequest= new LocationRequest();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());
        startForeground(Constants.LOCATION_SERVICE_ID,builder.build());
    }
    private void stopLocationService(){
        com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null)
        {
            String action=intent.getAction();
             mob=intent.getStringExtra("phoneNum");
            if(action.equals(Constants.ACTION_START_LOCATION_SERVICE))
            {
                startLocationService();
            }
            else if(action.equals(Constants.ACTION_STOP_LOCATION_SERVICE)){
                stopLocationService();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

//    public void onLocationChanged(Location location) {
//        if (location != null) {
//            Log.d(TAG, "== location != null");
//
//            //Send result to activities
//            sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
//        }
//
//    }

    private void sendMessageToUI(String lat, String lng) {
        Log.d(TAG, "Sending info...");

        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
























