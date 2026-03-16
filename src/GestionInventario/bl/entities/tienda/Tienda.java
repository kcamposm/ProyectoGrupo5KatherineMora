package GestionInventario.bl.entities.tienda;

import GestionInventario.bl.entities.clientes.Cliente;
import GestionInventario.bl.entities.clientes.ColaClientes;
import GestionInventario.bl.entities.productos.ArbolProductos;
import GestionInventario.bl.entities.productos.Producto;
import GestionInventario.dao.ProductoDAO;
import GestionInventario.dao.VentaDAO;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

public class Tienda {

    private String nombre;
    private ArbolProductos inventario;
    private ColaClientes colaClientes;

    private final ProductoDAO productoDAO;
    private final VentaDAO ventaDAO;

    public Tienda(String nombre) {
        this.nombre = nombre;
        this.inventario = new ArbolProductos();
        this.colaClientes = new ColaClientes();
        this.productoDAO = new ProductoDAO();
        this.ventaDAO = new VentaDAO();
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

    public void cargarInventarioDesdeBD() throws SQLException {
        inventario = new ArbolProductos();

        List<Producto> productos = productoDAO.listarTodos();
        for (Producto producto : productos) {
            inventario.insertar(producto);
        }
    }

    public void agregarProductoAlInventario(String nombre,
                                            double precio,
                                            int cantidadJugadores,
                                            Duration duracionJuego,
                                            int edadMinima,
                                            String categoria,
                                            String imagenProducto) throws SQLException {

        Producto producto = new Producto(
                nombre,
                precio,
                cantidadJugadores,
                duracionJuego,
                edadMinima,
                categoria,
                imagenProducto
        );

        // 1. Guardar en BD
        productoDAO.insertar(producto);

        // 2. Mantener estructura en memoria
        inventario.insertar(producto);
    }

    public Producto buscarProductoEnInventario(String nombreProducto) {
        if (nombreProducto == null || nombreProducto.isBlank()) {
            return null;
        }
        return inventario.buscar(nombreProducto.trim());
    }

    public boolean agregarProductoAlCarrito(Cliente cliente, String nombreProducto) {
        if (cliente == null || nombreProducto == null || nombreProducto.isBlank()) {
            return false;
        }

        Producto productoInventario = inventario.buscar(nombreProducto.trim());
        if (productoInventario == null) {
            return false;
        }

        cliente.getCarrito().insertarProductoFinal(productoInventario.copiar());
        return true;
    }

    public void encolarCliente(Cliente cliente) {
        if (cliente != null) {
            colaClientes.encolar(cliente);
        }
    }

    public Cliente atenderSiguienteCliente() {
        return colaClientes.atenderSiguiente();
    }

    public Cliente atenderSiguienteClienteYRegistrarVenta() throws SQLException {
        Cliente cliente = colaClientes.atenderSiguiente();

        if (cliente != null) {
            ventaDAO.guardarVenta(cliente);
        }

        return cliente;
    }

    public String generarFactura(Cliente cliente) {
        if (cliente == null) {
            return "No hay cliente para facturar.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n====================================\n");
        sb.append("          FACTURA DE COMPRA         \n");
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