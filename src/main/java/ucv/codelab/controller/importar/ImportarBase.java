package ucv.codelab.controller.importar;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ucv.codelab.repository.BaseRepository;
import ucv.codelab.util.PopUp;
import ucv.codelab.util.SQLiteConexion;

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
                PopUp.error("Error de lectura", "Ocurrió un error al leer el archivo " + archivo.getName()
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
            cancelarSeleccion();
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
        fileChooser.setTitle("Seleccionar archivo CSV");

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
            PopUp.error("Sin información", "No hay datos válidos para mostrar en el archivo indicado");
        }
    }

    /**
     * Maneja el evento de clic en el botón "Importar datos".
     * 
     * <p>
     * Valida que existan datos cargados, establece conexión con la base de datos
     * y procede a insertar los datos válidos. Muestra un resumen del proceso
     * indicando cuántos registros se insertaron exitosamente y cuántos fallaron.
     * </p>
     */
    @FXML
    protected void clicImportarDatos() {
        if (listaDatos == null || listaDatos.isEmpty()) {
            PopUp.error("Sin datos", "No hay datos para importar. Seleccione un archivo primero.");
            cancelarSeleccion();
            return;
        }

        try (Connection connection = SQLiteConexion.getInstance().getConexion()) {
            // Inserta los datos de la tabla
            int insercciones[] = insertarDatos(connection);

            String mensaje = "";

            // Si hay al menos un dato insertado muestra el mensaje positivo
            if (insercciones[0] > 0) {
                mensaje += "Se insertaron exitosamente " + insercciones[0] + " datos.\n";
            }
            // Si ocurrio un error al mostrar algun dato actualiza el mensaje y la tabla
            if (insercciones[1] > 0) {
                mensaje += "Ocurrió un error al insertar " + insercciones[1]
                        + " datos, verifica que se cumplan con los criterios"
                        + " indicados y no este previamente ingresado.";
                cargarDatosEnTabla();
            } else {
                // De lo contrario limpia los registros de la tabla y el archivo seleccionado
                cancelarSeleccion();
            }

            // Muestra un enunciado con la cantidad de datos insertados
            PopUp.informacion("Datos importados", mensaje);
        } catch (SQLException e) {
            PopUp.error("Error de conexión",
                    "No se pudo conectar a la base de datos.");
            return;
        }
    }

    /**
     * Procesa la inserción de datos en la base de datos separando la validación
     * de la inserción efectiva.
     * 
     * <p>
     * Primero valida todos los datos y los separa en válidos e inválidos.
     * Luego inserta únicamente los datos válidos y actualiza la lista original
     * con los datos que no pudieron ser procesados.
     * </p>
     * 
     * @param connection La conexión a la base de datos
     * @return Un arreglo de enteros donde [0] representa los registros insertados
     *         exitosamente y [1] los registros que fallaron
     */
    private int[] insertarDatos(Connection connection) {
        int[] registroInsertados = { 0, 0 };
        BaseRepository<T> repository = repositorioBase(connection);

        // Separar validación de inserción
        List<T> datosValidos = new ArrayList<>();
        List<T> datosInvalidos = new ArrayList<>();

        for (T dato : listaDatos) {
            if (validar(repository, dato)) {
                datosValidos.add(dato);
            } else {
                datosInvalidos.add(dato);
            }
        }

        // Insertar solo datos válidos
        for (T dato : datosValidos) {
            repository.save(dato);
            registroInsertados[0]++;
        }

        // Actualizar lista original con datos que no se pudieron insertar
        listaDatos.clear();
        listaDatos.addAll(datosInvalidos);
        registroInsertados[1] = datosInvalidos.size();

        return registroInsertados;
    }

    /**
     * Método abstracto que debe ser implementado por las clases hijas para
     * proporcionar el repositorio específico que manejará la importación.
     * 
     * @param connection La conexión a la base de datos
     * @return El repositorio base configurado para el tipo de datos específico
     */
    protected abstract BaseRepository<T> repositorioBase(Connection connection);

    /**
     * Valida si un dato puede ser insertado en la base de datos.
     * Las implementaciones deben verificar:
     * - Duplicados (si aplica)
     * - Formato de datos
     * - Restricciones de negocio
     * 
     * @param repository Repositorio para consultas de validación
     * @param dato       Dato a validar
     * @return true si el dato es válido para inserción, false en caso contrario
     */
    protected abstract boolean validar(BaseRepository<T> repository, T dato);

    /**
     * Cancela la selección actual y restaura el estado inicial del controlador.
     * 
     * <p>
     * Deshabilita el botón de importar, limpia el texto de ruta seleccionada,
     * borra los datos cargados, reinicia la tabla y elimina la referencia
     * al archivo seleccionado.
     * </p>
     */
    private void cancelarSeleccion() {
        // Se deshabilita el boton de importar y se regresa al texto default
        botonImportarDatos.setDisable(true);
        textoRutaSeleccionada.setText("No se ha seleccionado ningún archivo");

        // Borra la lista de datos precargados
        if (listaDatos != null) {
            listaDatos.clear();
        }

        // Reinicia los datos de la tabla
        tablaMuestra.setItems(FXCollections.observableArrayList());

        // Borra de la caches el archivo guardado
        archivoSeleccionado = null;
    }
}