package ucv.codelab.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import ucv.codelab.model.Orden;

public class OrdenRepository extends BaseRepository<Orden> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public OrdenRepository(Connection connection) {
        super(connection);
    }

    @Override
    protected String getTableName() {
        return "orden";
    }

    @Override
    protected String getIdColumnName() {
        return "id_orden";
    }

    @Override
    protected Orden mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Orden(
                rs.getInt("id_orden"),
                rs.getInt("id_trabajador"),
                rs.getInt("id_cliente"),
                rs.getInt("id_empresa"),
                LocalDate.parse(rs.getString("fecha_orden"), DATE_FORMATTER));
    }

    @Override
    protected void setStatementParametersForInsert(PreparedStatement stmt, Orden orden) throws SQLException {
        stmt.setInt(1, orden.getIdTrabajador());
        stmt.setInt(2, orden.getIdCliente());
        stmt.setInt(3, orden.getIdEmpresa());
        stmt.setString(4, orden.getFechaOrden().format(DATE_FORMATTER));
    }

    @Override
    protected void setStatementParametersForUpdate(PreparedStatement stmt, Orden orden) throws SQLException {
        stmt.setInt(1, orden.getIdTrabajador());
        stmt.setInt(2, orden.getIdCliente());
        stmt.setInt(3, orden.getIdEmpresa());
        stmt.setString(4, orden.getFechaOrden().format(DATE_FORMATTER));
        stmt.setInt(5, orden.getIdOrden());
    }

    @Override
    protected String buildInsertSQL() {
        return "INSERT INTO orden (id_trabajador, id_cliente, id_empresa, fecha_orden) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected String buildUpdateSQL() {
        return "UPDATE orden SET id_trabajador = ?, id_cliente = ?, id_empresa = ?, fecha_orden = ? WHERE id_orden = ?";
    }

    @Override
    protected void updateEntityWithGeneratedId(Orden orden, ResultSet generatedKeys) throws SQLException {
        int id = generatedKeys.getInt(1);
        orden.setIdOrden(id);
    }

    /**
     * Busca órdenes por ID de cliente
     * 
     * @param idCliente ID del cliente
     * @return Lista de órdenes asociadas al cliente
     */
    public List<Orden> findByCliente(int idCliente) {
        String sql = "SELECT * FROM orden WHERE id_cliente = ?";
        return executeQuery(sql, idCliente);
    }

    /**
     * Busca órdenes por ID de trabajador
     * 
     * @param idTrabajador ID del trabajador
     * @return Lista de órdenes asociadas al trabajador
     */
    public List<Orden> findByTrabajador(int idTrabajador) {
        String sql = "SELECT * FROM orden WHERE id_trabajador = ?";
        return executeQuery(sql, idTrabajador);
    }

    /**
     * Busca órdenes por rango de fechas
     * 
     * @param desde Fecha de inicio (inclusive)
     * @param hasta Fecha de fin (inclusive)
     * @return Lista de órdenes en el rango de fechas
     */
    public List<Orden> findByFechaRange(LocalDate desde, LocalDate hasta) {
        String sql = "SELECT * FROM orden WHERE fecha_orden BETWEEN ? AND ?";
        return executeQuery(sql,
                desde.format(DATE_FORMATTER),
                hasta.format(DATE_FORMATTER));
    }
}