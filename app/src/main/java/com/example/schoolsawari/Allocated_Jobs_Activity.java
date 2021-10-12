package com.example.schoolsawari;

import android.app.ProgressDialog;
import android.content.Intent;
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

import java.util.ArrayList;

public class Allocated_Jobs_Activity extends AppCompatActivity {
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
        setContentView(R.layout.activity_allocated__jobs_);
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
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    bookingList=new ArrayList<>();
                    for (DataSnapshot categ:snapshot.getChildren()){
                        // for (DataSnapshot ads:categ.getChildren()){
                        BookingModelClass ad=new BookingModelClass();
                        ad = categ.getValue(BookingModelClass.class);
                        if (uid.equals(ad.getParentId())) {
                            if (ad.isAccepted()) {
                                bookingList.add(ad);
                            }
                        }
                        //}
                    }
                    try {
                        if (bookingList.size()==0) {
                            progressDialog.dismiss();
                            text.setText("No Job Allocated");
                            Toast.makeText(Allocated_Jobs_Activity.this, "No Allocated Job Found", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            All_Request_Adapter adapter = new All_Request_Adapter(
                                    bookingList);
                            recyclerView.setAdapter(adapter);
                            progressDialog.dismiss();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(Allocated_Jobs_Activity.this, ""+e, Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void MoveOnProfile(View view) {
        Intent i = new Intent(Allocated_Jobs_Activity.this, Parent_Profile_Activity.class);
        startActivity(i);
        finish();
    }


    public void MoveOnDriver(View view) {
        Intent i = new Intent(Allocated_Jobs_Activity.this, Driver_List_Activity.class);
        startActivity(i);
        finish();
    }
}