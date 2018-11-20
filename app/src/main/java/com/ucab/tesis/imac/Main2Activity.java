package com.ucab.tesis.imac;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ucab.tesis.imac.fragments.FragmentA;
import com.ucab.tesis.imac.fragments.FragmentB;
import com.ucab.tesis.imac.fragments.FragmentFOTO;
import com.ucab.tesis.imac.fragments.FragmentVF;
import com.ucab.tesis.imac.interfaces.ComunicatorIF;
import com.ucab.tesis.imac.modelo.Items;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FragmentA.OnFragmentInteractionListener,
        FragmentB.OnFragmentInteractionListener,
        FragmentFOTO.OnFragmentInteractionListener,
        FragmentVF.OnFragmentInteractionListener,
        ComunicatorIF{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        FragmentA fragmentA = new FragmentA();

        ArrayList<String> lista_parques = getIntent().getExtras().getStringArrayList("NOMBRE_PARQUES");
        Log.d("LISTA P A R Q U E S",String.valueOf(lista_parques));

        fragmentA.ListaParques(lista_parques);

        
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contendorFragments, fragmentA)
                .commit();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapaActivity.class);
                startActivity(intent);

            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        FragmentFOTO fragmentFOTO = null;

        if (id == R.id.nav_1) {
            fragmentFOTO = new FragmentFOTO();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contendorFragments, fragmentFOTO)
                    .addToBackStack(null).commit();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void enviar_datos(Items items_data) {
        FragmentB fragmentB = new FragmentB();
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("objeto", items_data);
        fragmentB.setArguments(bundle1);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contendorFragments, fragmentB)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void vegetacion_fauna(String data,String tipo){
        FragmentVF fragmentVF = new FragmentVF();
        Bundle bundle2 = new Bundle();
        List<String> listaVF = new ArrayList<>();

        if(tipo.equals("Vegetacion")) {
           listaVF.add(data);
           listaVF.add("Vegetacion");
           bundle2.putStringArrayList("VF", (ArrayList<String>) listaVF);
        }

        if(tipo.equals("Fauna")){
            listaVF.add(data);
            listaVF.add("Fauna");
            bundle2.putStringArrayList("VF", (ArrayList<String>) listaVF);
        }

        fragmentVF.setArguments(bundle2);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contendorFragments,fragmentVF)
                .addToBackStack(null)
                .commit();
    }

}
