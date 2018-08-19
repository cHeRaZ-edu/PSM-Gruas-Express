package com.psm.edu.psm_gruas_express;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class InitActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    Toolbar toolbarBottom;
    Toolbar toolbarActionBar;
    ActionBarDrawerToggle mdrawerToggle;
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
