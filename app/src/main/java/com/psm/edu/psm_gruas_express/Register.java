package com.psm.edu.psm_gruas_express;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.psm.edu.psm_gruas_express.models.Grua;
import com.psm.edu.psm_gruas_express.models.NetCallback;
import com.psm.edu.psm_gruas_express.models.User;
import com.psm.edu.psm_gruas_express.networking.Networking;
import com.psm.edu.psm_gruas_express.photoUtil.PhotoUtil;
import com.psm.edu.psm_gruas_express.sharedprefences.SharedUtil;

import java.io.IOException;

public class Register extends AppCompatActivity {
    private Register instance;
    private ImageView imgPerfil;
    private EditText editTxtName;
    private EditText editTxtLastName;
    private EditText editTxtNickname;
    private EditText editTxtEmail;
    private EditText editTxtPassword;
    private EditText editTxtConfirmPassword;
    private Button btnRegister;
    private FloatingActionButton btnPhoto;
    private boolean isPhoto = false;
    private Bitmap bitmap;
    public static final String JSON_USER = "json_user";
    public static final String JSON_GRUA = "json_grua";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance  = this;
        setContentView(R.layout.activity_register);

        inflateView();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isValidateRegister())
                    return;

                    User user = new User(
                            editTxtName.getText().toString(),
                            editTxtLastName.getText().toString(),
                            editTxtNickname.getText().toString(),
                            editTxtEmail.getText().toString(),
                            editTxtPassword.getText().toString()
                    );
                    // Register ...
                    if(!Networking.isNetworkAvailable(Register.this)) {
                        Toast.makeText(Register.this,"No hay internet",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new Networking(Register.this).execute(Networking.SIGNUP, user,bitmap, new NetCallback() {
                        @Override
                        public void onWorkFinish(Object... objects) {
                            final User user = (User)objects[0];
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"Bienvenido" , Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Register.this,InitActivity.class);
                                    intent.putExtra(JSON_USER, user.toJSON());
                                    Grua grua = new Grua("","", "");
                                    grua.setId(-1);
                                    intent.putExtra(JSON_GRUA, grua.toJSON());
                                    SharedUtil.setUserNickname(Register.this,user.getNickname());
                                    SharedUtil.setUserPassword(Register.this, user.getPassword());
                                    SharedUtil.setUserProvider(Register.this, user.getProvider());
                                    startActivity(intent);
                                }
                            });

                        }

                        @Override
                        public void onMessageThreadMain(Object data) {
                            final String message = (String)data;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),message , Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
            }
        });

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoUtil.ImageSelect(instance);
            }
        });

    }

    private void inflateView() {
        imgPerfil = (ImageView) findViewById(R.id.imgPerfilRegister);
        editTxtName = (EditText) findViewById(R.id.editTxtNameRegister);
        editTxtLastName = (EditText) findViewById(R.id.editTxtLastNameRegister);
        editTxtNickname = (EditText) findViewById(R.id.editTxtNicknameRegister);
        editTxtEmail = (EditText) findViewById(R.id.editTxtEmailRegister);
        editTxtPassword = (EditText) findViewById(R.id.editTxtPasswordRegister);
        editTxtConfirmPassword = (EditText) findViewById(R.id.editTxtConfirmPasswordRegister);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnPhoto = (FloatingActionButton) findViewById(R.id.btnPhotoRegister);
    }

    private boolean isValidateRegister() {
        String emailString = editTxtEmail.getText().toString().trim();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        //Validar datos vacios
        if(
            editTxtNickname.getText().toString().trim().isEmpty() ||
            editTxtName.getText().toString().trim().isEmpty() ||
            editTxtLastName.getText().toString().trim().isEmpty() ||
            emailString.trim().isEmpty() ||
            editTxtPassword.getText().toString().trim().isEmpty() ||
            editTxtConfirmPassword.getText().toString().trim().isEmpty()
        ) {
            Toast.makeText(getApplicationContext(),"Faltan llenar campos",Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar email
        if (!emailString.matches(emailPattern)) {
            Toast.makeText(getApplicationContext(),"Correo electronico no es valido",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!editTxtPassword.getText().toString().equals(editTxtConfirmPassword.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Contraseña no correcta", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!isPhoto) {
            Toast.makeText(getApplicationContext(), "No ha seleccionado una foto de perfil", Toast.LENGTH_SHORT).show();
            return false;
        }

        return  true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && PhotoUtil.PHOTO_SHOT == requestCode) {
            isPhoto = true;
            bitmap = (Bitmap) data.getExtras().get("data");
            bitmap = PhotoUtil.ResizeBitmap(bitmap);
            imgPerfil.setImageBitmap(bitmap);

        } else if(resultCode == RESULT_OK && PhotoUtil.STORAGE_IMAGE == requestCode) {
            isPhoto = true;
            Uri uri = data.getData();
            try {
                bitmap =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                bitmap = PhotoUtil.ResizeBitmap(bitmap);
                imgPerfil.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
