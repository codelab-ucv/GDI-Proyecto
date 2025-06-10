package ucv.codelab;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ucv.codelab.util.DatabaseInitializer;
import ucv.codelab.util.Personalizacion;

/**
 * Clase principal de la aplicación JavaFX para el sistema de gestión de
 * órdenes.
 * 
 * <p>
 * Esta clase extiende {@link Application} y actúa como punto de entrada
 * principal
 * para la interfaz gráfica de usuario. Se encarga de inicializar la aplicación
 * JavaFX, configurar la ventana principal, aplicar estilos personalizados y
 * gestionar la navegación entre diferentes vistas FXML.
 * </p>
 * 
 * <p>
 * <strong>Responsabilidades principales:</strong>
 * </p>
 * <ul>
 * <li>Inicialización de la base de datos SQLite</li>
 * <li>Configuración de la ventana principal de la aplicación</li>
 * <li>Aplicación de estilos y personalización visual</li>
 * <li>Carga dinámica de vistas FXML</li>
 * <li>Gestión de navegación entre pantallas</li>
 * <li>Configuración de propiedades de la ventana (título, icono,
 * redimensionamiento)</li>
 * </ul>
 * 
 * <p>
 * <strong>Flujo de inicialización:</strong>
 * </p>
 * <ol>
 * <li>Ejecución del método {@code main()} que inicializa la base de datos</li>
 * <li>Lanzamiento de la aplicación JavaFX con {@code launch()}</li>
 * <li>Llamada automática a {@code start()} por el framework JavaFX</li>
 * <li>Carga de la vista inicial "Login.fxml"</li>
 * <li>Aplicación de estilos personalizados basados en configuración</li>
 * <li>Configuración y visualización de la ventana principal</li>
 * </ol>
 * 
 * <p>
 * <strong>Configuración de la ventana:</strong>
 * </p>
 * <ul>
 * <li>Dimensiones fijas: 400x560 píxeles</li>
 * <li>No redimensionable por el usuario</li>
 * <li>Título dinámico basado en la empresa actual</li>
 * <li>Icono personalizable a través de configuración</li>
 * <li>Estilos CSS aplicados dinámicamente</li>
 * </ul>
 * 
 * <p>
 * <strong>Sistema de navegación:</strong>
 * La clase proporciona un mecanismo centralizado para cambiar entre vistas
 * FXML mediante el método {@link #setRoot(String)}, permitiendo una navegación
 * fluida sin necesidad de crear nuevas ventanas.
 * </p>
 * 
 * <p>
 * <strong>Estructura de archivos FXML:</strong>
 * Los archivos FXML deben ubicarse en el directorio {@code /ucv/codelab/view/}
 * del classpath y seguir la convención de nombres sin extensión al llamar
 * a los métodos de navegación.
 * </p>
 * 
 * @see javafx.application.Application
 * @see ucv.codelab.util.DatabaseInitializer
 * @see ucv.codelab.util.Personalizacion
 * @see ucv.codelab.Launcher
 */
public class Main extends Application {

    /**
     * Escena principal de la aplicación JavaFX.
     * 
     * <p>
     * Variable estática que mantiene la referencia a la escena principal
     * de la aplicación, permitiendo el cambio dinámico de vistas sin
     * necesidad de crear nuevas ventanas. Actúa como contenedor principal
     * para todos los elementos visuales de la interfaz.
     * </p>
     */
    private static Scene scene;

    /**
     * Método de inicialización principal de la aplicación JavaFX.
     * 
     * <p>
     * Este método es llamado automáticamente por el framework JavaFX después
     * del método {@code main()} y {@code launch()}. Se encarga de configurar
     * la ventana principal, cargar la vista inicial, aplicar estilos y
     * establecer las propiedades de la ventana.
     * </p>
     * 
     * <p>
     * <strong>Configuraciones aplicadas:</strong>
     * </p>
     * <ul>
     * <li>Carga de la vista "Login.fxml" como pantalla inicial</li>
     * <li>Aplicación de estilos CSS personalizados (letra, tamaño, color de
     * fondo)</li>
     * <li>Configuración de ventana no redimensionable</li>
     * <li>Establecimiento del icono de la aplicación</li>
     * <li>Configuración del título dinámico basado en la empresa actual</li>
     * </ul>
     * 
     * @param stage Ventana principal proporcionada por JavaFX
     * @throws IOException si ocurre un error al cargar el archivo FXML inicial
     *                     o al acceder a los recursos de la aplicación
     */
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("Login"), 400, 560);

        // Usa los estilos base del programa
        scene.getRoot().setStyle(Personalizacion.getTipoLetra()
                + Personalizacion.getTamanoLetra()
                + Personalizacion.getColorFondo());

        // Establece el stage actual
        stage.setScene(scene);

        // Actualiza los datos
        stage.setResizable(false);
        stage.getIcons().add(Personalizacion.getLogo());
        stage.setTitle(Personalizacion.getEmpresaActual().getNombreEmpresa());
        stage.show();
    }

    /**
     * Cambia la vista actual de la aplicación cargando un nuevo archivo FXML.
     * 
     * <p>
     * Método estático que permite la navegación entre diferentes pantallas
     * de la aplicación sin necesidad de crear nuevas ventanas. Mantiene
     * la misma escena pero cambia el contenido raíz por el especificado
     * en el archivo FXML.
     * </p>
     * 
     * <p>
     * <strong>Uso típico:</strong>
     * </p>
     * 
     * <pre>
     * // Navegar a la pantalla principal
     * Main.setRoot("MainView");
     * 
     * // Navegar a la pantalla de productos
     * Main.setRoot("ProductoView");
     * </pre>
     * 
     * <p>
     * <strong>Nota:</strong> El archivo FXML debe existir en el directorio
     * {@code /ucv/codelab/view/} del classpath y el nombre debe proporcionarse
     * sin la extensión ".fxml".
     * </p>
     * 
     * @param fxml Nombre del archivo FXML a cargar (sin extensión)
     * @throws IOException si el archivo FXML no existe, contiene errores de
     *                     sintaxis,
     *                     o no se puede acceder al recurso
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Carga un archivo FXML desde el directorio de vistas y retorna el nodo raíz.
     * 
     * <p>
     * Método utilitario privado que maneja la carga de archivos FXML utilizando
     * {@link FXMLLoader}. Construye automáticamente la ruta completa al archivo
     * basándose en la estructura de directorios del proyecto.
     * </p>
     * 
     * <p>
     * <strong>Convención de rutas:</strong>
     * Los archivos FXML deben ubicarse en {@code /ucv/codelab/view/[nombre].fxml}
     * dentro del classpath de la aplicación.
     * </p>
     * 
     * <p>
     * <strong>Manejo de errores:</strong>
     * Si el archivo no existe o contiene errores de sintaxis FXML, se lanza
     * una {@link IOException} que debe ser manejada por el método llamador.
     * </p>
     * 
     * @param fxml Nombre del archivo FXML a cargar (sin extensión)
     * @return Nodo raíz del archivo FXML cargado
     * @throws IOException si ocurre un error al cargar el archivo FXML,
     *                     incluyendo archivo no encontrado o errores de sintaxis
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/ucv/codelab/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Punto de entrada principal de la aplicación.
     * 
     * <p>
     * Método estático que inicia la aplicación completa. Se ejecuta antes
     * que cualquier componente de JavaFX y se encarga de inicializar la
     * base de datos antes de lanzar la interfaz gráfica.
     * </p>
     * 
     * <p>
     * <strong>Secuencia de inicialización:</strong>
     * </p>
     * <ol>
     * <li>Inicialización de la base de datos SQLite</li>
     * <li>Lanzamiento de la aplicación JavaFX</li>
     * <li>Llamada automática al método {@code start()}</li>
     * </ol>
     * 
     * <p>
     * <strong>Importante:</strong> Este método debe ser llamado desde
     * {@link Launcher#main(String[])} cuando se ejecuta como JAR para
     * evitar problemas de dependencias de JavaFX.
     * </p>
     * 
     * @param args Argumentos de línea de comandos (no utilizados actualmente)
     */
    public static void main(String[] args) {
        // Inicia la base de datos
        DatabaseInitializer.initializeDatabase();
        launch();
    }
}