package com.example.schoolsawari;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class SignUp_Activity extends AppCompatActivity {
    ShapeableImageView shap;
    Spinner spinnerType;
    EditText email,password;
    Button btnSignUp;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_);
        shap = findViewById(R.id.shap);
        spinnerType = findViewById(R.id.spinnerType);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnSignUp = findViewById(R.id.btnSignUp);

        spinnerType.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        shap.setShapeAppearanceModel(shap.getShapeAppearanceModel().toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, 40)
                .setTopLeftCorner(CornerFamily.ROUNDED, 40)
                .setBottomLeftCorner(CornerFamily.ROUNDED, 0)
                .setBottomRightCorner(CornerFamily.ROUNDED, 0)
                .build());

        ArrayList<String> listType = new ArrayList<>();
        listType.add("Select User Type");
        listType.add("Driver");
        listType.add("Parent");

        ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listType);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(arrayAdapter);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) parent.getChildAt(0)).setTextSize(14);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String SEmail = email.getText().toString();
                String SPassword = password.getText().toString();
                if (spinnerType.getSelectedItemPosition()==0){
                    Toast.makeText(SignUp_Activity.this, "Please select User Type", Toast.LENGTH_SHORT).show();
                    spinnerType.requestFocus();
                }
                 else if (SEmail.isEmpty()){
                    Toast.makeText(SignUp_Activity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(SEmail).matches()) {
                    email.setError("Invalid Email Address ");
                    return;
                }
                else if (!PASSWORD_PATTERN.matcher(SPassword).matches()) {
                    password.setError("Weak Password! Passwords must have upper and lower case letters,at least 1 special character at least 1 number, and be at least 8 characters long. ");
                    return;
                }
                else {

                    String type = spinnerType.getSelectedItem().toString();
                    if (spinnerType.getSelectedItemPosition()==1){

                        Intent i = new Intent(SignUp_Activity.this,SignUp_Driver_Activity.class);
                        i.putExtra("email",SEmail);
                        i.putExtra("password",SPassword);
                        i.putExtra("userType",type);
                        startActivity(i);
                        finish();
                    }
                    else {
                        Intent i = new Intent(SignUp_Activity.this,SignUp_Perents_Activity.class);
                        i.putExtra("email",SEmail);
                        i.putExtra("password",SPassword);
                        i.putExtra("userType",type);
                        startActivity(i);
                        finish();
                    }
                }
            }
        });

    }

    public void MoveOn(View view) {
        Intent i = new Intent(SignUp_Activity.this,Log_In_Activity.class);
        startActivity(i);
    }
}