package GestionInventario.ui;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class EstilosUI {
    public EstilosUI() {
    }

    public static JButton crearBotonRedondeado(String texto, final Color base, final Color hover) {
        final JButton boton = new JButton(texto) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(this.getBackground());
                g2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 30, 30);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setForeground(Color.WHITE);
        boton.setPreferredSize(new Dimension(140, 40));
        boton.setCursor(Cursor.getPredefinedCursor(12));
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                boton.setBackground(hover);
            }

            public void mouseExited(MouseEvent evt) {
                boton.setBackground(base);
            }
        });
        boton.setBackground(base);
        return boton;
    }

    public static JTextField crearCampoTexto() {
        JTextField field = new JTextField(20) {
            protected void paintComponent(Graphics g) {
                if (!this.isOpaque()) {
                    Graphics2D g2 = (Graphics2D)g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(this.getBackground());
                    g2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 20, 20);
                    g2.dispose();
                }

                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        field.setMaximumSize(new Dimension(300, 30));
        return field;
    }

    public static JPasswordField crearCampoContrasena() {
        JPasswordField field = new JPasswordField(20) {
            protected void paintComponent(Graphics g) {
                if (!this.isOpaque()) {
                    Graphics2D g2 = (Graphics2D)g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(this.getBackground());
                    g2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 20, 20);
                    g2.dispose();
                }

                super.paintComponent(g);
            }
        };
        field.setOpaque(true);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        field.setMaximumSize(new Dimension(300, 30));
        return field;
    }

    public static JButton crearBotonIcono(String ruta, int ancho, int alto) {
        URL url = EstilosUI.class.getResource(ruta);
        if (url == null) {
            System.err.println("No se encontró el ícono: " + ruta);
            return new JButton("❌");
        } else {
            ImageIcon original = new ImageIcon(url);
            Image imagenEscalada = original.getImage().getScaledInstance(ancho, alto, 4);
            ImageIcon icono = new ImageIcon(imagenEscalada);
            JButton boton = new JButton(icono);
            boton.setPreferredSize(new Dimension(ancho, alto));
            boton.setMinimumSize(new Dimension(ancho, alto));
            boton.setContentAreaFilled(false);
            boton.setBorderPainted(false);
            boton.setFocusPainted(false);
            boton.setOpaque(false);
            boton.setCursor(Cursor.getPredefinedCursor(12));
            boton.setToolTipText("\ud83d\uddbc Ícono: " + ruta);
            return boton;
        }
    }

    public static JButton crearBotonConHoverIcono(String rutaBase, String rutaHover, int ancho, int alto) {
        URL urlBase = EstilosUI.class.getResource(rutaBase);
        URL urlHover = EstilosUI.class.getResource(rutaHover);
        if (urlBase != null && urlHover != null) {
            ImageIcon iconoBase = new ImageIcon(urlBase);
            ImageIcon iconoHover = new ImageIcon(urlHover);
            final Image imagenBase = iconoBase.getImage().getScaledInstance(ancho, alto, 4);
            final Image imagenHover = iconoHover.getImage().getScaledInstance(ancho, alto, 4);
            final JButton boton = new JButton(new ImageIcon(imagenBase));
            boton.setPreferredSize(new Dimension(ancho, alto));
            boton.setMinimumSize(new Dimension(ancho, alto));
            boton.setContentAreaFilled(false);
            boton.setBorderPainted(false);
            boton.setFocusPainted(false);
            boton.setOpaque(false);
            boton.setCursor(Cursor.getPredefinedCursor(12));
            boton.setAlignmentX(0.5F);
            boton.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    boton.setIcon(new ImageIcon(imagenHover));
                }

                public void mouseExited(MouseEvent e) {
                    boton.setIcon(new ImageIcon(imagenBase));
                }
            });
            return boton;
        } else {
            System.err.println("❌ No se encontró uno de los íconos:");
            System.err.println("   Base: " + rutaBase);
            System.err.println("   Hover: " + rutaHover);
            return new JButton("❌");
        }
    }

    public static ImageIcon escalarIcono(String ruta, int ancho, int alto) {
        URL url = EstilosUI.class.getResource(ruta);
        if (url == null) {
            System.err.println("❌ No se encontró el ícono: " + ruta);
            return new ImageIcon();
        } else {
            Image imagen = (new ImageIcon(url)).getImage().getScaledInstance(ancho, alto, 4);
            return new ImageIcon(imagen);
        }
    }

    public static void aplicarHoverPlano(final JButton boton) {
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setBackground(new Color(0, 0, 0, 0));
        boton.setCursor(Cursor.getPredefinedCursor(12));
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(new Color(255, 255, 255, 30));
                boton.setOpaque(true);
            }

            public void mouseExited(MouseEvent e) {
                boton.setOpaque(false);
            }

            public void mousePressed(MouseEvent e) {
                boton.setBackground(new Color(255, 255, 255, 60));
                boton.setOpaque(true);
            }

            public void mouseReleased(MouseEvent e) {
                boton.setBackground(new Color(255, 255, 255, 30));
            }
        });
    }

    public static void estilizarTabla(JTable tabla) {
        tabla.setRowHeight(28);
        tabla.setFont(new Font("SansSerif", 0, 13));
        tabla.getTableHeader().setFont(new Font("SansSerif", 1, 14));
        tabla.setGridColor(new Color(220, 220, 220));
        tabla.setShowGrid(true);
    }

    public static JTextField crearCampoNumero() {
        JTextField field = new JTextField(3) {
            protected void paintComponent(Graphics g) {
                if (!this.isOpaque()) {
                    Graphics2D g2 = (Graphics2D)g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(this.getBackground());
                    g2.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 15, 15);
                    g2.dispose();
                }

                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        field.setMaximumSize(new Dimension(150, 28));
        field.setFont(new Font("SansSerif", 0, 13));
        return field;
    }

    public static class ComboBoxRedondeado extends BasicComboBoxUI {
        public ComboBoxRedondeado() {
        }

        protected JButton createArrowButton() {
            JButton boton = new JButton("▼");
            boton.setBorder(BorderFactory.createEmptyBorder());
            boton.setContentAreaFilled(false);
            boton.setFocusPainted(false);
            boton.setForeground(Color.DARK_GRAY);
            return boton;
        }

        public void installUI(JComponent c) {
            super.installUI(c);
            this.comboBox.setBackground(Color.WHITE);
            this.comboBox.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            this.comboBox.setRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    label.setBorder(new EmptyBorder(5, 10, 5, 10));
                    return label;
                }
            });
        }

        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(this.comboBox.getBackground());
            g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 20, 20);
        }
    }
}
