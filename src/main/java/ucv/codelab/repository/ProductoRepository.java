package ucv.codelab.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import ucv.codelab.model.Producto;
import ucv.codelab.util.SQLiteConexion;

/**
 * Repositorio para la gestión de productos en la base de datos.
 * 
 * <p>
 * Esta clase extiende {@link BaseRepository} y proporciona operaciones CRUD
 * específicas para la entidad {@link Producto}, incluyendo métodos de búsqueda
 * personalizados por nombre, estado de vigencia y rango de precios.
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
 * <li>{@code precio} - Precio del producto (decimal)</li>
 * <li>{@code vigente} - Estado de vigencia (1=activo, 0=inactivo)</li>
 * </ul>
 * 
 * @see BaseRepository
 * @see Producto
 */
public class ProductoRepository extends BaseRepository<Producto> {

    /**
     * Constructor que inicializa el repositorio con la conexión a la base de datos.
     * 
     * @throws SQLException Si ocurre un error al obtener la conexión a la base de
     *                      datos
     */
    public ProductoRepository() throws SQLException {
        super(SQLiteConexion.getInstance().getConexion());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTableName() {
        return "producto";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getIdColumnName() {
        return "id_producto";
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Mapea los campos de la tabla producto a un objeto Producto.
     * El campo {@code vigente} se convierte automáticamente de INT (0/1) a boolean.
     * </p>
     */
    @Override
    protected Producto mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id_producto"),
                rs.getString("nombre_producto"),
                rs.getDouble("precio"),
                rs.getBoolean("vigente")); // Se convierte el INT (0/1) a boolean
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Establece los parámetros para insertar un nuevo producto.
     * El campo {@code vigente} no se incluye ya que por defecto se establece como
     * activo.
     * </p>
     */
    @Override
    protected void setStatementParametersForInsert(PreparedStatement stmt, Producto producto) throws SQLException {
        stmt.setString(1, producto.getNombreProducto());
        stmt.setDouble(2, producto.getPrecio());
        // No se incluye el insert de vigente porque por defecto está activo
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Establece los parámetros para actualizar un producto existente.
     * El campo {@code vigente} se convierte de boolean a INT (0/1).
     * </p>
     */
    @Override
    protected void setStatementParametersForUpdate(PreparedStatement stmt, Producto producto) throws SQLException {
        stmt.setString(1, producto.getNombreProducto());
        stmt.setDouble(2, producto.getPrecio());
        // Convierte el boolean en un INT (0/1)
        stmt.setInt(3, producto.isVigente() ? 1 : 0);
        stmt.setInt(4, producto.getIdProducto());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String buildInsertSQL() {
        return "INSERT INTO producto (nombre_producto, precio) VALUES (?, ?)";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String buildUpdateSQL() {
        return "UPDATE producto SET nombre_producto = ?, precio = ?, vigente = ? WHERE id_producto = ?";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateEntityWithGeneratedId(Producto producto, ResultSet generatedKeys) throws SQLException {
        int id = generatedKeys.getInt(1);
        producto.setIdProducto(id);
    }

    /**
     * Busca un producto vigente por su ID.
     * 
     * <p>
     * Solo retorna productos que tengan el campo {@code vigente = 1}.
     * </p>
     * 
     * @param id ID del producto a buscar
     * @return Optional que contiene el producto si existe y está vigente, o vacío
     *         si no existe o no está vigente
     */
    public Optional<Producto> findByIdVigentes(int id) {
        String sql = "SELECT * FROM producto WHERE id_producto = ? AND vigente = 1";
        return executeQueryForSingleResult(sql, id);
    }

    /**
     * Busca productos por nombre utilizando búsqueda parcial (LIKE).
     * 
     * <p>
     * La búsqueda es case-sensitive y utiliza el operador LIKE con wildcards
     * para encontrar productos cuyo nombre contenga el texto especificado.
     * </p>
     * 
     * @param nombre Texto a buscar en el nombre del producto
     * @return Lista de productos que coinciden con el criterio de búsqueda
     */
    public List<Producto> findByNombre(String nombre) {
        String sql = "SELECT * FROM producto WHERE nombre_producto LIKE ?";
        return executeQuery(sql, "%" + nombre + "%");
    }

    /**
     * Busca productos vigentes por nombre utilizando búsqueda parcial.
     * 
     * <p>
     * Combina la búsqueda por nombre con el filtro de vigencia,
     * retornando solo productos activos que coincidan con el criterio.
     * </p>
     * 
     * @param nombre Texto a buscar en el nombre del producto
     * @return Lista de productos vigentes que coinciden con el criterio de búsqueda
     */
    public List<Producto> findByNombreVigentes(String nombre) {
        String sql = "SELECT * FROM producto WHERE nombre_producto LIKE ? AND vigente = 1";
        return executeQuery(sql, "%" + nombre + "%");
    }

    /**
     * Obtiene todos los productos vigentes de la base de datos.
     * 
     * <p>
     * Retorna únicamente los productos que tienen el campo {@code vigente = 1}.
     * </p>
     * 
     * @return Lista de productos con estado vigente activo
     */
    public List<Producto> findVigentes() {
        String sql = "SELECT * FROM producto WHERE vigente = 1";
        return executeQuery(sql);
    }

    /**
     * Busca productos dentro de un rango específico de precios.
     * 
     * <p>
     * Utiliza el operador BETWEEN para incluir productos cuyos precios
     * estén dentro del rango especificado (inclusive en ambos extremos).
     * </p>
     * 
     * @param precioMin Precio mínimo del rango (inclusive)
     * @param precioMax Precio máximo del rango (inclusive)
     * @return Lista de productos cuyos precios están dentro del rango especificado
     */
    public List<Producto> findByPrecioRange(double precioMin, double precioMax) {
        String sql = "SELECT * FROM producto WHERE precio BETWEEN ? AND ?";
        return executeQuery(sql, precioMin, precioMax);
    }
}