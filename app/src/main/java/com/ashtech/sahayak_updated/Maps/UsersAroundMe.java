package com.ashtech.sahayak_updated.Maps;
//Customer Activity

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.ashtech.sahayak_updated.Databases.SessionManager;
import com.ashtech.sahayak_updated.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsersAroundMe extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private SupportMapFragment mapFragment;

    private FusedLocationProviderClient mFusedLocationClient;

    Button te;

    LatLng pickuplocation;
    Button mRequest;
    String phoneNum;

    int distance=1;
    final static int LIMIT=3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_around_me);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        phoneNum = sessionManager.getUserDetailfromSession().get(SessionManager.KEY_PHONENUMBER);
        te=findViewById(R.id.useraroundmetext);
        mRequest=findViewById(R.id.requestbtn);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }else{
                checkLocationPermission();
            }
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    mLastLocation = location;
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                    if (!getDriversAroundStarted)
                        getDriversAround();
                }
            }
        }
    };



    public void RequestAllLocation(final View v) {
// uploading the pickup location on the Firebase
        DatabaseReference userslocationrefernce = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(userslocationrefernce);
        geoFire.setLocation(phoneNum, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });
// putting the marker on the map
        pickuplocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(pickuplocation).title("help").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));

        mRequest.setText("Finding near By helper...");

        getClosestUser();
    }
    // near me users
    private int radius = 1;
    private Boolean userfound = false;
    private String userfoundID;

    GeoQuery geoQuery;
    private void getClosestUser()
    {
        //driver available
        DatabaseReference LocationRef= FirebaseDatabase.getInstance().getReference("driverAvailable");
        GeoFire geoFire= new GeoFire(LocationRef);
        //center with the radius
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickuplocation.latitude, pickuplocation.longitude), radius);
        geoQuery.removeAllListeners();


        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!userfound) {
                    userfound = true;
                    userfoundID = key;

                    DatabaseReference helperRef=FirebaseDatabase.getInstance().getReference().child("Users").child(userfoundID).child("customerRequest");
                    String customerId=phoneNum;

                    HashMap map = new HashMap();
                    map.put("customerRideId", customerId);
                    helperRef.updateChildren(map);

                    mRequest.setText("Looking For User's Location");
                    getUserLocation();
                    mRequest.setText("Looking for Driver Location....");


                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!userfound) {
                    radius++;
                    getClosestUser();
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });


    }
    private Marker mDriverMarker;
    private DatabaseReference userLocationRef;
    private ValueEventListener userLocationRefListener;
    private void getUserLocation(){

        userLocationRef = FirebaseDatabase.getInstance().getReference().child("driverWorking").child(userfoundID).child("l");
        userLocationRefListener = userLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatLng = new LatLng(locationLat,locationLng);
                    if(mDriverMarker != null){
                        mDriverMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickuplocation.latitude);
                    loc1.setLongitude(pickuplocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);

                    if (distance<100){
                        mRequest.setText("Driver's Here");
                    }else{
                        mRequest.setText("Driver Found: " + String.valueOf(distance));
                    }



                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("your driver").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }



            @Override
    protected void onStop() {
        super.onStop();


    }



    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(UsersAroundMe.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(UsersAroundMe.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else{
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

//    List<Marker> userMarkers=new ArrayList<Marker>();
//    private  void  getAroundUser()
//    {
//
//        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driverAvailable");
//        GeoFire geoFire = new GeoFire(driverLocation);
//        geoFire.getLocation();
//
//    }




    boolean getDriversAroundStarted = false;
    List<Marker> markers = new ArrayList<Marker>();
    private void getDriversAround(){
        getDriversAroundStarted = true;
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driverAvailable");

        GeoFire gFire = new GeoFire(driverLocation);
        GeoQuery gQuery = gFire.queryAtLocation(new GeoLocation(mLastLocation.getLongitude(), mLastLocation.getLatitude()), 1000);
        gQuery.removeAllListeners();
        te.setText("enter...");
        gQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                for(Marker markerIt : markers) {
                    if (markerIt.getTag().equals(key)) {
                        te.setText("ifKeyEntered");
                        return;
                    }

                }
                    LatLng driverLocation = new LatLng(location.latitude, location.longitude);

                    Marker mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLocation).title(key).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));
                    mDriverMarker.setTag(key);
                    te.setText("markeradded");
                    markers.add(mDriverMarker);


            }

            @Override
            public void onKeyExited(String key) {
                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key)){
                        markerIt.remove();
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key)){

                        te.setText("moved");
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {

//                if(distance<=LIMIT)
//                {
//                    distance++;
//                    getDriversAround();
//                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
}
