package com.ashtech.sahayak_updated.Maps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ashtech.sahayak_updated.Databases.Joincode;
import com.ashtech.sahayak_updated.Databases.SessionManager;
import com.ashtech.sahayak_updated.R;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class JoinCircle extends AppCompatActivity {

    PinView pinView;
    DatabaseReference reference,currentref;
    FirebaseUser user;
    FirebaseAuth auth;
    String joinedmobno,Currentusermonno;
    DatabaseReference circleReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_circle);
        pinView=findViewById(R.id.pin_view_joinCircle);

        SessionManager sessionManager=new SessionManager(this,SessionManager.SESSION_USERSESSION);
        sessionManager.getUserDetailfromSession();
        Currentusermonno= sessionManager.getUserDetailfromSession().get(SessionManager.KEY_PHONENUMBER);
        reference= FirebaseDatabase.getInstance().getReference().child("Circles");
        currentref=FirebaseDatabase.getInstance().getReference().child("Users").child(Currentusermonno);





    }
    public void submitbtnClick(View view)
    {
        Query query =reference.orderByChild("code").equalTo(pinView.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
//                    UserHelperClass helperClass=null;
//                    for(DataSnapshot childDss:dataSnapshot.getChildren())
//                    {
//                        helperClass=childDss.getValue(UserHelperClass.class);
//                        joinedmobno=helperClass.getPhoneNum();
//
//                        circleReference=FirebaseDatabase.getInstance().getReference().child("Users").child(joinedmobno).child("CircleMember");
//
//                        CircleJoin circleJoin=new CircleJoin(Currentusermonno);
//                        CircleJoin circleJoin1=new CircleJoin(joinedmobno);
//
//                        circleReference.child(Currentusermonno).setValue(circleJoin).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if(task.isSuccessful())
//                                {
//                                    Toast.makeText(JoinCircle.this, "User Joined Circle Successfull", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//
//
//
//
//                    }


                   // grp member
                    Map<String,String> grpmemberObj=new HashMap<String, String>();
                    grpmemberObj.put("phoneNum",Currentusermonno);
                    DatabaseReference grpmemberRef=FirebaseDatabase.getInstance().getReference("Circles").child(pinView.getText().toString()).child("Members");
                    grpmemberRef.child(Currentusermonno).setValue(grpmemberObj);

                    //for users database
                    FirebaseDatabase root = FirebaseDatabase.getInstance();
                    DatabaseReference reference = root.getReference().child("Users").child(Currentusermonno).child("MyGroup");
                    Joincode joincode=new Joincode(pinView.getText().toString(),true);
                    reference.child(pinView.getText().toString()).setValue(joincode).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(JoinCircle.this, "You joined the circle", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });


                }
                else
                {
                    Toast.makeText(JoinCircle.this, "Invalid Code", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void crossBack(View v)
    {
        Intent intent= new Intent(JoinCircle.this,UserLocationMainActivity.class);
        startActivity(intent);
        finish();
    }









}
