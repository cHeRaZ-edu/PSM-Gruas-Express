package com.psm.edu.psm_gruas_express.fragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.psm.edu.psm_gruas_express.InitActivity;
import com.psm.edu.psm_gruas_express.R;
import com.psm.edu.psm_gruas_express.models.Calificacion;
import com.psm.edu.psm_gruas_express.models.NetCallback;
import com.psm.edu.psm_gruas_express.models.User;
import com.psm.edu.psm_gruas_express.networking.Networking;

public class FragmentCalificar extends Fragment {
    public static final String TAG = "fragment_vote";
    EditText editTxtComment;
    RatingBar starBar;
    FloatingActionButton btnSave;
    InitActivity activity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vote,container,false);
        activity = (InitActivity)getActivity();

        editTxtComment = (EditText) view.findViewById(R.id.editTxtCalificaicon);
        starBar = (RatingBar) view.findViewById(R.id.ratingBarVote);
        starBar.setFocusable(false);
        Drawable drawable = starBar.getProgressDrawable();
        drawable.setColorFilter(Color.parseColor("#FF4081"), PorterDuff.Mode.SRC_ATOP);
        btnSave = (FloatingActionButton)view.findViewById(R.id.btnSaveVote);

        starBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingBar.setRating((int)rating);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isValidate())
                    return;

                //table(idUser,idGrua,vote_5,vote_4,vote_3,vote_2,vote_1,message,imgPath)

                new Networking(activity).execute(Networking.CALIFICAR_GRUA, getCalificacion(), new NetCallback() {
                    @Override
                    public void onWorkFinish(Object... objects) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                new Networking(activity).execute(Networking.FIND_USER_GRUA, activity.grua_selected.getId(), new NetCallback() {
                                    @Override
                                    public void onWorkFinish(Object... objects) {
                                        activity.user_selected = (User)objects[0];
                                        activity.c_selected = (Calificacion)objects[1];
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                activity.changeFragment(new FragmentServiceSelected(), FragmentServiceSelected.TAG);
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
                    }

                    @Override
                    public void onMessageThreadMain(Object data) {
                        final String message = (String) data;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity,message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });


        return view;
    }

    boolean isValidate() {

        if(!(starBar.getRating() > 0.0f) || editTxtComment.getText().toString().isEmpty()) {
            Toast.makeText(activity,"Falta completar campos",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    Calificacion getCalificacion() {
        int vote_5 = starBar.getRating() == 5.0f ? 1 : 0;
        int vote_4 = starBar.getRating() == 4.0f ? 1 : 0;
        int vote_3 = starBar.getRating() == 3.0f ? 1 : 0;
        int vote_2 = starBar.getRating() == 2.0f ? 1 : 0;
        int vote_1 = starBar.getRating() == 1.0f ? 1 : 0;
        return new Calificacion(activity.user.getId(),activity.grua_selected.getId(),vote_5,vote_4,vote_3,vote_2,vote_1,editTxtComment.getText().toString(),activity.user.getImageURL());
    }
}
