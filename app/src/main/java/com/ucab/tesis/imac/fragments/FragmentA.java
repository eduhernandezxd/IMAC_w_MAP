package com.ucab.tesis.imac.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.ucab.tesis.imac.Main2Activity;
import com.ucab.tesis.imac.modelo.Items;
import com.ucab.tesis.imac.Adaptadores.ItemsAdapter;
import com.ucab.tesis.imac.R;
import com.ucab.tesis.imac.interfaces.ComunicatorIF;
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

public class FragmentA extends Fragment implements Response.Listener<JSONObject>,Response.ErrorListener {


    private OnFragmentInteractionListener mListener;

    private ArrayList<Items> l_datos;
    private ArrayList<String> lista;
    private ArrayList<String> lista_img;
    private RecyclerView recyclerView;
    private ComunicatorIF cif;
    private ProgressDialog progressDialog;

    public FragmentA() { }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_a, container, false);

        callJSONAPI();

        l_datos = new ArrayList<>();
        recyclerView = vista.findViewById(R.id.recyclerView_f);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

        return vista;
    }

    private void callJSONAPI() {
        String IP = getString(R.string.DIR_IP);
        String all_posts = "http://"+IP+"/wordpress/api/get_posts";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, all_posts, null, this, this);
        VolleySingleton.getInstancia(getContext()).addToRequestQueue(jsonObjectRequest);
    }


    private void llenar_lista() {

        for(int i=0;i<lista.size();i++) {
            l_datos.add(new Items(lista_img.get(i),R.drawable.ambiente_chacao, lista.get(i), "Direccion"));
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof Activity){
            Activity activity = (Activity) context;
            cif= (ComunicatorIF) activity;
        }


        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public void ListaParques(ArrayList<String> lista_parques) {
        lista = new ArrayList<>();
        lista = lista_parques;
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONObject response) {
        lista_img = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando");
        progressDialog.show();

        try{
            JSONArray jsonArray = response.getJSONArray("posts");


            for (int i=0;i<lista.size();i++){

                for (int d=0;d<jsonArray.length();d++){

                    JSONObject jsonObject = jsonArray.getJSONObject(d);
                    String aux = jsonObject.optString("title");
                    if(lista.get(i).equals(aux)){
                        String html_img = jsonObject.optString("content");
                        Document document = Jsoup.parse(html_img);
                        Elements element = document.getAllElements();
                        for(Element e: element){
                            Elements str = e.getElementsByTag("img");
                            String src = str.attr("src");

                            if(!src.equals("")) {
                                lista_img.add(src);
                                Log.d("     PRINT 2:", "          " + src);
                                break;
                            }
                        }
                    }
                }
            }

            Log.d("P R O B A N D O",String.valueOf(lista_img));

            llenar_lista();
            ItemsAdapter adapter1 = new ItemsAdapter(l_datos,getContext());
            recyclerView.setAdapter(adapter1);
            progressDialog.dismiss();
            adapter1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(),
                            "Ingresar informacion referente al: "+l_datos.get(recyclerView
                                    .getChildAdapterPosition(v)).getObjeto2(),Toast.LENGTH_LONG).show();

                    cif.enviar_datos(l_datos.get(recyclerView.getChildAdapterPosition(v)));

                }
            });





        }catch (JSONException e){
            e.printStackTrace();
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
