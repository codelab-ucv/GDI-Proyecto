package ucv.codelab.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import ucv.codelab.model.Cliente;
import ucv.codelab.util.SQLiteConexion;

/**
 * Repositorio para la gestión de clientes en la base de datos.
 * 
 * <p>
 * Esta clase extiende {@link BaseRepository} y proporciona operaciones CRUD
 * específicas para la entidad {@link Cliente}, incluyendo métodos de búsqueda
 * por DNI y nombre.
 * </p>
 * 
 * <p>
 * <strong>Tabla asociada:</strong> {@code cliente}
 * </p>
 * 
 * <p>
 * <strong>Campos de la tabla:</strong>
 * </p>
 * <ul>
 * <li>{@code id_cliente} - Clave primaria autoincremental</li>
 * <li>{@code nombre_cliente} - Nombre completo del cliente</li>
 * <li>{@code dni_cliente} - Documento de identidad del cliente</li>
 * <li>{@code telefono} - Número de teléfono (opcional)</li>
 * <li>{@code email_cliente} - Correo electrónico (opcional)</li>
 * </ul>
 * 
 * @see BaseRepository
 * @see Cliente
 */
public class ClienteRepository extends BaseRepository<Cliente> {

    /**
     * Constructor que inicializa el repositorio con la conexión a la base de datos.
     * 
     * @throws SQLException Si ocurre un error al obtener la conexión a la base de
     *                      datos
     */
    public ClienteRepository() throws SQLException {
        super(SQLiteConexion.getInstance().getConexion());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTableName() {
        return "cliente";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getIdColumnName() {
        return "id_cliente";
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Mapea todos los campos de la tabla cliente a un objeto Cliente,
     * incluyendo los campos opcionales como teléfono y email.
     * </p>
     */
    @Override
    protected Cliente mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("nombre_cliente"),
                rs.getString("dni_cliente"),
                rs.getString("telefono"),
                rs.getString("email_cliente"));
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Establece los parámetros para insertar un nuevo cliente,
     * incluyendo los campos opcionales teléfono y email.
     * </p>
     */
    @Override
    protected void setStatementParametersForInsert(PreparedStatement stmt, Cliente cliente) throws SQLException {
        stmt.setString(1, cliente.getNombreCliente());
        stmt.setString(2, cliente.getDniCliente());
        stmt.setString(3, cliente.getTelefono());
        stmt.setString(4, cliente.getEmailCliente());
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Establece los parámetros para actualizar un cliente existente,
     * incluyendo el ID del cliente como último parámetro.
     * </p>
     */
    @Override
    protected void setStatementParametersForUpdate(PreparedStatement stmt, Cliente cliente) throws SQLException {
        stmt.setString(1, cliente.getNombreCliente());
        stmt.setString(2, cliente.getDniCliente());
        stmt.setString(3, cliente.getTelefono());
        stmt.setString(4, cliente.getEmailCliente());
        stmt.setInt(5, cliente.getIdCliente());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String buildInsertSQL() {
        return "INSERT INTO cliente (nombre_cliente, dni_cliente, telefono, email_cliente) VALUES (?, ?, ?, ?)";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String buildUpdateSQL() {
        return "UPDATE cliente SET nombre_cliente = ?, dni_cliente = ?, telefono = ?, email_cliente = ? WHERE id_cliente = ?";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateEntityWithGeneratedId(Cliente cliente, ResultSet generatedKeys)
            throws SQLException {
        int id = generatedKeys.getInt(1);
        cliente.setIdCliente(id);
    }

    /**
     * Busca un cliente por su documento de identidad (DNI).
     * 
     * <p>
     * Este método es útil para verificar si un cliente ya existe en el sistema
     * antes de crear un nuevo registro o para recuperar información específica
     * de un cliente conocido.
     * </p>
     * 
     * @param dni DNI del cliente a buscar
     * @return Optional con el cliente encontrado o vacío si no existe
     */
    public Optional<Cliente> findByDni(String dni) {
        String sql = "SELECT * FROM cliente WHERE dni_cliente = ?";
        return executeQueryForSingleResult(sql, dni);
    }

    /**
     * Busca clientes cuyo nombre coincida parcialmente con el texto dado.
     * 
     * <p>
     * Utiliza el operador LIKE con wildcards para realizar una búsqueda
     * case-insensitive que incluye cualquier cliente cuyo nombre contenga
     * el texto especificado como substring.
     * </p>
     * 
     * <p>
     * Este método es útil para implementar funcionalidades de autocompletado
     * o búsqueda dinámica en interfaces de usuario.
     * </p>
     * 
     * @param nombre Texto a buscar en el nombre del cliente
     * @return Lista de clientes que coinciden con el criterio de búsqueda
     */
    public List<Cliente> findByNombre(String nombre) {
        String sql = "SELECT * FROM cliente WHERE nombre_cliente LIKE ?";
        return executeQuery(sql, "%" + nombre + "%");
    }
}