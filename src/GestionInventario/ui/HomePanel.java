package GestionInventario.ui;

import GestionInventario.bl.entities.productos.ListaProductos;
import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal de bienvenida para la aplicación de gestión de inventario
 * Proporciona punto de entrada para diferentes roles de usuario
 */
public class HomePanel extends JFrame {
    // Datos compartidos entre ventanas
    private final ListaProductos sharedInventory;

    // Componentes de la interfaz
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JButton adminButton;
    private JButton clientButton;

    public HomePanel(ListaProductos sharedInventory) {
        this.sharedInventory = sharedInventory;
        initUI();
    }

    /**
     * Inicializa todos los componentes visuales de la ventana principal
     */
    private void initUI() {
        // Configuración básica de la ventana
        setupWindow();

        // Crear paneles principales
        leftPanel = createLeftPanel();
        rightPanel = createRightPanel();

        // Configurar navegación
        setupNavigation();

        // Ensamblar ventana
        assembleWindow();
    }

    /**
     * Configura las propiedades básicas de la ventana principal
     */
    private void setupWindow() {
        setTitle("🏠 Inventario de Juegos de Mesa - Inicio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 520);
        setLocationRelativeTo(null); // Centrar en pantalla
        setMinimumSize(new Dimension(700, 450));
        setResizable(false); // Fijar tamaño para mejor diseño
    }

    /**
     * Crea el panel izquierdo con branding de la aplicación
     */
    private JPanel createLeftPanel() {
        JPanel left = new JPanel();
        left.setPreferredSize(new Dimension(220, 0));
        left.setBackground(new Color(60, 121, 98)); // Verde corporativo
        left.setLayout(new GridBagLayout());

        // Agregar título principal
        addStoreTitle(left);

        return left;
    }

    /**
     * Agrega el título de la tienda al panel izquierdo
     */
    private void addStoreTitle(JPanel parent) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel(
                "<html><div style='color:white;font-weight:bold;font-size:16px;text-align:center;'>" +
                        "🎲<br/>Tienda de<br/>Juegos</div></html>"
        );
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        parent.add(titleLabel, gbc);
    }

    /**
     * Crea el panel derecho con opciones de navegación
     */
    private JPanel createRightPanel() {
        JPanel right = new JPanel();
        right.setBackground(new Color(245, 245, 245)); // Fondo claro
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        return right;
    }

    /**
     * Crea los botones de navegación para diferentes roles
     */
    private void createNavigationButtons() {
        // Botón de acceso administrativo
        adminButton = EstilosUI.crearBotonRedondeado(
                "🔐 Entrar como Administrador",
                new Color(60, 121, 98), // Verde corporativo
                new Color(119, 187, 162)  // Verde más claro
        );
        adminButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        adminButton.setMaximumSize(new Dimension(250, 50));

        // Botón de acceso cliente
        clientButton = EstilosUI.crearBotonRedondeado(
                "👤 Entrar como Cliente",
                new Color(153, 153, 153), // Gris neutro
                new Color(200, 200, 200)  // Gris más claro
        );
        clientButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        clientButton.setMaximumSize(new Dimension(250, 50));
    }

    /**
     * Configura los eventos de navegación para los botones
     */
    private void setupNavigation() {
        createNavigationButtons();

        // Configurar acción del botón administrador
        adminButton.addActionListener(e -> {
            openAdminWindow();
        });

        // Configurar acción del botón cliente
        clientButton.addActionListener(e -> {
            showClientPendingMessage();
        });
    }

    /**
     * Abre la ventana de administración pasando el inventario compartido
     */
    private void openAdminWindow() {
        try {
            // Cerrar ventana actual
            dispose();

            // Crear y mostrar ventana de admin
            AdminWindow adminWindow = new AdminWindow(sharedInventory);
            adminWindow.setVisible(true);

        } catch (Exception ex) {
            showError("Error al abrir ventana de administración", ex);
        }
    }

    /**
     * Muestra mensaje informativo sobre la vista de cliente
     */
    private void showClientPendingMessage() {
        JOptionPane.showMessageDialog(
                this,
                "🚧 La vista de cliente está en desarrollo.\n\n" +
                        "Próximamente podrás:\n" +
                        "• Ver catálogo de juegos\n" +
                        "• Buscar productos\n" +
                        "• Ver detalles de artículos\n\n" +
                        "Por ahora, usa la vista de administrador.",
                "Función en Desarrollo",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Ensambla el contenido del panel derecho
     */
    private void assembleRightPanel() {
        // Mensaje de bienvenida
        JLabel welcomeLabel = new JLabel(
                "<html><div style='font-size:18px;font-weight:bold;color:#333;'>" +
                        "👋 Bienvenido al Sistema</div></html>"
        );
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Espaciado
        rightPanel.add(welcomeLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Botones de navegación
        rightPanel.add(adminButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(clientButton);
    }

    /**
     * Ensambla todos los componentes en la ventana principal
     */
    private void assembleWindow() {
        // Configurar panel derecho
        assembleRightPanel();

        // Configurar layout principal
        setLayout(new BorderLayout());

        // Agregar paneles
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    /**
     * Muestra un mensaje de error con información de depuración
     */
    private void showError(String message, Exception ex) {
        ex.printStackTrace(); // Para depuración en consola

        JOptionPane.showMessageDialog(
                this,
                message + "\n\nDetalles: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Obtiene el inventario compartido (para futuras extensiones)
     */
    public ListaProductos getSharedInventory() {
        return sharedInventory;
    }
}