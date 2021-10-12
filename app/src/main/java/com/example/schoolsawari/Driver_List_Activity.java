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

import com.example.schoolsawari.helper.All_Drivers_Adapter;
import com.example.schoolsawari.helper.DriverModelClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Driver_List_Activity extends AppCompatActivity {

    TextView text;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ArrayList<DriverModelClass> driversList=new ArrayList<>();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver__list_);

        recyclerView= findViewById(R.id.record);
        text= findViewById(R.id.text);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Processing...");

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        linearLayout.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayout);
        databaseReference = FirebaseDatabase.getInstance().getReference("Drivers");
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider_ry_venue));
        recyclerView.addItemDecoration(dividerItemDecoration);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    driversList=new ArrayList<>();
                    for (DataSnapshot categ:snapshot.getChildren()){
                        // for (DataSnapshot ads:categ.getChildren()){
                        DriverModelClass ad=new DriverModelClass();
                        ad = categ.getValue(DriverModelClass.class);
                        driversList.add(ad);

                        //}
                    }
                    try {
                        if (driversList.size()==0) {
                            progressDialog.dismiss();
                            text.setText("No Driver ");
                            Toast.makeText(Driver_List_Activity.this, "No Driver Found", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            All_Drivers_Adapter adapter = new All_Drivers_Adapter(
                                    driversList);
                            recyclerView.setAdapter(adapter);
                            progressDialog.dismiss();
                        }
                    }catch (Exception e){
                        Toast.makeText(Driver_List_Activity.this, ""+e, Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void MoveOnProfile(View view) {
        Intent i = new Intent(Driver_List_Activity.this, Parent_Profile_Activity.class);
        startActivity(i);
        finish();
    }

    public void MoveOnAllocatedJobs(View view) {
        Intent i = new Intent(Driver_List_Activity.this,Allocated_Jobs_Activity.class);
        startActivity(i);
        finish();
    }
}