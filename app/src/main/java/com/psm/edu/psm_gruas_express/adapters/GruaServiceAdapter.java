package com.psm.edu.psm_gruas_express.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.psm.edu.psm_gruas_express.InitActivity;
import com.psm.edu.psm_gruas_express.R;
import com.psm.edu.psm_gruas_express.fragments.FragmentServiceSelected;
import com.psm.edu.psm_gruas_express.models.Grua;

import java.util.List;

public class GruaServiceAdapter extends RecyclerView.Adapter<GruaServiceAdapter.ViewHolder>{

    List<Grua> gruas;
    InitActivity activity;
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageButton imgBtnViewBackground;
        public TextView tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imgBtnViewBackground = itemView.findViewById(R.id.imgBtnViewBackground);
            this.tvName = itemView.findViewById(R.id.tvName);
        }
    }

    public GruaServiceAdapter(List<Grua> gruas, InitActivity activity) {
        this.gruas = gruas;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service,parent,false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Grua grua = gruas.get(position);

        holder.tvName.setText(grua.getName());
        //Poner picasso URGENTE
        //holder.imgViewBackground ??????
        holder.imgBtnViewBackground.setOnClickListener(null);
        holder.imgBtnViewBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changeFragment(new FragmentServiceSelected(), FragmentServiceSelected.TAG);
            }
        });

    }

    @Override
    public int getItemCount() {
        return gruas.size();
    }
}
