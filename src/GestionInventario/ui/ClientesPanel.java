package GestionInventario.ui;

import GestionInventario.bl.entities.clientes.Cliente;
import GestionInventario.bl.entities.grafo.ResultadoRuta;
import GestionInventario.bl.entities.tienda.Tienda;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel de administración -- Gestión de clientes atendidos.
 *
 * Muestra todos los clientes que ya han sido atendidos (desencolados).
 * Estos clientes han completado su compra y su venta fue registrada.
 *
 * Funciones principales:
 *  - Tabla con: Nombre, Membresía, Prioridad #, Ubicación, Total carrito.
 *  - Botón "Ver Factura": muestra la factura detallada del cliente seleccionado.
 *  - Botón "Refrescar": recarga la tabla de clientes atendidos.
 *  - Muestra el historial de clientes procesados con sus facturas.
 */
public class ClientesPanel {

    private final Tienda tienda;
    private final JPanel mainPanel;

    // Tabla de clientes atendidos
    private javax.swing.table.DefaultTableModel tablaModel;
    private JTable tablaClientes;

    // Contador visible de clientes atendidos
    private JLabel totalAtendidosLabel;

    public ClientesPanel(Tienda tienda) {
        this.tienda = tienda;
        this.mainPanel = new JPanel(new BorderLayout(0, 10));
        initUI();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * Compatibilidad con AdminFormPanel: refresca la tabla cuando el inventario cambia.
     * AdminFormPanel llama este método después de guardar un producto.
     */
    public void recargarProductos() {
        recargarTabla();
    }

    // ----------------------------------------------------------------
    // Construcción de la interfaz
    // ----------------------------------------------------------------

    private void initUI() {
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mainPanel.add(crearCabecera(),    BorderLayout.NORTH);
        mainPanel.add(crearPanelTabla(),  BorderLayout.CENTER);
        mainPanel.add(crearPanelBotones(), BorderLayout.SOUTH);
    }

    /**
     * Cabecera con título, conteo de clientes y acciones rápidas.
     */
    private JPanel crearCabecera() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(0, 0, 12, 0)
        ));

        JLabel titulo = new JLabel("Clientes Atendidos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(new Color(44, 62, 80));

        totalAtendidosLabel = new JLabel("Atendidos: 0 clientes");
        totalAtendidosLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        totalAtendidosLabel.setForeground(new Color(100, 100, 100));

        JPanel tituloGroup = new JPanel(new BorderLayout(0, 4));
        tituloGroup.setOpaque(false);
        tituloGroup.add(titulo, BorderLayout.NORTH);
        tituloGroup.add(totalAtendidosLabel, BorderLayout.SOUTH);

        JButton refrescarBtn = EstilosUI.crearBotonRedondeado(
                "Refrescar", new Color(108, 117, 125), new Color(140, 150, 160)
        );
        refrescarBtn.setPreferredSize(new Dimension(120, 40));

        refrescarBtn.addActionListener(e -> recargarTabla());

        JPanel botonesHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botonesHeader.setOpaque(false);
        botonesHeader.add(refrescarBtn);

        header.add(tituloGroup,  BorderLayout.WEST);
        header.add(botonesHeader, BorderLayout.EAST);
        return header;
    }

    /**
     * Tabla principal con los clientes ya atendidos.
     * Columnas: Nombre | Membresía | Prioridad # | Ubicación | Total carrito
     */
    private JPanel crearPanelTabla() {
        tablaModel = new javax.swing.table.DefaultTableModel(
                new Object[]{"Nombre", "Membresía", "Prioridad #", "Ubicación", "Total carrito"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaClientes = new JTable(tablaModel);
        EstilosUI.estilizarTabla(tablaClientes);
        tablaClientes.setRowHeight(30);
        tablaClientes.setShowVerticalLines(false);
        tablaClientes.setGridColor(new Color(235, 235, 235));
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Anchos de columna sugeridos
        tablaClientes.getColumnModel().getColumn(0).setPreferredWidth(160);  // Nombre
        tablaClientes.getColumnModel().getColumn(1).setPreferredWidth(90);   // Membresía
        tablaClientes.getColumnModel().getColumn(2).setPreferredWidth(85);   // Prioridad #
        tablaClientes.getColumnModel().getColumn(3).setPreferredWidth(230);  // Ubicación
        tablaClientes.getColumnModel().getColumn(4).setPreferredWidth(110);  // Total

        JScrollPane scroll = new JScrollPane(tablaClientes);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Clientes atendidos -- historial de ventas procesadas"
        ));
        panel.add(scroll, BorderLayout.CENTER);

        recargarTabla(); // carga inicial
        return panel;
    }

    /**
     * Barra de botones de acción en la parte inferior del panel.
     * Solo contiene el botón "Ver Factura" para mostrar detalles del cliente seleccionado.
     */
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panel.setOpaque(false);

        JButton verFacturaBtn = EstilosUI.crearBotonRedondeado(
                "Ver Factura", new Color(80, 100, 155), new Color(110, 130, 185)
        );

        verFacturaBtn.setPreferredSize(new Dimension(155, 40));
        verFacturaBtn.addActionListener(e -> verFacturaClienteSeleccionado());

        // Nota explicativa
        JLabel nota = new JLabel("  Selecciona un cliente para ver su factura detallada.");
        nota.setFont(new Font("SansSerif", Font.ITALIC, 11));
        nota.setForeground(new Color(130, 130, 130));

        panel.add(verFacturaBtn);
        panel.add(nota);
        return panel;
    }

    // ----------------------------------------------------------------
    // Lógica de negocio
    // ----------------------------------------------------------------

    /**
     * Recarga la tabla con los clientes ya atendidos.
     * Muestra el historial de clientes procesados.
     */
    public void recargarTabla() {
        tablaModel.setRowCount(0);
        // Obtener clientes atendidos
        List<Cliente> clientes = tienda.getClientesAtendidos();

        for (Cliente c : clientes) {
            tablaModel.addRow(new Object[]{
                    c.getNombre(),
                    c.getTipoPrioridad(),
                    c.getPrioridad(),
                    c.getNombreUbicacion(),
                    String.format("$%.2f", c.getCarrito().calcularTotal())
            });
        }

        int total = clientes.size();
        totalAtendidosLabel.setText("Atendidos: " + total + (total == 1 ? " cliente" : " clientes"));
    }

    // ----------------------------------------------------------------
    // Modal: Ver Factura
    // ----------------------------------------------------------------

    /**
     * Genera y muestra la factura del cliente atendido seleccionado en la tabla.
     * Muestra la factura completa del cliente ya procesado.
     */
    private void verFacturaClienteSeleccionado() {
        int fila = tablaClientes.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(mainPanel,
                    "Selecciona un cliente de la tabla primero.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Cliente> clientes = tienda.getClientesAtendidos();
        if (fila >= clientes.size()) {
            // La tabla está desincronizada; refrescar
            recargarTabla();
            return;
        }

        Cliente cliente  = clientes.get(fila);
        ResultadoRuta ruta = tienda.obtenerRutaACliente(cliente);
        String factura   = tienda.generarFactura(cliente, ruta);

        mostrarModalFactura(factura, "Factura -- " + cliente.getNombre());
    }

    // ----------------------------------------------------------------
    // Modales auxiliares
    // ----------------------------------------------------------------

    /**
     * Modal que muestra el texto de una factura en fuente monoespaciada.
     *
     * @param texto  el texto generado por Tienda.generarFactura()
     * @param titulo título del modal
     */
    private void mostrarModalFactura(String texto, String titulo) {
        Window ventana = SwingUtilities.getWindowAncestor(mainPanel);
        JDialog dialog = new JDialog(ventana, titulo, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(530, 480);
        dialog.setLocationRelativeTo(mainPanel);
        dialog.setLayout(new BorderLayout(10, 10));

        JTextArea area = new JTextArea(texto);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JButton cerrarBtn = EstilosUI.crearBotonRedondeado(
                "Cerrar", new Color(120, 120, 120), new Color(160, 160, 160)
        );
        cerrarBtn.setPreferredSize(new Dimension(110, 36));
        cerrarBtn.addActionListener(e -> dialog.dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        bottom.add(cerrarBtn);

        dialog.add(scroll,  BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
