package com.example.schoolsawari.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.schoolsawari.R;

import java.util.ArrayList;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AdViewHolder> {

    ArrayList<AttendanceModel> driverList;
    Context context;
    public AttendanceAdapter(ArrayList<AttendanceModel> adList) {
        this.driverList = adList;
    }

    @NonNull
    @Override
    public AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.attendance_view, parent, false);

        return new AdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdViewHolder holder, int position) {

        AttendanceModel ad=driverList.get(position);
        if (ad!=null){
            holder.vDate.setText(ad.getDate());
            holder.vPickHomeTime.setText(ad.getPickHomeTime());
            holder.vPickSchoolTime.setText(ad.getPickSchoolTime());
            holder.vDropHomeTime.setText(ad.getDropHomeTime());
            holder.vDropSchoolTime.setText(ad.getDropSchoolTime());
        }
       // holder.image.setClipToOutline(true);
    }

    @Override
    public int getItemCount() {
        return driverList.size();
    }

    public class AdViewHolder extends RecyclerView.ViewHolder{
        TextView vDate,vPickHomeTime,vPickSchoolTime,vDropHomeTime,vDropSchoolTime;
        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            vDate = itemView.findViewById(R.id.date);
            vPickHomeTime = itemView.findViewById(R.id.pickHome);
            vPickSchoolTime = itemView.findViewById(R.id.pickSchool);
            vDropHomeTime = itemView.findViewById(R.id.dropHome);
            vDropSchoolTime = itemView.findViewById(R.id.dropSchool);


        }
    }
}
