
package org.gildardoalvarado.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.gildardoalvarado.DB.Conexion;
import org.gildardoalvarado.bean.Horarios;
import org.gildardoalvarado.report.GenerarReporte;
import org.gildardoalvarado.system.Principal;


public class HorariosController implements Initializable{

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("YY-MM-DD"));
        fecha.getCalendarView().todayButtonTextProperty().set("toDay");
        fecha.getCalendarView().setShowWeeks(false);
        fecha.getStylesheets().add("/org/gildardoalvarado/resource/DatePicker.css");
        grpFechas.add(fecha, 0, 0);
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoHorario", null);
        GenerarReporte.mostrarReporte("ReporteHorario.jasper", "Reporte Horario", parametros);
    }
    
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,CANCELAR,ACTUALIZAR,BUSCAR,EDITAR,REPORTE,NINGUNO};    
    operaciones tipoDeOperacion = operaciones.NINGUNO;
    private Principal escenarioPrincipal;
    private DatePicker fecha;
    
    @FXML private TextField txtHorarioInicio;
    @FXML private TextField txtHorarioSalida;
    
    @FXML private TableView tblHorario;
    @FXML private TableColumn colCodigoHorario;
    @FXML private TableColumn colHorarioInicio;
    @FXML private TableColumn colHorarioSalida;
    @FXML private TableColumn colLunes;
    @FXML private TableColumn colMartes;
    @FXML private TableColumn colMiercoles;
    @FXML private TableColumn colJueves;
    @FXML private TableColumn colViernes;
    
    @FXML private CheckBox CckLunes;
    @FXML private CheckBox CckMartes;
    @FXML private CheckBox CckMiercoles;
    @FXML private CheckBox CckJueves;
    @FXML private CheckBox CckViernes;
    @FXML private GridPane grpFechas;
    
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    public ObservableList<Horarios> listaHorario;
    
    public void cargarDatos(){
        tblHorario.setItems(getHorarios());
        colCodigoHorario.setCellValueFactory(new PropertyValueFactory<Horarios,Integer>("codigoHorario"));
        colHorarioInicio.setCellValueFactory(new PropertyValueFactory<Horarios,String>("horarioInicio"));
        colHorarioSalida.setCellValueFactory(new PropertyValueFactory<Horarios,String>("horarioSalida"));
        colLunes.setCellValueFactory(new PropertyValueFactory<Horarios,Boolean>("lunes"));
        colMartes.setCellValueFactory(new PropertyValueFactory<Horarios,Boolean>("martes"));
        colMiercoles.setCellValueFactory(new PropertyValueFactory<Horarios,Boolean>("miercoles"));
        colJueves.setCellValueFactory(new PropertyValueFactory<Horarios,Boolean>("jueves"));
        colViernes.setCellValueFactory(new PropertyValueFactory<Horarios,Boolean>("viernes"));
    }   
    
    public void SeleccionarDatos(){
        if(tipoDeOperacion != operaciones.GUARDAR){
            if(tblHorario.getSelectionModel().getSelectedItem() != null){
                txtHorarioInicio.setText(((Horarios)tblHorario.getSelectionModel().getSelectedItem()).getHorarioInicio());
                txtHorarioSalida.setText(((Horarios)tblHorario.getSelectionModel().getSelectedItem()).getHorarioSalida());
                CckLunes.setSelected(((Horarios)tblHorario.getSelectionModel().getSelectedItem()).isLunes());
                CckMartes.setSelected(((Horarios)tblHorario.getSelectionModel().getSelectedItem()).isMartes());
                CckMiercoles.setSelected(((Horarios)tblHorario.getSelectionModel().getSelectedItem()).isMiercoles());
                CckJueves.setSelected(((Horarios)tblHorario.getSelectionModel().getSelectedItem()).isJueves());
                CckViernes.setSelected(((Horarios)tblHorario.getSelectionModel().getSelectedItem()).isViernes());
            }else{
                JOptionPane.showMessageDialog(null, "Seleccionar un registro", "Debe seleccionar un registro", JOptionPane.WARNING_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null,"Seleccione un registro");
            tblHorario.getSelectionModel().clearSelection();
        }
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
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblHorario.getSelectionModel().clearSelection();
                tipoDeOperacion = operaciones.GUARDAR;
                break;
                    
            case GUARDAR:
                if(!txtHorarioInicio.getText().equals("") && !txtHorarioSalida.getText().equals("")){
                    if(CckLunes.isSelected() != false || CckMartes.isSelected() != false  || CckMiercoles.isSelected()!= false  || CckJueves.isSelected() != false  || CckViernes.isSelected() != false){
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
                        JOptionPane.showMessageDialog(null, "Tiene que asignar un dia de la semana para proseguir");
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "No se han llenado todos los registros");
                }
        }
    }
    
    public void guardar(){
       Horarios registro = new Horarios();
       registro.setHorarioInicio(txtHorarioInicio.getText());
       registro.setHorarioSalida(txtHorarioSalida.getText());
       registro.setLunes(CckLunes.isSelected());
       registro.setMartes(CckMartes.isSelected());
       registro.setMiercoles(CckMiercoles.isSelected());
       registro.setJueves(CckJueves.isSelected());
       registro.setViernes(CckViernes.isSelected());
       
       try{
           PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarHorario(?,?,?,?,?,?,?)}");
           procedimiento.setString(1, registro.getHorarioInicio());
           procedimiento.setString(2, registro.getHorarioSalida());
           procedimiento.setBoolean(3, registro.isLunes());
           procedimiento.setBoolean(4, registro.isMartes());
           procedimiento.setBoolean(5, registro.isMiercoles());
           procedimiento.setBoolean(6, registro.isJueves());
           procedimiento.setBoolean(7, registro.isViernes());
           procedimiento.execute();
           
           listaHorario.add(registro);
       }catch(Exception e){
           e.printStackTrace();
       }
        
    }
    
    public void eliminar(){
        if(tipoDeOperacion == operaciones.GUARDAR)
            tipoDeOperacion = operaciones.CANCELAR;
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblHorario.getSelectionModel().getSelectedItem() != null){
                    int resultado = JOptionPane.showConfirmDialog(null, "Eliminar un registro", "¿Está seguro de querer eliminar el registro?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(resultado == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("call sp_EliminarHorario(?)");
                            procedimiento.setInt(1,((Horarios)tblHorario.getSelectionModel().getSelectedItem()).getCodigoHorario());
                            procedimiento.execute();
                            listaHorario.remove(tblHorario.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "No ha seleccionado ningun Registro para eliminar");
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
                if(tblHorario.getSelectionModel().getSelectedItem() != null){
                activarControles();
                btnNuevo.setDisable(true);
                btnEliminar.setDisable(true);
                btnEditar.setText("Actualizar");
                btnReporte.setText("Cancelar");
                tipoDeOperacion = operaciones.ACTUALIZAR;
                }else{
                    JOptionPane.showMessageDialog(null, "Seleccione un registro","Debe Seleccionar un registro ",JOptionPane.WARNING_MESSAGE);
                }
            break;
            
            case ACTUALIZAR:
                if(!txtHorarioInicio.getText().equals("") && !txtHorarioSalida.getText().equals("")){
                    actualizar();
                    limpiarControles();
                    desactivarControles();
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
        try{
            Horarios registro = (Horarios)tblHorario.getSelectionModel().getSelectedItem();
            registro.setHorarioInicio(txtHorarioInicio.getText());
            registro.setHorarioSalida(txtHorarioSalida.getText());
            registro.setLunes(CckLunes.isSelected());
            registro.setMartes(CckMartes.isSelected());
            registro.setMiercoles(CckMiercoles.isSelected());
            registro.setJueves(CckJueves.isSelected());
            registro.setViernes(CckViernes.isSelected());
            
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarHorario(?,?,?,?,?,?,?,?)}");
            procedimiento.setString(1,registro.getHorarioInicio());
            procedimiento.setString(2,registro.getHorarioSalida());
            procedimiento.setBoolean(3, registro.isLunes());
            procedimiento.setBoolean(4, registro.isMartes());
            procedimiento.setBoolean(5, registro.isMiercoles());
            procedimiento.setBoolean(6, registro.isJueves());
            procedimiento.setBoolean(7, Boolean.valueOf(registro.isViernes()));
            procedimiento.setInt(8, ((Horarios)tblHorario.getSelectionModel().getSelectedItem()).getCodigoHorario());
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
            case REPORTE:
                imprimirReporte();
                tipoDeOperacion = operaciones.NINGUNO;
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
        tblHorario.getSelectionModel().clearSelection();
        tipoDeOperacion = operaciones.NINGUNO;
    }
    
    public void activarControles(){
       txtHorarioInicio.setEditable(true);
       txtHorarioSalida.setEditable(true);
       CckLunes.setDisable(false);
       CckMartes.setDisable(false);
       CckMiercoles.setDisable(false);
       CckJueves.setDisable(false);
       CckViernes.setDisable(false);
    }
    
    public void desactivarControles(){
       
       txtHorarioInicio.setEditable(false);
       txtHorarioSalida.setEditable(false);
       CckLunes.setDisable(true);
       CckMartes.setDisable(true);
       CckMiercoles.setDisable(true);
       CckJueves.setDisable(true);
       CckViernes.setDisable(true);
    }
    
    public void limpiarControles(){
       txtHorarioInicio.setText("");
       txtHorarioSalida.setText("");
       CckLunes.setSelected(false);
       CckMartes.setSelected(false);
       CckMiercoles.setSelected(false);
       CckJueves.setSelected(false);
       CckViernes.setSelected(false);
       cargarDatos();
    }

    public void ValidacionNumeros(KeyEvent validacion){
        
        String numero = validacion.getCharacter();
        if(!"1234567890-/:".contains(numero)){
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
