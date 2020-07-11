
package org.gildardoalvarado.controller;

import Atxy2k.CustomTextField.RestrictedTextField;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import org.gildardoalvarado.bean.Medico;
import org.gildardoalvarado.report.GenerarReporte;
import org.gildardoalvarado.system.Principal;


public class MedicoController implements Initializable{

    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,BUSCAR,CANCELAR,NINGUNO};    
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    
    private ObservableList<Medico> listaMedico;
    
    @FXML private TextField txtLicenciaMedica;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtHoraDeEntrada;
    @FXML private TextField txtHoraDeSalida;
    @FXML private TextField txtTurnoMaximo;
    @FXML private TextField txtSexo;
    
    @FXML private TableView tblMedicos;
    @FXML private TableColumn colCodigoMedico;
    @FXML private TableColumn colLicenciaMedica;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colEntrada;
    @FXML private TableColumn colSalida;
    @FXML private TableColumn colTurnoMaximo;
    @FXML private TableColumn colSexo;
    
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
            
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblMedicos.setItems(getMedicos());
        colCodigoMedico.setCellValueFactory(new PropertyValueFactory<Medico,Integer>("codigoMedico"));
        colLicenciaMedica.setCellValueFactory(new PropertyValueFactory<Medico,Integer>("LicenciaMedica"));
        colNombres.setCellValueFactory(new PropertyValueFactory<Medico,String>("Nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<Medico,String>("Apellidos"));
        colEntrada.setCellValueFactory(new PropertyValueFactory<Medico,String>("horaEntrada"));
        colSalida.setCellValueFactory(new PropertyValueFactory<Medico,String>("horaSalida"));
        colTurnoMaximo.setCellValueFactory(new PropertyValueFactory<Medico,Integer>("TurnoMaximo"));
        colSexo.setCellValueFactory(new PropertyValueFactory<Medico,String>("sexo"));
    }
    
    public void seleccionarElemento(){
         if(tipoDeOperacion != operaciones.GUARDAR){
            if(tblMedicos.getSelectionModel().getSelectedItem() != null){
               txtLicenciaMedica.setText(String.valueOf(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getLicenciaMedica()));
               txtNombres.setText(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getNombres());
               txtApellidos.setText(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getApellidos());
               txtHoraDeEntrada.setText(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getHoraEntrada());
               txtHoraDeSalida.setText(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getHoraSalida());
               txtTurnoMaximo.setText(String.valueOf(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getTurnoMaximo()));
               txtSexo.setText(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getSexo());

               //String v = null;
               //tblMedicos.setSelectionModel(v);
            }else{
               JOptionPane.showMessageDialog(null,"Seleccione un registro");
           }
        }else{
            JOptionPane.showMessageDialog(null,"No puede seleccionar un registro cuando se guarda otro registro");
            tblMedicos.getSelectionModel().clearSelection();

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
    
    public void nuevo(){
        switch(tipoDeOperacion){  
            case NINGUNO:
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                break;
                
            case GUARDAR:
                if(!txtLicenciaMedica.getText().equals("") && !txtNombres.getText().equals("") && !txtApellidos.getText().equals("") && !txtHoraDeEntrada.getText().equals("") && !txtHoraDeSalida.getText().equals("") && !txtSexo.getText().equals("")){//|| txtLicenciaMedica.getText() != null){
                    if(txtLicenciaMedica.getText().length() <= 6 && txtLicenciaMedica.getText().length() == 6){
                        guardar();
                        desactivarControles();
                        limpiarControles();
                        btnNuevo.setText("Nuevo");
                        btnEliminar.setText("Eliminar");
                        btnEditar.setDisable(false);
                        btnReporte.setDisable(false);
                        tipoDeOperacion = operaciones.NINGUNO;
                        cargarDatos();
                        JOptionPane.showMessageDialog(null, "Por favor asignele el número telefonico al medico en dado caso que tenga");
                        escenarioPrincipal.ventanaTelefonosMedico();
                        break;
                    }else{
                        JOptionPane.showMessageDialog(null, "La licencia Medica tiene que contener 6 digitos");
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
                if(tblMedicos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Esta seguro de eliminar el registro?","Eliminar Medico",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){    
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarMedico(?)}");
                            procedimiento.setInt(1,((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getCodigoMedico());
                            procedimiento.execute();
                            listaMedico.remove(tblMedicos.getSelectionModel().getSelectedIndex());
                            cargarDatos();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        limpiarControles();
                    }               
                }else{
                    JOptionPane.showMessageDialog(null, "No a seleccionado ningun registro");
                }
                break;
                
            case CANCELAR:
                cancelar();
            break;
        }
    }
    
     public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarMedico(?,?,?,?,?,?,?)}");
            Medico registro = (Medico)tblMedicos.getSelectionModel().getSelectedItem();
            registro.setLicenciaMedica(Integer.parseInt(txtLicenciaMedica.getText()));
            registro.setNombres(txtNombres.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setHoraEntrada(txtHoraDeEntrada.getText());
            registro.setHoraSalida(txtHoraDeSalida.getText());
            registro.setSexo(txtSexo.getText());
            
            procedimiento.setInt(1,registro.getLicenciaMedica());
            procedimiento.setString(2,registro.getNombres());
            procedimiento.setString(3,registro.getApellidos());
            procedimiento.setString(4,registro.getHoraEntrada());
            procedimiento.setString(5,registro.getHoraSalida());
            procedimiento.setString(6,registro.getSexo());
            procedimiento.setInt(7,((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getCodigoMedico());
            procedimiento.execute();
            
        }catch(Exception e){
            e.printStackTrace();
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
        parametros.put("codigoMedico",null);
        GenerarReporte.mostrarReporte("ReporteMedicos.jasper", "Reporte de Medicos", parametros);
        
    } 
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO: 
                if(tblMedicos.getSelectionModel().getSelectedItem() != null){
                    txtLicenciaMedica.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    activarControles();
                    btnEliminar.setDisable(true);
                    btnNuevo.setDisable(true);
                    txtTurnoMaximo.setEditable(false);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                    
                }else {
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento");
                }
                break;
            
            case ACTUALIZAR:
                if(!txtLicenciaMedica.getText().equals("") && !txtNombres.getText().equals("") && !txtApellidos.getText().equals("") && !txtHoraDeEntrada.getText().equals("") && !txtHoraDeSalida.getText().equals("") && !txtSexo.getText().equals("")){//|| txtLicenciaMedica.getText() != null){
                    if(txtLicenciaMedica.getText().length() <= 6){
                        actualizar();
                        txtLicenciaMedica.setDisable(false);
                        btnEditar.setText("Editar");
                        btnReporte.setText("Reporte");
                        tipoDeOperacion = operaciones.NINGUNO;
                        btnNuevo.setDisable(false);
                        btnEliminar.setDisable(false);
                        limpiarControles();
                        cargarDatos();
                        break;
                    }else{
                        JOptionPane.showMessageDialog(null, "La licencia Medica tiene que contener 6 digitos");
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "No se han llenado todos los registros");
                }
        }
    }
    
    public void guardar(){
        Medico registro = new Medico();
        
        registro.setLicenciaMedica(Integer.parseInt(txtLicenciaMedica.getText()));
        registro.setNombres(txtNombres.getText());
        registro.setApellidos(txtApellidos.getText());
        registro.setHoraEntrada(txtHoraDeEntrada.getText());
        registro.setHoraSalida(txtHoraDeSalida.getText());
        registro.setSexo(txtSexo.getText());

        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarMedico(?,?,?,?,?,?)}");
            procedimiento.setInt(1,registro.getLicenciaMedica());
            procedimiento.setString(2,registro.getNombres());
            procedimiento.setString(3,registro.getApellidos());
            procedimiento.setString(4,registro.getHoraEntrada());
            procedimiento.setString(5,registro.getHoraSalida());
            procedimiento.setString(6,registro.getSexo());

            procedimiento.execute();

            listaMedico.add(registro);

        }catch(Exception e){
            e.printStackTrace();
        }
            
            
    }
   
    /*public static boolean soloNumeros(String validacion){
        try{
            Integer.parseInt(validacion);
            return true;
        }catch(Exception e){
            return false;
        }
        
    }*/ 
    
    public void ValidacionNumeros(KeyEvent validacion){
        
        String numero = validacion.getCharacter();
        if(!"1234567890:".contains(numero)){
            validacion.consume();
        }
    }
    
    public void ValidacionLetras(KeyEvent validacion2){
        
        String numero = validacion2.getCharacter();
        if(!"qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM ".contains(numero)){
            validacion2.consume();
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
        txtLicenciaMedica.setDisable(false);
        tblMedicos.getSelectionModel().clearSelection();
        tipoDeOperacion = operaciones.NINGUNO;
    }
    
    public void desactivarControles(){
    
        txtLicenciaMedica.setEditable(false);
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtHoraDeEntrada.setEditable(false);
        txtHoraDeSalida.setEditable(false);
        txtTurnoMaximo.setEditable(false);
        txtSexo.setEditable(false);
    }
    
    public void activarControles(){
        
        txtLicenciaMedica.setEditable(true);
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtHoraDeEntrada.setEditable(true);
        txtHoraDeSalida.setEditable(true);
        txtTurnoMaximo.setEditable(false);
        txtSexo.setEditable(true);
    } 
    
    public void limpiarControles(){
    
        txtLicenciaMedica.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtHoraDeEntrada.setText("");
        txtHoraDeSalida.setText("");
        txtTurnoMaximo.setText("");
        txtSexo.setText("");
        tblMedicos.getSelectionModel().clearSelection();
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
    
    public void ventanaTelefonosMedico(){
        escenarioPrincipal.ventanaTelefonosMedico();
    }
}
