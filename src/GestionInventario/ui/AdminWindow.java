package GestionInventario.ui;

import GestionInventario.bl.entities.productos.ListaProductos;

import javax.swing.*;
import java.awt.*;

public class AdminWindow extends JFrame {
    private final ListaProductos lista;
    private InventoryPanel inventoryPanel;
    private AdminFormPanel formPanel;

    public AdminWindow(ListaProductos lista) {
        this.lista = lista;
        initUI();
    }


    private void initUI() {
        setTitle("Admin");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(60,121,98));
        sidebar.setPreferredSize(new Dimension(200,0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));

        JButton inventarioBtn = EstilosUI.crearBotonRedondeado("Inventario", new Color(153,153,153), new Color(200,200,200));
        JButton formBtn = EstilosUI.crearBotonRedondeado("Agregar Juego", new Color(153,153,153), new Color(200,200,200));
        JButton clientesBtn = EstilosUI.crearBotonRedondeado("Clientes (pendiente)", new Color(153,153,153), new Color(200,200,200));
        JButton ventasBtn = EstilosUI.crearBotonRedondeado("Ventas (pendiente)", new Color(153,153,153), new Color(200,200,200));

        inventarioBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        formBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        clientesBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        ventasBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(inventarioBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0,10)));
        sidebar.add(formBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0,10)));
        sidebar.add(clientesBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0,10)));
        sidebar.add(ventasBtn);

        // Panel principal
        JPanel main = new JPanel(new BorderLayout());
        inventoryPanel = new InventoryPanel(lista);
        formPanel = new AdminFormPanel(lista, inventoryPanel);

        // Mostrar inventario por defecto
        main.add(inventoryPanel.getMainPanel(), BorderLayout.CENTER);

        inventarioBtn.addActionListener(e -> {
            main.removeAll();
            main.add(inventoryPanel.getMainPanel(), BorderLayout.CENTER);
            main.revalidate(); main.repaint();
        });

        formBtn.addActionListener(e -> {
            main.removeAll();
            main.add(formPanel.getMainPanel(), BorderLayout.CENTER);
            main.revalidate(); main.repaint();
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(main, BorderLayout.CENTER);
    }
}