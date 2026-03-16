package GestionInventario.dao;

import GestionInventario.bl.entities.productos.Producto;

import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public void insertar(Producto producto) throws SQLException {
        String sql = "INSERT INTO productos(nombre, precio, cantidad_jugadores, duracion_minutos, edad_minima, categoria, imagen_producto) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getCantidadJugadores());

            if (producto.getDuracionJuego() != null) {
                ps.setLong(4, producto.getDuracionJuego().toMinutes());
            } else {
                ps.setLong(4, 0);
            }

            ps.setInt(5, producto.getEdadMinima());
            ps.setString(6, producto.getCategoria());
            ps.setString(7, producto.getImagenProducto());

            ps.executeUpdate();
        }
    }

    public List<Producto> listarTodos() throws SQLException {
        List<Producto> productos = new ArrayList<>();

        String sql = "SELECT nombre, precio, cantidad_jugadores, duracion_minutos, edad_minima, categoria, imagen_producto " +
                "FROM productos ORDER BY nombre";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Producto producto = new Producto(
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("cantidad_jugadores"),
                        Duration.ofMinutes(rs.getLong("duracion_minutos")),
                        rs.getInt("edad_minima"),
                        rs.getString("categoria"),
                        rs.getString("imagen_producto")
                );

                productos.add(producto);
            }
        }

        return productos;
    }

    public Producto buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT nombre, precio, cantidad_jugadores, duracion_minutos, edad_minima, categoria, imagen_producto " +
                "FROM productos WHERE nombre = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Producto(
                            rs.getString("nombre"),
                            rs.getDouble("precio"),
                            rs.getInt("cantidad_jugadores"),
                            Duration.ofMinutes(rs.getLong("duracion_minutos")),
                            rs.getInt("edad_minima"),
                            rs.getString("categoria"),
                            rs.getString("imagen_producto")
                    );
                }
            }
        }

        return null;
    }

    public boolean existeProducto(String nombre) throws SQLException {
        String sql = "SELECT COUNT(*) FROM productos WHERE nombre = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    public void eliminarPorNombre(String nombre) throws SQLException {
        String sql = "DELETE FROM productos WHERE nombre = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.executeUpdate();
        }
    }

    public void actualizar(Producto producto, String nombreOriginal) throws SQLException {
        String sql = "UPDATE productos " +
                "SET nombre = ?, precio = ?, cantidad_jugadores = ?, duracion_minutos = ?, edad_minima = ?, categoria = ?, imagen_producto = ? " +
                "WHERE nombre = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getCantidadJugadores());

            if (producto.getDuracionJuego() != null) {
                ps.setLong(4, producto.getDuracionJuego().toMinutes());
            } else {
                ps.setLong(4, 0);
            }

            ps.setInt(5, producto.getEdadMinima());
            ps.setString(6, producto.getCategoria());
            ps.setString(7, producto.getImagenProducto());
            ps.setString(8, nombreOriginal);

            ps.executeUpdate();
        }
    }
}