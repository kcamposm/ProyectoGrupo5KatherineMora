package GestionInventario.ui;

import GestionInventario.bl.entities.tienda.Tienda;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.Duration;

public class AdminFormPanel {

    private final Tienda tienda;
    private final InventoryPanel inventoryPanel;
    private final JPanel mainPanel;

    private JTextField nombreField;
    private JTextField precioField;
    private JTextField jugadoresField;
    private JTextField duracionField;
    private JTextField edadField;
    private JComboBox<String> categoriaField;

    public AdminFormPanel(Tienda tienda, InventoryPanel inventoryPanel) {
        this.tienda = tienda;
        this.inventoryPanel = inventoryPanel;
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
        nombreField.setToolTipText("Nombre del juego de mesa");

        precioField = EstilosUI.crearCampoTexto();
        precioField.setToolTipText("Precio en dólares, por ejemplo: 45.99");

        jugadoresField = EstilosUI.crearCampoNumero();
        jugadoresField.setToolTipText("Cantidad de jugadores");

        duracionField = EstilosUI.crearCampoNumero();
        duracionField.setToolTipText("Duración en minutos");

        edadField = EstilosUI.crearCampoNumero();
        edadField.setToolTipText("Edad mínima recomendada");

        categoriaField = new JComboBox<>(new String[]{
                "Estrategia", "Cooperativo", "Fiesta", "Rol", "Guerra", "Abstracto"
        });
        categoriaField.setToolTipText("Selecciona la categoría del juego");
        categoriaField.setMaximumSize(new Dimension(300, 30));

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
        guardar.setToolTipText("Guardar nuevo juego en el inventario y en la base de datos");

        mainPanel.add(guardar, gbc);

        guardar.addActionListener(e -> procesarRegistro());
    }

    private void procesarRegistro() {
        String nombre = nombreField.getText().trim();
        if (nombre.isBlank()) {
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "El nombre es obligatorio.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE
            );
            nombreField.requestFocus();
            return;
        }

        double precio;
        try {
            String precioTexto = precioField.getText().trim().replace(",", ".");
            if (precioTexto.isEmpty()) {
                throw new NumberFormatException();
            }

            precio = Double.parseDouble(precioTexto);
            if (precio < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "Precio inválido. Escriba un número positivo, por ejemplo 40.50.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE
            );
            precioField.requestFocus();
            return;
        }

        int jugadores;
        try {
            String jugadoresTexto = jugadoresField.getText().trim();
            if (jugadoresTexto.isEmpty()) {
                throw new NumberFormatException();
            }

            jugadores = Integer.parseInt(jugadoresTexto);
            if (jugadores <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "Cantidad de jugadores inválida. Debe ser un entero mayor que 0.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE
            );
            jugadoresField.requestFocus();
            return;
        }

        long duracion;
        try {
            String duracionTexto = duracionField.getText().trim();
            if (duracionTexto.isEmpty()) {
                throw new NumberFormatException();
            }

            duracion = Long.parseLong(duracionTexto);
            if (duracion <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "Duración inválida. Debe ser un entero mayor que 0.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE
            );
            duracionField.requestFocus();
            return;
        }

        int edad;
        try {
            String edadTexto = edadField.getText().trim();
            if (edadTexto.isEmpty()) {
                edad = 0;
            } else {
                edad = Integer.parseInt(edadTexto);
                if (edad < 0) {
                    throw new NumberFormatException();
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "Edad mínima inválida. Debe ser 0 o un entero positivo.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE
            );
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

            JOptionPane.showMessageDialog(
                    mainPanel,
                    "Producto guardado correctamente en la base de datos y en el inventario.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );

            if (inventoryPanel != null) {
                inventoryPanel.refresh();
            }

            limpiarCampos();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "No se pudo guardar el producto en la base de datos.\n\nDetalle: " + ex.getMessage(),
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
        categoriaField.setSelectedIndex(0);
        nombreField.requestFocus();
    }
}