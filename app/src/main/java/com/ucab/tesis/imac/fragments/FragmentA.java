package com.ucab.tesis.imac.fragments;

import android.app.Activity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentA extends Fragment {


    private OnFragmentInteractionListener mListener;

    private ArrayList<Items> l_datos;
    private RecyclerView recyclerView;
    private Activity activity;
    private ComunicatorIF cif;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;

    private String data1;
    private String url_img;




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

        l_datos = new ArrayList<>();
        recyclerView = vista.findViewById(R.id.recyclerView_f);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        llenar_lista();
        ItemsAdapter adapter1 = new ItemsAdapter(l_datos);
        recyclerView.setAdapter(adapter1);

        adapter1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),
                        "Ingresar informacion referente al: "+l_datos.get(recyclerView
                                .getChildAdapterPosition(v)).getObjeto2(),Toast.LENGTH_LONG).show();

                cif.enviar_datos(l_datos.get(recyclerView.getChildAdapterPosition(v)));

            }
        });


        return vista;
    }


    private void llenar_lista() {
            //Metodo para llenar la lista de los Parques correspndientes

        l_datos.add(new Items(R.drawable.plaza_bolivar,R.drawable.ambiente_chacao,"Parque 1", "Direccion Parque 1 "));
        l_datos.add(new Items(R.drawable.plaza_bolivar,R.drawable.ambiente_chacao,"Parque 2", "Direccion Parque 2 "));
        l_datos.add(new Items(R.drawable.plaza_bolivar,R.drawable.ambiente_chacao,"Parque 3", "Direccion Parque 3 "));
        l_datos.add(new Items(R.drawable.plaza_bolivar,R.drawable.ambiente_chacao,"Parque 4", "Direccion Parque 4 "));
        l_datos.add(new Items(R.drawable.plaza_bolivar,R.drawable.ambiente_chacao,"Parque 5", "Direccion Parque 5 "));


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
            this.activity = (Activity) context;
            cif= (ComunicatorIF) this.activity;
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


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
