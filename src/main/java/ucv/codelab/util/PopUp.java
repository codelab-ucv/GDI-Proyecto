package ucv.codelab.util;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * Clase utilitaria para la creación y gestión de diálogos emergentes (pop-ups)
 * en aplicaciones JavaFX.
 * 
 * <p>
 * Esta clase proporciona métodos estáticos para mostrar diferentes tipos de
 * diálogos de manera consistente y personalizada, aplicando automáticamente
 * las configuraciones de personalización del usuario como fuentes, colores
 * de fondo y logos corporativos.
 * </p>
 * 
 * <p>
 * <strong>Tipos de diálogos disponibles:</strong>
 * </p>
 * <ul>
 * <li>Diálogos de confirmación para decisiones críticas</li>
 * <li>Diálogos de entrada de texto para capturar datos del usuario</li>
 * <li>Diálogos informativos para mostrar mensajes al usuario</li>
 * <li>Diálogos de error para reportar problemas y excepciones</li>
 * </ul>
 * 
 * <p>
 * <strong>Personalización automática:</strong>
 * </p>
 * <ul>
 * <li>Aplicación de fuentes y tamaños configurados por el usuario</li>
 * <li>Uso de colores de fondo personalizados</li>
 * <li>Integración del logo corporativo como ícono de ventana</li>
 * <li>Manejo de errores con fallback a configuraciones por defecto</li>
 * </ul>
 * 
 * <p>
 * Todos los diálogos se configuran automáticamente con las preferencias
 * del usuario actuales obtenidas de la clase {@link Personalizacion}.
 * </p>
 * 
 * @see Alert
 * @see TextInputDialog
 * @see Personalizacion
 */
public class PopUp {

    /**
     * Muestra un diálogo de confirmación con botones de Aceptar y Cancelar.
     * 
     * <p>
     * Crea un diálogo de tipo CONFIRMATION que permite al usuario tomar
     * decisiones binarias. El diálogo se personaliza automáticamente con
     * las configuraciones actuales del usuario.
     * </p>
     * 
     * <p>
     * <strong>Botones disponibles:</strong> OK y Cancel por defecto.
     * El usuario puede cerrar el diálogo sin seleccionar ninguna opción.
     * </p>
     * 
     * @param titulo  Título que aparece en la barra del diálogo
     * @param header  Texto del encabezado principal del diálogo
     * @param mensaje Mensaje detallado que se muestra al usuario
     * @return Optional que contiene el ButtonType seleccionado por el usuario,
     *         o vacío si el diálogo fue cerrado sin selección
     */
    public static Optional<ButtonType> confirmacion(String titulo, String header, String mensaje) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        setAlertProperties(confirmacion, titulo, header, mensaje);
        return confirmacion.showAndWait();
    }

    /**
     * Muestra un diálogo de entrada de texto para capturar datos del usuario.
     * 
     * <p>
     * Crea un TextInputDialog personalizado que permite al usuario ingresar
     * texto. El diálogo incluye un campo de entrada de texto y botones
     * de OK y Cancel.
     * </p>
     * 
     * <p>
     * <strong>Personalización aplicada:</strong>
     * </p>
     * <ul>
     * <li>Fuente y tamaño de letra del usuario</li>
     * <li>Color de fondo personalizado</li>
     * <li>Logo corporativo como ícono de ventana</li>
     * </ul>
     * 
     * <p>
     * Si ocurre un error al cargar el logo personalizado, se utiliza
     * el logo por defecto y se registra el error en consola.
     * </p>
     * 
     * @param titulo  Texto que aparece en el encabezado del diálogo
     * @param mensaje Texto que describe qué tipo de información se solicita
     * @return String con el texto ingresado por el usuario, o cadena vacía
     *         si el diálogo fue cancelado o cerrado sin entrada
     */
    public static String inputDialog(String titulo, String mensaje) {
        // Crear el diálogo
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ingresar datos solicitados");
        dialog.setHeaderText(titulo);
        dialog.setContentText(mensaje);

        // Configura el tipo de letra
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getScene().getRoot().setStyle(Personalizacion.getTipoLetra()
                + Personalizacion.getTamanoLetra()
                + Personalizacion.getColorFondo());

        // Intenta usar el icono seleccionado por el usuario
        try {
            stage.getIcons().add(Personalizacion.getLogo());
        }
        // Si ocurre un problema usa el logo original e informa por cmd
        catch (Exception e) {
            System.err.println("Error al cargar el ícono: " + e.getMessage()
                    + "\nSe usaran datos por defecto");
            stage.getIcons().add(Personalizacion.getLogo());
        }

        // Mostrar el diálogo y esperar respuesta
        Optional<String> result = dialog.showAndWait();

        // Procesar la respuesta
        return result.isEmpty() ? "" : result.get();
    }

    /**
     * Muestra un diálogo de información con el título y mensaje especificados.
     * 
     * <p>
     * Crea un diálogo de tipo INFORMATION para mostrar mensajes informativos
     * al usuario. Es útil para confirmar operaciones exitosas, mostrar
     * resultados o proporcionar información relevante.
     * </p>
     * 
     * <p>
     * El diálogo se personaliza automáticamente con las configuraciones
     * actuales del usuario y solo contiene un botón de OK para cerrar.
     * </p>
     * 
     * @param titulo  El título del diálogo que describe el tipo de información
     * @param mensaje El mensaje informativo detallado para el usuario
     */
    public static void informacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        setAlertProperties(alert, "Información", titulo, mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un diálogo de alerta de error con el título y mensaje especificados.
     * 
     * <p>
     * Crea un diálogo de tipo ERROR para reportar errores, excepciones o
     * situaciones problemáticas al usuario. Utiliza iconografía y colores
     * apropiados para indicar la naturaleza crítica del mensaje.
     * </p>
     * 
     * <p>
     * Este tipo de diálogo es especialmente útil para:
     * </p>
     * <ul>
     * <li>Reportar errores de validación de datos</li>
     * <li>Informar sobre fallos en operaciones críticas</li>
     * <li>Mostrar excepciones capturadas de manera user-friendly</li>
     * <li>Alertar sobre problemas de conectividad o recursos</li>
     * </ul>
     * 
     * @param titulo  El título del diálogo de error que resume el problema
     * @param mensaje El mensaje detallado del error con información específica
     */
    public static void error(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        setAlertProperties(alert, "Error", titulo, mensaje);
        alert.showAndWait();
    }

    /**
     * Configura las propiedades comunes de personalización para todos los diálogos
     * Alert.
     * 
     * <p>
     * Este método privado centraliza la configuración de personalización
     * que se aplica a todos los tipos de diálogos Alert. Establece textos,
     * estilos visuales y logo corporativo de manera consistente.
     * </p>
     * 
     * <p>
     * <strong>Configuraciones aplicadas:</strong>
     * </p>
     * <ul>
     * <li>Título, encabezado y contenido del diálogo</li>
     * <li>Estilo CSS con fuente y tamaño personalizados</li>
     * <li>Color de fondo según preferencias del usuario</li>
     * <li>Logo corporativo como ícono de ventana con fallback seguro</li>
     * </ul>
     * 
     * <p>
     * <strong>Manejo de errores:</strong> Si no se puede cargar el logo
     * personalizado, automáticamente usa el logo por defecto y registra
     * el error en consola sin interrumpir la funcionalidad.
     * </p>
     * 
     * @param alert   El objeto Alert a configurar
     * @param title   Título que aparece en la barra de la ventana
     * @param header  Texto del encabezado del diálogo
     * @param content Mensaje principal del diálogo
     */
    private static void setAlertProperties(Alert alert, String title, String header, String content) {
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Hacer que el texto se ajuste y sea expandible
        alert.getDialogPane().setContentText(content);
        alert.getDialogPane().setPrefWidth(600); // Ancho base
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

        stage.getScene().getRoot().setStyle(Personalizacion.getTipoLetra()
                + Personalizacion.getTamanoLetra()
                + Personalizacion.getColorFondo());

        // Intenta usar el icono seleccionado por el usuario
        try {
            stage.getIcons().add(Personalizacion.getLogo());
        }
        // Si ocurre un problema usa el logo original e informa por cmd
        catch (Exception e) {
            System.err.println("Error al cargar el ícono: " + e.getMessage()
                    + "\nSe usaran datos por defecto");
            stage.getIcons().add(Personalizacion.getLogo());
        }
    }
}