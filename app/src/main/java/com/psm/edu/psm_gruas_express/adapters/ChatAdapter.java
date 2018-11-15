package com.psm.edu.psm_gruas_express.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.psm.edu.psm_gruas_express.InitActivity;
import com.psm.edu.psm_gruas_express.R;
import com.psm.edu.psm_gruas_express.models.User;
import com.psm.edu.psm_gruas_express.networking.Networking;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatAdapter extends BaseAdapter {
    Context context;
    List<User> users;

    public ChatAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public User getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_inbox,null);
        }

        User user = users.get(position);

        ImageView imgView = convertView.findViewById(R.id.imgViewUserChat);
        TextView tvUserName = convertView.findViewById(R.id.tvUserNameChat);

        tvUserName.setText(user.getNickname());
        Picasso.get()
                .load(Networking.SERVER_IP + user.getImageURL())
                .placeholder(R.drawable.ic_photo_camera_black_56dp)
                .resize(200, 200)
                .into(imgView);


        return convertView;
    }
}
