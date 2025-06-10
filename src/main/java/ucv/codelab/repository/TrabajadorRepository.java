package ucv.codelab.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import ucv.codelab.model.Trabajador;
import ucv.codelab.util.SQLiteConexion;

/**
 * Repositorio para la gestión de trabajadores en la base de datos.
 * 
 * <p>
 * Esta clase extiende {@link BaseRepository} y proporciona operaciones CRUD
 * específicas para la entidad {@link Trabajador}, incluyendo métodos de
 * autenticación, búsqueda por DNI y validación de roles de jefe.
 * </p>
 * 
 * <p>
 * <strong>Tabla asociada:</strong> {@code trabajador}
 * </p>
 * 
 * <p>
 * <strong>Campos de la tabla:</strong>
 * </p>
 * <ul>
 * <li>{@code id_trabajador} - Clave primaria autoincremental</li>
 * <li>{@code nombre_trabajador} - Nombre completo del trabajador</li>
 * <li>{@code dni_trabajador} - Documento de identidad (único)</li>
 * <li>{@code puesto} - Cargo del trabajador (JEFE, SUPERVISOR, TRABAJADOR)</li>
 * <li>{@code tipo_letra} - Configuración de fuente para UI (opcional)</li>
 * <li>{@code color_fondo} - Color de fondo para UI (opcional)</li>
 * <li>{@code password} - Contraseña para autenticación</li>
 * </ul>
 * 
 * @see BaseRepository
 * @see Trabajador
 */
public class TrabajadorRepository extends BaseRepository<Trabajador> {

    /**
     * Constructor que inicializa el repositorio con la conexión a la base de datos.
     * 
     * @throws SQLException Si ocurre un error al obtener la conexión a la base de
     *                      datos
     */
    public TrabajadorRepository() throws SQLException {
        super(SQLiteConexion.getInstance().getConexion());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTableName() {
        return "trabajador";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getIdColumnName() {
        return "id_trabajador";
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Mapea todos los campos de la tabla trabajador a un objeto Trabajador,
     * incluyendo las configuraciones de UI opcionales.
     * </p>
     */
    @Override
    protected Trabajador mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Trabajador(
                rs.getInt("id_trabajador"),
                rs.getString("nombre_trabajador"),
                rs.getString("dni_trabajador"),
                rs.getString("puesto"),
                rs.getString("tipo_letra"),
                rs.getString("color_fondo"),
                rs.getString("password"));
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Establece todos los parámetros necesarios para insertar un nuevo trabajador,
     * incluyendo las configuraciones de UI opcionales.
     * </p>
     */
    @Override
    protected void setStatementParametersForInsert(PreparedStatement stmt, Trabajador trabajador) throws SQLException {
        stmt.setString(1, trabajador.getNombreTrabajador());
        stmt.setString(2, trabajador.getDniTrabajador());
        stmt.setString(3, trabajador.getPuesto());
        stmt.setString(4, trabajador.getTipoLetra());
        stmt.setString(5, trabajador.getColorFondo());
        stmt.setString(6, trabajador.getPassword());
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Establece todos los parámetros para actualizar un trabajador existente,
     * incluyendo el ID del trabajador como último parámetro.
     * </p>
     */
    @Override
    protected void setStatementParametersForUpdate(PreparedStatement stmt, Trabajador trabajador) throws SQLException {
        stmt.setString(1, trabajador.getNombreTrabajador());
        stmt.setString(2, trabajador.getDniTrabajador());
        stmt.setString(3, trabajador.getPuesto());
        stmt.setString(4, trabajador.getTipoLetra());
        stmt.setString(5, trabajador.getColorFondo());
        stmt.setString(6, trabajador.getPassword());
        stmt.setInt(7, trabajador.getIdTrabajador());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String buildInsertSQL() {
        return "INSERT INTO trabajador (nombre_trabajador, dni_trabajador, puesto, tipo_letra, color_fondo, password) "
                +
                "VALUES (?, ?, ?, ?, ?, ?)";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String buildUpdateSQL() {
        return "UPDATE trabajador SET nombre_trabajador = ?, dni_trabajador = ?, puesto = ?, " +
                "tipo_letra = ?, color_fondo = ?, password = ? WHERE id_trabajador = ?";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateEntityWithGeneratedId(Trabajador trabajador, ResultSet generatedKeys) throws SQLException {
        int id = generatedKeys.getInt(1);
        trabajador.setIdTrabajador(id);
    }

    /**
     * Busca un trabajador por su documento de identidad (DNI).
     * 
     * <p>
     * El DNI debe ser único en la base de datos, por lo que este método
     * retorna un Optional que contiene el trabajador si existe.
     * </p>
     * 
     * @param dni DNI del trabajador a buscar
     * @return Optional con el trabajador encontrado o vacío si no existe
     */
    public Optional<Trabajador> findByDni(String dni) {
        String sql = "SELECT * FROM trabajador WHERE dni_trabajador = ?";
        return executeQueryForSingleResult(sql, dni);
    }

    /**
     * Busca trabajadores por nombre utilizando búsqueda parcial (LIKE).
     * 
     * <p>
     * La búsqueda es case-sensitive y utiliza el operador LIKE con wildcards
     * para encontrar trabajadores cuyo nombre contenga el texto especificado.
     * </p>
     * 
     * @param nombre Texto a buscar en el nombre del trabajador
     * @return Lista de trabajadores que coinciden con el criterio de búsqueda
     */
    public List<Trabajador> findByNombre(String nombre) {
        String sql = "SELECT * FROM trabajador WHERE nombre_trabajador LIKE ?";
        return executeQuery(sql, "%" + nombre + "%");
    }

    /**
     * Verifica si no existe ningún trabajador con el puesto "JEFE" en la base de
     * datos.
     * 
     * <p>
     * Este método es útil para validar si se puede registrar el primer trabajador
     * con rol de JEFE o para verificar la integridad de los roles en el sistema.
     * Utiliza LIMIT 1 para optimizar la consulta ya que solo necesita verificar
     * existencia.
     * </p>
     * 
     * @return {@code true} si no hay trabajadores con puesto "JEFE", {@code false}
     *         si existe al menos uno
     */
    public boolean sinJefe() {
        String sql = "SELECT * FROM trabajador WHERE puesto = ? LIMIT 1";
        return executeQueryForSingleResult(sql, "JEFE").isEmpty();
    }

    /**
     * Realiza la autenticación de un trabajador mediante su DNI y contraseña.
     * 
     * <p>
     * Este método se utiliza para el proceso de login en el sistema.
     * El DNI actúa como nombre de usuario y debe coincidir exactamente
     * con la contraseña proporcionada.
     * </p>
     * 
     * <p>
     * <strong>Nota de seguridad:</strong> En un entorno de producción,
     * las contraseñas deberían estar hasheadas en la base de datos.
     * </p>
     * 
     * @param user     DNI del trabajador (usado como nombre de usuario)
     * @param password Contraseña del trabajador en texto plano
     * @return Optional con el Trabajador si las credenciales son válidas,
     *         o vacío si no se encuentra coincidencia
     */
    public Optional<Trabajador> login(String user, String password) {
        String sql = "SELECT * FROM trabajador WHERE dni_trabajador = ? AND password = ?";
        return executeQueryForSingleResult(sql, user, password);
    }
}