package GestionInventario.bl.entities.productos;

public class ArbolProductos {

    private static class NodoArbol {
        private Producto producto;
        private NodoArbol izquierdo;
        private NodoArbol derecho;

        public NodoArbol(Producto producto) {
            this.producto = producto;
        }
    }

    private NodoArbol raiz;

    public void insertar(Producto producto) {
        if (producto == null) {
            return;
        }
        raiz = insertarRec(raiz, producto);
    }

    private NodoArbol insertarRec(NodoArbol actual, Producto producto) {
        if (actual == null) {
            return new NodoArbol(producto);
        }

        int comparacion = producto.getNombre().compareToIgnoreCase(actual.producto.getNombre());

        if (comparacion < 0) {
            actual.izquierdo = insertarRec(actual.izquierdo, producto);
        } else if (comparacion > 0) {
            actual.derecho = insertarRec(actual.derecho, producto);
        } else {
            actual.producto = producto;
        }

        return actual;
    }

    public Producto buscar(String nombre) {
        return buscarRec(raiz, nombre);
    }

    private Producto buscarRec(NodoArbol actual, String nombre) {
        if (actual == null || nombre == null) {
            return null;
        }

        int comparacion = nombre.compareToIgnoreCase(actual.producto.getNombre());

        if (comparacion == 0) {
            return actual.producto;
        }
        if (comparacion < 0) {
            return buscarRec(actual.izquierdo, nombre);
        }
        return buscarRec(actual.derecho, nombre);
    }

    public boolean contiene(String nombre) {
        return buscar(nombre) != null;
    }

    public void mostrarEnOrden() {
        if (raiz == null) {
            System.out.println("El inventario está vacío.");
            return;
        }
        mostrarEnOrdenRec(raiz);
    }

    private void mostrarEnOrdenRec(NodoArbol actual) {
        if (actual == null) {
            return;
        }
        mostrarEnOrdenRec(actual.izquierdo);
        System.out.println(actual.producto);
        mostrarEnOrdenRec(actual.derecho);
    }

    public boolean estaVacio() {
        return raiz == null;
    }
}