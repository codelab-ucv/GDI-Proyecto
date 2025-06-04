package ucv.codelab.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ucv.codelab.model.Producto;
import ucv.codelab.util.SQLiteConexion;

public class ProductoRepository extends BaseRepository<Producto> {

    public ProductoRepository() throws SQLException {
        super(SQLiteConexion.getInstance().getConexion());
    }

    @Override
    protected String getTableName() {
        return "producto";
    }

    @Override
    protected String getIdColumnName() {
        return "id_producto";
    }

    @Override
    protected Producto mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id_producto"),
                rs.getString("nombre_producto"),
                rs.getDouble("precio"),
                rs.getBoolean("vigente")); // Se convierte el INT (0/1) a boolean
    }

    @Override
    protected void setStatementParametersForInsert(PreparedStatement stmt, Producto producto) throws SQLException {
        stmt.setString(1, producto.getNombreProducto());
        stmt.setDouble(2, producto.getPrecio());
        // No se incluye el insert de vigente porque por defecto está activo
    }

    @Override
    protected void setStatementParametersForUpdate(PreparedStatement stmt, Producto producto) throws SQLException {
        stmt.setString(1, producto.getNombreProducto());
        stmt.setDouble(2, producto.getPrecio());
        // Convierte el boolean en un INT (0/1)
        stmt.setInt(3, producto.isVigente() ? 1 : 0);
        stmt.setInt(4, producto.getIdProducto());
    }

    @Override
    protected String buildInsertSQL() {
        return "INSERT INTO producto (nombre_producto, precio) VALUES (?, ?)";
    }

    @Override
    protected String buildUpdateSQL() {
        return "UPDATE producto SET nombre_producto = ?, precio = ?, vigente = ? WHERE id_producto = ?";
    }

    @Override
    protected void updateEntityWithGeneratedId(Producto producto, ResultSet generatedKeys) throws SQLException {
        int id = generatedKeys.getInt(1);
        producto.setIdProducto(id);
    }

    /**
     * Busca productos por nombre (búsqueda parcial)
     * 
     * @param nombre Texto a buscar en el nombre del producto
     * @return Lista de productos que coinciden con el criterio
     */
    public List<Producto> findByNombre(String nombre) {
        String sql = "SELECT * FROM producto WHERE nombre_producto LIKE ?";
        return executeQuery(sql, "%" + nombre + "%");
    }

    /**
     * Busca productos vigentes por nombre (búsqueda parcial)
     * 
     * @param nombre Texto a buscar en el nombre del producto
     * @return Lista de productos vigentes que coinciden con el criterio
     */
    public List<Producto> findByNombreVigentes(String nombre) {
        String sql = "SELECT * FROM producto WHERE nombre_producto LIKE ? AND vigente = 1";
        return executeQuery(sql, "%" + nombre + "%");
    }

    /**
     * Busca productos vigentes
     * 
     * @return Lista de productos con vigente = 1
     */
    public List<Producto> findVigentes() {
        String sql = "SELECT * FROM producto WHERE vigente = 1";
        return executeQuery(sql);
    }

    /**
     * Busca productos por rango de precio
     * 
     * @param precioMin Precio mínimo
     * @param precioMax Precio máximo
     * @return Lista de productos en el rango de precios
     */
    public List<Producto> findByPrecioRange(double precioMin, double precioMax) {
        String sql = "SELECT * FROM producto WHERE precio BETWEEN ? AND ?";
        return executeQuery(sql, precioMin, precioMax);
    }
}