package com.example.schoolsawari;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Log_In_Activity extends AppCompatActivity {
    ShapeableImageView shap;
    EditText txtEmail,txtPassword;
    FirebaseAuth firebaseAuth;
    DatabaseReference dref;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log__in_);
        shap=findViewById(R.id.shap);

        txtEmail=findViewById(R.id.txtEmail);
        txtPassword=findViewById(R.id.txtPassword);

        firebaseAuth=FirebaseAuth.getInstance();
        dref=FirebaseDatabase.getInstance().getReference();

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);
        shap.setShapeAppearanceModel(shap.getShapeAppearanceModel().toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, 40)
                .setTopLeftCorner(CornerFamily.ROUNDED, 40)
                .setBottomLeftCorner(CornerFamily.ROUNDED, 0)
                .setBottomRightCorner(CornerFamily.ROUNDED, 0)
                .build());
    }

    public void MoveOn(View view) {
        Intent i = new Intent(Log_In_Activity.this, SignUp_Activity.class);
        startActivity(i);
        finish();
    }

    public void resetPassword(View view) {
        final EditText txt=new EditText(Log_In_Activity.this);

        new AlertDialog.Builder(Log_In_Activity.this)
                .setMessage("Enter email")
                .setView(txt)
                .setTitle("Reset Password")
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(txt.getText().toString())){
                            FirebaseAuth.getInstance().sendPasswordResetEmail(txt.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(Log_In_Activity.this, "Link sent Successfully!", Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(Log_In_Activity.this, "Error "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }

    public void SignIn(View view) {
        if (TextUtils.isEmpty(txtEmail.getText().toString())){
            Toast.makeText(this, "Email is required!", Toast.LENGTH_SHORT).show();
            txtEmail.requestFocus();
        }else if (TextUtils.isEmpty(txtPassword.getText().toString())){
            Toast.makeText(this, "Password is required!", Toast.LENGTH_SHORT).show();
            txtPassword.requestFocus();
        }else {
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(txtEmail.getText().toString(),txtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        handleLogin(firebaseAuth.getCurrentUser());
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(Log_In_Activity.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }
    private void handleLogin(FirebaseUser firebaseUser){
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if (snapshot.hasChild("Drivers")){
                    if (snapshot.child("Drivers").hasChild(firebaseUser.getUid())){
                        SharedPreferences.Editor editor=getSharedPreferences("type",MODE_PRIVATE).edit();
                        editor.putString("uType","Driver");
                        editor.apply();
                        startActivity(new Intent(getApplicationContext(), Booking_Request_Activity
                        .class));
                        finish();
                    }else {
                        SharedPreferences.Editor editor=getSharedPreferences("type",MODE_PRIVATE).edit();
                        editor.putString("uType","Parent");
                        editor.apply();
                        startActivity(new Intent(getApplicationContext(),Driver_List_Activity
                                .class));
                        finish();
                    }
                }else {
                    SharedPreferences.Editor editor=getSharedPreferences("type",MODE_PRIVATE).edit();
                    editor.putString("uType","Parent");
                    editor.apply();
                    startActivity(new Intent(getApplicationContext(),Driver_List_Activity
                            .class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(Log_In_Activity.this, "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}