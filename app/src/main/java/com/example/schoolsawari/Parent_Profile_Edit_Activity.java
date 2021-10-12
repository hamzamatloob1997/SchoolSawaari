package com.example.schoolsawari;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolsawari.helper.ParentModelClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.FileDescriptor;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.schoolsawari.Driver_Booking_Activity.getSavedObjectFromPreference;


public class Parent_Profile_Edit_Activity extends AppCompatActivity {
    EditText u_name, number, address,noKids;
    Button update;
    CircleImageView profile_image;
    private int REQUEST_CODE=111;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    String name,phone,location,kids;
    private String DownloadImageUrl;
    Uri imgUri;
    String uid;
    ProgressDialog progressDialog;
    ParentModelClass parentModelClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_profile__edit_);
        u_name = findViewById(R.id.name);
        number = findViewById(R.id.number);
        update = findViewById(R.id.update);
        profile_image = findViewById(R.id.profile_image);
        address = findViewById(R.id.address);
        noKids = findViewById(R.id.noKids);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Processing...");

        storage = FirebaseStorage.getInstance();
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
         parentModelClass = getSavedObjectFromPreference(Parent_Profile_Edit_Activity.this,"profile","parent",ParentModelClass.class);
        u_name.setText(parentModelClass.getName());
        number.setText(parentModelClass.getNumber());
        address.setText(parentModelClass.getAddress());
        noKids.setText(parentModelClass.getNoOfKids());
//        imgUrl = getIntent().getStringExtra("image");
        Picasso.get().load(parentModelClass.getProfileImageUrl()).into(profile_image);
        databaseReference = FirebaseDatabase.getInstance().getReference("Parents");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        updateUI(user);
         uid = user.getUid();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                name = u_name.getText().toString().toUpperCase();
                phone = number.getText().toString();
                kids = noKids.getText().toString();
                location = address.getText().toString();

                if (name.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please Enter Name", Toast.LENGTH_SHORT).show();
                }
                else if (phone.isEmpty()) {

                    Toast.makeText(getApplicationContext(), "Please Enter Phone Number", Toast.LENGTH_SHORT).show();


                }
                else if (kids.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please Enter No of Kids", Toast.LENGTH_SHORT).show();
                }
                else if (location.isEmpty()){

                    Toast.makeText(getApplicationContext(), "Please Enter Address", Toast.LENGTH_SHORT).show();
                }
                else if (phone.length() != 11) {
                    number.setError("Please Enter Complete Number ");
                    return;
                }
                else if (imgUri == null) {



                    parentModelClass.setNoOfKids(kids);
                    parentModelClass.setName(name);
                    parentModelClass.setNumber(phone);
                    parentModelClass.setAddress(location);

                    databaseReference.child(uid).setValue(parentModelClass);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Your Profile Edit Successfully", Toast.LENGTH_SHORT).show();

                }
                else {

                    uploadImage(imgUri);
                }
            }
        });


    }

    private void updateUI(FirebaseUser user) {
    }

    public void ShowImage(Uri uri){
        Bitmap image=null;
        try {
            image=getBitmapFromUri(uri);
            profile_image.setImageBitmap(image);
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

        if (requestCode==REQUEST_CODE && resultCode== RESULT_OK && data!=null){
            imgUri = data.getData();
            ShowImage(imgUri);


        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void  uploadImage(Uri uri)
    {progressDialog.show();
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
                DownloadImageUrl = downloadUrl.toString();

                            databaseReference = FirebaseDatabase.getInstance().getReference("Parents");


                parentModelClass.setNoOfKids(kids);
                parentModelClass.setProfileImageUrl(DownloadImageUrl);
                parentModelClass.setName(name);
                parentModelClass.setNumber(phone);
                parentModelClass.setAddress(location);

                            databaseReference.child(uid).setValue(parentModelClass);
//                            databaseReference.child(Id).child("Email").setValue(email);
//                            databaseReference.child(Id).child("Name").setValue(name);
//                            databaseReference.child(Id).child("Phone").setValue(phone);
//                            databaseReference.child(Id).child("Location").setValue(location);
//                            databaseReference.child(Id).child("Date").setValue(currentDate);


                            Toast.makeText(getApplicationContext(), "Your Profile Edit Successfully", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();


                    }



        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static void saveObjectToSharedPreference(Context context, String preferenceFileName, String serializedObjectKey, Object object) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        final Gson gson = new Gson();
        String serializedObject = gson.toJson(object);
        sharedPreferencesEditor.putString(serializedObjectKey, serializedObject);
        sharedPreferencesEditor.apply();
    }
}