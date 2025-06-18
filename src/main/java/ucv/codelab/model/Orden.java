package ucv.codelab.model;

import java.time.LocalDate;

/**
 * Representa una orden de compra en el sistema de gestión de órdenes.
 * 
 * <p>
 * Esta clase modela el encabezado de una orden de compra, que conecta
 * un trabajador, un cliente y una empresa en una transacción específica.
 * La orden actúa como contenedor principal para múltiples sub-órdenes
 * que detallan los productos específicos solicitados.
 * </p>
 * 
 * <p>
 * <strong>Tabla asociada:</strong> {@code orden}
 * </p>
 * 
 * <p>
 * <strong>Campos de la tabla:</strong>
 * </p>
 * <ul>
 * <li>{@code id_orden} - Clave primaria autoincremental</li>
 * <li>{@code id_trabajador} - Clave foránea que referencia al trabajador
 * responsable</li>
 * <li>{@code id_cliente} - Clave foránea que referencia al cliente que realiza
 * la orden</li>
 * <li>{@code id_empresa} - Clave foránea que referencia a la empresa
 * asociada</li>
 * <li>{@code fecha_orden} - Fecha en que se creó la orden (formato TEXT en
 * BD)</li>
 * </ul>
 * 
 * <p>
 * <strong>Relaciones:</strong>
 * </p>
 * <ul>
 * <li>Pertenece a un único {@link Trabajador} (quien procesa la orden)</li>
 * <li>Pertenece a un único {@link Cliente} (quien solicita la orden)</li>
 * <li>Pertenece a una única {@link Empresa} (contexto empresarial)</li>
 * <li>Puede tener múltiples {@link SubOrden} (detalles de productos)</li>
 * </ul>
 * 
 * <p>
 * <strong>Flujo típico:</strong>
 * </p>
 * <ol>
 * <li>Un trabajador crea una orden para un cliente específico</li>
 * <li>La orden se asocia a una empresa</li>
 * <li>Se agregan sub-órdenes con los productos solicitados</li>
 * <li>La orden queda registrada con su fecha de creación</li>
 * </ol>
 * 
 * @see ucv.codelab.repository.OrdenRepository
 * @see SubOrden
 * @see Trabajador
 * @see Cliente
 * @see Empresa
 */
public class Orden {

    /**
     * Identificador único de la orden en la base de datos.
     * Corresponde a la clave primaria autoincremental.
     */
    private int idOrden;

    /**
     * Identificador del trabajador responsable de procesar esta orden.
     * Clave foránea que referencia la tabla trabajador.
     */
    private int idTrabajador;

    /**
     * Identificador del cliente que solicita esta orden.
     * Clave foránea que referencia la tabla cliente.
     */
    private int idCliente;

    /**
     * Identificador de la empresa asociada a esta orden.
     * Clave foránea que referencia la tabla empresa.
     */
    private int idEmpresa;

    /**
     * Fecha en que se creó la orden.
     * Se almacena como TEXT en la base de datos pero se maneja como LocalDate en
     * Java.
     */
    private LocalDate fechaOrden;

    /**
     * Constructor que inicializa una orden con todos sus datos.
     * 
     * <p>
     * Este constructor se utiliza principalmente al recuperar órdenes
     * existentes de la base de datos, donde todos los campos están
     * disponibles incluyendo el ID generado automáticamente.
     * </p>
     * 
     * @param idOrden      Identificador único de la orden
     * @param idTrabajador ID del trabajador responsable de la orden
     * @param idCliente    ID del cliente que solicita la orden
     * @param idEmpresa    ID de la empresa asociada a la orden
     * @param fechaOrden   Fecha de creación de la orden
     */
    public Orden(int idOrden, int idTrabajador, int idCliente, int idEmpresa, LocalDate fechaOrden) {
        this.idOrden = idOrden;
        this.idTrabajador = idTrabajador;
        this.idCliente = idCliente;
        this.idEmpresa = idEmpresa;
        this.fechaOrden = fechaOrden;
    }

    /**
     * Obtiene el identificador único de la orden.
     * 
     * @return ID de la orden
     */
    public int getIdOrden() {
        return idOrden;
    }

    /**
     * Establece el identificador único de la orden.
     * 
     * <p>
     * Este método se utiliza principalmente después de insertar
     * una nueva orden en la base de datos para asignar el ID
     * generado automáticamente.
     * </p>
     * 
     * @param idOrden ID de la orden a establecer
     */
    public void setIdOrden(int idOrden) {
        this.idOrden = idOrden;
    }

    /**
     * Obtiene el identificador del trabajador responsable de la orden.
     * 
     * @return ID del trabajador
     */
    public int getIdTrabajador() {
        return idTrabajador;
    }

    /**
     * Establece el identificador del trabajador responsable de la orden.
     * 
     * <p>
     * <strong>Importante:</strong> El ID debe corresponder a un trabajador
     * válido existente en la base de datos debido a la restricción de
     * clave foránea.
     * </p>
     * 
     * @param idTrabajador ID del trabajador a establecer
     */
    public void setIdTrabajador(int idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    /**
     * Obtiene el identificador del cliente que solicita la orden.
     * 
     * @return ID del cliente
     */
    public int getIdCliente() {
        return idCliente;
    }

    /**
     * Establece el identificador del cliente que solicita la orden.
     * 
     * <p>
     * <strong>Importante:</strong> El ID debe corresponder a un cliente
     * válido existente en la base de datos debido a la restricción de
     * clave foránea.
     * </p>
     * 
     * @param idCliente ID del cliente a establecer
     */
    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    /**
     * Obtiene el identificador de la empresa asociada a la orden.
     * 
     * @return ID de la empresa
     */
    public int getIdEmpresa() {
        return idEmpresa;
    }

    /**
     * Establece el identificador de la empresa asociada a la orden.
     * 
     * <p>
     * <strong>Importante:</strong> El ID debe corresponder a una empresa
     * válida existente en la base de datos debido a la restricción de
     * clave foránea.
     * </p>
     * 
     * @param idEmpresa ID de la empresa a establecer
     */
    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    /**
     * Obtiene la fecha de creación de la orden.
     * 
     * @return Fecha de la orden
     */
    public LocalDate getFechaOrden() {
        return fechaOrden;
    }

    /**
     * Establece la fecha de creación de la orden.
     * 
     * <p>
     * Generalmente esta fecha se establece automáticamente al momento
     * de crear la orden y representa cuándo fue registrada en el sistema.
     * </p>
     * 
     * @param fechaOrden Fecha de la orden a establecer
     */
    public void setFechaOrden(LocalDate fechaOrden) {
        this.fechaOrden = fechaOrden;
    }

    public static Object[] cabecera() {
        return new Object[] { "ID", "ID Trabajador", "ID Cliente", "Fecha Orden" };
    }

    public Object[] registro() {
        return new Object[] { idCliente, idTrabajador, idCliente, fechaOrden };
    }
}