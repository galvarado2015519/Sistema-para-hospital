
package org.gildardoalvarado.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javax.swing.JOptionPane;
import org.gildardoalvarado.DB.Conexion;
import org.gildardoalvarado.bean.Cargos;
import org.gildardoalvarado.system.Principal;


public class CargosController implements Initializable{
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,CANCELAR,EDITAR,ACTUALIZAR,REPORTE,NINGUNO}
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    
    @FXML private TextField txtNombreCargo;
    @FXML private TableView tblCargo;
    @FXML private TableColumn colCodigoCargo;
    @FXML private TableColumn colNombreCargo;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    private ObservableList<Cargos> listaCargos;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
         cargarDatos();
    }
    
    public void cargarDatos(){
        tblCargo.setItems(getCargos());
        colCodigoCargo.setCellValueFactory(new PropertyValueFactory<Cargos,Integer>("codigoCargo"));
        colNombreCargo.setCellValueFactory(new PropertyValueFactory<Cargos,String>("nombreCargo"));
    }
    
    public void seleccionarElemento(){
        if(tipoDeOperacion != operaciones.GUARDAR){
            if(tblCargo.getSelectionModel().getSelectedItem() != null){
                txtNombreCargo.setText(((Cargos)tblCargo.getSelectionModel().getSelectedItem()).getNombreCargo());
            }else{
                JOptionPane.showMessageDialog(null,"No ha seleccionado un registro");
            }
        }else{
            JOptionPane.showMessageDialog(null,"No se puede seleccionar un registro cuando se guarda otro");
            tblCargo.getSelectionModel().clearSelection();
        }
    }
    
    public ObservableList<Cargos> getCargos(){
        ArrayList<Cargos> lista = new ArrayList<Cargos>();
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EnlistarCargos()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new Cargos(resultado.getInt("codigoCargo"),
                                     resultado.getString("nombreCargo")));
                
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaCargos = FXCollections.observableList(lista);
    }
    
    public Cargos buscarCargos(int codigoCargo){
        Cargos resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("call sp_BuscarCargo(?)");
            procedimiento.setInt(1, codigoCargo);
            ResultSet registro = procedimiento.executeQuery();
            
            while(registro.next()){
                resultado = new Cargos(registro.getInt("codigoCargo"),
                                       registro.getString("nombreCargo"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
     
    public void nuevo(){
        switch(tipoDeOperacion){
            
            case NINGUNO:
                tblCargo.getSelectionModel().clearSelection();
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;    
            
            break;
                
            case GUARDAR:
                if(!txtNombreCargo.getText().equals("")){
                    guardar();
                    desactivarControles();
                    limpiarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    cargarDatos();
                    break; 
                }else{
                    JOptionPane.showMessageDialog(null, "No se han llenado todos los registros");
                }   
        }
    }
    
    public void eliminar(){
        
        if(tipoDeOperacion == operaciones.GUARDAR)
            tipoDeOperacion = operaciones.CANCELAR;
        
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblCargo.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Eliminar Registro","¿Seguro que quiere eliminar el registro?",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION)
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarCargo(?)}");
                            procedimiento.setInt(1,((Cargos)tblCargo.getSelectionModel().getSelectedItem()).getCodigoCargo());
                            procedimiento.execute();
                            listaCargos.remove(tblCargo.getSelectionModel().getSelectedIndex());
                            cargarDatos();
                            limpiarControles();
                            
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                }else{
                    JOptionPane.showMessageDialog(null, "No ha seleccionado ningun registro");
                }
            break;
             
            case CANCELAR:
                cancelar();
        }
    
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarCargo(?,?)}");
            Cargos registro  = (Cargos)tblCargo.getSelectionModel().getSelectedItem();
            
            registro.setNombreCargo(txtNombreCargo.getText());
            registro.setCodigoCargo(((Cargos)tblCargo.getSelectionModel().getSelectedItem()).getCodigoCargo());
            
            
            procedimiento.setString(1,registro.getNombreCargo());
            procedimiento.setInt(2,registro.getCodigoCargo());
            procedimiento.execute();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblCargo.getSelectionModel().getSelectedItem() != null){
                    activarControles();
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnReporte.setText("Cancelar");
                    btnEditar.setText("Actualizar");
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else{
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento");
                }
            break;
            
            case ACTUALIZAR:
                if(!txtNombreCargo.getText().equals("")){
                    actualizar();
                    desactivarControles();
                    limpiarControles();
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);
                    btnReporte.setText("Reporte");
                    btnEditar.setText("Editar");
                    tipoDeOperacion = operaciones.NINGUNO;
                    cargarDatos();
                }else{
                    JOptionPane.showMessageDialog(null, "No se han llenado todos los registros");
                }  
                break;
        }
    }
    
    public void reporte(){
        if(tipoDeOperacion == operaciones.ACTUALIZAR){
            tipoDeOperacion = operaciones.CANCELAR;
        }
        switch(tipoDeOperacion){
            case NINGUNO:

            break;

            case CANCELAR:
                cancelar();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tipoDeOperacion = operaciones.NINGUNO;
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                limpiarControles();
                tblCargo.getSelectionModel().clearSelection();
                tipoDeOperacion = operaciones.NINGUNO;
            break;
            }
    }
    
    public void guardar(){
        Cargos registro = new Cargos();
        registro.setNombreCargo(txtNombreCargo.getText());
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarCargo(?)}");
                procedimiento.setString(1,registro.getNombreCargo());

                procedimiento.execute();
                listaCargos.add(registro);

            }catch(Exception e){
                e.printStackTrace();
            }
       
    }
    
    public void cancelar(){
        limpiarControles();
        desactivarControles();
        btnNuevo.setText("Nuevo");
        btnEliminar.setText("Eliminar");
        btnEditar.setText("Editar");
        btnReporte.setText("Reporte");
        btnNuevo.setDisable(false);
        btnEliminar.setDisable(false);
        btnEditar.setDisable(false);
        btnReporte.setDisable(false);
        tblCargo.getSelectionModel().clearSelection();
        tipoDeOperacion = operaciones.NINGUNO;
    }
    
    public void activarControles(){
        txtNombreCargo.setEditable(true);
    }
    
    public void desactivarControles(){
        txtNombreCargo.setEditable(false);
        
    }
    
    public void ValidacionLetras(KeyEvent validacion){
        
        String numero = validacion.getCharacter();
        if(!"qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM ".contains(numero)){
            validacion.consume();
        }
    }
    
    public void limpiarControles(){
        txtNombreCargo.setText("");
        
    }
        
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
}
