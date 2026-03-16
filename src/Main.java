import GestionInventario.bl.entities.clientes.Cliente;
import GestionInventario.bl.entities.productos.Producto;
import GestionInventario.bl.entities.tienda.Tienda;
import GestionInventario.ui.HomePanel;

import javax.swing.*;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static Tienda tienda;

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Excepción no capturada: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        });

        tienda = new Tienda("Tienda de Juegos CENFOTEC");

        inicializarDatos();

        menu();
    }

    private static void inicializarDatos() {
        try {
            tienda.cargarInventarioDesdeBD();

            // Solo inserta ejemplos si la BD está vacía
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
                case 1 -> agregarProductoInventario();
                case 2 -> mostrarInventario();
                case 3 -> registrarClienteYLlenarCarrito();
                case 4 -> atenderSiguienteCliente();
                case 5 -> buscarProducto();
                case 6 -> abrirModoGrafico();
                case 7 -> recargarInventarioDesdeBD();
                case 8 -> System.out.println("Saliendo del sistema...");
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }

        } while (opcion != 8);
    }

    private static void mostrarMenu() {
        System.out.println("\n==============================================");
        System.out.println(" SISTEMA DE GESTIÓN DE INVENTARIO Y VENTAS ");
        System.out.println("==============================================");
        System.out.println("1. Agregar producto al inventario");
        System.out.println("2. Mostrar inventario");
        System.out.println("3. Registrar cliente y llenar carrito");
        System.out.println("4. Atender siguiente cliente y guardar venta");
        System.out.println("5. Buscar producto por nombre");
        System.out.println("6. Abrir interfaz gráfica Swing");
        System.out.println("7. Recargar inventario desde la base de datos");
        System.out.println("8. Salir");
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
            System.out.println("Producto agregado correctamente en memoria y base de datos.");
        } catch (SQLException e) {
            System.out.println("No se pudo guardar el producto en la base de datos.");
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

        Cliente cliente = new Cliente(nombreCliente, prioridad);

        if (tienda.getInventario().estaVacio()) {
            System.out.println("No hay productos en inventario.");
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
                    System.out.println("No se encontró el producto en inventario.");
                }

                continuar = leerTexto("¿Desea agregar otro producto? (s/n): ");
            } while (continuar.equalsIgnoreCase("s"));
        }

        tienda.encolarCliente(cliente);
        System.out.println("Cliente agregado a la cola correctamente.");
    }

    private static void atenderSiguienteCliente() {
        System.out.println("\n=== Atender siguiente cliente ===");

        try {
            Cliente cliente = tienda.atenderSiguienteClienteYRegistrarVenta();

            if (cliente == null) {
                System.out.println("No hay clientes en cola.");
                return;
            }

            System.out.println(tienda.generarFactura(cliente));
            System.out.println("La venta fue guardada en la base de datos.");
        } catch (SQLException e) {
            System.out.println("No se pudo registrar la venta en la base de datos.");
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

    private static void abrirModoGrafico() {
        SwingUtilities.invokeLater(() -> {
            HomePanel home = new HomePanel(tienda);
            home.setVisible(true);
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