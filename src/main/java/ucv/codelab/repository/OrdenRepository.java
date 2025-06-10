package ucv.codelab.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import ucv.codelab.model.Orden;
import ucv.codelab.util.SQLiteConexion;

/**
 * Repositorio para la gestión de órdenes en la base de datos.
 * 
 * <p>
 * Esta clase extiende {@link BaseRepository} y proporciona operaciones CRUD
 * específicas para la entidad {@link Orden}, incluyendo métodos de búsqueda
 * por trabajador, rango de fechas y gestión de fechas con formato ISO.
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
 * <li>{@code id_trabajador} - Clave foránea referenciando al trabajador
 * responsable</li>
 * <li>{@code id_cliente} - Clave foránea referenciando al cliente de la
 * orden</li>
 * <li>{@code id_empresa} - Clave foránea referenciando a la empresa</li>
 * <li>{@code fecha_orden} - Fecha de creación de la orden en formato ISO
 * (YYYY-MM-DD)</li>
 * </ul>
 * 
 * <p>
 * <strong>Formato de fechas:</strong> Este repositorio utiliza el formato
 * ISO_LOCAL_DATE
 * (YYYY-MM-DD) definido en {@link #DATE_FORMATTER} para el almacenamiento y
 * recuperación
 * consistente de fechas en la base de datos SQLite.
 * </p>
 * 
 * @see BaseRepository
 * @see Orden
 * @see LocalDate
 */
public class OrdenRepository extends BaseRepository<Orden> {

    /**
     * Formateador de fechas utilizado para conversión entre LocalDate y String.
     * 
     * <p>
     * Utiliza el formato ISO_LOCAL_DATE (YYYY-MM-DD) para garantizar
     * consistencia en el almacenamiento de fechas en la base de datos SQLite.
     * </p>
     */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Constructor que inicializa el repositorio con la conexión a la base de datos.
     * 
     * @throws SQLException Si ocurre un error al obtener la conexión a la base de
     *                      datos
     */
    public OrdenRepository() throws SQLException {
        super(SQLiteConexion.getInstance().getConexion());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTableName() {
        return "orden";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getIdColumnName() {
        return "id_orden";
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Mapea los campos de la tabla orden a un objeto Orden.
     * La fecha se convierte automáticamente de String (formato ISO) a LocalDate
     * utilizando el {@link #DATE_FORMATTER}.
     * </p>
     */
    @Override
    protected Orden mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Orden(
                rs.getInt("id_orden"),
                rs.getInt("id_trabajador"),
                rs.getInt("id_cliente"),
                rs.getInt("id_empresa"),
                LocalDate.parse(rs.getString("fecha_orden"), DATE_FORMATTER));
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Establece los parámetros para insertar una nueva orden.
     * La fecha se convierte de LocalDate a String utilizando el
     * {@link #DATE_FORMATTER}.
     * </p>
     */
    @Override
    protected void setStatementParametersForInsert(PreparedStatement stmt, Orden orden) throws SQLException {
        stmt.setInt(1, orden.getIdTrabajador());
        stmt.setInt(2, orden.getIdCliente());
        stmt.setInt(3, orden.getIdEmpresa());
        stmt.setString(4, orden.getFechaOrden().format(DATE_FORMATTER));
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Establece los parámetros para actualizar una orden existente.
     * La fecha se convierte de LocalDate a String utilizando el
     * {@link #DATE_FORMATTER},
     * y el ID de la orden se incluye como último parámetro.
     * </p>
     */
    @Override
    protected void setStatementParametersForUpdate(PreparedStatement stmt, Orden orden) throws SQLException {
        stmt.setInt(1, orden.getIdTrabajador());
        stmt.setInt(2, orden.getIdCliente());
        stmt.setInt(3, orden.getIdEmpresa());
        stmt.setString(4, orden.getFechaOrden().format(DATE_FORMATTER));
        stmt.setInt(5, orden.getIdOrden());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String buildInsertSQL() {
        return "INSERT INTO orden (id_trabajador, id_cliente, id_empresa, fecha_orden) VALUES (?, ?, ?, ?)";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String buildUpdateSQL() {
        return "UPDATE orden SET id_trabajador = ?, id_cliente = ?, id_empresa = ?, fecha_orden = ? WHERE id_orden = ?";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateEntityWithGeneratedId(Orden orden, ResultSet generatedKeys) throws SQLException {
        int id = generatedKeys.getInt(1);
        orden.setIdOrden(id);
    }

    /**
     * Busca todas las órdenes asociadas a un trabajador específico.
     * 
     * <p>
     * Este método es útil para generar reportes de productividad,
     * asignar cargas de trabajo o revisar el historial de órdenes
     * de un trabajador en particular.
     * </p>
     * 
     * @param idTrabajador ID del trabajador cuyas órdenes se desean consultar
     * @return Lista de órdenes asociadas al trabajador especificado
     */
    public List<Orden> findByTrabajador(int idTrabajador) {
        String sql = "SELECT * FROM orden WHERE id_trabajador = ?";
        return executeQuery(sql, idTrabajador);
    }

    /**
     * Busca órdenes dentro de un rango específico de fechas.
     * 
     * <p>
     * Utiliza el operador BETWEEN para incluir órdenes cuyas fechas
     * estén dentro del rango especificado (inclusive en ambos extremos).
     * Las fechas se comparan en formato ISO (YYYY-MM-DD).
     * </p>
     * 
     * <p>
     * Este método es especialmente útil para generar reportes periódicos,
     * análisis de tendencias temporales y filtrado de órdenes por períodos
     * específicos como semanas, meses o trimestres.
     * </p>
     * 
     * @param desde Fecha de inicio del rango (inclusive)
     * @param hasta Fecha de fin del rango (inclusive)
     * @return Lista de órdenes cuyas fechas están dentro del rango especificado
     */
    public List<Orden> findByFechaRange(LocalDate desde, LocalDate hasta) {
        String sql = "SELECT * FROM orden WHERE fecha_orden BETWEEN ? AND ?";
        return executeQuery(sql,
                desde.format(DATE_FORMATTER),
                hasta.format(DATE_FORMATTER));
    }
}