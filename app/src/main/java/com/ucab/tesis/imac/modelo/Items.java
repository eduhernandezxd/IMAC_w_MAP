package com.ucab.tesis.imac.modelo;

import java.io.Serializable;

public class Items implements Serializable{

    //Lista Principal
    private int objeto1;
    private String objeto2;

    //Lista Secundaria
    private int objeto3;
    private String objeto4;

    public Items(int objeto1, int objeto3,String objeto2, String objeto4){
        this.objeto1 = objeto1;
        this.objeto2 = objeto2;
        this.objeto3 = objeto3;
        this.objeto4 = objeto4;


    }

    public int getObjeto3() {
        return objeto3;
    }

    public void setObjeto3(int objeto3) {
        this.objeto3 = objeto3;
    }

    public String getObjeto4() {
        return objeto4;
    }

    public void setObjeto4(String objeto4) {
        this.objeto4 = objeto4;
    }

    public int getObjeto1() {
        return objeto1;
    }

    public void setObjeto1(int objeto1) {
        this.objeto1 = objeto1;
    }

    public String getObjeto2() {
        return objeto2;
    }

    public void setObjeto2(String objeto2) {
        this.objeto2 = objeto2;
    }




}
