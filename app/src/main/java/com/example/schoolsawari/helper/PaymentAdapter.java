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

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.AdViewHolder> {

    ArrayList<PaymentModel> driverList;
    Context context;
    public PaymentAdapter(ArrayList<PaymentModel> adList) {
        this.driverList = adList;
    }

    @NonNull
    @Override
    public AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.payment_view, parent, false);

        return new AdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdViewHolder holder, int position) {

        PaymentModel ad=driverList.get(position);
        if (ad!=null){
            holder.vDate.setText(ad.getDate());
            holder.vTID.setText(ad.getTransactionId());
            holder.vPayment.setText(ad.getPrice());
            holder.vSource.setText(ad.getSource());
        }
       // holder.image.setClipToOutline(true);
    }

    @Override
    public int getItemCount() {
        return driverList.size();
    }

    public class AdViewHolder extends RecyclerView.ViewHolder{
        TextView vDate,vTID,vPayment,vSource;
        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            vDate = itemView.findViewById(R.id.date);
            vTID = itemView.findViewById(R.id.textTransaction);
            vPayment = itemView.findViewById(R.id.textPayment);
            vSource = itemView.findViewById(R.id.txtSource);


        }
    }
}
