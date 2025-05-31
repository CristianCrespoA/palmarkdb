package invpalmark;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;
import java.text.DecimalFormat;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.RowFilter;
import javax.swing.JTable;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.JPanel;
import java.awt.Image;
import java.io.*;

public class InvView extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(InvView.class.getName());
    private final java.util.List<Producto> productos = new java.util.ArrayList<>();
    private DefaultTableModel modeloTabla;
    private javax.swing.table.TableRowSorter<DefaultTableModel> sorter;
    private int codigoC = 1;
    
    private static class PrecioRenderer extends DefaultTableCellRenderer {
        private final DecimalFormat formatter = new DecimalFormat("$ #,##0.00");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Number) {
                value = formatter.format(value);
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
    
    private static class CodigoRenderer extends DefaultTableCellRenderer {
        public CodigoRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }
    }
    
    private static class CantidadRenderer extends DefaultTableCellRenderer {
        private final DecimalFormat formatter = new DecimalFormat("#,##0");
        public CantidadRenderer() {
            setHorizontalAlignment(SwingConstants.LEFT);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Number) {
                value = formatter.format(((Number) value).longValue());
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
    

    public InvView() {
        initComponents();
        setLocationRelativeTo(null);
        setResizable(false); 
        setTitle("PalmarkDB");
        
        Image icon = new ImageIcon(getClass().getResource("/img/logo.png")).getImage();
        setIconImage(icon);

        modeloTabla = new DefaultTableModel(new Object[]{"Código", "Nombre", "Cantidad", "Precio"}, 0) {
            Class[] types = new Class[]{
                String.class, String.class, Integer.class, Double.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tProductos.setModel(modeloTabla);
        
        sorter = new javax.swing.table.TableRowSorter<>(modeloTabla);
        tProductos.setRowSorter(sorter);
        
        javax.swing.table.JTableHeader header = tProductos.getTableHeader();
        java.awt.Font headerFont = header.getFont();
        header.setFont(headerFont.deriveFont(java.awt.Font.BOLD));
        
        tProductos.getColumnModel().getColumn(0).setPreferredWidth(60);
        tProductos.getColumnModel().getColumn(1).setPreferredWidth(300);
        tProductos.getColumnModel().getColumn(2).setPreferredWidth(100);
        tProductos.getColumnModel().getColumn(3).setPreferredWidth(100);
        
        tProductos.getColumnModel().getColumn(0).setCellRenderer(new CodigoRenderer());
        tProductos.getColumnModel().getColumn(2).setCellRenderer(new CantidadRenderer());
        tProductos.getColumnModel().getColumn(3).setCellRenderer(new PrecioRenderer());

        bAgregar.addActionListener(e -> agregarProducto());
        bEliminar.addActionListener(e -> eliminarProducto());
        bEditar.addActionListener(e -> editarProducto());
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcion = JOptionPane.showConfirmDialog( null, "¿Estás seguro de que quieres salir? Los datos se guardaran.", "Confirmar salida", JOptionPane.YES_NO_OPTION);

                if (opcion == JOptionPane.YES_OPTION) {
                    guardarProductos();
                    System.exit(0);
                }
            }
        });
        
        searchBar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void filtrar() {
                String texto = searchBar.getText();
                if (texto.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto.trim()));
                }
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });
        
        tProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && !evt.isConsumed()) {
                    evt.consume();
                    editarProducto();
                }
            }
        });
        
        cargarProductos();
    }
    
    private void agregarProducto() {
        String nombre;
        while (true) {
            nombre = JOptionPane.showInputDialog(this, "Nombre del producto:", "Nuevo producto", JOptionPane.QUESTION_MESSAGE);
            if (nombre == null) return;

            nombre = nombre.trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (nombre.length() > 30) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese el nombre del producto inferior a 30 carácteres.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        }

        int cantidad;
        while (true) {
            String cantidadStr = JOptionPane.showInputDialog(this, "Cantidad:", "Nuevo producto", JOptionPane.QUESTION_MESSAGE);
            if (cantidadStr == null) return;

            try {
                cantidad = Integer.parseInt(cantidadStr.trim());
                if (cantidad <= 0) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingresa un número entero para la cantidad, sin letras ni símbolos.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    break;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, ingresa un número entero para la cantidad, sin letras ni símbolos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        double precio;
        while (true) {
            String precioStr = JOptionPane.showInputDialog(this, "Precio:", "Nuevo producto", JOptionPane.QUESTION_MESSAGE);
            if (precioStr == null) return;

            try {
                precio = Double.parseDouble(precioStr.trim());
                if (precio <= 0) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingresa un número decimal para el precio.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    break;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, ingresa un número decimal para el precio.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        String codigo = String.format("%03d", codigoC++);

        Producto producto = new Producto(codigo, nombre, cantidad, precio);
        productos.add(producto);

        modeloTabla.addRow(new Object[]{codigo, nombre, cantidad, precio});
    }
    
    private void editarProducto() {
        int fila = tProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un producto para editar.", "Editar producto", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modeloFila = tProductos.convertRowIndexToModel(fila);
        Producto producto = productos.get(modeloFila);
        
        String nombre = producto.getNombre();
        String cantidad = String.valueOf(producto.getCantidad());
        String precio = String.valueOf(producto.getPrecio());
        
        while (true) {
            JTextField campoNombre = new JTextField(nombre);
            JTextField campoCantidad = new JTextField(cantidad);
            JTextField campoPrecio = new JTextField(precio);
            String campoCodigo = producto.getCodigo();
            
            JPanel panel = new JPanel(new java.awt.GridLayout(0, 1));
            panel.add(new JLabel("<html><b>Nombre:</b></html>"));
            panel.add(campoNombre);
            panel.add(new JLabel("<html><b>Cantidad:</b></html>"));
            panel.add(campoCantidad);
            panel.add(new JLabel("<html><b>Precio:</b></html>"));
            panel.add(campoPrecio);

            int opcion = JOptionPane.showConfirmDialog(this, panel, "Editar producto ["+campoCodigo+"]", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (opcion != JOptionPane.OK_OPTION) return;
            
            nombre = campoNombre.getText().trim();
            cantidad = campoCantidad.getText().trim();
            precio = campoPrecio.getText().trim();
            
            if (nombre.isEmpty() || nombre.length() > 30) {
                JOptionPane.showMessageDialog(this, "El nombre debe tener entre 1 y 30 caracteres.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            int nuevaCantidad;
            try {
                nuevaCantidad = Integer.parseInt(cantidad);
                if (nuevaCantidad <= 0) {
                    JOptionPane.showMessageDialog(this, "Cantidad debe ser un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Cantidad debe ser un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            double nuevoPrecio;
            try {
                nuevoPrecio = Double.parseDouble(precio);
                if (nuevoPrecio <= 0) {
                    JOptionPane.showMessageDialog(this, "Precio debe ser un número decimal válido.", "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Precio debe ser un número decimal válido.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            producto.setNombre(nombre);
            producto.setCantidad(nuevaCantidad);
            producto.setPrecio(nuevoPrecio);

            modeloTabla.setValueAt(nombre, modeloFila, 1);
            modeloTabla.setValueAt(nuevaCantidad, modeloFila, 2);
            modeloTabla.setValueAt(nuevoPrecio, modeloFila, 3);

            JOptionPane.showMessageDialog(this, "Producto editado correctamente.");
            break;

        }
    }
    
    private void eliminarProducto() {
        int producto = tProductos.getSelectedRow();
        if (producto == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un producto para eliminar.", "Eliminar producto", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modeloFila = tProductos.convertRowIndexToModel(producto);
        String codigoProducto = (String) modeloTabla.getValueAt(modeloFila, 0);
        String nombreProducto = (String) modeloTabla.getValueAt(modeloFila, 1);

        int opcion = JOptionPane.showConfirmDialog(this, "¿Seguro que quieres eliminar el producto ["
                + codigoProducto + "] "
                + nombreProducto
                + "?", "Eliminar producto", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            productos.remove(modeloFila);
            modeloTabla.removeRow(modeloFila);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tProductos = new javax.swing.JTable();
        bAgregar = new javax.swing.JButton();
        bEditar = new javax.swing.JButton();
        bEliminar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        logo = new javax.swing.JLabel();
        searchBar = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tProductos.setAutoCreateRowSorter(true);
        tProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Nombre", "Cantidad", "Precio"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tProductos.setToolTipText("");
        tProductos.setName(""); // NOI18N
        jScrollPane1.setViewportView(tProductos);

        bAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/agregar.png"))); // NOI18N
        bAgregar.setToolTipText("Agregar producto");

        bEditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/editar.png"))); // NOI18N
        bEditar.setToolTipText("Editar producto");
        bEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bEditarActionPerformed(evt);
            }
        });

        bEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/eliminar.png"))); // NOI18N
        bEliminar.setToolTipText("Eliminar producto");

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jLabel1.setText("SISTEMA DE INVENTARIO");

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logo.png"))); // NOI18N
        logo.setToolTipText("PalmarkDB");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/search.png"))); // NOI18N
        jLabel2.setToolTipText("Buscar productos");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(logo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(151, 151, 151))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE))
                .addGap(32, 32, 32))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(bAgregar)
                        .addGap(96, 96, 96)
                        .addComponent(bEditar)
                        .addGap(93, 93, 93)
                        .addComponent(bEliminar)
                        .addGap(149, 149, 149))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(searchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addGap(93, 93, 93))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(logo, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(searchBar))
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(bAgregar)
                    .addComponent(bEliminar)
                    .addComponent(bEditar))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bEditarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bEditarActionPerformed

    private void guardarProductos() {
    try (PrintWriter writer = new PrintWriter(new FileWriter("productos.csv"))) {
        for (Producto producto : productos) {
            writer.println(producto.getCodigo() + "," +
                           producto.getNombre() + "," +
                           producto.getCantidad() + "," +
                           producto.getPrecio());
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error al guardar productos: " + e.getMessage());
    }
}

private void cargarProductos() {
    File archivo = new File("productos.csv");
    if (!archivo.exists()) return;

    try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] partes = linea.split(",");
            if (partes.length == 4) {
                String codigo = partes[0];
                String nombre = partes[1];
                int cantidad = Integer.parseInt(partes[2]);
                double precio = Double.parseDouble(partes[3]);

                Producto producto = new Producto(codigo, nombre, cantidad, precio);
                productos.add(producto);
                modeloTabla.addRow(new Object[]{codigo, nombre, cantidad, precio});

                int codNum = Integer.parseInt(codigo);
                if (codNum >= codigoC) {
                    codigoC = codNum + 1;
                }
            }
        }
    } catch (IOException | NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage());
    }
}
    
    public static void main(String args[]) {
        FlatDarkLaf.setup();

        java.awt.EventQueue.invokeLater(() -> new InvView().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAgregar;
    private javax.swing.JButton bEditar;
    private javax.swing.JButton bEliminar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel logo;
    private javax.swing.JTextField searchBar;
    private javax.swing.JTable tProductos;
    // End of variables declaration//GEN-END:variables

}