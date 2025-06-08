package ucv.codelab.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import ucv.codelab.model.Orden;
import ucv.codelab.model.auxiliar.VentaInfo;
import ucv.codelab.util.SQLiteConexion;

public class OrdenRepository extends BaseRepository<Orden> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public OrdenRepository() throws SQLException {
        super(SQLiteConexion.getInstance().getConexion());
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

    /**
     * Busca información de ventas con filtros dinámicos
     * 
     * @param idOrden    ID específico de orden (opcional)
     * @param cliente    Nombre del cliente para filtrar (opcional)
     * @param trabajador Nombre del trabajador para filtrar (opcional)
     * @param fechaDesde Fecha de inicio del rango (opcional)
     * @param fechaHasta Fecha de fin del rango (opcional)
     * @param idEmpresa  ID de la empresa (requerido)
     * @return Lista de información de ventas que coinciden con los filtros
     * @throws SQLException si ocurre un error en la consulta
     */
    public List<VentaInfo> buscarVentas(Integer idOrden, String cliente, String trabajador,
            LocalDate fechaDesde, LocalDate fechaHasta, int idEmpresa) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT o.id_orden, c.nombre_cliente, t.nombre_trabajador, o.fecha_orden FROM orden o ");
        sql.append("INNER JOIN cliente c ON o.id_cliente = c.id_cliente ");
        sql.append("INNER JOIN trabajador t ON o.id_trabajador = t.id_trabajador ");
        sql.append("WHERE o.id_empresa = ?");

        List<Object> parametros = new ArrayList<>();
        parametros.add(idEmpresa);

        // Filtro por ID de orden (si se proporciona)
        if (idOrden != null) {
            sql.append(" AND o.id_orden = ?");
            parametros.add(idOrden);
        }

        // Filtro por nombre de cliente
        if (cliente != null && !cliente.trim().isEmpty()) {
            sql.append(" AND c.nombre_cliente LIKE ?");
            parametros.add("%" + cliente.trim() + "%");
        }

        // Filtro por nombre de trabajador
        if (trabajador != null && !trabajador.trim().isEmpty()) {
            sql.append(" AND t.nombre_trabajador LIKE ?");
            parametros.add("%" + trabajador.trim() + "%");
        }

        // Filtros de fecha
        if (fechaDesde != null) {
            sql.append(" AND DATE(o.fecha_orden) >= ?");
            parametros.add(fechaDesde.format(DATE_FORMATTER));
        }

        if (fechaHasta != null) {
            sql.append(" AND DATE(o.fecha_orden) <= ?");
            parametros.add(fechaHasta.format(DATE_FORMATTER));
        }

        sql.append(" ORDER BY o.fecha_orden DESC");

        List<VentaInfo> ventas = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            // Asignar parámetros dinámicamente
            for (int i = 0; i < parametros.size(); i++) {
                Object param = parametros.get(i);
                if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    VentaInfo venta = new VentaInfo(
                            rs.getInt("id_orden"),
                            rs.getString("nombre_cliente"),
                            rs.getString("nombre_trabajador"),
                            LocalDate.parse(rs.getString("fecha_orden"), DATE_FORMATTER));
                    ventas.add(venta);
                }
            }
        }
        return ventas;
    }
}