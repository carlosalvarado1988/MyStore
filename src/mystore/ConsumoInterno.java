/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mystore;

import com.mxrck.autocompleter.TextAutoCompleter;
import java.awt.event.KeyEvent;
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
public class ConsumoInterno extends javax.swing.JDialog {

    /**
     * Creates new form ConsumoInterno
     */
    
    static metodos me = new metodos();
    static Acciones ac = new Acciones();
    public ConsumoInterno(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        me.Autocomplet(txtproductosconsumo, "nombre");
        me.Autocomplet(txtcodigo,"codigo");
        cargar();
        TableCellListener tclconsumodiario = new TableCellListener(tbconsumodiario, ac.acciontablaconsumointerno);
    }

    

    private void cargar() {
        Conexion mysql = new Conexion();
        Date date = new Date();
        dateconsumo.setDate(date);
        try {
            ResultSet rs = mysql.select("select id_consumo_interno_enca from consumo_interno_enca where estado = 'PROCESO'");
            if (rs.next() == true) {
                String id_en_proceso = rs.getString("id_consumo_interno_enca");
                labelID.setText(id_en_proceso);
                labelestado.setText("EN PROCESO");
                
                CargarTabla();
                
               
            } else {
                
                me.Insertar("insert into CONSUMO_INTERNO_ENCA (estado) values ('PROCESO')");
                Conexion con = new Conexion();
                ResultSet rss = con.select("select id_consumo_interno_enca from CONSUMO_INTERNO_ENCA where estado = 'PROCESO'");
                rss.next();
                String id = rss.getString("id_consumo_interno_enca");
                labelID.setText(id);
                labelestado.setText("EN PROCESO");
                 CargarTabla();
            }
        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(null, "Error capturando compra en Proceso, contacte Administrador" + ex);
            Logger.getLogger(Compras.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void CargarTabla() {
        String id_enca = labelID.getText();
        me.cargartabla(tbconsumodiario, "select ci.id_consumo_interno_deta,  @i := @i + 1 as '#', p.codigo as CODIGO, p.nombre as PRODUCTO,  m.nombre_marca as MARCA,  ci.precio_unitario as 'PRECIO UNITARIO', ci.cantidad as CANTIDAD, ROUND((precio_unitario * cantidad),2) as TOTAL, ci.consecionado as CONSECIONADO from consumo_interno_deta as ci, productos_inventario as p, marca_prod as m, (select @i := 0) as id where ci.fk_producto = p.id_prod AND p.fk_marca = m.id_marca AND fk_consumo_interno_enca =" + id_enca);
        tbconsumodiario.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (JOptionPane.showConfirmDialog(null, "¿Desea modificar el registro?", "Confirmar", JOptionPane.YES_NO_OPTION) == 0) {
                    me.Insertar("UPDATE consumo_interno_deta SET cantidad='" + tbconsumodiario.getValueAt(e.getFirstRow(), 4) + "', precio_unitario ='" + tbconsumodiario.getValueAt(e.getFirstRow(), 3) + "' WHERE id_consumo_interno_deta=" + tbconsumodiario.getModel().getValueAt(e.getFirstRow(), 0));
                }

                Monto();
            }

        });
        Monto();
    }

    public void actuInventario() {
        try {
            Conexion mysql = new Conexion();
            ResultSet rs = mysql.select("SELECT fk_producto, Sum( cantidad ) as cant, consecionado FROM consumo_interno_deta WHERE fk_consumo_interno_enca = " + labelID.getText() + " GROUP BY fk_producto, consecionado");
            while (rs.next()) {
                String id_prod = rs.getString("fk_producto");
                String cantidad = rs.getString("cant");
                String conse = rs.getString("consecionado");
                String Cant_to_update;
                if (conse.equals("Si")) {
                    Cant_to_update = "cant_disp_conse";
                } else {
                    Cant_to_update = "cant_disp_prop";
                }

                me.Insertar("Update productos_inventario set " + Cant_to_update + " = " + Cant_to_update + " - " + cantidad + " where id_prod = " + id_prod);
                labelestado.setText("REGISTRADA");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error actualizando inventario, contacte Administrador" + ex);
            Logger.getLogger(VentaDiaria.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clear() {
        txtproductosconsumo.setText("");
        txtcantidadconsumo.setText("");
        txtpreciocosto.setText("");
        txtvalortotalconsumo.setText("");
        txtcodigo.setText("");
    }

    private void Disables() {
        labelestado.setEnabled(false);
        btncancelarconsumo.setEnabled(false);
        dateconsumo.setEnabled(false);
        rbprodconsecionado.setEnabled(false);
        rbprodpropio.setEnabled(false);
        txtproductosconsumo.setEnabled(false);
        txtcantidadconsumo.setEnabled(false);
        txtpreciocosto.setEnabled(false);
        txtvalortotalconsumo.setEnabled(false);
        btnadicionarconsumo.setEnabled(false);
        btneliminarconsumo.setEnabled(false);
        cmdregistrarconsumo.setEnabled(false);
        txtmontoconsumo.setEnabled(false);
        
        btnSalirConsumo.setEnabled(true);
    }

    private void Enables() {
        btncancelarconsumo.setEnabled(true);
        dateconsumo.setEnabled(true);
        txtproductosconsumo.setEnabled(true);
        labelestado.setEnabled(true);
        txtcantidadconsumo.setEnabled(true);
        txtpreciocosto.setEnabled(true);
        txtvalortotalconsumo.setEnabled(true);
        rbprodconsecionado.setEnabled(true);
        rbprodpropio.setEnabled(true);
        btnadicionarconsumo.setEnabled(true);
        btneliminarconsumo.setEnabled(true);
        cmdregistrarconsumo.setEnabled(true);
        txtmontoconsumo.setEnabled(true);
        
        btnSalirConsumo.setEnabled(false);
    }

    public void Monto() {
        try {
            Conexion cn = new Conexion();
            String idenca = labelID.getText();
            String mont = "select ROUND(SUM(precio_total),2) as monto FROM consumo_interno_deta where fk_consumo_interno_enca = " + idenca;
            ResultSet rs = cn.select(mont);
            rs.next();
            txtmontoconsumo.setText(rs.getString("monto"));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "fallo monto" + ex);

        }
    }

    /*private void PrecioCompra() {
        try {
            String prod = txtproductosconsumo.getText();
            Conexion mysql = new Conexion();
            ResultSet rs = mysql.select("select precio_costo from productos_inventario where nombre = '" + prod + "'");
            if (rs.next() == true) {
                String pc = rs.getString("precio_costo");
                txtpreciocosto.setText(pc);
            } else {
                txtpreciocosto.setText("0");
            }
          

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "error al extraer el precio de compra " + ex);
        }
    }*/

    private void ValorTotal() {

        Double Cant = Double.parseDouble(txtcantidadconsumo.getText());
        Double PU = Double.parseDouble(txtpreciocosto.getText());
        Double VT = Cant * PU;
        txtvalortotalconsumo.setText(Double.toString(me.Redondear(VT)));

    }

    void InsertarDeta() {
        if (txtproductosconsumo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Necesita ingresar el Nombre del Producto");
        } else {

            try {
                Conexion mysql = new Conexion();
                Connection cn = mysql.Conectar();
                String prod = txtproductosconsumo.getText();
                ResultSet rs = mysql.select("select id_prod from productos_inventario where nombre = '" + prod + "'");
                rs.next();
                int idprod = rs.getInt("id_prod");
                double cant, preUni, preTot;
                cant = Double.parseDouble(txtcantidadconsumo.getText());
                preUni = Double.parseDouble(txtpreciocosto.getText());
                preTot = Double.parseDouble(txtvalortotalconsumo.getText());    
                int idenca = Integer.parseInt(labelID.getText());
                //boolean consecionado = rbprodconsecionado.isSelected();
                String consecionado;
                if (rbprodconsecionado.isSelected() == true) {
                    consecionado = "Si";
                } else {
                    consecionado = "No";
                }

                String sSQL = "INSERT INTO CONSUMO_INTERNO_DETA (fk_producto, cantidad, precio_unitario, precio_total, consecionado, fk_consumo_interno_enca)"
                        + "VALUES(? , ? , ? , ? , ? , ?)";

                try {
                    PreparedStatement pst = cn.prepareStatement(sSQL);
                    pst.setInt(1, idprod);
                    pst.setDouble(2, cant);
                    pst.setDouble(3, preUni);
                    pst.setDouble(4, preTot);
                    pst.setString(5, consecionado);
                    pst.setInt(6, idenca);

                    int n = pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "n" + n);
                    if (n > 0) {
                      //  JOptionPane.showMessageDialog(null, "Item insertado");
                        clear();
                        CargarTabla();
                    } else {
                    }

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error insertando consumo sql" + ex);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error extraendo id de productos");

            }
        }
    }

    void RegistrarVenta_encaVenta() {
        String fechaventa, monto, idenca;
        //int idenca = Integer.parseInt(labelID.getText());
        fechaventa = me.FechaFormat(dateconsumo.getDate()); // fecha 
        monto = txtmontoconsumo.getText(); //monto
        idenca = labelID.getText();

        String updateENCAsql = "update CONSUMO_INTERNO_ENCA set fecha = '" + fechaventa + "', monto = " + monto + ", estado = 'REGISTRADA' where id_consumo_interno_enca =" + idenca;
        me.Insertar(updateENCAsql);
        JOptionPane.showMessageDialog(null, "Consumo interno ID: " + idenca + " fue Registrado");
        labelestado.setText("REGISTRADA");
        Disables();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        rbprodpropio = new javax.swing.JRadioButton();
        jLabel8 = new javax.swing.JLabel();
        txtproductosconsumo = new javax.swing.JTextField();
        btnadicionarconsumo = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        rbprodconsecionado = new javax.swing.JRadioButton();
        btneliminarconsumo = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        labelID = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtmontoconsumo = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbconsumodiario = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        txtcantidadconsumo = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        cmdregistrarconsumo = new javax.swing.JButton();
        btnSalirConsumo = new javax.swing.JButton();
        dateconsumo = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        txtpreciocosto = new javax.swing.JFormattedTextField();
        txtvalortotalconsumo = new javax.swing.JFormattedTextField();
        btncancelarconsumo = new javax.swing.JButton();
        labelestado = new javax.swing.JLabel();
        txtcodigo = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        rbprodpropio.setSelected(true);
        rbprodpropio.setText("Prod. Propio");
        rbprodpropio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbprodpropioActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel8.setText("TOTAL CONSUMO:");

        txtproductosconsumo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtproductosconsumoActionPerformed(evt);
            }
        });
        txtproductosconsumo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtproductosconsumoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtproductosconsumoFocusLost(evt);
            }
        });

        btnadicionarconsumo.setText("Adicionar");
        btnadicionarconsumo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnadicionarconsumoActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Eras Medium ITC", 0, 12)); // NOI18N
        jLabel5.setText("Precio Unitario:");

        rbprodconsecionado.setText("Prod. Consecionado");
        rbprodconsecionado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbprodconsecionadoActionPerformed(evt);
            }
        });

        btneliminarconsumo.setText("Eliminar");
        btneliminarconsumo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btneliminarconsumoActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("INGRESO DE CONSUMO INTERNO");

        labelID.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        labelID.setText("ID");

        jLabel6.setText("Valor Total:");

        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Fecha:");

        txtmontoconsumo.setEditable(false);
        txtmontoconsumo.setBackground(new java.awt.Color(153, 255, 255));
        txtmontoconsumo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtmontoconsumo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtmontoconsumo.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N

        tbconsumodiario.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbconsumodiario);

        jLabel7.setFont(new java.awt.Font("Eras Medium ITC", 0, 12)); // NOI18N
        jLabel7.setText("Cantidad");

        txtcantidadconsumo.setBackground(new java.awt.Color(0, 255, 204));
        txtcantidadconsumo.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtcantidadconsumo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtcantidadconsumo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtcantidadconsumoActionPerformed(evt);
            }
        });
        txtcantidadconsumo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtcantidadconsumoFocusLost(evt);
            }
        });
        txtcantidadconsumo.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txtcantidadconsumoInputMethodTextChanged(evt);
            }
        });
        txtcantidadconsumo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtcantidadconsumoKeyTyped(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Eras Demi ITC", 0, 14)); // NOI18N
        jLabel10.setText("Nombre del Producto");

        cmdregistrarconsumo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdregistrarconsumo.setText("<html>REGISTRAR<br>CONSUMO</html>");
        cmdregistrarconsumo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdregistrarconsumo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdregistrarconsumoActionPerformed(evt);
            }
        });

        btnSalirConsumo.setText("Salir");
        btnSalirConsumo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirConsumoActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel11.setText("ID VENTA:");

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

        txtvalortotalconsumo.setBackground(new java.awt.Color(153, 255, 255));
        txtvalortotalconsumo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtvalortotalconsumo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtvalortotalconsumo.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtvalortotalconsumo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtvalortotalconsumoKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtvalortotalconsumoKeyTyped(evt);
            }
        });

        btncancelarconsumo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btncancelarconsumo.setText("<html>CANCELAR<BR> Registro de Consumo</html>");
        btncancelarconsumo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncancelarconsumoActionPerformed(evt);
            }
        });

        labelestado.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        labelestado.setText("ESTADO");

        jLabel12.setFont(new java.awt.Font("Eras Demi ITC", 0, 14)); // NOI18N
        jLabel12.setText("Codigo");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(470, 470, 470)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(324, 324, 324)
                                .addComponent(jLabel8)
                                .addGap(18, 18, 18)
                                .addComponent(txtmontoconsumo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel2)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(rbprodconsecionado)
                                                    .addComponent(rbprodpropio)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addGap(56, 56, 56)
                                                        .addComponent(btnSalirConsumo, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(2, 2, 2))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(btneliminarconsumo, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(cmdregistrarconsumo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnadicionarconsumo, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(20, 20, 20)
                                        .addComponent(txtvalortotalconsumo, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(37, 37, 37)
                                        .addComponent(jLabel6))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addGap(197, 197, 197)
                                        .addComponent(jLabel10))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtproductosconsumo, javax.swing.GroupLayout.PREFERRED_SIZE, 442, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtpreciocosto, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(txtcantidadconsumo, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(35, 35, 35)
                                        .addComponent(jLabel7))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btncancelarconsumo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(dateconsumo, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(380, 380, 380)
                                        .addComponent(labelestado, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 14, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelID)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btncancelarconsumo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(dateconsumo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelestado)))
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel10)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(jLabel12)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtproductosconsumo, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtcantidadconsumo, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtpreciocosto, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtcodigo, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtvalortotalconsumo, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rbprodpropio, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rbprodconsecionado)
                        .addGap(18, 18, 18)
                        .addComponent(btnadicionarconsumo, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btneliminarconsumo, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(cmdregistrarconsumo, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtmontoconsumo, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnSalirConsumo, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rbprodpropioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbprodpropioActionPerformed
        if (rbprodpropio.isSelected() == true) {
            rbprodconsecionado.setSelected(false);
        }
    }//GEN-LAST:event_rbprodpropioActionPerformed

    private void txtproductosconsumoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtproductosconsumoActionPerformed

    }//GEN-LAST:event_txtproductosconsumoActionPerformed

    private void txtproductosconsumoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtproductosconsumoFocusLost

    }//GEN-LAST:event_txtproductosconsumoFocusLost

    private void btnadicionarconsumoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnadicionarconsumoActionPerformed
        InsertarDeta();
        Monto();

    }//GEN-LAST:event_btnadicionarconsumoActionPerformed

    private void rbprodconsecionadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbprodconsecionadoActionPerformed
        if (rbprodconsecionado.isSelected() == true) {
            rbprodpropio.setSelected(false);
        }
    }//GEN-LAST:event_rbprodconsecionadoActionPerformed

    private void txtcantidadconsumoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcantidadconsumoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcantidadconsumoActionPerformed

    private void txtcantidadconsumoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcantidadconsumoFocusLost
        ValorTotal();
    }//GEN-LAST:event_txtcantidadconsumoFocusLost

    private void txtcantidadconsumoInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtcantidadconsumoInputMethodTextChanged

    }//GEN-LAST:event_txtcantidadconsumoInputMethodTextChanged

    private void txtcantidadconsumoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcantidadconsumoKeyTyped
        me.SNum(evt, txtcantidadconsumo.getText());
    }//GEN-LAST:event_txtcantidadconsumoKeyTyped

    private void cmdregistrarconsumoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdregistrarconsumoActionPerformed
        if (dateconsumo.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Debe ingresar una Fecha");
        } else {
            RegistrarVenta_encaVenta();
        }
    }//GEN-LAST:event_cmdregistrarconsumoActionPerformed

    private void btnSalirConsumoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirConsumoActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnSalirConsumoActionPerformed

    private void txtpreciocostoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtpreciocostoFocusGained
        if (!txtproductosconsumo.getText().isEmpty()) {
            me.PrecioCompra(txtproductosconsumo, txtpreciocosto);
        }
    }//GEN-LAST:event_txtpreciocostoFocusGained

    private void txtpreciocostoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtpreciocostoFocusLost

    }//GEN-LAST:event_txtpreciocostoFocusLost

    private void txtpreciocostoInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtpreciocostoInputMethodTextChanged

    }//GEN-LAST:event_txtpreciocostoInputMethodTextChanged

    private void txtpreciocostoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtpreciocostoKeyTyped
        me.SNum(evt, txtpreciocosto.getText());
    }//GEN-LAST:event_txtpreciocostoKeyTyped

    private void txtvalortotalconsumoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtvalortotalconsumoKeyTyped
        me.SNum(evt, txtvalortotalconsumo.getText());
    }//GEN-LAST:event_txtvalortotalconsumoKeyTyped

    private void btncancelarconsumoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncancelarconsumoActionPerformed
        me.Insertar("DELETE FROM consumo_interno_deta WHERE fk_consumo_interno_enca=" + labelID.getText());
        clear();
        CargarTabla();
    }//GEN-LAST:event_btncancelarconsumoActionPerformed

    private void btneliminarconsumoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btneliminarconsumoActionPerformed
        if (JOptionPane.showConfirmDialog(null, "¿Desea Eliminar registro?", "Confirmar", JOptionPane.YES_NO_OPTION) == 0) {
            me.eliminar(tbconsumodiario, "consumo_interno_deta", "id_consumo_interno_deta");

        }
    }//GEN-LAST:event_btneliminarconsumoActionPerformed

    private void txtvalortotalconsumoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtvalortotalconsumoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            InsertarDeta();
        }
    }//GEN-LAST:event_txtvalortotalconsumoKeyPressed

    private void txtproductosconsumoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtproductosconsumoFocusGained
        if (!txtcodigo.getText().isEmpty()) {
            me.NombreSugerido(txtcodigo, txtproductosconsumo);
        }
    }//GEN-LAST:event_txtproductosconsumoFocusGained

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
            java.util.logging.Logger.getLogger(ConsumoInterno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConsumoInterno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConsumoInterno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConsumoInterno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ConsumoInterno dialog = new ConsumoInterno(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnSalirConsumo;
    private javax.swing.JButton btnadicionarconsumo;
    private javax.swing.JButton btncancelarconsumo;
    private javax.swing.JButton btneliminarconsumo;
    private javax.swing.JButton cmdregistrarconsumo;
    private com.toedter.calendar.JDateChooser dateconsumo;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelID;
    private javax.swing.JLabel labelestado;
    private javax.swing.JRadioButton rbprodconsecionado;
    private javax.swing.JRadioButton rbprodpropio;
    private static javax.swing.JTable tbconsumodiario;
    private javax.swing.JTextField txtcantidadconsumo;
    private javax.swing.JTextField txtcodigo;
    private javax.swing.JFormattedTextField txtmontoconsumo;
    private javax.swing.JFormattedTextField txtpreciocosto;
    private javax.swing.JTextField txtproductosconsumo;
    private javax.swing.JFormattedTextField txtvalortotalconsumo;
    // End of variables declaration//GEN-END:variables
}
