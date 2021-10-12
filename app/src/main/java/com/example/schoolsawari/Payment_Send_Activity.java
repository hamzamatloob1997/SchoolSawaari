package com.example.schoolsawari;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolsawari.helper.BookingModelClass;
import com.example.schoolsawari.helper.PaymentModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Payment_Send_Activity extends AppCompatActivity {
    EditText textTransaction,textPayment;
    Spinner txtSource;
    Button btnSend;
    DatabaseReference databaseReference,bookingReference;
    FirebaseAuth firebaseAuth;
    String uid,dId,rId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment__send_);
        txtSource = findViewById(R.id.txtSource);
        textPayment = findViewById(R.id.textPayment);
        textTransaction = findViewById(R.id.textTransaction);
        btnSend = findViewById(R.id.btnSend);

       // txtSource.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

        ArrayList<String> listSource = new ArrayList<>();
        listSource.add("Select Source ");
        listSource.add("Bank");
        listSource.add("EasyPesa");
        listSource.add("Jazz Cash");
        ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,listSource);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        txtSource.setAdapter(arrayAdapter);
        txtSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) parent.getChildAt(0)).setTextSize(14);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();
        bookingReference = FirebaseDatabase.getInstance().getReference("Booking");
        bookingReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot request:snapshot.getChildren()){
                        BookingModelClass bookingModelClass = new BookingModelClass();
                        bookingModelClass = request.getValue(BookingModelClass.class);
                        if (uid.equals(bookingModelClass.getParentId())){
                            dId = bookingModelClass.getDriverId();
                            rId = bookingModelClass.getRequestId();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String price = textPayment.getText().toString();
                String tId = textTransaction.getText().toString();

                if (txtSource.getSelectedItemPosition()==0){
                    Toast.makeText(Payment_Send_Activity.this, "Select the Source", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(price)){
                    Toast.makeText(Payment_Send_Activity.this, "Enter Payment", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(tId)){
                    Toast.makeText(Payment_Send_Activity.this, "Enter Transaction Id", Toast.LENGTH_SHORT).show();
                }
                else{
                    Calendar calendar = Calendar.getInstance();
                    String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
                    databaseReference = FirebaseDatabase.getInstance().getReference("Payment");

                    PaymentModel paymentModel = new PaymentModel();
                    paymentModel.setSource(txtSource.getSelectedItem().toString());
                    paymentModel.setPrice(price);
                    paymentModel.setTransactionId(tId);
                    paymentModel.setDate(currentDate);
                    paymentModel.setDriverId(dId);
                    paymentModel.setParentId(uid);
                    paymentModel.setRequestId(rId);

                    databaseReference.child(rId).setValue(paymentModel);
                    Toast.makeText(Payment_Send_Activity.this, "Send Successfully", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
}