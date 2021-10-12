package com.example.schoolsawari.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolsawari.Driver_Side_Map_Activity;
import com.example.schoolsawari.R;
import com.example.schoolsawari.Tracking_Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class All_Request_Adapter extends RecyclerView.Adapter<All_Request_Adapter.AdViewHolder> {
    FirebaseAuth firebaseAuth;
    DatabaseReference  driverReference,parentReference;
    ArrayList<BookingModelClass> adList;
    Context context;
    String rId;
    String uid;
    ParentModelClass parentModelClass;
    DriverModelClass driverModelClass;
    BookingModelClass addd = new BookingModelClass();
//    CreateSocietyClass student;
//    FirebaseAuth firebaseAuth;
//    FirebaseDatabase firebaseDatabase;
    DatabaseReference dref;
    public All_Request_Adapter(ArrayList<BookingModelClass> adList) {
        this.adList = adList;
    }

    @NonNull
    @Override
    public AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.request_view, parent, false);

        return new AdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdViewHolder holder, int position) {

        BookingModelClass ad =adList.get(position);

       if (ad!=null){
           firebaseAuth = FirebaseAuth.getInstance();

           parentReference = FirebaseDatabase.getInstance().getReference();
           parentReference.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   DataSnapshot parent=snapshot.child("Parents");
                   if (parent.exists()){
                       parentModelClass=parent.child(ad.getParentId()).getValue(ParentModelClass.class);

                   }

                   DataSnapshot drivers=snapshot.child("Drivers");
                   if (drivers.exists()){
                       driverModelClass  =drivers.child(ad.getDriverId()).getValue(DriverModelClass.class);
                   }

                   String uid = FirebaseAuth.getInstance().getUid();
                   if (uid.equals(ad.getDriverId())){
                       String pick=getAddress(ad.getPickupLocation().Longitude,ad.getPickupLocation().Latitude);
                       String drop=getAddress(ad.getDropOffLocation().Longitude,ad.getDropOffLocation().Latitude);
                       Picasso.get().load(parentModelClass.getProfileImageUrl()).into(holder.image);
                       holder.vName.setText(parentModelClass.getName());
                       holder.vNumber.setText(parentModelClass.getNumber());
                       holder.VPickUp.setText(pick);
                       holder.vDropOff.setText(drop);
                   }
                   else {
                       String pick=getAddress(ad.getPickupLocation().Longitude,ad.getPickupLocation().Latitude);
                       String drop=getAddress(ad.getDropOffLocation().Longitude,ad.getDropOffLocation().Latitude);
                       Picasso.get().load(driverModelClass.getProfileImageUrl()).into(holder.image);
                       holder.vName.setText(driverModelClass.getName());
                       holder.vNumber.setText(driverModelClass.getNumber());
                       holder.VPickUp.setText(pick);
                       holder.vDropOff.setText(drop);
                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });



////            holder.vdetail.setText(ad.getDetails());
       }
       // holder.image.setClipToOutline(true);
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }

    private String getAddress(Double longi,Double lat) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String address="";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, longi, 1);
            Address obj = addresses.get(0);
            String  add = obj.getAddressLine(0);
//optional
          /*  add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();*/

            Log.e("Location", "Address" + add);
            address=add;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return address;
    }
    public class AdViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView image;
        TextView vName,VPickUp,vDropOff,vNumber;
        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            vName = itemView.findViewById(R.id.name);
            image=itemView.findViewById(R.id.image);
            VPickUp=itemView.findViewById(R.id.pickUp);
            vDropOff=itemView.findViewById(R.id.dropOff);
            vNumber=itemView.findViewById(R.id.number);
            image.setShapeAppearanceModel(image.getShapeAppearanceModel().toBuilder()
                    .setTopRightCorner(CornerFamily.ROUNDED, 20)
                    .setTopLeftCorner(CornerFamily.ROUNDED, 20)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, 20)
                    .setBottomRightCorner(CornerFamily.ROUNDED, 20)
                    .build());
             uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adList.get(getAdapterPosition()).isAccepted()&& uid.equals(adList.get(getAdapterPosition()).getDriverId())) {
                        Intent i = new Intent(context, Driver_Side_Map_Activity.class);
                        i.putExtra("ad", adList.get(getAdapterPosition()));
                        context.startActivity(i);
                    }
                    else if(adList.get(getAdapterPosition()).isAccepted()&& uid.equals(adList.get(getAdapterPosition()).getParentId())){
                        Intent i = new Intent(context, Tracking_Activity.class);
                        i.putExtra("ad", adList.get(getAdapterPosition()));
                        context.startActivity(i);
                    }
                    else{



                        rId = adList.get(getAdapterPosition()).getRequestId();
                        addd=adList.get(getAdapterPosition());
                        if (!adList.get(getAdapterPosition()).isAccepted()){
                            new AlertDialog.Builder(v.getContext())
                                    .setMessage("Do You Want to Accept or Delete this Request?")
                                    .setTitle("Accept OR Delete")
                                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Calendar calendar = Calendar.getInstance();
                                            String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
                                            dref= FirebaseDatabase.getInstance().getReference();
                                            addd.setAccepted(true);
                                            addd.setDate(currentDate);
                                            dref.child("Booking").child(rId).setValue(addd);
                                            Toast.makeText(v.getContext(), "Request Accepted Successfully", Toast.LENGTH_SHORT).show();


                                        }
                                    }).setNegativeButton("Delete", new
                                    DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Booking");
                                            dR.child(rId).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(v.getContext(), "Delete Successful", Toast.LENGTH_SHORT).show();
                                                    }else {
                                                        Toast.makeText(v.getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                            dialog.dismiss();
                                        }
                                    }).show();
                        }

                    }
                }
            });


//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//                    rId = adList.get(getAdapterPosition()).getRequestId();
//                    addd=adList.get(getAdapterPosition());
//                    if (!adList.get(getAdapterPosition()).isAccepted()){
//                        new AlertDialog.Builder(v.getContext())
//                                .setMessage("Do You Want to Accept or Delete this Request?")
//                                .setTitle("Accept OR Delete")
//                                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                        dref= FirebaseDatabase.getInstance().getReference();
//                                        addd.setAccepted(true);
//                                        dref.child("Booking").child(rId).setValue(addd);
//                                        Toast.makeText(v.getContext(), "Request Accepted Successfully", Toast.LENGTH_SHORT).show();
//
//
//                                    }
//                                }).setNegativeButton("Delete", new
//                                DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Booking");
//                                        dR.child(rId).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()){
//                                                    Toast.makeText(v.getContext(), "Delete Successful", Toast.LENGTH_SHORT).show();
//                                                }else {
//                                                    Toast.makeText(v.getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//                                        });
//                                        dialog.dismiss();
//                                    }
//                                }).show();
//                    }
//                    return true;
//                }
//            });
        }
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
