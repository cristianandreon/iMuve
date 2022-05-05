package com.imuve.cristian.imuve;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Cristian on 20/07/2015.
 */
public class GPSManager  implements LocationListener {

    static LocationManager locationManager;
    static Context context;

    public GPSManager(Context context) {
        this.context = context;
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10.0f, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location loc) {
        // editLocation.setText("");
        // pb.setVisibility(View.INVISIBLE);
        Toast.makeText(GPSManager.context, "Location changed: Lat: " + loc.getLatitude() + " Lng: " + loc.getLongitude(), Toast.LENGTH_SHORT).show();
        // String longitude = "Longitude: " + loc.getLongitude();
        // String latitude = "Latitude: " + loc.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}



