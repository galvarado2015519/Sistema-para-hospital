
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
import org.gildardoalvarado.bean.Areas;
import org.gildardoalvarado.system.Principal;


public class AreasController implements Initializable{
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,CANCELAR,EDITAR,ACTUALIZAR,REPORTE,NINGUNO}
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    
    @FXML private TextField txtNombres;
    
    @FXML private TableView tblArea;
    @FXML private TableColumn colCodigoArea;
    @FXML private TableColumn colNombre;
    
    
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    
    private ObservableList<Areas> listaArea;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblArea.setItems(getAreas());
        colCodigoArea.setCellValueFactory(new PropertyValueFactory<Areas,Integer>("codigoArea"));
        colNombre.setCellValueFactory(new PropertyValueFactory<Areas,String>("nombreArea"));
    }
    
    public void seleccionarElemento(){
        if(tipoDeOperacion != operaciones.GUARDAR){
            if(tblArea.getSelectionModel().getSelectedItem() != null){
                txtNombres.setText(((Areas)tblArea.getSelectionModel().getSelectedItem()).getNombreArea());
            }else{
                JOptionPane.showMessageDialog(null,"No ha seleccionado un registro");
            }
        }else{
            tblArea.getSelectionModel().clearSelection();
            JOptionPane.showMessageDialog(null,"No se puede seleccionar un registro cuando se guarda otro");
        }
    }
    public Areas buscarAreas(int codigoArea){
        Areas resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("call sp_BuscarArea(?)");
            procedimiento.setInt(1, codigoArea);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Areas(registro.getInt("codigoArea"),
                                      registro.getString("nombreArea"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public ObservableList<Areas> getAreas(){
        ArrayList<Areas> lista = new ArrayList<Areas>();
        
        try{
        
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EnlistarAreas()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new Areas(resultado.getInt("codigoArea"),
                                    resultado.getString("nombreArea")));
                        
            }
        
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaArea = FXCollections.observableList(lista);
    }
            
            
    public void nuevo(){
        
        switch(tipoDeOperacion){
            
            case NINGUNO:
                tblArea.getSelectionModel().clearSelection();
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
            break;    
            
            case GUARDAR:
                if(!txtNombres.getText().equals("")){
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
                    JOptionPane.showMessageDialog(null, "Aun no se han llenado todos los registros");
                }
        }
        
    }
    
    public void eliminar(){
        if(tipoDeOperacion == operaciones.GUARDAR)
            tipoDeOperacion = operaciones.CANCELAR;
        
        switch(tipoDeOperacion){
            
            case NINGUNO:
                if(tblArea.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"Eliminar Registro","¿Seguro que desea eliminar el registro?",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarArea(?)}");
                            procedimiento.setInt(1, ((Areas)tblArea.getSelectionModel().getSelectedItem()).getCodigoArea());
                            procedimiento.execute();
                            listaArea.remove(tblArea.getSelectionModel().getSelectedItem());
                            cargarDatos();
                            
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        limpiarControles();
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Seleccione un registro");
                }
                
            break;
        
            case CANCELAR:
                cancelar();
                tipoDeOperacion = operaciones.NINGUNO;
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarArea(?,?)}");
            Areas registro = (Areas)tblArea.getSelectionModel().getSelectedItem();
            registro.setNombreArea(txtNombres.getText());
            registro.setCodigoArea(((Areas)tblArea.getSelectionModel().getSelectedItem()).getCodigoArea());
            
            procedimiento.setString(1,registro.getNombreArea());
            procedimiento.setInt(2, registro.getCodigoArea());
            procedimiento.execute();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblArea.getSelectionModel().getSelectedItem() != null){
                    activarControles();
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else{
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento");
                }
                break;
                
            case ACTUALIZAR:
                if(!txtNombres.getText().equals("")){
                    actualizar();
                    desactivarControles();
                    limpiarControles();
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);
                    btnEditar.setText("Editar");
                    btnReporte.setText("Reporte");
                    cargarDatos();
                    tipoDeOperacion = operaciones.NINGUNO;
                    break;
                }else{
                    JOptionPane.showMessageDialog(null, "Aun no se han llenado todos los registros");
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
                cancelar();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tipoDeOperacion = operaciones.NINGUNO;
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                limpiarControles();
                tipoDeOperacion = operaciones.NINGUNO;
            break;
            }
    }
    
    public void guardar(){
        Areas registro = new Areas();
            registro.setNombreArea(txtNombres.getText());

            try{

                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarArea(?)}");
                procedimiento.setString(1,registro.getNombreArea());

                procedimiento.execute();
                listaArea.add(registro);

            }catch(Exception e){
                e.printStackTrace();
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
        tblArea.getSelectionModel().clearSelection();
        tipoDeOperacion = operaciones.NINGUNO;
    }
    
    public void desactivarControles(){
        txtNombres.setEditable(false);
        
    }
    
    public void activarControles(){
        txtNombres.setEditable(true);   
    }
    
     public void limpiarControles(){
        txtNombres.setText("");
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
