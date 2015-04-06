/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mystore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Administrador
 */
public class ClientesProveedores extends javax.swing.JDialog {

    /**
     * Creates new form ClientesProveedores
     */
    static metodos mt = new metodos();
    static Acciones ac = new Acciones();
    static int panelindex = 0;
    public ClientesProveedores(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        cargar();
        TableCellListener tclproveedores = new TableCellListener(tbproveedores, ac.acciontablaproveedores);
        TableCellListener tclclientes = new TableCellListener(tbclientes, ac.acciontablaclientes);
        Paneles.setSelectedIndex(panelindex);
    }
    
    
    
    
    public static void cargar(){
    mt.cargartabla(tbproveedores, "select id_provee, @i := @i + 1 as '#', nombre_provee as NOMBRE, direccion AS DIRECCION, telefono AS TELEFONO, contacto_provee AS 'PERSONA DE CONTACTO' from PROVEEDOR, (select @i := 0) as id order by nombre ASC");
    mt.cargartabla(tbclientes, "select id_cliente, @i := @i + 1 as '#', nombre as NOMBRE, direccion AS DIRECCION, telefono AS TELEFONO from CLIENTE, (select @i := 0) as id order by nombre ASC");
    }
//    private void ActuProvTab() throws SQLException{
//    String SQL = "select @i := @i + 1 as '#', nombre_provee as NOMBRE, direccion AS DIRECCION, telefono AS TELEFONO, contacto_provee AS 'PERSONA DE CONTACTO' from PROVEEDOR order by nombre ASC";
//    tbproveedores.setModel(mt.Consulta(SQL));
//    }
//    
//    private void ActuClienTab() throws SQLException{
//    String SQL = "select @i := @i + 1 as '#', nombre as NOMBRE, direccion AS DIRECCION, telefono AS TELEFONO from CLIENTE order by nombre ASC";
//    tbclientes.setModel(mt.Consulta(SQL));
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Paneles = new javax.swing.JTabbedPane();
        panelproveedores = new javax.swing.JPanel();
        jButton18 = new javax.swing.JButton();
        SalirProveedores = new javax.swing.JButton();
        cmdinsertprovee = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtproveedor = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtdireccionprovee = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        txttelefonoprovee = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtcontactoprovee = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbproveedores = new javax.swing.JTable();
        panelclientes = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtcliente = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtdireccioncliente = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txttelefonocliente = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbclientes = new javax.swing.JTable();
        cmdingresoclientes = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jButton18.setText("Cancelar");

        SalirProveedores.setText("Salir");
        SalirProveedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SalirProveedoresActionPerformed(evt);
            }
        });

        cmdinsertprovee.setText("Ingresar");
        cmdinsertprovee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdinsertproveeActionPerformed(evt);
            }
        });

        jButton6.setText("Modificar");

        jButton8.setText("Eliminar");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jLabel6.setText("Nombre Proveedor:");

        jLabel1.setText("Direccion: ");

        txtdireccionprovee.setColumns(20);
        txtdireccionprovee.setRows(5);
        jScrollPane1.setViewportView(txtdireccionprovee);

        jLabel2.setText("Telefono:");

        jLabel3.setText("Persona Contacto");

        tbproveedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "NOMBRE", "DIRECCION", "TELEFONO", "CONTACTO"
            }
        ));
        jScrollPane2.setViewportView(tbproveedores);

        javax.swing.GroupLayout panelproveedoresLayout = new javax.swing.GroupLayout(panelproveedores);
        panelproveedores.setLayout(panelproveedoresLayout);
        panelproveedoresLayout.setHorizontalGroup(
            panelproveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelproveedoresLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelproveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelproveedoresLayout.createSequentialGroup()
                        .addGroup(panelproveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelproveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                            .addComponent(txtproveedor))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelproveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelproveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txttelefonoprovee)
                            .addComponent(txtcontactoprovee, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelproveedoresLayout.createSequentialGroup()
                        .addGroup(panelproveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 711, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelproveedoresLayout.createSequentialGroup()
                                .addComponent(cmdinsertprovee)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(SalirProveedores)))
                        .addGap(19, 19, 19))))
        );
        panelproveedoresLayout.setVerticalGroup(
            panelproveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelproveedoresLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(panelproveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtproveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txttelefonoprovee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(panelproveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelproveedoresLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelproveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelproveedoresLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(panelproveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtcontactoprovee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(panelproveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton18)
                    .addComponent(SalirProveedores)
                    .addComponent(cmdinsertprovee)
                    .addComponent(jButton8)
                    .addComponent(jButton6))
                .addContainerGap())
        );

        Paneles.addTab("Proveedores", panelproveedores);

        jLabel7.setText("Nombre Cliente:");

        txtdireccioncliente.setColumns(20);
        txtdireccioncliente.setRows(5);
        jScrollPane3.setViewportView(txtdireccioncliente);

        jLabel4.setText("Direccion: ");

        jLabel5.setText("Telefono:");

        tbclientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "NOMBRE", "DIRECCION", "TELEFONO"
            }
        ));
        jScrollPane4.setViewportView(tbclientes);

        cmdingresoclientes.setText("Ingresar");
        cmdingresoclientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdingresoclientesActionPerformed(evt);
            }
        });

        jButton10.setText("Modificar");

        jButton11.setText("Eliminar");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton20.setText("Cancelar");

        jButton21.setText("Salir");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelclientesLayout = new javax.swing.GroupLayout(panelclientes);
        panelclientes.setLayout(panelclientesLayout);
        panelclientesLayout.setHorizontalGroup(
            panelclientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelclientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelclientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelclientesLayout.createSequentialGroup()
                        .addGroup(panelclientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelclientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                            .addComponent(txtcliente))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 114, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txttelefonocliente, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelclientesLayout.createSequentialGroup()
                        .addGroup(panelclientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 711, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelclientesLayout.createSequentialGroup()
                                .addComponent(cmdingresoclientes)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton21)))
                        .addGap(19, 19, 19))))
        );
        panelclientesLayout.setVerticalGroup(
            panelclientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelclientesLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(panelclientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtcliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txttelefonocliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelclientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(panelclientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton20)
                    .addComponent(jButton21)
                    .addComponent(cmdingresoclientes)
                    .addComponent(jButton11)
                    .addComponent(jButton10))
                .addContainerGap())
        );

        Paneles.addTab("Clientes", panelclientes);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Paneles)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Paneles)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SalirProveedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SalirProveedoresActionPerformed
        this.dispose();
    }//GEN-LAST:event_SalirProveedoresActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton21ActionPerformed

    private void cmdinsertproveeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdinsertproveeActionPerformed
       JTextField[] texts = {txtproveedor};
        if (!mt.validartextbox(texts)) {
            JOptionPane.showMessageDialog(null, "Necesita ingresar el nombre del proveedor.");
        }else{
            mt.Insertar("insert into PROVEEDOR (nombre_provee, direccion, telefono, contacto_provee) values ('"+txtproveedor.getText()+"','"+txtdireccionprovee.getText()+"','"+txttelefonoprovee.getText()+"','"+txtcontactoprovee.getText()+"')");
            cargar();
        }                       
    }//GEN-LAST:event_cmdinsertproveeActionPerformed

    private void cmdingresoclientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdingresoclientesActionPerformed
        JTextComponent[] texts = {txtcliente};
        if (!mt.validartextbox(texts)) {
            JOptionPane.showMessageDialog(null, "Necesita ingresar el Nombre del Cliente.");
        }else{
            mt.Insertar("insert into CLIENTE (nombre, direccion, telefono) values ('"+txtcliente.getText()+"','"+txtdireccioncliente.getText()+"','"+txttelefonocliente.getText()+"')");
            cargar();
        }                   
    }//GEN-LAST:event_cmdingresoclientesActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        mt.eliminar(tbclientes, "cliente", "id_cliente");
        cargar();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        mt.eliminar(tbproveedores, "proveedor", "id_provee");
        cargar();
    }//GEN-LAST:event_jButton8ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ClientesProveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientesProveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientesProveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientesProveedores.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ClientesProveedores dialog = new ClientesProveedores(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane Paneles;
    private javax.swing.JButton SalirProveedores;
    private javax.swing.JButton cmdingresoclientes;
    private javax.swing.JButton cmdinsertprovee;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPanel panelclientes;
    private javax.swing.JPanel panelproveedores;
    private static javax.swing.JTable tbclientes;
    private static javax.swing.JTable tbproveedores;
    private javax.swing.JTextField txtcliente;
    private javax.swing.JTextField txtcontactoprovee;
    private javax.swing.JTextArea txtdireccioncliente;
    private javax.swing.JTextArea txtdireccionprovee;
    private javax.swing.JTextField txtproveedor;
    private javax.swing.JTextField txttelefonocliente;
    private javax.swing.JTextField txttelefonoprovee;
    // End of variables declaration//GEN-END:variables
}