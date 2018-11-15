package com.psm.edu.psm_gruas_express.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.psm.edu.psm_gruas_express.InitActivity;
import com.psm.edu.psm_gruas_express.R;
import com.psm.edu.psm_gruas_express.fragments.FragmentServiceSelected;
import com.psm.edu.psm_gruas_express.models.Calificacion;
import com.psm.edu.psm_gruas_express.models.Grua;
import com.psm.edu.psm_gruas_express.models.NetCallback;
import com.psm.edu.psm_gruas_express.models.User;
import com.psm.edu.psm_gruas_express.networking.Networking;
import com.squareup.picasso.Picasso;

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
        final int pos = position;
        Grua grua = gruas.get(position);

        holder.tvName.setText(grua.getName());
        //Poner picasso URGENTE
        //holder.imgViewBackground ??????
        Picasso.get()
                .load(Networking.SERVER_IP + grua.getImageURL())
                .placeholder(R.drawable.ic_photo_camera_black_56dp)
                .resize(200, 200)
                .into(holder.imgBtnViewBackground);
        holder.imgBtnViewBackground.setOnClickListener(null);
        holder.imgBtnViewBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.grua_selected = gruas.get(pos);
                new Networking(activity).execute(Networking.FIND_USER_GRUA, activity.grua_selected.getId(), new NetCallback() {
                    @Override
                    public void onWorkFinish(Object... objects) {
                        activity.user_selected = (User)objects[0];
                        activity.c_selected = (Calificacion)objects[1];
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity.changeFragment(new FragmentServiceSelected(), FragmentServiceSelected.TAG);
                            }
                        });
                    }

                    @Override
                    public void onMessageThreadMain(Object data) {
                        final String message = (String) data;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity,message,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return gruas.size();
    }
}
