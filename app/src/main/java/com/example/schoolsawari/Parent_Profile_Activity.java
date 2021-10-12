package com.example.schoolsawari;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolsawari.helper.ParentModelClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Parent_Profile_Activity extends AppCompatActivity {
    CircleImageView profileImage;
    TextView fullName;


    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    String uid;
    ProgressDialog progressDialog;
    ParentModelClass parentModelClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent__profile_);
        profileImage = findViewById(R.id.profileImage);
        fullName = findViewById(R.id.fullName);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Processing...");

        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("Parents");
        progressDialog.show();
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    parentModelClass=snapshot.getValue(ParentModelClass.class);
                    fullName.setText(parentModelClass.getName());
                    //Toast.makeText(Profile_Society_Activity.this, ""+snapshot.toString(), Toast.LENGTH_SHORT).show();
                    saveObjectToSharedPreference(Parent_Profile_Activity.this,"profile","parent",parentModelClass);
                    if (parentModelClass.getProfileImageUrl()!=null){
                        //show image here
                        Picasso.get().load(parentModelClass.getProfileImageUrl()).into(profileImage);
                    }

                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }





    public void MoveOnDriver(View view) {
        Intent i = new Intent(Parent_Profile_Activity.this,Driver_List_Activity.class);
        startActivity(i);
        finish();
    }
    public static void saveObjectToSharedPreference(Context context, String preferenceFileName, String serializedObjectKey, Object object) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        final Gson gson = new Gson();
        String serializedObject = gson.toJson(object);
        sharedPreferencesEditor.putString(serializedObjectKey, serializedObject);
        sharedPreferencesEditor.apply();
    }

    public void SignOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(Parent_Profile_Activity.this,Log_In_Activity.class);
        startActivity(i);
        finishAffinity();
        finish();
    }

    public void MoveOnAllocatedJobs(View view) {
        Intent i = new Intent(Parent_Profile_Activity.this,Allocated_Jobs_Activity.class);
        startActivity(i);
        finish();
    }

    public void ProfileEdit(View view) {
        Intent i = new Intent(getApplicationContext(),Parent_Profile_Edit_Activity.class);
        startActivity(i);
    }

    public void Attendance(View view) {
        Intent i = new Intent(Parent_Profile_Activity.this,Attendance_Activity.class);
        startActivity(i);
    }

    public void MovePayment(View view) {
        Intent i = new Intent(Parent_Profile_Activity.this, Payment_Send_Activity.class);
        startActivity(i);
    }
}