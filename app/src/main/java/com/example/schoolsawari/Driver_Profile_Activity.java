package com.example.schoolsawari;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolsawari.helper.DriverModelClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.schoolsawari.Parent_Profile_Edit_Activity.saveObjectToSharedPreference;


public class Driver_Profile_Activity extends AppCompatActivity {
    RelativeLayout profileEdit;
    CircleImageView profileImage;
    TextView fullName;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    String uid;
    ProgressDialog progressDialog;
    DriverModelClass driverModelClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver__profile_);
        profileImage = findViewById(R.id.profileImage);
        fullName = findViewById(R.id.fullName);
        profileEdit = findViewById(R.id.profileEdit);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Processing...");

        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("Drivers");
        progressDialog.show();
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    driverModelClass=snapshot.getValue(DriverModelClass.class);
                    fullName.setText(driverModelClass.getName());
                    //Toast.makeText(Profile_Society_Activity.this, ""+snapshot.toString(), Toast.LENGTH_SHORT).show();
                    saveObjectToSharedPreference(Driver_Profile_Activity.this,"profile","driver",driverModelClass);
                    if (driverModelClass.getProfileImageUrl()!=null){
                        //show image here
                        Picasso.get().load(driverModelClass.getProfileImageUrl()).into(profileImage);
                    }

                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent i = new Intent(Driver_Profile_Activity.this,Driver_Profile_Edit_Activity.class);
            i.putExtra("name",driverModelClass.getName());
            i.putExtra("number",driverModelClass.getNumber());
            i.putExtra("address",driverModelClass.getAddress());
            i.putExtra("image",driverModelClass.getProfileImageUrl());
            i.putExtra("model",driverModelClass.getVehicleModel());
            startActivity(i);
            }
        });
    }


    public void MoveOnBooking(View view) {
        Intent i = new Intent(Driver_Profile_Activity.this, Booking_Request_Activity.class);
        startActivity(i);
        finish();
    }

    public void SignOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(Driver_Profile_Activity.this,Log_In_Activity.class);
        startActivity(i);
        finishAffinity();
        finish();
    }
    public void MoveOnJobs(View view) {
        Intent i = new Intent(Driver_Profile_Activity.this,Driver_Jobs_Activity.class);
        startActivity(i);
        finish();
    }

    public void MovePayment(View view) {
        Intent i = new Intent(Driver_Profile_Activity.this,Payment_Recive_Activity.class);
        startActivity(i);
    }
}