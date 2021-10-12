package com.example.schoolsawari;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolsawari.helper.ParentModelClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileDescriptor;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp_Perents_Activity extends AppCompatActivity {
    ShapeableImageView shap;
    CircleImageView profileImage;
    EditText txtFullName,txtAddress,txtNumber,txtNoOfKids;
    Button btnSignUp;
    private int REQUEST_CODE_PROFILE_IMAGE=101;
    private Uri imgUriProfileImage;
    String email,password;
    String uid;
    ProgressDialog progressDialog;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    DatabaseReference dref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__perents_);
        shap=findViewById(R.id.shap);

        profileImage=findViewById(R.id.profileImage);
        Intent i=getIntent();
        email=i.getExtras().getString("email");
        password=i.getExtras().getString("password");

        txtFullName=findViewById(R.id.fullName);
        txtAddress=findViewById(R.id.address);
        txtNumber=findViewById(R.id.number);
        txtNoOfKids=findViewById(R.id.noKids);

        btnSignUp=findViewById(R.id.btnSignUp);

        firebaseAuth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        dref= FirebaseDatabase.getInstance().getReference();

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);

        shap.setShapeAppearanceModel(shap.getShapeAppearanceModel().toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, 40)
                .setTopLeftCorner(CornerFamily.ROUNDED, 40)
                .setBottomLeftCorner(CornerFamily.ROUNDED, 0)
                .setBottomRightCorner(CornerFamily.ROUNDED, 0)
                .build());

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE_PROFILE_IMAGE);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(txtFullName.getText().toString())){
                    Toast.makeText(SignUp_Perents_Activity.this, "Name is required!", Toast.LENGTH_SHORT).show();
                    txtFullName.requestFocus();
                }else if (TextUtils.isEmpty(txtAddress.getText().toString())){
                    Toast.makeText(SignUp_Perents_Activity.this, "Address is required!", Toast.LENGTH_SHORT).show();
                    txtAddress.requestFocus();
                }else if (TextUtils.isEmpty(txtNumber.getText().toString())){
                    Toast.makeText(SignUp_Perents_Activity.this, "Number is required!", Toast.LENGTH_SHORT).show();
                    txtNumber.requestFocus();
                }else if (TextUtils.isEmpty(txtNoOfKids.getText().toString())){
                    Toast.makeText(SignUp_Perents_Activity.this, "Vehicle Model is required!", Toast.LENGTH_SHORT).show();
                    txtNoOfKids.requestFocus();
                }else {
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                firebaseUser=firebaseAuth.getCurrentUser();
                                uid = firebaseUser.getUid();
                                uploadProfileImage(imgUriProfileImage);
                            }else {
                                Toast.makeText(SignUp_Perents_Activity.this, "Error : "+task.getException(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });

    }

    public void ShowImage(Uri uri,CircleImageView imageView){
        Bitmap image=null;
        try {
            image=getBitmapFromUri(uri);
            imageView.setImageBitmap(image);
        } catch (IOException e) {
            // Toast.makeText(this, "Exception While Reading Image : "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==REQUEST_CODE_PROFILE_IMAGE && resultCode== RESULT_OK && data!=null){
            imgUriProfileImage = data.getData();
            ShowImage(imgUriProfileImage,profileImage);


        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileImage(Uri uri) {
        progressDialog.show();
        StorageReference reference = storage.getReference();
        StorageReference imageFolder = reference.child("Profile Images");
        StorageReference img = imageFolder.child(uid+"-profileImage");
        img.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl;
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                downloadUrl = urlTask.getResult();
                String DownloadImageUrl = downloadUrl.toString();
                ParentModelClass parentModelClass=new ParentModelClass();
                parentModelClass.setName(txtFullName.getText().toString());
                parentModelClass.setAddress(txtAddress.getText().toString());
                parentModelClass.setNumber(txtNumber.getText().toString());
                parentModelClass.setId(firebaseUser.getUid());
                parentModelClass.setNoOfKids(txtNoOfKids.getText().toString());
                parentModelClass.setProfileImageUrl(DownloadImageUrl);
                dref.child("Parents").child(firebaseUser.getUid()).setValue(parentModelClass)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    progressDialog.dismiss();
                                    Toast.makeText(SignUp_Perents_Activity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), Parent_Profile_Activity.class));
                                    finish();
                                }else {
                                    Toast.makeText(SignUp_Perents_Activity.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firebaseUser.delete();
                Toast.makeText(SignUp_Perents_Activity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}