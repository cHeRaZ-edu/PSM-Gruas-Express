package com.psm.edu.psm_gruas_express.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.psm.edu.psm_gruas_express.InitActivity;
import com.psm.edu.psm_gruas_express.R;
import com.psm.edu.psm_gruas_express.adapters.ChatAdapter;
import com.psm.edu.psm_gruas_express.models.NetCallback;
import com.psm.edu.psm_gruas_express.models.User;
import com.psm.edu.psm_gruas_express.networking.Networking;

import java.util.ArrayList;
import java.util.List;

public class FragmentChat extends Fragment {
    public static final String TAG = "CHAT";
    InitActivity activity;
    Toolbar toolbarBottom;
    ListView lvChat;
    List<User> users = new ArrayList<>();
    ChatAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat,container,false);
        activity = (InitActivity) getActivity();
        toolbarBottom = (Toolbar) view.findViewById(R.id.toolbar_bottom);
        activity.ToolbarBottomEvent(toolbarBottom);
        lvChat = (ListView)view.findViewById(R.id.idlistViewChat);
        adapter = new ChatAdapter(activity,users);
        lvChat.setAdapter(adapter);

        lvChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = users.get(position);
                activity.user_selected = user;
                activity.changeFragment(new FragmentMessage(),FragmentMessage.TAG);
            }
        });


        new Networking(activity).execute(Networking.GET_USERS_SEND_MESSAGES, activity.user.getId(), new NetCallback() {
            @Override
            public void onWorkFinish(Object... objects) {
                final List<User> users_list = (List<User>) objects[0];
                users.clear();
                users.addAll(users_list);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
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


        return view;
    }



}
