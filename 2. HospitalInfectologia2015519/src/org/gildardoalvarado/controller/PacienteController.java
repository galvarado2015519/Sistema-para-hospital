
package org.gildardoalvarado.controller;

import java.net.URL;
import java.sql.Date;
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
import eu.schudt.javafx.controls.calendar.DatePicker;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javafx.scene.layout.GridPane;
import org.gildardoalvarado.DB.Conexion;
import org.gildardoalvarado.bean.Paciente;
import org.gildardoalvarado.report.GenerarReporte;
import org.gildardoalvarado.system.Principal;


public class PacienteController implements Initializable{
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,CANCELAR,EDITAR,ACTUALIZAR,REPORTE,NINGUNO}
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private DatePicker fecha;
  
    @FXML private TextField txtDPI;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtOcupacion;
    @FXML private TextField txtSexo;
    @FXML private TextField txtFechaNac;
    
    @FXML private TableView tblPaciente;
    @FXML private TableColumn colCodigoPaciente;
    @FXML private TableColumn colDPI;
    @FXML private TableColumn colNombre;
    @FXML private TableColumn colApellidos;    
    @FXML private TableColumn colFechaNac;
    @FXML private TableColumn colEdad;    
    @FXML private TableColumn colDireccion;
    @FXML private TableColumn colOcupacion;
    @FXML private TableColumn colSexo;
    
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @FXML private GridPane grpFecha;
    
    private ObservableList<Paciente> listaPaciente;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatos();
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyy-M-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("toDay");
        fecha.getCalendarView().setShowWeeks(false);
        fecha.getStylesheets().add("/org/gildardoalvarado/resource/DatePicker.css");
        grpFecha.add(fecha, 0, 0);
    }
    
    public void cargarDatos(){
        tblPaciente.setItems(getPaciente());
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<Paciente,Integer>("codigoPaciente"));
        colDPI.setCellValueFactory(new PropertyValueFactory<Paciente,String>("DPI"));
        colNombre.setCellValueFactory(new PropertyValueFactory<Paciente,String>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<Paciente,String>("apellidos"));
        colFechaNac.setCellValueFactory(new PropertyValueFactory<Paciente,Date>("fechaNacimiento"));
        colEdad.setCellValueFactory(new PropertyValueFactory<Paciente,Integer>("edad"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<Paciente,String>("direccion"));
        colOcupacion.setCellValueFactory(new PropertyValueFactory<Paciente,String>("Ocupacion"));
        colSexo.setCellValueFactory(new PropertyValueFactory<Paciente,String>("sexo"));
        
    }
    
    public void seleccionarElemento(){
        if(tipoDeOperacion != operaciones.GUARDAR){
            if(tblPaciente.getSelectionModel().getSelectedItem() != null){
                txtDPI.setText(((Paciente)tblPaciente.getSelectionModel().getSelectedItem()).getDPI());
                txtNombres.setText(((Paciente)tblPaciente.getSelectionModel().getSelectedItem()).getNombres());
                txtApellidos.setText(((Paciente)tblPaciente.getSelectionModel().getSelectedItem()).getApellidos());
                txtDireccion.setText(((Paciente)tblPaciente.getSelectionModel().getSelectedItem()).getDireccion());
                txtOcupacion.setText(((Paciente)tblPaciente.getSelectionModel().getSelectedItem()).getOcupacion());
                txtSexo.setText(((Paciente)tblPaciente.getSelectionModel().getSelectedItem()).getSexo());
                fecha.selectedDateProperty().set(((Paciente)tblPaciente.getSelectionModel().getSelectedItem()).getFechaNacimiento());
            }else{
                JOptionPane.showMessageDialog(null,"No ha seleccionado un registro");
            }
        }else{
            JOptionPane.showMessageDialog(null,"No puede seleccionar un registro cuando se guarda otro registro");  
            tblPaciente.getSelectionModel().clearSelection();
        }
    
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
    
    public void nuevo(){
        
        switch(tipoDeOperacion){
            
            case NINGUNO:
                tblPaciente.getSelectionModel().clearSelection();
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
            break;    
            
            case GUARDAR:
                if(!txtDPI.getText().equals("") && !txtNombres.getText().equals("") && !txtApellidos.getText().equals("") && !txtDireccion.getText().equals("") && !txtOcupacion.getText().equals("") && !txtSexo.getText().equals("") && fecha.selectedDateProperty().get() != null){
                    if(txtDPI.getText().length() == 13){
                    guardar();
                    desactivarControles();
                    limpiarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    cargarDatos();
                    }else{
                        JOptionPane.showMessageDialog(null, "El DPI tiene que contener 13 digitos");
                    }
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
                if(tblPaciente.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Esta seguro de eliminar el Paciente?","Eliminar Paciente",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                        if(respuesta == JOptionPane.YES_OPTION){
                           try{  
                               PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarPaciente(?)}");
                               procedimiento.setInt(1,((Paciente)tblPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
                               procedimiento.execute();
                               listaPaciente.remove(tblPaciente.getSelectionModel().getSelectedIndex());
                               
                           }catch(Exception e){
                                e.printStackTrace();
                           }
                        }
                        limpiarControles();
                }else{
                    JOptionPane.showMessageDialog(null,"No ha seleccionado ningun registro");
                }
                    
                                           
            break;
        
            case CANCELAR:
                cancelar();
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarPaciente(?,?,?,?,?,?,?)}");
            Paciente registro = (Paciente)tblPaciente.getSelectionModel().getSelectedItem();
            registro.setDPI(txtDPI.getText());
            registro.setNombres(txtNombres.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setDireccion(txtDireccion.getText());
            registro.setOcupacion(txtOcupacion.getText());
            registro.setSexo(txtSexo.getText());
            
            procedimiento.setString(1,registro.getDPI());
            procedimiento.setString(2,registro.getNombres());
            procedimiento.setString(3,registro.getApellidos());
            procedimiento.setString(4,registro.getDireccion());
            procedimiento.setString(5,registro.getOcupacion());
            procedimiento.setString(6,registro.getSexo());
            procedimiento.setInt(7,((Paciente)tblPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
            procedimiento.execute();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblPaciente.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    activarControles();
                    btnEliminar.setDisable(true);
                    btnNuevo.setDisable(true);
                    txtDPI.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else{
                    JOptionPane.showMessageDialog(null,"Seleccione un registro");
                }
                break;
                
            case ACTUALIZAR:
                if(!txtDPI.getText().equals("") && !txtNombres.getText().equals("") && !txtApellidos.getText().equals("") && !txtDireccion.getText().equals("") && !txtOcupacion.getText().equals("") && !txtSexo.getText().equals("") && fecha.selectedDateProperty().get() != null){
                    txtDPI.setDisable(false);
                    actualizar();
                    btnEditar.setText("Editar");
                    btnReporte.setText("Reporte");
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
    
    public void generarReporte(){
        if(tipoDeOperacion == operaciones.ACTUALIZAR){
            tipoDeOperacion = operaciones.CANCELAR;
        }
        switch(tipoDeOperacion){
            case NINGUNO:
                imprimirReporte();
                tipoDeOperacion = operaciones.NINGUNO;
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
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoPaciente",null);
        GenerarReporte.mostrarReporte("ReportePacientes.jasper", "Reporte de Paciente", parametros);
    }
    
    public void imprimirReporte2(){
        Map parametros = new HashMap();
        parametros.put("codigoHorario", null);
        GenerarReporte.mostrarReporte("ReportePacientes.jasper", "Reporte De Horario", parametros);
    }
    
    public void guardar(){
        Paciente registro = new Paciente();
            registro.setDPI(txtDPI.getText());
            registro.setNombres(txtNombres.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setFechaNacimiento(fecha.getSelectedDate());
            registro.setDireccion(txtDireccion.getText());
            registro.setOcupacion(txtOcupacion.getText());
            registro.setSexo(txtSexo.getText());

            try{

                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarPaciente(?,?,?,?,?,?,?)}");
                procedimiento.setString(1,registro.getDPI());
                procedimiento.setString(2,registro.getNombres());
                procedimiento.setString(3,registro.getApellidos());
                procedimiento.setDate(4,new java.sql.Date(registro.getFechaNacimiento().getTime()));
                procedimiento.setString(5,registro.getDireccion());
                procedimiento.setString(6,registro.getOcupacion());
                procedimiento.setString(7,registro.getSexo());

                procedimiento.execute();
                listaPaciente.add(registro);        

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
        txtDPI.setDisable(false);
        tblPaciente.getSelectionModel().clearSelection();
        tipoDeOperacion = operaciones.NINGUNO;
    }
    
    public void desactivarControles(){
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtDireccion.setEditable(false);
        txtOcupacion.setEditable(false);
        txtDPI.setEditable(false);
        txtSexo.setEditable(false);
        grpFecha.setDisable(true);
    }
    
    public void activarControles(){
        
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtDireccion.setEditable(true);
        txtOcupacion.setEditable(true);
        txtDPI.setEditable(true);
        txtSexo.setEditable(true);
        grpFecha.setDisable(false);
        
    }
    
    public void ValidacionLetras(KeyEvent validacion){
        
        String numero = validacion.getCharacter();
        if(!"qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM ".contains(numero)){
            validacion.consume();
        }
    }
    
    public void ValidacionNumeros(KeyEvent validacion){
        
        String numero = validacion.getCharacter();
        if(!"123456789".contains(numero)){
            validacion.consume();
        }
    }
    
    public void ValidacionFecha(KeyEvent validacion){
        
        String numero = validacion.getCharacter();
        if(!"123456789/-".contains(numero)){
            validacion.consume();
        }
    }
      
    public void limpiarControles(){
        txtNombres.setText("");
        txtApellidos.setText("");
        txtDireccion.setText("");
        txtOcupacion.setText("");
        txtDPI.setText("");
        txtSexo.setText("");
        fecha.selectedDateProperty().setValue(null);
    }
       
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaContactoUrgencia(){
        escenarioPrincipal.ventanaContactoUrgencia();
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
   
}
