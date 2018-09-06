package com.psm.edu.psm_gruas_express.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.psm.edu.psm_gruas_express.InitActivity;
import com.psm.edu.psm_gruas_express.R;

public class FragmentMain extends Fragment {
    public static final String TAG = "GUI_MAIN";
    Button BtnService;
    Button BtnMap;
    Button BtnStateEmergency;
    Button BtnMyService;
    Button BtnChat;
    Button BtnSettings;
    Button BtnLogout;
    InitActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gui_main,container,false);
        activity = (InitActivity)getActivity();
        BtnService = view.findViewById(R.id.BtnServices);
        BtnMap = view.findViewById(R.id.BtnMap);
        BtnStateEmergency = view.findViewById(R.id.BtnStateEmergency);
        BtnMyService = view.findViewById(R.id.BtnMyService);
        BtnChat = view.findViewById(R.id.BtnChat);
        BtnSettings = view.findViewById(R.id.BtnSettings);
        BtnLogout = view.findViewById(R.id.BtnLogout);
        EventButtons();
        return view;
    }

    private void EventButtons() {
        BtnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changeFragment(new FragmentServices(),FragmentServices.TAG);
            }
        });

        BtnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changeFragment(new FragmentMap(), FragmentMap.TAG);
            }
        });

        BtnStateEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changeFragment(new FragmentStateEmergency(), FragmentStateEmergency.TAG);
            }
        });

        BtnMyService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changeFragment(new FragmentMyService(),FragmentMyService.TAG);
            }
        });

        BtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changeFragment(new FragmentChat(),FragmentChat.TAG);
            }
        });

        BtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changeFragment(new FragmentSettings(), FragmentSettings.TAG);
            }
        });

        BtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.LogOut();
            }
        });
    }
}
