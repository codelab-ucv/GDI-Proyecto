package ucv.codelab.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase que implementa el patrón Singleton para gestionar una única conexión
 * a una base de datos SQLite almacenada en la carpeta Documents del usuario.
 */
public class SQLiteConexion {
    // Instancia única del singleton
    private static SQLiteConexion instancia;

    private Connection conexion;

    // Obtener la ruta a la carpeta Documents del usuario
    private final String userHome = System.getProperty("user.home");
    private final String documentsPath = userHome + File.separator + "Documents" + File.separator + "database";
    private final String dbName = "gdi.db";
    private final String rutaDB = documentsPath + File.separator + dbName;

    /**
     * Constructor privado para evitar múltiples instancias
     * 
     * @throws SQLException si ocurre un error al conectar a la base de datos
     */
    private SQLiteConexion() throws SQLException {
        if (!validarDirectorio()) {
            throw new SQLException("No se pudo crear el directorio para la base de datos");
        }
        if (!conectar()) {
            throw new SQLException("No se pudo establecer la conexión a la base de datos");
        }
    }

    /**
     * Método para obtener la instancia única de la conexión (Singleton)
     * 
     * @return Instancia única de SQLiteConexion
     * @throws SQLException si ocurre un error al crear la conexión
     */
    public static synchronized SQLiteConexion getInstance() throws SQLException {
        if (instancia == null) {
            instancia = new SQLiteConexion();
        }
        return instancia;
    }

    /**
     * Método para conectar a la base de datos
     * 
     * @return true si la conexión fue exitosa, false en caso contrario
     */
    private boolean conectar() {
        try {
            // Cargar el driver de SQLite
            Class.forName("org.sqlite.JDBC");

            // Establecer la conexión
            String url = "jdbc:sqlite:" + rutaDB;
            conexion = DriverManager.getConnection(url);

            System.out.println("Conexión establecida con éxito a la base de datos SQLite.");
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el driver SQLite: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos SQLite: " + e.getMessage());
            return false;
        }
    }

    /**
     * Valida que exista el directorio donde se almacenará la base de datos
     * 
     * @return true si el directorio existe o fue creado correctamente, false en
     *         caso contrario
     */
    private boolean validarDirectorio() {
        // Verificar si la carpeta Documents existe
        File carpetaDocuments = new File(documentsPath);
        if (!carpetaDocuments.exists()) {
            System.out.println("La carpeta Documents no existe. Creando carpeta...");
            if (carpetaDocuments.mkdir()) {
                System.out.println("Carpeta Documents creada con éxito.");
            } else {
                System.err.println("No se pudo crear la carpeta Documents.");
                return false;
            }
        }

        System.out.println("Intentando crear/conectar base de datos en: " + rutaDB);
        return true;
    }

    /**
     * Método para obtener la conexión directamente
     * 
     * @return Objeto Connection
     */
    public Connection getConexion() {
        return conexion;
    }

    /**
     * Cierra la conexión a la base de datos
     */
    public void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("Conexión cerrada correctamente.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}