package com.psm.edu.psm_gruas_express.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.psm.edu.psm_gruas_express.R;
import com.psm.edu.psm_gruas_express.models.MessageChat;
import com.psm.edu.psm_gruas_express.networking.Networking;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends BaseAdapter {
    int idUser;
    Context context;
    List<MessageChat> messages;

    public MessageAdapter(Context context, List<MessageChat> messages, int idUser) {
        this.context = context;
        this.messages = messages;
        this.idUser = idUser;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public MessageChat getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message,null);
        }
        MessageChat message = messages.get(position);

        CardView cardView = convertView.findViewById(R.id.cardViewMessage);
        ImageView img = convertView.findViewById(R.id.imgViewMessage);
        TextView tvDesc = convertView.findViewById(R.id.tvDescMessage);
        TextView tvTime = convertView.findViewById(R.id.tvTimeMessage);

        tvDesc.setText(message.getMessage());
        tvTime.setText(message.getTime_send());


        if(idUser == message.getIdUserSend()) {
            //cambiar color
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.color_message));
            ((LinearLayout.LayoutParams)cardView.getLayoutParams()).gravity = Gravity.RIGHT;
        } else {
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
            ((LinearLayout.LayoutParams)cardView.getLayoutParams()).gravity = Gravity.LEFT;
        }

        if(message.getImageURL().trim().isEmpty()) {
            img.setVisibility(View.GONE);
        } else {
            //...picasso
            Picasso.get()
                    .load(Networking.SERVER_IP + message.getImageURL())
                    .placeholder(R.drawable.ic_photo_camera_black_56dp)
                    .resize(200, 200)
                    .into(img);
            img.setVisibility(View.VISIBLE);
        }


        return convertView;
    }
}
