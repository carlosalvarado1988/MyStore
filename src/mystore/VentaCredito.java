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
public class VentaCredito extends javax.swing.JDialog {

    /**
     * Creates new form VentaCredito
     */
    static int[] combointCLIENTE;
    static metodos me = new metodos();
    static Acciones ac = new Acciones();
    public VentaCredito(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        cargar();
        me.Autocomplet(txtproductosvender, "nombre");
        me.Autocomplet(txtcodigo,"codigo");
        TableCellListener tclventacredito = new TableCellListener(tbventaalcredito, ac.acciontablaventacredito);
    }

    private void cargar() {
        Date date = new Date();
        dateventa.setDate(date);
        Conexion mysql = new Conexion();
        ResultSet rs = mysql.select("select id_venta_cred_enca from venta_credito_enca where estado = 'PROCESO'");
        try {
            if (rs.next() == true) {
                String id_en_proceso = rs.getString("id_venta_cred_enca");
                    labelID.setText(id_en_proceso);
                    labelestado.setText("EN PROCESO");
                    combointCLIENTE = me.cargarcombo(cmbcliente, "select id_cliente, nombre from CLIENTE order by nombre ASC", 1, 2);
                   // Enables();
                   // me.Autocomplet(txtproductosvender, "nombre");
                    CargarTabla();

            } else {

           // Disables();
           // me.Autocomplet(txtproductosvender, "nombre");  
                combointCLIENTE = me.cargarcombo(cmbcliente, "select id_cliente, nombre from CLIENTE order by nombre ASC", 1, 2);
                me.Insertar("insert into VENTA_CREDITO_ENCA (estado) values ('PROCESO')");
                Conexion con = new Conexion();
                ResultSet rss = con.select("select id_venta_cred_enca from VENTA_CREDITO_ENCA where estado = 'PROCESO'");
                rss.next();
                String id = rss.getString("id_venta_cred_enca");
                labelID.setText(id);
                labelestado.setText("EN PROCESO");
                CargarTabla();
            }
        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(null, "Error capturando compra en Proceso, contacte Administrador " + ex);
            Logger.getLogger(Compras.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    private void clear() {
        txtproductosvender.setText("");
        txtcantidadventa.setText("");
        txtpreciounitariosugerido.setText("");
        txtvalortotalventa.setText("");
        txtcodigo.setText("");
    }

  /*  private void Disables() {
        labelestado.setEnabled(false);
        btncancelarventa.setEnabled(false);
        cmbcliente.setEnabled(false);
        dateventa.setEnabled(false);
        rbprodconsecionado.setEnabled(false);
        rbprodpropio.setEnabled(false);
        txtproductosvender.setEnabled(false);

        txtcantidadventa.setEnabled(false);
        txtpreciounitariosugerido.setEnabled(false);
        txtvalortotalventa.setEnabled(false);
        btnadicionarventa.setEnabled(false);
        btneliminar.setEnabled(false);
        cmdregistrarventa.setEnabled(false);
        txtmonto.setEnabled(false);
        btnSalirVentas.setEnabled(true);
    }*/

   /* private void Enables() {
        labelestado.setEnabled(true);
        btncancelarventa.setEnabled(true);
        dateventa.setEnabled(true);
        cmbcliente.setEnabled(true);
        txtproductosvender.setEnabled(true);

        txtcantidadventa.setEnabled(true);
        txtpreciounitariosugerido.setEnabled(true);
        txtvalortotalventa.setEnabled(true);
        rbprodconsecionado.setEnabled(true);
        rbprodpropio.setEnabled(true);
        btnadicionarventa.setEnabled(true);
        btneliminar.setEnabled(true);
        cmdregistrarventa.setEnabled(true);
        txtmonto.setEnabled(true);
        btnSalirVentas.setEnabled(true);
    }*/

    private static void Monto() {
        try {
            Conexion cn = new Conexion();
            String idenca = labelID.getText();
            String mont = "select ROUND(sum(precio_total),2) as monto FROM venta_credito_deta where id_venta_cred_enca = " + idenca;
            ResultSet rs = cn.select(mont);
            rs.next();
            txtmonto.setText(rs.getString("monto"));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "fallo monto" + ex);

        }
    }

    public void actuInventario() {
        try {
            Conexion mysql = new Conexion();
            ResultSet rs = mysql.select("SELECT fk_prod, Sum( cantidad ) as cant, consecionado FROM venta_credito_deta WHERE id_venta_cred_enca = " + labelID.getText() + " GROUP BY fk_prod, consecionado");
            while (rs.next()) {
                String id_prod = rs.getString("fk_prod");
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

   /* private void PrecioSugerido() {
        try {
            String prod = txtproductosvender.getText();
            Conexion mysql = new Conexion();
            ResultSet rs = mysql.select("select precio_sugerido from productos_inventario where nombre = '" + prod + "'");

            if (rs.next() == true) {
                String ps = rs.getString("precio_sugerido");
                txtpreciounitariosugerido.setText(ps);
            } else {
                txtpreciounitariosugerido.setText("0");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Necesita ingresar el Nombre del Producto de la lista");

        }
    }*/

    private void ValorTotal() {

        Double Cant = Double.parseDouble(txtcantidadventa.getText());
        Double PU = Double.parseDouble(txtpreciounitariosugerido.getText());
        Double VT = Cant * PU;
        txtvalortotalventa.setText(Double.toString(me.Redondear(VT)));

    }

    void InsertarDeta() {
        if (txtproductosvender.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Necesita ingresar el Nombre del Producto");
        } else {

            try {
                Conexion mysql = new Conexion();
                Connection cn = mysql.Conectar();
                String prod = txtproductosvender.getText();
                ResultSet rs = mysql.select("select id_prod from productos_inventario where nombre = '" + prod + "'");
                rs.next();
                int idprod = rs.getInt("id_prod");
                double cant, preUni, valTot;
                cant = Double.parseDouble(txtcantidadventa.getText());
                preUni = Double.parseDouble(txtpreciounitariosugerido.getText());
                valTot = Double.parseDouble(txtvalortotalventa.getText());
                int idenca = Integer.parseInt(labelID.getText());
                String consecionado;
                if (rbprodconsecionado.isSelected() == true) {
                    consecionado = "Si";
                } else {
                    consecionado = "No";
                }

                String sSQL = "INSERT INTO VENTA_CREDITO_DETA (fk_prod, cantidad, precio, precio_total, consecionado, id_venta_cred_enca)"
                        + "VALUES(? , ? , ? , ? , ? , ?)";

                try {
                    PreparedStatement pst = cn.prepareStatement(sSQL);
                    pst.setInt(1, idprod);
                    pst.setDouble(2, cant);
                    pst.setDouble(3, preUni);
                    pst.setDouble(4, valTot);
                    pst.setString(5, consecionado);
                    pst.setInt(6, idenca);

                    int n = pst.executeUpdate();
                    if (n > 0) {
                        //JOptionPane.showMessageDialog(null, "Item insertado");
                        clear();
                        CargarTabla();
                    } else {
                    }

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "error insertando venta sql" + ex);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "error extraendo id de productos");

            }
        }
    }

    public static void CargarTabla() {
        String id_enca = labelID.getText();
        me.cargartabla(tbventaalcredito, "select v.id_venta_cred_deta,  @i := @i + 1 as '#', p.codigo as CODIGO, p.nombre as PRODUCTO,  m.nombre_marca as MARCA,  v.precio as 'PRECIO UNITARIO', v.cantidad as CANTIDAD, ROUND((precio * cantidad),2) as TOTAL, v.consecionado as CONSECIONADO from venta_credito_deta as v, productos_inventario as p, marca_prod as m, (select @i := 0) as id where v.fk_prod = p.id_prod AND p.fk_marca = m.id_marca AND id_venta_cred_enca =" + id_enca);
        Monto();
    }

    void RegistrarVenta_encaVenta() {
        String fechaventa, monto, idenca;
        fechaventa = me.FechaFormat(dateventa.getDate()); // fecha
        int cliente = combointCLIENTE[cmbcliente.getSelectedIndex()]; // cliente
        monto = txtmonto.getText(); //monto
        idenca = labelID.getText();

        String updateENCAsql = "update VENTA_CREDITO_ENCA set fk_cliente = '" + cliente + "', fecha = '" + fechaventa + "', monto = " + monto
                + ", estado = 'REGISTRADA' where id_venta_cred_enca =" + idenca;
        me.Insertar(updateENCAsql);
        String RegistroDeuda = "insert into DEUDAS (fk_venta_cred_enca, saldo, fecha, estado) values(" + idenca + ", " + monto + ", '" + fechaventa + "', 'PENDIENTE')";
        me.Insertar(RegistroDeuda);
        JOptionPane.showMessageDialog(null, "Venta ID: " + idenca + " fue Registrada");
        labelestado.setText("REGISTRADA");
        cargar();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel6 = new javax.swing.JLabel();
        txtmonto = new javax.swing.JFormattedTextField();
        rbprodconsecionado = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        txtproductosvender = new javax.swing.JTextField();
        rbprodpropio = new javax.swing.JRadioButton();
        jLabel8 = new javax.swing.JLabel();
        btnadicionarventa = new javax.swing.JButton();
        btneliminar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbventaalcredito = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        btnSalirVentas = new javax.swing.JButton();
        txtcantidadventa = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        cmdregistrarventa = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtpreciounitariosugerido = new javax.swing.JFormattedTextField();
        dateventa = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        txtvalortotalventa = new javax.swing.JFormattedTextField();
        btncancelarventa = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        labelID = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cmbcliente = new javax.swing.JComboBox();
        labelestado = new javax.swing.JLabel();
        txtcodigo = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel6.setText("Valor Total:");

        txtmonto.setEditable(false);
        txtmonto.setBackground(new java.awt.Color(153, 255, 255));
        txtmonto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtmonto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtmonto.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N

        rbprodconsecionado.setText("Prod. Consecionado");
        rbprodconsecionado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbprodconsecionadoActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Eras Medium ITC", 0, 12)); // NOI18N
        jLabel5.setText("Precio Unitario:");

        txtproductosvender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtproductosvenderActionPerformed(evt);
            }
        });
        txtproductosvender.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtproductosvenderFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtproductosvenderFocusLost(evt);
            }
        });

        rbprodpropio.setSelected(true);
        rbprodpropio.setText("Prod. Propio");
        rbprodpropio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbprodpropioActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel8.setText("TOTAL VENTA:");

        btnadicionarventa.setText("Adicionar");
        btnadicionarventa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnadicionarventaActionPerformed(evt);
            }
        });

        btneliminar.setText("Eliminar");
        btneliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btneliminarActionPerformed(evt);
            }
        });
        btneliminar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                btneliminarFocusGained(evt);
            }
        });

        tbventaalcredito.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbventaalcredito.setFocusable(false);
        jScrollPane1.setViewportView(tbventaalcredito);

        jLabel7.setFont(new java.awt.Font("Eras Medium ITC", 0, 12)); // NOI18N
        jLabel7.setText("Cantidad");

        btnSalirVentas.setText("Salir");
        btnSalirVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirVentasActionPerformed(evt);
            }
        });

        txtcantidadventa.setBackground(new java.awt.Color(0, 255, 204));
        txtcantidadventa.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtcantidadventa.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtcantidadventa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtcantidadventaActionPerformed(evt);
            }
        });
        txtcantidadventa.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtcantidadventaFocusLost(evt);
            }
        });
        txtcantidadventa.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txtcantidadventaInputMethodTextChanged(evt);
            }
        });
        txtcantidadventa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtcantidadventaKeyTyped(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Eras Demi ITC", 0, 14)); // NOI18N
        jLabel10.setText("Nombre del Producto");

        cmdregistrarventa.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdregistrarventa.setText("<html>REGISTRAR<br>VENTA<br>AL CREDITO</html>");
        cmdregistrarventa.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdregistrarventa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdregistrarventaActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Fecha:");

        txtpreciounitariosugerido.setBackground(new java.awt.Color(0, 255, 204));
        txtpreciounitariosugerido.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtpreciounitariosugerido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtpreciounitariosugerido.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtpreciounitariosugerido.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtpreciounitariosugeridoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtpreciounitariosugeridoFocusLost(evt);
            }
        });
        txtpreciounitariosugerido.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txtpreciounitariosugeridoInputMethodTextChanged(evt);
            }
        });
        txtpreciounitariosugerido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtpreciounitariosugeridoKeyTyped(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel11.setText("ID VENTA:");

        txtvalortotalventa.setBackground(new java.awt.Color(153, 255, 255));
        txtvalortotalventa.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtvalortotalventa.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtvalortotalventa.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtvalortotalventa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtvalortotalventaKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtvalortotalventaKeyTyped(evt);
            }
        });

        btncancelarventa.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btncancelarventa.setText("<html>CANCELAR<BR> Registro de Venta</html>");
        btncancelarventa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncancelarventaActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("INGRESO DE VENTA AL CREDITO PARA UN CLIENTE");

        labelID.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        labelID.setText("ID");

        jLabel3.setText("Cliente");

        cmbcliente.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(64, 64, 64)
                                        .addComponent(btnSalirVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(rbprodconsecionado)
                                            .addComponent(rbprodpropio)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(btneliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cmdregistrarventa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnadicionarventa, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(txtvalortotalventa, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(33, 33, 33)
                                        .addComponent(jLabel6))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(340, 340, 340)
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtmonto, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel2)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addGap(100, 100, 100)
                                        .addComponent(jLabel10)))
                                .addGap(0, 151, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btncancelarventa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(dateventa, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(35, 35, 35)
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cmbcliente, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(108, 108, 108)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel5)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                            .addComponent(labelestado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtproductosvender, javax.swing.GroupLayout.PREFERRED_SIZE, 442, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(txtpreciounitariosugerido, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(txtcantidadventa, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(labelID)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btncancelarventa, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(dateventa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(cmbcliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelestado)))
                        .addGap(13, 13, 13)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(jLabel12))))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtproductosvender, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtcantidadventa, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtpreciounitariosugerido, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(139, 139, 139)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtvalortotalventa, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(rbprodpropio, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rbprodconsecionado)
                        .addGap(18, 18, 18)
                        .addComponent(btnadicionarventa, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btneliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(cmdregistrarventa, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtmonto, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnSalirVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rbprodconsecionadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbprodconsecionadoActionPerformed
        if (rbprodconsecionado.isSelected() == true) {
            rbprodpropio.setSelected(false);
        }
    }//GEN-LAST:event_rbprodconsecionadoActionPerformed

    private void txtproductosvenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtproductosvenderActionPerformed

    }//GEN-LAST:event_txtproductosvenderActionPerformed

    private void txtproductosvenderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtproductosvenderFocusLost

    }//GEN-LAST:event_txtproductosvenderFocusLost

    private void rbprodpropioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbprodpropioActionPerformed
        if (rbprodpropio.isSelected() == true) {
            rbprodconsecionado.setSelected(false);
        }
    }//GEN-LAST:event_rbprodpropioActionPerformed

    private void btnadicionarventaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnadicionarventaActionPerformed
        InsertarDeta();
        Monto();

        

    }//GEN-LAST:event_btnadicionarventaActionPerformed

    private void btneliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btneliminarActionPerformed
        me.eliminar(tbventaalcredito, "venta_credito_deta", "id_venta_cred_deta");
        CargarTabla();
    }//GEN-LAST:event_btneliminarActionPerformed

    private void btnSalirVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirVentasActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnSalirVentasActionPerformed

    private void txtcantidadventaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcantidadventaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcantidadventaActionPerformed

    private void txtcantidadventaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcantidadventaFocusLost
        ValorTotal();
        ////Causa error cuando se activa el cell listener
    }//GEN-LAST:event_txtcantidadventaFocusLost

    private void txtcantidadventaInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtcantidadventaInputMethodTextChanged

    }//GEN-LAST:event_txtcantidadventaInputMethodTextChanged

    private void txtcantidadventaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcantidadventaKeyTyped
        me.SNum(evt, txtcantidadventa.getText());
    }//GEN-LAST:event_txtcantidadventaKeyTyped

    private void cmdregistrarventaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdregistrarventaActionPerformed
        if (dateventa.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Debe ingresar una Fecha");
        } else {
            RegistrarVenta_encaVenta();
        }
    }//GEN-LAST:event_cmdregistrarventaActionPerformed

    private void txtpreciounitariosugeridoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtpreciounitariosugeridoFocusGained
        if (!txtproductosvender.getText().isEmpty()) {
            me.PrecioSugerido(txtproductosvender, txtpreciounitariosugerido);
        }
    }//GEN-LAST:event_txtpreciounitariosugeridoFocusGained

    private void txtpreciounitariosugeridoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtpreciounitariosugeridoFocusLost

    }//GEN-LAST:event_txtpreciounitariosugeridoFocusLost

    private void txtpreciounitariosugeridoInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtpreciounitariosugeridoInputMethodTextChanged

    }//GEN-LAST:event_txtpreciounitariosugeridoInputMethodTextChanged

    private void txtpreciounitariosugeridoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtpreciounitariosugeridoKeyTyped
        me.SNum(evt, txtpreciounitariosugerido.getText());
    }//GEN-LAST:event_txtpreciounitariosugeridoKeyTyped

    private void txtvalortotalventaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtvalortotalventaKeyTyped
        me.SNum(evt, txtvalortotalventa.getText());
    }//GEN-LAST:event_txtvalortotalventaKeyTyped

    private void btncancelarventaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncancelarventaActionPerformed
        me.Insertar("DELETE FROM VENTA_CREDITO_DETA where id_venta_cred_enca =" + labelID.getText());
        clear();
        CargarTabla();
        
        /*String encaID = labelID.getText();
        me.Insertar("update VENTA_CREDITO_ENCA set estado = 'CANCELADA' where id_venta_cred_enca =" + encaID);
        labelestado.setText("CANCELADA");
        clear();*/
        

    }//GEN-LAST:event_btncancelarventaActionPerformed

    private void btneliminarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_btneliminarFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_btneliminarFocusGained

    private void txtvalortotalventaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtvalortotalventaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            InsertarDeta();
        }
    }//GEN-LAST:event_txtvalortotalventaKeyPressed

    private void txtproductosvenderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtproductosvenderFocusGained
        if (!txtcodigo.getText().isEmpty()) {
            me.NombreSugerido(txtcodigo, txtproductosvender);
        }
    }//GEN-LAST:event_txtproductosvenderFocusGained
    
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
            java.util.logging.Logger.getLogger(VentaCredito.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentaCredito.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentaCredito.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentaCredito.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                VentaCredito dialog = new VentaCredito(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnSalirVentas;
    private javax.swing.JButton btnadicionarventa;
    private javax.swing.JButton btncancelarventa;
    private javax.swing.JButton btneliminar;
    private static javax.swing.JComboBox cmbcliente;
    private javax.swing.JButton cmdregistrarventa;
    private com.toedter.calendar.JDateChooser dateventa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private static javax.swing.JLabel labelID;
    private javax.swing.JLabel labelestado;
    private javax.swing.JRadioButton rbprodconsecionado;
    private javax.swing.JRadioButton rbprodpropio;
    private static javax.swing.JTable tbventaalcredito;
    private javax.swing.JTextField txtcantidadventa;
    private javax.swing.JTextField txtcodigo;
    private static javax.swing.JFormattedTextField txtmonto;
    private javax.swing.JFormattedTextField txtpreciounitariosugerido;
    private javax.swing.JTextField txtproductosvender;
    private javax.swing.JFormattedTextField txtvalortotalventa;
    // End of variables declaration//GEN-END:variables
}