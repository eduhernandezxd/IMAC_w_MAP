package com.ucab.tesis.imac;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import android.net.NetworkInfo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.ucab.tesis.imac.modelo.Parques;
import com.ucab.tesis.imac.modelo.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Response.Listener<JSONObject>,Response.ErrorListener {

    private Button auxboton;
    private Parques parques;
    private String url;
    private String urlaux;
    private ArrayList<String> lista_posts;
    private ArrayList<String> lista_parques;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auxboton=findViewById(R.id.button);

        llamar_JSONAPI();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null){
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info!=null) {
                return info.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }

    private void llamar_JSONAPI() {
        String IP = getString(R.string.DIR_IP);
        String all_posts = "http://"+IP+"/wordpress/api/get_posts";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, all_posts, null, this, this);
        VolleySingleton.getInstancia(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("E R R O R",error.toString());

        if (isConnected()) {
            Toast.makeText(getApplicationContext(),"No se pudo conectar con el servidor",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
            startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(),"Se necesita una conexión " +
                    "a internet para disfrutar de la aplicación, por favor activela " +
                    "antes de continuar",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResponse(JSONObject response) {

        try {
            JSONArray jsonArray = response.getJSONArray("posts");
            checkPostsParques(jsonArray);
            checkParques();

            auxboton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("NOMBRE_PARQUES",lista_parques);
                    intent.putExtras(bundle);

                    if (isConnected()) {
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(),"Se necesita una conexión " +
                                "a internet para disfrutar de la aplicación, por favor activela " +
                                "antes de continuar",Toast.LENGTH_LONG).show();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkPostsParques(JSONArray jsonArray)  {
        lista_posts = new ArrayList<>();
        JSONObject jsonObject;

        try{
                for(int i=0;i<jsonArray.length();i++){
                    jsonObject= jsonArray.getJSONObject(i);
                    String aux = jsonObject.optString("title");
                    lista_posts.add(aux);
                }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void checkParques(){
        lista_parques = new ArrayList<>();


        for (int i=0;i<lista_posts.size();i++){

            String nombre = lista_posts.get(i);
            String letra1="P";
            String letra2="N";

            if ((!String.valueOf(nombre.charAt(0)).equals(letra1))
                    && (!String.valueOf(nombre.charAt(0)).equals(letra2))) {
                        String[] aux = nombre.split(" ", 2);
                        String titulo = aux[1];
                        lista_parques.add(titulo);
                    } else {
               //Primer Filtro de Parques
            }
        }
        Log.d("TITULOS DE LOS PARQUES",String.valueOf(lista_parques));

        for(int count=0;count<lista_parques.size();count++){
            for(int d=0; d<lista_parques.size();d++) {
                if ((lista_parques.get(d).equals(lista_parques.get(count)))) {
                        lista_parques.remove(d);
                        break;
                }

            }
        }
        Log.d("TITULOS DE LOS PARQUES2",String.valueOf(lista_parques));
    }

}


