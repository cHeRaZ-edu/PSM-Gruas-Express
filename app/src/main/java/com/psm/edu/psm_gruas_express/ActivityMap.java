package com.psm.edu.psm_gruas_express;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.psm.edu.psm_gruas_express.models.MarkerUser;
import com.psm.edu.psm_gruas_express.models.NetCallback;
import com.psm.edu.psm_gruas_express.models.User;
import com.psm.edu.psm_gruas_express.networking.Networking;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityMap extends AppCompatActivity implements OnMapReadyCallback {
    DrawerLayout drawerLayout;
    Toolbar toolbarActionBar;
    ActionBarDrawerToggle mdrawerToggle;
    NavigationView navigationView;
    TextView tvNickname;
    TextView tvEmail;
    CircleImageView imgPerfil;
    ImageView imgBackground;
    User user;
    Switch switchInvisible;
    Switch switchMode;
    int invisible = 0;
    int mode_user = 0;
    ActivityMap instance;
    List<MarkerUser> markers = new ArrayList<>();
    // Geocoding:
    // Transforma coordenadas geograficas en direcciones (calle, colonia, avenidas, pais, etc)
    // Reverse Geocoding
    // Transofma nombre de direcciones (calles, colonias, avenidas, pais, etc) en coordenadas geograficas
    Geocoder geocoder;

    // Objeto con el cual podremos hacer uso de nuestro mapa de Google
    GoogleMap map;

    // LocationManager: Nos permite obtener la ubicacion del usuario a traves de diferentes metodos.
    LocationManager locationManager;

    // Fragmento del mapa
    MapFragment mapFragment;

    // Extra: Lo utilizamos para agregar marcadores al mapa
    List<LatLng> markerPositions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //Find id view
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutMap);
        toolbarActionBar = (Toolbar) findViewById(R.id.toolbar_map);
        navigationView = (NavigationView) findViewById(R.id.nav_view_map);
        View header = navigationView.getHeaderView(0);
        tvNickname = (TextView) header.findViewById(R.id.tvNicknameNav);
        tvEmail = (TextView) header.findViewById(R.id.tvEmailNav);
        imgPerfil = (CircleImageView) header.findViewById(R.id.imgPerfilNav);
        imgBackground = (ImageView) header.findViewById(R.id.imgBackgroundNav);
        switchInvisible = (Switch) header.findViewById(R.id.switchInvisible);
        switchMode = (Switch) header.findViewById(R.id.switchEmergency);
        // Fragmento que contiene al mapa de google en el Layout
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_gruas_google);
        instance = this;
        //Navegation bar icon burger
        setSupportActionBar(toolbarActionBar);
        mdrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbarActionBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mdrawerToggle.syncState();
        NavEventMenuItemView();
        EventModeUser();

        // Como estamos implementando OnMapReadyCallback (Ver arriba) esto quiere decir que se debe de encontrar
        // el metodo "onMapReady" y una ves que se termine de cargar el mapa se llamara a este metodo para poder
        // comenzar a utilizar el objeto mapa
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        if(intent != null) {
            String json_user =  intent.getStringExtra(Register.JSON_USER);
            mode_user = intent.getIntExtra(InitActivity.KEY_MODE, 0);
            invisible = intent.getIntExtra(InitActivity.KEY_INVISIBLE, 0);
            if(json_user == null) {
                Toast.makeText(this,"No se encontro datos del usuario", Toast.LENGTH_SHORT).show();
                finish();
            }
            user = new Gson().fromJson(json_user, User.class);
            tvNickname.setText("Usuario: " + user.getNickname());
            tvEmail.setText("Correo: " + user.getEmail());
            if(mode_user == 1)
                switchMode.setChecked(true);
            if(invisible == 1)
                switchInvisible.setChecked(true);
        }
    }

    // Se llama cuando el mapa este listo para trabajar con el
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Instaciammos nuestor objeto mapa
        map = googleMap;

        // Inicializa nuestro objeto LocationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Listener para detectar los eventos "Click" dentro del mapa
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            // Este evento nos devuelve la cooordenada geografica donde se dio click dentro del mapa
            @Override
            public void onMapClick(LatLng latLng) {

                // Funcion extra que desarrollamos para agregar marcadores al mapa
                //addMarker("Hola", latLng, false, false);
            }
        });

        // Si estamos en Android 6.0+ tenemos que pedir permisos en tiempo de ejecucion
        // Si estamos debajo de Android 6.0 solo hace falta pedir permisos desde el AndroidManifest
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkPermissions();
        else
            moveMapCameraToUserLocation();
    }

    // Se llama cuando un permiso es aceptado o rechazado Android 6.0+
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Se requiere aceptar el permiso", Toast.LENGTH_SHORT).show();
                checkPermissions();
            } else {
                Toast.makeText(this, "Permisio concedido", Toast.LENGTH_SHORT).show();
                moveMapCameraToUserLocation();
            }
        }
    }

    private void checkPermissions() {
        // Apartir de Android 6.0+ necesitamos pedir el permiso de ubicacion
        // directamente en tiempo de ejecucion de la app
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si no tenemos permiso para la ubicacion
            // Solicitamos permiso
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else {
            // Ya se han concedido los permisos anteriormente
            moveMapCameraToUserLocation();
        }
    }

    private void moveMapCameraToUserLocation() {
        // Continuamos obteniendo la ubicacion del usuario para despues mostrar esa ubicacion en el mapa por default
        // pero cuando no se encuentre la ubicacion entonces pondremos una ubicacion fija.
        LatLng currentLocation = getCurrentLocation();
        if(currentLocation == null)
            return;
        try {
            // Muestra el boton de "Mi Ubicacion" en el mapa (El tipico circulo azul de google)
            map.setMyLocationEnabled(true);

        } catch (SecurityException e) {
            e.printStackTrace();
        }

        // Si se pudo obtener la ubicacion del usuario
        if (currentLocation != null) {

            map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            currentLocation,
                            16.f
                    )
            );

            // Movemos la camara para que apunte a otra coordenada diferente e la default
            // ..
        } else { // Si no se pudo obtener la ubicacion

            // Ponemos una ubicacion fija
            LatLng mtyLocation = new LatLng(25.65, -100.29);

            map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            currentLocation,
                            16.f
                    )
            );

        }
    }

    private LatLng getCurrentLocation() throws SecurityException {

        //Lista de provedores para acceder a la geolocalizacion
        //Wifi
        //gps
        //datos
        //gps
        //red de tecel
        List<String> providers = locationManager.getProviders(true);//Obtener los provedores activos

        Location bestLocation = null;

        for(String provider : providers) {
            Location loc = locationManager.getLastKnownLocation(provider);

            if(loc == null) {
                continue;
            }

            if(bestLocation == null || loc.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = loc;
            }

            if(bestLocation == null) {
                showToast("No se pudo obtener la ubicacion. Espere un momento");
                return null;
            }

        }

        if(bestLocation == null)
            return null;

        //LatLng guarda latitud y longitud
        return new LatLng(
                bestLocation.getLatitude(),
                bestLocation.getLongitude()
        );
    }

    private void addMarker(String title, LatLng position, boolean clean, boolean polys) {
        if (clean) {
            map.clear();
        }

        // De esta manera se pueden agregar marcadores al mapa
        MarkerOptions opts = new MarkerOptions();
        opts.position(position);
        opts.title(title);

        // La clase GoogleMap tiene el metodo addMarker
        map.addMarker(opts);

        if (!polys)
            return;

        if (markerPositions == null)
            markerPositions = new ArrayList<>();

        // EXTRA: Tambien se pueden poner lineas dentro del mapa
        PolylineOptions line = new PolylineOptions();
        line.width(8);
        line.color(Color.BLUE);

        if (markerPositions.size() > 0) {
            LatLng latLng = markerPositions.get(markerPositions.size() - 1);
            line.add(latLng);
        }
        line.add(position);
        markerPositions.add(position);

        // Muestra una linea en el mapa
        map.addPolyline(line);
    }

    private void showToast(String msg) {
        Toast.makeText(ActivityMap.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void NavEventMenuItemView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()) {
                    case R.id.idMenuGrua:
                        //... cambiar
                        new Networking(instance).execute(Networking.GET_MARKERS, new NetCallback() {
                            @Override
                            public void onWorkFinish(Object... objects) {
                                final List<MarkerUser> list_marker = (List<MarkerUser>) objects[0];
                                instance.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        markers  = list_marker;
                                        map.clear();
                                        for(MarkerUser m : markers) {
                                            if(m.getId() != user.getId() && m.getMode() == 0) {
                                                addMarker(m.getNickname(),m.getLatitudeLongitude(),false,false);
                                            }
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onMessageThreadMain(Object data) {
                                final String message = (String)data;
                                instance.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(instance,message,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        break;
                    case R.id.idStateEmergency:
                        //... cambiar
                        new Networking(instance).execute(Networking.GET_MARKERS, new NetCallback() {
                            @Override
                            public void onWorkFinish(Object... objects) {
                                final List<MarkerUser> list_marker = (List<MarkerUser>) objects[0];
                                instance.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        markers  = list_marker;
                                        map.clear();
                                        for(MarkerUser m : markers) {
                                            if(m.getId() != user.getId() && m.getMode() == 1) {
                                                addMarker(m.getNickname(),m.getLatitudeLongitude(),false,false);
                                            }
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onMessageThreadMain(Object data) {
                                final String message = (String)data;
                                instance.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(instance,message,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        break;
                    default:

                }
                drawerLayout.closeDrawers();
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawers();
        else {
            Intent intent = getIntent();
            intent.putExtra(InitActivity.KEY_MODE,mode_user);
            intent.putExtra(InitActivity.KEY_INVISIBLE, invisible);
            setResult(Activity.RESULT_OK,intent);
            super.onBackPressed();
        }

    }

    public void EventModeUser() {
        switchInvisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int result = isChecked ? 1 : 0;
                if(result == invisible)
                    return;
                invisible = result;

                new Networking(ActivityMap.this, true).execute(Networking.UPDATE_MODE_USER, user.getId(), invisible, mode_user, new NetCallback() {
                    @Override
                    public void onWorkFinish(Object... objects) {

                    }

                    @Override
                    public void onMessageThreadMain(Object data) {
                        final String message = (String)data;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ActivityMap.this,message, Toast.LENGTH_SHORT).show();
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

                new Networking(ActivityMap.this, true).execute(Networking.UPDATE_MODE_USER, user.getId(), invisible, mode_user, new NetCallback() {
                    @Override
                    public void onWorkFinish(Object... objects) {

                    }

                    @Override
                    public void onMessageThreadMain(Object data) {
                        final String message = (String)data;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ActivityMap.this,message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }
        });
    }
}
