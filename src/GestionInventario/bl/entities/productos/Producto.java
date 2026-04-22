package GestionInventario.bl.entities.productos;

import java.time.Duration;

public class Producto {

    private String nombre;
    private double precio;
    private int cantidadJugadores;
    private Duration duracionJuego;
    private int edadMinima;
    private String categoria;
    private String imagenProducto;
    private Producto siguiente;

    public Producto(String nombre, double precio, int cantidadJugadores, Duration duracionJuego,
                    int edadMinima, String categoria, String imagenProducto) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidadJugadores = cantidadJugadores;
        this.duracionJuego = duracionJuego;
        this.edadMinima = edadMinima;
        this.categoria = categoria;
        this.imagenProducto = imagenProducto;
        this.siguiente = null;
    }

    public Producto copiar() {
        return new Producto(nombre, precio, cantidadJugadores, duracionJuego, edadMinima, categoria, imagenProducto);
    }

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

    public String getImagenProducto() {
        return imagenProducto;
    }

    public Producto getSiguiente() {
        return siguiente;
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
        long duracion = duracionJuego != null ? duracionJuego.toMinutes() : 0;

        return "Producto{" +
                "nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", cantidadJugadores=" + cantidadJugadores +
                ", duracionJuego=" + duracion + " min" +
                ", edadMinima=" + edadMinima +
                ", categoria='" + categoria + '\'' +
                '}';
    }
}