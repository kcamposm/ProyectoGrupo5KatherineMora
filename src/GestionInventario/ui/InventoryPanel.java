package GestionInventario.ui;

import GestionInventario.bl.entities.productos.Producto;
import GestionInventario.bl.entities.tienda.Tienda;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class InventoryPanel {

    private final Tienda tienda;
    private final JPanel mainPanel;
    private final DefaultTableModel model;
    private final JTable table;
    private final JLabel totalLabel;
    private final JLabel countLabel;

    public InventoryPanel(Tienda tienda) {
        this.tienda = tienda;
        this.mainPanel = new JPanel(new BorderLayout());
        this.model = new DefaultTableModel(
                new Object[]{"Nombre", "Precio", "Jugadores", "Duración", "Edad mínima", "Categoría"},
                0
        ) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        this.table = new JTable(model);
        this.totalLabel = new JLabel("Costos Totales: $0.00");
        this.countLabel = new JLabel("Total productos: 0");

        initUI();
        cargarTabla();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void initUI() {
        JPanel topPanel = createTopPanel();
        JPanel tablePanel = createTablePanel();
        JPanel bottomPanel = createBottomPanel();

        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(245, 245, 245));
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(20, 20, 15, 20)
        ));

        JLabel titleLabel = new JLabel("Inventario de Productos");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(44, 62, 80));

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        statsPanel.setOpaque(false);

        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalLabel.setForeground(new Color(60, 121, 98));

        countLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        countLabel.setForeground(new Color(100, 100, 100));

        statsPanel.add(countLabel);
        statsPanel.add(totalLabel);

        top.add(titleLabel, BorderLayout.WEST);
        top.add(statsPanel, BorderLayout.EAST);

        return top;
    }

    private JPanel createTablePanel() {
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        setupModernTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 0, 5, 0),
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1)
        ));
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableContainer.add(scrollPane, BorderLayout.CENTER);
        return tableContainer;
    }

    private void setupModernTable() {
        table.setRowHeight(34);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(210, 230, 222));
        table.setSelectionForeground(new Color(30, 60, 50));
        table.setGridColor(new Color(235, 235, 235));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setForeground(new Color(60, 121, 98));
        header.setBackground(new Color(248, 249, 250));
        header.setReorderingAllowed(false);
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(245, 245, 245));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.add(countLabel);
        summaryPanel.add(totalLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton refreshBtn = EstilosUI.crearBotonRedondeado(
                "Refrescar",
                new Color(108, 117, 125),
                new Color(140, 150, 160)
        );
        refreshBtn.setPreferredSize(new Dimension(140, 40));
        refreshBtn.addActionListener(e -> refresh());

        buttonPanel.add(refreshBtn);

        bottomPanel.add(summaryPanel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        return bottomPanel;
    }

    public void cargarTabla() {
        model.setRowCount(0);

        double total = 0;
        int count = 0;

        List<Producto> productos = tienda.getInventario().obtenerProductosEnOrden();

        for (Producto producto : productos) {
            long duracionMin = 0;
            if (producto.getDuracionJuego() != null) {
                duracionMin = producto.getDuracionJuego().toMinutes();
            }

            model.addRow(new Object[]{
                    producto.getNombre(),
                    String.format("$%.2f", producto.getPrecio()),
                    String.valueOf(producto.getCantidadJugadores()),
                    duracionMin + " min",
                    String.valueOf(producto.getEdadMinima()),
                    producto.getCategoria()
            });

            total += producto.getPrecio();
            count++;
        }

        totalLabel.setText(String.format("Costos Totales: $%.2f", total));
        countLabel.setText("Total productos: " + count);
    }

    public void refresh() {
        cargarTabla();
    }
}