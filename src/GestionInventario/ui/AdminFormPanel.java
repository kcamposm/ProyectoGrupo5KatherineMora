package GestionInventario.ui;

import GestionInventario.bl.entities.tienda.Tienda;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.Duration;

public class AdminFormPanel {

    private final Tienda tienda;
    private final InventoryPanel inventoryPanel;
    private final ClientesPanel clientesPanel;
    private final JPanel mainPanel;

    private JTextField nombreField;
    private JTextField precioField;
    private JTextField jugadoresField;
    private JTextField duracionField;
    private JTextField edadField;
    private JComboBox<String> categoriaField;

    public AdminFormPanel(Tienda tienda, InventoryPanel inventoryPanel, ClientesPanel clientesPanel) {
        this.tienda = tienda;
        this.inventoryPanel = inventoryPanel;
        this.clientesPanel = clientesPanel;
        this.mainPanel = new JPanel(new GridBagLayout());
        initUI();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void initUI() {
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        nombreField = EstilosUI.crearCampoTexto();
        precioField = EstilosUI.crearCampoTexto();
        jugadoresField = EstilosUI.crearCampoNumero();
        duracionField = EstilosUI.crearCampoNumero();
        edadField = EstilosUI.crearCampoNumero();

        categoriaField = new JComboBox<String>(new String[]{
                "Estrategia", "Cooperativo", "Fiesta", "Rol", "Guerra", "Abstracto"
        });
        categoriaField.setUI(new EstilosUI.ComboBoxRedondeado());
        categoriaField.setMaximumSize(new Dimension(300, 36));

        int y = 0;

        gbc.gridy = y++;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel("Precio ($):"), gbc);

        gbc.gridy = y++;
        gbc.gridx = 0;
        mainPanel.add(nombreField, gbc);
        gbc.gridx = 1;
        mainPanel.add(precioField, gbc);

        gbc.gridy = y++;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Cantidad de jugadores:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel("Duración (min):"), gbc);

        gbc.gridy = y++;
        gbc.gridx = 0;
        mainPanel.add(jugadoresField, gbc);
        gbc.gridx = 1;
        mainPanel.add(duracionField, gbc);

        gbc.gridy = y++;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Edad mínima:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel("Categoría:"), gbc);

        gbc.gridy = y++;
        gbc.gridx = 0;
        mainPanel.add(edadField, gbc);
        gbc.gridx = 1;
        mainPanel.add(categoriaField, gbc);

        gbc.gridy = y++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0;

        JButton guardar = EstilosUI.crearBotonRedondeado(
                "Guardar Juego",
                new Color(60, 121, 98),
                new Color(119, 187, 162)
        );
        guardar.setPreferredSize(new Dimension(180, 40));

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

        double precio;
        try {
            precio = Double.parseDouble(precioField.getText().trim().replace(",", "."));
            if (precio < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Precio inválido.", "Error", JOptionPane.WARNING_MESSAGE);
            precioField.requestFocus();
            return;
        }

        int jugadores;
        try {
            jugadores = Integer.parseInt(jugadoresField.getText().trim());
            if (jugadores <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Cantidad de jugadores inválida.", "Error", JOptionPane.WARNING_MESSAGE);
            jugadoresField.requestFocus();
            return;
        }

        long duracion;
        try {
            duracion = Long.parseLong(duracionField.getText().trim());
            if (duracion <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Duración inválida.", "Error", JOptionPane.WARNING_MESSAGE);
            duracionField.requestFocus();
            return;
        }

        int edad;
        try {
            String edadTexto = edadField.getText().trim();
            edad = edadTexto.isBlank() ? 0 : Integer.parseInt(edadTexto);
            if (edad < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Edad mínima inválida.", "Error", JOptionPane.WARNING_MESSAGE);
            edadField.requestFocus();
            return;
        }

        String categoria = (String) categoriaField.getSelectedItem();

        try {
            tienda.agregarProductoAlInventario(
                    nombre,
                    precio,
                    jugadores,
                    Duration.ofMinutes(duracion),
                    edad,
                    categoria,
                    ""
            );

            JOptionPane.showMessageDialog(mainPanel, "Producto agregado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            inventoryPanel.refresh();
            clientesPanel.recargarProductos();
            limpiarCampos();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "No se pudo guardar el producto.\nDetalle: " + ex.getMessage(),
                    "Error de Base de Datos",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void limpiarCampos() {
        nombreField.setText("");
        precioField.setText("");
        jugadoresField.setText("");
        duracionField.setText("");
        edadField.setText("");
        nombreField.requestFocus();
    }
}