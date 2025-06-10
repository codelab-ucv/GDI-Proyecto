package ucv.codelab;

/**
 * Clase de lanzamiento para evitar problemas de JavaFX con archivos JAR
 * ejecutables.
 * 
 * <p>
 * Esta clase actúa como punto de entrada alternativo para la aplicación cuando
 * se ejecuta como archivo JAR independiente. Su propósito principal es evitar
 * el error de runtime que ocurre cuando se intenta ejecutar directamente una
 * clase que extiende {@link javafx.application.Application} desde un JAR.
 * </p>
 * 
 * <p>
 * <strong>Problema que resuelve:</strong>
 * </p>
 * <p>
 * Cuando se crea un JAR ejecutable de uma aplicación JavaFX, el JVM puede
 * generar errores si la clase principal extiende {@code Application} debido
 * a problemas de carga de módulos y dependencias de JavaFX. Este launcher
 * actúa como intermediario que no extiende ninguna clase de JavaFX.
 * </p>
 * 
 * <p>
 * <strong>Configuración del JAR:</strong>
 * </p>
 * <p>
 * En el archivo MANIFEST.MF del JAR ejecutable, se debe especificar:
 * </p>
 * 
 * <pre>
 * Main-Class: ucv.codelab.Launcher
 * </pre>
 * 
 * <p>
 * En lugar de usar directamente {@code ucv.codelab.Main}, lo que evita
 * problemas de inicialización del runtime de JavaFX.
 * </p>
 * 
 * <p>
 * <strong>Patrón de diseño:</strong>
 * </p>
 * <p>
 * Este es un ejemplo del patrón "Launcher" o "Bootstrap" comúnmente usado
 * en aplicaciones JavaFX modernas para garantizar compatibilidad con
 * diferentes formas de distribución y ejecución.
 * </p>
 * 
 * <p>
 * <strong>Uso recomendado:</strong>
 * </p>
 * <ul>
 * <li>Ejecución desde JAR: usar esta clase como punto de entrada</li>
 * <li>Ejecución desde IDE: usar directamente {@link Main}</li>
 * <li>Distribución: configurar esta clase en el manifiesto del JAR</li>
 * </ul>
 * 
 * @see Main
 * @see javafx.application.Application
 */
public class Launcher {

    /**
     * Punto de entrada principal para la ejecución desde JAR ejecutable.
     * 
     * <p>
     * Este método simplemente delega la ejecución al método {@code main()}
     * de la clase {@link Main}, pero al no extender {@code Application},
     * evita problemas de inicialización que pueden ocurrir al ejecutar
     * aplicaciones JavaFX desde archivos JAR.
     * </p>
     * 
     * <p>
     * <strong>Flujo de ejecución:</strong>
     * </p>
     * <ol>
     * <li>JVM ejecuta {@code Launcher.main()}</li>
     * <li>Se delega inmediatamente a {@code Main.main()}</li>
     * <li>Continúa el flujo normal de inicialización de la aplicación</li>
     * </ol>
     * 
     * <p>
     * <strong>Ventajas:</strong>
     * </p>
     * <ul>
     * <li>Evita errores de "JavaFX runtime components are missing"</li>
     * <li>Facilita la distribución como JAR ejecutable</li>
     * <li>Mantiene compatibilidad con diferentes versiones de Java</li>
     * <li>No interfiere con el flujo normal de la aplicación</li>
     * </ul>
     * 
     * @param args Argumentos de línea de comandos que se pasan directamente
     *             a la aplicación principal
     */
    public static void main(String[] args) {
        // Simplemente llamamos al Main original
        Main.main(args);
    }
}