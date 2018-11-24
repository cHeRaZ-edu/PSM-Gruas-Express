package com.psm.edu.psm_gruas_express;

import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.psm.edu.psm_gruas_express.BackgroundUtil.ServiceUtil;
import com.psm.edu.psm_gruas_express.database.table.GruaSessionDataSource;
import com.psm.edu.psm_gruas_express.database.table.UserSessionDataSource;
import com.psm.edu.psm_gruas_express.fragments.FragmentCalificar;
import com.psm.edu.psm_gruas_express.fragments.FragmentChat;
import com.psm.edu.psm_gruas_express.fragments.FragmentMain;
import com.psm.edu.psm_gruas_express.fragments.FragmentMap;
import com.psm.edu.psm_gruas_express.fragments.FragmentMyService;
import com.psm.edu.psm_gruas_express.fragments.FragmentServiceSelected;
import com.psm.edu.psm_gruas_express.fragments.FragmentServices;
import com.psm.edu.psm_gruas_express.fragments.FragmentSettings;
import com.psm.edu.psm_gruas_express.geolocalizacion.GeoManager;
import com.psm.edu.psm_gruas_express.models.Calificacion;
import com.psm.edu.psm_gruas_express.models.Grua;
import com.psm.edu.psm_gruas_express.models.NetCallback;
import com.psm.edu.psm_gruas_express.models.Position;
import com.psm.edu.psm_gruas_express.models.User;
import com.psm.edu.psm_gruas_express.networking.Networking;
import com.psm.edu.psm_gruas_express.sharedprefences.SharedUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class InitActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_GET_MODES = 3;
    public static final String KEY_INVISIBLE = "invisible";
    public static final String KEY_MODE = "mode";
    public ImageView imgBackgroundService;
    DrawerLayout drawerLayout;
    Toolbar toolbarActionBar;
    ActionBarDrawerToggle mdrawerToggle;
    NavigationView navigationView;
    TextView tvNickname;
    TextView tvEmail;
    public CircleImageView imgPerfil;
    public ImageView imgBackground;
    Switch switchInvisible;
    Switch switchMode;
    GestureOverlayView gestureView;
    //Objeto el cual tendra nuestras gesturas que creemos
    // nosotros mismos con la app "Gesture Builder"
    GestureLibrary m_gestureLibrary;
    public String TAG="";
    public User user;
    public Grua grua = new Grua("","","");
    public Grua grua_selected = null;
    public User user_selected = null;
    public Calificacion c_selected = null;
    public int mode_user = 0;
    public int invisible = 0;
    private  boolean hidden_gesture = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        //Find id view
        drawerLayout = (DrawerLayout) findViewById(R.id.idDrawerLayout);
        toolbarActionBar = (Toolbar) findViewById(R.id.toolbar_top);
        navigationView = (NavigationView) findViewById(R.id.nav_panel);
        View header = navigationView.getHeaderView(0);
        tvNickname = (TextView) header.findViewById(R.id.tvNicknameNav);
        tvEmail = (TextView) header.findViewById(R.id.tvEmailNav);
        imgPerfil = (CircleImageView) header.findViewById(R.id.imgPerfilNav);
        imgBackground = (ImageView) header.findViewById(R.id.imgBackgroundNav);
        switchInvisible = (Switch) header.findViewById(R.id.switchInvisible);
        switchMode = (Switch) header.findViewById(R.id.switchEmergency);
        gestureView = (GestureOverlayView) findViewById(R.id.gesture_view);
        imgBackgroundService = new ImageView(this);
        imgBackgroundService.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_photo_camera_black_56dp));

        EventModeUser();

        //Navegation bar icon burger
        setSupportActionBar(toolbarActionBar);
        mdrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbarActionBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mdrawerToggle.syncState();

        NavEventMenuItemView();

        m_gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        // Si no se pudo cargar nuestra libreria (coleccion) de gesturas
        if (!m_gestureLibrary.load()) {
            finish();
        }

        EventGesture();

        if (TAG.equals(""))
            changeFragment(new FragmentMain(), FragmentMain.TAG);
    }

    @Override
    protected void onStart() {
        super.onStart();



        Intent intent = getIntent();


        if(intent != null) {
            String json_user =  intent.getStringExtra(Register.JSON_USER);
            String json_grua = intent.getStringExtra(Register.JSON_GRUA);
            if(json_user == null || json_grua == null) {
                Toast.makeText(this,"No se encontro datos del usuario", Toast.LENGTH_SHORT).show();
                ForceLogOut();//Cierra por completo la sesion del usuario
            }
            user = new Gson().fromJson(json_user, User.class);
            grua = new Gson().fromJson(json_grua, Grua.class);
            tvNickname.setText("Usuario: " + user.getNickname());
            tvEmail.setText("Correo: " + user.getEmail());
            SaveSessionSQLite();
            if(user.getId() != -1) {
                Intent intentService = new Intent(this, ServiceUtil.class);
                intentService.putExtra(Register.JSON_USER,user.toJSON());
                startService(intentService);
            }

            UpdateDrawerLayout();



        }

        LatLng bestLocation = GeoManager.getCurrentLocation(InitActivity.this);
        if(bestLocation == null) {
            Toast.makeText(getApplicationContext(),"No hay ningun provedor geolocalizacion habilitado",Toast.LENGTH_SHORT).show();
            return;
        }


        new Networking(InitActivity.this, true).execute(Networking.UPDATE_GEO_USER, user.getId(), new Position(bestLocation), new NetCallback() {
            @Override
            public void onWorkFinish(Object... objects) {
                invisible = (int)objects[0];
                mode_user = (int)objects[1];

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mode_user == 1)
                            switchMode.setChecked(true);
                        else
                            switchMode.setChecked(false);

                        if(invisible == 1)
                            switchInvisible.setChecked(true);
                        else
                            switchInvisible.setChecked(false);
                    }
                });

                return;
            }

            @Override
            public void onMessageThreadMain(Object data) {
                final String message = (String) data;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //Eventes
    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            switch (TAG) {
                case FragmentMain.TAG:
                    LogOut();
                    return;
                case FragmentServiceSelected.TAG:
                    changeFragment(new FragmentServices(),FragmentServices.TAG);
                    return;
                case FragmentCalificar.TAG:
                    changeFragment(new FragmentServiceSelected(),FragmentServiceSelected.TAG);
                    return;
                default:
                    changeFragment(new FragmentMain(),FragmentMain.TAG);

            }
            }
    }

    private void NavEventMenuItemView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()) {
                    case R.id.idInicio:
                        changeFragment(new FragmentMain(),FragmentMain.TAG);
                        break;
                    case R.id.idMiServicio:
                        changeFragment(new FragmentMyService(),FragmentMyService.TAG);
                        break;
                    case R.id.idServicios:
                        changeFragment(new FragmentServices(),FragmentServices.TAG);
                        break;
                    case R.id.idMaps:
                        Intent intent = new Intent(InitActivity.this, ActivityMap.class);
                        intent.putExtra(Register.JSON_USER, user.toJSON());
                        intent.putExtra(InitActivity.KEY_INVISIBLE,invisible);
                        intent.putExtra(InitActivity.KEY_MODE, mode_user);
                        startActivityForResult(intent,InitActivity.REQUEST_CODE_GET_MODES);
                        break;
                    case R.id.idChat:
                        changeFragment(new FragmentChat(),FragmentChat.TAG);
                        break;
                    case R.id.idSettings:
                        changeFragment(new FragmentSettings(), FragmentSettings.TAG);
                        break;
                    case R.id.idSalir:
                        LogOut();
                        break;
                    default:

                }
                drawerLayout.closeDrawers();
                return false;
            }
        });
    }

    public void ToolbarBottomEvent(Toolbar toolview) {
        toolview.inflateMenu(R.menu.toolbar_shortcut);
        toolview.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch(item.getItemId()) {
                    case R.id.idHome:
                        changeFragment(new FragmentMain(),FragmentMain.TAG);
                        return true;
                    case R.id.idSearch:
                        hidden_gesture = false;
                        gestureView.setGestureVisible(!hidden_gesture);
                        Toast.makeText(getApplicationContext(),"Gestura",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.idChat:
                        changeFragment(new FragmentChat(),FragmentChat.TAG);
                        return true;
                    default:
                            return true;
                }
            }
        });
    }

    //Change Fragment
    public void changeFragment(Fragment newFragment, String tag) {
        //Operaciones de agregar, remplazar y eliminar

        // Administra los frgamentos de un activity
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        //Buscar si ya existe el mismo frgamento "abierto"
        android.support.v4.app.Fragment fragmentoActual = fm.findFragmentByTag(tag);
        if (fragmentoActual != null && fragmentoActual.isVisible()) {
            Toast.makeText(getApplicationContext(), "Ya se esta mostrando", Toast.LENGTH_LONG).show();
            HiddenGestureView();
            return;
        }
        // realiza las "transicciones" de un fragmento
        // Agregar, remplazar y eliminar
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.fragment_base, newFragment, tag);

        ft.commit();//aplicar cambios
        TAG = tag;
        HiddenGestureView();
    }

    //LogOut
    public void LogOut() {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }


        builder.setTitle("Salir")
                .setMessage("Salir de mecanicos y Gruas")
                .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ForceLogOut();
                    }
                })
                .setNegativeButton("Quedarse", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();

    }

    private void ForceLogOut() {
        AuthUI.getInstance()
                .signOut(getApplicationContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        SharedUtil.setUserNickname(InitActivity.this,"");
                        SharedUtil.setUserPassword(InitActivity.this, "");
                        SharedUtil.setUserProvider(InitActivity.this, "");
                        finish();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentByTag(TAG);
        if(f != null)
            f.onActivityResult(requestCode,resultCode, data);

        if(requestCode == REQUEST_CODE_GET_MODES) {
            if(resultCode == RESULT_OK ) {
                mode_user = data.getIntExtra(KEY_MODE,0);
                invisible = data.getIntExtra(KEY_INVISIBLE, 0);

                if(mode_user == 1)
                    switchMode.setChecked(true);
                else
                    switchMode.setChecked(false);

                if(invisible == 1)
                    switchInvisible.setChecked(true);
                else
                    switchInvisible.setChecked(false);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void UpdateDrawerLayout() {
        if(user == null)
            return;
        if(user.getId() == -1)
            return;
        Picasso.get()
                .load(Networking.SERVER_IP + user.getImageURL())
                .placeholder(R.drawable.ic_photo_camera_black_56dp)
                .resize(200, 200)
                .into(imgPerfil);

        Picasso.get()
                .load(Networking.SERVER_IP + user.getImageBackgroundURL())
                .placeholder(R.drawable.ic_photo_camera_black_56dp)
                .resize(200, 200)
                .into(imgBackground);

        if(grua == null)
            return;
        if(grua.getId() == -1)
            return;

        Picasso.get()
                .load(Networking.SERVER_IP + grua.getImageURL())
                .placeholder(R.drawable.ic_photo_camera_black_56dp)
                .resize(200, 200)
                .into(imgBackgroundService);

    }

    private void SaveSessionSQLite() {
        if(user == null)
            return;
        if(user.getId() == -1)
            return;
        new UserSessionDataSource(InitActivity.this).setUserSession(user);
        if(grua == null)
            return;
        new GruaSessionDataSource(InitActivity.this).setGruaSession(grua);
    }

    public void EventModeUser() {
        switchInvisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int result = isChecked ? 1 : 0;
                if(result == invisible)
                    return;
                invisible = result;

                new Networking(InitActivity.this, true).execute(Networking.UPDATE_MODE_USER, user.getId(), invisible, mode_user, new NetCallback() {
                    @Override
                    public void onWorkFinish(Object... objects) {

                    }

                    @Override
                    public void onMessageThreadMain(Object data) {
                        final String message = (String)data;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InitActivity.this,message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        switchMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int result = isChecked ? 1 : 0;

                if(result == mode_user)
                    return;
                mode_user = result;

                new Networking(InitActivity.this, true).execute(Networking.UPDATE_MODE_USER, user.getId(), invisible, mode_user, new NetCallback() {
                    @Override
                    public void onWorkFinish(Object... objects) {

                    }

                    @Override
                    public void onMessageThreadMain(Object data) {
                        final String message = (String)data;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InitActivity.this,message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }
        });
    }

    public void EventGesture() {
        gestureView.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {
            @Override
            public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {

                if(hidden_gesture)
                    return;

                List<Prediction> predictions =
                        m_gestureLibrary.recognize(gesture);

                if(predictions.size() > 0) { //Reconoce una gestura

                    Prediction prediction = predictions.get(0);

                    if(prediction.score > 1.0) {
                        String name = prediction.name;

                        switch(name) {
                            case "circle":
                                changeFragment(new FragmentSettings(), FragmentSettings.TAG);
                                Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
                                break;
                            case "espiral":
                                Intent intent = new Intent(InitActivity.this, ActivityMap.class);
                                intent.putExtra(Register.JSON_USER, user.toJSON());
                                intent.putExtra(InitActivity.KEY_INVISIBLE,invisible);
                                intent.putExtra(InitActivity.KEY_MODE, mode_user);
                                HiddenGestureView();
                                startActivityForResult(intent, InitActivity.REQUEST_CODE_GET_MODES);
                                Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
                                break;
                            case "letra_v":
                                changeFragment(new FragmentChat(),FragmentChat.TAG);
                                Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
                                break;
                            case "ocho":
                                changeFragment(new FragmentMyService(),FragmentMyService.TAG);
                                Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
                                break;
                            case "snake":
                                changeFragment(new FragmentMain(), FragmentMain.TAG);
                                Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
                                break;
                            case "triangle":
                                changeFragment(new FragmentServices(),FragmentServices.TAG);
                                Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
                                break;
                                default:
                        }
/*
                        if(name.equals("circle")) {

                            //m_relativeLayout.setBackgroundColor(Color.RED);

                        } else if(name.equals("espiral")) {

                            //m_relativeLayout.setBackgroundColor(Color.GREEN);

                        } else if(name.equals("letra_v")) {

                            //m_relativeLayout.setBackgroundColor(Color.BLUE);

                        }
                        */
                    }

                }

            }
        });
    }

    public void HiddenGestureView() {
        hidden_gesture = true;
        gestureView.setGestureVisible(!hidden_gesture);
    }



}
