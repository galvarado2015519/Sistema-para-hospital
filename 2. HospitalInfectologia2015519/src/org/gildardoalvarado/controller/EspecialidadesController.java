
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
import org.gildardoalvarado.bean.Especialidad;
import org.gildardoalvarado.system.Principal;


public class EspecialidadesController implements Initializable{
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,CANCELAR,ACTUALIZAR,EDITAR,REPORTE,NINGUNO}
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    
    @FXML private TextField txtEspecialidad;
    
    @FXML private TableView tblEspecialidad;
    @FXML private TableColumn colCodigoEspecialidad;
    @FXML private TableColumn colEspecialidad;
    
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    private ObservableList<Especialidad> listaEspecialidades;
    
     @Override
    public void initialize(URL location, ResourceBundle resources) {
            cargarDatos();
    }
    
    public void cargarDatos(){
        tblEspecialidad.setItems(getEspecialidad());
        colCodigoEspecialidad.setCellValueFactory(new PropertyValueFactory<Especialidad,Integer>("codigoEspecialidad"));    
        colEspecialidad.setCellValueFactory(new PropertyValueFactory<Especialidad,String>("nombreEspecialidad"));
    }
    
    public void seleccionarElemento(){
        if(tipoDeOperacion != operaciones.GUARDAR){
            if(tblEspecialidad.getSelectionModel().getSelectedItem() != null){
                txtEspecialidad.setText(((Especialidad)tblEspecialidad.getSelectionModel().getSelectedItem()).getNombreEspecialidad());
            }else{
                JOptionPane.showMessageDialog(null,"No ha seleccionado un registro");
            }
        }else{
            JOptionPane.showMessageDialog(null,"No puede seleccionar un registro cuando se guarda otro registro");       
            tblEspecialidad.getSelectionModel().clearSelection();
        }
    }
    
    public ObservableList<Especialidad> getEspecialidad(){
        ArrayList<Especialidad> lista = new ArrayList<Especialidad>();
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EnlistarEspecialidades()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
            
                lista.add(new Especialidad(resultado.getInt("codigoEspecialidad"),
                                           resultado.getString("nombreEspecialidad")));
            }
                        
        }catch(Exception e){
            e.printStackTrace();
        }
    
        return listaEspecialidades = FXCollections.observableList(lista);
    }
   
    public Especialidad buscarEspecialidad(int codigoEspecialidad){
        Especialidad resultado = null;
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarEspecialidad(?)}");
            procedimiento.setInt(1, codigoEspecialidad);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Especialidad(registro.getInt("codigoEspecialidad"),
                                             registro.getString("nombreEspecialidad"));
            
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public void nuevo(){
        
        switch(tipoDeOperacion){
            
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                tblEspecialidad.getSelectionModel().clearSelection();
                break;
                
            case GUARDAR:
                if(!txtEspecialidad.getText().equals("")){
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
     
    public void guardar(){
        Especialidad registro = new Especialidad();
        registro.setNombreEspecialidad(txtEspecialidad.getText());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarEspecialidad(?)}");
            procedimiento.setString(1,registro.getNombreEspecialidad());
            procedimiento.execute();
            cargarDatos();
            listaEspecialidades.add(registro);
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void eliminar(){
        
        if(tipoDeOperacion == operaciones.GUARDAR)
            tipoDeOperacion = operaciones.CANCELAR;
        
        switch(tipoDeOperacion){
            
            case NINGUNO:
                if(tblEspecialidad.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Eleminar Registro", "¿Seguro que quiere eliminar el registro?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarEspecialidad(?)}");
                            procedimiento.setInt(1, ((Especialidad)tblEspecialidad.getSelectionModel().getSelectedItem()).getCodigoEspecialidad());
                            procedimiento.execute();
                            listaEspecialidades.remove(tblEspecialidad.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            cargarDatos();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }else{
                    JOptionPane.showMessageDialog(null,"No hay ningun registro seleccionado");
                    tblEspecialidad.getSelectionModel().clearSelection();
                }               ;
            break;
                
            case CANCELAR:
                cancelar();
                
            break;
        
        }
        
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarEspecialidad(?,?)}");
            Especialidad registro = (Especialidad)tblEspecialidad.getSelectionModel().getSelectedItem();
            
            registro.setNombreEspecialidad(txtEspecialidad.getText());
            registro.setCodigoEspecialidad(((Especialidad)tblEspecialidad.getSelectionModel().getSelectedItem()).getCodigoEspecialidad());
            
            procedimiento.setString(1, registro.getNombreEspecialidad());
            procedimiento.setInt(2, registro.getCodigoEspecialidad());
            procedimiento.execute();
            cargarDatos();
            
        }catch(Exception e){
            e.printStackTrace();
        }
        cargarDatos();
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblEspecialidad.getSelectionModel().getSelectedItem() != null){
                    
                    activarControles();
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else{
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento");
                }
            break;
                
            case ACTUALIZAR:
                if(!txtEspecialidad.getText().equals("")){
                    actualizar();
                    desactivarControles();
                    limpiarControles();
                    btnEditar.setText("Editar");
                    btnReporte.setText("Reporte");
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    break;
                }else{
                    JOptionPane.showMessageDialog(null, "No se han llenado todos los registros");
                }
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
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tipoDeOperacion = operaciones.NINGUNO;
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                limpiarControles();
                tblEspecialidad.getSelectionModel().clearSelection();
                tipoDeOperacion = operaciones.NINGUNO;
                cancelar();
            break;
            }
    }
        
    public void ValidacionLetras(KeyEvent validacion){
        
        String numero = validacion.getCharacter();
        if(!"qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM ".contains(numero)){
            validacion.consume();
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
        tblEspecialidad.getSelectionModel().clearSelection();
        tipoDeOperacion = operaciones.NINGUNO;
    }
    
    public void desactivarControles(){
        txtEspecialidad.setEditable(false);           
    }
    
    public void activarControles(){
        txtEspecialidad.setEditable(true);
    }
   
      
    public void limpiarControles(){
        txtEspecialidad.setText("");
        cargarDatos();
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
    