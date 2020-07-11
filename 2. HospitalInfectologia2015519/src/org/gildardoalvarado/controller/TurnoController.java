
package org.gildardoalvarado.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.DepthTest;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javax.swing.JOptionPane;
import org.gildardoalvarado.DB.Conexion;
import org.gildardoalvarado.bean.Especialidad;
import org.gildardoalvarado.bean.MedicoEspecialidad;
import org.gildardoalvarado.bean.Paciente;
import org.gildardoalvarado.bean.ResponsableTurno;
import org.gildardoalvarado.bean.Turno;
import org.gildardoalvarado.system.Principal;


public class TurnoController implements Initializable{

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoEspecialidadMedico.setItems(getMedicoEspecidalidad());
        cmbCodigoPaciente.setItems(getPaciente());
        cmbResponsableTurno.setItems(getResponsableTurno());
    }
    
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,CANCELAR,EDITAR,ACTUALIZAR,BUSCAR,REPORTE,NINGUNO}
    private Principal escenarioPrincipal;
    operaciones tipoDeOperacion = operaciones.NINGUNO;

    private ObservableList<MedicoEspecialidad> listaMedicoEspecialidad;
    private ObservableList<Paciente> listaPaciente;
    private ObservableList<Turno> listaTurno;
    private ObservableList<ResponsableTurno> listaResponsableTurno;
    
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private TextField txtFechaTurno;
    @FXML private TextField txtFechaCita;
    @FXML private TextField txtValorCita;
    @FXML private ComboBox cmbCodigoEspecialidadMedico;
    @FXML private ComboBox cmbCodigoPaciente;
    @FXML private ComboBox cmbResponsableTurno;    
    @FXML private TableView tblTurno;
    @FXML private TableColumn colCodigoTurno;
    @FXML private TableColumn colFechaCita;
    @FXML private TableColumn colFechaTurno;
    @FXML private TableColumn colValorCita;
    @FXML private TableColumn colCodigoEspecialidad;
    @FXML private TableColumn colCodigoPaciente;
    @FXML private TableColumn colCodigoResponsableTurno;
    
    
    public void cargarDatos(){
        tblTurno.setItems(getTurno());
        colCodigoTurno.setCellValueFactory(new PropertyValueFactory<Turno, Integer>("codigoTurno"));
        colFechaTurno.setCellValueFactory(new PropertyValueFactory<Turno, String>("fechaTurno"));
        colFechaCita.setCellValueFactory(new PropertyValueFactory<Turno, String>("fechaCita"));
        colValorCita.setCellValueFactory(new PropertyValueFactory<Turno, Float>("valorCita"));
        colCodigoEspecialidad.setCellValueFactory(new PropertyValueFactory<Turno,Integer>("codigoMedicoEspecialidad"));
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<Turno,Integer>("codigoPaciente"));
        colCodigoResponsableTurno.setCellValueFactory(new PropertyValueFactory<Turno,Integer>("codigoResponsableTurno"));
    }
    
    public void seleccionarElemento(){
        if(tipoDeOperacion != operaciones.GUARDAR){
            if(tblTurno.getSelectionModel().getSelectedItem() != null){
                txtFechaTurno.setText(((Turno)tblTurno.getSelectionModel().getSelectedItem()).getFechaTurno());
                txtFechaCita.setText(((Turno)tblTurno.getSelectionModel().getSelectedItem()).getFechaCita());
                txtValorCita.setText(String.valueOf(((Turno)tblTurno.getSelectionModel().getSelectedItem()).getValorCita()));
                cmbCodigoEspecialidadMedico.getSelectionModel().select(buscarMedicoEspecialidad(((Turno)tblTurno.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad()));
                cmbCodigoPaciente.getSelectionModel().select(buscarPaciente(((Turno)tblTurno.getSelectionModel().getSelectedItem()).getCodigoPaciente()));
                cmbResponsableTurno.getSelectionModel().select(buscarResponsableTurno(((Turno)tblTurno.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno()));
            }else{
                JOptionPane.showMessageDialog(null, "No ha seleccionado ningun Registro especifico");
            }
        }else{
            JOptionPane.showMessageDialog(null, "No puede seleccionar un registro cuando esta en proceso de guardar otro");
            tblTurno.getSelectionModel().clearSelection();
        }
    }
    
    public ObservableList<Turno> getTurno(){
        ArrayList<Turno> lista = new ArrayList<Turno>();
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EnlistarTurnos()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new Turno(resultado.getInt("codigoTurno"),
                                    resultado.getString("fechaTurno"),
                                    resultado.getString("fechaCita"),
                                    resultado.getFloat("valorCita"),
                                    resultado.getInt("codigoMedicoEspecialidad"),
                                    resultado.getInt("codigoPaciente"),
                                    resultado.getInt("codigoResponsableTurno")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaTurno = FXCollections.observableList(lista);
    }
    
    public Turno buscarTurno(int codigoTurno){
        Turno resultado = null;
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("call sp_BuscarTurno(?)");
            ResultSet registro = procedimiento.executeQuery();
            
            while(registro.next()){
                resultado = new Turno(registro.getInt("codigoTurno"),
                                     registro.getString("fechaTurno"),
                                    registro.getString("fechaCita"),
                                    registro.getFloat("valorCita"),
                                    registro.getInt("codigoMedicoEspecialidad"),
                                    registro.getInt("codigoPaciente"),
                                    registro.getInt("codigoResponsableTurno"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
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
    
    public ObservableList<Paciente> getPaciente(){
        ArrayList<Paciente> lista = new ArrayList<Paciente>();
        
        try{
            
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EnlistarPacientes()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new Paciente(resultado.getInt("codigoPaciente"),
                                        resultado.getString("DPI"),
                                        resultado.getString("nombres"),
                                        resultado.getString("apellidos"),
                                        resultado.getDate("fechaNacimiento"),
                                        resultado.getInt("edad"),
                                        resultado.getString("direccion"),
                                        resultado.getString("ocupacion"),
                                        resultado.getString("sexo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
    
        return listaPaciente = FXCollections.observableList(lista);
    }
    
    public  Paciente buscarPaciente(int codigoPaciente){
        Paciente resultado = null;
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarPaciente(?)}");
            procedimiento.setInt(1,codigoPaciente);
            ResultSet registro = procedimiento.executeQuery();
            
            while(registro.next()){
                resultado = new Paciente(registro.getInt("codigoPaciente"),
                                         registro.getString("DPI"),
                                         registro.getString("nombres"),
                                         registro.getString("apellidos"),
                                         registro.getDate("fechaNacimiento"),
                                         registro.getInt("edad"),
                                         registro.getString("direccion"),
                                         registro.getString("ocupacion"),
                                         registro.getString("sexo"));
            }
         }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    } 
    
    public ObservableList<ResponsableTurno> getResponsableTurno(){
        ArrayList<ResponsableTurno> lista = new ArrayList<ResponsableTurno>();
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("call sp_EnlistarResponsableTurno()");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new ResponsableTurno(resultado.getInt("codigoResponsableTurno"),
                                               resultado.getString("nombresResponsable"),
                                               resultado.getString("apellidosResponsable"),
                                               resultado.getString("telefonoPersonal"),
                                               resultado.getInt("codigoArea"),
                                               resultado.getInt("codigoCargo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaResponsableTurno = FXCollections.observableList(lista);
    }
    
    public ResponsableTurno buscarResponsableTurno(int codigoResponsableTurno){
        ResponsableTurno resultado = null;
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarResponsableTurno(?)}");
            procedimiento.setInt(1, codigoResponsableTurno);
            ResultSet registro = procedimiento.executeQuery();
            
            while(registro.next()){
                resultado = new ResponsableTurno(registro.getInt("codigoResponsableTurno"),
                                                 registro.getString("nombresResponsable"),
                                                 registro.getString("apellidosResponsable"),
                                                 registro.getString("telefonoPersonal"),
                                                 registro.getInt("codigoArea"),
                                                 registro.getInt("codigoCargo"));
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
                btnReporte.setDisable(true);
                btnEditar.setDisable(true);
                cmbCodigoEspecialidadMedico.setDisable(false);
                cmbCodigoPaciente.setDisable(false);
                cmbResponsableTurno.setDisable(false); 
                tblTurno.getSelectionModel().clearSelection();
                tipoDeOperacion = operaciones.GUARDAR;
                break;
                
            case GUARDAR:
                if(!txtFechaTurno.getText().equals("")&& !txtFechaCita.getText().equals("")&& !txtValorCita.getText().equals("")&&cmbCodigoEspecialidadMedico.getSelectionModel().getSelectedItem() != null && cmbCodigoPaciente.getSelectionModel().getSelectedItem() != null && cmbResponsableTurno.getSelectionModel().getSelectedItem() != null){
                    guardar();
                    limpiarControles();
                    desactivarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    cmbCodigoEspecialidadMedico.setDisable(true);
                    cmbCodigoPaciente.setDisable(true);
                    cmbResponsableTurno.setDisable(true);
                    tipoDeOperacion = operaciones.NINGUNO;
                    cargarDatos();
                    break;
                }else{
                    JOptionPane.showMessageDialog(null, "No se han llenado todos los registros");
                }
        }
    }
    
    public void guardar(){
       Turno registro = new Turno();
       
       registro.setFechaTurno(txtFechaTurno.getText());
       registro.setFechaCita(txtFechaCita.getText());
       registro.setValorCita(Float.parseFloat(txtValorCita.getText()));
       registro.setCodigoMedicoEspecialidad(((MedicoEspecialidad)cmbCodigoEspecialidadMedico.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad());
       registro.setCodigoPaciente(((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
       registro.setCodigoResponsableTurno(((ResponsableTurno)cmbResponsableTurno.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno());
       
       try{
           PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarTurno(?,?,?,?,?,?)}");
           procedimiento.setString(1, registro.getFechaTurno());
           procedimiento.setString(2, registro.getFechaCita());
           procedimiento.setFloat(3, registro.getValorCita());
           procedimiento.setInt(4, registro.getCodigoMedicoEspecialidad());
           procedimiento.setInt(5, registro.getCodigoPaciente());
           procedimiento.setInt(6, registro.getCodigoResponsableTurno());
           procedimiento.execute();
           
       }catch(Exception e){
           e.printStackTrace();
       }
        
    }
    
    public void eliminar(){
        if(tipoDeOperacion == operaciones.GUARDAR)
            tipoDeOperacion = operaciones.CANCELAR;
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblTurno.getSelectionModel().getSelectedItem() != null){
                    int resultado = JOptionPane.showConfirmDialog(null, "¿Desea eleminar este registro?", "Eliminar Resgistro", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(resultado == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ElimiarTurno(?)}");
                            procedimiento.setInt(1, ((Turno)tblTurno.getSelectionModel().getSelectedItem()).getCodigoTurno());
                            procedimiento.execute();
                            listaTurno.remove(tblTurno.getSelectionModel().getSelectedIndex());
                            cargarDatos();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        limpiarControles();
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Aun no a seleccionado ningun registro");
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
                if(tblTurno.getSelectionModel().getSelectedItem() != null){
                    activarControles();
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    actualizar();
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                   
                }else{
                    JOptionPane.showMessageDialog(null, "No se ha seleccionado un registro");
                }
                break;
                
            case ACTUALIZAR:
                if(!txtFechaTurno.getText().equals("")&& txtFechaCita.getText().equals("")&& txtValorCita.getText().equals("")&&cmbCodigoEspecialidadMedico.getSelectionModel().getSelectedItem() != null && cmbCodigoPaciente.getSelectionModel().getSelectedItem() != null && cmbResponsableTurno.getSelectionModel().getSelectedItem() != null){
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
        Turno registro = (Turno)tblTurno.getSelectionModel().getSelectedItem();
        registro.setFechaCita(txtFechaCita.getText());
        registro.setFechaTurno(txtFechaTurno.getText());
        registro.setValorCita(Float.parseFloat(txtValorCita.getText()));
        registro.setCodigoMedicoEspecialidad(((MedicoEspecialidad)cmbCodigoEspecialidadMedico.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad());
        registro.setCodigoPaciente(((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
        registro.setCodigoResponsableTurno(((ResponsableTurno)cmbResponsableTurno.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno());
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarTurno(?,?,?,?,?,?,?)}");
            procedimiento.setString(1, registro.getFechaTurno());
            procedimiento.setString(2, registro.getFechaCita());
            procedimiento.setFloat(3, registro.getValorCita());
            procedimiento.setInt(4, registro.getCodigoMedicoEspecialidad());
            procedimiento.setInt(5, registro.getCodigoPaciente());
            procedimiento.setInt(6, registro.getCodigoResponsableTurno());
            procedimiento.setInt(7, ((Turno)tblTurno.getSelectionModel().getSelectedItem()).getCodigoTurno());
            procedimiento.execute();
            
        }catch(Exception e){
            e.printStackTrace();
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
        cmbCodigoEspecialidadMedico.getSelectionModel().select(null);
        cmbCodigoPaciente.getSelectionModel().select(null);
        cmbResponsableTurno.getSelectionModel().select(null);
        tblTurno.getSelectionModel().clearSelection();
        tipoDeOperacion = operaciones.NINGUNO;
    }
    
    public void activarControles(){
        txtFechaTurno.setEditable(true);
        txtFechaCita.setEditable(true);
        txtValorCita.setEditable(true);
        cmbCodigoEspecialidadMedico.setDisable(false);
        cmbCodigoPaciente.setDisable(false);
        cmbResponsableTurno.setDisable(false);
        
    }
    
    public void desactivarControles(){
        txtFechaTurno.setEditable(false);
        txtFechaCita.setEditable(false);
        txtValorCita.setEditable(false);
        cmbCodigoEspecialidadMedico.setDisable(true);
        cmbCodigoPaciente.setDisable(true);
        cmbResponsableTurno.setDisable(true);
    }
    
    public void limpiarControles(){
        txtFechaTurno.setText("");
        txtFechaCita.setText("");
        txtValorCita.setText("");
        cmbCodigoEspecialidadMedico.getSelectionModel().select(null);
        cmbCodigoPaciente.getSelectionModel().select(null);
        cmbResponsableTurno.getSelectionModel().select(null);
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
    public void ValidacionFecha(KeyEvent validacion){
        String numero = validacion.getCharacter();
        if(!"1234567890-:".contains(numero)){
            validacion.consume();
        }
    }
    public void ValidacionValorCita(KeyEvent validacion){
        String numero = validacion.getCharacter();
        if(!"1234567890. ".contains(numero)){
            validacion.consume();
        }
    }
    public void AvisoDeEscritura(MouseEvent validacion){
        JOptionPane.showMessageDialog(null, "1. Se recomienda escribir la fecha de forma : 'yyyy'-'mm'-'dd'"
                + "  2. Solo estaran habilitados los numeros y el signo ' - '");
    }
}

