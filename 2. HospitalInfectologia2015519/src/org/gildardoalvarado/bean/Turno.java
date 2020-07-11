
package org.gildardoalvarado.bean;


public class Turno {
    private int codigoTurno;	
    private String fechaTurno;
    private String fechaCita;
    private float valorCita; 
    private int codigoMedicoEspecialidad;
    private int codigoPaciente;
    private int codigoResponsableTurno;

    public Turno() {
    }

    public Turno(int codigoTurno, String fechaTurno, String fechaCita, float valorCita, int codigoMedicoEspecialidad, int codigoPaciente, int codigoResponsableTurno) {
        this.codigoTurno = codigoTurno;
        this.fechaTurno = fechaTurno;
        this.fechaCita = fechaCita;
        this.valorCita = valorCita;
        this.codigoMedicoEspecialidad = codigoMedicoEspecialidad;
        this.codigoPaciente = codigoPaciente;
        this.codigoResponsableTurno = codigoResponsableTurno;
    }

    public int getCodigoTurno() {
        return codigoTurno;
    }

    public void setCodigoTurno(int codigoTurno) {
        this.codigoTurno = codigoTurno;
    }

    public String getFechaTurno() {
        return fechaTurno;
    }

    public void setFechaTurno(String fechaTurno) {
        this.fechaTurno = fechaTurno;
    }

    public String getFechaCita() {
        return fechaCita;
    }

    public void setFechaCita(String fechaCita) {
        this.fechaCita = fechaCita;
    }

    public float getValorCita() {
        return valorCita;
    }

    public void setValorCita(float valorCita) {
        this.valorCita = valorCita;
    }

    public int getCodigoMedicoEspecialidad() {
        return codigoMedicoEspecialidad;
    }

    public void setCodigoMedicoEspecialidad(int codigoMedicoEspecialidad) {
        this.codigoMedicoEspecialidad = codigoMedicoEspecialidad;
    }

    public int getCodigoPaciente() {
        return codigoPaciente;
    }

    public void setCodigoPaciente(int codigoPaciente) {
        this.codigoPaciente = codigoPaciente;
    }

    public int getCodigoResponsableTurno() {
        return codigoResponsableTurno;
    }

    public void setCodigoResponsableTurno(int codigoResponsableTurno) {
        this.codigoResponsableTurno = codigoResponsableTurno;
    }

        
}
