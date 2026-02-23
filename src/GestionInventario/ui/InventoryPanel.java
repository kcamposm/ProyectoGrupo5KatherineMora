package GestionInventario.ui;

import GestionInventario.bl.entities.productos.ListaProductos;
import GestionInventario.bl.entities.productos.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InventoryPanel {
    private final ListaProductos lista;
    private final JPanel mainPanel;
    private final DefaultTableModel model;
    private final JLabel totalLabel;
    private final JTable table;

    public InventoryPanel(ListaProductos lista) {
        this.lista = lista;
        this.mainPanel = new JPanel(new BorderLayout());
        this.model = new DefaultTableModel(new Object[]{"Nombre","Precio","Jugadores","Duración (min)","Edad Min","Categoría"}, 0);
        this.table = new JTable(model);
        this.totalLabel = new JLabel("Costos Totales: $0.00");
        initUI();
        cargarTabla();
    }

    public JPanel getMainPanel() { return mainPanel; }

    private void initUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        top.add(new JLabel("<html><h2>Inventario</h2></html>"), BorderLayout.WEST);
        top.add(totalLabel, BorderLayout.EAST);

        JScrollPane scroll = new JScrollPane(table);
        EstilosUI.estilizarTabla(table);

        mainPanel.add(top, BorderLayout.NORTH);
        mainPanel.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton eliminarBtn = EstilosUI.crearBotonRedondeado("Eliminar seleccionado", new Color(200,50,50), new Color(220,100,100));
        eliminarBtn.addActionListener(e -> eliminarSeleccionado());
        bottom.add(eliminarBtn);
        mainPanel.add(bottom, BorderLayout.SOUTH);
    }

    public void cargarTabla() {
        model.setRowCount(0);
        double total = 0.0;

        if (lista == null) {
            totalLabel.setText("Lista no inicializada");
            return;
        }

        Producto temp = lista.getPrimero();
        while (temp != null) {
            long durMin = temp.getDuracionJuego() != null ? temp.getDuracionJuego().toMinutes() : 0;
            model.addRow(new Object[]{
                    temp.getNombre(),
                    String.format("%.2f", temp.getPrecio()),
                    temp.getCantidadJugadores(),
                    durMin,
                    temp.getEdadMinima(),
                    temp.getCategoria()
            });
            total += temp.getPrecio();
            temp = temp.getSiguiente();
        }
        totalLabel.setText(String.format("Costos Totales: $%.2f", total));
    }

    private void eliminarSeleccionado() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(mainPanel, "Seleccione una fila para eliminar.");
            return;
        }
        String nombre = (String) model.getValueAt(row, 0);
        lista.eliminarNodo(nombre);
        cargarTabla();
    }

    // método que AdminFormPanel llamará después de insertar
    public void refresh() {
        cargarTabla();
    }
}