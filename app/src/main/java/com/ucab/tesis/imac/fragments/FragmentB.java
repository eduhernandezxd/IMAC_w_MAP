package com.ucab.tesis.imac.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ucab.tesis.imac.modelo.Items;
import com.ucab.tesis.imac.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragmentB extends Fragment {

    private OnFragmentInteractionListener mListener;

    TextView texto_s;
    ImageView imagen_s;
    Bundle bundle;

    private List<String> list_padre;
    private HashMap<String,List<String>> listHashMap;

    public FragmentB() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_b, container, false);

        texto_s = vista.findViewById(R.id.textoDetalle);
        imagen_s = vista.findViewById(R.id.fotodetalle);


        bundle = getArguments();
        Items datos = null;

        if(bundle != null){

            datos = (Items) bundle.getSerializable("objeto");
            texto_s.setText(datos.getObjeto4());
        }

        ExpandableListView expandableListView = vista.findViewById(R.id.expand_list);
        initData();
        ExpandableListAdapter expandableListAdapter = new com.ucab.tesis.imac.Adaptadores.ExpandableListAdapter(getContext(),list_padre,listHashMap);
        expandableListView.setAdapter(expandableListAdapter);

        return vista;
    }

    private void initData() {
        list_padre = new ArrayList<>();
        listHashMap = new HashMap<>();

        list_padre.add("Normas y Reglamentos");
        list_padre.add("Reseña Histórica");
        list_padre.add("Vegetación Arborea");
        list_padre.add("Actividades Recreacionales");

        List<String> normas= new ArrayList<>();
        normas.add("Opcion 2");


        List<String> historia = new ArrayList<>();
        historia.add("Opcion 1");


        List<String> vegetal= new ArrayList<>();
        vegetal.add("Opcion 1");
        vegetal.add("Opcion 2");
        vegetal.add("Opcion 3");
        vegetal.add("Opcion 4");

        List<String> activi= new ArrayList<>();
        activi.add("Opcion 1");
        activi.add("Opcion 2");
        activi.add("Opcion 3");
        activi.add("Opcion 4");

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
