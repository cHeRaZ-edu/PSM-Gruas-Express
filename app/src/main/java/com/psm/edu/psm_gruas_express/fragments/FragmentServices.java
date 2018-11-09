package com.psm.edu.psm_gruas_express.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.psm.edu.psm_gruas_express.InitActivity;
import com.psm.edu.psm_gruas_express.R;
import com.psm.edu.psm_gruas_express.adapters.GruaServiceAdapter;
import com.psm.edu.psm_gruas_express.models.Grua;

import java.util.ArrayList;
import java.util.List;

public class FragmentServices extends Fragment {
    public static final String TAG = "Services";
    InitActivity activity;
    RecyclerView recyclerView;
    GruaServiceAdapter adapter;
    List<Grua> gruas;
    Toolbar toolbarBottom;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services,container,false);
        activity = (InitActivity) getActivity();
        toolbarBottom = (Toolbar) view.findViewById(R.id.toolbar_bottom);
        activity.ToolbarBottomEvent(toolbarBottom);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_gruas_services);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        gruas = new ArrayList<>();
        adapter = new GruaServiceAdapter(gruas,activity);
        //... Leer servicios desde webservices y mostrarlos en el reclcyer view
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        for(int i = 0;i<4;i++) {
            gruas.add(new Grua("My Services", "endpoint"));
        }

        adapter.notifyDataSetChanged();

    }
}
