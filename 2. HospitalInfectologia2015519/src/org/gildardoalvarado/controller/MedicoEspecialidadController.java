
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javax.swing.JOptionPane;
import org.gildardoalvarado.DB.Conexion;
import org.gildardoalvarado.bean.Especialidad;
import org.gildardoalvarado.bean.Horarios;
import org.gildardoalvarado.bean.Medico;
import org.gildardoalvarado.bean.MedicoEspecialidad;
import org.gildardoalvarado.system.Principal;

public class MedicoEspecialidadController implements Initializable{

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoMedico.setItems(getMedicos());
        cmbCodigoHorario.setItems(getHorarios());
        cmbCodigoEspecialidad.setItems(getEspecialidad());
    }
    
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,CANCELAR,ACTUALIZAR,EDITAR,BUSCAR,REPORTE,NINGUNO}
    operaciones tipoDeOperacion = operaciones.NINGUNO;
    private Principal escenarioPrincipal;
    
    private ObservableList<MedicoEspecialidad> listaMedicoEspecialidad;
    private ObservableList<Medico> listaMedico;
    public ObservableList<Horarios> listaHorario;
    private ObservableList<Especialidad> listaEspecialidades;
    
    @FXML private ComboBox cmbCodigoMedico;
    @FXML private ComboBox cmbCodigoHorario;
    @FXML private ComboBox cmbCodigoEspecialidad;
    @FXML private TableView tblMedicoEspecialidad;
    @FXML private TableColumn colEspecialidadMedico;
    @FXML private TableColumn colCodigoMedico;
    @FXML private TableColumn colCodigoHorario;
    @FXML private TableColumn colCodigoEspecialidad;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnReporte;
    @FXML private Button btnEditar;
    
    public void cargarDatos(){
        tblMedicoEspecialidad.setItems(getMedicoEspecidalidad());
        colEspecialidadMedico.setCellValueFactory(new PropertyValueFactory<MedicoEspecialidad, Integer>("codigoMedicoEspecialidad"));
        colCodigoMedico.setCellValueFactory(new PropertyValueFactory<MedicoEspecialidad, Integer>("codigoMedico"));
        colCodigoHorario.setCellValueFactory(new PropertyValueFactory<MedicoEspecialidad, Integer>("codigoHorario"));
        colCodigoEspecialidad.setCellValueFactory(new PropertyValueFactory<MedicoEspecialidad, Integer>("codigoEspecialidad"));
    }
    
    public void seleccionarElemento(){
        if(tipoDeOperacion != operaciones.GUARDAR){
            if(tblMedicoEspecialidad.getSelectionModel().getSelectedItem() != null){
                cmbCodigoMedico.getSelectionModel().select(buscarMedico(((MedicoEspecialidad)tblMedicoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoMedico()));
                cmbCodigoHorario.getSelectionModel().select(buscarHorario(((MedicoEspecialidad)tblMedicoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoHorario()));
                cmbCodigoEspecialidad.getSelectionModel().select(buscarEspecialidad(((MedicoEspecialidad)tblMedicoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoEspecialidad()));
          }else{
               JOptionPane.showMessageDialog(null,"Seleccione un registro");
           }
        }else{
            JOptionPane.showMessageDialog(null,"No puede seleccionar un registro cuando se guarda otro registro");
            tblMedicoEspecialidad.getSelectionModel().clearSelection();

        }
    }
    
    public ObservableList<MedicoEspecialidad> getMedicoEspecidalidad(){
        ArrayList<MedicoEspecialidad> lista = new ArrayList<MedicoEspecialidad>();
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EnlistarMedicoEspecialidad()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new MedicoEspecialidad (resultado.getInt("codigoMedicoEspecialidad"),
                                                  resultado.getInt("codigoMedico"),
                                                  resultado.getInt("codigoEspecialidad"),
                                                  resultado.getInt("codigoHorario")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaMedicoEspecialidad = FXCollections.observableList(lista);
    }
    
    public MedicoEspecialidad buscarMedicoEspecialidad(int codigoMedicoEspecialidad){
        MedicoEspecialidad resultado = null;
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("call sp_BuscarMedicoEspecialidad(?)");
            procedimiento.setInt(1, codigoMedicoEspecialidad);
            ResultSet registro = procedimiento.executeQuery();
            
            while(registro.next()){
                resultado = new MedicoEspecialidad(registro.getInt("codigoMedicoEspecialidad"),
                                                   registro.getInt("codigoMedico"),
                                                   registro.getInt("codigoEspecialidad"),
                                                   registro.getInt("codigoHorario"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
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
            
    public ObservableList<Medico> getMedicos(){
        ArrayList<Medico> lista = new ArrayList<Medico>();
        
        try{
        
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EnlistarMedicos}");
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
    
    public ObservableList<Horarios> getHorarios(){
        ArrayList<Horarios> lista = new ArrayList<Horarios>();
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("call sp_EnlistarHorarios()");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new Horarios(resultado.getInt("codigoHorario"),
                                       resultado.getString("horarioInicio"),
                                       resultado.getString("horarioSalida"),
                                       resultado.getBoolean("lunes"),
                                       resultado.getBoolean("martes"),
                                       resultado.getBoolean("miercoles"),
                                       resultado.getBoolean("jueves"),
                                       resultado.getBoolean("viernes")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaHorario = FXCollections.observableList(lista);
    }
    
    public Horarios buscarHorario(int codigoHorario){
        Horarios registro = null;
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("call sp_BuscarHorario(?)");
            procedimiento.setInt(1, codigoHorario);
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                registro = new Horarios(resultado.getInt("codigoHorario"),
                                        resultado.getString("horarioInicio"),
                                        resultado.getString("horarioSalida"),
                                        resultado.getBoolean("lunes"),
                                        resultado.getBoolean("martes"),
                                        resultado.getBoolean("miercoles"),
                                        resultado.getBoolean("jueves"),
                                        resultado.getBoolean("viernes"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return registro;
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
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                cmbCodigoMedico.setDisable(false);
                cmbCodigoHorario.setDisable(false);
                cmbCodigoEspecialidad.setDisable(false);
                tblMedicoEspecialidad.getSelectionModel().clearSelection();
                tipoDeOperacion = operaciones.GUARDAR;
            break;
                
            case GUARDAR:
                if(cmbCodigoMedico.getSelectionModel().getSelectedItem() != null && cmbCodigoHorario.getSelectionModel().getSelectedItem() != null && cmbCodigoEspecialidad.getSelectionModel().getSelectedItem() != null){//|| txtLicenciaMedica.getText() != null){
                    guardar();
                    desactivarControles();
                    limpiarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    cmbCodigoMedico.setDisable(true);
                    cmbCodigoHorario.setDisable(true);
                    cmbCodigoEspecialidad.setDisable(true);
                    tipoDeOperacion = operaciones.NINGUNO;
                    cargarDatos();
                    break;
                }else{
                    JOptionPane.showMessageDialog(null, "No se han llenado todos los registros");
                }
        }
    }
    
    public void guardar(){
        MedicoEspecialidad registro = new MedicoEspecialidad();
        
        registro.setCodigoMedico(((Medico)cmbCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
        registro.setCodigoHorario(((Horarios)cmbCodigoHorario.getSelectionModel().getSelectedItem()).getCodigoHorario());
        registro.setCodigoEspecialidad(((Especialidad)cmbCodigoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoEspecialidad());
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarMedicoEspecialidad(?,?,?)}");
            procedimiento.setInt(1, registro.getCodigoMedico());
            procedimiento.setInt(3, registro.getCodigoEspecialidad());
            procedimiento.setInt(2, registro.getCodigoHorario());
            procedimiento.execute();
            cargarDatos();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void eliminar(){
    if(tipoDeOperacion == operaciones.GUARDAR)
        tipoDeOperacion = operaciones.CANCELAR;
    
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblMedicoEspecialidad.getSelectionModel().getSelectedItem() != null){
                    int resultado = JOptionPane.showConfirmDialog(null, "¿Está seguro que desea eliminar este registro?", "Eliminar Registro", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(resultado == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("call sp_BuscarMedicoEspecialidad(?)");
                            procedimiento.setInt(1,((MedicoEspecialidad)tblMedicoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad());
                            procedimiento.execute();
                            listaMedicoEspecialidad.remove(tblMedicoEspecialidad.getSelectionModel().getSelectedIndex());
                            cargarDatos();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "No se a seleccionado ningun registro", "Error", JOptionPane.WARNING_MESSAGE);
                }
            break;
                
            case CANCELAR:
                cancelar();
            break;
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
            activarControles();
            btnNuevo.setDisable(true);
            btnEliminar.setDisable(true);
            btnEditar.setText("Actualizar");
            btnReporte.setText("Cancelar");
            tipoDeOperacion = operaciones.ACTUALIZAR;
            break;
                
            case ACTUALIZAR:
            if(cmbCodigoMedico.getSelectionModel().getSelectedItem() != null && cmbCodigoHorario.getSelectionModel().getSelectedItem() != null && cmbCodigoEspecialidad.getSelectionModel().getSelectedItem() != null){
                actualizar();
                desactivarControles();
                limpiarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tipoDeOperacion = operaciones.NINGUNO;
                break;
            }else{
                    JOptionPane.showMessageDialog(null, "No se han llenado todos los registros");
            }
        }
    }
   
    public void actualizar(){
        MedicoEspecialidad registro = (MedicoEspecialidad)tblMedicoEspecialidad.getSelectionModel().getSelectedItem();
        registro.setCodigoMedico(((Medico)cmbCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
        registro.setCodigoHorario(((Horarios)cmbCodigoHorario.getSelectionModel().getSelectedItem()).getCodigoHorario());
        registro.setCodigoEspecialidad(((Especialidad)cmbCodigoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoEspecialidad());
        registro.setCodigoMedicoEspecialidad(((MedicoEspecialidad)tblMedicoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad());
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarMedicoEspecialidad(?,?,?,?)}");
            procedimiento.setInt(1, registro.getCodigoMedico());
            procedimiento.setInt(2, registro.getCodigoEspecialidad());
            procedimiento.setInt(3, registro.getCodigoHorario());
            procedimiento.setInt(4, registro.getCodigoMedicoEspecialidad());
            procedimiento.execute();
            cargarDatos();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void reporte(){
        if(tipoDeOperacion == operaciones.ACTUALIZAR)
            tipoDeOperacion = operaciones.CANCELAR;
        
        switch(tipoDeOperacion){
            case NINGUNO:
                
            break;
                
            case CANCELAR:
            cancelar();
            break;
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
        cmbCodigoMedico.getSelectionModel().select(null);
        cmbCodigoHorario.getSelectionModel().select(null);
        cmbCodigoEspecialidad.getSelectionModel().select(null);
        tblMedicoEspecialidad.getSelectionModel().clearSelection();
        tipoDeOperacion = operaciones.NINGUNO;
    }
    
    public void limpiarControles(){
        cmbCodigoMedico.getSelectionModel().select(null);
        cmbCodigoHorario.getSelectionModel().select(null);
        cmbCodigoEspecialidad.getSelectionModel().select(null);
        tblMedicoEspecialidad.getSelectionModel().clearSelection();
    }
    
    public void desactivarControles(){
        cmbCodigoMedico.setDisable(true);
        cmbCodigoHorario.setDisable(true);
        cmbCodigoEspecialidad.setDisable(true);
    }
    
    public void activarControles(){
        cmbCodigoMedico.setDisable(false);
        cmbCodigoHorario.setDisable(false);
        cmbCodigoEspecialidad.setDisable(false);
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
