package GestionInventario.ui;

import GestionInventario.bl.entities.productos.ListaProductos;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;

public class AdminFormPanel {
    private final ListaProductos lista;
    private final InventoryPanel inventoryPanel;
    private final JPanel mainPanel;

    private JTextField nombreField;
    private JTextField precioField;
    private JTextField jugadoresField;
    private JTextField duracionField;
    private JTextField edadField;
    private JComboBox<String> categoriaField;

    public AdminFormPanel(ListaProductos lista, InventoryPanel inventoryPanel) {
        this.lista = lista;
        this.inventoryPanel = inventoryPanel;
        this.mainPanel = new JPanel(new GridBagLayout());
        initUI();
    }

    public JPanel getMainPanel() { return mainPanel; }

    private void initUI() {
        mainPanel.setBackground(new Color(245,245,245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        nombreField = EstilosUI.crearCampoTexto();
        precioField = EstilosUI.crearCampoTexto();
        jugadoresField = EstilosUI.crearCampoNumero();
        duracionField = EstilosUI.crearCampoNumero();
        edadField = EstilosUI.crearCampoNumero();
        categoriaField = new JComboBox<>(new String[]{"Estrategia","Cooperativo","Fiesta","Rol","Guerra","Abstracto"});
        categoriaField.setMaximumSize(new Dimension(300,30));

        int y = 0;
        gbc.gridy = y++; gbc.gridx = 0; mainPanel.add(new JLabel("Nombre:"), gbc); gbc.gridx = 1; mainPanel.add(new JLabel("Precio:"), gbc);
        gbc.gridy = y++; gbc.gridx = 0; mainPanel.add(nombreField, gbc); gbc.gridx = 1; mainPanel.add(precioField, gbc);

        gbc.gridy = y++; gbc.gridx = 0; mainPanel.add(new JLabel("Cantidad Jugadores:"), gbc); gbc.gridx = 1; mainPanel.add(new JLabel("Duración (min):"), gbc);
        gbc.gridy = y++; gbc.gridx = 0; mainPanel.add(jugadoresField, gbc); gbc.gridx = 1; mainPanel.add(duracionField, gbc);

        gbc.gridy = y++; gbc.gridx = 0; mainPanel.add(new JLabel("Edad Minima:"), gbc); gbc.gridx = 1; mainPanel.add(new JLabel("Categoria"), gbc);
        gbc.gridy = y++; gbc.gridx = 0; mainPanel.add(edadField, gbc); gbc.gridx = 1; mainPanel.add(categoriaField, gbc);

        gbc.gridy = y++; gbc.gridx = 0; gbc.gridwidth = 2;
        JButton guardar = EstilosUI.crearBotonRedondeado("Guardar Juego", new Color(60,121,98), new Color(119,187,162));
        mainPanel.add(guardar, gbc);

        guardar.addActionListener(e -> procesarRegistro());
    }

    private void procesarRegistro() {
        String nombre = nombreField.getText().trim();
        if (nombre.isBlank()) {
            JOptionPane.showMessageDialog(mainPanel, "El nombre es obligatorio.", "Error", JOptionPane.WARNING_MESSAGE);
            nombreField.requestFocus();
            return;
        }

        String precioTxt = precioField.getText().trim().replace(",", ".");
        double precio;
        try {
            if (precioTxt.isEmpty()) throw new NumberFormatException();
            precio = Double.parseDouble(precioTxt);
            if (precio < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Precio inválido. Escriba un número positivo (ej. 40.50).", "Error", JOptionPane.WARNING_MESSAGE);
            precioField.requestFocus();
            return;
        }

        int jugadores;
        try {
            String jtxt = jugadoresField.getText().trim();
            if (jtxt.isEmpty()) throw new NumberFormatException();
            jugadores = Integer.parseInt(jtxt);
            if (jugadores <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Cantidad de jugadores inválida. Escriba un entero mayor que 0.", "Error", JOptionPane.WARNING_MESSAGE);
            jugadoresField.requestFocus();
            return;
        }

        long durMin;
        try {
            String dtxt = duracionField.getText().trim();
            if (dtxt.isEmpty()) throw new NumberFormatException();
            durMin = Long.parseLong(dtxt);
            if (durMin <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Duración inválida. Escriba minutos como número entero (ej. 50).", "Error", JOptionPane.WARNING_MESSAGE);
            duracionField.requestFocus();
            return;
        }


        int edad;
        try {
            String etxt = edadField.getText().trim();
            if (etxt.isEmpty()) {
                // Si quieres permitir vacío, asigna un valor por defecto (ej. 0)
                edad = 0;
            } else {
                edad = Integer.parseInt(etxt);
                if (edad < 0) throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Edad mínima inválida. Escriba un número entero (ej. 8).", "Error", JOptionPane.WARNING_MESSAGE);
            edadField.requestFocus();
            return;
        }

        String categoria = (String) categoriaField.getSelectedItem();
        String imagen = ""; // placeholder si no manejas imagen ahora

        // Insertar en la lista compartida (usar insertarNodoFinal para mantener orden)
        try {
            lista.insertarNodoInicio(nombre, precio, jugadores, java.time.Duration.ofMinutes(durMin), edad, categoria, imagen);
            JOptionPane.showMessageDialog(mainPanel, "Producto agregado al inventario.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // Refrescar la vista de inventario si existe
            if (inventoryPanel != null) {
                inventoryPanel.refresh();
            }

            // Limpiar campos
            nombreField.setText("");
            precioField.setText("");
            jugadoresField.setText("");
            duracionField.setText("");
            edadField.setText("");
            nombreField.requestFocus();

        } catch (Exception ex) {
            // Captura cualquier error inesperado y lo muestra
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainPanel, "Error al guardar el producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }







    }
}