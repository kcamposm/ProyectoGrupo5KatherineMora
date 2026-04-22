import GestionInventario.bl.entities.clientes.Cliente;
import GestionInventario.bl.entities.clientes.ColaClientes;
import GestionInventario.bl.entities.productos.Producto;
import GestionInventario.bl.entities.tienda.ResultadoAtencion;
import GestionInventario.bl.entities.tienda.Tienda;
import GestionInventario.ui.HomePanel;

import javax.swing.*;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static Tienda tienda;

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            error.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Excepción no capturada: " + error.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        });

        tienda = new Tienda("Tienda de Juegos CENFOTEC", "Alajuela Centro");
        inicializarSistema();
        menu();
    }

    private static void inicializarSistema() {
        try {
            tienda.cargarInventarioDesdeBD();

            if (tienda.getInventario().estaVacio()) {
                tienda.agregarProductoAlInventario("Catan", 45.50, 4, Duration.ofMinutes(90), 10, "Estrategia", "");
                tienda.agregarProductoAlInventario("Dixit", 30.00, 6, Duration.ofMinutes(40), 8, "Fiesta", "");
                tienda.agregarProductoAlInventario("Carcassonne", 35.75, 5, Duration.ofMinutes(60), 8, "Estrategia", "");
                tienda.agregarProductoAlInventario("Pandemic", 42.00, 4, Duration.ofMinutes(50), 10, "Cooperativo", "");
                tienda.cargarInventarioDesdeBD();
            }
        } catch (SQLException e) {
            System.out.println("No fue posible cargar la base de datos.");
            System.out.println("Detalle: " + e.getMessage());
        }
    }

    public static void menu() {
        int opcion;

        do {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    agregarProductoInventario();
                    break;
                case 2:
                    mostrarInventario();
                    break;
                case 3:
                    registrarClienteYLlenarCarrito();
                    break;
                case 4:
                    atenderSiguienteCliente();
                    break;
                case 5:
                    buscarProducto();
                    break;
                case 6:
                    agregarUbicacionMapa();
                    break;
                case 7:
                    agregarConexionMapa();
                    break;
                case 8:
                    mostrarMapa();
                    break;
                case 9:
                    mostrarColaClientes();
                    break;
                case 10:
                    abrirModoGrafico();
                    break;
                case 11:
                    recargarInventarioDesdeBD();
                    break;
                case 12:
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción inválida. Intente de nuevo.");
                    break;
            }

        } while (opcion != 12);
    }

    private static void mostrarMenu() {
        System.out.println("\n====================================================");
        System.out.println(" SISTEMA DE GESTIÓN DE INVENTARIO Y ENTREGAS ");
        System.out.println("====================================================");
        System.out.println("1. Agregar producto al inventario");
        System.out.println("2. Mostrar inventario");
        System.out.println("3. Registrar cliente y llenar carrito");
        System.out.println("4. Atender siguiente cliente");
        System.out.println("5. Buscar producto por nombre");
        System.out.println("6. Agregar ubicación al mapa");
        System.out.println("7. Agregar conexión entre ubicaciones");
        System.out.println("8. Mostrar mapa de entregas");
        System.out.println("9. Mostrar cola de clientes");
        System.out.println("10. Abrir interfaz gráfica Swing");
        System.out.println("11. Recargar inventario desde base de datos");
        System.out.println("12. Salir");
    }

    private static void agregarProductoInventario() {
        System.out.println("\n=== Agregar producto al inventario ===");

        String nombre = leerTexto("Nombre: ");
        double precio = leerDouble("Precio: ");
        int cantidadJugadores = leerEntero("Cantidad de jugadores: ");
        int duracionMinutos = leerEntero("Duración en minutos: ");
        int edadMinima = leerEntero("Edad mínima: ");
        String categoria = leerTexto("Categoría: ");

        try {
            tienda.agregarProductoAlInventario(
                    nombre,
                    precio,
                    cantidadJugadores,
                    Duration.ofMinutes(duracionMinutos),
                    edadMinima,
                    categoria,
                    ""
            );
            System.out.println("Producto agregado correctamente.");
        } catch (SQLException e) {
            System.out.println("No se pudo guardar el producto.");
            System.out.println("Detalle: " + e.getMessage());
        }
    }

    private static void mostrarInventario() {
        System.out.println("\n=== Inventario ===");
        tienda.getInventario().mostrarEnOrden();
    }

    private static void registrarClienteYLlenarCarrito() {
        System.out.println("\n=== Registrar cliente ===");

        String nombreCliente = leerTexto("Nombre del cliente: ");
        int prioridad = leerPrioridad();
        String ubicacion = leerTexto("Ubicación del cliente: ");

        Cliente cliente = new Cliente(nombreCliente, prioridad, ubicacion);

        if (tienda.getInventario().estaVacio()) {
            System.out.println("No hay productos en el inventario.");
        } else {
            String continuar;
            do {
                System.out.println("\nInventario disponible:");
                tienda.getInventario().mostrarEnOrden();

                String nombreProducto = leerTexto("Digite el nombre exacto del producto a agregar al carrito: ");
                boolean agregado = tienda.agregarProductoAlCarrito(cliente, nombreProducto);

                if (agregado) {
                    System.out.println("Producto agregado al carrito.");
                } else {
                    System.out.println("No se encontró el producto en el inventario.");
                }

                continuar = leerTexto("¿Desea agregar otro producto? (s/n): ");
            } while (continuar.equalsIgnoreCase("s"));
        }

        tienda.encolarCliente(cliente);
        System.out.println("Cliente agregado a la cola.");
        System.out.println("La ubicación del cliente fue agregada automáticamente al grafo si no existía.");
    }

    private static void atenderSiguienteCliente() {
        System.out.println("\n=== Atender siguiente cliente ===");

        try {
            ResultadoAtencion resultado = tienda.atenderSiguienteClienteYRegistrarVenta();

            System.out.println(resultado.getMensaje());

            if (resultado.isAtendido()) {
                System.out.println(tienda.generarFactura(resultado.getCliente(), resultado.getRuta()));
            }

        } catch (SQLException e) {
            System.out.println("No se pudo registrar la venta.");
            System.out.println("Detalle: " + e.getMessage());
        }
    }

    private static void buscarProducto() {
        System.out.println("\n=== Buscar producto ===");

        String nombre = leerTexto("Nombre del producto: ");
        Producto producto = tienda.buscarProductoEnInventario(nombre);

        if (producto == null) {
            System.out.println("No se encontró el producto.");
        } else {
            System.out.println("Producto encontrado:");
            System.out.println(producto);
        }
    }

    private static void agregarUbicacionMapa() {
        System.out.println("\n=== Agregar ubicación al mapa ===");

        String nombreUbicacion = leerTexto("Nombre de la nueva ubicación: ");
        boolean agregada = tienda.agregarUbicacionAlMapa(nombreUbicacion);

        if (agregada) {
            System.out.println("Ubicación agregada correctamente.");
        } else {
            System.out.println("No se pudo agregar la ubicación. Puede que ya exista o sea inválida.");
        }
    }

    private static void agregarConexionMapa() {
        System.out.println("\n=== Agregar conexión entre ubicaciones ===");

        String origen = leerTexto("Ubicación origen: ");
        String destino = leerTexto("Ubicación destino: ");
        double distancia = leerDouble("Distancia entre ambas ubicaciones: ");

        boolean agregada = tienda.agregarConexionAlMapa(origen, destino, distancia);

        if (agregada) {
            System.out.println("Conexión agregada correctamente.");
        } else {
            System.out.println("No se pudo agregar la conexión. Verifique que ambas ubicaciones existan.");
        }
    }

    private static void mostrarMapa() {
        System.out.println("\n=== Mapa de entregas ===");
        System.out.println(tienda.obtenerRepresentacionMapa());
    }

    private static void mostrarColaClientes() {
        System.out.println("\n=== Cola de clientes ===");

        ColaClientes cola = tienda.getColaClientes();
        List<Cliente> clientes = cola.obtenerClientesEnOrden();

        if (clientes.isEmpty()) {
            System.out.println("No hay clientes en espera.");
            return;
        }

        for (int i = 0; i < clientes.size(); i++) {
            Cliente cliente = clientes.get(i);
            System.out.println((i + 1) + ". " + cliente);
        }
    }

    private static void abrirModoGrafico() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                HomePanel home = new HomePanel(tienda);
                home.setVisible(true);
            }
        });
    }

    private static void recargarInventarioDesdeBD() {
        try {
            tienda.cargarInventarioDesdeBD();
            System.out.println("Inventario recargado correctamente desde la base de datos.");
        } catch (SQLException e) {
            System.out.println("No se pudo recargar el inventario.");
            System.out.println("Detalle: " + e.getMessage());
        }
    }

    private static int leerPrioridad() {
        int prioridad;
        do {
            prioridad = leerEntero("Prioridad (1 = Básico, 2 = Afiliado, 3 = Premium): ");
            if (prioridad < 1 || prioridad > 3) {
                System.out.println("La prioridad debe estar entre 1 y 3.");
            }
        } while (prioridad < 1 || prioridad > 3);
        return prioridad;
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número entero válido.");
            }
        }
    }

    private static double leerDouble(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número decimal válido.");
            }
        }
    }
}