package GestionInventario.ui;

import GestionInventario.bl.entities.tienda.Tienda;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JFrame {

    private final Tienda tienda;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JButton adminButton;
    private JButton clientButton;

    public HomePanel(Tienda tienda) {
        this.tienda = tienda;
        initUI();
    }

    private void initUI() {
        setupWindow();
        leftPanel = createLeftPanel();
        rightPanel = createRightPanel();
        setupNavigation();
        assembleWindow();
    }

    private void setupWindow() {
        setTitle("Inventario de Juegos de Mesa - Inicio");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 520);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(700, 450));
        setResizable(true);
    }

    private JPanel createLeftPanel() {
        JPanel left = new JPanel(new GridBagLayout());
        left.setPreferredSize(new Dimension(240, 0));
        left.setBackground(new Color(60, 121, 98));

        JLabel titleLabel = new JLabel(
                "<html><div style='color:white;font-weight:bold;font-size:18px;text-align:center;'>" +
                        "Tienda de<br/>Juegos</div></html>"
        );
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        left.add(titleLabel);

        return left;
    }

    private JPanel createRightPanel() {
        JPanel right = new JPanel();
        right.setBackground(new Color(245, 245, 245));
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        JLabel welcome = new JLabel("Bienvenido al sistema de inventario");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 22));
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Seleccione cómo desea ingresar");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setForeground(new Color(90, 90, 90));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        createNavigationButtons();

        right.add(welcome);
        right.add(Box.createRigidArea(new Dimension(0, 10)));
        right.add(subtitle);
        right.add(Box.createRigidArea(new Dimension(0, 40)));
        right.add(adminButton);
        right.add(Box.createRigidArea(new Dimension(0, 18)));
        right.add(clientButton);
        right.add(Box.createVerticalGlue());

        return right;
    }

    private void createNavigationButtons() {
        adminButton = EstilosUI.crearBotonRedondeado(
                "Entrar como Administrador",
                new Color(60, 121, 98),
                new Color(91, 153, 128)
        );
        adminButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        adminButton.setMaximumSize(new Dimension(280, 48));

        clientButton = EstilosUI.crearBotonRedondeado(
                "Entrar como Cliente",
                new Color(130, 130, 130),
                new Color(170, 170, 170)
        );
        clientButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        clientButton.setMaximumSize(new Dimension(280, 48));
    }

    private void setupNavigation() {
        adminButton.addActionListener(e -> openAdminWindow());
        clientButton.addActionListener(e -> openAdminWindow());
    }

    private void openAdminWindow() {
        try {
            dispose();
            AdminWindow adminWindow = new AdminWindow(tienda);
            adminWindow.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo abrir la ventana de administración.\nDetalle: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void assembleWindow() {
        setLayout(new BorderLayout());
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
}