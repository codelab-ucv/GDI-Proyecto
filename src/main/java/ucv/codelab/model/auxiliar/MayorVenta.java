package ucv.codelab.model.auxiliar;

/**
 * Clase auxiliar de solo lectura para representar estadísticas de productos más
 * vendidos.
 * 
 * <p>
 * Esta clase encapsula los datos resultantes de consultas de análisis de
 * ventas,
 * específicamente para mostrar información agregada sobre los productos con
 * mayor
 * volumen de ventas en el sistema. Se utiliza exclusivamente para presentación
 * de datos en la interfaz de usuario.
 * </p>
 * 
 * <p>
 * <strong>Propósito:</strong> Clase de solo lectura (read-only) diseñada
 * únicamente
 * para la visualización de datos estadísticos. NO realiza modificaciones en la
 * base de datos ni contiene lógica de negocio.
 * </p>
 * 
 * <p>
 * <strong>Datos que encapsula:</strong>
 * </p>
 * <ul>
 * <li>Nombre del producto analizado</li>
 * <li>Cantidad total vendida (unidades acumuladas)</li>
 * <li>Monto total generado por las ventas del producto</li>
 * </ul>
 * 
 * <p>
 * <strong>Casos de uso típicos:</strong>
 * </p>
 * <ul>
 * <li>Reportes de productos top en interfaces gráficas</li>
 * <li>Dashboards de análisis de ventas</li>
 * <li>Exportación de datos estadísticos</li>
 * <li>Visualización de tendencias de productos</li>
 * </ul>
 * 
 * <p>
 * Los datos de esta clase son típicamente generados por consultas SQL complejas
 * que realizan agregaciones (SUM, GROUP BY) sobre las tablas de órdenes,
 * sub-órdenes y productos.
 * </p>
 * 
 * @see ucv.codelab.service.ConsultaAvanzadaSQL#mayorVentaCantidad(Integer,
 *      java.time.LocalDate, java.time.LocalDate, Integer, int)
 */
public class MayorVenta {

    /**
     * Nombre del producto que se está analizando.
     * 
     * <p>
     * Campo de solo lectura que identifica el producto específico
     * para el cual se han calculado las estadísticas de venta.
     * </p>
     */
    private String nombreProducto;

    /**
     * Cantidad total de unidades vendidas del producto.
     * 
     * <p>
     * Representa la suma acumulada de todas las cantidades vendidas
     * de este producto específico a través de todas las órdenes
     * que coinciden con los criterios de búsqueda aplicados.
     * </p>
     */
    private int cantidadVendida;

    /**
     * Monto total generado por las ventas del producto.
     * 
     * <p>
     * Calculado como la suma de (cantidad_vendida × precio_unitario)
     * para todas las transacciones del producto. Representa los
     * ingresos totales generados por este producto específico.
     * </p>
     */
    private double montoVendido;

    /**
     * Constructor que inicializa una instancia con los datos estadísticos del
     * producto.
     * 
     * <p>
     * Crea un objeto inmutable que encapsula las estadísticas de venta
     * de un producto específico. Una vez creado, los datos no pueden
     * ser modificados (clase de solo lectura).
     * </p>
     * 
     * <p>
     * <strong>Nota:</strong> Los valores proporcionados deben ser consistentes
     * y representar datos ya calculados y validados por las consultas SQL
     * correspondientes.
     * </p>
     * 
     * @param nombreProducto  Nombre del producto analizado (no debe ser null ni
     *                        vacío)
     * @param cantidadVendida Cantidad total de unidades vendidas (debe ser ≥ 0)
     * @param montoVendido    Monto total generado por las ventas (debe ser ≥ 0.0)
     */
    public MayorVenta(String nombreProducto, int cantidadVendida, double montoVendido) {
        this.nombreProducto = nombreProducto;
        this.cantidadVendida = cantidadVendida;
        this.montoVendido = montoVendido;
    }

    /**
     * Obtiene el nombre del producto analizado.
     * 
     * @return Nombre del producto, nunca null para instancias válidas
     */
    public String getNombreProducto() {
        return nombreProducto;
    }

    /**
     * Obtiene la cantidad total de unidades vendidas del producto.
     * 
     * <p>
     * Este valor representa la suma acumulada de todas las cantidades
     * vendidas del producto en el período y criterios especificados
     * en la consulta original.
     * </p>
     * 
     * @return Cantidad total vendida, siempre ≥ 0 para datos válidos
     */
    public int getCantidadVendida() {
        return cantidadVendida;
    }

    /**
     * Obtiene el monto total generado por las ventas del producto.
     * 
     * <p>
     * Representa los ingresos totales calculados como la suma de
     * (cantidad × precio) para todas las transacciones del producto
     * que coinciden con los criterios de la consulta.
     * </p>
     * 
     * @return Monto total vendido en la moneda del sistema, siempre ≥ 0.0 para
     *         datos válidos
     */
    public double getMontoVendido() {
        return montoVendido;
    }
}