package com.psm.edu.psm_gruas_express;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.psm.edu.psm_gruas_express.fragments.FragmentChat;
import com.psm.edu.psm_gruas_express.fragments.FragmentMain;

public class InitActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    Toolbar toolbarBottom;
    Toolbar toolbarActionBar;
    ActionBarDrawerToggle mdrawerToggle;
    String TAG="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        drawerLayout = (DrawerLayout) findViewById(R.id.idDrawerLayout);
        toolbarActionBar = (Toolbar) findViewById(R.id.toolbar_top);
        toolbarBottom = (Toolbar) findViewById(R.id.toolbar_bottom);
        setSupportActionBar(toolbarActionBar);
        mdrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbarActionBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mdrawerToggle.syncState();
        if(mdrawerToggle.isDrawerSlideAnimationEnabled()){
            Toast.makeText(this, "Funciono",Toast.LENGTH_LONG).show();
        }
        toolbarBottom.inflateMenu(R.menu.toolbar_shortcut);

        mdrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });



        if (TAG.equals(""))
            changeFragment(new FragmentMain(), FragmentMain.TAG);
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            switch (TAG) {
                case FragmentMain.TAG:
                    LogOut();
                    return;
                default:
                    changeFragment(new FragmentMain(),FragmentMain.TAG);

            }
            }
    }

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
                       finish();
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
}
