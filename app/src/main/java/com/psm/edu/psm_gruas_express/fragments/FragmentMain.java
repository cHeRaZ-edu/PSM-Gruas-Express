package com.psm.edu.psm_gruas_express.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.psm.edu.psm_gruas_express.ActivityMap;
import com.psm.edu.psm_gruas_express.InitActivity;
import com.psm.edu.psm_gruas_express.R;
import com.psm.edu.psm_gruas_express.Register;

public class FragmentMain extends Fragment {
    public static final String TAG = "GUI_MAIN";
    ImageButton BtnService;
    ImageButton BtnMap;
    ImageButton BtnStateEmergency;
    ImageButton BtnMyService;
    ImageButton BtnChat;
    ImageButton BtnSettings;
    ImageButton BtnLogout;
    InitActivity activity;
    Toolbar toolbarBottom;

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
        toolbarBottom = (Toolbar) view.findViewById(R.id.toolbar_bottom);
        EventButtons();
        activity.ToolbarBottomEvent(toolbarBottom);
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
                Intent intent = new Intent(activity, ActivityMap.class);
                intent.putExtra(Register.JSON_USER, activity.user.toJSON());
                intent.putExtra(InitActivity.KEY_INVISIBLE,activity.invisible);
                intent.putExtra(InitActivity.KEY_MODE, activity.mode_user);
                getActivity().startActivityForResult(intent,InitActivity.REQUEST_CODE_GET_MODES);
                //activity.changeFragment(new FragmentMap(), FragmentMap.TAG);
            }
        });

        BtnStateEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ActivityMap.class);
                intent.putExtra(Register.JSON_USER, activity.user.toJSON());
                intent.putExtra(InitActivity.KEY_INVISIBLE,activity.invisible);
                intent.putExtra(InitActivity.KEY_MODE, activity.mode_user);
                getActivity().startActivityForResult(intent, InitActivity.REQUEST_CODE_GET_MODES);
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
