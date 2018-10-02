package com.ucab.tesis.imac.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ucab.tesis.imac.R;

import java.io.File;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;


public class FragmentFOTO extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button boton_enviar,boton_opciones;
    private ImageView foto;

    private final String CARPETA_RAIZ="imagenesIMAC/";
    private final String RUTA_IMAGEN=CARPETA_RAIZ+"misImagenes";
    private String path;
    private final int COD_LOAD=10;
    private final int COD_SHOOT=20;
    private final int COD_PERMISO=100;
    private Uri imagenuri;



    public FragmentFOTO() { }


    public static FragmentFOTO newInstance(String param1, String param2) {
        FragmentFOTO fragment = new FragmentFOTO();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_foto, container, false);
        boton_enviar = vista.findViewById(R.id.boton_enviar_foto);
        boton_opciones = vista.findViewById(R.id.boton_opciones_foto);
        foto = vista.findViewById(R.id.foto_tomada);



        boton_opciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CargarImagen();

            }
        });

        boton_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Enviar Foto y Descripción",Toast.LENGTH_LONG).show();
            }
        });

        if (validarPermisosAndroid6()){
            boton_opciones.setEnabled(true);
        }else {
            boton_opciones.setEnabled(false);
            Toast.makeText(getContext(),"Se requieren permisos para poder habilitar Opciones de Imagen",Toast.LENGTH_LONG).show();
        }

        return vista;
    }

    private boolean validarPermisosAndroid6() {

        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if ((checkSelfPermission(getContext(),CAMERA)== PackageManager.PERMISSION_GRANTED)&&
                (checkSelfPermission(getContext(),WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if ((shouldShowRequestPermissionRationale(CAMERA))||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},COD_PERMISO);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == COD_PERMISO){
            if (grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED&&
                    grantResults[1]==PackageManager.PERMISSION_GRANTED){
                boton_opciones.setEnabled(true);
            }else {
                solicitarPermisosManual();
            }
        }

    }

    private void solicitarPermisosManual() {

        final CharSequence[] opciones ={"SI","NO"};
        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(getContext());
        alertOpciones.setTitle("¿Desea configurar los permisos de formal manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (opciones[which].equals("SI")){

                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package",getActivity().getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);

                }else{
                    dialog.dismiss();
                    Toast.makeText(getContext(),"Los permisos no fueron aceptados",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Se requieren los siguientes permisos para el correcto funcionamiento");
        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        dialogo.show();
    }

    private void CargarImagen() {

        final CharSequence[] opciones ={"Tomar Foto","Cargar Foto","Cancelar"};
        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(getContext());
        alertOpciones.setTitle("Seleccione una Opción");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (opciones[which].equals("Tomar Foto")){

                   tomarFoto();

                }else if (opciones[which].equals("Cargar Foto")){

                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/");
                    startActivityForResult(intent.createChooser(intent,
                            "Seleccione la Aplicación"),COD_LOAD);

                }else if(opciones[which].equals("Cancelar")){
                    dialog.dismiss();
                }
            }
        });

        alertOpciones.show();

    }

    private void tomarFoto() {
        File file_imagen = new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);

        boolean isCreate = file_imagen.exists();
        String nombre="";

        if (!isCreate){
            isCreate = file_imagen.mkdirs();
        }else{
            nombre = (System.currentTimeMillis() / 1000) + ".jpg";
        }

        path = Environment.getExternalStorageDirectory()+
                File.separator+RUTA_IMAGEN+File.separator+nombre;

        File imagen = new File(path);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(imagen));
        startActivityForResult(intent,COD_SHOOT);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            switch (requestCode){

                case COD_LOAD:
                    Uri path_load = data.getData();
                    foto.setImageURI(path_load);
                    break;
                case COD_SHOOT:
                    MediaScannerConnection.scanFile(getActivity(), new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("Ruta de Almacenamiento","Path: "+path);
                                }
                            });

                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    foto.setImageBitmap(bitmap);

                    break;
            }

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
