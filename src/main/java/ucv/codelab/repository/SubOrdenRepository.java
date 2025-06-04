package ucv.codelab.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ucv.codelab.model.SubOrden;
import ucv.codelab.util.SQLiteConexion;

public class SubOrdenRepository extends BaseRepository<SubOrden> {

    public SubOrdenRepository() throws SQLException {
        super(SQLiteConexion.getInstance().getConexion());
    }

    @Override
    protected String getTableName() {
        return "sub_orden";
    }

    @Override
    protected String getIdColumnName() {
        return "id_sub_orden";
    }

    @Override
    protected SubOrden mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new SubOrden(
                rs.getInt("id_sub_orden"),
                rs.getInt("id_orden"),
                rs.getInt("id_producto"),
                rs.getInt("cantidad"));
    }

    @Override
    protected void setStatementParametersForInsert(PreparedStatement stmt, SubOrden subOrden) throws SQLException {
        stmt.setInt(1, subOrden.getIdOrden());
        stmt.setInt(2, subOrden.getIdProducto());
        stmt.setInt(3, subOrden.getCantidad());
    }

    @Override
    protected void setStatementParametersForUpdate(PreparedStatement stmt, SubOrden subOrden) throws SQLException {
        stmt.setInt(1, subOrden.getIdOrden());
        stmt.setInt(2, subOrden.getIdProducto());
        stmt.setInt(3, subOrden.getCantidad());
        stmt.setInt(4, subOrden.getIdSubOrden());
    }

    @Override
    protected String buildInsertSQL() {
        return "INSERT INTO sub_orden (id_orden, id_producto, cantidad) VALUES (?, ?, ?)";
    }

    @Override
    protected String buildUpdateSQL() {
        return "UPDATE sub_orden SET id_orden = ?, id_producto = ?, cantidad = ? WHERE id_sub_orden = ?";
    }

    @Override
    protected void updateEntityWithGeneratedId(SubOrden subOrden, ResultSet generatedKeys) throws SQLException {
        int id = generatedKeys.getInt(1);
        subOrden.setIdSubOrden(id);
    }

    /**
     * Busca subórdenes por ID de orden principal
     * @param idOrden ID de la orden principal
     * @return Lista de subórdenes asociadas
     */
    public List<SubOrden> findByOrden(int idOrden) {
        String sql = "SELECT * FROM sub_orden WHERE id_orden = ?";
        return executeQuery(sql, idOrden);
    }

    /**
     * Busca subórdenes por ID de producto
     * @param idProducto ID del producto
     * @return Lista de subórdenes que contienen el producto
     */
    public List<SubOrden> findByProducto(int idProducto) {
        String sql = "SELECT * FROM sub_orden WHERE id_producto = ?";
        return executeQuery(sql, idProducto);
    }

    /**
     * Elimina todas las subórdenes asociadas a una orden principal
     * @param idOrden ID de la orden principal
     * @return Número de registros eliminados
     */
    public int deleteByOrden(int idOrden) throws SQLException {
        String sql = "DELETE FROM sub_orden WHERE id_orden = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idOrden);
            return stmt.executeUpdate();
        }
    }
}