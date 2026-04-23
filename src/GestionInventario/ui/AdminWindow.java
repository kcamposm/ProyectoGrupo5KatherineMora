package GestionInventario.ui;

import GestionInventario.bl.entities.tienda.Tienda;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal del administrador.
 *
 * Sidebar izquierdo con navegación entre paneles:
 *  1. Inventario      — tabla de productos en el árbol BST.
 *  2. Agregar Juego   — formulario para ingresar nuevos productos.
 *  3. Clientes        — cola de clientes en espera + acciones (ver factura, atender).
 *  4. Cola de Clientes — vista alternativa de la cola con panel de factura integrado.
 *  5. Ubicaciones     — gestión del grafo de rutas (agregar vértices/aristas, ver mapa).
 */
public class AdminWindow extends JFrame {

    private final Tienda tienda;

    // Paneles de contenido
    private InventoryPanel    inventoryPanel;
    private AdminFormPanel    formPanel;
    private ClientesPanel     clientesPanel;
    private ColaClientesPanel colaPanel;
    private UbicacionesPanel  ubicacionesPanel; // panel del mapa de rutas

    // Contenedor principal que alterna entre paneles (switching manual)
    private JPanel mainContentPanel;

    public AdminWindow(Tienda tienda) {
        this.tienda = tienda;
        initUI();
    }

    private void initUI() {
        setupWindow();

        JPanel sidebar = createSidebar();
        mainContentPanel = createMainContentPanel();

        setupNavigation(sidebar);
        assembleWindow(sidebar, mainContentPanel);
    }

    private void setupWindow() {
        setTitle("Panel de Administración - Inventario de Juegos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 560));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Sidebar
    // ─────────────────────────────────────────────────────────────────────────

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(60, 121, 98));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        return sidebar;
    }

    /**
     * Crea los botones del menú lateral en el orden de aparición.
     * El array devuelto mantiene ese orden para asignarse por índice en setupNavigation.
     *
     * Índices: 0=Inventario, 1=AgregarJuego, 2=Clientes,
     *          3=Cola, 4=Ubicaciones, 5=Volver
     */
    private JButton[] createNavigationButtons() {
        JButton inventarioBtn = EstilosUI.crearBotonRedondeado(
                "Inventario", new Color(120, 120, 120), new Color(160, 160, 160)
        );
        JButton formBtn = EstilosUI.crearBotonRedondeado(
                "Agregar Juego", new Color(120, 120, 120), new Color(160, 160, 160)
        );
        JButton clientesBtn = EstilosUI.crearBotonRedondeado(
                "Clientes", new Color(120, 120, 120), new Color(160, 160, 160)
        );
        JButton colaBtn = EstilosUI.crearBotonRedondeado(
                "Cola de Clientes", new Color(80, 100, 155), new Color(110, 130, 185)
        );
        JButton ubicacionesBtn = EstilosUI.crearBotonRedondeado(
                "Ubicaciones", new Color(120, 80, 140), new Color(155, 110, 175)
        );
        JButton volverBtn = EstilosUI.crearBotonRedondeado(
                "Volver al inicio", new Color(160, 90, 90), new Color(190, 120, 120)
        );

        JButton[] buttons = {inventarioBtn, formBtn, clientesBtn, colaBtn, ubicacionesBtn, volverBtn};

        for (JButton btn : buttons) {
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        return buttons;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Panel de contenido principal
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Instancia todos los sub-paneles y muestra el inventario por defecto.
     */
    private JPanel createMainContentPanel() {
        JPanel main = new JPanel(new BorderLayout());

        inventoryPanel   = new InventoryPanel(tienda);
        clientesPanel    = new ClientesPanel(tienda);
        formPanel        = new AdminFormPanel(tienda, inventoryPanel, clientesPanel);
        colaPanel        = new ColaClientesPanel(tienda);
        ubicacionesPanel = new UbicacionesPanel(tienda); // carga mapa base automáticamente

        inventoryPanel.refresh();

        // Vista inicial: inventario
        main.add(inventoryPanel.getMainPanel(), BorderLayout.CENTER);
        return main;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Navegación
    // ─────────────────────────────────────────────────────────────────────────

    private void setupNavigation(JPanel sidebar) {
        JButton[] buttons = createNavigationButtons();

        JButton inventarioBtn    = buttons[0];
        JButton formBtn          = buttons[1];
        JButton clientesBtn      = buttons[2];
        JButton colaBtn          = buttons[3];
        JButton ubicacionesBtn   = buttons[4];
        JButton volverBtn        = buttons[5];

        // Añadir botones al sidebar con separadores
        sidebar.add(inventarioBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(formBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(clientesBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(colaBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(ubicacionesBtn);
        sidebar.add(Box.createVerticalGlue()); // separa "Volver" hacia el fondo
        sidebar.add(volverBtn);

        // ── Handlers de navegación ───────────────────────────────────────────

        inventarioBtn.addActionListener(e -> {
            inventoryPanel.refresh();
            switchToPanel(inventoryPanel.getMainPanel(), "Inventario");
        });

        formBtn.addActionListener(e ->
                switchToPanel(formPanel.getMainPanel(), "Agregar Juego")
        );

        clientesBtn.addActionListener(e -> {
            // Refrescar la tabla de cola al entrar al panel
            clientesPanel.recargarTabla();
            switchToPanel(clientesPanel.getMainPanel(), "Clientes");
        });

        colaBtn.addActionListener(e -> {
            colaPanel.recargarCola();
            switchToPanel(colaPanel.getMainPanel(), "Cola de Clientes");
        });

        // Al abrir Ubicaciones, refrescar la representación del mapa
        ubicacionesBtn.addActionListener(e -> {
            ubicacionesPanel.actualizarMapa();
            switchToPanel(ubicacionesPanel.getMainPanel(), "Ubicaciones");
        });

        volverBtn.addActionListener(e -> volverAlInicio());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Utilidades
    // ─────────────────────────────────────────────────────────────────────────

    /** Intercambia el panel de contenido visible y actualiza el título de la ventana. */
    private void switchToPanel(JPanel newPanel, String title) {
        mainContentPanel.removeAll();
        mainContentPanel.add(newPanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
        setTitle("Panel de Administración — " + title);
    }

    /** Cierra esta ventana y reabre HomePanel. */
    private void volverAlInicio() {
        try {
            dispose();
            HomePanel homePanel = new HomePanel(tienda);
            homePanel.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo volver al inicio.\nDetalle: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assembleWindow(JPanel sidebar, JPanel mainContent) {
        setLayout(new BorderLayout());
        add(sidebar,     BorderLayout.WEST);
        add(mainContent, BorderLayout.CENTER);
    }
}
