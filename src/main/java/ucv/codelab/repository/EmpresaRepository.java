package ucv.codelab.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import ucv.codelab.model.Empresa;
import ucv.codelab.util.SQLiteConexion;

/**
 * Repositorio para la gestión de empresas en la base de datos.
 * 
 * <p>
 * Esta clase extiende {@link BaseRepository} y proporciona operaciones CRUD
 * específicas para la entidad {@link Empresa}, incluyendo métodos de búsqueda
 * por RUC, nombre y validación de existencia de empresas.
 * </p>
 * 
 * <p>
 * <strong>Tabla asociada:</strong> {@code empresa}
 * </p>
 * 
 * <p>
 * <strong>Campos de la tabla:</strong>
 * </p>
 * <ul>
 * <li>{@code id_empresa} - Clave primaria autoincremental</li>
 * <li>{@code nombre_empresa} - Nombre de la empresa</li>
 * <li>{@code ruc} - Registro Único de Contribuyente (único)</li>
 * <li>{@code email_empresa} - Correo electrónico de la empresa (opcional)</li>
 * <li>{@code ubicacion} - Dirección física de la empresa (opcional)</li>
 * <li>{@code logo} - Ruta o referencia al logotipo de la empresa
 * (opcional)</li>
 * </ul>
 * 
 * @see BaseRepository
 * @see Empresa
 */
public class EmpresaRepository extends BaseRepository<Empresa> {

    /**
     * Constructor que inicializa el repositorio con la conexión a la base de datos.
     * 
     * @throws SQLException Si ocurre un error al obtener la conexión a la base de
     *                      datos
     */
    public EmpresaRepository() throws SQLException {
        super(SQLiteConexion.getInstance().getConexion());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTableName() {
        return "empresa";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getIdColumnName() {
        return "id_empresa";
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Mapea todos los campos de la tabla empresa a un objeto Empresa,
     * incluyendo los campos opcionales como email, ubicación y logo.
     * </p>
     */
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

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Establece los parámetros para insertar una nueva empresa,
     * incluyendo todos los campos opcionales.
     * </p>
     */
    @Override
    protected void setStatementParametersForInsert(PreparedStatement stmt,
            Empresa empresa) throws SQLException {
        stmt.setString(1, empresa.getNombreEmpresa());
        stmt.setString(2, empresa.getRuc());
        stmt.setString(3, empresa.getEmailEmpresa());
        stmt.setString(4, empresa.getUbicacion());
        stmt.setString(5, empresa.getLogo());
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Establece los parámetros para actualizar una empresa existente,
     * incluyendo el ID de la empresa como último parámetro.
     * </p>
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected String buildInsertSQL() {
        return "INSERT INTO empresa (nombre_empresa, ruc, email_empresa, ubicacion, logo) VALUES (?, ?, ?, ?, ?)";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String buildUpdateSQL() {
        return "UPDATE empresa SET nombre_empresa = ?, ruc = ?, email_empresa = ?, ubicacion = ?, logo = ? WHERE id_empresa = ?";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateEntityWithGeneratedId(Empresa empresa, ResultSet generatedKeys)
            throws SQLException {
        int id = generatedKeys.getInt(1);
        empresa.setIdEmpresa(id);
    }

    /**
     * Busca una empresa por su Registro Único de Contribuyente (RUC).
     * 
     * <p>
     * El RUC debe ser único en la base de datos, por lo que este método
     * retorna un Optional que contiene la empresa si existe.
     * </p>
     * 
     * @param ruc RUC de la empresa a buscar
     * @return Optional con la empresa encontrada o vacío si no existe
     */
    public Optional<Empresa> findByRuc(String ruc) {
        String sql = "SELECT * FROM empresa WHERE ruc = ?";
        return executeQueryForSingleResult(sql, ruc);
    }

    /**
     * Busca empresas cuyo nombre coincida parcialmente con el texto dado.
     * 
     * <p>
     * Utiliza el operador LIKE con wildcards para realizar una búsqueda
     * case-sensitive que incluye cualquier empresa cuyo nombre contenga
     * el texto especificado como substring.
     * </p>
     * 
     * <p>
     * Este método es útil para implementar funcionalidades de autocompletado
     * o búsqueda dinámica en interfaces de usuario.
     * </p>
     * 
     * @param nombre Texto a buscar en el nombre de la empresa
     * @return Lista de empresas que coinciden con el criterio de búsqueda
     */
    public List<Empresa> findByNombre(String nombre) {
        String sql = "SELECT * FROM empresa WHERE nombre_empresa LIKE ?";
        return executeQuery(sql, "%" + nombre + "%");
    }

    /**
     * Obtiene la última empresa registrada en la base de datos.
     * 
     * <p>
     * Utiliza ORDER BY con DESC y LIMIT 1 para obtener eficientemente
     * el registro más reciente basado en el ID autoincremental.
     * </p>
     * 
     * <p>
     * Si no existe ninguna empresa en la base de datos, retorna una empresa
     * por defecto con nombre "GDI" y RUC "20123456789" para mantener
     * la funcionalidad del sistema.
     * </p>
     * 
     * @return La última empresa registrada, o una empresa por defecto si no existe
     *         ninguna
     */
    public Empresa getLastId() {
        String sql = "SELECT * FROM empresa ORDER BY id_empresa DESC LIMIT 1;";
        Optional<Empresa> empresa = executeQueryForSingleResult(sql);
        return empresa.isPresent() ? empresa.get() : new Empresa("GDI", "20123456789");
    }

    /**
     * Verifica si una empresa ya existe en la base de datos basándose en su nombre
     * y RUC.
     * 
     * <p>
     * Este método es útil para validar duplicados antes de insertar una nueva
     * empresa,
     * asegurando que no se registren empresas con la misma combinación de nombre
     * y RUC en el sistema.
     * </p>
     * 
     * <p>
     * La búsqueda requiere coincidencia exacta tanto del nombre como del RUC,
     * proporcionando una validación robusta contra duplicados.
     * </p>
     * 
     * @param nombre Nombre de la empresa a verificar
     * @param ruc    RUC de la empresa a verificar
     * @return Optional con la empresa encontrada si existe, o vacío si no hay
     *         duplicados
     */
    public Optional<Empresa> empresaExiste(String nombre, String ruc) {
        String sql = "SELECT * FROM empresa WHERE nombre_empresa = ? AND ruc = ?";
        return executeQueryForSingleResult(sql, nombre, ruc);
    }
}