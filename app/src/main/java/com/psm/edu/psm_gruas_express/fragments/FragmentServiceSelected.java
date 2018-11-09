package com.psm.edu.psm_gruas_express.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.psm.edu.psm_gruas_express.customView.VoteView;
import com.psm.edu.psm_gruas_express.InitActivity;
import com.psm.edu.psm_gruas_express.R;

public class FragmentServiceSelected extends Fragment {
    public static final String TAG = "grua_service_selected";
    private InitActivity activity;
    private Toolbar toolbarBottom;
    private VoteView voteView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_selected,container,false);
        activity  = (InitActivity) getActivity();
        toolbarBottom = (Toolbar) view.findViewById(R.id.toolbar_bottom);
        activity.ToolbarBottomEvent(toolbarBottom);
        voteView = new VoteView(getContext(),view.findViewById(R.id.viewVote));
        voteView.setFive(78363);
        voteView.setFour(20077);
        voteView.setThree(4719);
        voteView.setTwo(2419);
        voteView.setOne(4442);

        voteView.notifyChanged();

        return view;
    }

}
