package com.ucab.tesis.imac.Adaptadores;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ucab.tesis.imac.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> lista_padre;
    private HashMap<String, List<String>> listHashMap;

    public ExpandableListAdapter(Context context,List<String> lista_padre, HashMap<String, List<String>> listHashMap) {
        this.context = context;
        this.lista_padre = lista_padre;
        this.listHashMap = listHashMap;
    }

    @Override
    public int getGroupCount() {
        return lista_padre.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listHashMap.get(lista_padre.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return lista_padre.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listHashMap.get(lista_padre.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String padre = (String)this.getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_padre,null);
        }

        TextView textViewPadre = (TextView)convertView.findViewById(R.id.textView_padre);
        textViewPadre.setTypeface(null, Typeface.BOLD);
        textViewPadre.setText(padre);

        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
         String hijo = (String)this.getChild(groupPosition,childPosition);

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_hijo, null);

        }
            TextView textViewHijo = (TextView)convertView.findViewById(R.id.textView_hijo);
            textViewHijo.setText(hijo);
            return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
