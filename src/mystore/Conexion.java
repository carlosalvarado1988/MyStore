/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mystore;

import com.sun.rowset.CachedRowSetImpl;
import java.sql.*;
import javax.sql.rowset.CachedRowSet;
import javax.swing.JOptionPane;

public class Conexion {

    public String db = "mitiendita";
    public String url = "jdbc:mysql://localhost:3306/" + db;
    String user = "root";
    String pass = "";

    public Conexion() {
    }

    public Connection Conectar() {
        Connection link = null;
        try {
            Class.forName("org.gjt.mm.mysql.Driver");
            link = DriverManager.getConnection(this.url, this.user, this.pass);
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return link;
    }

    public CachedRowSet select(String sqlcad) {
        ResultSet rs;
        Connection cnx = Conectar();
        CachedRowSet crs = null;
        try {
            crs = new CachedRowSetImpl();
            Statement sttm = cnx.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = sttm.executeQuery(sqlcad);
            crs.populate(rs);
            cnx.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return crs;
    }
}
