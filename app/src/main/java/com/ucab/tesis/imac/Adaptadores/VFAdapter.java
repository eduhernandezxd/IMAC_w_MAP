package com.ucab.tesis.imac.Adaptadores;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ucab.tesis.imac.R;
import com.ucab.tesis.imac.modelo.Parques;
import java.util.ArrayList;

public class VFAdapter extends RecyclerView.Adapter<VFAdapter.ViewHolderVF>{

    private ArrayList<Parques> lista_datos;
    private Context context;

    public VFAdapter(ArrayList<Parques> lista_datos,Context context) {
        this.lista_datos = lista_datos;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderVF onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_lista_vf,null,false);

        return new ViewHolderVF(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderVF holder, int position) {

            holder.dato1.setText(lista_datos.get(position).getTitulo());
            holder.dato2.setText(lista_datos.get(position).getDescripcion());


            Glide.with(context)
                .load(lista_datos.get(position).getImg())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.cargando))
                .into(holder.dato3);
    }

    @Override
    public int getItemCount() { return lista_datos.size(); }

    public class ViewHolderVF extends RecyclerView.ViewHolder {

        TextView dato1;
        TextView dato2;
        ImageView dato3;

        public ViewHolderVF(View itemView) {
            super(itemView);
            dato1 = itemView.findViewById(R.id.title_VF);
            dato2 = itemView.findViewById(R.id.descripcion_VF);
            dato3 = itemView.findViewById(R.id.img_VF);
        }
    }
}
