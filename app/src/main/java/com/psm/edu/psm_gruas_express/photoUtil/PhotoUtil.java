package com.psm.edu.psm_gruas_express.photoUtil;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;

public class PhotoUtil {
    public static final int PHOTO_SHOT = 300;
    public static final int STORAGE_IMAGE = 200;
    public static void ImageSelect(final Activity activity) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        builder.setTitle("Medio")
                .setMessage("Elige la opcion para la imagen")
                .setPositiveButton("Foto", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        activity.startActivityForResult(intent, PHOTO_SHOT);
                    }
                })
                .setNegativeButton("SD", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        Intent intent = null;
                        if(Build.VERSION.SDK_INT >= 19) {
                            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.setType("image/*");
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                        } else {
                            intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                        }
                        //Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                        //i.setType("image/*");
                        //i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                        activity.startActivityForResult(Intent.createChooser(intent,"Selecciona una foto"), STORAGE_IMAGE);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
}
