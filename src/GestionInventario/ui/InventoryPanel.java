package GestionInventario.ui;

import GestionInventario.bl.entities.productos.ListaProductos;
import GestionInventario.bl.entities.productos.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class InventoryPanel {
    private final ListaProductos lista;
    private final JPanel mainPanel;
    private final DefaultTableModel model;
    private final JLabel totalLabel;
    private final JTable table;
    private final JLabel countLabel;

    public InventoryPanel(ListaProductos lista) {
        this.lista = lista;
        this.mainPanel = new JPanel(new BorderLayout());
        this.model = new DefaultTableModel(new Object[]{"Nombre","Precio","Jugadores","Duración","Edad Min","Categoría"}, 0) {
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
        this.totalLabel = new JLabel("💰 Costos Totales: $0.00");
        this.countLabel = new JLabel("🎯 Total productos: 0");
        initUI();
        cargarTabla();
    }

    public JPanel getMainPanel() { return mainPanel; }

    private void initUI() {
        // Panel superior con título y estadísticas
        JPanel topPanel = createTopPanel();
        
        // Panel central con tabla estilizada
        JPanel tablePanel = createTablePanel();
        
        // Panel inferior con botones de acción
        JPanel bottomPanel = createBottomPanel();

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

        // Título moderno
        JLabel titleLabel = new JLabel(
            "<html><div style='font-size:20px;font-weight:bold;color:#2c3e50;'>" +
            "📦 Inventario de Productos</div></html>"
        );
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Panel de estadísticas
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        statsPanel.setOpaque(false);
        
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalLabel.setForeground(new Color(60, 121, 98));
        
        countLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
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
        tableContainer.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Configurar tabla con estilo moderno
        setupModernTable();

        // Scroll pane con borde redondeado
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 0, 10, 0),
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1)
        ));
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableContainer.add(scrollPane, BorderLayout.CENTER);
        return tableContainer;
    }

    private void setupModernTable() {
        // Configuración básica
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(60, 121, 98, 50));
        table.setSelectionForeground(new Color(30, 60, 50));
        table.setGridColor(new Color(240, 240, 240));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setOpaque(false);
        table.getTableHeader().setOpaque(false);

        // Configurar encabezado
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setForeground(new Color(60, 121, 98));
        header.setBackground(new Color(248, 249, 250));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(60, 121, 98)),
            BorderFactory.createEmptyBorder(10, 5, 10, 5)
        ));

        // Renderizador personalizado para filas
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                
                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    label.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                    
                    // Alineación específica por columna
                    if (column == 1) { // Precio
                        label.setHorizontalAlignment(SwingConstants.RIGHT);
                    } else if (column == 2 || column == 3 || column == 4) { // Números
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                    } else {
                        label.setHorizontalAlignment(SwingConstants.LEFT);
                    }
                }
                
                return c;
            }
        });
    }

    private JPanel createBottomPanel() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        bottom.setBackground(new Color(245, 245, 245));
        bottom.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 20, 15, 20)
        ));

        // Botón de eliminar con estilo moderno
        JButton eliminarBtn = EstilosUI.crearBotonRedondeado(
            "🗑️ Eliminar seleccionado", 
            new Color(220, 53, 69), 
            new Color(240, 100, 100)
        );
        eliminarBtn.addActionListener(e -> eliminarSeleccionado());

        // Botón de refrescar
        JButton refrescarBtn = EstilosUI.crearBotonRedondeado(
            "🔄 Refrescar", 
            new Color(108, 117, 125), 
            new Color(150, 160, 170)
        );
        refrescarBtn.addActionListener(e -> refresh());

        bottom.add(refrescarBtn);
        bottom.add(eliminarBtn);
        
        return bottom;
    }

    public void cargarTabla() {
        model.setRowCount(0);
        double total = 0.0;
        int count = 0;

        if (lista == null) {
            totalLabel.setText("❌ Lista no inicializada");
            return;
        }

        Producto temp = lista.getPrimero();
        while (temp != null) {
            long durMin = temp.getDuracionJuego() != null ? temp.getDuracionJuego().toMinutes() : 0;
            
            // Formatear valores para mejor visualización
            String nombre = temp.getNombre();
            String precio = String.format("$%.2f", temp.getPrecio());
            String jugadores = temp.getCantidadJugadores() + " 👥";
            String duracion = durMin + " min";
            String edad = temp.getEdadMinima() + "+ años";
            String categoria = "🏷️ " + temp.getCategoria();
            
            model.addRow(new Object[]{nombre, precio, jugadores, duracion, edad, categoria});
            total += temp.getPrecio();
            count++;
            temp = temp.getSiguiente();
        }
        
        totalLabel.setText(String.format("💰 Costos Totales: $%.2f", total));
        countLabel.setText("🎯 Total productos: " + count);
    }

    private void eliminarSeleccionado() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                mainPanel, 
                "⚠️ Por favor, seleccione un producto para eliminar.",
                "Selección requerida",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        String nombre = (String) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(
            mainPanel,
            "¿Está seguro que desea eliminar \"" + nombre + "\" del inventario?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            lista.eliminarNodo(nombre);
            cargarTabla();
            JOptionPane.showMessageDialog(
                mainPanel,
                "✅ Producto eliminado correctamente",
                "Eliminación completada",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    public void refresh() {
        cargarTabla();
    }
}
