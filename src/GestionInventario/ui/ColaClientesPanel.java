package GestionInventario.ui;

import GestionInventario.bl.entities.clientes.Cliente;
import GestionInventario.bl.entities.tienda.ResultadoAtencion;
import GestionInventario.bl.entities.tienda.Tienda;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel para el administrador que muestra la cola de clientes en espera.
 *
 * Características:
 *  - Tabla con todos los clientes en cola, ordenados por prioridad
 *    (Premium primero, luego Afiliado, luego Básico).
 *  - Botón "Atender siguiente" que procesa al cliente de mayor prioridad,
 *    registra la venta en la base de datos y muestra la factura.
 *  - Botón "Refrescar" para actualizar la tabla manualmente.
 *  - Panel inferior con la factura del último cliente atendido.
 */
public class ColaClientesPanel {

    private final Tienda tienda;
    private final JPanel mainPanel;

    // Tabla que lista a los clientes en cola
    private DefaultTableModel colaModel;
    private JTable colaTable;

    // Contador de clientes en cola
    private JLabel totalColaLabel;

    // Área donde se muestra la factura tras atender a un cliente
    private JTextArea facturaArea;

    public ColaClientesPanel(Tienda tienda) {
        this.tienda    = tienda;
        this.mainPanel = new JPanel(new BorderLayout(0, 12));
        initUI();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Construcción de la interfaz
    // ─────────────────────────────────────────────────────────────────────────

    private void initUI() {
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mainPanel.add(crearCabecera(), BorderLayout.NORTH);
        mainPanel.add(crearCuerpo(),   BorderLayout.CENTER);
        
        // Mostrar automáticamente la factura del siguiente cliente después de inicializar la UI
        SwingUtilities.invokeLater(this::mostrarFacturaSiguienteCliente);
    }

    /**
     * Encabezado con título, conteo de clientes y botones de acción.
     */
    private JPanel crearCabecera() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(0, 0, 12, 0)
        ));

        // Título principal y subetiqueta con el conteo
        JLabel titulo = new JLabel("Cola de Clientes en Espera");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(new Color(44, 62, 80));

        totalColaLabel = new JLabel("En cola: 0 clientes");
        totalColaLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        totalColaLabel.setForeground(new Color(100, 100, 100));

        JPanel tituloGroup = new JPanel(new BorderLayout(0, 4));
        tituloGroup.setOpaque(false);
        tituloGroup.add(titulo,       BorderLayout.NORTH);
        tituloGroup.add(totalColaLabel, BorderLayout.SOUTH);

        // Botones de acción en el lado derecho
        JButton refrescarBtn = EstilosUI.crearBotonRedondeado(
                "Refrescar", new Color(108, 117, 125), new Color(140, 150, 160)
        );
        JButton atenderBtn = EstilosUI.crearBotonRedondeado(
                "Atender siguiente", new Color(60, 121, 98), new Color(91, 153, 128)
        );
        refrescarBtn.setPreferredSize(new Dimension(140, 40));
        atenderBtn.setPreferredSize(new Dimension(185, 40));

        refrescarBtn.addActionListener(e -> recargarCola());
        atenderBtn.addActionListener(e -> atenderSiguienteCliente());

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botonesPanel.setOpaque(false);
        botonesPanel.add(refrescarBtn);
        botonesPanel.add(atenderBtn);

        header.add(tituloGroup,  BorderLayout.WEST);
        header.add(botonesPanel, BorderLayout.EAST);

        return header;
    }

    /**
     * Cuerpo dividido verticalmente:
     *  - Parte superior: tabla de clientes en cola.
     *  - Parte inferior: factura del último cliente atendido.
     */
    private JSplitPane crearCuerpo() {
        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                crearPanelTabla(),
                crearPanelFactura()
        );
        split.setDividerLocation(270); // altura inicial del panel de tabla
        split.setDividerSize(6);
        split.setBorder(null);
        split.setResizeWeight(0.55);   // proporción al redimensionar la ventana
        return split;
    }

    /**
     * Panel superior con la tabla de la cola.
     * Columnas: posición, nombre, membresía, artículos, total, ubicación.
     */
    private JPanel crearPanelTabla() {
        // Modelo de tabla no editable
        colaModel = new DefaultTableModel(
                new Object[]{"#", "Nombre", "Membresía", "Artículos", "Total", "Ubicación"}, 0
        ) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        colaTable = new JTable(colaModel);
        EstilosUI.estilizarTabla(colaTable);
        colaTable.setRowHeight(30);
        colaTable.setShowVerticalLines(false);
        colaTable.setGridColor(new Color(235, 235, 235));
        colaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Anchos sugeridos por columna
        colaTable.getColumnModel().getColumn(0).setPreferredWidth(35);   // #
        colaTable.getColumnModel().getColumn(1).setPreferredWidth(160);  // Nombre
        colaTable.getColumnModel().getColumn(2).setPreferredWidth(90);   // Membresía
        colaTable.getColumnModel().getColumn(3).setPreferredWidth(75);   // Artículos
        colaTable.getColumnModel().getColumn(4).setPreferredWidth(85);   // Total
        colaTable.getColumnModel().getColumn(5).setPreferredWidth(220);  // Ubicación

        JScrollPane scrollTabla = new JScrollPane(colaTable);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollTabla.getViewport().setBackground(Color.WHITE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Clientes en cola (ordenados por prioridad: Premium → Afiliado → Básico)"
        ));
        panel.add(scrollTabla, BorderLayout.CENTER);

        recargarCola(); // carga inicial
        return panel;
    }

    /**
     * Panel inferior con la factura del cliente atendido más recientemente.
     */
    private JPanel crearPanelFactura() {
        facturaArea = new JTextArea();
        facturaArea.setEditable(false);
        facturaArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        facturaArea.setText("La factura del cliente atendido aparecerá aquí.");
        facturaArea.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JScrollPane scrollFactura = new JScrollPane(facturaArea);
        scrollFactura.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollFactura.getViewport().setBackground(Color.WHITE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Vista previa - Siguiente cliente en cola"
        ));
        panel.add(scrollFactura, BorderLayout.CENTER);
        return panel;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Lógica de negocio
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Recarga la tabla con los clientes actualmente en cola, en orden de prioridad.
     * Puede llamarse también desde AdminWindow al navegar a este panel.
     */
    public void recargarCola() {
        colaModel.setRowCount(0);

        List<Cliente> clientes = tienda.getColaClientes().obtenerClientesEnOrden();
        int posicion = 1;

        for (Cliente c : clientes) {
            colaModel.addRow(new Object[]{
                    posicion++,
                    c.getNombre(),
                    c.getTipoPrioridad(),
                    c.getCarrito().contarProductos(),
                    String.format("$%.2f", c.getCarrito().calcularTotal()),
                    c.getNombreUbicacion()
            });
        }

        int total = clientes.size();
        totalColaLabel.setText("En cola: " + total + (total == 1 ? " cliente" : " clientes"));
        
        // Mostrar automáticamente la factura del siguiente cliente
        mostrarFacturaSiguienteCliente();
    }

    /**
     * Muestra la factura del siguiente cliente en la cola sin atenderlo.
     * Se llama automáticamente al abrir el panel y al recargar la cola.
     */
    private void mostrarFacturaSiguienteCliente() {
        // Verificar que facturaArea esté inicializado
        if (facturaArea == null) {
            return;
        }
        
        if (tienda.getColaClientes().estaVacia()) {
            facturaArea.setText("No hay clientes en cola para mostrar factura.");
            return;
        }

        // Obtener el siguiente cliente sin desencolarlo
        Cliente siguienteCliente = tienda.getColaClientes().verSiguiente();
        
        // Generar la factura del siguiente cliente
        String factura = tienda.generarFactura(siguienteCliente, null);
        
        // Agregar información de que es una vista previa
        String mensajeVistaPrevia = "\n" + "=".repeat(50) + "\n";
        mensajeVistaPrevia += "VISTA PREVIA - SIGUIENTE CLIENTE EN COLA\n";
        mensajeVistaPrevia += "Posición: #1 (Mayor prioridad)\n";
        mensajeVistaPrevia += "Estado: Pendiente de atención\n";
        mensajeVistaPrevia += "=".repeat(50) + "\n";
        
        facturaArea.setText(factura + mensajeVistaPrevia);
    }

    /**
     * Atiende al cliente con mayor prioridad en la cola:
     *  1. Obtiene el siguiente cliente sin desencolarlo aún.
     *  2. Valida que su ubicación esté conectada al grafo de rutas.
     *  3. Si está conectado: procede con atención normal.
     *  4. Si no está conectado: ofrece agregar conexión al grafo.
     *  5. Muestra factura y actualiza la tabla.
     */
    private void atenderSiguienteCliente() {
        if (tienda.getColaClientes().estaVacia()) {
            facturaArea.setText("No hay clientes en cola para atender.");
            return;
        }

        // Obtener el siguiente cliente sin removerlo de la cola aún
        Cliente siguienteCliente = tienda.getColaClientes().verSiguiente();
        
        // Validar que la ubicación del cliente esté conectada al grafo
        boolean ubicacionConectada = tienda.getMapaEntregas().hayRuta(
                tienda.getNombreUbicacionTienda(), 
                siguienteCliente.getNombreUbicacion()
        );

        if (!ubicacionConectada) {
            // La ubicación no está conectada - ofrecer agregar conexión
            int opcion = JOptionPane.showConfirmDialog(
                    mainPanel,
                    "El cliente '" + siguienteCliente.getNombre() + "' en ubicación '" + 
                    siguienteCliente.getNombreUbicacion() + "' no está conectado al mapa de rutas.\n\n" +
                    "¿Deseas agregar una conexión para poder atenderlo?",
                    "Ubicación desconectada",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (opcion == JOptionPane.YES_OPTION) {
                // Mostrar diálogo para agregar conexión
                mostrarDialogoAgregarConexion(siguienteCliente);
                return; // Salir para que el usuario confirme la conexión y luego reintente
            } else {
                facturaArea.setText(
                        "No se puede atender al cliente '" + siguienteCliente.getNombre() + "'.\n\n" +
                        "Motivo: Ubicación '" + siguienteCliente.getNombreUbicacion() + 
                        "' no está conectada al mapa de rutas y se rechazó agregar conexión."
                );
                return;
            }
        }

        // Si llegamos aquí, la ubicación está conectada, proceder con atención normal
        try {
            ResultadoAtencion resultado = tienda.atenderSiguienteClienteYRegistrarVenta();

            if (resultado.isAtendido()) {
                // Mostrar factura con datos del cliente y la ruta de entrega calculada
                String factura = tienda.generarFactura(resultado.getCliente(), resultado.getRuta());
                
                // Agregar información de validación de ruta
                String mensajeValidacion = "\n" + "=".repeat(50) + "\n";
                mensajeValidacion += "VALIDACIÓN DE RUTA: CONECTADA\n";
                mensajeValidacion += "La ubicación del cliente está conectada al mapa de rutas.\n";
                mensajeValidacion += "Ruta calculada exitosamente para entrega.\n";
                mensajeValidacion += "=".repeat(50) + "\n";
                
                facturaArea.setText(factura + mensajeValidacion);
                
                // Mostrar confirmación de atención exitosa
                JOptionPane.showMessageDialog(
                        mainPanel,
                        "¡Cliente atendido exitosamente!\n\n" +
                        "Cliente: " + resultado.getCliente().getNombre() + "\n" +
                        "Ubicación: " + resultado.getCliente().getNombreUbicacion() + "\n" +
                        "Ruta validada y conectada al mapa.",
                        "Atención completada",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                // El cliente existe pero no puede atenderse (ej.: ubicación desconectada)
                facturaArea.setText(
                        "No fue posible atender al cliente.\n\nMotivo: " + resultado.getMensaje()
                );
            }

            // Refrescar la tabla después de cada atención
            recargarCola();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "Error al registrar la venta en la base de datos:\n\n" + ex.getMessage(),
                    "Error de Base de Datos",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Muestra un diálogo para agregar una conexión entre la tienda y la ubicación del cliente.
     * Esto permite conectar ubicaciones desconectadas al mapa de rutas.
     * 
     * @param cliente El cliente cuya ubicación necesita ser conectada
     */
    private void mostrarDialogoAgregarConexion(Cliente cliente) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(mainPanel), 
                "Agregar Conexión al Mapa", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(mainPanel);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout(10, 10));

        // Título
        JLabel titulo = new JLabel("  Conectar ubicación con la tienda");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        titulo.setForeground(new Color(44, 62, 80));
        titulo.setBorder(BorderFactory.createEmptyBorder(12, 10, 5, 0));

        // Formulario
        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        JTextField origenField = EstilosUI.crearCampoTexto();
        JTextField destinoField = EstilosUI.crearCampoTexto();
        JTextField distanciaField = EstilosUI.crearCampoNumero();

        // Origen = tienda, Destino = ubicación del cliente
        origenField.setText(tienda.getNombreUbicacionTienda());
        origenField.setEditable(false);
        origenField.setBackground(new Color(238, 238, 238));

        destinoField.setText(cliente.getNombreUbicacion());
        destinoField.setEditable(false);
        destinoField.setBackground(new Color(238, 238, 238));

        form.add(new JLabel("Desde (Tienda):"));
        form.add(origenField);
        form.add(new JLabel("Hasta (Cliente):"));
        form.add(destinoField);
        form.add(new JLabel("Distancia (km):"));
        form.add(distanciaField);

        // Botones
        JButton agregarBtn = EstilosUI.crearBotonRedondeado("Agregar", new Color(60, 121, 98), new Color(91, 153, 128));
        JButton cancelarBtn = EstilosUI.crearBotonRedondeado("Cancelar", new Color(120, 120, 120), new Color(160, 160, 160));
        agregarBtn.setPreferredSize(new Dimension(110, 36));
        cancelarBtn.setPreferredSize(new Dimension(110, 36));

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        botonesPanel.add(cancelarBtn);
        botonesPanel.add(agregarBtn);

        dialog.add(titulo, BorderLayout.NORTH);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(botonesPanel, BorderLayout.SOUTH);

        // Acción del botón agregar
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

            // Agregar la conexión al mapa
            boolean conexionAgregada = tienda.agregarConexionAlMapa(
                    tienda.getNombreUbicacionTienda(),
                    cliente.getNombreUbicacion(),
                    distancia
            );

            dialog.dispose();

            if (conexionAgregada) {
                JOptionPane.showMessageDialog(mainPanel,
                        String.format("Conexión creada exitosamente:\n%s <-> %s (%.2f km)",
                                tienda.getNombreUbicacionTienda(),
                                cliente.getNombreUbicacion(),
                                distancia),
                        "Conexión agregada", JOptionPane.INFORMATION_MESSAGE);
                
                // Reintentar atender al cliente automáticamente
                SwingUtilities.invokeLater(() -> atenderSiguienteCliente());
            } else {
                JOptionPane.showMessageDialog(mainPanel,
                        "No se pudo agregar la conexión. Puede que ya exista.\n" +
                        "Intentando atender al cliente de todas formas...",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                
                // Reintentar igualmente
                SwingUtilities.invokeLater(() -> atenderSiguienteCliente());
            }
        });

        cancelarBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
}
