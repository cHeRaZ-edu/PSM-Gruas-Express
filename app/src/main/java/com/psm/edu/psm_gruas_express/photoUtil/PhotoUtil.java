package com.psm.edu.psm_gruas_express.photoUtil;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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

    public static Bitmap ResizeBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if(width<1200 && height<1200)
            return bitmap;
        int perHeight = 100, perWidth = 100;
        if(width>1200)
            perWidth = 120000/width;
        if(height>1200)
            perHeight = 120000/height;
        int per_aux = perHeight > perWidth ? perWidth : perHeight;
        float per = (float)per_aux/100f;
        float scaleWidth = ((float) width * per) / width;
        float scaleHeight = ((float) height * per) / height;

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap temp = Bitmap.createBitmap(
                bitmap, 0, 0, width, height, matrix, false);
        bitmap.recycle();

        return temp;
    }
}

