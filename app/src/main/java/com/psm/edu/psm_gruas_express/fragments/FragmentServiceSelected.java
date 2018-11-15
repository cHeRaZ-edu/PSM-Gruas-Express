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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.psm.edu.psm_gruas_express.InitActivity;
import com.psm.edu.psm_gruas_express.R;
import com.psm.edu.psm_gruas_express.adapters.CalificacionAdapter;
import com.psm.edu.psm_gruas_express.customView.VoteView;
import com.psm.edu.psm_gruas_express.models.Calificacion;
import com.psm.edu.psm_gruas_express.models.NetCallback;
import com.psm.edu.psm_gruas_express.networking.Networking;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FragmentServiceSelected extends Fragment {
    public static final String TAG = "grua_service_selected";
    private InitActivity activity;
    private Toolbar toolbarBottom;
    private VoteView voteView;
    private TextView tvNameService;
    private ImageView imgViewService;
    private TextView tvDesc;
    private TextView tvNameUser;
    private TextView tvPhone;
    private TextView tvEmail;
    private ImageButton btnSendMessage;
    private Button btnSeeComments;
    private  Button btnDejarCalificacion;
    private ListView lvCommentary;
    private List<Calificacion> calificacions = new ArrayList<>();
    private CalificacionAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_selected,container,false);
        activity  = (InitActivity) getActivity();
        toolbarBottom = (Toolbar) view.findViewById(R.id.toolbar_bottom);
        activity.ToolbarBottomEvent(toolbarBottom);

        tvNameService = (TextView) view.findViewById(R.id.tvNameServiceSelected);
        tvDesc = (TextView)view.findViewById(R.id.tvDescSelected);
        imgViewService = (ImageView)view.findViewById(R.id.imgViewServiceSelected);
        tvNameUser = (TextView)view.findViewById(R.id.tvNameUserSelected);
        tvPhone = (TextView)view.findViewById(R.id.tvPhoneSelected);
        tvEmail = (TextView)view.findViewById(R.id.tvEmailSelected);
        btnSendMessage = (ImageButton)view.findViewById(R.id.btnSendMessageSelected);
        btnSeeComments = (Button)view.findViewById(R.id.btnSeeCommentSelected);
        btnDejarCalificacion = (Button)view.findViewById(R.id.btnDejarCalificaicon);
        lvCommentary = (ListView)view.findViewById(R.id.lvCommentary);
        voteView = new VoteView(getContext(),view.findViewById(R.id.viewVote));

        tvNameUser.setText(activity.user_selected.getNickname());
        tvPhone.setText(activity.user_selected.getPhone());
        tvEmail.setText(activity.user_selected.getEmail());

        voteView.setFive(activity.c_selected.getVote_5());
        voteView.setFour(activity.c_selected.getVote_4());
        voteView.setThree(activity.c_selected.getVote_3());
        voteView.setTwo(activity.c_selected.getVote_2());
        voteView.setOne(activity.c_selected.getVote_1());
        voteView.notifyChanged();


        /*
        voteView.setFive(0);
        voteView.setFour(0);
        voteView.setThree(0);
        voteView.setTwo(0);
        voteView.setOne(1);
        voteView.notifyChanged();

        */


        adapter = new CalificacionAdapter(calificacions, activity);
        lvCommentary.setAdapter(adapter);

        btnDejarCalificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changeFragment(new FragmentCalificar(), FragmentCalificar.TAG);
            }
        });


        tvNameService.setText(activity.grua_selected.getName());
        tvDesc.setText(activity.grua_selected.getDescription());

        btnSeeComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Networking(activity).execute(Networking.GET_ALL_CALIFICACION, activity.grua_selected, new NetCallback() {
                    @Override
                    public void onWorkFinish(Object... objects) {
                        final List<Calificacion> list_calificacion = (List<Calificacion>)objects[0];
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                calificacions.clear();
                                calificacions.addAll(list_calificacion);
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
            }
        });

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changeFragment(new FragmentMessage(),FragmentMessage.TAG);
            }
        });

        if(activity.user_selected.getId() == activity.user.getId()) {
            btnSendMessage.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


        if(activity.grua_selected == null)
            return;
        if(activity.grua_selected.getId() == -1)
            return;

        Picasso.get()
                .load(Networking.SERVER_IP + activity.grua_selected.getImageURL())
                .placeholder(R.drawable.ic_photo_camera_black_56dp)
                .resize(200, 200)
                .into(imgViewService);
    }
}
