package de.mksoft.demotrainingsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private LocationManager locationManager;
    private boolean locationPermissionGranted;
    private String provider;

    private GoogleMap map=null;
    private CameraPosition cameraPosition;


    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        builder=new AlertDialog.Builder(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        checkLocationProvider();
        Criteria criteria=new Criteria();
        provider=locationManager.getBestProvider(criteria, false);
        Location location=locationManager.getLastKnownLocation(provider);
        if(location!=null){
            onLocationChanged(location);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    protected void onPause(){
        super.onPause();
        locationManager.removeUpdates(this);
    }

    private void checkLocationProvider(){
        boolean enabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!enabled){
            builder.setMessage("Bitte aktivieren Sie die Standortbestimmung auf Ihrem Smartphone")
                    .setTitle("GPS nicht aktiviert!!!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            AlertDialog alert=builder.create();
            alert.show();
        }
    }
    public void onMapReady(GoogleMap googleMap) {
        this.map=googleMap;
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(52.903943, 10.184134))
                .title("Mannschaftsheim"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(52.605934428, 10.00612833), 15
        ));
    }



    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(this.map!=null) {
            this.map.moveCamera(CameraUpdateFactory.newLatLng(
                    new LatLng(location.getLatitude(), location.getLongitude())
            ));
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}