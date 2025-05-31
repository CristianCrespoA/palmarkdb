package invpalmark;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;
import java.text.DecimalFormat;
import java.awt.Component;
import javax.swing.JTable;

public class InvView extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(InvView.class.getName());
    private final java.util.List<Producto> productos = new java.util.ArrayList<>();
    private DefaultTableModel modeloTabla;
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
        setResizable(false); 

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
    }
    
    private void agregarProducto() {
        String nombre;
        while (true) {
            nombre = JOptionPane.showInputDialog(this, "Nombre del producto:");
            if (nombre == null) return;

            nombre = nombre.trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (nombre.length() > 30) {
                JOptionPane.showMessageDialog(this, "El nombre del producto no debe superar los 30 caracteres.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        }

        int cantidad;
        while (true) {
            String cantidadStr = JOptionPane.showInputDialog(this, "Cantidad:");
            if (cantidadStr == null) return;

            try {
                cantidad = Integer.parseInt(cantidadStr.trim());
                break;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Cantidad inválida. Ingrese un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        double precio;
        while (true) {
            String precioStr = JOptionPane.showInputDialog(this, "Precio:");
            if (precioStr == null) return;

            try {
                precio = Double.parseDouble(precioStr.trim());
                break;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Precio inválido. Ingrese un número decimal.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        String codigo = String.format("%03d", codigoC++);

        Producto producto = new Producto(nombre, cantidad, precio);
        productos.add(producto);

        modeloTabla.addRow(new Object[]{codigo, nombre, cantidad, precio});
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

        bAgregar.setText("Agregar");

        bEditar.setText("Editar");

        bEliminar.setText("Eliminar");

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jLabel1.setText("SISTEMA DE INVENTARIO");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(87, 87, 87)
                        .addComponent(bAgregar)
                        .addGap(75, 75, 75)
                        .addComponent(bEditar)
                        .addGap(74, 74, 74)
                        .addComponent(bEliminar))
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(163, 163, 163)
                        .addComponent(jLabel1)
                        .addGap(151, 151, 151)))
                .addGap(32, 32, 32))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bAgregar)
                    .addComponent(bEditar)
                    .addComponent(bEliminar))
                .addContainerGap(55, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        FlatDarkLaf.setup();

        java.awt.EventQueue.invokeLater(() -> new InvView().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAgregar;
    private javax.swing.JButton bEditar;
    private javax.swing.JButton bEliminar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tProductos;
    // End of variables declaration//GEN-END:variables
}
