package com.ashtech.sahayak_updated.Maps;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ashtech.sahayak_updated.Common.LoginSignup.StartUpScreen;
import com.ashtech.sahayak_updated.Databases.SessionManager;
import com.ashtech.sahayak_updated.HelperClass.IOnLoadLocationListner;
import com.ashtech.sahayak_updated.HelperClass.MyLatLng;
import com.ashtech.sahayak_updated.R;
import com.ashtech.sahayak_updated.Users.MyProfile;
import com.ashtech.sahayak_updated.trial;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class UserLocationMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, IOnLoadLocationListner, GeoQueryEventListener {

    //variable
    static final float END_SCALE = 0.7f;
    //Drawer Menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    private Switch mWorkingSwitch;

    ImageView menu;
    LinearLayout contentView;

    long DAYSTOEXPIRE = 14;
    // Maps Hooks
    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    int ent = 0;
    static double DANGERRADII = 0.05f;        //100m

    private FusedLocationProviderClient mFusedLocationClient;
    private SupportMapFragment mapFragment;


    String phoneNum;
    LatLng latLng;
    Marker mMarker;

    // Covid Variable
    LocationCallback locationCallback;

    Marker currentUser;
    ArrayList<LatLng> dangerousArea;
    Location lastLocation;
    private GeoQuery geoQuery;

    DatabaseReference myLocationref, mycityref;
    GeoFire geoFireLocation;
    IOnLoadLocationListner iOnLoadLocationListner;


    // driver
    String customerId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_maps_user_location_main);


        //menu Hoooks
        drawerLayout = findViewById(R.id.drawer_Userlocation);
        navigationView = findViewById(R.id.navigation_view_userlocation);
        menu = findViewById(R.id.menu2_icon);

        //hooks
        contentView = findViewById(R.id.content_userLocation);
        navigationDrawer();

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        phoneNum = sessionManager.getUserDetailfromSession().get(SessionManager.KEY_PHONENUMBER);


        //Permission Check


        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        buildLocationRequest();
                        buildLoationCallback();
                        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(UserLocationMainActivity.this);

                        initArea();
                        settingGeoFire();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(UserLocationMainActivity.this, "You must Enable permission", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

        removeExpiredMarker();



    }

    private void removeExpiredMarker() {

        //-----------------------------remove marker after 14 days-----------------------------------------//
        long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(DAYSTOEXPIRE, TimeUnit.DAYS);
        Query oldMarker = FirebaseDatabase.getInstance().getReference("TimeMarker").orderByChild("timestamp").endAt(cutoff);
        oldMarker.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String markerKey = itemSnapshot.getKey();
                    itemSnapshot.getRef().removeValue();
                    DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("DangerousArea").child("MyCity").child(markerKey);
                    refAvailable.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query oldentered =FirebaseDatabase.getInstance().getReference("Users").child(phoneNum).child("Entered").orderByChild("timestamp").endAt(cutoff);
        oldentered.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String markerKey = itemSnapshot.getKey();
                    itemSnapshot.getRef().removeValue();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void initArea() {
        mycityref = FirebaseDatabase.getInstance().getReference("DangerousArea").child("MyCity");
        iOnLoadLocationListner = this;

        mycityref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Update dangerous List
                List<MyLatLng> latlnglist = new ArrayList<>();
                for (DataSnapshot locaitonSnapshot : snapshot.getChildren()) {
                    MyLatLng latLng = locaitonSnapshot.getValue(MyLatLng.class);
                    latlnglist.add(latLng);
                }

                iOnLoadLocationListner.onLoadLocationSuccess(latlnglist);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void addUserMarker() {

        //setting my location in fireBase
        geoFireLocation.setLocation(phoneNum, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });
        if (currentUser != null) currentUser.remove();
        currentUser = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lastLocation.getLatitude(),
                        lastLocation.getLongitude()))
                .title("You")
                .anchor(0.5f, 0.5f)
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_icon)));
        CameraPosition position = new CameraPosition.Builder().
                target(currentUser.getPosition()).zoom(18).bearing(19).tilt(30).build();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        animateMarker(currentUser, lastLocation);


    }


    private void settingGeoFire() {

        myLocationref = FirebaseDatabase.getInstance().getReference("Users").child(phoneNum).child("MyLocation");
        geoFireLocation = new GeoFire(myLocationref);
    }

    private void buildLoationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (mMap != null) {
                    lastLocation = locationResult.getLastLocation();
                    addUserMarker();

                }
            }
        };
    }

    private void buildLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setSmallestDisplacement(10f);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (mFusedLocationClient != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }

        //Add Circle for dangerous areaa

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                handleMapLongClick(latLng);
            }
        });
        addCircleArea();



    }

    private void addCircleArea() {
        if (geoQuery != null) {

            geoQuery.removeGeoQueryEventListener(this);
            geoQuery.removeAllListeners();
        }
        for (LatLng latLng : dangerousArea) {

            mMap.addCircle(new CircleOptions().center(latLng)
                    .radius(DANGERRADII * 1000)        //500m
                    .strokeColor(Color.argb(255, 255, 0, 0))
                    .fillColor(Color.argb(64, 255, 0, 0))
                    .strokeWidth(5.0f));
            mMap.addMarker(new MarkerOptions().position(latLng).title("COVID ENCOUNTERED").icon(BitmapDescriptorFactory.fromResource(R.drawable.poisonicon)));
            //Create GeoQuery when user in danger Location

            geoQuery = geoFireLocation.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), DANGERRADII);  //50m

            geoQuery.addGeoQueryEventListener(UserLocationMainActivity.this);

        }
    }

    @Override
    protected void onStop() {
        mFusedLocationClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }


    @Override
    public void onLoadLocationSuccess(List<MyLatLng> latLngs) {
        dangerousArea = new ArrayList<>();
        for (MyLatLng myLatLng : latLngs) {
            LatLng convert = new LatLng(myLatLng.getLatitude(), myLatLng.getLongitude());
            dangerousArea.add(convert);
        }
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(UserLocationMainActivity.this);

        if (mMap != null) {
            mMap.clear();
            //Add USer Marker
            addUserMarker();

            //Add CIrcle of dangerous area
            addCircleArea();
        }

    }

    @Override
    public void onLoadLocationFailed(String msg) {
        Toast.makeText(this, "" + msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onKeyEntered(String key, final GeoLocation location) {


        sendNotification("ASHTECH", String.format("%s Entered in dangerous area!!", key));


        final DatabaseReference mark = FirebaseDatabase.getInstance().getReference("Users").child(key).child("Entered");

        mark.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        MyLatLng latLng = childSnapshot.getValue(MyLatLng.class);
                        Double lon1 = Math.toRadians(latLng.getLongitude());
                        Double lon2 = Math.toRadians(location.longitude);
                        Double lat1 = Math.toRadians(latLng.getLatitude());
                        Double lat2 = Math.toRadians(location.latitude);


                        double dlon = lon2 - lon1;
                        double dlat = lat2 - lat1;
                        double a = Math.pow(Math.sin(dlat / 2), 2)
                                + Math.cos(lat1) * Math.cos(lat2)
                                * Math.pow(Math.sin(dlon / 2), 2);

                        double c = 2 * Math.asin(Math.sqrt(a));
                        double r = 6371 * c*0.00001;


                        if (r < DANGERRADII) {
                            ent = 1;
                            break;

                        }

                    }
                }
                if (ent == 0) {
                    DatabaseReference markpush = mark.push();
                    markpush.child("location").setValue(location);
                    markpush.child("timestamp").setValue(ServerValue.TIMESTAMP);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public void onKeyExited(String key) {
        sendNotification("ASHTECH", String.format("%s Is no longer in dangerous area!!", key));

    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        Log.d("Move", String.format("%s moved within the dangerous area[%f/%f]", key, location.latitude, location.longitude));

    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        Log.e("ERROR", "" + error);
    }


    private void sendNotification(String title, String content) {

        String NOTIFICATION_CHANNEL_ID = "ASHTECT_Multiple_Channel";
        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            //Config
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            manager.createNotificationChannel(notificationChannel);

        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        Notification notification = builder.build();
        manager.notify(new Random().nextInt(), notification);
    }


    public void animateMarker(final Marker marker, final Location location) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final LatLng startLatLng = marker.getPosition();
        final double startRotation = marker.getRotation();
        final long duration = 500;

        final LinearInterpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                double lng = t * location.getLongitude() + (1 - t)
                        * startLatLng.longitude;
                double lat = t * location.getLatitude() + (1 - t)
                        * startLatLng.latitude;

                float rotation = (float) (t * location.getBearing() + (1 - t)
                        * startRotation);

                marker.setPosition(new LatLng(lat, lng));
                marker.setRotation(rotation);

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    //---------------------------------nearby places------------------------------------------------//
    public void nearbyHospital(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        String x = String.valueOf(location.getLatitude());
                        String y = String.valueOf(location.getLongitude());

                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?&saddr="
                                        + x
                                        + ","
                                        + y
                                        + "&daddr=nearby hospitals"
                                ));
                        startActivity(intent);

                    }


                }
            });
        }


    }

    public void nearbyPolice(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        String x = String.valueOf(location.getLatitude());
                        String y = String.valueOf(location.getLongitude());

                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?&saddr="
                                        + x
                                        + ","
                                        + y
                                        + "&daddr=nearby police station"

                                ));
                        startActivity(intent);


                    }


                }
            });
        }


    }


    // Working Status of User


    //----------------------------------------------------------navigetion drawer----------------------------------------------------------//


    private void navigationDrawer() {

        //Navigation Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_myCircle);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else
                    drawerLayout.openDrawer(GravityCompat.START);

            }
        });

        animateNavigationDrawer();

    }

    private void animateNavigationDrawer() {
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.nav_home) {
            Intent homeIntent = new Intent(UserLocationMainActivity.this, MapsDashboardNavigation.class);
            startActivity(homeIntent);
            finish();
        } else if (id == R.id.nav_createCircle) {
            Intent joinintent = new Intent(UserLocationMainActivity.this, CreateCircle.class);


            startActivity(joinintent);
        } else if (id == R.id.nav_joinCircle) {
            Intent joinintent = new Intent(UserLocationMainActivity.this, JoinCircle.class);
            startActivity(joinintent);
        } else if (id == R.id.nav_mygrp) {

            Intent mycircleIntent = new Intent(UserLocationMainActivity.this, MyGroup.class);
            startActivity(mycircleIntent);

        } else if (id == R.id.nav_in8Mrmbr) {

            Intent myIntent = new Intent(UserLocationMainActivity.this, trial.class);
            startActivity(myIntent);

        } else if (id == R.id.nav_shareLocation) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, "My Location is: " + "https://www.google.com/maps/@" + lastLocation.getLatitude() + "," + lastLocation.getLongitude() + ",17z");
            startActivity(i.createChooser(i, "Share Using: "));

        } else if (id == R.id.nav_logout) {

            SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
            if (sessionManager.checkLogin())
                sessionManager.logoutUserFromSession();
            startActivity(new Intent(getApplicationContext(), StartUpScreen.class));
            finish();
        } else if (id == R.id.nav_profile) {
            Intent myIntent = new Intent(UserLocationMainActivity.this, MyProfile.class);
            startActivity(myIntent);

        }


        return true;
    }



    private void handleMapLongClick(LatLng latLng) {
        if(mMarker!=null)
            mMarker.remove();
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMarker=mMap.addMarker(markerOptions);
        ShowDialog(latLng);

    }
    void ShowDialog(final LatLng laln)
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alert_dialog, null);
        Button acceptButton = view.findViewById(R.id.acceptBtn);
        final TextView name=view.findViewById(R.id.name);
        final TextView phone=view.findViewById(R.id.phone);
        final TextView enteredzone=view.findViewById(R.id.encountered);
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        final DatabaseReference reportRef= FirebaseDatabase.getInstance().getReference("Reported");
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(laln.latitude, laln.longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();

            name.setText(address);
            phone.setText(city);
            enteredzone.setText("");
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    reportRef.child(phoneNum).setValue(laln);
                    Toast.makeText(UserLocationMainActivity.this, "Area Reported to\n near by Police Station ", Toast.LENGTH_LONG).show();
                    alertDialog.cancel();
                }
            });
            alertDialog.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
