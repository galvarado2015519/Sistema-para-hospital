
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
import org.gildardoalvarado.bean.Areas;
import org.gildardoalvarado.bean.Cargos;
import org.gildardoalvarado.bean.ResponsableTurno;
import org.gildardoalvarado.bean.TelefonoMedico;
import org.gildardoalvarado.system.Principal;


public class ResponsableTurnoController implements Initializable{

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoCargo.setItems(getCargos());
        cmbCodigoArea.setItems(getAreas());
    }
   
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,CANCELAR,EDITAR,ACTUALIZAR,BUSCAR,REPORTE,NINGUNO}
    operaciones tipoDeOperacion = operaciones.NINGUNO;
    private Principal escenarioPrincipal;
    
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @FXML private TextField txtNombreResponsable;
    @FXML private TextField txtApellidosResponsable;
    @FXML private TextField txtTelefonoPersonal;
    
    @FXML private ComboBox cmbCodigoCargo;
    @FXML private ComboBox cmbCodigoArea;
    
    @FXML private TableView tblResponsableTurno;
    @FXML private TableColumn colCodigoResponsable;
    @FXML private TableColumn colNombreResponsable;
    @FXML private TableColumn colApellidosResponsable;
    @FXML private TableColumn colTelefonoPersonal;
    @FXML private TableColumn colCodigoCargo;
    @FXML private TableColumn colCodigoArea;
    
    private ObservableList<ResponsableTurno> listaResponsableTurno;
    private ObservableList<TelefonoMedico> listaTelefonosMedico;
    private ObservableList<Areas> listaArea;
    private ObservableList<Cargos> listaCargos;
    
    public void seleccionarElementos(){
        if(tipoDeOperacion != operaciones.GUARDAR){
            if(tblResponsableTurno.getSelectionModel().getSelectedItem() != null){
                txtNombreResponsable.setText(((ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem()).getNombresResponsable());
                txtApellidosResponsable.setText(((ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem()).getApellidosResponsable());
                txtTelefonoPersonal.setText(((ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem()).getTelefonoPersonal());
                cmbCodigoCargo.getSelectionModel().select(buscarCargos(((ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem()).getCodigoCargo()));
                cmbCodigoArea.getSelectionModel().select(buscarAreas(((ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem()).getCodigoArea()));
            }else{
               JOptionPane.showMessageDialog(null,"Seleccione un registro");
            }
        }else{
            JOptionPane.showMessageDialog(null,"No puede seleccionar un registro cuando se guarda otro registro");
            tblResponsableTurno.getSelectionModel().clearSelection();
        }
    }
    
    public void cargarDatos(){
        tblResponsableTurno.setItems(getResponsableTurno());
        colCodigoResponsable.setCellValueFactory(new PropertyValueFactory<ResponsableTurno,Integer>("codigoResponsableTurno"));
        colNombreResponsable.setCellValueFactory(new PropertyValueFactory<ResponsableTurno,String>("nombresResponsable"));
        colApellidosResponsable.setCellValueFactory(new PropertyValueFactory<ResponsableTurno,String>("apellidosResponsable"));
        colTelefonoPersonal.setCellValueFactory(new PropertyValueFactory<ResponsableTurno,String>("telefonoPersonal"));
        colCodigoCargo.setCellValueFactory(new PropertyValueFactory<ResponsableTurno,Integer>("codigoCargo"));
        colCodigoArea.setCellValueFactory(new PropertyValueFactory<ResponsableTurno,Integer>("codigoArea"));
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
    
    public ResponsableTurno BuscarResponsableTurno(int codigoResponsableTurno){
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
    
    public Cargos buscarCargos(int codigoCargos){
        Cargos resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("call sp_BuscarCargo(?)");
            procedimiento.setInt(1, codigoCargos);
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
                activarControles();
                limpiarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                cmbCodigoArea.setDisable(false);
                cmbCodigoCargo.setDisable(false);
                tipoDeOperacion = operaciones.GUARDAR;
                break;
                
            case GUARDAR:
                if(!txtNombreResponsable.getText().equals("") && !txtApellidosResponsable.getText().equals("") && !txtTelefonoPersonal.getText().equals("") && cmbCodigoArea.getSelectionModel().getSelectedItem() != null && cmbCodigoCargo.getSelectionModel().getSelectedItem() != null){
                guardar();
                limpiarControles();
                desactivarControles();
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
       ResponsableTurno registro = new ResponsableTurno();
       registro.setNombresResponsable(txtNombreResponsable.getText());
       registro.setApellidosResponsable(txtApellidosResponsable.getText());
       registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
       registro.setCodigoArea(((Areas)cmbCodigoArea.getSelectionModel().getSelectedItem()).getCodigoArea());
       registro.setCodigoCargo(((Cargos)cmbCodigoCargo.getSelectionModel().getSelectedItem()).getCodigoCargo());
   
       try{
           PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarResponsableTurno(?,?,?,?,?)}");
           procedimiento.setString(1,registro.getNombresResponsable());
           procedimiento.setString(2,registro.getApellidosResponsable());
           procedimiento.setString(3,registro.getTelefonoPersonal());
           procedimiento.setInt(4,registro.getCodigoArea());
           procedimiento.setInt(5,registro.getCodigoCargo());
           procedimiento.execute();
           
           listaResponsableTurno.add(registro);
           
       }catch(Exception e){
           e.printStackTrace();
       }
        
    }
    
    public void eliminar(){
        if(tipoDeOperacion == operaciones.GUARDAR)
            tipoDeOperacion = operaciones.CANCELAR;
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblResponsableTurno.getSelectionModel().getSelectedItem() != null){
                    int numero = JOptionPane.showConfirmDialog(null, "Eliminar un registro", "¿Está seguro que desea eliminar el registro?", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(numero == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("call sp_EliminarResponsableTurno(?)");
                            procedimiento.setInt(1,((ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno());
                            procedimiento.execute();
                            listaResponsableTurno.remove(tblResponsableTurno.getSelectionModel().getSelectedIndex());
                            cargarDatos();
                            limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "No se ha seleccionado ningun registro");
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
                if(tblResponsableTurno.getSelectionModel().getSelectedItem() != null){
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
                if(!txtNombreResponsable.getText().equals("") && !txtApellidosResponsable.getText().equals("") && !txtTelefonoPersonal.getText().equals("") && cmbCodigoArea.getSelectionModel().getSelectedItem() != null && cmbCodigoCargo.getSelectionModel().getSelectedItem() != null){
                    actualizar();
                    desactivarControles();
                    limpiarControles();
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);
                    btnReporte.setText("Reporte");
                    btnEditar.setText("Editar");
                    tipoDeOperacion = operaciones.NINGUNO;
            break;
                }else{
                    JOptionPane.showMessageDialog(null, "No se han llenado todos los registros");
                }
        }
    }
    
    public void actualizar(){
        ResponsableTurno registro = new ResponsableTurno();
            registro.setNombresResponsable(txtNombreResponsable.getText());
            registro.setApellidosResponsable(txtApellidosResponsable.getText());
            registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
            registro.setCodigoArea(((Areas)cmbCodigoArea.getSelectionModel().getSelectedItem()).getCodigoArea());
            registro.setCodigoCargo(((Cargos)cmbCodigoCargo.getSelectionModel().getSelectedItem()).getCodigoCargo());
        
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("call sp_EditarResponsableTurno(?,?,?,?,?,?)");
            procedimiento.setString(1,registro.getNombresResponsable());
            procedimiento.setString(2,registro.getApellidosResponsable());
            procedimiento.setString(3,registro.getTelefonoPersonal());
            procedimiento.setInt(4,registro.getCodigoArea());
            procedimiento.setInt(5,registro.getCodigoCargo());
            procedimiento.setInt(6,((ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno());
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
        cmbCodigoArea.setDisable(true);
        cmbCodigoCargo.setDisable(true);
        tblResponsableTurno.getSelectionModel().clearSelection();
        tipoDeOperacion = operaciones.NINGUNO;
    }
    
    public void activarControles(){
       txtNombreResponsable.setEditable(true);
       txtApellidosResponsable.setEditable(true);
       txtTelefonoPersonal.setEditable(true);
    }
    
    public void desactivarControles(){
       txtNombreResponsable.setEditable(false);
       txtApellidosResponsable.setEditable(false);
       txtTelefonoPersonal.setEditable(false);
    }
    
    public void limpiarControles(){
       txtNombreResponsable.setText("");
       txtApellidosResponsable.setText("");
       txtTelefonoPersonal.setText("");
       cmbCodigoArea.setDisable(true);
       cmbCodigoCargo.setDisable(true);
       cmbCodigoArea.getSelectionModel().select(null);
       cmbCodigoCargo.getSelectionModel().select(null);
       cargarDatos();
    }
    
    public void ValidacionNumeros(KeyEvent validacion){
        
        String numero = validacion.getCharacter();
        if(!"123456789".contains(numero)){
            validacion.consume();
        }
    }
    
    public void ValidacionLetras(KeyEvent validacion2){
        
        String numero = validacion2.getCharacter();
        if(!"qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM ".contains(numero)){
            validacion2.consume();
        }
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
