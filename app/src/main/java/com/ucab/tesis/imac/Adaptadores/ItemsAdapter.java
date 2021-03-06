package com.ucab.tesis.imac.Adaptadores;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ucab.tesis.imac.modelo.Items;
import com.ucab.tesis.imac.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolderDatos>
        implements View.OnClickListener{

    ArrayList<Items> l_datos;
    Context context;
    private View.OnClickListener listener;


    public ItemsAdapter(ArrayList<Items> l_datos,Context context) {
        this.l_datos = l_datos;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_lista,null,false);

        view.setOnClickListener(this);

        return new ViewHolderDatos(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
           holder.dato1.setText(l_datos.get(position).getObjeto2());

         Glide.with(context)
                 .load(l_datos.get(position).getObjeto1())
                 .apply(new RequestOptions()
                        .placeholder(R.drawable.cargando))
                 .into(holder.dato2);

    }

    @Override
    public int getItemCount() {
        return l_datos.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }

    @Override
    public void onClick(View v) {
        if (listener!=null){
            listener.onClick(v);
        }

    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {

        TextView dato1;
        CircleImageView dato2;

        public ViewHolderDatos(View itemView) {

            super(itemView);

            dato1=itemView.findViewById(R.id.lista1);
            dato2=itemView.findViewById(R.id.imageItem);



        }

    }
}
