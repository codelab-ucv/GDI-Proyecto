package ucv.codelab.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ucv.codelab.model.SubOrden;
import ucv.codelab.util.SQLiteConexion;

/**
 * Repositorio para la gestión de subórdenes en la base de datos.
 * 
 * <p>
 * Esta clase extiende {@link BaseRepository} y proporciona operaciones CRUD
 * específicas para la entidad {@link SubOrden}, que representa los productos
 * individuales y sus cantidades dentro de una orden principal. Incluye métodos
 * especializados para búsqueda por orden, producto y eliminación en cascada.
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
 * <li>{@code id_orden} - Clave foránea referenciando a la orden principal</li>
 * <li>{@code id_producto} - Clave foránea referenciando al producto
 * solicitado</li>
 * <li>{@code cantidad} - Cantidad del producto en esta suborden</li>
 * </ul>
 * 
 * <p>
 * <strong>Relaciones:</strong> Una {@link ucv.codelab.model.Orden} puede tener
 * múltiples SubOrden, creando una relación 1:N que permite detallar todos
 * los productos y cantidades que componen una orden completa.
 * </p>
 * 
 * @see BaseRepository
 * @see SubOrden
 * @see ucv.codelab.model.Orden
 */
public class SubOrdenRepository extends BaseRepository<SubOrden> {

    /**
     * Constructor que inicializa el repositorio con la conexión a la base de datos.
     * 
     * @throws SQLException Si ocurre un error al obtener la conexión a la base de
     *                      datos
     */
    public SubOrdenRepository() throws SQLException {
        super(SQLiteConexion.getInstance().getConexion());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTableName() {
        return "sub_orden";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getIdColumnName() {
        return "id_sub_orden";
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Mapea todos los campos de la tabla sub_orden a un objeto SubOrden,
     * incluyendo las referencias a la orden principal y al producto.
     * </p>
     */
    @Override
    protected SubOrden mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new SubOrden(
                rs.getInt("id_sub_orden"),
                rs.getInt("id_orden"),
                rs.getInt("id_producto"),
                rs.getInt("cantidad"));
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Establece los parámetros para insertar una nueva suborden,
     * incluyendo la referencia a la orden principal, producto y cantidad.
     * </p>
     */
    @Override
    protected void setStatementParametersForInsert(PreparedStatement stmt, SubOrden subOrden) throws SQLException {
        stmt.setInt(1, subOrden.getIdOrden());
        stmt.setInt(2, subOrden.getIdProducto());
        stmt.setInt(3, subOrden.getCantidad());
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Establece los parámetros para actualizar una suborden existente,
     * incluyendo el ID de la suborden como último parámetro.
     * </p>
     */
    @Override
    protected void setStatementParametersForUpdate(PreparedStatement stmt, SubOrden subOrden) throws SQLException {
        stmt.setInt(1, subOrden.getIdOrden());
        stmt.setInt(2, subOrden.getIdProducto());
        stmt.setInt(3, subOrden.getCantidad());
        stmt.setInt(4, subOrden.getIdSubOrden());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String buildInsertSQL() {
        return "INSERT INTO sub_orden (id_orden, id_producto, cantidad) VALUES (?, ?, ?)";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String buildUpdateSQL() {
        return "UPDATE sub_orden SET id_orden = ?, id_producto = ?, cantidad = ? WHERE id_sub_orden = ?";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateEntityWithGeneratedId(SubOrden subOrden, ResultSet generatedKeys) throws SQLException {
        int id = generatedKeys.getInt(1);
        subOrden.setIdSubOrden(id);
    }

    /**
     * Busca todas las subórdenes asociadas a una orden principal específica.
     * 
     * <p>
     * Este método es fundamental para recuperar todos los productos y cantidades
     * que componen una orden completa. Es especialmente útil para generar
     * facturas detalladas, calcular totales de órdenes y mostrar el desglose
     * completo de productos solicitados.
     * </p>
     * 
     * @param idOrden ID de la orden principal cuyas subórdenes se desean consultar
     * @return Lista de subórdenes asociadas a la orden especificada
     */
    public List<SubOrden> findByOrden(int idOrden) {
        String sql = "SELECT * FROM sub_orden WHERE id_orden = ?";
        return executeQuery(sql, idOrden);
    }

    /**
     * Busca todas las subórdenes que contienen un producto específico.
     * 
     * <p>
     * Este método permite rastrear en qué órdenes se ha solicitado un producto
     * particular, lo cual es útil para análisis de demanda, gestión de inventario
     * y reportes de popularidad de productos.
     * </p>
     * 
     * @param idProducto ID del producto cuyas subórdenes se desean consultar
     * @return Lista de subórdenes que contienen el producto especificado
     */
    public List<SubOrden> findByProducto(int idProducto) {
        String sql = "SELECT * FROM sub_orden WHERE id_producto = ?";
        return executeQuery(sql, idProducto);
    }

    /**
     * Elimina todas las subórdenes asociadas a una orden principal.
     * 
     * <p>
     * Este método implementa una eliminación en cascada manual que es útil
     * cuando se necesita limpiar completamente los detalles de una orden
     * antes de eliminar la orden principal, o cuando se requiere rehacer
     * completamente el contenido de una orden.
     * </p>
     * 
     * <p>
     * <strong>Importante:</strong> Esta operación es irreversible y eliminará
     * permanentemente todos los registros de subórdenes asociados a la orden
     * especificada. Se recomienda validar la existencia de la orden principal
     * antes de ejecutar esta operación.
     * </p>
     * 
     * @param idOrden ID de la orden principal cuyas subórdenes se eliminarán
     * @return Número de registros de subórdenes eliminados
     * @throws SQLException Si ocurre un error durante la operación de eliminación
     */
    public int deleteByOrden(int idOrden) throws SQLException {
        String sql = "DELETE FROM sub_orden WHERE id_orden = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idOrden);
            return stmt.executeUpdate();
        }
    }
}