package ucv.codelab.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import ucv.codelab.model.Empresa;
import ucv.codelab.util.SQLiteConexion;

/**
 * Repositorio para la entidad Empresa
 */
public class EmpresaRepository extends BaseRepository<Empresa> {

    /**
     * Constructor
     * 
     * @param connection Conexión a la base de datos
     * @throws SQLException Si no se puede establecer la conexión
     */
    public EmpresaRepository() throws SQLException {
        super(SQLiteConexion.getInstance().getConexion());
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

    /**
     * Obtiene la ultima empresa registrada en la base de datos
     * 
     * @return Devuelve la ultima empresa registrada, en caso de no encontrarse
     *         devuelve una empresa por defecto
     */
    public Empresa getLastId() {
        String sql = "SELECT * FROM empresa ORDER BY id_empresa DESC LIMIT 1;";
        Optional<Empresa> empresa = executeQueryForSingleResult(sql);
        return empresa.isPresent() ? empresa.get() : new Empresa("GDI", "20123456789");
    }

    /**
     * Confirma si una empresa se encuentra previamente registrada en la base de
     * datos segun su Nombre y RUC
     * 
     * @param nombre nombre de la empresa que se intenta subir
     * @param ruc    ruc de la empresa que se intenta subir
     * @return Optional de la empresa encontrada o vacío si no existe
     */
    public Optional<Empresa> empresaExiste(String nombre, String ruc) {
        String sql = "SELECT * FROM empresa WHERE nombre_empresa = ? AND ruc = ?";
        return executeQueryForSingleResult(sql, nombre, ruc);
    }
}
