package GestionInventario.ui;

import GestionInventario.bl.entities.tienda.Tienda;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla de inicio de la aplicación.
 *
 * Ofrece dos modos de acceso:
 *  - Administrador: abre AdminWindow directamente.
 *  - Cliente manual: abre el diálogo de registro de ClienteWindow.
 *
 * Además incluye una sección de «Acceso Rápido» con usuarios de prueba
 * pregrabados para demostración inmediata sin tener que registrarse.
 */
public class HomePanel extends JFrame {

    private final Tienda tienda;

    // Componentes principales
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JButton adminButton;
    private JButton clientButton;

    // Acceso rápido: selector de usuario de prueba
    private JComboBox<String> usuariosBox;

    public HomePanel(Tienda tienda) {
        this.tienda = tienda;
        initUI();
    }

    private void initUI() {
        setupWindow();
        leftPanel  = createLeftPanel();
        rightPanel = createRightPanel();
        setupNavigation();
        assembleWindow();
    }

    private void setupWindow() {
        setTitle("Tienda de Juegos de Mesa — Inicio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 560);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(750, 480));
        setResizable(false);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Panel izquierdo (branding)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Sidebar verde con el nombre de la tienda y decoración.
     */
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

    // ─────────────────────────────────────────────────────────────────────────
    // Panel derecho (opciones de acceso)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Panel principal con:
     *  1. Título de bienvenida.
     *  2. Botón «Entrar como Administrador».
     *  3. Botón «Entrar como Cliente» (abre registro manual).
     *  4. Sección «Acceso Rápido» con usuarios de prueba pregrabados.
     */
    private JPanel createRightPanel() {
        JPanel right = new JPanel();
        right.setBackground(new Color(245, 245, 245));
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        // Título
        JLabel welcome = new JLabel("Bienvenido al sistema de inventario");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 22));
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Seleccione cómo desea ingresar");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setForeground(new Color(90, 90, 90));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Botones principales
        createNavigationButtons();

        // Separador visual entre acceso normal y acceso rápido
        JSeparator separador = new JSeparator(JSeparator.HORIZONTAL);
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separador.setForeground(new Color(200, 200, 200));
        separador.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Sección: acceso rápido con usuarios de prueba
        JPanel accesoRapidoPanel = createAccesoRapidoPanel();

        right.add(welcome);
        right.add(Box.createRigidArea(new Dimension(0, 10)));
        right.add(subtitle);
        right.add(Box.createRigidArea(new Dimension(0, 30)));
        right.add(adminButton);
        right.add(Box.createRigidArea(new Dimension(0, 14)));
        right.add(clientButton);
        right.add(Box.createRigidArea(new Dimension(0, 28)));
        right.add(separador);
        right.add(Box.createRigidArea(new Dimension(0, 18)));
        right.add(accesoRapidoPanel);
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
        adminButton.setMaximumSize(new Dimension(290, 48));

        clientButton = EstilosUI.crearBotonRedondeado(
                "Entrar como Cliente",
                new Color(130, 130, 130),
                new Color(170, 170, 170)
        );
        clientButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        clientButton.setMaximumSize(new Dimension(290, 48));
    }

    /**
     * Panel de acceso rápido:
     * Un JComboBox con los usuarios de prueba pregrabados y un botón
     * «Entrar» que abre ClienteWindow directamente sin el formulario de registro.
     */
    private JPanel createAccesoRapidoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JLabel etiqueta = new JLabel("Acceso rápido — usuario de prueba:");
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 13));
        etiqueta.setForeground(new Color(70, 70, 70));
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Construir opciones del combo: "Nombre — Tipo ($créditos)"
        String[] opciones = construirOpcionesUsuarios();
        usuariosBox = new JComboBox<>(opciones);
        usuariosBox.setUI(new EstilosUI.ComboBoxRedondeado());
        usuariosBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        usuariosBox.setMaximumSize(new Dimension(350, 36));

        JButton entrarRapidoBtn = EstilosUI.crearBotonRedondeado(
                "Entrar como este usuario",
                new Color(80, 100, 155),
                new Color(110, 130, 185)
        );
        entrarRapidoBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        entrarRapidoBtn.setMaximumSize(new Dimension(240, 42));

        entrarRapidoBtn.addActionListener(e -> {
            int idx = usuariosBox.getSelectedIndex();
            ClienteWindow.abrirConUsuario(tienda, HomePanel.this, idx);
        });

        panel.add(etiqueta);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(usuariosBox);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(entrarRapidoBtn);

        return panel;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Navegación
    // ─────────────────────────────────────────────────────────────────────────

    private void setupNavigation() {
        adminButton.addActionListener(e -> openAdminWindow());
        // Abre el diálogo de registro manual con opción de usuarios de prueba
        clientButton.addActionListener(e -> ClienteWindow.mostrarRegistroYAbrir(tienda, this));
    }

    private void openAdminWindow() {
        try {
            dispose();
            new AdminWindow(tienda).setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo abrir el panel de administración.\nDetalle: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Utilidades
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Construye las cadenas del JComboBox de usuarios de prueba,
     * leyendo los datos desde ClienteWindow.USUARIOS_PRUEBA.
     */
    private static String[] construirOpcionesUsuarios() {
        Object[][] usuarios = ClienteWindow.USUARIOS_PRUEBA;
        String[] opciones   = new String[usuarios.length];
        for (int i = 0; i < usuarios.length; i++) {
            opciones[i] = String.format("%s — %s ($%.0f)",
                    usuarios[i][0], usuarios[i][1], usuarios[i][2]);
        }
        return opciones;
    }

    private void assembleWindow() {
        setLayout(new BorderLayout());
        add(leftPanel,  BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
}
