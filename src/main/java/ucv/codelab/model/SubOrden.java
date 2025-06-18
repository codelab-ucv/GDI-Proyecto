package ucv.codelab.model;

/**
 * Representa una sub-orden dentro del sistema de gestión de órdenes.
 * 
 * <p>
 * Esta clase modela los elementos individuales que componen una orden de
 * compra.
 * Cada sub-orden representa un producto específico con su cantidad dentro de
 * una orden principal. Una orden puede contener múltiples sub-órdenes, pero
 * cada producto solo puede aparecer una vez por orden.
 * </p>
 * 
 * <p>
 * <strong>Tabla asociada:</strong> {@code sub_orden}
 * </p>
 * 
 * <p>
 * <strong>Campos de la tabla:</strong>
 * </p>
 * <ul>
 * <li>{@code id_sub_orden} - Clave primaria autoincremental</li>
 * <li>{@code id_orden} - Referencia a la orden principal (clave foránea)</li>
 * <li>{@code id_producto} - Referencia al producto (clave foránea)</li>
 * <li>{@code cantidad} - Cantidad del producto en esta sub-orden</li>
 * </ul>
 * 
 * <p>
 * <strong>Restricciones:</strong>
 * </p>
 * <ul>
 * <li>La combinación de id_orden + id_producto debe ser única</li>
 * <li>Evita que una orden añada un mismo producto dos veces</li>
 * </ul>
 * 
 * <p>
 * <strong>Relaciones:</strong>
 * </p>
 * <ul>
 * <li>Cada sub-orden pertenece a una única orden</li>
 * <li>Cada sub-orden referencia a un único producto</li>
 * <li>Una orden puede tener múltiples sub-órdenes</li>
 * <li>Un producto puede estar en múltiples sub-órdenes (de diferentes
 * órdenes)</li>
 * </ul>
 * 
 * @see ucv.codelab.repository.SubOrdenRepository
 * @see ucv.codelab.model.Orden
 * @see ucv.codelab.model.Producto
 */
public class SubOrden {

    /**
     * Identificador único de la sub-orden en la base de datos.
     * Corresponde a la clave primaria autoincremental.
     */
    private int idSubOrden;

    /**
     * Identificador de la orden principal a la que pertenece esta sub-orden.
     * Corresponde a una clave foránea que referencia la tabla orden.
     */
    private int idOrden;

    /**
     * Identificador del producto incluido en esta sub-orden.
     * Corresponde a una clave foránea que referencia la tabla producto.
     */
    private int idProducto;

    /**
     * Cantidad del producto solicitada en esta sub-orden.
     * Debe ser un número entero positivo.
     */
    private int cantidad;

    /**
     * Constructor que inicializa una sub-orden con todos sus datos.
     * 
     * <p>
     * Este constructor se utiliza principalmente al recuperar datos
     * de la base de datos, donde ya se conocen todos los identificadores
     * y la cantidad de la sub-orden.
     * </p>
     * 
     * @param idSubOrden Identificador único de la sub-orden
     * @param idOrden    Identificador de la orden principal
     * @param idProducto Identificador del producto
     * @param cantidad   Cantidad del producto en esta sub-orden
     */
    public SubOrden(int idSubOrden, int idOrden, int idProducto, int cantidad) {
        this.idSubOrden = idSubOrden;
        this.idOrden = idOrden;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }

    /**
     * Obtiene el identificador único de la sub-orden.
     * 
     * @return ID de la sub-orden
     */
    public int getIdSubOrden() {
        return idSubOrden;
    }

    /**
     * Establece el identificador único de la sub-orden.
     * 
     * <p>
     * Este método se utiliza principalmente después de insertar
     * una nueva sub-orden en la base de datos para asignar el ID
     * generado automáticamente.
     * </p>
     * 
     * @param idSubOrden ID de la sub-orden a establecer
     */
    public void setIdSubOrden(int idSubOrden) {
        this.idSubOrden = idSubOrden;
    }

    /**
     * Obtiene el identificador de la orden principal.
     * 
     * @return ID de la orden a la que pertenece esta sub-orden
     */
    public int getIdOrden() {
        return idOrden;
    }

    /**
     * Establece el identificador de la orden principal.
     * 
     * <p>
     * <strong>Importante:</strong> La combinación de idOrden + idProducto
     * debe ser única en el sistema. Asegúrese de validar la unicidad
     * antes de actualizar este campo.
     * </p>
     * 
     * @param idOrden ID de la orden principal a establecer
     */
    public void setIdOrden(int idOrden) {
        this.idOrden = idOrden;
    }

    /**
     * Obtiene el identificador del producto.
     * 
     * @return ID del producto incluido en esta sub-orden
     */
    public int getIdProducto() {
        return idProducto;
    }

    /**
     * Establece el identificador del producto.
     * 
     * <p>
     * <strong>Importante:</strong> La combinación de idOrden + idProducto
     * debe ser única en el sistema. Asegúrese de validar la unicidad
     * antes de actualizar este campo.
     * </p>
     * 
     * @param idProducto ID del producto a establecer
     */
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    /**
     * Obtiene la cantidad del producto en esta sub-orden.
     * 
     * @return Cantidad del producto solicitada
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad del producto en esta sub-orden.
     * 
     * @param cantidad Cantidad del producto a establecer (debe ser positiva)
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}