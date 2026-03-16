package GestionInventario.ui;

import GestionInventario.bl.entities.clientes.Cliente;
import GestionInventario.bl.entities.productos.Producto;
import GestionInventario.bl.entities.tienda.Tienda;

import javax.swing.*;
import java.awt.*;

public class ClientesPanel {

    private final Tienda tienda;
    private final JPanel mainPanel;

    private JTextField nombreField;
    private JComboBox<String> prioridadBox;
    private JComboBox<String> productosBox;
    private JTextArea carritoArea;

    private Cliente clienteActual;

    public ClientesPanel(Tienda tienda) {
        this.tienda = tienda;
        this.mainPanel = new JPanel(new BorderLayout(10, 10));
        initUI();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void initUI() {
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Registro de clientes y carrito");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);
        titlePanel.add(title);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        nombreField = EstilosUI.crearCampoTexto();

        prioridadBox = new JComboBox<>(new String[]{
                "1 - Básico",
                "2 - Afiliado",
                "3 - Premium"
        });

        productosBox = new JComboBox<>();
        recargarProductos();

        formPanel.add(new JLabel("Nombre del cliente:"));
        formPanel.add(nombreField);
        formPanel.add(new JLabel("Prioridad:"));
        formPanel.add(prioridadBox);
        formPanel.add(new JLabel("Producto:"));
        formPanel.add(productosBox);

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton crearBtn = EstilosUI.crearBotonRedondeado(
                "Crear cliente",
                new Color(60, 121, 98),
                new Color(91, 153, 128)
        );

        JButton agregarBtn = EstilosUI.crearBotonRedondeado(
                "Agregar al carrito",
                new Color(80, 80, 140),
                new Color(110, 110, 180)
        );

        JButton encolarBtn = EstilosUI.crearBotonRedondeado(
                "Enviar a cola",
                new Color(160, 120, 60),
                new Color(190, 150, 95)
        );

        JButton limpiarBtn = EstilosUI.crearBotonRedondeado(
                "Limpiar",
                new Color(130, 130, 130),
                new Color(170, 170, 170)
        );

        crearBtn.setPreferredSize(new Dimension(150, 40));
        agregarBtn.setPreferredSize(new Dimension(170, 40));
        encolarBtn.setPreferredSize(new Dimension(150, 40));
        limpiarBtn.setPreferredSize(new Dimension(120, 40));

        botonesPanel.add(crearBtn);
        botonesPanel.add(agregarBtn);
        botonesPanel.add(encolarBtn);
        botonesPanel.add(limpiarBtn);

        carritoArea = new JTextArea();
        carritoArea.setEditable(false);
        carritoArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        carritoArea.setText("Aquí se mostrará el carrito del cliente.");

        JScrollPane scrollPane = new JScrollPane(carritoArea);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.add(titlePanel, BorderLayout.NORTH);
        topContainer.add(formPanel, BorderLayout.CENTER);

        mainPanel.add(topContainer, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(botonesPanel, BorderLayout.SOUTH);

        crearBtn.addActionListener(e -> crearCliente());
        agregarBtn.addActionListener(e -> agregarProductoCarrito());
        encolarBtn.addActionListener(e -> encolarCliente());
        limpiarBtn.addActionListener(e -> limpiarFormulario());
    }

    public void recargarProductos() {
        if (productosBox == null) {
            return;
        }

        productosBox.removeAllItems();

        for (Producto producto : tienda.getInventario().obtenerProductosEnOrden()) {
            productosBox.addItem(producto.getNombre());
        }
    }

    private void crearCliente() {
        String nombre = nombreField.getText().trim();

        if (nombre.isBlank()) {
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "Debe ingresar el nombre del cliente.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            nombreField.requestFocus();
            return;
        }

        int prioridad = prioridadBox.getSelectedIndex() + 1;
        clienteActual = new Cliente(nombre, prioridad);

        carritoArea.setText(
                "Cliente creado: " + clienteActual.getNombre() + "\n" +
                        "Prioridad: " + clienteActual.getTipoPrioridad() + "\n\n" +
                        "Carrito vacío.\n"
        );
    }

    private void agregarProductoCarrito() {
        if (clienteActual == null) {
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "Primero debe crear un cliente.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String nombreProducto = (String) productosBox.getSelectedItem();

        if (nombreProducto == null || nombreProducto.isBlank()) {
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "No hay productos disponibles en el inventario.",
                    "Inventario vacío",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        boolean agregado = tienda.agregarProductoAlCarrito(clienteActual, nombreProducto);

        if (agregado) {
            carritoArea.setText(
                    "Cliente: " + clienteActual.getNombre() + "\n" +
                            "Prioridad: " + clienteActual.getTipoPrioridad() + "\n\n" +
                            clienteActual.getCarrito().generarDetalleFactura()
            );
        } else {
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "No se pudo agregar el producto al carrito.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void encolarCliente() {
        if (clienteActual == null) {
            JOptionPane.showMessageDialog(
                    mainPanel,
                    "No hay un cliente creado para enviar a la cola.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        tienda.encolarCliente(clienteActual);

        JOptionPane.showMessageDialog(
                mainPanel,
                "Cliente enviado a la cola correctamente.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
        );

        limpiarFormulario();
    }

    private void limpiarFormulario() {
        clienteActual = null;
        nombreField.setText("");
        prioridadBox.setSelectedIndex(0);
        carritoArea.setText("Aquí se mostrará el carrito del cliente.");
        nombreField.requestFocus();
    }
}