
package org.gildardoalvarado.system;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import org.gildardoalvarado.controller.AreasController;
import org.gildardoalvarado.controller.CargosController;
import org.gildardoalvarado.controller.ContactoUrgenciaController;
import org.gildardoalvarado.controller.EspecialidadesController;
import org.gildardoalvarado.controller.HorariosController;
import org.gildardoalvarado.controller.MedicoController;
import org.gildardoalvarado.controller.MedicoEspecialidadController;
import org.gildardoalvarado.controller.MenuPrincipalController;
import org.gildardoalvarado.controller.PacienteController;
import org.gildardoalvarado.controller.ProgramadorController;
import org.gildardoalvarado.controller.ReporteCitasController;
import org.gildardoalvarado.controller.ResponsableTurnoController;
import org.gildardoalvarado.controller.TelefonosMedicoController;
import org.gildardoalvarado.controller.TurnoController;
import org.gildardoalvarado.report.GenerarReporte;


public class Principal extends Application {
    private final String PAQUETE_VISTA ="/org/gildardoalvarado/view/";
    private Stage escenarioPrincipal;
    private Scene escena;
    
    @Override
    public void start(Stage escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
        escenarioPrincipal.setTitle("Hospital de Infectologia");
        escenarioPrincipal.getIcons().add(new Image("/org/gildardoalvarado/images/iconoHospital.png"));
        menuPrincipal();
        escenarioPrincipal.show();
    }
    
    public void menuPrincipal(){
        try{
            MenuPrincipalController menuPrincipal = (MenuPrincipalController)cambiarEscena("MenuPrincipalView.fxml", 600,400);
            menuPrincipal.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaMedicos(){
        try{    
            MedicoController medicoController = (MedicoController)cambiarEscena("MedicoView.fxml",750,605);
            medicoController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    public void ventanaProgramador(){
        try{
            ProgramadorController programadorController = (ProgramadorController)cambiarEscena("ProgramadorView.fxml",600,400);
            programadorController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaPacientes(){
        try{
            PacienteController pacienteController = (PacienteController)cambiarEscena("PacientesView.fxml",899,571);
            pacienteController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaEspecialidades(){
        try{
            EspecialidadesController especialidadesController = (EspecialidadesController)cambiarEscena("EspecialidadesView.fxml",635,504);
            especialidadesController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
            
    }
    
    public void ventanaCargos(){
        try{
            CargosController cargosController = (CargosController)cambiarEscena("CargosView.fxml",600,400);
            cargosController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaAreas(){
        try{
            AreasController areasController = (AreasController)cambiarEscena("AreasView.fxml",600,400);
            areasController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaContactoUrgencia(){
        try{
            ContactoUrgenciaController contactoUrgenciaController = (ContactoUrgenciaController)cambiarEscena("ContactoUrgenciaView.fxml",653,400);
            contactoUrgenciaController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaTelefonosMedico(){
        try{
            TelefonosMedicoController telefonosMedicoController = (TelefonosMedicoController)cambiarEscena("TelefonosMedicoView.fxml",653,440);
            telefonosMedicoController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaReporteCitas(){
        try{
            ReporteCitasController reporteCitasController = (ReporteCitasController) cambiarEscena("ReporteCitasControllerView.fxml",685,588);
            reporteCitasController.setEscenarioPrincipal(this);
        
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaTurno(){
        try{
            TurnoController turnoController = (TurnoController) cambiarEscena("TurnoView.fxml",777,496);
            turnoController.setEscenarioPrincipal(this);
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaHorarios(){
        try{
            HorariosController horariosController = (HorariosController) cambiarEscena("HorarioView.fxml",696,502);
            horariosController.setEscenarioPrincipal(this);
                
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaResponsableTurno(){
        try{
            ResponsableTurnoController responsableTurnoController = (ResponsableTurnoController) cambiarEscena("TurnoResponsableView.fxml",666,547);
            responsableTurnoController.setEscenarioPrincipal(this);
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaMedicoEspecialidad(){
        try{
            MedicoEspecialidadController medicoEspecialidadController = (MedicoEspecialidadController)cambiarEscena("MedicoEspecialidadView.fxml",702,504);
            medicoEspecialidadController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaReporteFinal(){
        try{
            
            String respuesta = JOptionPane.showInputDialog("Ingrese el codigo de medico para hacer su reporte");
            if(Integer.parseInt(respuesta) == JOptionPane.OK_OPTION){
            Map parametros = new HashMap();
            parametros.put("codigoMedico",null);
            GenerarReporte.mostrarReporte("ReporteFinal.jasper", "Reporte Final", parametros);
            }else{
            
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    } 
    
    public Initializable cambiarEscena(String fxml, int ancho, int alto) throws Exception{
        Initializable resultado = null;
        FXMLLoader cargadorFXML = new FXMLLoader();
        InputStream archivo = Principal.class.getResourceAsStream(PAQUETE_VISTA+fxml);
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Principal.class.getResource(PAQUETE_VISTA +fxml));
        escena = new Scene((AnchorPane)cargadorFXML.load(archivo),ancho,alto);
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.sizeToScene();
        resultado = (Initializable)cargadorFXML.getController();
        return resultado;
    }
    
    public static void main(String[] args) {
        launch(args);
    }

    
}
