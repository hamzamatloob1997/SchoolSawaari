package com.example.schoolsawari.helper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolsawari.Driver_Booking_Activity;
import com.example.schoolsawari.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class All_Drivers_Adapter extends RecyclerView.Adapter<All_Drivers_Adapter.AdViewHolder> {

    ArrayList<DriverModelClass> driverList;
    Context context;
    public All_Drivers_Adapter(ArrayList<DriverModelClass> adList) {
        this.driverList = adList;
    }

    @NonNull
    @Override
    public AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.driver_view, parent, false);

        return new AdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdViewHolder holder, int position) {

        DriverModelClass ad=driverList.get(position);
        if (ad!=null){
            Picasso.get().load(driverList.get(position).getProfileImageUrl()).into(holder.image);
            holder.vModel.setText(ad.getVehicleModel());
            holder.vNumber.setText(ad.getNumber());
            holder.vAddress.setText(ad.getAddress());
            holder.vName.setText(ad.getName().toUpperCase());
        }
       // holder.image.setClipToOutline(true);
    }

    @Override
    public int getItemCount() {
        return driverList.size();
    }

    public class AdViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView image;
        TextView vName,vModel,vNumber,vAddress;
        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            vName = itemView.findViewById(R.id.name);
            vModel = itemView.findViewById(R.id.model);
            vNumber = itemView.findViewById(R.id.number);
            vAddress = itemView.findViewById(R.id.address);
            image=itemView.findViewById(R.id.image);
            image.setShapeAppearanceModel(image.getShapeAppearanceModel().toBuilder()
                    .setTopRightCorner(CornerFamily.ROUNDED, 20)
                    .setTopLeftCorner(CornerFamily.ROUNDED, 20)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, 20)
                    .setBottomRightCorner(CornerFamily.ROUNDED, 20)
                    .build());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                        Intent i = new Intent(context, Driver_Booking_Activity.class);
                        i.putExtra("driver",driverList.get(getAdapterPosition()));
                        context.startActivity(i);

                }
            });
        }
    }
}
