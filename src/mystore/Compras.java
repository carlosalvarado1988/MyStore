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
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 *
 * @author Administrador
 */
public class Compras extends javax.swing.JDialog {

    /**
     * Creates new form Compras
     */
    static int[] combointPROVEE;
    static metodos me = new metodos();
    static Acciones ac = new Acciones();

    public Compras(javax.swing.JDialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
        me.Autocomplet(txtproductos, "nombre");
        me.Autocomplet(txtcodigo,"codigo");
        cargar();

        //Esto inicia los listener para modificar las tablas, se le meten como parametros el nombre de la tabla
        //y una action de acciones.java donde va lo que hace cuando se dispara el evento.
        //Hay que crear una action por cada tabla porque llevan sentencias sql diferentes
        TableCellListener tclcompras = new TableCellListener(tbcompras, ac.acciontablacompras);
    }

    private void cargar() {

        Conexion mysql = new Conexion();
        Date date = new Date();
        datecompra.setDate(date);
        try {
            ResultSet rs = mysql.select("select id_compra_enca from compras_enca where estado = 'PROCESO'");
            if (rs.next() == true) {
                String id_en_proceso = rs.getString("id_compra_enca");
                labelID.setText(id_en_proceso);
                labelestado.setText("EN PROCESO");
                combointPROVEE = me.cargarcombo(cmbprovee, "select id_provee, nombre_provee from PROVEEDOR order by nombre_provee ASC", 1, 2);
             //   Enables();

                CargarTabla();

            } else {

                combointPROVEE = me.cargarcombo(cmbprovee, "select id_provee, nombre_provee from PROVEEDOR order by nombre_provee ASC", 1, 2);
                //   Enables();
                me.Insertar("insert into COMPRAS_ENCA (estado) values ('PROCESO')");
                ResultSet rss = mysql.select("select id_compra_enca from COMPRAS_ENCA where estado = 'PROCESO'");
                rss.next();
                String id = rss.getString("id_compra_enca");
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
        txtproductos.setText("");
        txtcantidadcompra.setText("");
        txtpreciocosto.setText("");
        txtvalortotalcompra.setText("");
        txtcodigo.setText("");
    }

    public void actuInventario() {
        try {
            Conexion mysql = new Conexion();
            ResultSet rs = mysql.select("SELECT fk_producto, Sum( cantidad ) as cant FROM compras_deta WHERE fk_compra_enca = " + labelID.getText() + " GROUP BY fk_producto");
            while (rs.next()) {
                String id_prod = rs.getString("fk_producto");
                String cantidad = rs.getString("cant");
                me.Insertar("Update productos_inventario set cant_disp_prop = cant_disp_prop + " + cantidad + " where id_prod = " + id_prod);
                labelestado.setText("REGISTRADA");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error actualizando inventario, contacte Administrador" + ex);
            Logger.getLogger(VentaDiaria.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void actuCaja() {
        try {
            Conexion mysql = new Conexion();
            ResultSet rs = mysql.select("SELECT monto, fecha FROM compras_enca WHERE id_compra_enca = " + labelID.getText());
            rs.next();
            String fecha = rs.getString("fecha");
            String monto = rs.getString("monto");
            me.Insertar("insert into bitacora (fecha, transaccion, nombre_tabla, fk_tabla_id, monto) values('" + fecha + "', 'Egreso', 'Compra de Productos Propios', " + labelID.getText() + ", " + monto + ")");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error Actualizando caja, contacte Administrador" + ex);
            Logger.getLogger(VentaDiaria.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void Disables() {

        btneliminar.setEnabled(false);
        cmdregistrarcompra.setEnabled(false);

    }

    private void Enables() {

        btneliminar.setEnabled(true);
        cmdregistrarcompra.setEnabled(true);

    }

    /*private void PrecioCompra() {
        try {
            String prod = txtproductos.getText();
            Conexion mysql = new Conexion();
            ResultSet rs = mysql.select("select precio_costo from productos_inventario where nombre = '" + prod + "'");
            if (rs.next() == true) {
                String pc = rs.getString("precio_costo");
                txtpreciocosto.setText(pc);
            } else {
                txtpreciocosto.setText("0");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Necesita ingresar el Nombre del Producto" + ex);
        }
    }*/

    private static void Monto() {
        try {
            Conexion cn = new Conexion();
            String idenca = labelID.getText();
            String mont = "select ROUND(SUM(precio_total),2) as monto FROM compras_deta where fk_compra_enca = " + idenca;
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
        fechacompra = me.FechaFormat(datecompra.getDate()); // fecha 
        int provee = combointPROVEE[cmbprovee.getSelectedIndex()]; // proveedor
        monto = txtmonto.getText(); //monto
        factura = txtfactura.getText();
        idenca = labelID.getText();

        String updateENCAsql = "update COMPRAS_ENCA set fk_provee = " + provee + ", fecha = '" + fechacompra + "', monto = " + monto
                + ", num_factura = '" + factura + "', estado = 'REGISTRADA' where id_compra_enca =" + idenca;
        me.Insertar(updateENCAsql);
        JOptionPane.showMessageDialog(null, "Compra ID: " + idenca + " fue Registrada");

    }

    void InsertarDeta() {
        JTextField[] texts = {txtproductos, txtcantidadcompra};
        if (!me.validartextbox(texts)) {
            JOptionPane.showMessageDialog(null, "Necesita ingresar el Nombre y Cantidad del Producto");
        } else {
            try {
                Conexion mysql = new Conexion();
                Connection cn = mysql.Conectar();
                String prod = txtproductos.getText();
                ResultSet rs = mysql.select("select id_prod from productos_inventario where nombre = '" + prod + "'");
                if (rs.next()) {
                    int idprod = rs.getInt("id_prod");
                    double cant, preUni, preTot;
                    cant = Double.parseDouble(txtcantidadcompra.getText());
                    preUni = Double.parseDouble(txtpreciocosto.getText());
                    preTot = Double.parseDouble(txtvalortotalcompra.getText());
                    int idenca = Integer.parseInt(labelID.getText());

                    String sSQL = "INSERT INTO COMPRAS_DETA (fk_producto, cantidad, precio_unitario, precio_total, fk_compra_enca)"
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
                            clear();
                            CargarTabla();
                        }

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "error insertando compra sql");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "El producto no existe en la base de datos.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "error extraendo id de productos");
                Logger.getLogger(Compras.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void CargarTabla() {
        String id_enca = labelID.getText();
        me.cargartabla(tbcompras, "select c.id_compra_deta,  @i := @i + 1 as '#', p.codigo as CODIGO, p.nombre as PRODUCTO,  m.nombre_marca as MARCA,  c.precio_unitario as 'PRECIO UNITARIO', c.cantidad as CANTIDAD, ROUND((precio_unitario * cantidad),2) as TOTAL  from compras_deta as c, productos_inventario as p, marca_prod as m, (select @i := 0) as id where c.fk_producto = p.id_prod AND p.fk_marca = m.id_marca AND fk_compra_enca =" + id_enca);
        Monto();
    }

     
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cmbprovee = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        txtproductos = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btnnuevoproducto = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtcantidadcompra = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbcompras = new javax.swing.JTable();
        btnadicionar = new javax.swing.JButton();
        btneliminar = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtfactura = new javax.swing.JTextField();
        cmdregistrarcompra = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        btnSalirCompras = new javax.swing.JButton();
        datecompra = new com.toedter.calendar.JDateChooser();
        txtpreciocosto = new javax.swing.JFormattedTextField();
        txtvalortotalcompra = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        btncancelarregistro = new javax.swing.JButton();
        labelID = new javax.swing.JLabel();
        txtmonto = new javax.swing.JFormattedTextField();
        labelestado = new javax.swing.JLabel();
        txtcodigo = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Fecha:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("INGRESO DE COMPRAS PRODUCTOS PROPIOS");

        jLabel3.setText("Proveedor");

        cmbprovee.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Añadir Producto");

        txtproductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtproductosActionPerformed(evt);
            }
        });
        txtproductos.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtproductosFocusGained(evt);
            }
        });
        txtproductos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtproductosKeyTyped(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Precio Unitario:");

        btnnuevoproducto.setText("+");
        btnnuevoproducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnnuevoproductoActionPerformed(evt);
            }
        });

        jLabel6.setText("Valor Total:");

        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Cantidad");

        txtcantidadcompra.setBackground(new java.awt.Color(0, 255, 204));
        txtcantidadcompra.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtcantidadcompra.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtcantidadcompra.setNextFocusableComponent(txtvalortotalcompra);
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

        tbcompras.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbcompras.setFocusable(false);
        tbcompras = new javax.swing.JTable() {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                if (colIndex == 0 || colIndex==1 || colIndex==2 || colIndex==3 || colIndex==6) {
                    return false;
                }
                return true;
            }
        };
        jScrollPane1.setViewportView(tbcompras);

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

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel8.setText("TOTAL COMPRA:");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Numero Factura: ");

        cmdregistrarcompra.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdregistrarcompra.setText("<html>REGISTRAR<br>COMPRA</html>");
        cmdregistrarcompra.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdregistrarcompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdregistrarcompraActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Eras Demi ITC", 0, 14)); // NOI18N
        jLabel10.setText("Codigo");

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
        txtpreciocosto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtpreciocostoActionPerformed(evt);
            }
        });
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

        txtvalortotalcompra.setEditable(false);
        txtvalortotalcompra.setBackground(new java.awt.Color(153, 255, 255));
        txtvalortotalcompra.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtvalortotalcompra.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtvalortotalcompra.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtvalortotalcompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtvalortotalcompraActionPerformed(evt);
            }
        });
        txtvalortotalcompra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtvalortotalcompraKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtvalortotalcompraKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtvalortotalcompraKeyTyped(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel11.setText("ID COMPRA:");

        btncancelarregistro.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btncancelarregistro.setText("<html>CANCELAR<BR> Registro de Compra</html>");
        btncancelarregistro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncancelarregistroActionPerformed(evt);
            }
        });

        labelID.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        labelID.setText("ID");

        txtmonto.setEditable(false);
        txtmonto.setBackground(new java.awt.Color(153, 255, 255));
        txtmonto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtmonto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtmonto.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        labelestado.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        labelestado.setText("ESTADO");

        jLabel12.setFont(new java.awt.Font("Eras Demi ITC", 0, 14)); // NOI18N
        jLabel12.setText("Nombre del Producto");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(txtcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtproductos, javax.swing.GroupLayout.PREFERRED_SIZE, 471, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(txtpreciocosto, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jLabel10)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel5)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                            .addComponent(jLabel7)
                            .addGap(82, 82, 82))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addComponent(btncancelarregistro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(labelestado, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(labelID, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(106, 157, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(677, 677, 677)
                                .addComponent(txtcantidadcompra, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(datecompra, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnnuevoproducto, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cmbprovee, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 668, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(30, 30, 30)
                                        .addComponent(jLabel6))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(btnSalirCompras, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(btnadicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btneliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(cmdregistrarcompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(txtvalortotalcompra, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtfactura, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(89, 89, 89)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtmonto, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(281, 281, 281)
                    .addComponent(jLabel12)
                    .addContainerGap(411, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btncancelarregistro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(btnnuevoproducto))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(labelID)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel1)
                                    .addComponent(datecompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(cmbprovee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(labelestado))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel7)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtcodigo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtproductos, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtcantidadcompra, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtpreciocosto, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(66, 66, 66)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtvalortotalcompra, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(82, 82, 82)
                                .addComponent(btnadicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btneliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(48, 48, 48)
                                .addComponent(cmdregistrarcompra, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel9)
                                    .addComponent(txtfactura, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(5, 5, 5))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtmonto, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnSalirCompras, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(23, 23, 23))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(133, 133, 133)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(567, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmdregistrarcompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdregistrarcompraActionPerformed
        if (tbcompras.getRowCount() != 0) {
            if (datecompra.getDate() == null) {
                JOptionPane.showMessageDialog(null, "Debe ingresar una Fecha");
            } else {
                RegistrarCompra_encaCompra();
                actuInventario();
                actuCaja();
                cargar();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Debe ingresar al menos un Producto");
        }
    }//GEN-LAST:event_cmdregistrarcompraActionPerformed

    private void btnSalirComprasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirComprasActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnSalirComprasActionPerformed

    private void txtproductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtproductosActionPerformed
    }//GEN-LAST:event_txtproductosActionPerformed

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

    private void txtcantidadcompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcantidadcompraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcantidadcompraActionPerformed

    private void txtcantidadcompraInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtcantidadcompraInputMethodTextChanged
    }//GEN-LAST:event_txtcantidadcompraInputMethodTextChanged

    private void txtpreciocostoInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtpreciocostoInputMethodTextChanged
    }//GEN-LAST:event_txtpreciocostoInputMethodTextChanged

    private void txtcantidadcompraFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcantidadcompraFocusLost
        ValorTotal();
    }//GEN-LAST:event_txtcantidadcompraFocusLost

    private void txtpreciocostoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtpreciocostoFocusGained
        if (!txtproductos.getText().isEmpty()) {
            me.PrecioCompra(txtproductos, txtpreciocosto);
        }
    }//GEN-LAST:event_txtpreciocostoFocusGained

    private void txtpreciocostoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtpreciocostoFocusLost
    }//GEN-LAST:event_txtpreciocostoFocusLost

    private void btnadicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnadicionarActionPerformed
        InsertarDeta();
    }//GEN-LAST:event_btnadicionarActionPerformed

    private void btncancelarregistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncancelarregistroActionPerformed
        me.Insertar("DELETE FROM COMPRAS_DETA where fk_compra_enca =" + labelID.getText());
        clear();
        CargarTabla();
    }//GEN-LAST:event_btncancelarregistroActionPerformed

    private void txtpreciocostoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtpreciocostoKeyTyped
        me.SNum(evt, txtpreciocosto.getText());
    }//GEN-LAST:event_txtpreciocostoKeyTyped

    private void txtcantidadcompraKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcantidadcompraKeyTyped
        me.SNum(evt, txtcantidadcompra.getText());
    }//GEN-LAST:event_txtcantidadcompraKeyTyped

    private void txtvalortotalcompraKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtvalortotalcompraKeyTyped
        me.SNum(evt, txtvalortotalcompra.getText());
    }//GEN-LAST:event_txtvalortotalcompraKeyTyped

    private void btneliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btneliminarActionPerformed
        me.eliminar(tbcompras, "compras_deta", "id_compra_deta");
        CargarTabla();
    }//GEN-LAST:event_btneliminarActionPerformed

    private void txtpreciocostoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtpreciocostoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtpreciocostoActionPerformed

    private void txtvalortotalcompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtvalortotalcompraActionPerformed

    }//GEN-LAST:event_txtvalortotalcompraActionPerformed

    private void txtvalortotalcompraKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtvalortotalcompraKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            InsertarDeta();
        }
    }//GEN-LAST:event_txtvalortotalcompraKeyPressed

    private void txtproductosKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtproductosKeyTyped

    }//GEN-LAST:event_txtproductosKeyTyped

    private void txtvalortotalcompraKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtvalortotalcompraKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtvalortotalcompraKeyReleased

    private void txtproductosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtproductosFocusGained
        if (!txtcodigo.getText().isEmpty()) {
            me.NombreSugerido(txtcodigo, txtproductos);
        }
    }//GEN-LAST:event_txtproductosFocusGained

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
            java.util.logging.Logger.getLogger(Compras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Compras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Compras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Compras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Compras dialog = new Compras(new javax.swing.JDialog(), true);
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
    private com.toedter.calendar.JDateChooser datecompra;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private static javax.swing.JLabel labelID;
    private javax.swing.JLabel labelestado;
    private static javax.swing.JTable tbcompras;
    private javax.swing.JTextField txtcantidadcompra;
    private javax.swing.JTextField txtcodigo;
    private javax.swing.JTextField txtfactura;
    private static javax.swing.JFormattedTextField txtmonto;
    private javax.swing.JFormattedTextField txtpreciocosto;
    public static javax.swing.JTextField txtproductos;
    private javax.swing.JFormattedTextField txtvalortotalcompra;
    // End of variables declaration//GEN-END:variables
}
