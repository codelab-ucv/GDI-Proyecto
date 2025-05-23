package ucv.codelab.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import ucv.codelab.model.Empresa;

/**
 * Repositorio para la entidad Empresa
 */
public class EmpresaRepository extends BaseRepository<Empresa> {

    /**
     * Constructor
     * 
     * @param connection Conexión a la base de datos
     */
    public EmpresaRepository(Connection connection) {
        super(connection);
    }

    @Override
    protected String getTableName() {
        return "empresa";
    }

    @Override
    protected String getIdColumnName() {
        return "id_empresa";
    }

    @Override
    protected Empresa mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Empresa(
                rs.getInt("id_empresa"),
                rs.getString("nombre_empresa"),
                rs.getString("ruc"),
                rs.getString("email_empresa"),
                rs.getString("ubicacion"),
                rs.getString("logo"));
    }

    @Override
    protected void setStatementParametersForInsert(PreparedStatement stmt,
            Empresa empresa) throws SQLException {
        stmt.setString(1, empresa.getNombreEmpresa());
        stmt.setString(2, empresa.getRuc());
        stmt.setString(3, empresa.getEmailEmpresa());
        stmt.setString(4, empresa.getUbicacion());
        stmt.setString(5, empresa.getLogo());
    }

    @Override
    protected void setStatementParametersForUpdate(PreparedStatement stmt,
            Empresa empresa) throws SQLException {
        stmt.setString(1, empresa.getNombreEmpresa());
        stmt.setString(2, empresa.getRuc());
        stmt.setString(3, empresa.getEmailEmpresa());
        stmt.setString(4, empresa.getUbicacion());
        stmt.setString(5, empresa.getLogo());
        stmt.setInt(6, empresa.getIdEmpresa());
    }

    @Override
    protected String buildInsertSQL() {
        return "INSERT INTO empresa (nombre_empresa, ruc, email_empresa, ubicacion, logo) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    protected String buildUpdateSQL() {
        return "UPDATE empresa SET nombre_empresa = ?, ruc = ?, email_empresa = ?, ubicacion = ?, logo = ? WHERE id_empresa = ?";
    }

    @Override
    protected void updateEntityWithGeneratedId(Empresa empresa, ResultSet generatedKeys)
            throws SQLException {
        int id = generatedKeys.getInt(1);
        empresa.setIdEmpresa(id);
    }

    /**
     * Busca una empresa por su RUC
     * 
     * @param ruc RUC de la empresa a buscar
     * @return Optional con la empresa encontrada o vacío si no existe
     */
    public Optional<Empresa> findByRuc(String ruc) {
        String sql = "SELECT * FROM empresa WHERE ruc = ?";
        return executeQueryForSingleResult(sql, ruc);
    }

    /**
     * Busca empresas que contengan el nombre especificado
     * 
     * @param nombre Texto a buscar en el nombre de la empresa
     * @return Lista de empresas que coinciden con el criterio
     */
    public List<Empresa> findByNombre(String nombre) {
        String sql = "SELECT * FROM empresa WHERE nombre_empresa LIKE ?";
        return executeQuery(sql, "%" + nombre + "%");
    }
}
