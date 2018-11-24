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
import com.psm.edu.psm_gruas_express.R;
import com.psm.edu.psm_gruas_express.models.Grua;
import com.psm.edu.psm_gruas_express.models.NetCallback;
import com.psm.edu.psm_gruas_express.networking.Networking;
import com.psm.edu.psm_gruas_express.photoUtil.PhotoUtil;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class FragmentMyService extends Fragment {
    public static final String TAG = "MyService";
    InitActivity activity;
    Toolbar toolbarBottom;
    ImageView imgViewMyService;
    EditText editTxtName;
    EditText editTxtDesc;
    TextView tvUserName;
    TextView tvPhone;
    TextView tvEmail;
    ImageButton btnPhoto;
    FloatingActionButton btnSave;
    Bitmap bitmap;
    boolean isPhoto = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_service,container,false);
        activity = (InitActivity) getActivity();
        toolbarBottom = (Toolbar) view.findViewById(R.id.toolbar_bottom);
        imgViewMyService = (ImageView) view.findViewById(R.id.imgViewMyService);
        editTxtName = (EditText) view.findViewById(R.id.editTxtNameMyService);
        editTxtDesc = (EditText) view.findViewById(R.id.editTxtDescMyService);
        tvUserName = (TextView) view.findViewById(R.id.tvUserNameMyService);
        tvPhone = (TextView) view.findViewById(R.id.tvPhoneMyService);
        tvEmail = (TextView) view.findViewById(R.id.tvEmailMyService);
        btnPhoto = (ImageButton) view.findViewById(R.id.btnPhotoMyService);
        btnSave = (FloatingActionButton) view.findViewById(R.id.btnSaveMyService);

        activity.ToolbarBottomEvent(toolbarBottom);

        tvUserName.setText(activity.user.getName());
        tvPhone.setText(activity.user.getPhone());
        tvEmail.setText(activity.user.getEmail());
        if(activity.grua.getId() != -1) {
            editTxtName.setText(activity.grua.getName());
            editTxtDesc.setText(activity.grua.getDescription());
        } else
            Toast.makeText(activity,"Su servicio no esta publicado", Toast.LENGTH_SHORT).show();


        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoUtil.ImageSelect(activity);
            }
        });

        Bitmap bitmap = PhotoUtil.getBitmap(activity.imgBackgroundService.getDrawable(), Bitmap.Config.RGB_565);
        if(bitmap != null) {
            imgViewMyService.setImageBitmap(bitmap);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Networking.isNetworkAvailable(activity)) {
                    Toast.makeText(activity,"No hay internet",Toast.LENGTH_SHORT).show();
                    return;
                }
                String nameService = editTxtName.getText().toString();
                String desc = editTxtDesc.getText().toString();

                if(
                        nameService.trim().isEmpty()||
                                desc.trim().isEmpty() ||
                                !isPhoto
                        )
                {
                    Toast.makeText(activity.getApplicationContext(),"Falta campos que debe llenar",Toast.LENGTH_SHORT).show();
                    return;
                }
                activity.grua.setName(nameService);
                activity.grua.setDescription(desc);
                final int temp_id = activity.grua.getId();
                //... crear o actualizar servicio de grua
                new Networking(activity).execute(Networking.UPDATE_GRUA, activity.user.getId(), activity.grua, isPhoto ? bitmap : null, new NetCallback() {
                    @Override
                    public void onWorkFinish(Object... objects) {
                        activity.grua = (Grua)objects[0];
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String message = temp_id == -1 ? "Su servicio se ha publicado" : "Su servicio se ha actualizado";
                                Toast.makeText(activity,message, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
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
            isPhoto = true;
            bitmap = PhotoUtil.ResizeBitmap((Bitmap) data.getExtras().get("data"));
            imgViewMyService.setImageBitmap(bitmap);

        } else if(resultCode == RESULT_OK && PhotoUtil.STORAGE_IMAGE == requestCode) {
            isPhoto = true;
            Uri uri = data.getData();
            try {
                bitmap = PhotoUtil.ResizeBitmap(MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri));
                imgViewMyService.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
