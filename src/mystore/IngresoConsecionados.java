/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mystore;

import com.mxrck.autocompleter.TextAutoCompleter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 *
 * @author Administrador
 */
public class IngresoConsecionados extends javax.swing.JDialog {

    /**
     * Creates new form IngresoConsecionados
     */
     static int[] combointPROVEE;
     static metodos me = new metodos();
    static Acciones ac = new Acciones();
    
    public IngresoConsecionados(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        cargar();   
        me.Autocomplet(txtproductosconse, "nombre");
        me.Autocomplet(txtcodigo,"codigo");
        TableCellListener tclcomprasconsecionado = new TableCellListener(tbcomprasconsecionado, ac.acciontablaconsecionados);
    }
        
        private void cargar() {
        Conexion mysql = new Conexion();
        Date date = new Date();
        datecompraconse.setDate(date);
            try {
            ResultSet rs = mysql.select("select id_concesion from ingreso_prod_concesionado where estado = 'PROCESO'");
            if (rs.next() == true) {
                String id_en_proceso = rs.getString("id_concesion");
                    labelID.setText(id_en_proceso);
                    labelestado.setText("EN PROCESO");
                    combointPROVEE = me.cargarcombo(cmbprovee, "select id_provee, nombre_provee from PROVEEDOR order by nombre_provee ASC", 1, 2);
                    CargarTabla();
                    
            } else {
            
            combointPROVEE = me.cargarcombo(cmbprovee, "select id_provee, nombre_provee from PROVEEDOR order by nombre_provee ASC", 1, 2);
            me.Insertar("insert into ingreso_prod_concesionado (estado) values ('PROCESO')");
                ResultSet rss = mysql.select("select id_concesion from ingreso_prod_concesionado where estado = 'PROCESO'");
                rss.next();
                String id = rss.getString("id_concesion");
                labelID.setText(id);
                labelestado.setText("EN PROCESO");
                CargarTabla();
            
            }
        } catch (SQLException ex) {
            
            JOptionPane.showMessageDialog(null, "Error capturando compra en Proceso, contacte Administrador"+ex);
            Logger.getLogger(Compras.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

     private void clear(){
    txtproductosconse.setText("");
    txtcantidadcompra.setText("");
    txtpreciocosto.setText("");
    txtvalortotalcompra.setText("");
    txtcodigo.setText("");

    }
    private void Disables(){
    labelestado.setEnabled(false);
    btncancelarregistro.setEnabled(false);
    datecompraconse.setEnabled(false);
    cmbprovee.setEnabled(false);
    txtproductosconse.setEnabled(false);
    btnnuevoproducto.setEnabled(false);
    txtcantidadcompra.setEnabled(false);
    txtpreciocosto.setEnabled(false);
    txtvalortotalcompra.setEnabled(false);
    btnadicionar.setEnabled(false);
    btneliminar.setEnabled(false);
    cmdregistrarcompra.setEnabled(false);
    txtmonto.setEnabled(false);
    txtfactura.setEnabled(false);
   
    btnSalirCompras.setEnabled(true);
    }
    
  /*  private void Enables(){
    labelestado.setEnabled(true);
    btncancelarregistro.setEnabled(true);
    datecompraconse.setEnabled(true);
    cmbprovee.setEnabled(true);
    txtproductosconse.setEnabled(true);
    btnnuevoproducto.setEnabled(true); 
    txtcantidadcompra.setEnabled(true);
    txtpreciocosto.setEnabled(true);
    txtvalortotalcompra.setEnabled(true);
    btnadicionar.setEnabled(true);
    btneliminar.setEnabled(true);
    cmdregistrarcompra.setEnabled(true);
    txtmonto.setEnabled(true);
    txtfactura.setEnabled(true);
    
    btnSalirCompras.setEnabled(false);
    }*/
    
    
    public void actuInventario(){
        try {
            Conexion mysql = new Conexion();
            ResultSet rs = mysql.select("SELECT fk_producto, Sum( cantidad ) as cant FROM ingreso_prod_cons_deta WHERE fk_consecion = "+labelID.getText()+" GROUP BY fk_producto");
            while(rs.next()){
                String id_prod = rs.getString("fk_producto"); 
                String cantidad = rs.getString("cant"); 
            me.Insertar("Update productos_inventario set cant_disp_conse = cant_disp_conse + "+cantidad+" where id_prod = "+id_prod);               
            }
        } catch (SQLException ex) {
           JOptionPane.showMessageDialog(null, "Error actualizando inventario, contacte Administrador"+ex);
            Logger.getLogger(VentaDiaria.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
    
    
    /*private void PrecioCompra(){
        try {
            String prod = txtproductosconse.getText();
                     Conexion mysql = new Conexion();
                     ResultSet rs = mysql.select("select precio_costo from productos_inventario where nombre = '"+prod+"'");
                      if (rs.next()==true){
                     String pc = rs.getString("precio_costo");
                     txtpreciocosto.setText(pc);
                      }else{
                     txtpreciocosto.setText("0");
                     }
                     
                     
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Necesita ingresar el Nombre del Producto"+ex);
        }
   }*/
    
    
    private void Monto(){
    try {       
        Conexion cn = new Conexion();
        String idenca = labelID.getText();
        String mont = "select ROUND(SUM(precio_total),2) as monto FROM ingreso_prod_cons_deta where fk_consecion = " +idenca;
        ResultSet rs = cn.select(mont);
        rs.next();
        txtmonto.setText(rs.getString("monto"));
     } catch (SQLException ex) {
            Logger.getLogger(Compras.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void ValorTotal() {
      
        Double Cant = Double.parseDouble(txtcantidadcompra.getText());
        Double PU = Double.parseDouble(txtpreciocosto.getText());
        Double VT = Cant * PU;
        txtvalortotalcompra.setText(Double.toString(me.Redondear(VT)));
    
    }
    
  void RegistrarCompra_encaCompra() {
        String fechacompra, factura, monto, idenca;
        //int idenca = Integer.parseInt(labelID.getText());
        fechacompra = me.FechaFormat(datecompraconse.getDate()); // fecha 
        int provee = combointPROVEE[cmbprovee.getSelectedIndex()]; // proveedor
        monto = txtmonto.getText(); //monto
        factura = txtfactura.getText();
        idenca = labelID.getText();

        String updateENCAsql = "update INGRESO_PROD_CONCESIONADO set fk_proveedor = "+provee+", fecha = '"+fechacompra+"', monto = "+monto
                                + ", num_factura = '"+factura+"', estado = 'REGISTRADA' where id_consecion ="+idenca;
        me.Insertar(updateENCAsql);
        JOptionPane.showMessageDialog(null, "Compra ID: "+idenca+" fue Registrada");
        labelestado.setText("REGISTRADA");
        Disables();
      
    } 
    
    
    void InsertarDeta() { 
        if (txtproductosconse.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Necesita ingresar el Nombre del Producto");
        }else{
        
        try {
            Conexion mysql = new Conexion();
            Connection cn = mysql.Conectar();
            String prod = txtproductosconse.getText();
            ResultSet rs = mysql.select("select id_prod from productos_inventario where nombre = '"+prod+"'");
            rs.next();
            int idprod = rs.getInt("id_prod");
            double cant, preUni, preTot;
            cant = Double.parseDouble(txtcantidadcompra.getText());
            preUni = Double.parseDouble(txtpreciocosto.getText());
            preTot = Double.parseDouble(txtvalortotalcompra.getText());
            int idenca = Integer.parseInt(labelID.getText());
            

            String sSQL = "INSERT INTO INGRESO_PROD_CONS_DETA (fk_producto, cantidad, precio_unitario, precio_total, fk_consecion)"
                    + "VALUES(? , ? , ? , ? , ? )";

            try {
                PreparedStatement pst = cn.prepareStatement(sSQL);
                pst.setInt(1, idprod);
                pst.setDouble(2, cant);
                pst.setDouble(3, preUni);
                pst.setDouble(4, preTot);
                pst.setInt(5, idenca);

                int n = pst.executeUpdate();
                if (n > 0) {
                  //JOptionPane.showMessageDialog(null, "insertado");
                   clear();
                  CargarTabla();
                } else {
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "error insertando compra sql"+ex);
            }
    
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "error extraendo id de productos");
            Logger.getLogger(Compras.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
}
        
private void CargarTabla() {
          String id_enca = labelID.getText();
    me.cargartabla(tbcomprasconsecionado, "select c.id_cons_deta,  @i := @i + 1 as '#', p.codigo as CODIGO, p.nombre as PRODUCTO,  m.nombre_marca as MARCA,  c.precio_unitario as 'PRECIO UNITARIO', c.cantidad as CANTIDAD, ROUND((precio_unitario * cantidad),2) as TOTAL  from ingreso_prod_cons_deta as c, productos_inventario as p, marca_prod as m, (select @i := 0) as id where c.fk_producto = p.id_prod AND p.fk_marca = m.id_marca AND fk_consecion ="+id_enca);
        tbcomprasconsecionado.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if(JOptionPane.showConfirmDialog(null, "¿Desea modificar el registro?" , "Confirmar", JOptionPane.YES_NO_OPTION)==0){
                me.Insertar("UPDATE ingreso_prod_cons_deta SET cantidad='" + tbcomprasconsecionado.getValueAt(e.getFirstRow(), 5) + "', precio_unitario ='" + tbcomprasconsecionado.getValueAt(e.getFirstRow(), 4) + "' WHERE id_compra_deta=" + tbcomprasconsecionado.getModel().getValueAt(e.getFirstRow(), 0));
                }
                
                Monto();
            }
        });
        Monto();
     }     
    /* private void Autocomplet(){ 
        TextAutoCompleter textAutoCompleter = new TextAutoCompleter(txtproductosconse);
        textAutoCompleter.setCaseSensitive(false); // no es sensible a mayusculas
        textAutoCompleter.setMode(0); // busqueda infijo

        Conexion mysql = new Conexion();
        Connection cn = mysql.Conectar();
        try {
        PreparedStatement pst = cn.prepareStatement("select nombre from productos_inventario");
        ResultSet rs =pst.executeQuery();
         while( rs.next() )
        {
           
            textAutoCompleter.addItem( rs.getString( "nombre" ) );
        }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } 
        }*/
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtfactura = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        btnadicionar = new javax.swing.JButton();
        btneliminar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbcomprasconsecionado = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        txtcantidadcompra = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        cmdregistrarcompra = new javax.swing.JButton();
        btnSalirCompras = new javax.swing.JButton();
        datecompraconse = new com.toedter.calendar.JDateChooser();
        txtpreciocosto = new javax.swing.JFormattedTextField();
        txtvalortotalcompra = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        btncancelarregistro = new javax.swing.JButton();
        labelID = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtmonto = new javax.swing.JFormattedTextField();
        txtproductosconse = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cmbprovee = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        btnnuevoproducto = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        labelestado = new javax.swing.JLabel();
        txtcodigo = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel11.setText("TOTAL COMPRA:");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Numero Factura: ");

        btnadicionar.setText("Adicionar");
        btnadicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnadicionarActionPerformed(evt);
            }
        });

        btneliminar.setText("Eliminar");
        btneliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btneliminarActionPerformed(evt);
            }
        });

        tbcomprasconsecionado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbcomprasconsecionado.setFocusable(false);
        jScrollPane1.setViewportView(tbcomprasconsecionado);

        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Cantidad");

        txtcantidadcompra.setBackground(new java.awt.Color(0, 255, 204));
        txtcantidadcompra.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtcantidadcompra.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtcantidadcompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtcantidadcompraActionPerformed(evt);
            }
        });
        txtcantidadcompra.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtcantidadcompraFocusLost(evt);
            }
        });
        txtcantidadcompra.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txtcantidadcompraInputMethodTextChanged(evt);
            }
        });
        txtcantidadcompra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtcantidadcompraKeyTyped(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Eras Demi ITC", 0, 14)); // NOI18N
        jLabel10.setText("Nombre del Producto");

        cmdregistrarcompra.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdregistrarcompra.setText("<html>REGISTRAR<br>CONSECION</html>");
        cmdregistrarcompra.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdregistrarcompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdregistrarcompraActionPerformed(evt);
            }
        });

        btnSalirCompras.setText("Salir");
        btnSalirCompras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirComprasActionPerformed(evt);
            }
        });

        txtpreciocosto.setBackground(new java.awt.Color(0, 255, 204));
        txtpreciocosto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtpreciocosto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtpreciocosto.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtpreciocosto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtpreciocostoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtpreciocostoFocusLost(evt);
            }
        });
        txtpreciocosto.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txtpreciocostoInputMethodTextChanged(evt);
            }
        });
        txtpreciocosto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtpreciocostoKeyTyped(evt);
            }
        });

        txtvalortotalcompra.setBackground(new java.awt.Color(153, 255, 255));
        txtvalortotalcompra.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtvalortotalcompra.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtvalortotalcompra.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtvalortotalcompra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtvalortotalcompraKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtvalortotalcompraKeyTyped(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel13.setText("ID COMPRA:");

        btncancelarregistro.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btncancelarregistro.setText("<html>CANCELAR<BR> Registro de Compra</html>");
        btncancelarregistro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncancelarregistroActionPerformed(evt);
            }
        });

        labelID.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        labelID.setText("ID");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("INGRESO DE COMPRAS PRODUCTOS CONSECIONADOS");

        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Fecha:");

        jLabel6.setText("Valor Total:");

        txtmonto.setBackground(new java.awt.Color(153, 255, 255));
        txtmonto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtmonto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtmonto.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        txtproductosconse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtproductosconseActionPerformed(evt);
            }
        });
        txtproductosconse.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtproductosconseFocusGained(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Añadir Producto");

        cmbprovee.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText("Proveedor");

        btnnuevoproducto.setText("+");
        btnnuevoproducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnnuevoproductoActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Precio Unitario:");

        labelestado.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        labelestado.setText("ESTADO");

        jLabel14.setFont(new java.awt.Font("Eras Demi ITC", 0, 14)); // NOI18N
        jLabel14.setText("Codigo");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(datecompraconse, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addComponent(btncancelarregistro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel14)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnnuevoproducto, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel13)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelID, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelestado, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmdregistrarcompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btneliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnadicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(15, 15, 15))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtmonto, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(118, 118, 118))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel5))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                                .addComponent(txtproductosconse, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtpreciocosto, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(txtcantidadcompra, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(56, 56, 56))))))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGap(201, 201, 201)
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cmbprovee, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(243, 243, 243))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 677, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addComponent(jLabel6)
                                .addGap(32, 32, 32))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtfactura, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnSalirCompras, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(684, 684, 684)
                            .addComponent(txtvalortotalcompra, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(37, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btncancelarregistro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(btnnuevoproducto))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelID)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(datecompraconse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addComponent(labelestado))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel5)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtproductosconse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtpreciocosto, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtcantidadcompra, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 157, Short.MAX_VALUE)
                        .addComponent(btnadicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btneliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cmdregistrarcompra, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(92, 92, 92)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtmonto, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(96, 96, 96)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(cmbprovee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(66, 66, 66)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(31, 31, 31)
                            .addComponent(jLabel6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtvalortotalcompra, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 296, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGap(18, 18, 18)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSalirCompras, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addComponent(txtfactura, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap()))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnadicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnadicionarActionPerformed
        InsertarDeta();
        
     
        
    }//GEN-LAST:event_btnadicionarActionPerformed

    private void btneliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btneliminarActionPerformed
        if(JOptionPane.showConfirmDialog(null, "¿Desea Eliminar registro?" , "Confirmar", JOptionPane.YES_NO_OPTION)==0){
            me.eliminar(tbcomprasconsecionado, "ingreso_prod_cons_deta", "id_cons_deta");
            
            
        }
    }//GEN-LAST:event_btneliminarActionPerformed

    private void txtcantidadcompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcantidadcompraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcantidadcompraActionPerformed

    private void txtcantidadcompraFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcantidadcompraFocusLost
        ValorTotal();
    }//GEN-LAST:event_txtcantidadcompraFocusLost

    private void txtcantidadcompraInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtcantidadcompraInputMethodTextChanged

    }//GEN-LAST:event_txtcantidadcompraInputMethodTextChanged

    private void txtcantidadcompraKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcantidadcompraKeyTyped
        me.SNum(evt, txtcantidadcompra.getText());
    }//GEN-LAST:event_txtcantidadcompraKeyTyped

    private void cmdregistrarcompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdregistrarcompraActionPerformed
        if(datecompraconse.getDate()== null){
            JOptionPane.showMessageDialog(null, "Debe ingresar una Fecha");
        }else{
            RegistrarCompra_encaCompra();
        }
    }//GEN-LAST:event_cmdregistrarcompraActionPerformed

    private void btnSalirComprasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirComprasActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnSalirComprasActionPerformed

    private void txtpreciocostoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtpreciocostoFocusGained
        if(!txtproductosconse.getText().isEmpty()){
            me.PrecioCompra(txtproductosconse, txtpreciocosto);
        }
    }//GEN-LAST:event_txtpreciocostoFocusGained

    private void txtpreciocostoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtpreciocostoFocusLost
     
    }//GEN-LAST:event_txtpreciocostoFocusLost

    private void txtpreciocostoInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtpreciocostoInputMethodTextChanged

    }//GEN-LAST:event_txtpreciocostoInputMethodTextChanged

    private void txtpreciocostoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtpreciocostoKeyTyped
        me.SNum(evt, txtpreciocosto.getText());
    }//GEN-LAST:event_txtpreciocostoKeyTyped

    private void txtvalortotalcompraKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtvalortotalcompraKeyTyped
        me.SNum(evt, txtvalortotalcompra.getText());
    }//GEN-LAST:event_txtvalortotalcompraKeyTyped

    private void btncancelarregistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncancelarregistroActionPerformed
       // Disables();
        me.Insertar("DELETE FROM ingreso_prod_cons_deta WHERE fk_consecion=" + labelID.getText());
        clear();
        CargarTabla();
    }//GEN-LAST:event_btncancelarregistroActionPerformed

    private void txtproductosconseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtproductosconseActionPerformed

    }//GEN-LAST:event_txtproductosconseActionPerformed

    private void btnnuevoproductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnnuevoproductoActionPerformed
        this.dispose();
        IngresoProductos IP = new IngresoProductos(new javax.swing.JDialog(), true);
        IP.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
            Compras Comp = new Compras(new javax.swing.JDialog(), true);   
            Comp.setVisible(true);
            }
        });
        IP.setVisible(true);
    }//GEN-LAST:event_btnnuevoproductoActionPerformed

    private void txtvalortotalcompraKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtvalortotalcompraKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            InsertarDeta();
        }
    }//GEN-LAST:event_txtvalortotalcompraKeyPressed

    private void txtproductosconseFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtproductosconseFocusGained
       if (!txtcodigo.getText().isEmpty()) {
            me.NombreSugerido(txtcodigo, txtproductosconse);
        }
    }//GEN-LAST:event_txtproductosconseFocusGained

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
            java.util.logging.Logger.getLogger(IngresoConsecionados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IngresoConsecionados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IngresoConsecionados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IngresoConsecionados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                IngresoConsecionados dialog = new IngresoConsecionados(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnSalirCompras;
    private javax.swing.JButton btnadicionar;
    private javax.swing.JButton btncancelarregistro;
    private javax.swing.JButton btneliminar;
    private javax.swing.JButton btnnuevoproducto;
    private static javax.swing.JComboBox cmbprovee;
    private javax.swing.JButton cmdregistrarcompra;
    private com.toedter.calendar.JDateChooser datecompraconse;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelID;
    private javax.swing.JLabel labelestado;
    private static javax.swing.JTable tbcomprasconsecionado;
    private javax.swing.JTextField txtcantidadcompra;
    private javax.swing.JTextField txtcodigo;
    private javax.swing.JTextField txtfactura;
    private javax.swing.JFormattedTextField txtmonto;
    private javax.swing.JFormattedTextField txtpreciocosto;
    private javax.swing.JTextField txtproductosconse;
    private javax.swing.JFormattedTextField txtvalortotalcompra;
    // End of variables declaration//GEN-END:variables
}
