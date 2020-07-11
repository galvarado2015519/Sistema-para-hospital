
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
import org.gildardoalvarado.bean.ContactoUrgencia;
import org.gildardoalvarado.bean.Paciente;
import org.gildardoalvarado.system.Principal;


public class ContactoUrgenciaController implements Initializable{
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,CANCELAR,EDITAR,ACTUALIZAR,REPORTE,NINGUNO}
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtNumeroContacto;
    
    @FXML private TableView tblNumeroContacto;
    @FXML private TableColumn colCodigoContacto;
    @FXML private TableColumn colCodigoPaciente;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colNumeroContacto;
    
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @FXML private ComboBox cmbCodigoPaciente;
    
    
    private ObservableList<ContactoUrgencia> listaContactoUrgencia;
    private ObservableList<Paciente> listaPaciente;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoPaciente.setItems(getPaciente());
    }
    
    public void seleccionarElemento(){
        if(tipoDeOperacion != operaciones.GUARDAR){
            if(tblNumeroContacto.getSelectionModel().getSelectedItem() != null){
                txtNombres.setText(((ContactoUrgencia)tblNumeroContacto.getSelectionModel().getSelectedItem()).getNombres());
                txtApellidos.setText(((ContactoUrgencia)tblNumeroContacto.getSelectionModel().getSelectedItem()).getApellidos());
                txtNumeroContacto.setText(((ContactoUrgencia)tblNumeroContacto.getSelectionModel().getSelectedItem()).getNumeroContacto());
                cmbCodigoPaciente.getSelectionModel().select(buscarPaciente(((ContactoUrgencia)tblNumeroContacto.getSelectionModel().getSelectedItem()).getCodigoPaciente()));
            }else{
                JOptionPane.showMessageDialog(null,"No ha seleccionado ningun registro");
            }
        }else{
            JOptionPane.showMessageDialog(null,"No puede seleccionar un registro cuando se guarda otro registro");       
            tblNumeroContacto.getSelectionModel().clearSelection();
        }
    }
    
    public void cargarDatos(){
        tblNumeroContacto.setItems(getContactoUrgencia());
        colCodigoContacto.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia,Integer>("codigoContactoUrgencia"));
        colNombres.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia,String>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia,String>("apellidos"));
        colNumeroContacto.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia,String>("numeroContacto"));
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia,String>("codigoPaciente"));
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
    
    public ObservableList<ContactoUrgencia> getContactoUrgencia(){
    
        ArrayList<ContactoUrgencia> lista = new ArrayList<ContactoUrgencia>();
        
        try{
            
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EnlistarContactoUrgencia()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()){
                lista.add(new ContactoUrgencia(resultado.getInt("codigoContactoUrgencia"),
                                               resultado.getString("nombres"),
                                               resultado.getString("apellidos"),
                                               resultado.getString("numeroContacto"),
                                               resultado.getInt("codigoPaciente")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaContactoUrgencia = FXCollections.observableList(lista);
    }
    
    public void nuevo(){
        
        switch(tipoDeOperacion){
            case NINGUNO:
                tblNumeroContacto.getSelectionModel().clearSelection();
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                cmbCodigoPaciente.setDisable(false);
                tipoDeOperacion = operaciones.GUARDAR;
            break;
                
            case GUARDAR:
                if(!txtNombres.getText().equals("") && !txtApellidos.getText().equals("") && !txtNumeroContacto.getText().equals("") && cmbCodigoPaciente.getSelectionModel().getSelectedItem() != null){
                    guardar();
                    desactivarControles();
                    limpiarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    cmbCodigoPaciente.setDisable(true);
                    tipoDeOperacion = operaciones.NINGUNO;
                    cargarDatos();
                    break;
                }else{
                    JOptionPane.showMessageDialog(null, "No se han llenado todos los registros");
                }   
        }
    }
    
    public void guardar(){
        ContactoUrgencia registro = new ContactoUrgencia();
            registro.setNombres(txtNombres.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setNumeroContacto(txtNumeroContacto.getText());
            registro.setCodigoPaciente(((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());

            try{

                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarContactoUrgencia(?,?,?,?)}");
                procedimiento.setString(1, registro.getNombres());
                procedimiento.setString(2, registro.getApellidos());
                procedimiento.setString(3, registro.getNumeroContacto());
                procedimiento.setInt(4,registro.getCodigoPaciente());
                procedimiento.execute();
                listaContactoUrgencia.add(registro);

            }catch(Exception e){
                e.printStackTrace();
            }
    }
       
    public void eliminar(){
    if(tipoDeOperacion == operaciones.GUARDAR)
        tipoDeOperacion = operaciones.CANCELAR;
    
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblNumeroContacto.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "Eliminar Registro","¿Seguro que desea eliminar este registro?",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarContactoUrgencia(?)}");
                            procedimiento.setInt(1,((ContactoUrgencia)tblNumeroContacto.getSelectionModel().getSelectedItem()).getCodigoContactoUrgencia());
                            procedimiento.execute();
                            listaContactoUrgencia.remove(tblNumeroContacto.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Seleccione un registro");
                }
            break;
                
            case CANCELAR:
                cancelar();
            break;
        }
    }
     
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarContactoUrgencia(?,?,?,?,?)}");
            ContactoUrgencia registro = (ContactoUrgencia)tblNumeroContacto.getSelectionModel().getSelectedItem();
            
            registro.setNombres(txtNombres.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setNumeroContacto(txtNumeroContacto.getText());
            registro.setCodigoPaciente(((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
            
            procedimiento.setString(1,registro.getNombres());
            procedimiento.setString(2,registro.getApellidos());
            procedimiento.setString(3,registro.getNumeroContacto());
            procedimiento.setInt(4,registro.getCodigoPaciente());
            procedimiento.setInt(5,((ContactoUrgencia)tblNumeroContacto.getSelectionModel().getSelectedItem()).getCodigoContactoUrgencia());
            procedimiento.execute();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblNumeroContacto.getSelectionModel().getSelectedItem() != null){
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
                if(!txtNombres.getText().equals("") && !txtApellidos.getText().equals("") && !txtNumeroContacto.getText().equals("") && cmbCodigoPaciente.getSelectionModel().getSelectedItem() != null){
                    actualizar();
                    desactivarControles();
                    limpiarControles();
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);
                    cmbCodigoPaciente.setDisable(true);
                    btnEditar.setText("Editar");
                    btnReporte.setText("Reporte");
                    cargarDatos();
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
        
    public void ValidacionLetras(KeyEvent validacion){
        
        String numero = validacion.getCharacter();
        if(!"qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM ".contains(numero)){
            validacion.consume();
        }
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
        tblNumeroContacto.getSelectionModel().clearSelection();
        tipoDeOperacion = operaciones.NINGUNO;
    }
    
    public void activarControles(){
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtNumeroContacto.setEditable(true);
    }
    
    public void desactivarControles(){
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtNumeroContacto.setEditable(false);
    }
    
    public void limpiarControles(){
        txtNombres.setText("");
        txtApellidos.setText("");
        txtNumeroContacto.setText("");
        cmbCodigoPaciente.getSelectionModel().select(null);
        cmbCodigoPaciente.setDisable(true);
        cargarDatos();
    }
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }
    
    public void ventanaPacientes(){
        escenarioPrincipal.ventanaPacientes();
    }
    
    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
}
