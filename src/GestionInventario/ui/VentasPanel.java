package GestionInventario.ui;

import GestionInventario.bl.entities.clientes.Cliente;
import GestionInventario.bl.entities.tienda.Tienda;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class VentasPanel {

    private final Tienda tienda;
    private final ClientesPanel clientesPanel;
    private final JPanel mainPanel;
    private final JTextArea facturaArea;

    public VentasPanel(Tienda tienda, ClientesPanel clientesPanel) {
        this.tienda = tienda;
        this.clientesPanel = clientesPanel;
        this.mainPanel = new JPanel(new BorderLayout(10, 10));
        this.facturaArea = new JTextArea();
        initUI();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void initUI() {
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Ventas y facturación");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        facturaArea.setEditable(false);
        facturaArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        facturaArea.setText("Aquí se mostrará la factura del cliente atendido.");

        JButton atenderBtn = EstilosUI.crearBotonRedondeado(
                "Atender siguiente cliente",
                new Color(60, 121, 98),
                new Color(91, 153, 128)
        );
        atenderBtn.setPreferredSize(new Dimension(230, 42));

        JButton limpiarBtn = EstilosUI.crearBotonRedondeado(
                "Limpiar factura",
                new Color(130, 130, 130),
                new Color(170, 170, 170)
        );
        limpiarBtn.setPreferredSize(new Dimension(160, 42));

        atenderBtn.addActionListener(e -> atenderSiguienteCliente());
        limpiarBtn.addActionListener(e -> facturaArea.setText("Aquí se mostrará la factura del cliente atendido."));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(title);
        topPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        topPanel.add(atenderBtn);
        topPanel.add(limpiarBtn);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(facturaArea), BorderLayout.CENTER);
    }

    private void atenderSiguienteCliente() {
        try {
            Cliente cliente = tienda.atenderSiguienteClienteYRegistrarVenta();

            if (cliente == null) {
                facturaArea.setText("No hay clientes en cola.");
                return;
            }

            facturaArea.setText(tienda.generarFactura(cliente));

            if (clientesPanel != null) {
                clientesPanel.recargarProductos();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "No se pudo registrar la venta en la base de datos.\n\nDetalle: " + ex.getMessage(),
                    "Error de Base de Datos",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "Ocurrió un error al atender al cliente.\n\nDetalle: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}