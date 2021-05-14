package com.ashtech.sahayak_updated.Maps;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashtech.sahayak_updated.Databases.CircleJoin;
import com.ashtech.sahayak_updated.Databases.SessionManager;
import com.ashtech.sahayak_updated.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyGroup extends AppCompatActivity implements GroupAdapter.OnNoteListener {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    CircleJoin grpinfo;
    ArrayList<CircleJoin> groupList;

    DatabaseReference reference, circlereference;

    String groupMemberId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);


        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        sessionManager.getUserDetailfromSession();
        String Currentusermonno = sessionManager.getUserDetailfromSession().get(SessionManager.KEY_PHONENUMBER);


        recyclerView = findViewById(R.id.recycler_mygroup);
        groupList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        circlereference = FirebaseDatabase.getInstance().getReference().child("Circles");
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(Currentusermonno).child("MyGroup");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupList.clear();
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot dss:dataSnapshot.getChildren())
                    {
                        groupMemberId=dss.child("code").getValue(String.class);
                        circlereference.child(groupMemberId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                grpinfo=dataSnapshot.getValue(CircleJoin.class);
                                groupList.add(grpinfo);
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

        adapter = new GroupAdapter(groupList, getApplicationContext(),this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();




    }

    @Override
    public void onNoteListener(int position) {
        CircleJoin  element= groupList.get(position);
        String code= element.getCode();

        Intent intent=new Intent(this,MyCircleActivity.class);
        intent.putExtra("Code",code);
        startActivity(intent);
    }
}
