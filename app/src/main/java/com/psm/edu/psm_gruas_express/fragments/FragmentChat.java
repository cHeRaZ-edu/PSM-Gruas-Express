package com.psm.edu.psm_gruas_express.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.psm.edu.psm_gruas_express.InitActivity;
import com.psm.edu.psm_gruas_express.R;

public class FragmentChat extends Fragment {
    public static final String TAG = "CHAT";
    InitActivity activity;
    Toolbar toolbarBottom;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat,container,false);
        activity = (InitActivity) getActivity();
        toolbarBottom = (Toolbar) view.findViewById(R.id.toolbar_bottom);
        activity.ToolbarBottomEvent(toolbarBottom);

        return view;
    }



}
