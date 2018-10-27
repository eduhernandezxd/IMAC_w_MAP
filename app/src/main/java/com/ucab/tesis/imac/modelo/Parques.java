package com.ucab.tesis.imac.modelo;

public class Parques {

    private String titulo;
    private String descripcion;
    private String img;

    public Parques(String titulo, String descripcion, String img) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.img = img;
    }

    public Parques() { }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
