package GestionInventario.bl.entities.clientes;

import GestionInventario.bl.entities.grafo.Ubicacion;
import GestionInventario.bl.entities.productos.ListaProductos;

public class Cliente {

    private String nombre;
    private int prioridad;
    private Ubicacion ubicacion;
    private ListaProductos carrito;

    public Cliente(String nombre, int prioridad, String nombreUbicacion) {
        this.nombre = nombre;
        this.prioridad = prioridad;
        this.ubicacion = new Ubicacion(nombreUbicacion);
        this.carrito = new ListaProductos();
    }

    public String getNombre() {
        return nombre;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public String getNombreUbicacion() {
        return ubicacion != null ? ubicacion.getNombre() : "";
    }

    public ListaProductos getCarrito() {
        return carrito;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getTipoPrioridad() {
        switch (prioridad) {
            case 1:
                return "Básico";
            case 2:
                return "Afiliado";
            case 3:
                return "Premium";
            default:
                return "Desconocida";
        }
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "nombre='" + nombre + '\'' +
                ", prioridad=" + prioridad +
                " (" + getTipoPrioridad() + ")" +
                ", ubicacion='" + getNombreUbicacion() + '\'' +
                '}';
    }
}