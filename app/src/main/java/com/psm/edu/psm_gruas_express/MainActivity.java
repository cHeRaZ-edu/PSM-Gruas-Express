package com.psm.edu.psm_gruas_express;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.psm.edu.psm_gruas_express.models.NetCallback;
import com.psm.edu.psm_gruas_express.models.User;
import com.psm.edu.psm_gruas_express.networking.Networking;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = (Button) findViewById(R.id.BtnLogin);
        btnLoginWith = (Button) findViewById(R.id.BtnLoginWith);
        btnRegister = (Button) findViewById(R.id.BtnRegister);
        editTextUser = (EditText) findViewById(R.id.EditTextName);
        editTextPassword = (EditText) findViewById(R.id.EditTextPassword);

        ButtonEvent();
    }

    void ButtonEvent() {
        if(btnLogin!=null){
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //call web services login
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
            public void onWorkFinish(Object data) {
                final User user = (User)data;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"Bienvenido",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, InitActivity.class);
                        intent.putExtra(Register.JSON_USER, user.toJSON());
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



        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // ... Guardarlo en web services
                Toast.makeText(this,"Usuario " + user.getDisplayName(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this,InitActivity.class);
                startActivity(intent);
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(this,"No fue posible iniciar sesion",Toast.LENGTH_LONG).show();
            }
        }
    }
}
