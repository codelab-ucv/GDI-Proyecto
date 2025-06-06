package ucv.codelab.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import ucv.codelab.model.Trabajador;
import ucv.codelab.util.SQLiteConexion;

public class TrabajadorRepository extends BaseRepository<Trabajador> {

    public TrabajadorRepository() throws SQLException {
        super(SQLiteConexion.getInstance().getConexion());
    }

    @Override
    protected String getTableName() {
        return "trabajador";
    }

    @Override
    protected String getIdColumnName() {
        return "id_trabajador";
    }

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

    @Override
    protected void setStatementParametersForInsert(PreparedStatement stmt, Trabajador trabajador) throws SQLException {
        stmt.setString(1, trabajador.getNombreTrabajador());
        stmt.setString(2, trabajador.getDniTrabajador());
        stmt.setString(3, trabajador.getPuesto());
        stmt.setString(4, trabajador.getTipoLetra());
        stmt.setString(5, trabajador.getColorFondo());
        stmt.setString(6, trabajador.getPassword());
    }

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

    @Override
    protected String buildInsertSQL() {
        return "INSERT INTO trabajador (nombre_trabajador, dni_trabajador, puesto, tipo_letra, color_fondo, password) "
                +
                "VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String buildUpdateSQL() {
        return "UPDATE trabajador SET nombre_trabajador = ?, dni_trabajador = ?, puesto = ?, " +
                "tipo_letra = ?, color_fondo = ?, password = ? WHERE id_trabajador = ?";
    }

    @Override
    protected void updateEntityWithGeneratedId(Trabajador trabajador, ResultSet generatedKeys) throws SQLException {
        int id = generatedKeys.getInt(1);
        trabajador.setIdTrabajador(id);
    }

    /**
     * Busca un trabajador por su DNI
     * 
     * @param dni DNI del trabajador a buscar
     * @return Optional con el trabajador encontrado o vacío si no existe
     */
    public Optional<Trabajador> findByDni(String dni) {
        String sql = "SELECT * FROM trabajador WHERE dni_trabajador = ?";
        return executeQueryForSingleResult(sql, dni);
    }

    /**
     * Busca trabajadores por nombre (búsqueda parcial)
     * 
     * @param nombre Texto a buscar en el nombre del trabajador
     * @return Lista de trabajadores que coinciden con el criterio
     */
    public List<Trabajador> findByNombre(String nombre) {
        String sql = "SELECT * FROM trabajador WHERE nombre_trabajador LIKE ?";
        return executeQuery(sql, "%" + nombre + "%");
    }

    /**
     * Verifica si no existe ningún trabajador con el puesto "JEFE" en la base de
     * datos.
     * 
     * @return {@code true} si no hay trabajadores con puesto "JEFE", {@code false}
     *         si existe al menos uno.
     */
    public boolean sinJefe() {
        String sql = "SELECT * FROM trabajador WHERE puesto = ? LIMIT 1";
        return executeQueryForSingleResult(sql, "JEFE").isEmpty();
    }

    /**
     * Realiza la autenticación de un trabajador mediante su DNI y contraseña.
     * 
     * @param user     DNI del trabajador (usado como nombre de usuario).
     * @param password Contraseña del trabajador.
     * @return Un Optional con el Trabajador si las credenciales son válidas o vacío
     *         si no se encuentra coincidencia.
     */
    public Optional<Trabajador> login(String user, String password) {
        String sql = "SELECT * FROM trabajador WHERE dni_trabajador = ? AND password = ?";
        return executeQueryForSingleResult(sql, user, password);
    }
}