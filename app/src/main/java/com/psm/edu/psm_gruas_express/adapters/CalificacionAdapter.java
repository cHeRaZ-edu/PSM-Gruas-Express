package com.psm.edu.psm_gruas_express.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.psm.edu.psm_gruas_express.R;
import com.psm.edu.psm_gruas_express.models.Calificacion;
import com.psm.edu.psm_gruas_express.networking.Networking;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CalificacionAdapter extends BaseAdapter {
    List<Calificacion> calificacions;
    Context context;

    public CalificacionAdapter(List<Calificacion> calificacions, Context context) {
        this.calificacions = calificacions;
        this.context = context;
    }

    @Override
    public int getCount() {
        return calificacions.size();
    }

    @Override
    public Calificacion getItem(int position) {
        return calificacions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_comentario,null);
        }
        Calificacion c = calificacions.get(position);

        CircleImageView imgUser = convertView.findViewById(R.id.imgViewUserCommentary);
        RatingBar ratingBar = convertView.findViewById(R.id.ratingBarComemntary);
        TextView tvCommentary = convertView.findViewById(R.id.tvCommentary);

        Picasso.get()
                .load(Networking.SERVER_IP + c.getImageURL())
                .placeholder(R.drawable.ic_photo_camera_black_56dp)
                .resize(200, 200)
                .into(imgUser);
        ratingBar.setRating(c.getRating());
        tvCommentary.setText(c.getMessage());

        return convertView;
    }
}
