package com.ucab.tesis.imac;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = "MapaActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 6215;
    private static final int ERROR_DIALOG_REQUEST = 0101;
    Context context = this;
    //Var
    private Boolean mLocationPermissionsGranted = false;
    private Boolean EnableGPS = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GeoApiContext mGeoApiContext = null;
    private LatLng mLatLng;
    private MarkerOptions mClusterMarkers;
    private Polyline polyline;
    private int constante = 0;
    private AlertDialog alertDialog;
    private ArrayList<Marker> mTripMarkers = new ArrayList<>();
    //widgets
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        spinner = findViewById(R.id.spinner);

        getLocationPermission();

    }

    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    private void getLocationPermission() {
        Log.d(TAG, "GetLocationPermission: Success");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        //Buscar en Internet ACCESS Couse Location, Fine Location
        if ((ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (EnableGPS)) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }

    }

    private boolean isMapsEnable(){
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            buildAlertMessageNoGPS();
            return false;
        }
        EnableGPS = true;
        return true;
    }

    private void buildAlertMessageNoGPS() {


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Actualmente el GPS esta desactivado, para encontrar su parque o plaza de destino debe activar la ubicación del dispositivo")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent enableGPSIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGPSIntent,PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        Intent backIntent = new Intent(getApplicationContext(),Main2Activity.class);
                        EnableGPS = true;
                        startActivity(backIntent);
                        finish();
                    }
                });

        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS:
                if(mLocationPermissionsGranted){
                    if(resultCode == RESULT_OK){
                        Log.d("PRUEBA_DEFINITIVA","INICIA EL MAPA DE VERDAD");

                    }

                }else {
                getLocationPermission();
                }
        }

    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            return isMapsEnable();
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(checkMapServices()){
            if(mLocationPermissionsGranted){
                Log.d("PRUEBA","EL GPS ESTA ACTIVO");
                initMap();
            }
        }else{
            getLocationPermission();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();



    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapaActivity.this);

        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapaActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void init() {

        List<String> list = new ArrayList<>();
        list.add("Seleccionar Parque o Plaza");
        list.add("Parque Boyaca Chacao");
        list.add("Plaza Bolivar Chacao");
        list.add("Parque Justicia y Paz");
        list.add("Plaza La Castellana");
        list.add("Plaza Los Palos Grandes");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.
                simple_dropdown_item_1line, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter); // se une la lista con el spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                geolocate(String.valueOf(spinner.getSelectedItem()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void resetMap(){
        if(mMap != null) {
            mMap.clear();

            if(mClusterMarkers!= null){
                mClusterMarkers.visible(false);
            }

            if(constante != 0) {
                polyline.remove();
            }
        }
    }

    private void geolocate(String posicion) {
        Log.d(TAG, "geoLocate: geolocating");

        if (!posicion.equals("Seleccionar Parque o Plaza")) {
            resetMap();
            Geocoder geocoder = new Geocoder(MapaActivity.this);
            List<Address> list = new ArrayList<>();
            try {
                list = geocoder.getFromLocationName(posicion, 1);
            } catch (IOException e) {
                Log.e(TAG, "geoLocate: IOException" + e.getMessage());
            }
            if (list.size() > 0) {
                Address address = list.get(0);
                Log.d(TAG, "geoLocate: found a location" + address.toString());
                moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), 15f,
                        address.getAddressLine(0));
            }
        }
    }





    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: ubicacion  del dispositivo");
        mFusedLocationProviderClient = LocationServices.
                getFusedLocationProviderClient(this);
        try {
            Thread.sleep(2000);
            if (mLocationPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Ubicacion encontrada");
                            Location currentLocation = (Location) task.getResult();

                            mLatLng = new LatLng (currentLocation.getLatitude(),currentLocation.getLongitude());
                            moveCamera(new LatLng(currentLocation.getLatitude(),
                                            currentLocation.getLongitude()), 15f,
                                    "Mi ubicacion");

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapaActivity.this,
                                    "unable to get cyrrent location", Toast.LENGTH_SHORT).
                                    show();

                        }

                    }
                });

            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera : mueve la camara a las coordenadas dadas:" +
                latLng.latitude + "," + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (!title.equals("Mi ubicacion")) {
            mClusterMarkers = new MarkerOptions().position(latLng).title(title).snippet("¿Deseas llegar a esta posición?");
            mClusterMarkers.visible(true);
            mMap.addMarker(mClusterMarkers);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    if(EnableGPS){
                        initMap();
                    }
                }
            }
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapaActivity.this);
        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder().
                    apiKey(getString(R.string.google_maps_API_key)).build();

        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                return;
            }
            mMap.setMyLocationEnabled(true);
            // no me muestra el boton de localizacion
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            //mMap.getUiSettings().setZoomControlsEnabled(true);
            init();
            mMap.setOnInfoWindowClickListener(this);
        }
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {


        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(marker.getSnippet())
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {

                        Log.d(TAG, "calculateDirections: destination: " + mLatLng.latitude + ","+ mLatLng.longitude);
                        calculateDirections(marker,mLatLng);
                        dialog.dismiss();

                        //removeTripMarkers();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void calculateDirections(Marker marker , LatLng origen){
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
        directions.alternatives(false);
        directions.origin(
                new com.google.maps.model.LatLng(
                        origen.latitude,
                        origen.longitude
                )
        );

        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }


    private void addPolylinesToMap(final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                for (DirectionsRoute route : result.routes) {
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    //This loops through all the LatLng coordinates of ONE polyline.
                    for (com.google.maps.model.LatLng latLng : decodedPath) {

                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    constante++;
                    polyline.setColor(R.color.Blue);
                    //polyline.setColor(ContextCompat.getColor(getResources(), R.color.ruta));
                    polyline.setClickable(true);
                    zoomRoute(polyline.getPoints());

                }
            }
        });


    }

}

