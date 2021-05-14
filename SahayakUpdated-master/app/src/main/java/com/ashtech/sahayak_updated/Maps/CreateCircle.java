package com.ashtech.sahayak_updated.Maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.JetPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ashtech.sahayak_updated.Databases.CircleJoin;
import com.ashtech.sahayak_updated.Databases.Joincode;
import com.ashtech.sahayak_updated.Databases.SessionManager;
import com.ashtech.sahayak_updated.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CreateCircle extends AppCompatActivity {

    TextView circlecode ;
    Button gencode,creCircle;
    DatabaseReference reference,currentref,circleReference;
    String Currentusermonno,code;
    TextInputLayout newCirclename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_circle);
        circlecode=findViewById(R.id.code_createCircle);
        gencode=findViewById(R.id.CreateCircle_generateCode);
        creCircle=findViewById(R.id.Create_circle);
        newCirclename=findViewById(R.id.textCreatecirclename);

        SessionManager sessionManager=new SessionManager(this,SessionManager.SESSION_USERSESSION);
        sessionManager.getUserDetailfromSession();
        Currentusermonno= sessionManager.getUserDetailfromSession().get(SessionManager.KEY_PHONENUMBER);
        // Firebase References
        reference= FirebaseDatabase.getInstance().getReference().child("Users");
        currentref=FirebaseDatabase.getInstance().getReference().child("Users").child(Currentusermonno);
    }

    public void GenerateCode(View v)
    {
        Random r =new Random();
        int n=100000+r.nextInt(900000);
        code= String.valueOf(n);
        circlecode.setText(code);
        gencode.setVisibility(View.INVISIBLE);
        creCircle.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Circle Code Generated ", Toast.LENGTH_SHORT).show();
    }
    public  void  CreateCircleOnTheDatabase(View v)
    {

        //for users database
        FirebaseDatabase root = FirebaseDatabase.getInstance();
        DatabaseReference reference = root.getReference().child("Users").child(Currentusermonno).child("MyGroup");
        Joincode joincode=new Joincode(code,true);
        reference.child(code).setValue(joincode);


        //for Circle database

        FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
        DatabaseReference ref = rootnode.getReference("Circles");

        String circleName=newCirclename.getEditText().getText().toString();
        CircleJoin addnewcircle=new CircleJoin(circleName,code,Currentusermonno);
        ref.child(code).setValue(addnewcircle).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(CreateCircle.this, "Circle Created Successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(CreateCircle.this, "Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //grp member
        Map<String,String> grpmemberObj=new HashMap<String, String>();
        grpmemberObj.put("phoneNum",Currentusermonno);
        DatabaseReference grpmemberRef=FirebaseDatabase.getInstance().getReference("Circles").child(code).child("Members");
        grpmemberRef.child(Currentusermonno).setValue(grpmemberObj);


    }

}
