package GestionInventario.ui;

import GestionInventario.bl.entities.tienda.Tienda;

import javax.swing.*;
import java.awt.*;

public class AdminWindow extends JFrame {

    private final Tienda tienda;
    private InventoryPanel inventoryPanel;
    private AdminFormPanel formPanel;
    private ClientesPanel clientesPanel;
    private VentasPanel ventasPanel;
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

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(60, 121, 98));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        return sidebar;
    }

    private JButton[] createNavigationButtons() {
        JButton inventarioBtn = EstilosUI.crearBotonRedondeado(
                "Inventario",
                new Color(120, 120, 120),
                new Color(160, 160, 160)
        );

        JButton formBtn = EstilosUI.crearBotonRedondeado(
                "Agregar Juego",
                new Color(120, 120, 120),
                new Color(160, 160, 160)
        );

        JButton clientesBtn = EstilosUI.crearBotonRedondeado(
                "Clientes",
                new Color(120, 120, 120),
                new Color(160, 160, 160)
        );

        JButton ventasBtn = EstilosUI.crearBotonRedondeado(
                "Ventas",
                new Color(120, 120, 120),
                new Color(160, 160, 160)
        );

        JButton volverBtn = EstilosUI.crearBotonRedondeado(
                "Volver al inicio",
                new Color(160, 90, 90),
                new Color(190, 120, 120)
        );

        JButton[] buttons = {inventarioBtn, formBtn, clientesBtn, ventasBtn, volverBtn};

        for (JButton btn : buttons) {
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        return buttons;
    }

    private JPanel createMainContentPanel() {
        JPanel main = new JPanel(new BorderLayout());

        inventoryPanel = new InventoryPanel(tienda);
        clientesPanel = new ClientesPanel(tienda);
        ventasPanel = new VentasPanel(tienda, clientesPanel);
        formPanel = new AdminFormPanel(tienda, inventoryPanel, clientesPanel);

        inventoryPanel.refresh();
        clientesPanel.recargarProductos();

        main.add(inventoryPanel.getMainPanel(), BorderLayout.CENTER);
        return main;
    }

    private void setupNavigation(JPanel sidebar) {
        JButton[] buttons = createNavigationButtons();

        JButton inventarioBtn = buttons[0];
        JButton formBtn = buttons[1];
        JButton clientesBtn = buttons[2];
        JButton ventasBtn = buttons[3];
        JButton volverBtn = buttons[4];

        sidebar.add(inventarioBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(formBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(clientesBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(ventasBtn);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(volverBtn);

        inventarioBtn.addActionListener(e -> {
            inventoryPanel.refresh();
            switchToPanel(inventoryPanel.getMainPanel(), "Inventario");
        });

        formBtn.addActionListener(e -> switchToPanel(formPanel.getMainPanel(), "Agregar Juego"));

        clientesBtn.addActionListener(e -> {
            clientesPanel.recargarProductos();
            switchToPanel(clientesPanel.getMainPanel(), "Clientes");
        });

        ventasBtn.addActionListener(e -> switchToPanel(ventasPanel.getMainPanel(), "Ventas"));

        volverBtn.addActionListener(e -> volverAlInicio());
    }

    private void switchToPanel(JPanel newPanel, String title) {
        mainContentPanel.removeAll();
        mainContentPanel.add(newPanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
        setTitle("Panel de Administración - " + title);
    }

    private void volverAlInicio() {
        try {
            dispose();
            HomePanel homePanel = new HomePanel(tienda);
            homePanel.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo volver al inicio.\nDetalle: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void assembleWindow(JPanel sidebar, JPanel mainContent) {
        setLayout(new BorderLayout());
        add(sidebar, BorderLayout.WEST);
        add(mainContent, BorderLayout.CENTER);
    }
}