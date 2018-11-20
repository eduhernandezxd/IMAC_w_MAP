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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ucab.tesis.imac.R;
import com.ucab.tesis.imac.modelo.VolleySingleton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;


public class FragmentFOTO extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Button boton_opciones;
    private EditText descripcion,correo,nombre_apellido;
    private ImageView foto;
    private Bitmap bitmap;
    private String path;
    private final int COD_LOAD=10;
    private final int COD_SHOOT=20;
    private final int COD_PERMISO=100;

    public FragmentFOTO() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_foto, container, false);

        Button boton_enviar = vista.findViewById(R.id.boton_enviar_foto);
        boton_opciones = vista.findViewById(R.id.boton_opciones_foto);
        descripcion = vista.findViewById(R.id.foto_descripcion);
        correo = vista.findViewById(R.id.foto_correo);
        nombre_apellido = vista.findViewById(R.id.foto_nombre);
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
                callWebService();
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
        String CARPETA_RAIZ = "imagenesIMAC/";
        String RUTA_IMAGEN = CARPETA_RAIZ + "misImagenes";
        File file_imagen = new File(Environment.getExternalStorageDirectory(), RUTA_IMAGEN);

        boolean isCreate = file_imagen.exists();
        String nombre="";

        if (!isCreate){
            isCreate = file_imagen.mkdirs();
        }else{
            nombre = (System.currentTimeMillis() / 1000) + ".jpg";
        }

        path = Environment.getExternalStorageDirectory()+
                File.separator+ RUTA_IMAGEN +File.separator+nombre;

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

                    try {
                        bitmap=MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),path_load);
                        foto.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case COD_SHOOT:
                    MediaScannerConnection.scanFile(getActivity(), new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("Ruta de Almacenamiento","Path: "+path);
                                }
                            });

                    bitmap = BitmapFactory.decodeFile(path);
                    foto.setImageBitmap(bitmap);

                    break;
            }

        }

    }

    private void callWebService(){

        String DIR_IP = getString(R.string.DIR_IP);
        String url = "http://"+DIR_IP+"/backendFOTO/webservices.php?";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equalsIgnoreCase("Successfull")){
                    descripcion.setText("");
                    nombre_apellido.setText("");
                    correo.setText("");
                    Toast.makeText(getContext(),"Se ha enviado exitosamente",
                            Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(),"Ha ocurrido un error, por favor intenta de nuevo",
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error al Enviar Imagen",error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String description=descripcion.getText().toString();
                String name=nombre_apellido.getText().toString();
                String email=correo.getText().toString();

                String image = convertImgToString(bitmap);

                Map<String,String> parametros = new HashMap<>();
                parametros.put("DESCRIPTION",description);
                parametros.put("NAME",name);
                parametros.put("EMAIL",email);
                parametros.put("IMAGE",image);

                return parametros;
            }
        };

        VolleySingleton.getInstancia(getContext()).addToRequestQueue(stringRequest);
    }

    private String convertImgToString(Bitmap bitmap) {

        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,arrayOutputStream);
        byte[] imageByte = arrayOutputStream.toByteArray();
        String imageString = Base64.encodeToString(imageByte,Base64.DEFAULT);

        return imageString;
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
