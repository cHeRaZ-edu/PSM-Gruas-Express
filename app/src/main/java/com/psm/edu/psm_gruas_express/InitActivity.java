package com.psm.edu.psm_gruas_express;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.psm.edu.psm_gruas_express.fragments.FragmentChat;
import com.psm.edu.psm_gruas_express.fragments.FragmentMain;
import com.psm.edu.psm_gruas_express.fragments.FragmentMap;
import com.psm.edu.psm_gruas_express.fragments.FragmentMyService;
import com.psm.edu.psm_gruas_express.fragments.FragmentServiceSelected;
import com.psm.edu.psm_gruas_express.fragments.FragmentServices;
import com.psm.edu.psm_gruas_express.fragments.FragmentSettings;
import com.psm.edu.psm_gruas_express.fragments.FragmentStateEmergency;
import com.psm.edu.psm_gruas_express.models.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class InitActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    Toolbar toolbarActionBar;
    ActionBarDrawerToggle mdrawerToggle;
    NavigationView navigationView;
    TextView tvNickname;
    TextView tvEmail;
    CircleImageView imgPerfil;
    ImageView imgBackground;
    String TAG="";
    User user;
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

        //Navegation bar icon burger
        setSupportActionBar(toolbarActionBar);
        mdrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbarActionBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mdrawerToggle.syncState();

        NavEventMenuItemView();

        if (TAG.equals(""))
            changeFragment(new FragmentMain(), FragmentMain.TAG);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();

        if(intent != null) {
            String json_user =  intent.getStringExtra(Register.JSON_USER);
            if(json_user == null) {
                Toast.makeText(this,"No se encontro datos del usuario", Toast.LENGTH_SHORT).show();
                ForceLogOut();
            }
            user = new Gson().fromJson(json_user, User.class);
            tvNickname.setText("Usuario: " + user.getNickname());
            tvEmail.setText("Correo: " + user.getEmail());
        }
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
                        changeFragment(new FragmentMap(), FragmentMap.TAG);
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
        FragmentManager fragmentManager = getFragmentManager();

        //Operaciones de agregar, remplazar y eliminar

        // Administra los frgamentos de un activity
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        //Buscar si ya existe el mismo frgamento "abierto"
        android.support.v4.app.Fragment fragmentoActual = fm.findFragmentByTag(tag);
        if (fragmentoActual != null && fragmentoActual.isVisible()) {
            Toast.makeText(getApplicationContext(), "Ya se esta mostrando", Toast.LENGTH_LONG).show();
            return;
        }
        // realiza las "transicciones" de un fragmento
        // Agregar, remplazar y eliminar
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.fragment_base, newFragment, tag);

        ft.commit();//aplicar cambios
        TAG = tag;
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
                        finish();
                    }
                });
    }
}
