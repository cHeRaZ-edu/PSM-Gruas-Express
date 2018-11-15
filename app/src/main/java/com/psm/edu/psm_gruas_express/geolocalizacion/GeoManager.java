package com.psm.edu.psm_gruas_express.geolocalizacion;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class GeoManager {
    public static LatLng getCurrentLocation(Activity activity)throws SecurityException {
        LocationManager locationManager = (LocationManager) activity.getSystemService(activity.LOCATION_SERVICE);

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
                return null;
            }
        }

        if(bestLocation == null)
            return null;

        return new LatLng(
                bestLocation.getLatitude(),
                bestLocation.getLongitude()
        );
    }
}
