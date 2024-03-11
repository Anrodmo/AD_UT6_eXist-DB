
package com.ad.ad_ut6_exist.db;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author anrod
 */
public class Libro {
    private String id, autor, titulo, genero, precio, fecha, descripcion;

    public Libro() {}

    public Libro(String id, String autor, String titulo, String genero, 
            double precio, LocalDate fecha, String descripcion) {
        this.id = id;
        this.autor = autor;
        this.titulo = titulo;
        this.genero = genero;
        this.precio = String.valueOf(precio) ;
        this.fecha = convertirLocalDateAString(fecha);
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = String.valueOf(precio) ;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = convertirLocalDateAString(fecha);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    
    
    
    
    
    public static String convertirLocalDateAString(LocalDate fecha) {       
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");        
        return fecha.format(formatter);
    }
    
  
    
    
    
}
