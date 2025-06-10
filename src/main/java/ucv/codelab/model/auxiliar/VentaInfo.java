package ucv.codelab.model.auxiliar;

import java.time.LocalDate;

/**
 * Clase auxiliar de solo lectura para representar información resumida de
 * ventas.
 * 
 * <p>
 * Esta clase encapsula los datos esenciales de una transacción de venta,
 * combinando información de múltiples entidades (orden, cliente, trabajador)
 * en una vista consolidada para presentación en interfaces de usuario.
 * Se utiliza exclusivamente para mostrar datos de consultas de búsqueda
 * y análisis de ventas.
 * </p>
 * 
 * <p>
 * <strong>Propósito:</strong> Clase de solo lectura (read-only) diseñada
 * únicamente
 * para la visualización de información de ventas consolidada. NO realiza
 * modificaciones en la base de datos ni contiene lógica de negocio.
 * </p>
 * 
 * <p>
 * <strong>Información consolidada que encapsula:</strong>
 * </p>
 * <ul>
 * <li>Identificador único de la orden de venta</li>
 * <li>Nombre del cliente que realizó la compra</li>
 * <li>Nombre del trabajador que procesó la venta</li>
 * <li>Fecha en que se realizó la transacción</li>
 * </ul>
 * 
 * <p>
 * <strong>Casos de uso típicos:</strong>
 * </p>
 * <ul>
 * <li>Listados de ventas en interfaces de búsqueda</li>
 * <li>Reportes históricos de transacciones</li>
 * <li>Dashboards de seguimiento de ventas</li>
 * <li>Exportación de datos de auditoría</li>
 * <li>Interfaces de filtrado y consulta avanzada</li>
 * </ul>
 * 
 * <p>
 * Los datos de esta clase son generados típicamente por consultas SQL que
 * realizan INNER JOIN entre las tablas {@code orden}, {@code cliente} y
 * {@code trabajador} para consolidar la información de venta en una sola vista.
 * </p>
 * 
 * <p>
 * <strong>Tablas relacionadas:</strong>
 * </p>
 * <ul>
 * <li>{@code orden} - Proporciona id_orden y fecha_orden</li>
 * <li>{@code cliente} - Proporciona nombre_cliente</li>
 * <li>{@code trabajador} - Proporciona nombre_trabajador</li>
 * </ul>
 * 
 * @see ucv.codelab.service.ConsultaAvanzadaSQL#buscarVentas(Integer, String,
 *      String, java.time.LocalDate, java.time.LocalDate, int)
 * @see ucv.codelab.model.Orden
 * @see ucv.codelab.model.Cliente
 * @see ucv.codelab.model.Trabajador
 */
public class VentaInfo {

    /**
     * Identificador único de la orden de venta.
     * 
     * <p>
     * Campo de solo lectura que corresponde a la clave primaria
     * de la tabla {@code orden}. Permite identificar unívocamente
     * la transacción de venta específica.
     * </p>
     */
    private int idOrden;

    /**
     * Nombre del cliente que realizó la compra.
     * 
     * <p>
     * Campo de solo lectura que contiene el nombre completo del cliente
     * obtenido de la tabla {@code cliente}. Facilita la identificación
     * del comprador sin necesidad de realizar consultas adicionales.
     * </p>
     */
    private String nombreCliente;

    /**
     * Nombre del trabajador que procesó la venta.
     * 
     * <p>
     * Campo de solo lectura que contiene el nombre completo del empleado
     * responsable de procesar la orden, obtenido de la tabla {@code trabajador}.
     * Útil para seguimiento y análisis de desempeño de ventas por empleado.
     * </p>
     */
    private String nombreTrabajador;

    /**
     * Fecha en que se realizó la orden de venta.
     * 
     * <p>
     * Campo de solo lectura que representa la fecha de creación de la orden.
     * Utiliza {@link LocalDate} para manejo consistente de fechas sin
     * información de zona horaria.
     * </p>
     */
    private LocalDate fechaOrden;

    /**
     * Constructor que inicializa una instancia con la información consolidada de
     * venta.
     * 
     * <p>
     * Crea un objeto inmutable que encapsula los datos esenciales de una
     * transacción de venta. Una vez creado, los datos no pueden ser
     * modificados (clase de solo lectura).
     * </p>
     * 
     * <p>
     * <strong>Nota:</strong> Los valores proporcionados deben corresponder
     * a datos consistentes obtenidos de las consultas SQL que realizan
     * JOIN entre las tablas relacionadas.
     * </p>
     * 
     * @param idOrden          Identificador único de la orden (debe ser > 0)
     * @param nombreCliente    Nombre del cliente (no debe ser null ni vacío)
     * @param nombreTrabajador Nombre del trabajador (no debe ser null ni vacío)
     * @param fechaOrden       Fecha de la orden (no debe ser null)
     */
    public VentaInfo(int idOrden, String nombreCliente, String nombreTrabajador, LocalDate fechaOrden) {
        this.idOrden = idOrden;
        this.nombreCliente = nombreCliente;
        this.nombreTrabajador = nombreTrabajador;
        this.fechaOrden = fechaOrden;
    }

    /**
     * Obtiene el identificador único de la orden de venta.
     * 
     * <p>
     * Este ID corresponde a la clave primaria de la tabla {@code orden}
     * y puede utilizarse para realizar consultas adicionales sobre
     * los detalles específicos de la transacción.
     * </p>
     * 
     * @return ID de la orden, siempre > 0 para instancias válidas
     */
    public int getIdOrden() {
        return idOrden;
    }

    /**
     * Obtiene el nombre del cliente que realizó la compra.
     * 
     * <p>
     * Proporciona el nombre completo del cliente sin necesidad de
     * realizar consultas adicionales a la tabla {@code cliente}.
     * </p>
     * 
     * @return Nombre del cliente, nunca null para instancias válidas
     */
    public String getNombreCliente() {
        return nombreCliente;
    }

    /**
     * Obtiene el nombre del trabajador que procesó la venta.
     * 
     * <p>
     * Proporciona el nombre completo del empleado responsable de
     * la transacción sin necesidad de consultas adicionales a
     * la tabla {@code trabajador}.
     * </p>
     * 
     * @return Nombre del trabajador, nunca null para instancias válidas
     */
    public String getNombreTrabajador() {
        return nombreTrabajador;
    }

    /**
     * Obtiene la fecha en que se realizó la orden de venta.
     * 
     * <p>
     * Proporciona la fecha de creación de la orden en formato
     * {@link LocalDate}, facilitando operaciones de comparación
     * y filtrado temporal.
     * </p>
     * 
     * @return Fecha de la orden, nunca null para instancias válidas
     */
    public LocalDate getFechaOrden() {
        return fechaOrden;
    }
}