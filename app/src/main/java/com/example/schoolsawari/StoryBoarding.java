package com.example.schoolsawari;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StoryBoarding extends AppCompatActivity {

    LinearLayout layout;
    int index=0;
    Button btnLocation;
    FirebaseAuth firebaseAuth;
    DatabaseReference dref;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storyboarding);

        btnLocation=findViewById(R.id.btnLocation);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);

        locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        firebaseAuth = FirebaseAuth.getInstance();
        dref= FirebaseDatabase.getInstance().getReference();

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocationPermission();
                if (firebaseAuth.getCurrentUser()!=null){
                    handleLogin(firebaseAuth.getCurrentUser());

                }
                else {
                    Intent i = new Intent(StoryBoarding.this,Log_In_Activity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
        layout=findViewById(R.id.bg_storyBoarding);
        int arr[]={R.drawable.boarding1,R.drawable.boarding2,R.drawable.boarding3};
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                if (index==2){
                    btnLocation.setVisibility(View.VISIBLE);
                }
                if (index==1 || index==2){
                    layout.setBackground(getResources().getDrawable(arr[index]));
                }else{

                }
            }
        });
    }

    public void skipStory(View view) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    LocationManager locManager;

    LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {

        }
    };
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(StoryBoarding.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
//                new AlertDialog.Builder(this)
//                        .setTitle(R.string.title_location_permission)
//                        .setMessage(R.string.text_location_permission)
//                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                //Prompt the user once explanation has been shown
//                                ActivityCompat.requestPermissions(MainActivity.this,
//                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                        MY_PERMISSIONS_REQUEST_LOCATION);
//                            }
//                        })
//                        .create()
//                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, locationListener);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
    private void handleLogin(FirebaseUser firebaseUser){
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if (snapshot.hasChild("Drivers")){
                    if (snapshot.child("Drivers").hasChild(firebaseUser.getUid())){
                        startActivity(new Intent(getApplicationContext(), Booking_Request_Activity
                                .class));
                        finish();
                    }else {
                        startActivity(new Intent(getApplicationContext(),Driver_List_Activity
                                .class));
                        finish();
                    }
                }else {
                    startActivity(new Intent(getApplicationContext(),Driver_List_Activity
                            .class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(StoryBoarding.this, "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

}