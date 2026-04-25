package GestionInventario.ui;

import GestionInventario.bl.entities.productos.ListaProductos;
import javax.swing.*;
import java.awt.*;

public class HomePanel extends JFrame {
    private final ListaProductos listaCompartida;

    public HomePanel(ListaProductos listaCompartida) {
        this.listaCompartida = listaCompartida;
        initUI();
    }

    private void initUI() {
        setTitle("Home - Gestión Inventario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 520);
        setLocationRelativeTo(null);

        JPanel left = new JPanel();
        left.setPreferredSize(new Dimension(220, 0));
        left.setBackground(new Color(60, 121, 98));
        left.setLayout(new GridBagLayout());
        left.add(new JLabel("<html><div style='color:white;font-weight:bold;'>Tienda<br/>de Juegos</div></html>"));

        JPanel right = new JPanel();
        right.setBackground(new Color(245, 245, 245));
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));

        JButton adminBtn = EstilosUI.crearBotonRedondeado("Entrar como Admin", new Color(60,121,98), new Color(119,187,162));
        adminBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        adminBtn.addActionListener(e -> {
            // Abrir ventana de admin pasando la lista compartida
            dispose();
            AdminWindow adminWindow = new AdminWindow(listaCompartida);
            adminWindow.setVisible(true);
        });

        JButton clientBtn = EstilosUI.crearBotonRedondeado("Entrar como Cliente", new Color(153,153,153), new Color(200,200,200));
        clientBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        clientBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Vista cliente pendiente (próxima iteración).");
        });

        right.add(new JLabel("Bienvenido"));
        right.add(Box.createRigidArea(new Dimension(0,20)));
        right.add(adminBtn);
        right.add(Box.createRigidArea(new Dimension(0,10)));
        right.add(clientBtn);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(left, BorderLayout.WEST);
        getContentPane().add(right, BorderLayout.CENTER);
    }
}