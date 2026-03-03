package GestionInventario.ui;

import GestionInventario.bl.entities.productos.ListaProductos;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal de administración para la gestión del inventario
 * Proporciona acceso a las diferentes funcionalidades del sistema
 */
public class AdminWindow extends JFrame {
    // Componentes principales
    private final ListaProductos lista;
    private InventoryPanel inventoryPanel;
    private AdminFormPanel formPanel;
    
    // Panel contenedor principal para el contenido dinámico
    private JPanel mainContentPanel;

    public AdminWindow(ListaProductos lista) {
        this.lista = lista;
        initUI();
    }

    /**
     * Inicializa todos los componentes visuales de la ventana
     */
    private void initUI() {
        // Configuración básica de la ventana
        setupWindow();
        
        // Crear componentes
        JPanel sidebar = createSidebar();
        mainContentPanel = createMainContentPanel();
        
        // Configurar navegación
        setupNavigation(sidebar);
        
        // Ensamblar la ventana
        assembleWindow(sidebar, mainContentPanel);
    }
    
    /**
     * Configura las propiedades básicas de la ventana principal
     */
    private void setupWindow() {
        setTitle("Panel de Administración - Inventario de Juegos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null); // Centrar en pantalla
        setMinimumSize(new Dimension(800, 500));
    }
    
    /**
     * Crea el panel lateral de navegación
     */
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(60, 121, 98)); // Verde oscuro
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        return sidebar;
    }
    
    /**
     * Crea los botones de navegación del sidebar
     */
    private JButton[] createNavigationButtons() {
        JButton inventarioBtn = EstilosUI.crearBotonRedondeado("📦 Inventario", 
            new Color(153, 153, 153), new Color(200, 200, 200));
        JButton formBtn = EstilosUI.crearBotonRedondeado("➕ Agregar Juego", 
            new Color(153, 153, 153), new Color(200, 200, 200));
        JButton clientesBtn = EstilosUI.crearBotonRedondeado("👥 Clientes (pendiente)", 
            new Color(153, 153, 153), new Color(200, 200, 200));
        JButton ventasBtn = EstilosUI.crearBotonRedondeado("💰 Ventas (pendiente)", 
            new Color(153, 153, 153), new Color(200, 200, 200));
        
        // Configurar tamaño máximo para todos los botones
        JButton[] buttons = {inventarioBtn, formBtn, clientesBtn, ventasBtn};
        for (JButton btn : buttons) {
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        
        return buttons;
    }
    
    /**
     * Crea el panel principal donde se mostrará el contenido dinámico
     */
    private JPanel createMainContentPanel() {
        JPanel main = new JPanel(new BorderLayout());
        
        // Inicializar los paneles de contenido
        inventoryPanel = new InventoryPanel(lista);
        formPanel = new AdminFormPanel(lista, inventoryPanel);
        
        // Mostrar inventario por defecto
        main.add(inventoryPanel.getMainPanel(), BorderLayout.CENTER);
        
        return main;
    }
    
    /**
     * Configura los eventos de navegación para los botones
     */
    private void setupNavigation(JPanel sidebar) {
        JButton[] buttons = createNavigationButtons();
        JButton inventarioBtn = buttons[0];
        JButton formBtn = buttons[1];
        JButton clientesBtn = buttons[2];
        JButton ventasBtn = buttons[3];
        
        // Agregar botones al sidebar con espaciado
        sidebar.add(inventarioBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(formBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(clientesBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(ventasBtn);
        
        // Configurar navegación - Inventario
        inventarioBtn.addActionListener(e -> {
            switchToPanel(inventoryPanel.getMainPanel(), "Inventario");
        });
        
        // Configurar navegación - Agregar Juego
        formBtn.addActionListener(e -> {
            switchToPanel(formPanel.getMainPanel(), "Agregar Juego");
        });
        
        // Configurar navegación - Clientes (pendiente)
        clientesBtn.addActionListener(e -> {
            showPendingFeature("Clientes");
        });
        
        // Configurar navegación - Ventas (pendiente)
        ventasBtn.addActionListener(e -> {
            showPendingFeature("Ventas");
        });
    }
    
    /**
     * Cambia el panel principal mostrado
     */
    private void switchToPanel(JPanel newPanel, String title) {
        mainContentPanel.removeAll();
        mainContentPanel.add(newPanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
        
        // Opcional: actualizar título de la ventana
        setTitle("Panel de Administración - " + title);
    }
    
    /**
     * Muestra un mensaje para funciones pendientes
     */
    private void showPendingFeature(String featureName) {
        JOptionPane.showMessageDialog(this, 
            "La funcionalidad de '" + featureName + "' está en desarrollo.\n" +
            "Próximamente disponible.", 
            "Función en Desarrollo", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Ensambla todos los componentes en la ventana principal
     */
    private void assembleWindow(JPanel sidebar, JPanel mainContent) {
        setLayout(new BorderLayout());
        add(sidebar, BorderLayout.WEST);
        add(mainContent, BorderLayout.CENTER);
    }
}