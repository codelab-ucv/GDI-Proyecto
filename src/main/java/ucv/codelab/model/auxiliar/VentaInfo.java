package ucv.codelab.model.auxiliar;

import java.time.LocalDate;

/**
 * Clase usada unicamente para mostrar en la interfaz.
 * NO REALIZA CAMBIOS EN LA BASE DE DATOS
 */
public class VentaInfo {
    private int idOrden;
    private String nombreCliente;
    private String nombreTrabajador;
    private LocalDate fechaOrden;

    public VentaInfo(int idOrden, String nombreCliente, String nombreTrabajador, LocalDate fechaOrden) {
        this.idOrden = idOrden;
        this.nombreCliente = nombreCliente;
        this.nombreTrabajador = nombreTrabajador;
        this.fechaOrden = fechaOrden;
    }

    public int getIdOrden() {
        return idOrden;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public String getNombreTrabajador() {
        return nombreTrabajador;
    }

    public LocalDate getFechaOrden() {
        return fechaOrden;
    }
}