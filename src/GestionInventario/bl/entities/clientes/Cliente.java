package GestionInventario.bl.entities.clientes;

import GestionInventario.bl.entities.productos.ListaProductos;

public class Cliente {

    private String nombre;
    private int prioridad;
    private ListaProductos carrito;

    public Cliente(String nombre, int prioridad) {
        this.nombre = nombre;
        this.prioridad = prioridad;
        this.carrito = new ListaProductos();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public ListaProductos getCarrito() {
        return carrito;
    }

    public String getTipoPrioridad() {
        return switch (prioridad) {
            case 1 -> "Básico";
            case 2 -> "Afiliado";
            case 3 -> "Premium";
            default -> "Desconocida";
        };
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "nombre='" + nombre + '\'' +
                ", prioridad=" + prioridad +
                " (" + getTipoPrioridad() + ")" +
                '}';
    }
}