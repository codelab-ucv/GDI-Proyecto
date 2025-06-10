package ucv.codelab.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase que implementa el patrón Singleton para gestionar una única conexión
 * a una base de datos SQLite almacenada en la carpeta Documents del usuario.
 * 
 * <p>
 * Esta implementación garantiza que solo exista una instancia de conexión
 * a la base de datos durante toda la ejecución de la aplicación, proporcionando
 * un punto de acceso global y eficiente manejo de recursos.
 * </p>
 * 
 * <p>
 * <strong>Ubicación de la base de datos:</strong>
 * </p>
 * <ul>
 * <li>Directorio: {@code {user.home}/Documents/database/}</li>
 * <li>Nombre del archivo: {@code gdi.db}</li>
 * <li>URL de conexión: {@code jdbc:sqlite:{ruta_completa}}</li>
 * </ul>
 * 
 * <p>
 * <strong>Características principales:</strong>
 * </p>
 * <ul>
 * <li>Patrón Singleton thread-safe con sincronización</li>
 * <li>Creación automática de directorios si no existen</li>
 * <li>Reconexión automática en caso de conexión cerrada</li>
 * <li>Manejo robusto de excepciones y logging de errores</li>
 * </ul>
 * 
 * <p>
 * La clase gestiona automáticamente la creación del directorio de la base de
 * datos
 * en la carpeta Documents del usuario actual, asegurando que la aplicación
 * funcione correctamente independientemente del sistema operativo.
 * </p>
 * 
 * @see Connection
 * @see DriverManager
 */
public class SQLiteConexion {

    /**
     * Instancia única del singleton para garantizar una sola conexión
     */
    private static SQLiteConexion instancia;

    /**
     * Objeto Connection que mantiene la conexión activa a la base de datos
     */
    private Connection conexion;

    /**
     * Ruta al directorio home del usuario actual
     */
    private final String userHome = System.getProperty("user.home");

    /**
     * Ruta completa al directorio donde se almacenará la base de datos
     */
    private final String documentsPath = userHome + File.separator + "Documents" + File.separator + "database";

    /**
     * Nombre del archivo de la base de datos SQLite
     */
    private final String dbName = "gdi.db";

    /**
     * Ruta completa al archivo de la base de datos
     */
    private final String rutaDB = documentsPath + File.separator + dbName;

    /**
     * Constructor privado para evitar múltiples instancias del Singleton.
     * 
     * <p>
     * Inicializa la conexión validando primero que el directorio de destino
     * exista o pueda ser creado, y luego establece la conexión a la base de datos.
     * </p>
     * 
     * @throws SQLException Si ocurre un error al crear el directorio o al
     *                      establecer la conexión a la base de datos
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
     * Método para obtener la instancia única de la conexión (Patrón Singleton).
     * 
     * <p>
     * Implementa el patrón Singleton con inicialización perezosa y sincronización
     * para garantizar thread-safety. Si no existe una instancia previa, crea una
     * nueva; de lo contrario, retorna la instancia existente.
     * </p>
     * 
     * <p>
     * <strong>Thread Safety:</strong> Este método está sincronizado para prevenir
     * condiciones de carrera en entornos multi-hilo.
     * </p>
     * 
     * @return Instancia única de SQLiteConexion
     * @throws SQLException Si ocurre un error al crear la primera instancia de la
     *                      conexión
     */
    public static synchronized SQLiteConexion getInstance() throws SQLException {
        if (instancia == null) {
            instancia = new SQLiteConexion();
        }
        return instancia;
    }

    /**
     * Establece la conexión a la base de datos SQLite.
     * 
     * <p>
     * Carga el driver JDBC de SQLite y establece la conexión utilizando
     * la URL construida con la ruta del archivo de base de datos.
     * Registra en consola el resultado de la operación.
     * </p>
     * 
     * <p>
     * <strong>Driver requerido:</strong> {@code org.sqlite.JDBC}
     * </p>
     * 
     * @return {@code true} si la conexión fue establecida exitosamente,
     *         {@code false} en caso de error
     */
    private boolean conectar() {
        try {
            // Cargar el driver de SQLite
            Class.forName("org.sqlite.JDBC");

            // Establecer la conexión
            String url = "jdbc:sqlite:" + rutaDB;
            conexion = DriverManager.getConnection(url);

            System.out.println("Conexion establecida con exito a la base de datos SQLite.");
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
     * Valida que exista el directorio donde se almacenará la base de datos.
     * 
     * <p>
     * Verifica la existencia del directorio {@code Documents/database} en el
     * directorio home del usuario. Si no existe, intenta crearlo automáticamente.
     * Registra en consola el progreso y resultado de las operaciones.
     * </p>
     * 
     * <p>
     * <strong>Estructura de directorios creada:</strong>
     * {@code {user.home}/Documents/database/}
     * </p>
     * 
     * @return {@code true} si el directorio existe o fue creado correctamente,
     *         {@code false} si no se pudo crear el directorio
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
     * Obtiene la conexión a la base de datos con reconexión automática.
     * 
     * <p>
     * Retorna el objeto Connection activo. Si la conexión está cerrada o es nula,
     * intenta reconectarse automáticamente antes de retornar la conexión.
     * Este comportamiento garantiza que siempre se tenga una conexión válida
     * disponible para las operaciones de base de datos.
     * </p>
     * 
     * <p>
     * <strong>Reconexión automática:</strong> La función detecta conexiones
     * cerradas o nulas y automáticamente intenta restablecer la conexión
     * antes de lanzar una excepción.
     * </p>
     * 
     * @return Objeto Connection válido y activo
     * @throws SQLException Si no se puede establecer o restablecer la conexión
     */
    public Connection getConexion() throws SQLException {
        try {
            // Verificar si la conexión es nula o está cerrada
            if (conexion == null || conexion.isClosed()) {
                System.out.println("Conexión cerrada o nula. Intentando reconectar...");
                if (!conectar()) {
                    throw new SQLException("No se pudo restablecer la conexión a la base de datos");
                }
            }
            return conexion;
        } catch (SQLException e) {
            // Si hay error al verificar el estado, intentar reconectar
            System.out.println("Error al verificar conexión. Intentando reconectar...");
            if (!conectar()) {
                throw new SQLException("No se pudo restablecer la conexión a la base de datos");
            }
            return conexion;
        }
    }

    /**
     * Cierra la conexión a la base de datos de forma segura.
     * 
     * <p>
     * Cierra la conexión actual si está disponible y registra el resultado
     * en consola. Maneja las excepciones de forma apropiada para evitar
     * errores durante el cierre de la aplicación.
     * </p>
     * 
     * <p>
     * <strong>Uso recomendado:</strong> Llamar este método al finalizar
     * la aplicación para liberar recursos de base de datos apropiadamente.
     * </p>
     */
    public void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("Conexion cerrada correctamente.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexion: " + e.getMessage());
            }
        }
    }
}