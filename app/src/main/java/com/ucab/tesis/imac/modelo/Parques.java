package com.ucab.tesis.imac.modelo;

public class Parques {

    private String titulo;
    private String reseña;

    public Parques(String titulo, String reseña) {
        this.titulo = titulo;
        this.reseña = reseña;
    }

    public Parques() {

    }

    public String getReseña() {
        return reseña;
    }

    public void setReseña(String reseña) {
        this.reseña = reseña;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
