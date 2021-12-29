package com.example.geofenceapitesting;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.geofenceapitesting.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.SphericalUtil;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnMapLongClickListener {
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private GeofencingClient geofencingClient;
    private GeoFenceHelper geoFenceHelper;
    private int Fine_Location_Access_Request_Code = 10001;
    private int Fine_BACKGRPOUNDLOCATION_Access_Request_Code = 10002;
    private float GEOFENCE_RADIUS = 10; // take this input from user afterwards

    private EditText IDInput ;
    private LatLng oldlatlng;
    String GEOFENCE_ID ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        IDInput = findViewById(R.id.gfidinput);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        geoFenceHelper = new GeoFenceHelper(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        oldlatlng = new LatLng(48.8589, 2.29365);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(oldlatlng, 16));
        enableUserLocation();
        mMap.setOnMapLongClickListener(this);

    }
    @SuppressLint("MissingPermission")
    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Fine_Location_Access_Request_Code);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Fine_Location_Access_Request_Code);

            }

        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Fine_Location_Access_Request_Code) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //we have the permission
                mMap.setMyLocationEnabled(true);
            } else {
                //we dont have the permission
            }
        }
        if (requestCode == Fine_BACKGRPOUNDLOCATION_Access_Request_Code) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //we have the permission
                Toast.makeText(MapsActivity.this, "BG LOCATION PERMISSION GIVEN..NOW YOU CAN ADD GEOFENCES", Toast.LENGTH_SHORT).show();

            } else {
                //we dont have the permission
                Toast.makeText(MapsActivity.this, "BG LOCATION PERMISSION IS NECCESSARRY TO TRIGGER THE GEOFENCES", Toast.LENGTH_SHORT).show();

            }
        }
    }


    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

         GEOFENCE_ID = IDInput.getText().toString();
        if (Build.VERSION.SDK_INT >= 29) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                if(!GEOFENCE_ID.equals("")){
                    Toast.makeText(MapsActivity.this, "Distance = " + SphericalUtil.computeDistanceBetween(latLng,  oldlatlng) + "m"
                            , Toast.LENGTH_SHORT).show();

                    handleMapLongClick( latLng,GEOFENCE_ID);
                    IDInput.setText("");


                   }
                else {
                    Toast.makeText(MapsActivity.this, "Pls Enter the Id or the name of the location", Toast.LENGTH_SHORT).show();
                    IDInput.requestFocus();
                    return;

                }
                oldlatlng = latLng;
            }else{

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Fine_BACKGRPOUNDLOCATION_Access_Request_Code);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Fine_BACKGRPOUNDLOCATION_Access_Request_Code);

                }




            }

        } else {

            if(!GEOFENCE_ID.equals("")){
                Toast.makeText(MapsActivity.this, "Distance = " + SphericalUtil.computeDistanceBetween(latLng,  oldlatlng) + "m"
                        , Toast.LENGTH_SHORT).show();

                handleMapLongClick( latLng,GEOFENCE_ID);
                IDInput.setText("");


            }
            else {
                Toast.makeText(MapsActivity.this, "Pls Enter the Id or the name of the location", Toast.LENGTH_SHORT).show();
                IDInput.requestFocus();
                return;

            }
             oldlatlng = latLng;

        }

    }
    private void handleMapLongClick(LatLng latLng, String GEOFENCE_ID)
    {

        addmarker(latLng);
        addCircle(latLng, GEOFENCE_RADIUS);
        addGeofence(latLng, GEOFENCE_RADIUS,GEOFENCE_ID);


    }


    @SuppressLint("MissingPermission")
    private void addGeofence(LatLng latLng, float radius, String GEOFENCE_ID) {

        Geofence geofence = geoFenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);// this geofence will trigger when ever the person enters or dwells or exits from the geofence
        GeofencingRequest geofencingRequest = geoFenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geoFenceHelper.getPendingIntent();


        geofencingClient.addGeofences(geofencingRequest, pendingIntent).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(@NonNull Void unused) {
                Log.d(TAG, "on success: Geodence Added....");
                ;

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geoFenceHelper.getErrorString(e);
                        Log.d(TAG, "on failure" + errorMessage);


                    }
                });



    }

    private void addmarker(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);

    }
    private void addCircle(LatLng latLng, float radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255,255,0,0));
        circleOptions.fillColor(Color.argb(64,255,0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }


}