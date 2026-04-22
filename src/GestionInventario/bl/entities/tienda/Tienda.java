package GestionInventario.bl.entities.tienda;

import GestionInventario.bl.entities.clientes.Cliente;
import GestionInventario.bl.entities.clientes.ColaClientes;
import GestionInventario.bl.entities.grafo.GrafoUbicaciones;
import GestionInventario.bl.entities.grafo.ResultadoRuta;
import GestionInventario.bl.entities.grafo.Ubicacion;
import GestionInventario.bl.entities.productos.ArbolProductos;
import GestionInventario.bl.entities.productos.Producto;
import GestionInventario.dao.ProductoDAO;
import GestionInventario.dao.VentaDAO;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

public class Tienda {

    private String nombre;
    private Ubicacion ubicacion;
    private ArbolProductos inventario;
    private ColaClientes colaClientes;
    private GrafoUbicaciones mapaEntregas;

    private final ProductoDAO productoDAO;
    private final VentaDAO ventaDAO;

    public Tienda(String nombre, String nombreUbicacion) {
        this.nombre = nombre;
        this.ubicacion = new Ubicacion(nombreUbicacion);
        this.inventario = new ArbolProductos();
        this.colaClientes = new ColaClientes();
        this.mapaEntregas = new GrafoUbicaciones();
        this.productoDAO = new ProductoDAO();
        this.ventaDAO = new VentaDAO();

        mapaEntregas.cargarMapaBase();
        mapaEntregas.agregarUbicacion(this.ubicacion.getNombre());
    }

    public String getNombre() {
        return nombre;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public String getNombreUbicacionTienda() {
        return ubicacion != null ? ubicacion.getNombre() : "";
    }

    public ArbolProductos getInventario() {
        return inventario;
    }

    public ColaClientes getColaClientes() {
        return colaClientes;
    }

    public GrafoUbicaciones getMapaEntregas() {
        return mapaEntregas;
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

        productoDAO.insertar(producto);
        inventario.insertar(producto);
    }

    public Producto buscarProductoEnInventario(String nombreProducto) {
        if (nombreProducto == null || nombreProducto.trim().isEmpty()) {
            return null;
        }
        return inventario.buscar(nombreProducto.trim());
    }

    public boolean agregarProductoAlCarrito(Cliente cliente, String nombreProducto) {
        if (cliente == null || nombreProducto == null || nombreProducto.trim().isEmpty()) {
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
        if (cliente == null) {
            return;
        }

        agregarUbicacionAlMapa(cliente.getNombreUbicacion());
        colaClientes.encolar(cliente);
    }

    public boolean agregarUbicacionAlMapa(String nombreUbicacion) {
        return mapaEntregas.agregarUbicacion(nombreUbicacion);
    }

    public boolean agregarConexionAlMapa(String origen, String destino, double distancia) {
        return mapaEntregas.agregarConexion(origen, destino, distancia);
    }

    public String obtenerRepresentacionMapa() {
        return mapaEntregas.obtenerRepresentacionMapa();
    }

    public ResultadoRuta obtenerRutaACliente(Cliente cliente) {
        if (cliente == null) {
            return new ResultadoRuta();
        }

        return mapaEntregas.obtenerCaminoMasCorto(
                getNombreUbicacionTienda(),
                cliente.getNombreUbicacion()
        );
    }

    public ResultadoAtencion atenderSiguienteClienteYRegistrarVenta() throws SQLException {
        if (colaClientes.estaVacia()) {
            return new ResultadoAtencion(
                    false,
                    "No hay clientes en cola.",
                    null,
                    null
            );
        }

        Cliente siguiente = colaClientes.verSiguiente();
        ResultadoRuta ruta = obtenerRutaACliente(siguiente);

        if (!ruta.isExisteCamino()) {
            return new ResultadoAtencion(
                    false,
                    "No se puede atender al siguiente cliente porque su ubicación está desconectada del mapa.",
                    siguiente,
                    ruta
            );
        }

        Cliente clienteAtendido = colaClientes.atenderSiguiente();
        ventaDAO.guardarVenta(clienteAtendido);

        return new ResultadoAtencion(
                true,
                "Cliente atendido correctamente. Venta registrada en base de datos.",
                clienteAtendido,
                ruta
        );
    }

    public String generarFactura(Cliente cliente, ResultadoRuta ruta) {
        if (cliente == null) {
            return "No hay cliente para facturar.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n====================================\n");
        sb.append("          FACTURA DE COMPRA         \n");
        sb.append("====================================\n");
        sb.append("Tienda: ").append(nombre).append("\n");
        sb.append("Ubicación de la tienda: ").append(getNombreUbicacionTienda()).append("\n");
        sb.append("Cliente: ").append(cliente.getNombre()).append("\n");
        sb.append("Ubicación del cliente: ").append(cliente.getNombreUbicacion()).append("\n");
        sb.append("Prioridad: ").append(cliente.getTipoPrioridad())
                .append(" (").append(cliente.getPrioridad()).append(")\n");
        sb.append("------------------------------------\n");
        sb.append(cliente.getCarrito().generarDetalleFactura());

        if (ruta != null && ruta.isExisteCamino()) {
            sb.append("------------------------------------\n");
            sb.append("Ruta de entrega óptima:\n");
            sb.append(ruta.caminoComoTexto()).append("\n");
            sb.append("Distancia total: ").append(String.format("%.2f", ruta.getDistanciaTotal())).append(" km\n");
        } else {
            sb.append("------------------------------------\n");
            sb.append("No fue posible calcular una ruta de entrega.\n");
        }

        sb.append("====================================\n");
        return sb.toString();
    }
}