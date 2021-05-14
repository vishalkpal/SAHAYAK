package com.ashtech.sahayak_updated.Maps;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashtech.sahayak_updated.Databases.SessionManager;
import com.ashtech.sahayak_updated.Databases.UserHelperClass;
import com.ashtech.sahayak_updated.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import MemberAdapter.MemberAdapter;


public class MyCircleActivity extends AppCompatActivity implements MemberAdapter.OnSelectListener {


    int count=0;

    String currname,currphone;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    UserHelperClass userHelperClass;
    ArrayList<UserHelperClass> nameList;
    DatabaseReference circleRefernce, userreference;
    String circlecode;

    String circleMemberId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_circle);

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        sessionManager.getUserDetailfromSession();
        String Currentusermonno = sessionManager.getUserDetailfromSession().get(SessionManager.KEY_PHONENUMBER);
        recyclerView = findViewById(R.id.recycler_myCircle);
        nameList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        circlecode = getIntent().getStringExtra("Code");

        circleRefernce = FirebaseDatabase.getInstance().getReference().child("Circles").child(circlecode).child("Members");
        userreference = FirebaseDatabase.getInstance().getReference().child("Users");
        // reference = FirebaseDatabase.getInstance().getReference().child("Users").child(Currentusermonno).child("MyGroup");

        circleRefernce.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nameList.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot dss : dataSnapshot.getChildren()) {
                        circleMemberId = dss.child("phoneNum").getValue(String.class);
                        userreference.child(circleMemberId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                userHelperClass = dataSnapshot.getValue(UserHelperClass.class);
                                nameList.add(userHelperClass);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();


            }
        });
        adapter = new MemberAdapter(nameList, getApplicationContext(), this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onSelectListener(int position) {

        UserHelperClass element = nameList.get(position);
        String selectedUserPhoneNum = element.getPhoneNum();

        showDialog(selectedUserPhoneNum);
//          abhi dhekta hu baad m
//        Intent intent=new Intent(this,MyCircleActivity.class);
//        intent.putExtra("Code",code);
//        startActivity(intent);


    }

    public void getToTheGroupMapActivity(View v) {
        Intent intent = new Intent(MyCircleActivity.this, GroupMapsActivity.class);
        intent.putExtra("code", circlecode);
        startActivity(intent);

    }

    void showDialog(final String selphoneNum) {

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.alert_dialog, null);
        Button acceptButton = view.findViewById(R.id.acceptBtn);
        final TextView name=view.findViewById(R.id.name);
        final TextView phone=view.findViewById(R.id.phone);
        final TextView enteredzone=view.findViewById(R.id.encountered);


        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(selphoneNum);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        UserHelperClass userHelperClass= snapshot.getValue(UserHelperClass.class);
                         currname =userHelperClass.getFullname();
                         currphone=userHelperClass.getPhoneNum();
                         name.setText(currname);
                         phone.setText(currphone);
                         enteredzone.setText("Entered in: "+count);

                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.child("Entered").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    count = (int) snapshot.getChildrenCount();
                    alertDialog.show();
                      }
                else {
                    count = 0;
                    alertDialog.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    alertDialog.cancel();
            }
        });
    }
}
