package GestionInventario.dao;

import GestionInventario.bl.entities.clientes.Cliente;
import GestionInventario.bl.entities.productos.Producto;

import java.sql.*;

public class VentaDAO {

    public void guardarVenta(Cliente cliente) throws SQLException {
        if (cliente == null) {
            throw new SQLException("El cliente no puede ser null.");
        }

        String sqlVenta = "INSERT INTO ventas(cliente_nombre, prioridad, total) VALUES (?, ?, ?)";
        String sqlDetalle = "INSERT INTO venta_detalle(venta_id, nombre_producto, precio, categoria) VALUES (?, ?, ?, ?)";

        Connection con = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            int ventaId;

            try (PreparedStatement psVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                psVenta.setString(1, cliente.getNombre());
                psVenta.setInt(2, cliente.getPrioridad());
                psVenta.setDouble(3, cliente.getCarrito().calcularTotal());

                int filasAfectadas = psVenta.executeUpdate();

                if (filasAfectadas == 0) {
                    throw new SQLException("No se pudo insertar la venta.");
                }

                try (ResultSet generatedKeys = psVenta.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        ventaId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID de la venta.");
                    }
                }
            }

            try (PreparedStatement psDetalle = con.prepareStatement(sqlDetalle)) {
                Producto actual = cliente.getCarrito().getPrimero();

                while (actual != null) {
                    psDetalle.setInt(1, ventaId);
                    psDetalle.setString(2, actual.getNombre());
                    psDetalle.setDouble(3, actual.getPrecio());
                    psDetalle.setString(4, actual.getCategoria());
                    psDetalle.addBatch();

                    actual = actual.getSiguiente();
                }

                psDetalle.executeBatch();
            }

            con.commit();

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw e;

        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }
}