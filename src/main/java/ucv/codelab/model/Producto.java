package ucv.codelab.model;

/**
 * Representa un producto en el sistema de gestión de órdenes.
 * 
 * <p>
 * Esta clase modela la información de los productos que pueden ser incluidos
 * en las órdenes de compra del sistema. Cada producto tiene un precio y un
 * estado de vigencia que determina si está disponible para nuevas órdenes.
 * </p>
 * 
 * <p>
 * <strong>Tabla asociada:</strong> {@code producto}
 * </p>
 * 
 * <p>
 * <strong>Campos de la tabla:</strong>
 * </p>
 * <ul>
 * <li>{@code id_producto} - Clave primaria autoincremental</li>
 * <li>{@code nombre_producto} - Nombre del producto</li>
 * <li>{@code precio} - Precio del producto (tipo REAL)</li>
 * <li>{@code vigente} - Estado de vigencia del producto (1=vigente, 0=no
 * vigente, por defecto 1)</li>
 * </ul>
 * 
 * <p>
 * <strong>Relaciones:</strong>
 * </p>
 * <ul>
 * <li>Un producto puede estar incluido en múltiples sub-órdenes</li>
 * <li>Cada sub-orden referencia a un único producto</li>
 * </ul>
 * 
 * @see ucv.codelab.repository.ProductoRepository
 */
public class Producto {

    /**
     * Identificador único del producto en la base de datos.
     * Corresponde a la clave primaria autoincremental.
     */
    private int idProducto;

    /**
     * Nombre del producto.
     * Campo requerido que identifica al producto en el sistema.
     */
    private String nombreProducto;

    /**
     * Precio del producto.
     * Campo requerido que especifica el costo del producto.
     */
    private double precio;

    /**
     * Estado de vigencia del producto.
     * Indica si el producto está disponible para ser incluido en nuevas órdenes.
     * Por defecto es true (vigente).
     */
    private boolean vigente;

    /**
     * Constructor que inicializa un producto con todos sus datos.
     * 
     * <p>
     * Este constructor se utiliza principalmente al recuperar datos
     * de la base de datos, donde ya se conocen todos los campos del producto.
     * </p>
     * 
     * @param idProducto     Identificador único del producto
     * @param nombreProducto Nombre del producto
     * @param precio         Precio del producto
     * @param vigente        Estado de vigencia del producto
     */
    public Producto(int idProducto, String nombreProducto, double precio, boolean vigente) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.precio = precio;
        this.vigente = vigente;
    }

    /**
     * Obtiene el identificador único del producto.
     * 
     * @return ID del producto
     */
    public int getIdProducto() {
        return idProducto;
    }

    /**
     * Establece el identificador único del producto.
     * 
     * <p>
     * Este método se utiliza principalmente después de insertar
     * un nuevo producto en la base de datos para asignar el ID
     * generado automáticamente.
     * </p>
     * 
     * @param idProducto ID del producto a establecer
     */
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    /**
     * Obtiene el nombre del producto.
     * 
     * @return Nombre del producto
     */
    public String getNombreProducto() {
        return nombreProducto;
    }

    /**
     * Establece el nombre del producto.
     * 
     * @param nombreProducto Nombre del producto a establecer
     */
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    /**
     * Obtiene el precio del producto.
     * 
     * @return Precio del producto
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * Establece el precio del producto.
     * 
     * @param precio Precio del producto a establecer
     */
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    /**
     * Verifica si el producto está vigente.
     * 
     * @return true si el producto está vigente y disponible para órdenes, false en
     *         caso contrario
     */
    public boolean isVigente() {
        return vigente;
    }

    /**
     * Establece el estado de vigencia del producto.
     * 
     * <p>
     * Los productos no vigentes no deberían estar disponibles para
     * nuevas órdenes, aunque pueden mantenerse en el sistema por
     * razones históricas.
     * </p>
     * 
     * @param vigente Estado de vigencia a establecer
     */
    public void setVigente(boolean vigente) {
        this.vigente = vigente;
    }

    public static Object[] cabecera() {
        return new Object[] { "ID", "Nombre", "Precio", "Vigente" };
    }

    public Object[] registro() {
        return new Object[] { idProducto, nombreProducto, precio, vigente };
    }
}