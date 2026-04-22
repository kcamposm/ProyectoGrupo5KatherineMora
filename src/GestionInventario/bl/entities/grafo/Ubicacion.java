package GestionInventario.bl.entities.grafo;

public class Ubicacion {

    private String nombre;

    public Ubicacion(String nombre) {
        this.nombre = nombre == null ? "" : nombre.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? "" : nombre.trim();
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Ubicacion)) return false;

        Ubicacion otra = (Ubicacion) obj;
        return nombre != null && nombre.equalsIgnoreCase(otra.nombre);
    }

    @Override
    public int hashCode() {
        return nombre == null ? 0 : nombre.toLowerCase().hashCode();
    }
}