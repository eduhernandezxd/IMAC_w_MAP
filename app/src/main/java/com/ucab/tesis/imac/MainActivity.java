package com.ucab.tesis.imac;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.ucab.tesis.imac.modelo.Parques;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Response.Listener<JSONObject>,Response.ErrorListener {

    private ImageView logo;
    private Button auxboton;
    private JsonObjectRequest jsonObjectRequest;
    private RequestQueue requestQueue;
    private Parques parques;
    private static final String DIR_IP = "b03fa107.ngrok.io";
    private String url;
    private String urlaux;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logo=findViewById(R.id.logo);
        auxboton=findViewById(R.id.button);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        llamar_JSONAPI();

    }

    @Override
    protected void onResume() {
        super.onResume();




    }

    private void llamar_JSONAPI() {

        String titulo_parque = "Parque%20Boyaca";
        String fauna_parque = "Fauna%20"+titulo_parque;

        url = "http://"+DIR_IP+"/wordpress/api/get_post/?slug="+fauna_parque;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        requestQueue.add(jsonObjectRequest);
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

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("E R R O R",error.toString());

        if (isConnected()) {
            Toast.makeText(getApplicationContext(),"No se pudo conectar con el servidor",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(),"Se necesita una conexión " +
                    "a internet para disfrutar de la aplicación, por favor activela " +
                    "antes de continuar",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        final Parques parques =new Parques();
        ArrayList<String> lista_img = new ArrayList<>();

        try {
            JSONObject jsonObject = response.getJSONObject("post");
            parques.setTitulo(jsonObject.optString("title"));
            parques.setReseña(jsonObject.optString("content"));


            String html_img = parques.getReseña();
            Log.d("     PRINT1:" ,"          "+html_img);
            Document document = Jsoup.parse(html_img);
            Elements element = document.getAllElements();
            urlaux =null;
            for(Element e: element){

                Elements str = e.getElementsByTag("img");


                    String src = str.attr("src");

                    if(!src.equals(urlaux) && !src.equals("")) {
                        lista_img.add(src);
                        Log.d("     PRINT 2:", "          " + src);
                        urlaux = src;
                    }

            }


            Log.d(" P R I N T LISTAAAA ","         "+lista_img);

            auxboton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("KEY_TITLE", parques.getTitulo());
                    bundle.putString("KEY_RESEÑA", parques.getReseña());
                    intent.putExtra("bundle",bundle);
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
}
