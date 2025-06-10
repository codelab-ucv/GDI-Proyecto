package ucv.codelab.controller.configuracion;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import ucv.codelab.Main;
import ucv.codelab.controller.MainController;
import ucv.codelab.repository.TrabajadorRepository;
import ucv.codelab.util.Personalizacion;
import ucv.codelab.util.PopUp;

/**
 * Controlador para la gestión de personalización de la interfaz de usuario.
 * 
 * <p>
 * Esta clase implementa {@link Initializable} y maneja la ventana de
 * configuración
 * de personalización, permitiendo a los usuarios modificar aspectos visuales de
 * la aplicación como colores, tipo de fuente y tamaño de letra. Los cambios se
 * aplican inmediatamente y se persisten en la base de datos.
 * </p>
 * 
 * <p>
 * <strong>Funcionalidades principales:</strong>
 * </p>
 * <ul>
 * <li>Selección de color de fondo mediante ColorPicker</li>
 * <li>Configuración de tipo de fuente desde lista predefinida</li>
 * <li>Ajuste de tamaño de letra mediante Spinner (rango 10-18)</li>
 * <li>Persistencia de cambios en base de datos</li>
 * <li>Recarga automática de la aplicación con nuevos estilos</li>
 * </ul>
 * 
 * <p>
 * <strong>Proceso de personalización:</strong>
 * </p>
 * <ul>
 * <li>Carga configuración actual del usuario autenticado</li>
 * <li>Permite modificación interactiva de parámetros visuales</li>
 * <li>Confirma cambios mediante diálogo de confirmación</li>
 * <li>Actualiza datos en base de datos y memoria local</li>
 * <li>Reinicia la aplicación aplicando nuevos estilos</li>
 * </ul>
 * 
 * <p>
 * Los cambios de personalización se aplican globalmente a toda la aplicación
 * y son específicos para cada usuario autenticado. La aplicación se reinicia
 * automáticamente para aplicar los cambios de forma inmediata.
 * </p>
 * 
 * @see Initializable
 * @see Personalizacion
 * @see TrabajadorRepository
 * @see MainController
 */
public class PersonalizarController implements Initializable {

    /**
     * ColorPicker para seleccionar el color de fondo de la interfaz.
     * 
     * <p>
     * Permite al usuario elegir un color personalizado que se aplicará como
     * color de fondo principal en toda la aplicación. El color seleccionado
     * se convierte automáticamente a formato hexadecimal para su almacenamiento.
     * </p>
     */
    @FXML
    private ColorPicker color;

    /**
     * ComboBox para seleccionar el tipo de fuente de la interfaz.
     * 
     * <p>
     * Contiene una lista predefinida de fuentes disponibles incluyendo:
     * System, Arial, Times New Roman, Verdana, Courier New, Tahoma, Georgia,
     * Trebuchet MS, Impact, Lucida Console y Consolas.
     * </p>
     */
    @FXML
    private ComboBox<String> tipoLetra;

    /**
     * Spinner para ajustar el tamaño de la fuente.
     * 
     * <p>
     * Permite seleccionar un tamaño de fuente en el rango de 10 a 18 píxeles.
     * El valor se configura mediante un
     * {@link SpinnerValueFactory.IntegerSpinnerValueFactory}
     * que controla los límites y el incremento de valores.
     * </p>
     */
    @FXML
    private Spinner<Integer> tamanoLetra;

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Inicializa los componentes de personalización cargando la configuración
     * actual del usuario autenticado desde {@link Personalizacion}. Configura
     * los valores iniciales de todos los controles y establece las opciones
     * disponibles para cada componente.
     * </p>
     * 
     * <p>
     * <strong>Configuraciones realizadas:</strong>
     * </p>
     * <ul>
     * <li>ColorPicker: Establece el color actual del usuario</li>
     * <li>Spinner: Configura rango 10-18 con valor actual del usuario</li>
     * <li>ComboBox: Carga lista de fuentes y selecciona la actual</li>
     * </ul>
     * 
     * <p>
     * Las fuentes disponibles incluyen tanto fuentes del sistema como
     * fuentes web-safe comúnmente utilizadas para garantizar compatibilidad
     * multiplataforma.
     * </p>
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configura el valor inicial del ColorPicker
        color.setValue(Personalizacion.getColor());
        // Configura el rango de valores del spinner
        SpinnerValueFactory.IntegerSpinnerValueFactory factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(10,
                18, Personalizacion.getTamano());
        tamanoLetra.setValueFactory(factory);

        // Configura los valores del comboBox
        tipoLetra.getItems().addAll("System", "Arial", "Times New Roman", "Verdana", "Courier New", "Tahoma", "Georgia",
                "Trebuchet MS", "Impact", "Lucida Console", "Consolas");
        tipoLetra.getSelectionModel().select(Personalizacion.getFuente());
    }

    /**
     * Maneja el evento de guardar y aplicar los cambios de personalización.
     * 
     * <p>
     * Solicita confirmación al usuario antes de aplicar los cambios mediante
     * un diálogo de confirmación. Si el usuario confirma, actualiza la
     * configuración
     * tanto en memoria local como en la base de datos, y reinicia la aplicación
     * para aplicar los nuevos estilos inmediatamente.
     * </p>
     * 
     * <p>
     * <strong>Proceso de actualización:</strong>
     * </p>
     * <ul>
     * <li>Muestra diálogo de confirmación con advertencia de pérdida de
     * cambios</li>
     * <li>Convierte color seleccionado a formato hexadecimal</li>
     * <li>Combina tipo y tamaño de fuente en formato "fuente/tamaño"</li>
     * <li>Actualiza el objeto Trabajador actual en memoria</li>
     * <li>Persiste cambios en base de datos mediante
     * {@link TrabajadorRepository}</li>
     * <li>Reinicia la aplicación con nuevos estilos aplicados</li>
     * </ul>
     * 
     * <p>
     * En caso de error durante el proceso, se muestran mensajes de error
     * específicos para problemas de carga de ventanas o conexión a base de datos.
     * </p>
     */
    @FXML
    private void clicGuardarActualizar() {
        Optional<ButtonType> resultado = PopUp.confirmacion("Actualizar Interfaz",
                "¿Está seguro que desea modificar la interfaz?",
                "Se perderán los cambios no guardados.");
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

            try {
                TrabajadorRepository repository = new TrabajadorRepository();

                // Actualiza los datos localmente
                String hexColor = "#" + color.getValue().toString().substring(2, 8);
                Personalizacion.getTrabajadorActual().setColorFondo(hexColor);
                String letra = tipoLetra.getValue() + "/" + tamanoLetra.getValue();
                Personalizacion.getTrabajadorActual().setTipoLetra(letra);

                // Actualiza en la bdd
                repository.update(Personalizacion.getTrabajadorActual());

                // Actualiza el programa
                recargarPrograma();
            } catch (IOException e) {
                PopUp.error("Error al cargar ventanas",
                        "Error crítico al cargar el menu, vuelva a intentarlo o contacte un administrador");
            } catch (SQLException e) {
                PopUp.error("Error de conexion", "Ocurrio un error con a la base de datos.");
                return;
            }
        }
    }

    /**
     * Reinicia la aplicación aplicando los nuevos estilos de personalización.
     * 
     * <p>
     * Cierra la ventana actual y recarga completamente la aplicación con los
     * nuevos estilos aplicados. Este proceso garantiza que todos los cambios
     * de personalización se reflejen inmediatamente en toda la interfaz.
     * </p>
     * 
     * <p>
     * <strong>Proceso de recarga:</strong>
     * </p>
     * <ul>
     * <li>Limpia el caché de ventanas mediante
     * {@link MainController#limpiarCache()}</li>
     * <li>Cierra la ventana de personalización actual</li>
     * <li>Carga el FXML del menú principal</li>
     * <li>Crea nueva escena con dimensiones estándar (1300x780)</li>
     * <li>Aplica estilos personalizados del usuario autenticado</li>
     * <li>Configura propiedades de la ventana (tamaño mínimo, icono, título)</li>
     * <li>Muestra la nueva ventana principal</li>
     * </ul>
     * 
     * <p>
     * Los estilos aplicados incluyen tipo de fuente, tamaño de fuente y color
     * de fondo según la configuración personalizada del usuario actual.
     * </p>
     * 
     * @throws IOException Si ocurre un error al cargar el archivo FXML del menú
     *                     principal
     */
    private void recargarPrograma() throws IOException {
        // Borra las ventanas en memoria
        MainController.limpiarCache();

        // Cierra la ventana actual
        Stage stageActual = (Stage) color.getScene().getWindow();
        stageActual.close();

        // Vuelve a abrir el programa en la ventana principal
        Stage stage = new Stage();
        Scene scene = new Scene(loadFXML("MenuPrincipal"), 1300, 780);

        // Usa los estilos base del programa
        scene.getRoot().setStyle(Personalizacion.getTipoLetra()
                + Personalizacion.getTamanoLetra()
                + Personalizacion.getColorFondo());

        stage.setScene(scene);
        stage.setMinWidth(1300);
        stage.setMinHeight(780);
        stage.getIcons().add(Personalizacion.getLogo());
        stage.setTitle(Personalizacion.getEmpresaActual().getNombreEmpresa());
        stage.show();
    }

    /**
     * Carga un archivo FXML desde el directorio de vistas de la aplicación.
     * 
     * <p>
     * Método utilitario para cargar archivos FXML ubicados en el paquete de
     * vistas de la aplicación. Construye la ruta completa del archivo y
     * utiliza {@link FXMLLoader} para crear el objeto Parent correspondiente.
     * </p>
     * 
     * <p>
     * La ruta se construye automáticamente agregando la extensión .fxml y
     * el prefijo del paquete de vistas: "/ucv/codelab/view/".
     * </p>
     * 
     * @param fxml Nombre del archivo FXML sin extensión
     * @return Objeto Parent cargado desde el archivo FXML
     * @throws IOException Si ocurre un error al cargar el archivo FXML
     */
    private Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/ucv/codelab/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }
}