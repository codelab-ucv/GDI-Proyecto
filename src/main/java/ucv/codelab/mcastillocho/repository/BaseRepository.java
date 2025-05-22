package ucv.codelab.mcastillocho.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio base abstracto para todas las entidades.
 * Esta clase proporciona la implementación común para operaciones CRUD,
 * trabajando con SQLite como base de datos.
 * 
 * @param <T> Tipo de entidad que maneja el repositorio
 */
public abstract class BaseRepository<T> {

    /**
     * Conexión a la base de datos SQLite
     */
    protected Connection connection;

    /**
     * Constructor que recibe la conexión a la base de datos
     * 
     * @param connection Conexión activa a la base de datos SQLite
     */
    public BaseRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Obtiene el nombre de la tabla en la base de datos
     * 
     * @return Nombre de la tabla
     */
    protected abstract String getTableName();

    /**
     * Obtiene el nombre de la columna que representa el ID en la tabla
     * 
     * @return Nombre de la columna ID
     */
    protected abstract String getIdColumnName();

    /**
     * Convierte un ResultSet en una entidad del tipo correspondiente
     * 
     * @param rs ResultSet con los datos de la entidad
     * @return Entidad creada a partir del ResultSet
     * @throws SQLException si ocurre un error al acceder a los datos
     */
    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;

    /**
     * Establece los parámetros para la sentencia SQL de inserción
     * 
     * @param stmt   PreparedStatement para la inserción
     * @param entity Entidad cuyos datos se insertarán
     * @throws SQLException si ocurre un error al establecer los parámetros
     */
    protected abstract void setStatementParametersForInsert(PreparedStatement stmt, T entity) throws SQLException;

    /**
     * Establece los parámetros para la sentencia SQL de actualización
     * 
     * @param stmt   PreparedStatement para la actualización
     * @param entity Entidad cuyos datos se actualizarán
     * @throws SQLException si ocurre un error al establecer los parámetros
     */
    protected abstract void setStatementParametersForUpdate(PreparedStatement stmt, T entity) throws SQLException;

    /**
     * Construye la sentencia SQL para inserción
     * 
     * @return Sentencia SQL para insertar una entidad
     */
    protected abstract String buildInsertSQL();

    /**
     * Construye la sentencia SQL para actualización
     * 
     * @return Sentencia SQL para actualizar una entidad
     */
    protected abstract String buildUpdateSQL();

    /**
     * Actualiza la entidad con el ID generado automáticamente
     * 
     * @param entity        Entidad a actualizar
     * @param generatedKeys ResultSet con las claves generadas
     * @throws SQLException si ocurre un error al obtener las claves
     */
    protected abstract void updateEntityWithGeneratedId(T entity, ResultSet generatedKeys) throws SQLException;

    /**
     * Busca una entidad por su ID
     * 
     * @param id ID de la entidad a buscar
     * @return Optional que contiene la entidad si existe, o vacío si no existe
     */
    public Optional<T> findById(int id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, (Integer) id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToEntity(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar entidad por ID: " + id, e);
        }
    }

    /**
     * Obtiene todas las entidades de la tabla
     * 
     * @return Lista con todas las entidades
     */
    public List<T> findAll() {
        List<T> result = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName();

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(mapResultSetToEntity(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todas las entidades de " + getTableName(), e);
        }
    }

    /**
     * Guarda una nueva entidad en la base de datos
     * 
     * @param entity Entidad a guardar
     */
    public void save(T entity) {
        String sql = buildInsertSQL();

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setStatementParametersForInsert(stmt, entity);
            stmt.executeUpdate();

            // Obtener ID generado si es necesario
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // Actualiza el modelo colocando el ID generado
                    updateEntityWithGeneratedId(entity, generatedKeys);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar entidad en " + getTableName(), e);
        }
    }

    /**
     * Actualiza una entidad existente en la base de datos
     * 
     * @param entity Entidad a actualizar
     */
    public void update(T entity) {
        String sql = buildUpdateSQL();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setStatementParametersForUpdate(stmt, entity);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar entidad en " + getTableName(), e);
        }
    }

    /**
     * Elimina una entidad de la base de datos por su ID
     * 
     * @param id ID de la entidad a eliminar
     */
    public void delete(int id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, (Integer) id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar entidad con ID: " + id + " de " + getTableName(), e);
        }
    }

    /**
     * Ejecuta una consulta personalizada
     * 
     * @param sql        Sentencia SQL a ejecutar
     * @param parameters Parámetros para la sentencia preparada
     * @return Lista de entidades que coinciden con la consulta
     */
    protected List<T> executeQuery(String sql, Object... parameters) {
        List<T> result = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Establecer parámetros
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i] instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) parameters[i]);
                } else if (parameters[i] instanceof String) {
                    stmt.setString(i + 1, (String) parameters[i]);
                } else if (parameters[i] instanceof Double) {
                    stmt.setDouble(i + 1, (Double) parameters[i]);
                } else if (parameters[i] == null) {
                    stmt.setNull(i + 1, Types.NULL);
                } else {
                    throw new IllegalArgumentException(
                            "Tipo de parámetro no soportado: " + parameters[i].getClass().getName());
                }
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(mapResultSetToEntity(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error al ejecutar consulta personalizada: " + sql, e);
        }
    }

    /**
     * Ejecuta una consulta que devuelve una única entidad o ninguna
     * 
     * @param sql        Sentencia SQL a ejecutar
     * @param parameters Parámetros para la sentencia preparada
     * @return Optional con la entidad encontrada o vacío si no existe
     */
    protected Optional<T> executeQueryForSingleResult(String sql, Object... parameters) {
        List<T> results = executeQuery(sql, parameters);
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(results.get(0));
    }
}