package com.osw4lmovil.app.movil;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.MarkerOptions;
import android.R.*;

public class MapsActivity extends AppCompatActivity implements OnMyLocationButtonClickListener,
        OnMapReadyCallback {

    private GoogleMap mMap;
    private Extras call = new Extras(this);
    private Button mButton, mBtnClear, mNormal, mHibrido, mSatelite, mReload;
    protected Activity self;
    private final String titulo = "Error de localizacion";
    private final String mensage = "el GPS se encuentra apagado, por favor enciendalo!";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // context app
        self = this;

        if (!call.Internet(this)){
            setContentView(R.layout.fallo_internet);
            call.Alert(titulo, mensage, this);
            call.msg("No Hay Conexion", this);
            mReload = (Button)findViewById(R.id.reload);
            mReload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    call.Restart(self);
                }
            });
        }
        else
        {
            setContentView(R.layout.activity_principal);
            SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            mButton = (Button) findViewById(R.id.ubicacion);
            mBtnClear = (Button) findViewById(R.id.reiniciar);

            mNormal = (Button) findViewById(R.id.normal);
            mHibrido = (Button) findViewById(R.id.hibrido);
            mSatelite = (Button) findViewById(R.id.satelite);


            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        enableMyLocation();
                        if (mMap.isMyLocationEnabled()) {
                            mMap.getMyLocation();
                            call.setText(mMap.getMyLocation().getLatitude(),
                                    mMap.getMyLocation().getLongitude(), "editar");
                            marcar_posicion(mMap.getMyLocation().getLatitude(),
                                    mMap.getMyLocation().getLongitude());
                        }
                    } catch (Exception e){
                        call.Alert(titulo, mensage , self);
                    }
                }
            });

            mBtnClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMap.clear();
                    call.Alert("Atencion (Instrucciones)", "El marcador Azul en el mapa es para cualquier punto" +
                            "\n y el marcador verde es para la ubicacion", self);
                    mMap.setMapType(mMap.MAP_TYPE_NORMAL);
                    call.setText(0, 0, "limpiar");
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(0, 0), 0));

                    call.msg("Se ha reiniciado el mapa", self);
                }
            });

            mNormal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    call.msg("se ha cambiado el mapa a normal", self);
                }
            });

            mHibrido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    call.msg("se ha cambiado el mapa a hibrido", self);
                }
            });

            mSatelite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    call.msg("se ha cambiado el mapa a satelite", self);
                }
            });

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Marcar(latLng.longitude, latLng.latitude);
            }
        });
    }

    public void marcar_posicion(double latitud, double longitud){
        mMap.clear();

        call.setText(latitud, longitud, "editar");

        mMap.addMarker(new MarkerOptions().position(new LatLng(latitud, longitud))
                .title("Presicion : "+mMap.getMyLocation().getAccuracy()).snippet(":D [" + latitud + "] , [" + longitud + "]")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(latitud, longitud), 11));
        call.msg("posicion actual \n" + mMap.getMyLocation().getLatitude() + " : " + mMap.getMyLocation().getLongitude(), self);
    }

    public void Marcar(double longitud, double latitud){
        mMap.clear();

        call.setText(latitud, longitud, "editar");

        mMap.addMarker(new MarkerOptions().position(new LatLng(latitud, longitud))
                .title("Presicion : "+mMap.getMyLocation().getAccuracy()).snippet(" [" + latitud + "] , [" + longitud + "]")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(latitud, longitud), mMap.getCameraPosition().zoom));

        call.msg("cambio: " + latitud + " : " + longitud, this);
    }


    // metodos de la interface

    @Override
    public boolean onMyLocationButtonClick() {
        try {
            enableMyLocation();
            if (mMap.isMyLocationEnabled())
                marcar_posicion(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
        } catch (Exception e){
            Log.e("error :", e.toString());
            call.Alert(titulo, mensage ,this);
        }
        return false;
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

}