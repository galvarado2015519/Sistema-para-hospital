
package org.gildardoalvarado.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.gildardoalvarado.system.Principal;


public class MenuPrincipalController implements Initializable {
    private Principal escenarioPrincipal;

   
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
     public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    public void ventanaPacientes(){
        escenarioPrincipal.ventanaPacientes();
    }
    public void ventanaMedicos(){
        escenarioPrincipal.ventanaMedicos();
    }
    public void ventanaProgramador(){
        escenarioPrincipal.ventanaProgramador();
    }
    public void ventanaEspecialidades(){
        escenarioPrincipal.ventanaEspecialidades();
    }
    public void ventanaReporteCitas(){
        escenarioPrincipal.ventanaReporteCitas();
    }
    public void ventanaCargos(){
        escenarioPrincipal.ventanaCargos();
    }
    public void ventanaAreas(){
        escenarioPrincipal.ventanaAreas();  
    }
    public void ventanaTelefonosMedico(){
        escenarioPrincipal.ventanaTelefonosMedico();
    }
    public void ventanaTurno(){
        escenarioPrincipal.ventanaTurno();
    }
    public void ventanaHorarios(){
        escenarioPrincipal.ventanaHorarios();
    }
    public void ventanaResponsableTurno(){
        escenarioPrincipal.ventanaResponsableTurno();
    }
    public void ventanaMedicoEspecialidad(){
        escenarioPrincipal.ventanaMedicoEspecialidad();
    }
    public void ventanaReporteFinal(){
        escenarioPrincipal.ventanaReporteFinal();
    }
}
