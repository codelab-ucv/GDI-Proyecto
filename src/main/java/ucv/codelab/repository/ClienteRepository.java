package ucv.codelab.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import ucv.codelab.model.Cliente;

/**
 * Repositorio para la entidad Cliente
 */
public class ClienteRepository extends BaseRepository<Cliente> {

    /**
     * Constructor
     * 
     * @param connection Conexión a la base de datos
     */
    public ClienteRepository(Connection connection) {
        super(connection);
    }

    @Override
    protected String getTableName() {
        return "cliente";
    }

    @Override
    protected String getIdColumnName() {
        return "id_cliente";
    }

    @Override
    protected Cliente mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("nombre_cliente"),
                rs.getString("dni_cliente"),
                rs.getString("telefono"),
                rs.getString("email_cliente"));
    }

    @Override
    protected void setStatementParametersForInsert(PreparedStatement stmt, Cliente cliente) throws SQLException {
        stmt.setString(1, cliente.getNombreCliente());
        stmt.setString(2, cliente.getDniCliente());
        stmt.setString(3, cliente.getTelefono());
        stmt.setString(4, cliente.getEmailCliente());
    }

    @Override
    protected void setStatementParametersForUpdate(PreparedStatement stmt, Cliente cliente) throws SQLException {
        stmt.setString(1, cliente.getNombreCliente());
        stmt.setString(2, cliente.getDniCliente());
        stmt.setString(3, cliente.getTelefono());
        stmt.setString(4, cliente.getEmailCliente());
        stmt.setInt(5, cliente.getIdCliente());
    }

    @Override
    protected String buildInsertSQL() {
        return "INSERT INTO cliente (nombre_cliente, dni_cliente, telefono, email_cliente) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected String buildUpdateSQL() {
        return "UPDATE cliente SET nombre_cliente = ?, dni_cliente = ?, telefono = ?, email_cliente = ? WHERE id_cliente = ?";
    }

    @Override
    protected void updateEntityWithGeneratedId(Cliente cliente, ResultSet generatedKeys)
            throws SQLException {
        int id = generatedKeys.getInt(1);
        cliente.setIdCliente(id);
    }

    /**
     * Busca un cliente por su DNI
     *  
     * @param dni DNI del cliente a buscar.
     * @return Optional con el cliente encontrado o vacío si no existe.
     */
    public Optional<Cliente> findByDni(String dni) {
        String sql = "SELECT * FROM cliente WHERE dni_cliente = ?";
        return executeQueryForSingleResult(sql, dni);
    }

    /**
     * Busca clientes cuyo nombre coincida parcialmente con el texto dado.
     * 
     * @param nombre Texto a buscar en el nombre del cliente.
     * @return Lista de clientes que coinciden con el criterio.
     */
    public List<Cliente> findByNombre(String nombre) {
        String sql = "SELECT * FROM cliente WHERE nombre_cliente LIKE ?";
        return executeQuery(sql, "%" + nombre + "%");
    }
}
