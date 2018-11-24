package com.psm.edu.psm_gruas_express;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.psm.edu.psm_gruas_express.database.table.GruaSessionDataSource;
import com.psm.edu.psm_gruas_express.database.table.UserSessionDataSource;
import com.psm.edu.psm_gruas_express.models.Grua;
import com.psm.edu.psm_gruas_express.models.NetCallback;
import com.psm.edu.psm_gruas_express.models.User;
import com.psm.edu.psm_gruas_express.networking.Networking;
import com.psm.edu.psm_gruas_express.sharedprefences.SharedUtil;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private static final int REQUEST_CODE_CHANGE_COLOR = 12;
    // Choose authentication providers
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.FacebookBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build(),
            new AuthUI.IdpConfig.TwitterBuilder().build());
    Button btnLogin;
    Button btnRegister;
    Button btnLoginWith;
    EditText editTextUser;
    EditText editTextPassword;
    FloatingActionButton btnChangeColor;
    RelativeLayout relativeLayout;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutLogin);
        btnLogin = (Button) findViewById(R.id.BtnLogin);
        btnLoginWith = (Button) findViewById(R.id.BtnLoginWith);
        btnRegister = (Button) findViewById(R.id.BtnRegister);
        editTextUser = (EditText) findViewById(R.id.EditTextName);
        editTextPassword = (EditText) findViewById(R.id.EditTextPassword);
        btnChangeColor = (FloatingActionButton) findViewById(R.id.btnChangeColor);
        imageView = (ImageView) findViewById(R.id.imgViewPerfil);

        ButtonEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();

        relativeLayout.setBackgroundColor(SharedUtil.getBackgroundColorLogin(MainActivity.this));

        User user = new UserSessionDataSource(this).SelectTableUser();

        if(user != null) {
            if(user.getId() != -1) {
                Picasso.get()
                        .load(Networking.SERVER_IP + user.getImageURL())
                        .placeholder(R.drawable.ic_photo_camera_black_56dp)
                        .resize(200, 200)
                        .into(imageView);
            }
        }

        SessionUser();
    }

    void ButtonEvent() {
        if(btnChangeColor != null) {

            btnChangeColor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ChangeColor.class);
                    startActivity(intent);
                    //startActivityForResult(intent,REQUEST_CODE_CHANGE_COLOR);
                }
            });

        }
        if(btnLogin!=null){
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!Networking.isNetworkAvailable(MainActivity.this)) {
                        Toast.makeText(MainActivity.this,"No hay internet",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Login();
                }
            });
        }
        if(btnRegister!=null){
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // call intent
                    Intent intent = new Intent(MainActivity.this, Register.class);
                    startActivity(intent);
                }
            });
        }
        if(btnLoginWith!=null) {
            btnLoginWith.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!Networking.isNetworkAvailable(MainActivity.this)) {
                        Toast.makeText(MainActivity.this,"No hay internet",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Create and launch sign-in intent
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }
            });
        }
    }

    void SessionUser() {
        String nickname = SharedUtil.getUserNickname(this);
        String password = SharedUtil.getUserPassword(this);

        if(nickname.trim().isEmpty() || password.trim().isEmpty())
            return;

        if(!Networking.isNetworkAvailable(MainActivity.this)) {
            Toast.makeText(MainActivity.this,"No hay Conexion, modo Desconectado",Toast.LENGTH_SHORT).show();
            //Obtener mediante SQLite ************IMPORTANTE
            User user = new UserSessionDataSource(MainActivity.this).SelectTableUser();
            Grua grua = new GruaSessionDataSource(MainActivity.this).SelectTableUser();
            if(user.getId() == -1)
                return;
            Intent intent = new Intent(MainActivity.this, InitActivity.class);
            intent.putExtra(Register.JSON_USER, user.toJSON());
            intent.putExtra(Register.JSON_GRUA, grua.toJSON());
            startActivity(intent);
            return;
        }

        new Networking(MainActivity.this).execute(Networking.LOGIN, nickname, password, new NetCallback() {
            @Override
            public void onWorkFinish(Object... objects) {
                if(!(boolean)objects[2]) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Obtener mediante SQLite ************IMPORTANTE
                            User user = new UserSessionDataSource(MainActivity.this).SelectTableUser();
                            Grua grua = new GruaSessionDataSource(MainActivity.this).SelectTableUser();
                            if(user.getId() == -1)
                                return;
                            Toast.makeText(getApplicationContext(),"Entrando en modo desconectado", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, InitActivity.class);
                            intent.putExtra(Register.JSON_USER, user.toJSON());
                            intent.putExtra(Register.JSON_GRUA, grua.toJSON());
                            startActivity(intent);
                        }
                    });

                    return;
                }
                final User user = (User)objects[0];
                final Grua grua = (Grua) objects[1];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"Bienvenido",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, InitActivity.class);
                        intent.putExtra(Register.JSON_USER, user.toJSON());
                        intent.putExtra(Register.JSON_GRUA, grua.toJSON());
                        SharedUtil.setUserNickname(MainActivity.this,user.getNickname());
                        SharedUtil.setUserPassword(MainActivity.this, user.getPassword());
                        SharedUtil.setUserProvider(MainActivity.this, user.getProvider());
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

    void Login() {
        String nickname;
        String password;
        nickname = editTextUser.getText().toString();
        password = editTextPassword.getText().toString();
        //Validar edit vacios
        if(nickname.toString().trim().isEmpty() || password.toString().trim().isEmpty()) {
            Toast.makeText(MainActivity.this,"Falta llenar campos" , Toast.LENGTH_SHORT).show();
            return;
        }
        // call web services login
        new Networking(MainActivity.this).execute(Networking.LOGIN, nickname, password, new NetCallback() {
            @Override
            public void onWorkFinish(Object... objects) {
                if(!(boolean)objects[2])
                    return;
                final User user = (User)objects[0];
                final Grua grua = (Grua)objects[1];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"Bienvenido",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, InitActivity.class);
                        intent.putExtra(Register.JSON_USER, user.toJSON());
                        intent.putExtra(Register.JSON_GRUA, grua.toJSON());
                        SharedUtil.setUserNickname(MainActivity.this,user.getNickname());
                        SharedUtil.setUserPassword(MainActivity.this, user.getPassword());
                        SharedUtil.setUserProvider(MainActivity.this, user.getProvider());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_CHANGE_COLOR) {

            if(resultCode == RESULT_OK) {
                int Color =
                        data.getIntExtra(ChangeColor.KEY_COLOR,-1) != -1 ?
                                data.getIntExtra(ChangeColor.KEY_COLOR,-1) :
                                getResources().getColor(R.color.background_color);
                relativeLayout.setBackgroundColor(Color);
            }

        }

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser fb_user = FirebaseAuth.getInstance().getCurrentUser();
                final User user = new User(fb_user.getDisplayName(),
                        "",
                        null,
                        fb_user.getEmail(),
                        null,
                        "",
                        fb_user.getProviders().get(0));
                if(!Networking.isNetworkAvailable(MainActivity.this)) {
                    Toast.makeText(MainActivity.this,"No hay internet",Toast.LENGTH_SHORT).show();
                    return;
                }
                new Networking(this).execute(Networking.LOGIN_WITH, user.getName(), user.getProvider(), new NetCallback() {
                    @Override
                    public void onWorkFinish(Object... objects) {
                        final User temp_user = (User)objects[0];
                        final Grua temp_grua = (Grua)objects[1];

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(temp_user.getId() == -1) {
                                        RegisterUser(user);
                                    } else {
                                        Toast.makeText(MainActivity.this,"Bienvenido",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainActivity.this, InitActivity.class);
                                        intent.putExtra(Register.JSON_USER, temp_user.toJSON());
                                        intent.putExtra(Register.JSON_GRUA,temp_grua.toJSON());
                                        SharedUtil.setUserNickname(MainActivity.this,temp_user.getNickname());
                                        SharedUtil.setUserPassword(MainActivity.this, temp_user.getPassword());
                                        SharedUtil.setUserProvider(MainActivity.this, temp_user.getProvider());
                                        startActivity(intent);
                                    }
                                }
                            });

                    }

                    @Override
                    public void onMessageThreadMain(Object data) {
                        final String message = (String)data;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            } else {
                Toast.makeText(this,"No fue posible iniciar sesion",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void RegisterUser(final User user) {

        ///....
        // Checar si ya se registro - registar o no iniciar sesion
        // iniciar sesion
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }

        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.alert_dialog_input,null);

        builder.setTitle("Faltan informacion requerida")
                .setMessage("Completa los siguentes campos")
                .setView(view);

        final EditText editTxtEmailDialog = view.findViewById(R.id.editTxtEmailAlertDialog);
        final EditText editTxtNicknameDialog = view.findViewById(R.id.editTxtNicknameAlertDialog);
        final EditText editTxtPasswordDialog = view.findViewById(R.id.editTxtPasswordAlertDialog);

        if(user.getEmail() != null)
            editTxtEmailDialog.setVisibility(View.GONE);

        builder.setPositiveButton("Registar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = user.getEmail() != null ? user.getEmail() : editTxtEmailDialog.getText().toString();
                String nickname = editTxtNicknameDialog.getText().toString();
                String password = editTxtPasswordDialog.getText().toString();
                if(!isValidateRegisterProvider(email,nickname,password))
                    return;
                user.setEmail(email);
                user.setNickname(nickname);
                user.setPassword(password);



                //User aux = new User(user.getName(), user.getLastName(),nickname,email,password,"", user.getProvider());
                if(!Networking.isNetworkAvailable(MainActivity.this)) {
                    Toast.makeText(MainActivity.this,"No hay internet",Toast.LENGTH_SHORT).show();
                    return;
                }
                new Networking(MainActivity.this).execute(Networking.SIGNUP_PROVIDER, user,new NetCallback() {
                    @Override
                    public void onWorkFinish(Object... objects) {

                        final User user_temp = (User) objects[0];
                        if(user_temp.getId() != -1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,"Bienvenido",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, InitActivity.class);
                                    Grua grua = new Grua("","", "");
                                    grua.setId(-1);
                                    intent.putExtra(Register.JSON_USER, user_temp.toJSON());
                                    intent.putExtra(Register.JSON_GRUA,grua.toJSON());
                                    SharedUtil.setUserNickname(MainActivity.this,user_temp.getNickname());
                                    SharedUtil.setUserPassword(MainActivity.this, user_temp.getPassword());
                                    SharedUtil.setUserProvider(MainActivity.this, user_temp.getProvider());
                                    startActivity(intent);
                                }
                            });
                        }

                    }

                    @Override
                    public void onMessageThreadMain(Object data) {
                        final String message = (String) data;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //... Remover instancia iniciar con ...
                Toast.makeText(MainActivity.this,"Su registro se ha cancelado", Toast.LENGTH_SHORT).show();
            }
        })
        .setIcon(android.R.drawable.ic_dialog_info)
        .show();

    }

    boolean isValidateRegisterProvider(String email, String nickname, String password) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        //Validar datos vacios
        if(
                nickname.trim().isEmpty() ||
                email.trim().isEmpty() ||
                password.trim().isEmpty()
            ) {
            Toast.makeText(getApplicationContext(),"Faltan llenar campos",Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar email
        if (!email.matches(emailPattern)) {
            Toast.makeText(getApplicationContext(),"Correo electronico no es valido",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
