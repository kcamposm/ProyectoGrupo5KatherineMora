package GestionInventario.bl.entities.tienda;

import GestionInventario.bl.entities.clientes.Cliente;
import GestionInventario.bl.entities.clientes.ColaClientes;
import GestionInventario.bl.entities.productos.ArbolProductos;
import GestionInventario.bl.entities.productos.Producto;

import java.time.Duration;

public class Tienda {

    private String nombre;
    private ArbolProductos inventario;
    private ColaClientes colaClientes;

    public Tienda(String nombre) {
        this.nombre = nombre;
        this.inventario = new ArbolProductos();
        this.colaClientes = new ColaClientes();
    }

    public String getNombre() {
        return nombre;
    }

    public ArbolProductos getInventario() {
        return inventario;
    }

    public ColaClientes getColaClientes() {
        return colaClientes;
    }

    public void agregarProductoAlInventario(String nombre, double precio, int cantidadJugadores,
                                            Duration duracionJuego, int edadMinima,
                                            String categoria, String imagenProducto) {
        Producto producto = new Producto(nombre, precio, cantidadJugadores, duracionJuego,
                edadMinima, categoria, imagenProducto);
        inventario.insertar(producto);
    }

    public Producto buscarProductoEnInventario(String nombreProducto) {
        return inventario.buscar(nombreProducto);
    }

    public boolean agregarProductoAlCarrito(Cliente cliente, String nombreProducto) {
        if (cliente == null || nombreProducto == null || nombreProducto.isBlank()) {
            return false;
        }

        Producto productoInventario = inventario.buscar(nombreProducto);
        if (productoInventario == null) {
            return false;
        }

        cliente.getCarrito().insertarProductoFinal(productoInventario.copiar());
        return true;
    }

    public void encolarCliente(Cliente cliente) {
        colaClientes.encolar(cliente);
    }

    public Cliente atenderSiguienteCliente() {
        return colaClientes.atenderSiguiente();
    }

    public void mostrarInventario() {
        inventario.mostrarEnOrden();
    }

    public String generarFactura(Cliente cliente) {
        if (cliente == null) {
            return "No hay cliente para facturar.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n====================================\n");
        sb.append("          FACTURA DE COMPRA          \n");
        sb.append("====================================\n");
        sb.append("Tienda: ").append(nombre).append("\n");
        sb.append("Cliente: ").append(cliente.getNombre()).append("\n");
        sb.append("Prioridad: ").append(cliente.getTipoPrioridad())
                .append(" (").append(cliente.getPrioridad()).append(")\n");
        sb.append("------------------------------------\n");
        sb.append(cliente.getCarrito().generarDetalleFactura());
        sb.append("====================================\n");

        return sb.toString();
    }
}