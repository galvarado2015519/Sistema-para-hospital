
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javax.swing.JOptionPane;
import org.gildardoalvarado.DB.Conexion;
import org.gildardoalvarado.bean.Medico;
import org.gildardoalvarado.bean.TelefonoMedico;
import org.gildardoalvarado.system.Principal;

public class TelefonosMedicoController implements Initializable{
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();

        cmbCodigoMedico.setItems(getMedicos());
        
    }
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,BUSCAR,CANCELAR,NINGUNO};    
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    
    private ObservableList<TelefonoMedico> listaTelefonosMedico;
    private ObservableList<Medico> listaMedico;

    @FXML private TextField txtTelefonoPersonal;
    @FXML private TextField txtTelefonoTrabajo;
    
    @FXML private TableView tblTelefonoMedico;
    @FXML private TableColumn colCodigoTelefonoMedico;
    @FXML private TableColumn colCodigoMedico;
    @FXML private TableColumn colTelefonoPersonal;
    @FXML private TableColumn colTelefonoTrabajo;
    
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private ComboBox cmbCodigoMedico;
        
    public void seleccionarElemento(){
        if(tipoDeOperacion != operaciones.GUARDAR){
            if(tblTelefonoMedico.getSelectionModel().getSelectedItem() != null){    
                txtTelefonoPersonal.setText(((TelefonoMedico)tblTelefonoMedico.getSelectionModel().getSelectedItem()).getTelefonoPersonal());
                txtTelefonoTrabajo.setText(((TelefonoMedico)tblTelefonoMedico.getSelectionModel().getSelectedItem()).getTelefonoTrabajo());
                //cmbCodigoMedico.setValue(((TelefonoMedico)tblTelefonoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
                cmbCodigoMedico.getSelectionModel().select(buscarMedico(((TelefonoMedico)tblTelefonoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico()));
            }else{
                JOptionPane.showMessageDialog(null,"Seleccione un registro");
            }
        }else{
            JOptionPane.showMessageDialog(null,"No puede seleccionar un registro cuando se guarda otro registro");       
            tblTelefonoMedico.getSelectionModel().clearSelection();    
        }
    }  
        
    public ObservableList<Medico> getMedicos(){
        ArrayList<Medico> lista = new ArrayList<Medico>();
        try{
        
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EnlistarMedicos()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new Medico(resultado.getInt("codigoMedico"),
                                     resultado.getInt("licenciaMedica"),
                                     resultado.getString("nombres"),
                                     resultado.getString("apellidos"),
                                     resultado.getString("horaEntrada"),
                                     resultado.getString("horaSalida"),
                                     resultado.getInt("turnoMaximo"),
                                     resultado.getString("sexo")));
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaMedico = FXCollections.observableList(lista);
    }
    
    private ObservableList<TelefonoMedico> getTelefonosMedico(){
        ArrayList<TelefonoMedico> lista = new ArrayList<TelefonoMedico>();
        
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EnlistarTelefonosMedico()}");
                ResultSet resultado = procedimiento.executeQuery();
                
                while(resultado.next()){
                    lista.add(new TelefonoMedico(resultado.getInt("codigoTelefonoMedico"),
                                                 resultado.getString("telefonoPersonal"),
                                                 resultado.getString("telefonoTrabajo"),
                                                 resultado.getInt("codigoMedico")));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        return listaTelefonosMedico = FXCollections.observableList(lista);
    }
    
    public void guardar(){
        TelefonoMedico registro = new TelefonoMedico();
            registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
            registro.setTelefonoTrabajo(txtTelefonoTrabajo.getText());
            registro.setCodigoMedico(((Medico)cmbCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());

                try{
                    PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarTelefonoMedico (?,?,?)}");
                    procedimiento.setString(1,registro.getTelefonoPersonal());
                    procedimiento.setString(2,registro.getTelefonoTrabajo());
                    procedimiento.setInt(3,registro.getCodigoMedico());
                    procedimiento.execute();
                    listaTelefonosMedico.add(registro);

                }catch(Exception e){
                    e.printStackTrace();
                }
    }
    
       
    public void cargarDatos(){
        tblTelefonoMedico.setItems(getTelefonosMedico());
        colCodigoTelefonoMedico.setCellValueFactory(new PropertyValueFactory<TelefonoMedico,Integer>("codigoTelefonoMedico"));
        colTelefonoPersonal.setCellValueFactory(new PropertyValueFactory<TelefonoMedico,String>("telefonoPersonal"));
        colTelefonoTrabajo.setCellValueFactory(new PropertyValueFactory<TelefonoMedico,String>("telefonoTrabajo"));
        colCodigoMedico.setCellValueFactory(new PropertyValueFactory<TelefonoMedico,String>("codigoMedico"));

    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            
            case NINGUNO:
                tblTelefonoMedico.getSelectionModel().clearSelection();
                limpiarControles();
                activarControles();
                cmbCodigoMedico.setDisable(false);
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                cmbCodigoMedico.setDisable(false);
                tipoDeOperacion = operaciones.GUARDAR;
                
                break;
                
            case GUARDAR:
                if(!txtTelefonoPersonal.getText().equals("") && !txtTelefonoTrabajo.getText().equals("") && cmbCodigoMedico.getSelectionModel().getSelectedItem() != null){
                    guardar();
                    desactivarControles();
                    limpiarControles();
                    cmbCodigoMedico.setDisable(true);
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
    
    public Medico buscarMedico(int codigoMedico){
        Medico registro = null;
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarMedico(?)}");
            procedimiento.setInt(1,codigoMedico);
            ResultSet resultado = procedimiento.executeQuery();

            while(resultado.next()){
                registro = new Medico(resultado.getInt("codigoMedico"),
                                      resultado.getInt("licenciaMedica"),
                                      resultado.getString("nombres"),
                                      resultado.getString("apellidos"),
                                      resultado.getString("horaEntrada"),
                                      resultado.getString("horaSalida"),
                                      resultado.getInt("turnoMaximo"),
                                      resultado.getString("sexo"));

            }
        }catch(Exception e){
            e.printStackTrace();
            
        }
        return registro;
    }
     
    public void eliminar(){
        
        if(tipoDeOperacion == operaciones.GUARDAR)
            tipoDeOperacion = operaciones.CANCELAR;
        
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblTelefonoMedico.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Esta seguro de eliminar el registro?","Eliminar TelefonoMedico",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){    
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarMedico(?)}");
                            procedimiento.setInt(1,((TelefonoMedico)tblTelefonoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
                            procedimiento.execute();
                            listaMedico.remove(tblTelefonoMedico.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                                                        
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }               
                
                }else{
                    JOptionPane.showMessageDialog(null, "No a seleccionado ningun registro");
                } 
                cargarDatos();
                break;
                
            case CANCELAR:
                cancelar();
                
            break;
        
        }
        
    }
   
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarTelefonoMedico(?,?,?,?)}");
            TelefonoMedico registro = (TelefonoMedico)tblTelefonoMedico.getSelectionModel().getSelectedItem();
            registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
            registro.setTelefonoTrabajo(txtTelefonoTrabajo.getText());
            registro.setCodigoMedico(((Medico)cmbCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
            
            
            procedimiento.setString(1,registro.getTelefonoPersonal());
            procedimiento.setString(2,registro.getTelefonoTrabajo());
            procedimiento.setInt(3,registro.getCodigoMedico());
            procedimiento.setInt(4,((TelefonoMedico)tblTelefonoMedico.getSelectionModel().getSelectedItem()).getCodigoTelefonoMedico());

            procedimiento.execute();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
     
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO: 
                if(tblTelefonoMedico.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    activarControles();
                    btnEliminar.setDisable(true);
                    btnNuevo.setDisable(true);
                    cmbCodigoMedico.setDisable(false);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                    
                }else {
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento");
                }
                break;
            
            case ACTUALIZAR:
                if(!txtTelefonoPersonal.getText().equals("") && !txtTelefonoTrabajo.getText().equals("") && cmbCodigoMedico.getSelectionModel().getSelectedItem() != null){
                    actualizar();
                    desactivarControles();
                    btnEditar.setText("Editar");
                    btnReporte.setText("Reporte");
                    cmbCodigoMedico.setDisable(true);
                    tipoDeOperacion = operaciones.NINGUNO;
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);
                    limpiarControles();
                    cargarDatos();
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
     
    public TelefonoMedico buscarTelefonoMedico(int codigoTelefono){
        TelefonoMedico resultado = null;
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("call sp_BuscarTelefonoMedico(?)");
            procedimiento.setInt(1,codigoTelefono);
            ResultSet registro = procedimiento.executeQuery();
            
            while(registro.next()){
                resultado = new TelefonoMedico(registro.getInt("codigoTelefonoMedico"),
                                               registro.getString("telefonoPersonal"),
                                               registro.getString("telefonoTrabajo"),
                                               registro.getInt("codigoMedico"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public void ValidacionNumeros(KeyEvent validacion){
        
        String numero = validacion.getCharacter();
        if(!"1234567890".contains(numero)){
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
        tblTelefonoMedico.getSelectionModel().clearSelection();
        tipoDeOperacion = operaciones.NINGUNO;
    }
    
    public void activarControles(){
        txtTelefonoPersonal.setEditable(true);
        txtTelefonoTrabajo.setEditable(true);
    }
    
    public void desactivarControles(){
        txtTelefonoPersonal.setEditable(false);
        txtTelefonoTrabajo.setEditable(false);
        
    }
    public void limpiarControles(){
        txtTelefonoPersonal.setText("");
        txtTelefonoTrabajo.setText("");
        cmbCodigoMedico.getSelectionModel().select(null);
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
    
    public void ventanaMedicos(){
        escenarioPrincipal.ventanaMedicos();
    }
    
}
