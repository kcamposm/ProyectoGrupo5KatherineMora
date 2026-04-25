package GestionInventario.ui;

import GestionInventario.bl.entities.clientes.Cliente;
import GestionInventario.bl.entities.grafo.ResultadoRuta;
import GestionInventario.bl.entities.grafo.Ubicacion;
import GestionInventario.bl.entities.productos.Producto;
import GestionInventario.bl.entities.tienda.Tienda;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Ventana principal para el usuario cliente.
 *
 * Flujo general:
 *   1. El cliente se registra desde HomePanel (nombre + membresía) o elige
 *      un usuario de prueba pregrabado.
 *   2. Se abre esta ventana con el inventario visible y sus créditos disponibles.
 *   3. Selecciona productos de la tabla y los agrega al carrito.
 *   4. En «Mi Carrito» presiona «Proceder al pago».
 *   5. Ingresa dirección (provincia, cantón, distrito, punto de referencia).
 *   6. El sistema verifica la ruta con Dijkstra:
 *      - Si la ubicación está desconectada, ofrece agregar una conexión al grafo.
 *   7. Si hay ruta, se descuentan los créditos y el cliente se encola.
 */
public class ClienteWindow extends JFrame {

    // ── Usuarios de prueba pregrabados ───────────────────────────────────────
    // Formato: {nombre, tipo de membresía, créditos, prioridad (1/2/3)}
    // Accesible desde HomePanel para el acceso rápido
    public static final Object[][] USUARIOS_PRUEBA = {
            {"Ana García",       "Premium",  200.0, 3},
            {"Carlos López",     "Afiliado", 100.0, 2},
            {"María Rodríguez",  "Básico",    50.0, 1},
            {"Pedro Herrera",    "Premium",  200.0, 3},
            {"Laura Soto",       "Afiliado", 100.0, 2},
            {"Roberto Quesada",  "Básico",    50.0, 1},
    };

    // ── Paleta de colores ────────────────────────────────────────────────────
    private static final Color COLOR_VERDE    = new Color(60, 121, 98);
    private static final Color COLOR_VERDE_H  = new Color(91, 153, 128);
    private static final Color COLOR_SIDEBAR  = new Color(45, 90, 75);
    private static final Color COLOR_AZUL     = new Color(60, 80, 150);
    private static final Color COLOR_AZUL_H   = new Color(90, 110, 180);
    private static final Color COLOR_ROJO     = new Color(155, 60, 60);
    private static final Color COLOR_ROJO_H   = new Color(185, 90, 90);
    private static final Color COLOR_FONDO    = new Color(245, 245, 245);

    // ── Identificadores de vista (CardLayout) ────────────────────────────────
    private static final String VISTA_INVENTARIO = "inventario";
    private static final String VISTA_CARRITO    = "carrito";

    // ── Estado del cliente ───────────────────────────────────────────────────
    private final Tienda tienda;
    private Cliente clienteActual;
    private double creditos;

    // ── Componentes de cabecera ──────────────────────────────────────────────
    private JLabel creditosLabel;

    // ── Panel de contenido (CardLayout) ─────────────────────────────────────
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    // ── Componentes del panel Inventario ────────────────────────────────────
    private DefaultTableModel inventarioModel;
    private JTable inventarioTable;

    // ── Componentes del panel Carrito ─────────────────────────────────────
    private JTextArea carritoArea;
    private JLabel totalCarritoLabel;

    // ─────────────────────────────────────────────────────────────────────────
    // Constructor privado — usar mostrarRegistroYAbrir() o abrirConUsuario()
    // ─────────────────────────────────────────────────────────────────────────

    private ClienteWindow(Tienda tienda, String nombre, int prioridad, double creditos) {
        this.tienda    = tienda;
        // Se crea con ubicación provisional; se actualiza definitivamente en el checkout
        this.clienteActual = new Cliente(nombre, prioridad, "Sin definir");
        this.creditos  = creditos;
        initUI();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Puntos de entrada públicos
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Muestra el diálogo de registro manual (con selector de usuario de prueba).
     * Si el usuario confirma, cierra HomePanel y abre ClienteWindow.
     *
     * @param tienda instancia compartida de la tienda
     * @param padre  ventana padre (HomePanel) para centrar el diálogo
     */
    public static void mostrarRegistroYAbrir(Tienda tienda, JFrame padre) {
        JDialog dialog = new JDialog(padre, "Acceso como Cliente", true);
        dialog.setSize(430, 320);
        dialog.setLocationRelativeTo(padre);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setResizable(false);

        // Título
        JLabel titulo = new JLabel("  Inicia tu sesión de compra");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 15));
        titulo.setForeground(new Color(44, 62, 80));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 0));

        // ── Selector de usuarios de prueba ───────────────────────────────────
        JPanel pruebaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pruebaPanel.setOpaque(false);

        JLabel pruebaLabel = new JLabel("Usuario de prueba:");
        pruebaLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        pruebaLabel.setForeground(new Color(90, 90, 90));

        String[] opciones = construirOpcionesUsuarios();
        JComboBox<String> pruebaBox = new JComboBox<>(opciones);
        pruebaBox.setUI(new EstilosUI.ComboBoxRedondeado());
        pruebaBox.setPreferredSize(new Dimension(230, 30));

        JButton cargarBtn = EstilosUI.crearBotonRedondeado(
                "Cargar", new Color(108, 117, 125), new Color(140, 150, 160)
        );
        cargarBtn.setPreferredSize(new Dimension(80, 30));

        pruebaPanel.add(pruebaLabel);
        pruebaPanel.add(pruebaBox);
        pruebaPanel.add(cargarBtn);

        // ── Formulario manual ─────────────────────────────────────────────────
        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JTextField nombreField = EstilosUI.crearCampoTexto();
        JComboBox<String> tipoBox = new JComboBox<>(new String[]{
                "Básico      (+$50 créditos)",
                "Afiliado   (+$100 créditos)",
                "Premium  (+$200 créditos)"
        });
        tipoBox.setUI(new EstilosUI.ComboBoxRedondeado());

        form.add(new JLabel("Nombre:"));
        form.add(nombreField);
        form.add(new JLabel("Membresía:"));
        form.add(tipoBox);

        // Separador visual
        JSeparator sep = new JSeparator();
        sep.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        centro.setOpaque(false);
        //centro.add(pruebaPanel);
        centro.add(Box.createRigidArea(new Dimension(0, 8)));
        centro.add(sep);
        centro.add(Box.createRigidArea(new Dimension(0, 8)));
        centro.add(form);

        // ── Botones principales ───────────────────────────────────────────────
        JButton entrarBtn   = EstilosUI.crearBotonRedondeado("Entrar",   new Color(60, 121, 98), new Color(91, 153, 128));
        JButton cancelarBtn = EstilosUI.crearBotonRedondeado("Cancelar", new Color(120, 120, 120), new Color(160, 160, 160));
        entrarBtn.setPreferredSize(new Dimension(110, 38));
        cancelarBtn.setPreferredSize(new Dimension(110, 38));

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        botonesPanel.add(cancelarBtn);
        botonesPanel.add(entrarBtn);

        dialog.add(titulo,      BorderLayout.NORTH);
        dialog.add(centro,      BorderLayout.CENTER);
        dialog.add(botonesPanel, BorderLayout.SOUTH);

        // ── Lógica: cargar usuario de prueba ──────────────────────────────────
        cargarBtn.addActionListener(e -> {
            int idx = pruebaBox.getSelectedIndex();
            if (idx >= 0 && idx < USUARIOS_PRUEBA.length) {
                nombreField.setText((String) USUARIOS_PRUEBA[idx][0]);
                // prioridad: Premium=3→índice 2, Afiliado=2→índice 1, Básico=1→índice 0
                int prioridad = (int) USUARIOS_PRUEBA[idx][3];
                tipoBox.setSelectedIndex(prioridad - 1);
            }
        });

        final boolean[] confirmado = {false};

        entrarBtn.addActionListener(e -> {
            if (nombreField.getText().trim().isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Ingresa tu nombre.", "Validación", JOptionPane.WARNING_MESSAGE);
                nombreField.requestFocus();
                return;
            }
            confirmado[0] = true;
            dialog.dispose();
        });

        cancelarBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true); // bloquea hasta cerrar

        if (confirmado[0]) {
            String nombre    = nombreField.getText().trim();
            int    prioridad = tipoBox.getSelectedIndex() + 1;
            double creditosInicial = calcularCreditosPorPrioridad(prioridad);
            padre.dispose();
            new ClienteWindow(tienda, nombre, prioridad, creditosInicial).setVisible(true);
        }
    }

    /**
     * Abre directamente la ventana del cliente con un usuario de prueba pregrabado.
     * Llamado desde HomePanel cuando el usuario selecciona la opción de acceso rápido.
     *
     * @param tienda    instancia de la tienda
     * @param padre     HomePanel (se cierra al abrir esta ventana)
     * @param indice    índice en USUARIOS_PRUEBA
     */
    public static void abrirConUsuario(Tienda tienda, JFrame padre, int indice) {
        if (indice < 0 || indice >= USUARIOS_PRUEBA.length) return;
        String nombre    = (String) USUARIOS_PRUEBA[indice][0];
        int    prioridad = (int)   USUARIOS_PRUEBA[indice][3];
        double creditos  = (double) USUARIOS_PRUEBA[indice][2];
        padre.dispose();
        new ClienteWindow(tienda, nombre, prioridad, creditos).setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Inicialización de la interfaz
    // ─────────────────────────────────────────────────────────────────────────

    private void initUI() {
        setTitle("Tienda de Juegos — " + clienteActual.getNombre());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 560));

        setLayout(new BorderLayout());
        add(crearCabecera(),          BorderLayout.NORTH);
        add(crearSidebar(),           BorderLayout.WEST);
        add(crearContenidoPrincipal(), BorderLayout.CENTER);

        cardLayout.show(mainContentPanel, VISTA_INVENTARIO);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Cabecera
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Barra superior verde con nombre del cliente, tipo de membresía y créditos.
     * creditosLabel se actualiza tras cada compra exitosa.
     */
    private JPanel crearCabecera() {
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(COLOR_VERDE);
        cabecera.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        cabecera.setPreferredSize(new Dimension(0, 55));

        JLabel infoCliente = new JLabel(
                "  " + clienteActual.getNombre() + "  |  " + clienteActual.getTipoPrioridad()
        );
        infoCliente.setFont(new Font("SansSerif", Font.BOLD, 16));
        infoCliente.setForeground(Color.WHITE);

        creditosLabel = new JLabel(String.format("Créditos: $%.2f", creditos));
        creditosLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        creditosLabel.setForeground(new Color(200, 240, 215));

        cabecera.add(infoCliente,   BorderLayout.WEST);
        cabecera.add(creditosLabel, BorderLayout.EAST);
        return cabecera;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Sidebar de navegación
    // ─────────────────────────────────────────────────────────────────────────

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(185, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JButton inventarioBtn = EstilosUI.crearBotonRedondeado("Inventario",      COLOR_VERDE, COLOR_VERDE_H);
        JButton carritoBtn    = EstilosUI.crearBotonRedondeado("Mi Carrito",      COLOR_AZUL,  COLOR_AZUL_H);
        JButton volverBtn     = EstilosUI.crearBotonRedondeado("Volver al inicio", COLOR_ROJO, COLOR_ROJO_H);

        for (JButton btn : new JButton[]{inventarioBtn, carritoBtn, volverBtn}) {
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        sidebar.add(inventarioBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(carritoBtn);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(volverBtn);

        inventarioBtn.addActionListener(e -> {
            recargarInventario();
            cardLayout.show(mainContentPanel, VISTA_INVENTARIO);
        });

        carritoBtn.addActionListener(e -> {
            actualizarVistaCarrito();
            cardLayout.show(mainContentPanel, VISTA_CARRITO);
        });

        volverBtn.addActionListener(e -> volverAlInicio());
        return sidebar;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Panel de contenido principal (CardLayout)
    // ─────────────────────────────────────────────────────────────────────────

    private JPanel crearContenidoPrincipal() {
        cardLayout       = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.add(crearPanelInventario(), VISTA_INVENTARIO);
        mainContentPanel.add(crearPanelCarrito(),    VISTA_CARRITO);
        return mainContentPanel;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Panel: Inventario
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Tabla con todos los productos disponibles.
     * El cliente selecciona una fila y presiona «Agregar al carrito».
     */
    private JPanel crearPanelInventario() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);

        // Encabezado
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(COLOR_FONDO);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(18, 20, 14, 20)
        ));

        JLabel tituloPan = new JLabel("Inventario de Juegos de Mesa");
        tituloPan.setFont(new Font("SansSerif", Font.BOLD, 22));
        tituloPan.setForeground(new Color(44, 62, 80));

        JLabel sub = new JLabel("Selecciona un juego y presiona «Agregar al carrito»");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(new Color(120, 120, 120));

        JPanel tg = new JPanel(new BorderLayout(0, 4));
        tg.setOpaque(false);
        tg.add(tituloPan, BorderLayout.NORTH);
        tg.add(sub,       BorderLayout.SOUTH);
        topBar.add(tg, BorderLayout.WEST);

        // Tabla
        inventarioModel = new DefaultTableModel(
                new Object[]{"Nombre", "Precio", "Jugadores", "Duración", "Edad mín.", "Categoría"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return String.class; }
        };

        inventarioTable = new JTable(inventarioModel);
        EstilosUI.estilizarTabla(inventarioTable);
        inventarioTable.setRowHeight(34);
        inventarioTable.setShowVerticalLines(false);
        inventarioTable.setGridColor(new Color(235, 235, 235));
        inventarioTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(inventarioTable);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 20, 10, 20),
                BorderFactory.createLineBorder(new Color(220, 220, 220))
        ));
        scroll.getViewport().setBackground(Color.WHITE);

        // Botón agregar
        JButton agregarBtn = EstilosUI.crearBotonRedondeado(
                "Agregar al carrito", COLOR_VERDE, COLOR_VERDE_H
        );
        agregarBtn.setPreferredSize(new Dimension(200, 42));
        agregarBtn.addActionListener(e -> agregarProductoAlCarrito());

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        bottomBar.setBackground(COLOR_FONDO);
        bottomBar.add(agregarBtn);

        panel.add(topBar,    BorderLayout.NORTH);
        panel.add(scroll,    BorderLayout.CENTER);
        panel.add(bottomBar, BorderLayout.SOUTH);

        recargarInventario();
        return panel;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Panel: Carrito
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Muestra los productos en el carrito con el total y opciones de pago.
     */
    private JPanel crearPanelCarrito() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel tituloPan = new JLabel("Mi Carrito de Compras");
        tituloPan.setFont(new Font("SansSerif", Font.BOLD, 22));
        tituloPan.setForeground(new Color(44, 62, 80));

        totalCarritoLabel = new JLabel("Total: $0.00");
        totalCarritoLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalCarritoLabel.setForeground(COLOR_VERDE);

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        topRow.add(tituloPan,        BorderLayout.WEST);
        topRow.add(totalCarritoLabel, BorderLayout.EAST);

        carritoArea = new JTextArea();
        carritoArea.setEditable(false);
        carritoArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        carritoArea.setText("Tu carrito está vacío.\nAgrega productos desde el Inventario.");
        carritoArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JScrollPane scrollCarrito = new JScrollPane(carritoArea);
        scrollCarrito.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollCarrito.getViewport().setBackground(Color.WHITE);

        JButton vaciarBtn = EstilosUI.crearBotonRedondeado(
                "Vaciar carrito", new Color(120, 120, 120), new Color(160, 160, 160)
        );
        JButton pagarBtn = EstilosUI.crearBotonRedondeado(
                "Proceder al pago", COLOR_VERDE, COLOR_VERDE_H
        );
        vaciarBtn.setPreferredSize(new Dimension(155, 42));
        pagarBtn.setPreferredSize(new Dimension(195, 42));

        vaciarBtn.addActionListener(e -> vaciarCarrito());
        pagarBtn.addActionListener(e -> procesarPago());

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        botonesPanel.setOpaque(false);
        botonesPanel.add(vaciarBtn);
        botonesPanel.add(pagarBtn);

        panel.add(topRow,       BorderLayout.NORTH);
        panel.add(scrollCarrito, BorderLayout.CENTER);
        panel.add(botonesPanel,  BorderLayout.SOUTH);

        return panel;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Lógica — Inventario
    // ─────────────────────────────────────────────────────────────────────────

    private void recargarInventario() {
        inventarioModel.setRowCount(0);
        for (Producto p : tienda.getInventario().obtenerProductosEnOrden()) {
            long durMin = p.getDuracionJuego() != null ? p.getDuracionJuego().toMinutes() : 0;
            inventarioModel.addRow(new Object[]{
                    p.getNombre(),
                    String.format("$%.2f", p.getPrecio()),
                    p.getCantidadJugadores(),
                    durMin + " min",
                    p.getEdadMinima() + "+",
                    p.getCategoria()
            });
        }
    }

    private void agregarProductoAlCarrito() {
        int fila = inventarioTable.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un juego de la lista antes de agregar al carrito.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nombre = (String) inventarioModel.getValueAt(fila, 0);
        boolean ok = tienda.agregarProductoAlCarrito(clienteActual, nombre);

        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "«" + nombre + "» fue agregado al carrito.",
                    "Producto agregado", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo agregar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Lógica — Carrito
    // ─────────────────────────────────────────────────────────────────────────

    private void actualizarVistaCarrito() {
        if (clienteActual.getCarrito().contarProductos() == 0) {
            carritoArea.setText("Tu carrito está vacío.\nAgrega productos desde el Inventario.");
            totalCarritoLabel.setText("Total: $0.00");
        } else {
            carritoArea.setText(clienteActual.getCarrito().generarDetalleFactura());
            totalCarritoLabel.setText(
                    String.format("Total: $%.2f", clienteActual.getCarrito().calcularTotal())
            );
        }
    }

    /**
     * Vacía el carrito recreando el objeto cliente (ListaProductos no tiene clear()).
     * Se preserva nombre y prioridad; la ubicación se asigna en el checkout.
     */
    private void vaciarCarrito() {
        clienteActual = new Cliente(clienteActual.getNombre(), clienteActual.getPrioridad(), "Sin definir");
        actualizarVistaCarrito();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Checkout — Pago con créditos y verificación de ruta
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Punto de entrada del flujo de pago.
     * Valida que el carrito no esté vacío y que los créditos sean suficientes
     * antes de abrir el diálogo de dirección.
     */
    private void procesarPago() {
        if (clienteActual.getCarrito().contarProductos() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Tu carrito está vacío. Agrega productos antes de pagar.",
                    "Carrito vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = clienteActual.getCarrito().calcularTotal();

        if (creditos < total) {
            JOptionPane.showMessageDialog(this,
                    String.format(
                            "Créditos insuficientes.\n\n" +
                            "Total de la compra:     $%.2f\n" +
                            "Créditos disponibles:  $%.2f\n\n" +
                            "Elimina artículos del carrito o elige otro usuario.",
                            total, creditos),
                    "Fondos insuficientes", JOptionPane.ERROR_MESSAGE);
            return;
        }

        mostrarDialogoDireccion(total);
    }

    /**
     * Diálogo de dirección de entrega.
     * Campos: Provincia (combo), Cantón, Distrito (texto), Punto de referencia (área).
     *
     * Al confirmar:
     *  1. Agrega la ubicación al grafo (agregarUbicacionAlMapa).
     *  2. Verifica ruta con Dijkstra (obtenerRutaACliente).
     *  3. Si desconectada → ofrece agregar conexión.
     *  4. Descuenta créditos y encola al cliente.
     *
     * @param total monto a cobrar
     */
    private void mostrarDialogoDireccion(double total) {
        JDialog dialogo = new JDialog(this, "Dirección de entrega", true);
        dialogo.setSize(450, 420);
        dialogo.setLocationRelativeTo(this);
        dialogo.setResizable(false);
        dialogo.setLayout(new BorderLayout(10, 10));

        // ── Título del diálogo ────────────────────────────────────────────────
        JLabel titulo = new JLabel("  Indica dónde entregamos tu pedido");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 15));
        titulo.setForeground(new Color(44, 62, 80));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 0));

        // ── Campos de dirección ───────────────────────────────────────────────
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.insets  = new Insets(6, 4, 6, 4);
        gbc.weightx = 1;

        // Obtener ubicaciones dinámicas desde el grafo
        List<String> ubicaciones = tienda.obtenerUbicacionesDisponibles();
        String[] ubicacionesArray = ubicaciones.toArray(new String[0]);
        JComboBox<String> provinciaBox = new JComboBox<>(ubicacionesArray);
        provinciaBox.setUI(new EstilosUI.ComboBoxRedondeado());


        int y = 0;
        gbc.gridy = y++; formPanel.add(new JLabel("Provincia:"),             gbc);
        gbc.gridy = y++; formPanel.add(provinciaBox,                         gbc);
        gbc.gridy = y;   gbc.weighty = 1;
        gbc.fill  = GridBagConstraints.BOTH;


        // ── Resumen de pago ───────────────────────────────────────────────────
        JPanel resumenPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        resumenPanel.setBackground(new Color(240, 248, 244));
        resumenPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 5, 10)
        ));
        JLabel resumenLabel = new JLabel(String.format(
                "Total a cobrar: $%.2f   |   Créditos disponibles: $%.2f", total, creditos
        ));
        resumenLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        resumenLabel.setForeground(COLOR_VERDE);
        resumenPanel.add(resumenLabel);

        // ── Botones ──────────────────────────────────────────────────────────
        JButton confirmarBtn = EstilosUI.crearBotonRedondeado(
                "Confirmar compra", COLOR_VERDE, COLOR_VERDE_H
        );
        JButton cancelarBtn = EstilosUI.crearBotonRedondeado(
                "Cancelar", new Color(120, 120, 120), new Color(160, 160, 160)
        );
        confirmarBtn.setPreferredSize(new Dimension(175, 38));
        cancelarBtn.setPreferredSize(new Dimension(110, 38));

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        botonesPanel.setOpaque(false);
        botonesPanel.add(cancelarBtn);
        botonesPanel.add(confirmarBtn);

        JPanel footer = new JPanel(new BorderLayout());
        footer.add(resumenPanel, BorderLayout.NORTH);
        footer.add(botonesPanel, BorderLayout.SOUTH);

        dialogo.add(titulo,              BorderLayout.NORTH);
        dialogo.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        dialogo.add(footer,              BorderLayout.SOUTH);

        // ── Acción confirmar ─────────────────────────────────────────────────
        confirmarBtn.addActionListener(e -> {

            // Construir string de ubicación: "Provincia, Cantón, Distrito"
            String provincia     = (String) provinciaBox.getSelectedItem();

            // Asignar ubicación al cliente
            clienteActual.setUbicacion(new Ubicacion(provincia));

            // Paso 1: verificar ruta con Dijkstra
            ResultadoRuta ruta = tienda.obtenerRutaACliente(clienteActual);


            // Paso 2: encolar cliente (el BL también llama agregarUbicacionAlMapa internamente)
            creditos -= total;
            tienda.encolarCliente(clienteActual);

            // Actualizar label de créditos en la cabecera
            creditosLabel.setText(String.format("Créditos: $%.2f", creditos));

            dialogo.dispose();

            // Confirmación con resumen
            JOptionPane.showMessageDialog(this,
                    String.format(
                            "¡Compra realizada con éxito!\n\n" +
                            "Total cobrado:          $%.2f\n" +
                            "Créditos restantes:    $%.2f\n\n" +
                            "Pedido en cola para entrega a:\n%s",
                            total, creditos, provincia),
                    "Compra exitosa", JOptionPane.INFORMATION_MESSAGE);

            // Resetear carrito para una posible siguiente compra en la misma sesión
            clienteActual = new Cliente(
                    clienteActual.getNombre(),
                    clienteActual.getPrioridad(),
                    "Sin definir"
            );
            actualizarVistaCarrito();
        });

        cancelarBtn.addActionListener(e -> dialogo.dispose());
        dialogo.setVisible(true);
    }

    /**
     * Modal secundario para agregar una conexión al grafo desde dentro del checkout.
     * Origen prefijado = tienda, Destino prefijado = ubicación del cliente.
     *
     * @param origen  nodo de partida (tienda)
     * @param destino nodo de llegada (cliente)
     * @param padre   diálogo de checkout (para centrar este modal)
     */
    private void mostrarDialogoConexion(String origen, String destino, JDialog padre) {
        JDialog dialog = new JDialog(padre, "Agregar Conexión al Mapa", true);
        dialog.setSize(390, 255);
        dialog.setLocationRelativeTo(padre);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("  Conectar tu ubicación al mapa");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        titulo.setForeground(new Color(44, 62, 80));
        titulo.setBorder(BorderFactory.createEmptyBorder(12, 10, 5, 0));

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        JTextField origenField    = EstilosUI.crearCampoTexto();
        JTextField destinoField   = EstilosUI.crearCampoTexto();
        JTextField distanciaField = EstilosUI.crearCampoNumero();

        origenField.setText(origen);
        origenField.setEditable(false);
        origenField.setBackground(new Color(238, 238, 238));

        destinoField.setText(destino);
        destinoField.setEditable(false);
        destinoField.setBackground(new Color(238, 238, 238));

        form.add(new JLabel("Desde:"));
        form.add(origenField);
        form.add(new JLabel("Hasta:"));
        form.add(destinoField);
        form.add(new JLabel("Distancia (km):"));
        form.add(distanciaField);

        JButton agregarBtn  = EstilosUI.crearBotonRedondeado("Agregar",  COLOR_VERDE, COLOR_VERDE_H);
        JButton cancelarBtn = EstilosUI.crearBotonRedondeado("Cancelar", new Color(120, 120, 120), new Color(160, 160, 160));
        agregarBtn.setPreferredSize(new Dimension(110, 36));
        cancelarBtn.setPreferredSize(new Dimension(110, 36));

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        botonesPanel.add(cancelarBtn);
        botonesPanel.add(agregarBtn);

        dialog.add(titulo,      BorderLayout.NORTH);
        dialog.add(form,        BorderLayout.CENTER);
        dialog.add(botonesPanel, BorderLayout.SOUTH);

        agregarBtn.addActionListener(e -> {
            double distancia;
            try {
                distancia = Double.parseDouble(distanciaField.getText().trim().replace(",", "."));
                if (distancia <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Ingresa una distancia válida mayor a 0.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean ok = tienda.agregarConexionAlMapa(origen, destino, distancia);
            dialog.dispose();

            if (ok) {
                JOptionPane.showMessageDialog(padre,
                        String.format("Conexión creada: %s ↔ %s (%.2f km)", origen, destino, distancia),
                        "Conexión agregada", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(padre,
                        "No se pudo agregar la conexión. Puede que ya exista.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelarBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Utilidades
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Créditos iniciales según prioridad:
     * Premium (3) = $200, Afiliado (2) = $100, Básico (1) = $50.
     */
    private static double calcularCreditosPorPrioridad(int prioridad) {
        switch (prioridad) {
            case 3: return 200.0;
            case 2: return 100.0;
            default: return 50.0;
        }
    }

    /**
     * Construye el array de strings para el JComboBox de usuarios de prueba.
     * Formato de cada opción: "Nombre — Tipo ($créditos)"
     */
    private static String[] construirOpcionesUsuarios() {
        String[] opciones = new String[USUARIOS_PRUEBA.length];
        for (int i = 0; i < USUARIOS_PRUEBA.length; i++) {
            opciones[i] = String.format("%s — %s ($%.0f)",
                    USUARIOS_PRUEBA[i][0],
                    USUARIOS_PRUEBA[i][1],
                    USUARIOS_PRUEBA[i][2]);
        }
        return opciones;
    }

    /** Cierra esta ventana y reabre HomePanel. */
    private void volverAlInicio() {
        dispose();
        new HomePanel(tienda).setVisible(true);
    }
}
