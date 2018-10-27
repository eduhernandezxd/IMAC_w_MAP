package com.ucab.tesis.imac.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ucab.tesis.imac.interfaces.ComunicatorIF;
import com.ucab.tesis.imac.modelo.Items;
import com.ucab.tesis.imac.R;
import com.ucab.tesis.imac.modelo.VolleySingleton;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragmentB extends Fragment{

    private OnFragmentInteractionListener mListener;

    private Bundle bundle;
    private List<String> list_padre;
    private HashMap<String,List<String>> listHashMap;
    private List<String> normas;
    private List<String> historia;
    private ComunicatorIF cif;
    private String name;


    public FragmentB() { }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View vista = inflater.inflate(R.layout.fragment_b, container, false);

        TextView texto_s = vista.findViewById(R.id.textoDetalle);
        ImageView imagen_s = vista.findViewById(R.id.fotodetalle);
        bundle = getArguments();


        callJSONAPI_NORMAS();

        if(bundle != null) {
            Items datos = (Items) bundle.getSerializable("objeto");
            name = datos.getObjeto2();
            texto_s.setText(name);


            Glide.with(getContext())
                    .load(datos.getObjeto1())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.cargando)
                    )
                    .into(imagen_s);
            callJSONAPI_RESENA(datos);


            ExpandableListView expandableListView = vista.findViewById(R.id.expand_list);
            initData();
            ExpandableListAdapter expandableListAdapter = new com.ucab.tesis.imac.Adaptadores
                    .ExpandableListAdapter(getContext(), list_padre, listHashMap);
            expandableListView.setAdapter(expandableListAdapter);

            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                    if (groupPosition == 2) {
                        cif.vegetacion_fauna(name, "Vegetacion");
                        return true;
                    }
                    if (groupPosition == 3) {
                        cif.vegetacion_fauna(name, "Fauna");
                        return true;
                    }

                    return false;
                }
            });
        }


        return vista;
    }

    private void callJSONAPI_RESENA(Items datos) {
        String normas_parques = datos.getObjeto2();
        normas_parques = normas_parques.replace(" ","%20");
        String IP = getString(R.string.DIR_IP);
        String all_posts = "http://"+IP+"/wordpress/api/get_post/?slug="+normas_parques;

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, all_posts,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject jsonObject = response.optJSONObject("post");
                String content = jsonObject.optString("content");
                String[] aux = content.split("</p>",2);
                String aux2 = aux[0]+"</p>";
                String aux3 = String.valueOf(Html.fromHtml(aux2));
                historia.add(aux3);
                }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ErrorRESEÑA",error.toString());
            }
        });
        VolleySingleton.getInstancia(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void callJSONAPI_NORMAS() {
        String normas_parques ="Normativa%20Parques%20y%20Plazas";
        String IP = getString(R.string.DIR_IP);
        String all_posts = "http://"+IP+"/wordpress/api/get_post/?slug="+normas_parques;

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, all_posts,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject jsonObject = response.optJSONObject("post");
                String content = jsonObject.optString("content");
                normas.add(String.valueOf(Html.fromHtml((content))));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ErrorNORMAS",error.toString());
            }
        });
        VolleySingleton.getInstancia(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void initData() {
        list_padre = new ArrayList<>();
        listHashMap = new HashMap<>();

        list_padre.add("Normas y Reglamentos");
        list_padre.add("Reseña Histórica");
        list_padre.add("Vegetación Arborea");
        list_padre.add("Fauna Silvestre");

        normas= new ArrayList<>();
        historia = new ArrayList<>();

        List<String> vegetal= new ArrayList<>();
        vegetal.add("Ver detalle");

        List<String> activi= new ArrayList<>();
        activi.add("Ver detalle");

        listHashMap.put(list_padre.get(0),normas);
        listHashMap.put(list_padre.get(1),historia);
        listHashMap.put(list_padre.get(2),vegetal);
        listHashMap.put(list_padre.get(3),activi);
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
