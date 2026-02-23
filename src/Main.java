
import GestionInventario.bl.entities.productos.ListaProductos;
import GestionInventario.ui.HomePanel;
import javax.swing.*;
import java.time.Duration;

public class Main {
    public static void main(String[] args) {
        // Manejador global para ver excepciones silenciosas
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Excepción no capturada: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        });
        System.out.println("Main: DefaultUncaughtExceptionHandler instalado");

        // Crear datos de ejemplo (opcional)
        ListaProductos lista = new ListaProductos();
        lista.insertarNodoFinal("Catan", 45.50, 4, Duration.ofMinutes(90), 10, "Estrategia", "");
        lista.insertarNodoFinal("Dixit", 30.00, 6, Duration.ofMinutes(40), 8, "Fiesta", "");
        System.out.println("Main: lista de ejemplo creada");

        // Arrancar UI en EDT y pasar lista
        SwingUtilities.invokeLater(() -> {
            HomePanel home = new HomePanel(lista);
            home.setVisible(true);
        });

    }
}

