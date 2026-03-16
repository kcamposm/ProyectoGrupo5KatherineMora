import GestionInventario.bl.entities.clientes.Cliente;
import GestionInventario.bl.entities.productos.Producto;
import GestionInventario.bl.entities.tienda.Tienda;

import java.time.Duration;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Tienda tienda = new Tienda("Tienda de Juegos CENFOTEC");

    public static void main(String[] args) {
        cargarDatosEjemplo();
        menu();
    }

    public static void menu() {
        int opcion;

        do {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> agregarProductoInventario();
                case 2 -> mostrarInventario();
                case 3 -> registrarClienteYCarrito();
                case 4 -> atenderSiguienteCliente();
                case 5 -> mostrarColaClientes();
                case 6 -> buscarProducto();
                case 7 -> System.out.println("Saliendo del sistema...");
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }

        } while (opcion != 7);
    }

    private static void mostrarMenu() {
        System.out.println("\n====================================");
        System.out.println(" SISTEMA DE GESTIÓN DE INVENTARIOS ");
        System.out.println("====================================");
        System.out.println("1. Agregar producto al inventario");
        System.out.println("2. Mostrar inventario");
        System.out.println("3. Registrar cliente y llenar carrito");
        System.out.println("4. Atender siguiente cliente");
        System.out.println("5. Mostrar cola de clientes");
        System.out.println("6. Buscar producto por nombre");
        System.out.println("7. Salir");
    }

    private static void agregarProductoInventario() {
        System.out.println("\n=== Agregar producto al inventario ===");
        String nombre = leerTexto("Nombre: ");
        double precio = leerDouble("Precio: ");
        int cantidadJugadores = leerEntero("Cantidad de jugadores: ");
        int duracionMinutos = leerEntero("Duración en minutos: ");
        int edadMinima = leerEntero("Edad mínima: ");
        String categoria = leerTexto("Categoría: ");

        tienda.agregarProductoAlInventario(
                nombre,
                precio,
                cantidadJugadores,
                Duration.ofMinutes(duracionMinutos),
                edadMinima,
                categoria,
                ""
        );

        System.out.println("Producto agregado correctamente al inventario.");
    }

    private static void mostrarInventario() {
        System.out.println("\n=== Inventario de la tienda ===");
        tienda.mostrarInventario();
    }

    private static void registrarClienteYCarrito() {
        System.out.println("\n=== Registrar cliente en la cola ===");
        String nombreCliente = leerTexto("Nombre del cliente: ");
        int prioridad = leerPrioridad();

        Cliente cliente = new Cliente(nombreCliente, prioridad);

        if (tienda.getInventario().estaVacio()) {
            System.out.println("No hay productos en el inventario. El cliente se agregará con carrito vacío.");
        } else {
            String continuar;
            do {
                System.out.println("\nInventario disponible:");
                tienda.mostrarInventario();

                String nombreProducto = leerTexto("Escriba el nombre exacto del producto para agregar al carrito: ");
                boolean agregado = tienda.agregarProductoAlCarrito(cliente, nombreProducto);

                if (agregado) {
                    System.out.println("Producto agregado al carrito.");
                } else {
                    System.out.println("El producto no existe en el inventario.");
                }

                continuar = leerTexto("¿Desea agregar otro producto al carrito? (s/n): ");
            } while (continuar.equalsIgnoreCase("s"));
        }

        tienda.encolarCliente(cliente);
        System.out.println("Cliente registrado correctamente en la cola.");
    }

    private static void atenderSiguienteCliente() {
        System.out.println("\n=== Atender siguiente cliente ===");
        Cliente cliente = tienda.atenderSiguienteCliente();

        if (cliente == null) {
            System.out.println("No hay clientes en la cola.");
            return;
        }

        System.out.println(tienda.generarFactura(cliente));
    }

    private static void mostrarColaClientes() {
        System.out.println();
        tienda.getColaClientes().mostrarCola();
    }

    private static void buscarProducto() {
        System.out.println("\n=== Buscar producto ===");
        String nombre = leerTexto("Nombre del producto: ");
        Producto producto = tienda.buscarProductoEnInventario(nombre);

        if (producto == null) {
            System.out.println("No se encontró el producto en el inventario.");
        } else {
            System.out.println("Producto encontrado: " + producto);
        }
    }

    private static int leerPrioridad() {
        int prioridad;
        do {
            prioridad = leerEntero("Prioridad del cliente (1 = Básico, 2 = Afiliado, 3 = Premium): ");
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
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número decimal válido.");
            }
        }
    }

    private static void cargarDatosEjemplo() {
        tienda.agregarProductoAlInventario("Catan", 45.50, 4, Duration.ofMinutes(90), 10, "Estrategia", "");
        tienda.agregarProductoAlInventario("Dixit", 30.00, 6, Duration.ofMinutes(40), 8, "Fiesta", "");
        tienda.agregarProductoAlInventario("Carcassonne", 35.75, 5, Duration.ofMinutes(60), 8, "Estrategia", "");
        tienda.agregarProductoAlInventario("Pandemic", 42.00, 4, Duration.ofMinutes(50), 10, "Cooperativo", "");
    }
}