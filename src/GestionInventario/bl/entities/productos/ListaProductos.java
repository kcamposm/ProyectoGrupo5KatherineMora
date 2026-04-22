package GestionInventario.bl.entities.productos;

import java.time.Duration;

public class ListaProductos {

    private Producto primero;

    public ListaProductos() {
        this.primero = null;
    }

    public Producto getPrimero() {
        return primero;
    }

    public void setPrimero(Producto primero) {
        this.primero = primero;
    }

    public boolean estaVacia() {
        return primero == null;
    }

    public void insertarNodoInicio(String nombre, double precio, int cantidadJugadores,
                                   Duration duracionJuego, int edadMinima,
                                   String categoria, String imagenProducto) {
        Producto nuevo = new Producto(nombre, precio, cantidadJugadores, duracionJuego,
                edadMinima, categoria, imagenProducto);
        nuevo.setSiguiente(primero);
        primero = nuevo;
    }

    public void insertarNodoFinal(String nombre, double precio, int cantidadJugadores,
                                  Duration duracionJuego, int edadMinima,
                                  String categoria, String imagenProducto) {
        Producto nuevo = new Producto(nombre, precio, cantidadJugadores, duracionJuego,
                edadMinima, categoria, imagenProducto);
        insertarProductoFinal(nuevo);
    }

    public void insertarProductoFinal(Producto producto) {
        if (producto == null) {
            return;
        }

        producto.setSiguiente(null);

        if (estaVacia()) {
            primero = producto;
            return;
        }

        Producto temp = primero;
        while (temp.getSiguiente() != null) {
            temp = temp.getSiguiente();
        }
        temp.setSiguiente(producto);
    }

    public Producto buscar(String nombre) {
        Producto temp = primero;
        while (temp != null) {
            if (temp.getNombre().equalsIgnoreCase(nombre)) {
                return temp;
            }
            temp = temp.getSiguiente();
        }
        return null;
    }

    public Producto eliminarNodo(String nombre) {
        if (estaVacia()) {
            return null;
        }

        if (primero.getNombre().equalsIgnoreCase(nombre)) {
            Producto eliminado = primero;
            primero = primero.getSiguiente();
            eliminado.setSiguiente(null);
            return eliminado;
        }

        Producto anterior = primero;
        Producto actual = primero.getSiguiente();

        while (actual != null && !actual.getNombre().equalsIgnoreCase(nombre)) {
            anterior = actual;
            actual = actual.getSiguiente();
        }

        if (actual == null) {
            return null;
        }

        anterior.setSiguiente(actual.getSiguiente());
        actual.setSiguiente(null);
        return actual;
    }

    public int contarProductos() {
        int cantidad = 0;
        Producto temp = primero;
        while (temp != null) {
            cantidad++;
            temp = temp.getSiguiente();
        }
        return cantidad;
    }

    public double calcularTotal() {
        double total = 0;
        Producto temp = primero;
        while (temp != null) {
            total += temp.getPrecio();
            temp = temp.getSiguiente();
        }
        return total;
    }

    public String generarDetalleFactura() {
        if (estaVacia()) {
            return "Carrito vacío.\n";
        }

        StringBuilder sb = new StringBuilder();
        Producto temp = primero;
        int linea = 1;

        while (temp != null) {
            sb.append(linea)
                    .append(". ")
                    .append(temp.getNombre())
                    .append(" | Categoría: ")
                    .append(temp.getCategoria())
                    .append(" | Precio: $")
                    .append(String.format("%.2f", temp.getPrecio()))
                    .append("\n");
            temp = temp.getSiguiente();
            linea++;
        }

        sb.append("Total de productos: ").append(contarProductos()).append("\n");
        sb.append("Total a pagar: $").append(String.format("%.2f", calcularTotal())).append("\n");

        return sb.toString();
    }

    public void mostrarLista() {
        if (estaVacia()) {
            System.out.println("La lista está vacía.");
            return;
        }

        Producto temp = primero;
        int indice = 1;
        while (temp != null) {
            System.out.println(indice + ". " + temp);
            temp = temp.getSiguiente();
            indice++;
        }
    }
}