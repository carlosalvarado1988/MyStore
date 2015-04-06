/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mystore;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

/**
 *
 * @author Daniel
 */
public class Acciones {

    // cambiar referencia de campos en las sentencias sql cuando se agregue el campO CODIGO porque seran mostrados.
    metodos mt = new metodos();
////Form IngresoProductos
    Action acciontablatipos = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            TableCellListener tcl = (TableCellListener) e.getSource();
            if (JOptionPane.showConfirmDialog(null, "¿Desea modificar el campo?", "Confirmar", JOptionPane.YES_NO_OPTION) == 0) {
                if (mt.validartabla(tcl.getTable(), tcl, false)) {
                    mt.Insertar("UPDATE tipo_prod SET nombre_tipo='" + tcl.getTable().getValueAt(tcl.getRow(), 0) + "' WHERE id_tipo=" + tcl.getTable().getModel().getValueAt(tcl.getRow(), 0));

                } else {
                    JOptionPane.showMessageDialog(null, "El campo no puede estar en blanco.");
                }
            }
            IngresoProductos.cargar("");
        }

    };

    Action acciontablamarcas = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            TableCellListener tcl = (TableCellListener) e.getSource();
            if (JOptionPane.showConfirmDialog(null, "¿Desea modificar el campo?", "Confirmar", JOptionPane.YES_NO_OPTION) == 0) {
                if (mt.validartabla(tcl.getTable(), tcl, false)) {
                    mt.Insertar("UPDATE marca_prod SET nombre_marca='" + tcl.getTable().getValueAt(tcl.getRow(), 0) + "' WHERE id_marca=" + tcl.getTable().getModel().getValueAt(tcl.getRow(), 0));

                } else {
                    JOptionPane.showMessageDialog(null, "El campo no puede estar en blanco.");
                }
            }
            IngresoProductos.cargar("");
        }

    };

    Action acciontablaproductos = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            TableCellListener tcl = (TableCellListener) e.getSource();

            if (JOptionPane.showConfirmDialog(null, "¿Desea modificar el campo?", "Confirmar", JOptionPane.YES_NO_OPTION) == 0) {
                boolean bool = false;
                if (tcl.getColumn() == 6) {
                    bool = true;
                }
                if (mt.validartabla(tcl.getTable(), tcl, bool)) {
                    mt.Insertar("UPDATE PRODUCTOS_INVENTARIO SET codigo='"+ tcl.getTable().getValueAt(tcl.getRow(), 0) +"', nombre='" + tcl.getTable().getValueAt(tcl.getRow(), 1) + "', fk_marca=(SELECT id_marca FROM marca_prod WHERE nombre_marca='" + tcl.getTable().getValueAt(tcl.getRow(), 2) + "'), fk_tipo=(SELECT id_tipo FROM tipo_prod WHERE nombre_tipo='" + tcl.getTable().getValueAt(tcl.getRow(), 3) + "'), unidad_medida='" + tcl.getTable().getValueAt(tcl.getRow(), 4) + "', precio_costo=" + tcl.getTable().getValueAt(tcl.getRow(), 5) + " , precio_sugerido="+ tcl.getTable().getValueAt(tcl.getRow(), 6) + " WHERE id_prod=" + tcl.getTable().getModel().getValueAt(tcl.getRow(), 0));
                } else if (bool == true) {
                    JOptionPane.showMessageDialog(null, "El campo debe ser numérico.");
                } else {
                    JOptionPane.showMessageDialog(null, "El campo no puede estar en blanco.");
                }
            }
            IngresoProductos.cargar("");

        }
    };

    //////Form ClientesProveedores
    Action acciontablaproveedores = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            TableCellListener tcl = (TableCellListener) e.getSource();
            if (JOptionPane.showConfirmDialog(null, "¿Desea modificar el campo?", "Confirmar", JOptionPane.YES_NO_OPTION) == 0) {
                if (mt.validartabla(tcl.getTable(), tcl, false)) {
                    mt.Insertar("UPDATE proveedor SET nombre_provee='" + tcl.getTable().getValueAt(tcl.getRow(), 1) + "', telefono='" + tcl.getTable().getValueAt(tcl.getRow(), 3) + "', direccion='" + tcl.getTable().getValueAt(tcl.getRow(), 2) + "', contacto_provee='" + tcl.getTable().getValueAt(tcl.getRow(), 4) + "' WHERE id_provee = " + tcl.getTable().getModel().getValueAt(tcl.getRow(), 0));

                } else {
                    JOptionPane.showMessageDialog(null, "El campo no puede estar en blanco.");
                }
            }
            ClientesProveedores.cargar();
        }
    };
    
       Action acciontablaclientes = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            TableCellListener tcl = (TableCellListener) e.getSource();
            if (JOptionPane.showConfirmDialog(null, "¿Desea modificar el campo?", "Confirmar", JOptionPane.YES_NO_OPTION) == 0) {
                if (mt.validartabla(tcl.getTable(), tcl, false)) {
                    mt.Insertar("UPDATE cliente SET nombre='" + tcl.getTable().getValueAt(tcl.getRow(), 1) + "', telefono='" + tcl.getTable().getValueAt(tcl.getRow(), 3) + "', direccion='" + tcl.getTable().getValueAt(tcl.getRow(), 2) + "' WHERE id_cliente = " + tcl.getTable().getModel().getValueAt(tcl.getRow(), 0));
                } else {
                    JOptionPane.showMessageDialog(null, "El campo no puede estar en blanco.");
                }
            }
            ClientesProveedores.cargar();
        }
    };

       
       /////////////Form Compras
       Action acciontablacompras = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            TableCellListener tcl = (TableCellListener) e.getSource();
            if (JOptionPane.showConfirmDialog(null, "¿Desea modificar el campo?", "Confirmar", JOptionPane.YES_NO_OPTION) == 0) {
                if (mt.validartabla(tcl.getTable(), tcl, true)) {
                    mt.Insertar("UPDATE compras_deta SET precio_unitario=" + tcl.getTable().getValueAt(tcl.getRow(), 4) + ", cantidad=" + tcl.getTable().getValueAt(tcl.getRow(), 5) + " WHERE id_compra_deta = " + tcl.getTable().getModel().getValueAt(tcl.getRow(), 0));
                } else {
                    JOptionPane.showMessageDialog(null, "El campo debe ser numérico.");
                }
            }
            Compras.CargarTabla();
            
        }
    };
       /////////////////////////////////////////////////////////////////////////////////////////////
       ////////////////////////////////////////////////////////////////////////////////////////////
       //////////////////Form VentaDiaria////////////////////////////////////////////////////////
       
       Action acciontablaventadiaria = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            TableCellListener tcl = (TableCellListener) e.getSource();
            if (JOptionPane.showConfirmDialog(null, "¿Desea modificar el campo?", "Confirmar", JOptionPane.YES_NO_OPTION) == 0) {
                if (mt.validartabla(tcl.getTable(), tcl, true)) {
                    mt.Insertar("UPDATE ventas_deta SET precio_venta=" + tcl.getTable().getValueAt(tcl.getRow(), 3) + ", cantidad=" + tcl.getTable().getValueAt(tcl.getRow(), 4) + " WHERE id_venta_deta = " + tcl.getTable().getModel().getValueAt(tcl.getRow(), 0));
                } else {
                    JOptionPane.showMessageDialog(null, "El campo debe ser numérico.");
                }
            }
            VentaDiaria.CargarTabla();
        }
    };
       ////////////////////////////////////////////////////////////////////////////////////////////////
       ///////////////////////////////////////////////////////////////////////////////////////////////
       ////////////////////////////////Form VentaCredito////////////////////////////////////////////
       //"UPDATE venta_credito_deta SET precio_venta=" + tcl.getTable().getValueAt(tcl.getRow(), 3) + ", cantidad=" + tcl.getTable().getValueAt(tcl.getRow(), 4) + " WHERE id_venta_cred_deta = " + tcl.getTable().getModel().getValueAt(tcl.getRow(), 0)
       
       Action acciontablaventacredito = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            TableCellListener tcl = (TableCellListener) e.getSource();
            if (JOptionPane.showConfirmDialog(null, "¿Desea modificar el campo?", "Confirmar", JOptionPane.YES_NO_OPTION) == 0) {
                if (mt.validartabla(tcl.getTable(), tcl, true)) {
                    mt.Insertar("UPDATE venta_credito_deta SET precio=" + tcl.getTable().getValueAt(tcl.getRow(), 3) + ", cantidad=" + tcl.getTable().getValueAt(tcl.getRow(), 4) + " WHERE id_venta_cred_deta = " + tcl.getTable().getModel().getValueAt(tcl.getRow(), 0));
                } else {
                    JOptionPane.showMessageDialog(null, "El campo debe ser numérico.");
                }
            }
            VentaCredito.CargarTabla();
        }
    };
       
        Action acciontablaconsecionados = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            TableCellListener tcl = (TableCellListener) e.getSource();
            if (JOptionPane.showConfirmDialog(null, "¿Desea modificar el campo?", "Confirmar", JOptionPane.YES_NO_OPTION) == 0) {
                if (mt.validartabla(tcl.getTable(), tcl, true)) {
                    mt.Insertar("UPDATE ingreso_prod_cons_deta SET precio_unitario=" + tcl.getTable().getValueAt(tcl.getRow(), 4) + ", cantidad=" + tcl.getTable().getValueAt(tcl.getRow(), 5) + " WHERE id_cons_deta = " + tcl.getTable().getModel().getValueAt(tcl.getRow(), 0));
                } else {
                    JOptionPane.showMessageDialog(null, "El campo debe ser numérico.");
                }
            }
            VentaCredito.CargarTabla();
        }
    };
        
         Action acciontablaconsumointerno = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            TableCellListener tcl = (TableCellListener) e.getSource();
            if (JOptionPane.showConfirmDialog(null, "¿Desea modificar el campo?", "Confirmar", JOptionPane.YES_NO_OPTION) == 0) {
                if (mt.validartabla(tcl.getTable(), tcl, true)) {
                    mt.Insertar("UPDATE consumo_interno_deta SET precio_unitario=" + tcl.getTable().getValueAt(tcl.getRow(), 4) + ", cantidad=" + tcl.getTable().getValueAt(tcl.getRow(), 5) + " WHERE id_consumo_interno_deta = " + tcl.getTable().getModel().getValueAt(tcl.getRow(), 0));
                } else {
                    JOptionPane.showMessageDialog(null, "El campo debe ser numérico.");
                }
            }
            VentaCredito.CargarTabla();
        }
    };
        
        
}       
    ////
       
    /*    Action test = new AbstractAction() {
     @Override
     public void actionPerformed(ActionEvent e) {
     TableCellListener tcl = (TableCellListener) e.getSource();
     int[] indices = tcl.getIndices();
     String[] columnas = tcl.getColumnas();
            
     String columna = "";
            
     for (int c = 0; c < indices.length; c++) {
     if (indices[c] == tcl.getColumn()) {
     columna = columnas[c];
     }
     }
     String SQL = "UPDATE " + tcl.getNombreTabla() + " SET " + columna + "='" + tcl.getTable().getModel().getValueAt(tcl.getRow(), tcl.getColumn()) + "' WHERE " + tcl.getNombreIdTabla() + " = " + tcl.getTable().getModel().getValueAt(tcl.getRow(), 0);
     mt.Insertar(SQL);
     }
     };*/

