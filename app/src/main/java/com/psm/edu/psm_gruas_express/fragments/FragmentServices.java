package com.psm.edu.psm_gruas_express.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.psm.edu.psm_gruas_express.InitActivity;
import com.psm.edu.psm_gruas_express.R;
import com.psm.edu.psm_gruas_express.adapters.GruaServiceAdapter;
import com.psm.edu.psm_gruas_express.models.Grua;
import com.psm.edu.psm_gruas_express.models.NetCallback;
import com.psm.edu.psm_gruas_express.networking.Networking;

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

        UpdateService();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    void UpdateService() {
        if(!Networking.isNetworkAvailable(activity))
            return;

        new Networking(activity).execute(Networking.GET_ALL_GRUAS, new NetCallback() {
            @Override
            public void onWorkFinish(Object... objects) {
                final List<Grua> list_grua = (List<Grua>) objects[0];
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(list_grua.size() > 0) {
                            gruas.clear();
                            gruas.addAll(list_grua);
                            adapter.notifyDataSetChanged();
                        }
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
}
