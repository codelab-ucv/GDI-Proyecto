package ucv.codelab.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Clase utilitaria para inicializar la base de datos desde un archivo SQL
 */
public class DatabaseInitializer {

    // Nombre del archivo SQL
    private static final String SQL_FILE = "ucv/codelab/sql/gdi.sql";

    /**
     * Inicializa la base de datos utilizando el archivo SQL de esquema
     * 
     * @return true si la inicialización fue exitosa
     */
    public static boolean initializeDatabase() {
        try {
            // Obtener conexión
            Connection conexion = SQLiteConexion.getInstance().getConexion();

            // Leer el contenido del archivo SQL desde los recursos
            String sqlScript = loadResourceAsString(SQL_FILE);

            if (sqlScript == null || sqlScript.isEmpty()) {
                System.err.println("No se pudo cargar el archivo de esquema SQL");
                return false;
            }

            // Ejecutar el script SQL
            Statement statement = conexion.createStatement();
            statement.executeUpdate(sqlScript);
            statement.close();

            System.out.println("Base de datos inicializada correctamente.");
            return true;

        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
            return false;
        }
    }

    /**
     * Carga un archivo de recursos como string
     * 
     * @param resourceName nombre del archivo en el classpath
     * @return contenido del archivo como string o null si ocurre un error
     */
    private static String loadResourceAsString(String resourceName) {
        try (InputStream is = DatabaseInitializer.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                System.err.println("No se encontró el recurso: " + resourceName);
                return null;
            }

            try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                    BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo SQL: " + e.getMessage());
            return null;
        }
    }
}