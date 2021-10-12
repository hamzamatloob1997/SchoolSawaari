package com.example.schoolsawari;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolsawari.helper.All_Request_Adapter;
import com.example.schoolsawari.helper.BookingModelClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Booking_Request_Activity extends AppCompatActivity {
    TextView text;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ArrayList<BookingModelClass> bookingList=new ArrayList<>();
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking__request_);
        recyclerView= findViewById(R.id.record);
        text= findViewById(R.id.text);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Processing...");

        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        linearLayout.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayout);
        databaseReference = FirebaseDatabase.getInstance().getReference("Booking");
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider_ry_venue));
        recyclerView.addItemDecoration(dividerItemDecoration);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    bookingList=new ArrayList<>();
                    for (DataSnapshot categ:snapshot.getChildren()){
                        // for (DataSnapshot ads:categ.getChildren()){
                        BookingModelClass ad=new BookingModelClass();
                        ad = categ.getValue(BookingModelClass.class);
                        if (uid.equals(ad.getDriverId())) {
                            if (!ad.isAccepted()) {
                                bookingList.add(ad);
                            }
                        }
                        //}
                    }
                    try {
                        if (bookingList.size()==0) {
                            progressDialog.dismiss();
                            text.setText("No Request ");
                            Toast.makeText(Booking_Request_Activity.this, "No Request Found", Toast.LENGTH_SHORT).show();
                            All_Request_Adapter adapter = new All_Request_Adapter(
                                    bookingList);
                            recyclerView.setAdapter(adapter);
                            progressDialog.dismiss();
                        }
                        else {
                            All_Request_Adapter adapter = new All_Request_Adapter(
                                    bookingList);
                            recyclerView.setAdapter(adapter);
                            progressDialog.dismiss();
                        }
                    }catch (Exception e){
                        Toast.makeText(Booking_Request_Activity.this, "sdgfhgjkl"+e, Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void MoveOnProfile(View view) {
        Intent i = new Intent(Booking_Request_Activity.this,Driver_Profile_Activity.class);
        startActivity(i);
        finish();
    }


    public static <GenericClass> GenericClass getSavedObjectFromPreference(Context context, String preferenceFileName, String preferenceKey, Class<GenericClass> classType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
        if (sharedPreferences.contains(preferenceKey)) {
            final Gson gson = new Gson();
            return gson.fromJson(sharedPreferences.getString(preferenceKey, ""), classType);
        }
        return null;
    }

    public void MoveOnJobs(View view) {
        Intent i = new Intent(Booking_Request_Activity.this,Driver_Jobs_Activity.class);
        startActivity(i);
        finish();
    }
}