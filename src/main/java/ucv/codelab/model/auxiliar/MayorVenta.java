package ucv.codelab.model.auxiliar;

/**
 * Clase usada unicamente para mostrar en la interfaz.
 * NO REALIZA CAMBIOS EN LA BASE DE DATOS
 */
public class MayorVenta {

    // Datos de la orden
    private String nombreProducto;
    private int cantidadVendida;
    private double montoVendido;

    public MayorVenta(String nombreProducto, int cantidadVendida, double montoVendido) {
        this.nombreProducto = nombreProducto;
        this.cantidadVendida = cantidadVendida;
        this.montoVendido = montoVendido;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public double getMontoVendido() {
        return montoVendido;
    }

}
