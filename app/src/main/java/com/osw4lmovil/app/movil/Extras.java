package com.osw4lmovil.app.movil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.EditText;
import android.widget.Toast;
import android.R.*;

public class Extras extends Activity {

    private EditText mLongitud, mLatitud;
    Context context;

    public Extras(Context context){
        this.context = context;
    }

    public void setText(double longitud, double latitud, String estado) {

        mLongitud = (EditText)((Activity)context).findViewById(R.id.lng);
        mLatitud = (EditText)((Activity)context).findViewById(R.id.lat);

        switch (estado){
            case "editar":
                mLatitud.setText((latitud + ""));
                mLongitud.setText((longitud + ""));
                break;
            case "limpiar":
                mLatitud.setText((""));
                mLongitud.setText((""));
                break;
        }

    }

    public void Alert(String titulo, String mensaje, Context self) {
        AlertDialog.Builder alert = new AlertDialog.Builder(self);
        alert.setTitle(titulo);
        alert.setMessage(mensaje);
        alert.setNegativeButton("ok, Entendido", null);
        alert.show();
    }

    public void msg(String text, Context self){
        Toast.makeText(self, text, Toast.LENGTH_SHORT).show();
    }

    public void Restart(Activity activity){
        Intent intent = new Intent();
        intent.setClass(activity, activity.getClass());
        activity.startActivity(intent);
        activity.finish();
    }

    public boolean Internet(Context self) {
        boolean conectado = false;
        ConnectivityManager estados =
                (ConnectivityManager) self.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] redes = estados.getAllNetworkInfo();
        for (int i = 0; i < 2; i++)
            if (redes[i].getState() == NetworkInfo.State.CONNECTED)
                conectado = true;
        return conectado;
    }

}