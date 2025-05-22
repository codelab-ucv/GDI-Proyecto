package ucv.codelab.mcastillocho.model;

import java.time.LocalDate;

public class Orden {

    private int idOrden;
    private int idTrabajador;
    private int idCliente;
    private int idEmpresa;
    private LocalDate fechaOrden;

    public Orden(int idOrden, int idTrabajador, int idCliente, int idEmpresa, LocalDate fechaOrden) {
        this.idOrden = idOrden;
        this.idTrabajador = idTrabajador;
        this.idCliente = idCliente;
        this.idEmpresa = idEmpresa;
        this.fechaOrden = fechaOrden;
    }

    public int getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(int idOrden) {
        this.idOrden = idOrden;
    }

    public int getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(int idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public LocalDate getFechaOrden() {
        return fechaOrden;
    }

    public void setFechaOrden(LocalDate fechaOrden) {
        this.fechaOrden = fechaOrden;
    }
}
