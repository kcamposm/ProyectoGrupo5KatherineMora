import GestionInventario.bl.entities.tienda.Tienda;
import GestionInventario.ui.HomePanel;

import javax.swing.*;
import java.sql.SQLException;
import java.time.Duration;

public class Main {

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

        tienda = new Tienda("Tienda de Juegos CENFOTEC", "Cartago");
        inicializarSistema();
        
        // Ejecutar directamente la interfaz gráfica
        SwingUtilities.invokeLater(() -> {
            try {
                HomePanel homePanel = new HomePanel(tienda);
                homePanel.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "No se pudo iniciar la interfaz gráfica:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            }
        });
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
            JOptionPane.showMessageDialog(
                    null,
                    "No fue posible cargar la base de datos.\nDetalle: " + e.getMessage(),
                    "Error de Base de Datos",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
