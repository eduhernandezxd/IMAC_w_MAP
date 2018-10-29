package com.ucab.tesis.imac.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ucab.tesis.imac.Adaptadores.VFAdapter;
import com.ucab.tesis.imac.R;
import com.ucab.tesis.imac.modelo.Parques;
import com.ucab.tesis.imac.modelo.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class FragmentVF extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private ArrayList<Parques> lista_parques;
    private ArrayList<String> lista_img_v;
    private ArrayList<String> lista_img_f;

    public FragmentVF(){ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_vf, container, false);
        TextView texto = vista.findViewById(R.id.texto_vf);

        Bundle bundle = getArguments();

        if(bundle !=null){
            ArrayList<String> tipo = bundle.getStringArrayList("VF");
            String index = tipo.get(1);
            switch (index){
                case "Vegetacion":
                    String nombre1 = tipo.get(0);
                    texto.setText("Vegetación "+nombre1);
                    callJSONAPI_Vegetacion(nombre1);
                    break;
                case "Fauna":
                    String nombre2 = tipo.get(0);
                    texto.setText("Fauna "+nombre2);
                    callJSONAPI_Fauna(nombre2);
                    break;
            }
        }
        lista_parques = new ArrayList<>();
        recyclerView = vista.findViewById(R.id.recyclerView_VF);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return vista;
    }

    private void callJSONAPI_Vegetacion(String nombre) {
        String nombre_parques = nombre.replace(" ","%20");
        String IP = getString(R.string.DIR_IP);
        String all_posts = "http://"+IP+"/wordpress/api/get_post/?slug=Vegetacion%20"+nombre_parques;

        final JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,all_posts,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               lista_img_v = new ArrayList<>();
               ArrayList<String> lista_textos = new ArrayList<>();

                JSONObject jsonObject = response.optJSONObject("post");
                String content = jsonObject.optString("content");
                Document document = Jsoup.parse(content);
                Elements elements = document.getAllElements();
                String aux=null;
                for (Element e:elements){
                    Elements str=e.getElementsByTag("img");
                    String link = str.attr("src");
                    if(!link.equals(aux)&&!link.equals("")) {
                        lista_img_v.add(link);
                        aux = link;
                    }
                }
                String aux2=null;
                for(Element i:elements){
                    Elements str=i.getElementsByTag("p");
                    String link2 = str.text();
                    if(!link2.equals(aux2)&&!link2.equals("")){
                        lista_textos.add(link2);
                        aux2 = link2;
                    }

                }
                lista_textos.remove(0);
                llenar_lista(lista_img_v,getTitle_VF(lista_textos),getDescription_VF(lista_textos));
                VFAdapter vfAdapter = new VFAdapter(lista_parques,getContext());
                recyclerView.setAdapter(vfAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ErrorVEGETACION",error.toString());
            }
        });
        VolleySingleton.getInstancia(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void callJSONAPI_Fauna(String nombre) {
        String nombre_parques = nombre.replace(" ","%20");
        String IP = getString(R.string.DIR_IP);
        String all_posts = "http://"+IP+"/wordpress/api/get_post/?slug=Fauna%20"+nombre_parques;

        final JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,all_posts,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                lista_img_f = new ArrayList<>();
                ArrayList<String> lista_textos = new ArrayList<>();

                JSONObject jsonObject = response.optJSONObject("post");
                String content = jsonObject.optString("content");
                Document document = Jsoup.parse(content);
                Elements elements = document.getAllElements();
                String aux=null;
                for (Element e:elements){
                    Elements str=e.getElementsByTag("img");
                    String link = str.attr("src");
                    if(!link.equals(aux)&&!link.equals("")) {
                        lista_img_f.add(link);
                        aux = link;
                    }
                }
                String aux2=null;
                for(Element i:elements){
                    Elements str=i.getElementsByTag("p");
                    String link2 = str.text();
                    if(!link2.equals(aux2)&&!link2.equals("")){
                        lista_textos.add(link2);
                        aux2 = link2;
                    }

                }
                lista_textos.remove(0);
                llenar_lista(lista_img_f,getTitle_VF(lista_textos),getDescription_VF(lista_textos));
                VFAdapter vfAdapter = new VFAdapter(lista_parques,getContext());
                recyclerView.setAdapter(vfAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ErrorFAUNA",error.toString());
            }
        });
        VolleySingleton.getInstancia(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void llenar_lista(ArrayList<String> lista_img,
                              ArrayList<String> lista_title,ArrayList<String> lista_description){

        for(int index=0;index<lista_img.size();index++) {
            lista_parques.add(new Parques(lista_title.get(index), lista_description.get(index),
                    lista_img.get(index)));
        }
    }

    private ArrayList<String> getTitle_VF(ArrayList<String> lista_text){
        ArrayList<String> lista_title = new ArrayList<>();
        for(int index=0;index<lista_text.size();index++){
            if(index%2==0){
                lista_title.add(lista_text.get(index));
            }
        }
        return lista_title;
    }

    private ArrayList<String> getDescription_VF(ArrayList<String> lista_text){
        ArrayList<String> lista_description = new ArrayList<>();
        for(int index=0;index<lista_text.size();index++){
            if(index%2!=0){
                lista_description.add(lista_text.get(index));
            }
        }
        return lista_description;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
