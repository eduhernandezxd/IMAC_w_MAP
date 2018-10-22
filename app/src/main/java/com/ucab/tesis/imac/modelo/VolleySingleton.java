package com.ucab.tesis.imac.modelo;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {

    private static VolleySingleton instancia;
    private RequestQueue requestQueue;
    private static Context contexto;

    private VolleySingleton(Context context) {
        contexto = context;
        requestQueue = getRequestQueue();
    }

    private RequestQueue getRequestQueue() {

        if(requestQueue==null){
            requestQueue = Volley.newRequestQueue(contexto.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }

    public static synchronized VolleySingleton getInstancia(Context context){
        if(instancia==null){
            instancia = new VolleySingleton(context);
        }

        return instancia;
    }


}
