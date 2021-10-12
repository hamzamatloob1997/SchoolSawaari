package com.example.schoolsawari;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolsawari.helper.PaymentAdapter;
import com.example.schoolsawari.helper.PaymentModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Payment_Recive_Activity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ArrayList<PaymentModel> paymentList=new ArrayList<>();
    ProgressDialog progressDialog;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment__recive_);

        recyclerView= findViewById(R.id.record);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Processing...");

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        linearLayout.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayout);
        databaseReference = FirebaseDatabase.getInstance().getReference("Payment");
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider_ry_venue));
        recyclerView.addItemDecoration(dividerItemDecoration);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    paymentList=new ArrayList<>();
                    for (DataSnapshot categ:snapshot.getChildren()){
                       // for (DataSnapshot ads:categ.getChildren()){
                            PaymentModel ad=new PaymentModel();
                            ad = categ.getValue(PaymentModel.class);
                            if (uid.equals(ad.getDriverId())){
                                paymentList.add(ad);
                            }


                        //}
                    }
                    try {


                        PaymentAdapter adapter = new PaymentAdapter(
                                paymentList);
                        recyclerView.setAdapter(adapter);
                        progressDialog.dismiss();

                    }catch (Exception e){
                        Toast.makeText(Payment_Recive_Activity.this, ""+e, Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}