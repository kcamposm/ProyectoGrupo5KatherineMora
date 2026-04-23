package GestionInventario.bl.entities.productos;

import GestionInventario.bl.entities.productos.Producto;
import GestionInventario.dl.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            ps.setLong(4, producto.getDuracionJuego() != null ? producto.getDuracionJuego().toMinutes() : 0);
            ps.setInt(5, producto.getEdadMinima());
            ps.setString(6, producto.getCategoria());
            ps.setString(7, producto.getImagenProducto());

            ps.executeUpdate();
        }
    }

    public List<Producto> listarTodos() throws SQLException {
        List<Producto> productos = new ArrayList<Producto>();

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
}