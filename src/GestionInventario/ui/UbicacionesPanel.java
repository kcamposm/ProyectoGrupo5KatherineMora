package GestionInventario.ui;

import GestionInventario.bl.entities.tienda.Tienda;

import javax.swing.*;
import java.awt.*;

/**
 * Panel de administración — Gestión del mapa de ubicaciones (grafo de entregas).
 *
 * El mapa se representa internamente como un grafo no dirigido ponderado.
 * Las rutas de entrega se calculan con el algoritmo de Dijkstra en el BL.
 *
 * Estado inicial: el BL ya tiene el mapa base cargado (cargarMapaBase()) con
 * las ciudades principales de Costa Rica y la ubicación de la tienda.
 *
 * Funciones de este panel:
 *  - Ver la representación actual del grafo (vértices y aristas con distancias).
 *  - Agregar una nueva ubicación (vértice) al grafo.
 *  - Agregar una conexión bidireccional (arista con distancia en km) entre dos
 *    ubicaciones ya existentes.
 */
public class UbicacionesPanel {

    private final Tienda tienda;
    private final JPanel mainPanel;

    // Área de texto donde se muestra el grafo
    private JTextArea mapaArea;

    public UbicacionesPanel(Tienda tienda) {
        this.tienda    = tienda;
        this.mainPanel = new JPanel(new BorderLayout(10, 10));
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
    }

    /**
     * Cabecera con título, descripción y botón de actualización del mapa.
     */
    private JPanel crearCabecera() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(0, 0, 12, 0)
        ));

        JLabel titulo = new JLabel("Mapa de Ubicaciones y Rutas");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(new Color(44, 62, 80));

        JLabel subtitulo = new JLabel("Grafo de entrega — rutas calculadas con Dijkstra");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(100, 100, 100));

        JPanel tituloGroup = new JPanel(new BorderLayout(0, 4));
        tituloGroup.setOpaque(false);
        tituloGroup.add(titulo,    BorderLayout.NORTH);
        tituloGroup.add(subtitulo, BorderLayout.SOUTH);

        JButton refrescarBtn = EstilosUI.crearBotonRedondeado(
                "Actualizar mapa", new Color(108, 117, 125), new Color(140, 150, 160)
        );
        refrescarBtn.setPreferredSize(new Dimension(160, 40));
        refrescarBtn.addActionListener(e -> actualizarMapa());

        JPanel botonesHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        botonesHeader.setOpaque(false);
        botonesHeader.add(refrescarBtn);

        header.add(tituloGroup,   BorderLayout.WEST);
        header.add(botonesHeader, BorderLayout.EAST);
        return header;
    }

    /**
     * Cuerpo principal dividido en dos paneles:
     *  - Izquierda: visualización del grafo.
     *  - Derecha: formularios para agregar ubicación y agregar conexión.
     */
    private JSplitPane crearCuerpo() {
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                crearPanelMapa(),
                crearPanelFormularios()
        );
        split.setDividerLocation(480);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setResizeWeight(0.6); // el mapa ocupa el 60% del espacio
        return split;
    }

    // ── Panel izquierdo: representación textual del grafo ────────────────────

    /**
     * Panel con el textarea que muestra la lista de vértices y aristas del grafo.
     * Se actualiza llamando a actualizarMapa().
     */
    private JPanel crearPanelMapa() {
        mapaArea = new JTextArea();
        mapaArea.setEditable(false);
        mapaArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        mapaArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JScrollPane scroll = new JScrollPane(mapaArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Representación del grafo (vértice → vecinos con distancia en km)"
        ));
        panel.add(scroll, BorderLayout.CENTER);

        actualizarMapa(); // carga inicial con el mapa base
        return panel;
    }

    // ── Panel derecho: formularios de gestión ────────────────────────────────

    /**
     * Panel vertical que agrupa los dos formularios: agregar ubicación y agregar conexión.
     */
    private JPanel crearPanelFormularios() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        panel.add(crearFormUbicacion());
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(crearFormConexion());
        panel.add(Box.createVerticalGlue()); // empuja los formularios hacia arriba

        return panel;
    }

    /**
     * Formulario para agregar un nuevo vértice (ubicación) al grafo.
     * Llama a Tienda.agregarUbicacionAlMapa(nombre).
     *
     * Validaciones:
     *  - Nombre no puede estar vacío.
     *  - Si la ubicación ya existe, muestra aviso.
     */
    private JPanel crearFormUbicacion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Agregar Ubicación"
        ));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(6, 8, 6, 8);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JTextField nombreField = EstilosUI.crearCampoTexto();
        JButton agregarBtn = EstilosUI.crearBotonRedondeado(
                "Agregar", new Color(60, 121, 98), new Color(91, 153, 128)
        );
        agregarBtn.setPreferredSize(new Dimension(100, 36));

        // Fila 0: label
        gbc.gridy = 0; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(new JLabel("Nombre de la ubicación:"), gbc);

        // Fila 1: campo + botón
        gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 1;
        panel.add(nombreField, gbc);
        gbc.gridx = 1; gbc.weightx = 0;
        panel.add(agregarBtn, gbc);

        agregarBtn.addActionListener(e -> {
            String nombre = nombreField.getText().trim();
            if (nombre.isBlank()) {
                JOptionPane.showMessageDialog(panel,
                        "El nombre de la ubicación no puede estar vacío.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                nombreField.requestFocus();
                return;
            }

            boolean agregada = tienda.agregarUbicacionAlMapa(nombre);
            if (agregada) {
                JOptionPane.showMessageDialog(panel,
                        "Ubicación «" + nombre + "» agregada al mapa.",
                        "Ubicación agregada", JOptionPane.INFORMATION_MESSAGE);
                nombreField.setText("");
                actualizarMapa();
            } else {
                JOptionPane.showMessageDialog(panel,
                        "La ubicación «" + nombre + "» ya existe en el mapa.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        return panel;
    }

    /**
     * Formulario para agregar una arista (conexión bidireccional con distancia)
     * entre dos vértices ya existentes en el grafo.
     * Llama a Tienda.agregarConexionAlMapa(origen, destino, distancia).
     *
     * Validaciones:
     *  - Origen y destino no pueden estar vacíos.
     *  - Distancia debe ser > 0.
     *  - Ambas ubicaciones deben existir en el grafo.
     */
    private JPanel crearFormConexion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Agregar Conexión"
        ));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(5, 8, 5, 8);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridwidth = 1;

        JTextField origenField    = EstilosUI.crearCampoTexto();
        JTextField destinoField   = EstilosUI.crearCampoTexto();
        JTextField distanciaField = EstilosUI.crearCampoNumero();

        JButton agregarBtn = EstilosUI.crearBotonRedondeado(
                "Agregar conexión", new Color(80, 100, 155), new Color(110, 130, 185)
        );
        agregarBtn.setPreferredSize(new Dimension(160, 36));

        int y = 0;
        gbc.gridy = y++; gbc.gridx = 0; panel.add(new JLabel("Origen:"),          gbc);
        gbc.gridy = y++;                 panel.add(origenField,                    gbc);
        gbc.gridy = y++;                 panel.add(new JLabel("Destino:"),         gbc);
        gbc.gridy = y++;                 panel.add(destinoField,                   gbc);
        gbc.gridy = y++;                 panel.add(new JLabel("Distancia (km):"),  gbc);
        gbc.gridy = y++;                 panel.add(distanciaField,                 gbc);

        // Botón centrado en la última fila
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill   = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(agregarBtn, gbc);

        agregarBtn.addActionListener(e -> {
            String origen  = origenField.getText().trim();
            String destino = destinoField.getText().trim();

            if (origen.isBlank() || destino.isBlank()) {
                JOptionPane.showMessageDialog(panel,
                        "Origen y destino son obligatorios.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double distancia;
            try {
                distancia = Double.parseDouble(distanciaField.getText().trim().replace(",", "."));
                if (distancia <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel,
                        "Ingresa una distancia válida mayor a 0.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                distanciaField.requestFocus();
                return;
            }

            // El BL valida que ambos nodos existan antes de agregar la arista
            boolean ok = tienda.agregarConexionAlMapa(origen, destino, distancia);
            if (ok) {
                JOptionPane.showMessageDialog(panel,
                        String.format("Conexión creada: %s ↔ %s (%.2f km)", origen, destino, distancia),
                        "Conexión agregada", JOptionPane.INFORMATION_MESSAGE);
                origenField.setText("");
                destinoField.setText("");
                distanciaField.setText("");
                actualizarMapa();
            } else {
                JOptionPane.showMessageDialog(panel,
                        "No se pudo agregar la conexión.\n\n" +
                        "Posibles causas:\n" +
                        "• Uno o ambos nodos no existen en el mapa.\n" +
                        "• La conexión ya existe entre esas ubicaciones.\n" +
                        "• Origen y destino son el mismo nodo.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Actualización del mapa
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Refresca el área de texto con la representación actual del grafo.
     * Puede llamarse desde AdminWindow al navegar a este panel.
     */
    public void actualizarMapa() {
        String representacion = tienda.obtenerRepresentacionMapa();
        mapaArea.setText(representacion);
        mapaArea.setCaretPosition(0); // scroll al inicio
    }
}
