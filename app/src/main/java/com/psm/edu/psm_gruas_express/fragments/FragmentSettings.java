package com.psm.edu.psm_gruas_express.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.psm.edu.psm_gruas_express.InitActivity;
import com.psm.edu.psm_gruas_express.NotifyUtil.NotifyUtil;
import com.psm.edu.psm_gruas_express.R;
import com.psm.edu.psm_gruas_express.models.NetCallback;
import com.psm.edu.psm_gruas_express.models.User;
import com.psm.edu.psm_gruas_express.networking.Networking;
import com.psm.edu.psm_gruas_express.photoUtil.PhotoUtil;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class FragmentSettings extends Fragment {
    public static final String TAG = "Settigns";
    InitActivity activity;
    Toolbar toolbarBottom;
    CircleImageView imgViewPerfil;
    ImageView imgViewbackground;
    TextView tvNickname;
    TextView tvEmail;
    EditText editTxtName;
    EditText editTxtLastName;
    ImageButton btnSearchImgPerfil;
    ImageButton btnSearchImgBackground;
    EditText editTxtPhone;
    FloatingActionButton btnSave;
    enum SELECT_IMAGE{
        PERFIL,
        BACKGROUND
    };
    SELECT_IMAGE select_image;
    Bitmap bitmapImagePerfil = null, bitmapImageBackground = null;
    boolean isUpdloadImagePerfil = false, isUpdloadImageBackground = false, enabeldName = false;
    List<User> users = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,container,false);
        activity = (InitActivity) getActivity();
        toolbarBottom = (Toolbar) view.findViewById(R.id.toolbar_bottom);
        activity.ToolbarBottomEvent(toolbarBottom);

        imgViewPerfil = (CircleImageView) view.findViewById(R.id.imgViewUserSetting);
        imgViewbackground = (ImageView) view.findViewById(R.id.imgViewBackgroundSetting);
        tvNickname = (TextView) view.findViewById(R.id.tvNicknameSetting);
        editTxtName = (EditText) view.findViewById(R.id.editTxtNameSetting);
        editTxtLastName = (EditText) view.findViewById(R.id.editTxtLastNameSetting);
        btnSearchImgPerfil = (ImageButton) view.findViewById(R.id.imgBtnUserSetting);
        btnSearchImgBackground = (ImageButton) view.findViewById(R.id.imgBtnBackgroundSetting);
        editTxtPhone = (EditText) view.findViewById(R.id.editTxtPhoneSetting);
        tvEmail = (TextView) view.findViewById(R.id.tvEmailSetting);
        btnSave = (FloatingActionButton) view.findViewById(R.id.btnSaveSetting);


        if(!activity.user.getProvider().trim().isEmpty()) {
            enabeldName = false;
            editTxtName.setFocusable(false);
        } else
            enabeldName = true;


        tvNickname.setText(activity.user.getNickname());
        editTxtName.setText(activity.user.getName());
        editTxtLastName.setText(activity.user.getLastName());
        tvEmail.setText(activity.user.getEmail());
        editTxtPhone.setText(activity.user.getPhone());


        Bitmap bitmap = PhotoUtil.getBitmap(activity.imgPerfil.getDrawable(), Bitmap.Config.RGB_565);
        if(bitmap != null) {
            imgViewPerfil.setImageBitmap(bitmap);
        }
        Bitmap bitmap2 = PhotoUtil.getBitmap(activity.imgBackground.getDrawable(), Bitmap.Config.RGB_565);
        if(bitmap2 != null) {
            imgViewbackground.setImageBitmap(bitmap2);
        }




        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        btnSearchImgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_image = SELECT_IMAGE.PERFIL;
                PhotoUtil.ImageSelect(activity);
            }
        });

        btnSearchImgBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_image = SELECT_IMAGE.BACKGROUND;
                PhotoUtil.ImageSelect(activity);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Networking.isNetworkAvailable(activity)) {
                    Toast.makeText(activity,"No hay internet",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(
                    editTxtName.getText().toString().trim().isEmpty() ||
                    editTxtLastName.getText().toString().trim().isEmpty()||
                    editTxtPhone.getText().toString().trim().isEmpty()
                    ) {
                    Toast.makeText(activity,"Hay campos vacios", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(enabeldName) {
                    activity.user.setName(editTxtName.getText().toString());
                }
                activity.user.setLastName(editTxtLastName.getText().toString());
                activity.user.setPhone(editTxtPhone.getText().toString());


                new Networking(activity).execute(Networking.UPDATE_USER, activity.user, isUpdloadImagePerfil?bitmapImagePerfil:null, isUpdloadImageBackground?bitmapImageBackground:null, new NetCallback() {
                    @Override
                    public void onWorkFinish(Object... objects) {
                        activity.user = (User) objects[0];
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Actualizar drawerlayout
                                activity.UpdateDrawerLayout();
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



    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && PhotoUtil.PHOTO_SHOT == requestCode) {

            SetBitmapImagen(
                    PhotoUtil.ResizeBitmap((Bitmap) data.getExtras().get("data"))
            );

        } else if(resultCode == RESULT_OK && PhotoUtil.STORAGE_IMAGE == requestCode) {
            Uri uri = data.getData();
            try {
                SetBitmapImagen(
                        PhotoUtil.ResizeBitmap(MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri))
                        );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void SetBitmapImagen(Bitmap bitmap) {
        if(select_image == SELECT_IMAGE.PERFIL) {
            bitmapImagePerfil = bitmap;
            imgViewPerfil.setImageBitmap(bitmap);
            isUpdloadImagePerfil = true;
        } else if(select_image == SELECT_IMAGE.BACKGROUND) {
            bitmapImageBackground = bitmap;
            imgViewbackground.setImageBitmap(bitmap);
            isUpdloadImageBackground = true;
        }
    }

}
