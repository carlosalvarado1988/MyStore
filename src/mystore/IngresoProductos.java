/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mystore;

import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.ComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author Administrador
 */
public class IngresoProductos extends javax.swing.JDialog {

    /**
     * Creates new form IngresoProductos
     */
    static int[] combointMARCA;
    static int[] combointTIPO;
    static metodos mt = new metodos();
    static Acciones ac = new Acciones();

    //java.awt.Frame parent
    public IngresoProductos(javax.swing.JDialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
        cargar("");

        TableCellListener tclmarcas = new TableCellListener(tbmarcas, ac.acciontablamarcas);
        TableCellListener tcltipos = new TableCellListener(tbtipos, ac.acciontablatipos);
        TableCellListener tclproductos = new TableCellListener(tbproductos, ac.acciontablaproductos);

    }

    public static void cargar(String buscar){
        //Combos
        combointMARCA = mt.cargarcombo(cmbmarcas, "select id_marca, nombre_marca from MARCA_PROD order by nombre_marca ASC", 1, 2);
        combointTIPO = mt.cargarcombo(cmbtipos, "select id_tipo, nombre_tipo from TIPO_PROD order by nombre_tipo ASC", 1, 2);
        //Tabla marcas////////////////////////////
        mt.cargartabla(tbmarcas, "select id_marca, nombre_marca as 'LISTA DE MARCAS' from MARCA_PROD order by nombre_marca ASC");
        ////Tabla tipos/////////////////////////
        mt.cargartabla(tbtipos, "select id_tipo, nombre_tipo as 'LISTA DE TIPOS' from TIPO_PROD");
        //Tabla productos///////////////////
        int[] prodcol = {3, 4};
        ArrayList<ComboBoxModel> prodcomb = new ArrayList();
        prodcomb.add(cmbmarcas.getModel());
        prodcomb.add(cmbtipos.getModel());
        mt.cargartablacombos(tbproductos, "SELECT * FROM (select p.id_prod, p.codigo as CODIGO, p.nombre as NOMBRE, m.nombre_marca AS MARCA, t.nombre_tipo AS TIPO, p.unidad_medida AS 'U MEDIDA', p.precio_costo AS 'PRECIO COSTO', p.precio_sugerido AS 'PRECIO VENTA', p.cant_disp_prop AS 'PROPIOS DISPO', p.cant_disp_conse AS 'CONSEC DISPO' from PRODUCTOS_INVENTARIO as p, MARCA_PROD as m, TIPO_PROD as t WHERE p.fk_marca = m.id_marca AND p.fk_tipo = t.id_tipo AND p.visible = 1 AND p.nombre LIKE '%"+ buscar +"%') as query ORDER BY nombre desc", prodcol, prodcomb);
        

        //Aqui termina
    }

    private void Clear() {
        txtproducto.setText("");
        txtunidadmed.setText("");
        txtpreciocosto.setText("");
        txtpreciosugerido.setText("");
        txtcodigo.setText("");
    }
    
void InsertarProducto(){
        JTextField[] texts = {txtproducto, txtunidadmed, txtpreciocosto, txtpreciosugerido};

        if (!mt.validartextbox(texts)) {
            JOptionPane.showMessageDialog(null, "Faltan campos requeridos.");
        } else {
            Conexion mysql = new Conexion();
            Connection cn = mysql.Conectar();
            String nombre = txtproducto.getText(), unidad = txtunidadmed.getText(), codigo = txtcodigo.getText();
            double precio_cost = Double.parseDouble(txtpreciocosto.getText());
            double precio_sug = Double.parseDouble(txtpreciosugerido.getText());
            int marca = combointMARCA[cmbmarcas.getSelectedIndex()], tipo = combointTIPO[cmbtipos.getSelectedIndex()];

            String sSQL = "Insert into productos_inventario (nombre, fk_marca, fk_tipo, unidad_medida, precio_costo, precio_sugerido, cant_disp_prop, cant_disp_conse, codigo)"
                    + " values (? , ? , ? , ? , ? , ? , ?, ?, ?)";

            try {
                PreparedStatement pst = cn.prepareStatement(sSQL);
                pst.setString(1, nombre);
                pst.setInt(2, marca);
                pst.setInt(3, tipo);
                pst.setString(4, unidad);
                pst.setDouble(5, precio_cost);
                pst.setDouble(6, precio_sug);
                pst.setDouble(7, 0);
                pst.setDouble(8, 0);
                pst.setString(9, codigo);
                

                int n = pst.executeUpdate();
                if (n > 0) {

                    
                    Clear();
                } else {
                    JOptionPane.showMessageDialog(null, "No Insertado");
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
            cargar("");
        }
        }

void InsertarTipo(){
        JTextField[] texts = {txttipo};
   
        if (!mt.validartextbox(texts)) {
            JOptionPane.showMessageDialog(null, "Faltan campos requeridos.");
        } else if (mt.existe("SELECT * FROM tipo_prod WHERE nombre_tipo='" + txttipo.getText() + "'")){
            JOptionPane.showMessageDialog(null, "El nombre del tipo ya existe.");
        } else{
            mt.Insertar("insert into TIPO_PROD (nombre_tipo) values ('" + txttipo.getText() + "')");
            cargar("");
            txttipo.setText("");
        }
     }
void InsertarMarca(){
JTextField[] texts = {txtmarca};
        if (!mt.validartextbox(texts)) {
            JOptionPane.showMessageDialog(null, "Faltan campos requeridos.");
        } else if(mt.existe("SELECT * FROM marca_prod WHERE nombre_marca='"+txtmarca.getText()+"'")){
            JOptionPane.showMessageDialog(null, "El nombre de la marca ya existe.");
        }else {
            mt.Insertar("insert into MARCA_PROD (nombre_marca) values ('" + txtmarca.getText() + "')");
            cargar("");
            txtmarca.setText("");
        }
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtproducto = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtpreciosugerido = new javax.swing.JTextField();
        cmbtipos = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        cmbmarcas = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtunidadmed = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbproductos = new javax.swing.JTable();
        cmdingresarprod = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txtpreciocosto = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtbuscar = new javax.swing.JTextField();
        txtcodigo = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtmarca = new javax.swing.JTextField();
        cmdinsertarnarca = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbmarcas = new javax.swing.JTable();
        jButton6 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbtipos = new javax.swing.JTable();
        txttipo = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jPanel1KeyTyped(evt);
            }
        });

        jLabel1.setText("<html>Nombre<br> Producto:</html>");

        txtproducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtproductoActionPerformed(evt);
            }
        });
        txtproducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtproductoKeyTyped(evt);
            }
        });

        jLabel5.setText("Unidad de Medida:");

        txtpreciosugerido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtpreciosugeridoKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtpreciosugeridoKeyTyped(evt);
            }
        });

        cmbtipos.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setText("VENTA");

        cmbmarcas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbmarcas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbmarcasActionPerformed(evt);
            }
        });

        jLabel3.setText("Tipo:");

        jLabel2.setText("Marca:");

        txtunidadmed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtunidadmedActionPerformed(evt);
            }
        });

        jButton3.setText("Cancelar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton5.setText("Salir");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        tbproductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbproductos = new javax.swing.JTable() {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                if (colIndex==6 || colIndex==7) {
                    return false;
                }
                return true;
            }
        };
        jScrollPane3.setViewportView(tbproductos);

        cmdingresarprod.setText("Ingresar");
        cmdingresarprod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdingresarprodActionPerformed(evt);
            }
        });

        jButton16.setText("Eliminar");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jLabel8.setText("Precios Promedios (US$)");

        txtpreciocosto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtpreciocostoKeyTyped(evt);
            }
        });

        jLabel9.setText("COSTO");

        jLabel10.setText("BUSCAR:");

        txtbuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtbuscarActionPerformed(evt);
            }
        });
        txtbuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtbuscarKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtbuscarKeyTyped(evt);
            }
        });

        jLabel11.setText("CODIGO:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(txtproducto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtunidadmed, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtpreciocosto, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtpreciosugerido, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(20, 20, 20))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbmarcas, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbtipos, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton5)
                                .addContainerGap())
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(185, 185, 185)
                                .addComponent(jLabel8)
                                .addGap(50, 50, 50))))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cmdingresarprod)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtbuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 278, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cmbtipos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3)
                                    .addComponent(cmbmarcas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11))
                                .addGap(1, 1, 1)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel5)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtpreciosugerido, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtproducto, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtpreciocosto, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtunidadmed)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton5)
                    .addComponent(cmdingresarprod)
                    .addComponent(jButton16)
                    .addComponent(jLabel10)
                    .addComponent(txtbuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Productos", jPanel1);

        jLabel6.setText("Nombre Marca:");

        txtmarca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtmarcaKeyPressed(evt);
            }
        });

        cmdinsertarnarca.setText("Ingresar");
        cmdinsertarnarca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdinsertarnarcaActionPerformed(evt);
            }
        });

        jButton8.setText("Eliminar");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        tbmarcas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre Marca"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tbmarcas);

        jButton6.setText("Modificar");

        jButton4.setText("Cancelar");

        jButton17.setText("Salir");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(cmdinsertarnarca)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton17)
                .addGap(30, 30, 30))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(171, 171, 171)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtmarca, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(156, 156, 156)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(294, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtmarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 134, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdinsertarnarca)
                    .addComponent(jButton8)
                    .addComponent(jButton6)
                    .addComponent(jButton4)
                    .addComponent(jButton17))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Marcas", jPanel2);

        jButton18.setText("Cancelar");

        jButton19.setText("Salir");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton11.setText("Eliminar");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton13.setText("Modificar");

        jButton10.setText("Ingresar");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        tbtipos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tbtipos);

        txttipo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txttipoKeyPressed(evt);
            }
        });

        jLabel7.setText("Nombre Tipo:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(177, 177, 177)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txttipo, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jButton10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap(225, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 471, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)))
                .addComponent(jButton19)
                .addGap(44, 44, 44))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(56, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txttipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton18)
                    .addComponent(jButton19)
                    .addComponent(jButton10)
                    .addComponent(jButton11)
                    .addComponent(jButton13))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Tipos", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtunidadmedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtunidadmedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtunidadmedActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton17ActionPerformed

    private void txtpreciosugeridoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtpreciosugeridoKeyTyped
        mt.SNum(evt, txtpreciosugerido.getText());
    }//GEN-LAST:event_txtpreciosugeridoKeyTyped

    private void cmdingresarprodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdingresarprodActionPerformed
        InsertarProducto();
    }//GEN-LAST:event_cmdingresarprodActionPerformed

    private void cmdinsertarnarcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdinsertarnarcaActionPerformed
        InsertarMarca();
    }//GEN-LAST:event_cmdinsertarnarcaActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        InsertarTipo();

    }//GEN-LAST:event_jButton10ActionPerformed

    private void cmbmarcasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbmarcasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbmarcasActionPerformed

    private void txtproductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtproductoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtproductoActionPerformed

    private void txtproductoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtproductoKeyTyped
        // String valor = txtproducto.getText();
        // Cargar(valor);
    }//GEN-LAST:event_txtproductoKeyTyped

    private void txtpreciocostoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtpreciocostoKeyTyped
        mt.SNum(evt, txtpreciocosto.getText());
    }//GEN-LAST:event_txtpreciocostoKeyTyped

    private void jPanel1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel1KeyTyped
        
    }//GEN-LAST:event_jPanel1KeyTyped

    private void txtbuscarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtbuscarKeyTyped
        
    }//GEN-LAST:event_txtbuscarKeyTyped

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        mt.eliminar(tbproductos, "productos_inventario", "id_prod");
        cargar("");
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        mt.eliminar(tbmarcas, "marca_prod", "id_marca");
        cargar("");
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        mt.eliminar(tbtipos, "tipo_prod", "id_tipo");
        cargar("");
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton19ActionPerformed

    private void txtbuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtbuscarActionPerformed
    }//GEN-LAST:event_txtbuscarActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void txtbuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtbuscarKeyReleased
        if (!evt.isActionKey()){
        cargar(txtbuscar.getText());
        }
    }//GEN-LAST:event_txtbuscarKeyReleased

    private void txtpreciosugeridoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtpreciosugeridoKeyPressed
         if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            InsertarProducto();
        }
    }//GEN-LAST:event_txtpreciosugeridoKeyPressed

    private void txttipoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txttipoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            InsertarTipo();
        }
    }//GEN-LAST:event_txttipoKeyPressed

    private void txtmarcaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtmarcaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            InsertarMarca();
        }
    }//GEN-LAST:event_txtmarcaKeyPressed

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
            java.util.logging.Logger.getLogger(IngresoProductos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IngresoProductos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IngresoProductos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IngresoProductos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                IngresoProductos dialog = new IngresoProductos(new javax.swing.JDialog(), true);
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
    private static javax.swing.JComboBox cmbmarcas;
    private static javax.swing.JComboBox cmbtipos;
    private javax.swing.JButton cmdingresarprod;
    private javax.swing.JButton cmdinsertarnarca;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private static javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private static javax.swing.JTable tbmarcas;
    private static javax.swing.JTable tbproductos;
    private static javax.swing.JTable tbtipos;
    private javax.swing.JTextField txtbuscar;
    private javax.swing.JTextField txtcodigo;
    private javax.swing.JTextField txtmarca;
    private javax.swing.JTextField txtpreciocosto;
    private javax.swing.JTextField txtpreciosugerido;
    private javax.swing.JTextField txtproducto;
    private javax.swing.JTextField txttipo;
    private javax.swing.JTextField txtunidadmed;
    // End of variables declaration//GEN-END:variables
}
