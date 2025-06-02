package ucv.codelab.controller.importar;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Clase base abstracta para controladores de importación de datos desde
 * archivos CSV.
 * 
 * <p>
 * Esta clase proporciona funcionalidad común para importar datos de archivos
 * CSV
 * y mostrarlos en una tabla de JavaFX. Las clases que extiendan de esta deben
 * implementar los métodos abstractos para definir cómo cargar los datos
 * específicos
 * y configurar las columnas de la tabla.
 * </p>
 * 
 * <p>
 * Funcionalidades incluidas:
 * </p>
 * <ul>
 * <li>Selección de archivos CSV mediante FileChooser</li>
 * <li>Carga automática de datos en TableView</li>
 * <li>Manejo de errores de lectura de archivos</li>
 * <li>Habilitación/deshabilitación de botones según el estado</li>
 * </ul>
 * 
 * @param <T> El tipo de objeto que será importado y mostrado en la tabla
 * 
 */
public abstract class ImportarBase<T> implements Initializable {

    /**
     * Botón para seleccionar el archivo CSV a importar.
     */
    @FXML
    protected Button botonSeleccionarArchivo;

    /**
     * Botón para ejecutar la importación de los datos cargados.
     */
    @FXML
    protected Button botonImportarDatos;

    /**
     * Label que muestra la ruta del archivo seleccionado.
     */
    @FXML
    protected Label textoRutaSeleccionada;

    /**
     * Tabla que muestra una vista previa de los datos importados.
     */
    @FXML
    protected TableView<T> tablaMuestra;

    /**
     * Variable para almacenar el archivo seleccionado por el usuario.
     */
    private File archivoSeleccionado;

    /**
     * Lista que contiene los datos cargados desde el archivo CSV.
     */
    private List<T> listaDatos;

    /**
     * Inicializa el controlador configurando las columnas de la tabla
     * y deshabilitando el botón de importar hasta que se seleccione un archivo.
     * 
     * @param location  La ubicación utilizada para resolver rutas relativas del
     *                  objeto raíz, o null si no se conoce
     * @param resources Los recursos utilizados para localizar el objeto raíz, o
     *                  null si no se especificaron
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // El boton de importar datos esta deshabilitado al iniciar
        botonImportarDatos.setDisable(true);

        // Configurar las columnas de la tabla
        configurarColumnas();
    }

    /**
     * Método abstracto que debe ser implementado por las clases hijas para
     * definir cómo cargar los datos específicos desde un archivo CSV.
     * 
     * @param rutaArchivo La ruta completa del archivo CSV a cargar
     * @return Una lista con los objetos del tipo T cargados desde el archivo
     * @throws IOException Si ocurre un error durante la lectura del archivo
     */
    protected abstract List<T> cargarArchivo(String rutaArchivo) throws IOException;

    /**
     * Método abstracto que debe ser implementado por las clases hijas para
     * configurar las columnas específicas de la tabla según el tipo de datos.
     * 
     * <p>
     * Este método debe usar PropertyValueFactory para vincular las columnas
     * con los atributos correspondientes del tipo T.
     * </p>
     */
    protected abstract void configurarColumnas();

    /**
     * Maneja el evento de clic en el botón "Seleccionar archivo".
     * 
     * <p>
     * Abre un FileChooser para seleccionar un archivo CSV, carga los datos
     * y los muestra en la tabla. Si la carga es exitosa, habilita el botón
     * de importar.
     * </p>
     */
    @FXML
    protected void clicSeleccionarArchivo() {
        // Guarda cual es el archivo seleccionado
        File archivo = selectorArchivo();

        // Si el archivo no es nulo
        if (archivo != null) {
            // Guardar el archivo seleccionado
            archivoSeleccionado = archivo;

            // Mostrar la ruta en el label
            textoRutaSeleccionada.setText(archivoSeleccionado.getAbsolutePath());

            // Carga el archivo seleccionado
            try {
                listaDatos = cargarArchivo(archivoSeleccionado.getAbsolutePath());
            } catch (Exception e) {
                // Si no se puede leer el archivo retorna e imprime el error en consola
                mostrarError("Error de lectura", "Ocurrió un error al leer el archivo " + archivo.getName()
                        + "\nVerifique que el archivo seleccionado tenga el formato correcto.");
                e.printStackTrace();
                return;
            }

            // Muestra los datos en la tabla
            cargarDatosEnTabla();

            // Habilitar el botón de importar
            botonImportarDatos.setDisable(false);

            System.out.println("Archivo seleccionado: " + archivo.getAbsolutePath());
        } else {
            // El usuario canceló la selección
            textoRutaSeleccionada.setText("No se ha seleccionado ningún archivo");
            botonImportarDatos.setDisable(true);

            // Borra la lista de datos precargados
            if (listaDatos != null) {
                listaDatos.clear();
            }
            cargarDatosEnTabla();
            archivoSeleccionado = null;
        }
    }

    /**
     * Abre un FileChooser configurado para seleccionar archivos CSV.
     * 
     * @return El archivo seleccionado por el usuario, o null si canceló la
     *         selección
     */
    private File selectorArchivo() {
        // Crear una instancia de FileChooser
        FileChooser fileChooser = new FileChooser();

        // Configurar el título del diálogo
        fileChooser.setTitle("Seleccionar archivo CSV de productos");

        // Permite solo los archivos .csv
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));

        // Establece el directorio inicial en Documents
        String userHome = System.getProperty("user.home");
        File initialDirectory = new File(userHome, "Documents");
        if (initialDirectory.exists()) {
            fileChooser.setInitialDirectory(initialDirectory);
        }

        // Obtener la ventana actual
        Stage stage = (Stage) botonSeleccionarArchivo.getScene().getWindow();

        // Mostrar el diálogo de selección de archivo
        return fileChooser.showOpenDialog(stage);
    }

    /**
     * Carga los datos de la lista en la TableView para mostrar una vista previa.
     * 
     * <p>
     * Convierte la lista de datos a ObservableList para que JavaFX pueda
     * observar los cambios y actualizar la interfaz automáticamente.
     * Si no hay datos válidos, muestra un mensaje de error.
     * </p>
     */
    private void cargarDatosEnTabla() {
        if (listaDatos != null && !listaDatos.isEmpty()) {
            // Parsear la lista a ObservableList para que JavaFX pueda observar los cambios
            ObservableList<T> datosObservables = FXCollections.observableArrayList(listaDatos);

            // Asignar los datos a la tabla
            tablaMuestra.setItems(datosObservables);

            System.out.println("Se cargaron " + listaDatos.size() + " datos en la tabla");
        } else {
            // Limpiar la tabla si no hay datos
            tablaMuestra.setItems(FXCollections.observableArrayList());
            mostrarError("Sin información", "No hay datos válidos para mostrar en el archivo indicado");
        }
    }

    @FXML
    private void clicImportarDatos() {

    }

    /**
     * Muestra un diálogo de alerta de error con el título y mensaje especificados.
     * 
     * @param titulo  El título del diálogo de error
     * @param mensaje El mensaje detallado del error
     */
    protected void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}