package GestionInventario.bl.entities.productos;

import java.awt.*;
import java.sql.Time;
import java.time.Duration;
import java.util.Date;

public class Producto {


        /*De cada Producto hace falta registrar su nombre, precio,
    categoria y fechaVencimiento (si aplica), además de una cantidad, la cual representará la
    cantidad de unidades del Producto */


//Atributos

    private String nombre;
    private double precio;
    private int cantidadJugadores;
    private Duration duracionJuego;
    private int edadMinima;
    private String  categoria;
    private String imagenProducto;

    private Producto siguiente; // Este es el enlace que apunta al siguiente nodo de la lista

    //Metodos
    //Constructor


    public Producto(String nombre, double precio, int cantidadJugadores, Duration duracionJuego, int edadMinima, String categoria, String imagenProducto) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidadJugadores = cantidadJugadores;
        this.duracionJuego = duracionJuego;
        this.edadMinima = edadMinima;
        this.categoria = categoria;
        this.imagenProducto = imagenProducto;
        this.siguiente = null; //Como este dato no se puede nunca saber de antemano, siguiente nace null
    }

    //-----------------------------------------------Getters Setters-----------------------------------------------


    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public int getCantidadJugadores() {
        return cantidadJugadores;
    }

    public Duration getDuracionJuego() {
        return duracionJuego;
    }

    public int getEdadMinima() {
        return edadMinima;
    }

    public String getCategoria() {
        return categoria;
    }

    public Producto getSiguiente() {
        return siguiente;

    }

    public String getImagenProducto() {
        return imagenProducto;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setCantidadJugadores(int cantidadJugadores) {
        this.cantidadJugadores = cantidadJugadores;
    }

    public void setDuracionJuego(Duration duracionJuego) {
        this.duracionJuego = duracionJuego;
    }

    public void setEdadMinima(int edadMinima) {
        this.edadMinima = edadMinima;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setImagenProducto(String imagenProducto) {
        this.imagenProducto = imagenProducto;
    }

    public void setSiguiente(Producto siguiente) {
        this.siguiente = siguiente;
    }



    @Override
    public String toString() {
        return "Producto{" +
                "nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", cantidadJugadores=" + cantidadJugadores +
                ", duracionJuego=" + duracionJuego +
                ", edadMinima=" + edadMinima +
                ", categoria='" + categoria + '\'' +
                '}';
    }
}
