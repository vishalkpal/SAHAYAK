package com.ashtech.sahayak_updated.Maps;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.ashtech.sahayak_updated.Databases.SessionManager;
import com.ashtech.sahayak_updated.HelperClass.IOnLoadLocationListner;
import com.ashtech.sahayak_updated.HelperClass.MyLatLng;
import com.ashtech.sahayak_updated.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoQuery;
import com.github.kmenager.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private SupportMapFragment mapFragment;
    LatLngBounds.Builder builder;

    private FusedLocationProviderClient mFusedLocationClient;

    int t = 1;
    int fst=1;

    private HashMap<String, Marker> mMarkers = new HashMap<>();
    String phoneNum;

    String grpcode;

    //Presence System
    String destination;
    LatLng destinationLatLng;
    String name;
    MaterialAnimatedSwitch location_switch;

    //Marking
    List<MyLatLng> latlnglist;
    Marker currentUser;
    ArrayList<LatLng> dangerousArea;
    Location lastLocation;
    private GeoQuery geoQuery;

    DatabaseReference myLocationref, mycityref;
    GeoFire geoFireLocation;
    IOnLoadLocationListner iOnLoadLocationListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        phoneNum = sessionManager.getUserDetailfromSession().get(SessionManager.KEY_PHONENUMBER);
        name=sessionManager.getUserDetailfromSession().get(SessionManager.KEY_USERNAME);

        grpcode = getIntent().getStringExtra("code");
        destinationLatLng = new LatLng(0.0, 0.0);


        location_switch = (MaterialAnimatedSwitch) findViewById(R.id.locationSwitch);
        location_switch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean isOnline) {
                if (isOnline) {
                    connectUserToGroup();
                    Snackbar.make(mapFragment.getView(), "You are online", Snackbar.LENGTH_SHORT).show();
                } else {
                    disconnectUserFromGroup();
                    Snackbar.make(mapFragment.getView(), "You are offline", Snackbar.LENGTH_SHORT).show();

                }
            }
        });


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(4000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            } else {
                checkLocationPermission();
            }
        }

    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    mLastLocation = location;
                    HashMap<String, Object> value = new HashMap<String, Object>();
                    value.put("latitude", location.getLatitude());
                    value.put("longitude", location.getLongitude());

                    DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("Circles").child(grpcode).child("Locations").child(phoneNum);
                    refAvailable.setValue(value);
                   // refAvailable.child("name").setValue(name);

                    if (!getDriversAroundStarted) {

                        if (mLastLocation != null)
                            addcircle();
                            subscribeToUpdates();



                    }

                }
            }
        }
    };


    @Override
    protected void onStop() {
        super.onStop();


    }


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(GroupMapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(GroupMapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }


    boolean getDriversAroundStarted = false;

    private void subscribeToUpdates() {
        getDriversAroundStarted = true;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Circles").child(grpcode).child("Locations");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("Failed to read value.", String.valueOf(error.toException()));
            }
        });

    }

    private void setMarker(DataSnapshot dataSnapshot) {

        String key = dataSnapshot.getKey();
        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
        double lat = Double.parseDouble(value.get("latitude").toString());
        double lng = Double.parseDouble(value.get("longitude").toString());


        LatLng location = new LatLng(lat, lng);
        if (!mMarkers.containsKey(key)) {
            mMarkers.put(key, mMap.addMarker(new MarkerOptions().title(key).position(location).icon(BitmapDescriptorFactory.fromResource(R.drawable.user_location))));
        } else {
            mMarkers.get(key).setPosition(location);
        }
        builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
        }
        if(t<10)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),150));
            t++;
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }


    //Enabling Location

    private void connectUserToGroup() {
        checkLocationPermission();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(phoneNum).child("active");
        ref.setValue(true);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }

    private void disconnectUserFromGroup() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Users").child(phoneNum).child("active");
        dref.setValue(false);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Circles").child(grpcode).child("Locations");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(phoneNum, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });
    }
    //--------------------------adding markers circle ----------------//

    private void addcircle() {
        mycityref = FirebaseDatabase.getInstance().getReference("DangerousArea").child("MyCity");

        mycityref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Update dangerous List
                latlnglist = new ArrayList<>();
                for (DataSnapshot locaitonSnapshot : snapshot.getChildren()) {
                    MyLatLng latLng = locaitonSnapshot.getValue(MyLatLng.class);
                    latlnglist.add(latLng);
                }
                dangerousArea = new ArrayList<>();
                for (MyLatLng myLatLng : latlnglist) {
                    LatLng convert = new LatLng(myLatLng.getLatitude(), myLatLng.getLongitude());
                    dangerousArea.add(convert);
                }
                if (mMap != null) {
                    mMap.clear();
                    for (LatLng latLng : dangerousArea) {
                        mMap.addCircle(new CircleOptions().center(latLng)
                                .radius(50)        //500m
                                .strokeColor(0x220000ff)
                                .fillColor(Color.RED)
                                .strokeWidth(5.0f));
                       // mMap.addMarker(new MarkerOptions().position(latLng).title("COVID ENCOUNTERED").icon(BitmapDescriptorFactory.fromResource(R.drawable.red_zone)));

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }
}
