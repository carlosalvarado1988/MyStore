/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mystore;

/*import java.awt.event.KeyEvent;
 import java.sql.*;
 import java.util.Date;
 import java.text.SimpleDateFormat;
 import javax.swing.DefaultComboBoxModel;
 import javax.swing.JOptionPane;
 import javax.swing.table.*;*/
import com.mxrck.autocompleter.TextAutoCompleter;
import java.awt.Color;
//import java.awt.event.ActionEvent;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.awt.event.KeyEvent;
//import java.io.File;
import java.sql.*;
import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.table.*;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
//import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
//import javax.swing.event.TableModelEvent;
//import javax.swing.event.TableModelListener;
//import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Daniel
 */
public class metodos {

    public DefaultTableModel Consulta(String SQL) {
        DefaultTableModel mod = new DefaultTableModel();
        Conexion con = new Conexion();
        ResultSet rs = con.select(SQL);
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int cc = 1; cc < rsmd.getColumnCount() + 1; cc++) {
                mod.addColumn(rsmd.getColumnLabel(cc));
            }
            while (rs.next()) {
                Object[] fila = new Object[rsmd.getColumnCount()];
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    fila[i] = rs.getObject(i + 1);
                }
                mod.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }

        return mod;
    }

    
    public int[] comboarray(String SQL, int col) {
        Conexion con = new Conexion();
        ResultSet rs = con.select(SQL);
        int rows;
        rows = 0;
        try {
            while (rs.next()) {
                rows = rs.getRow();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        int[] ar = new int[rows];
        try {
            rs.beforeFirst();
            while (rs.next()) {
                ar[rs.getRow() - 1] = rs.getInt(col);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return ar;
    }

    public String FechaFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String d = String.valueOf(sdf.format(date));
        return d;
    }

    public DefaultComboBoxModel combo(String SQL, int col) {
        DefaultComboBoxModel mod = new DefaultComboBoxModel();
        Conexion con = new Conexion();
        ResultSet rs = con.select(SQL);
        try {
            while (rs.next()) {
                mod.addElement(rs.getString(col));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return mod;
    }

    public double Redondear(double numero) {
        return Math.rint(numero * 100) / 100;
    }

    public void Insertar(String SQL) {
        Conexion mysql = new Conexion();
        Connection cn = mysql.Conectar();
        try {
            Statement sttm = cn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sttm.executeUpdate(SQL);
            cn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);

        }

    }

    void consumesp(KeyEvent evt) {
        char c = evt.getKeyChar();
        if (Character.isWhitespace(c)) {
            evt.consume();
        }
    }

    public void SNum(KeyEvent e, String txt) {
        char c = e.getKeyChar();
        String b = Character.toString(c);
        if (Character.isDigit(c) || b.equals(".")) {
            if (b.equals(".") && txt.contains(".")) {
                e.consume();
            }
        } else {
            e.consume();
        }
    }

    //////////////////////////////  nuevos metodos /////////////////////////////////////
    public void cargartablacombos(JTable tabla, String SQL, int[] arraycolumnascombo, ArrayList<ComboBoxModel> combos) {
        //La sentencia SQL debe llevar el id como primera columna, esta se oculta visualmente pero puede llamarse desde el modelo
        //Columnas en tabla empiezan desde 0

        DefaultTableModel dtm;
        dtm = Consulta(SQL);
        tabla.setModel(dtm);
        String temp;
        ArrayList<JComboBox> combombos = new ArrayList();
        for (int ccc = 0; ccc < combos.size(); ccc++) {
            JComboBox com = new JComboBox();
            DefaultComboBoxModel mod = new DefaultComboBoxModel();
            for (int cccc = 0; cccc < combos.get(ccc).getSize(); cccc++) {
                temp = combos.get(ccc).getElementAt(cccc).toString();
                mod.addElement(temp);
            }
            com.setModel(mod);
            combombos.add(com);
        }
        for (int c = 0; c < tabla.getRowCount(); c++) {
            for (int cc = 0; cc < arraycolumnascombo.length; cc++) {
                TableColumn columna = tabla.getColumnModel().getColumn(arraycolumnascombo[cc]);
                JComboBox combombo = combombos.get(cc);
                Object index = dtm.getValueAt(c, arraycolumnascombo[cc]);
                combombo.setSelectedItem(index);
                columna.setCellEditor(new DefaultCellEditor(combombo));
            }

        }
        TableColumn tcolremover = tabla.getColumnModel().getColumn(0);
        tabla.getColumnModel().removeColumn(tcolremover);
        tabla.getTableHeader().setReorderingAllowed(false);

    }

    public void cargartabla(JTable tabla, String SQL) {
        //La sentencia SQL debe llevar el id como primera columna, esta se oculta visualmente pero puede llamarse desde el modelo
        //Columnas en tabla empiezan desde 0
        tabla.setModel(Consulta(SQL));
        TableColumn tcolremover = tabla.getColumnModel().getColumn(0);
        tabla.getColumnModel().removeColumn(tcolremover);
        tabla.getTableHeader().setReorderingAllowed(false);
    }

    public int[] cargarcombo(JComboBox combo, String SQL, int colarray, int colmodelo) {
        //columnas en resultset empiezan desde 1
        combo.setModel(combo(SQL, colmodelo));
        return comboarray(SQL, colarray);
    }

    public boolean validartextbox(JTextComponent[] textbox) {
        boolean boo = true;
        try {
            for (JTextComponent textbox1 : textbox) {
                if (textbox1.getText().isEmpty()) {
                    textbox1.setBorder(BorderFactory.createLineBorder(Color.decode("0xFF0000")));
                    boo = false;
                } else {
                    textbox1.setBorder(UIManager.getBorder("TextField.border"));
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, e);
            boo = false;
        }
        return boo;
    }

    public void eliminar(JTable tabla, String nombretabla, String campoid) {
        int[] campos = tabla.getSelectedRows();
        TableModel dtm = tabla.getModel();
        if (tabla.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione alguna fila");
        } else {
            StringBuilder SQL = new StringBuilder();
            SQL.append("DELETE FROM ");
            SQL.append(nombretabla);
            SQL.append(" WHERE ");
            SQL.append(campoid);
            SQL.append(" IN (");
            for (int camp : campos) {
                SQL.append(dtm.getValueAt(camp, 0));
                SQL.append(", ");
            }
            SQL.delete(SQL.length() - 2, SQL.length());
            SQL.append(")");
            String SQLfinal = SQL.toString();
            if (JOptionPane.showConfirmDialog(null, "¿Desea eliminar el registro?", "Alerta", JOptionPane.YES_NO_OPTION) == 0) {
                Insertar(SQLfinal);
            }
        }
    }

    public boolean validartabla(JTable tabla, TableCellListener tcl, boolean numerico) {
        String regex = "\\d*\\.?\\d*";

        String str = tabla.getModel().getValueAt(tcl.getRow(), tcl.getColumn()).toString();
        if (numerico) {
            if (str.matches(regex) && !str.isEmpty()) {
                return true;
            }
        } else {
            if (!str.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void Autocomplet(JTextField nombre, String campo) {
        TextAutoCompleter textAutoCompleter = new TextAutoCompleter(nombre);
        textAutoCompleter.setCaseSensitive(false); // no es sensible a mayusculas
        textAutoCompleter.setMode(0); // busqueda infijo
        Conexion mysql = new Conexion();
        
        
        try {
            if (campo=="nombre"){
                ResultSet rs = mysql.select("select nombre from productos_inventario where visible = 1");
               while (rs.next()) {
                textAutoCompleter.addItem(rs.getString("nombre"));
               }
            } else {
               ResultSet rs = mysql.select("select codigo from productos_inventario where visible = 1");
            while (rs.next()) {
                textAutoCompleter.addItem(rs.getString("codigo"));
                       }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    public boolean existe(String SQL) {
        Conexion cn = new Conexion();
        boolean boo = true;
        try {
            ResultSet rs = cn.select(SQL);
            boo = rs.next();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return boo;
    }
    
    public void NombreSugerido(JTextField codigo, JTextField nombre) {
        try {
            String cod = codigo.getText();
            Conexion mysql = new Conexion();
            ResultSet rs = mysql.select("select nombre from productos_inventario where codigo = '" + cod + "'");

            if (rs.next() == true) {
                String ps = rs.getString("nombre");
                nombre.setText(ps);
            } else {
               
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Necesita ingresar Codigo valido");

        }
    }
     public void PrecioCompra(JTextField nombre, JTextField precio) {
        try {
            String prod = nombre.getText();
            Conexion mysql = new Conexion();
            ResultSet rs = mysql.select("select precio_costo from productos_inventario where nombre = '" + prod + "'");
            if (rs.next() == true) {
                String pc = rs.getString("precio_costo");
                precio.setText(pc);
            } else {
                precio.setText("0");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Necesita ingresar el Nombre del Producto" + ex);
        }
    }

      public void PrecioSugerido(JTextField nombre, JTextField precio) {
        try {
            String prod = nombre.getText();
            Conexion mysql = new Conexion();
            ResultSet rs = mysql.select("select precio_sugerido from productos_inventario where nombre = '" + prod + "'");

            if (rs.next() == true) {
                String ps = rs.getString("precio_sugerido");
                precio.setText(ps);
            } else {
                precio.setText("0");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Necesita ingresar el Nombre del Producto de la lista");

        }
    }
}

/*  public void exportar(JTable tabla, String reqid, String total, String totalnoiva, String totaliva){
 JFileChooser chooser = new JFileChooser();
 FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de excel", "xls");
 chooser.setFileFilter(filter);
 chooser.setDialogTitle("Guardar Archivo");
 chooser.setMultiSelectionEnabled(false);
 chooser.setAcceptAllFileFilterUsed(false);

 if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
 List<JTable> tb = new ArrayList<>();
 List<String> nom = new ArrayList<>();
 tb.add(tabla);
 nom.add("Requerimiento " +RequerimientoForm.cmbreq.getSelectedItem().toString());
 String file = chooser.getSelectedFile().toString().concat(".xls");

 try {
 requerimiento.Exportar e = new Exportar();
 e.Exporter(new File(file), tb, nom);
 if (totalnoiva == null && totaliva == null){
 if (e.export(Double.parseDouble(total), null, null)) {
 JOptionPane.showMessageDialog(null, "Los datos fueron exportados a Excel.", "Hiperferretera", JOptionPane.INFORMATION_MESSAGE);
 }
 } else{
 if (e.export(Double.parseDouble(total), Double.parseDouble(totalnoiva), Double.parseDouble(totaliva))) {
 JOptionPane.showMessageDialog(null, "Los datos fueron exportados a Excel.", "Hiperferretera", JOptionPane.INFORMATION_MESSAGE);
 }
 }
 } catch (Exception ex) {
 JOptionPane.showMessageDialog(null, ex, "Hiperferretera", JOptionPane.INFORMATION_MESSAGE);
 JOptionPane.showMessageDialog(null, "Hubo un error", "Hiperferretera", JOptionPane.INFORMATION_MESSAGE);
 }
 }
 }
 }
 */
