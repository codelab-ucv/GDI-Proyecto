package ucv.codelab.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import ucv.codelab.model.Trabajador;

public class TrabajadorRepository extends BaseRepository<Trabajador> {

    public TrabajadorRepository(Connection connection) {
        super(connection);
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
                rs.getString("color_boton"));
    }

    @Override
    protected void setStatementParametersForInsert(PreparedStatement stmt, Trabajador trabajador) throws SQLException {
        stmt.setString(1, trabajador.getNombreTrabajador());
        stmt.setString(2, trabajador.getDniTrabajador());
        stmt.setString(3, trabajador.getPuesto());
        stmt.setString(4, trabajador.getTipoLetra());
        stmt.setString(5, trabajador.getColorFondo());
        stmt.setString(6, trabajador.getColorBoton());
    }

    @Override
    protected void setStatementParametersForUpdate(PreparedStatement stmt, Trabajador trabajador) throws SQLException {
        stmt.setString(1, trabajador.getNombreTrabajador());
        stmt.setString(2, trabajador.getDniTrabajador());
        stmt.setString(3, trabajador.getPuesto());
        stmt.setString(4, trabajador.getTipoLetra());
        stmt.setString(5, trabajador.getColorFondo());
        stmt.setString(6, trabajador.getColorBoton());
        stmt.setInt(7, trabajador.getIdTrabajador());
    }

    @Override
    protected String buildInsertSQL() {
        return "INSERT INTO trabajador (nombre_trabajador, dni_trabajador, puesto, tipo_letra, color_fondo, color_boton) "
                +
                "VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String buildUpdateSQL() {
        return "UPDATE trabajador SET nombre_trabajador = ?, dni_trabajador = ?, puesto = ?, " +
                "tipo_letra = ?, color_fondo = ?, color_boton = ? WHERE id_trabajador = ?";
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
}