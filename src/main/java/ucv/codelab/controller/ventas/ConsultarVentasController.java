package ucv.codelab.controller.ventas;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import ucv.codelab.model.auxiliar.VentaInfo;
import ucv.codelab.service.ConsultaAvanzadaSQL;
import ucv.codelab.service.writer.MakePdf;
import ucv.codelab.util.Personalizacion;
import ucv.codelab.util.PopUp;

/**
 * Controlador para la gestión de consultas avanzadas de ventas del sistema.
 * 
 * <p>
 * Esta clase implementa {@link Initializable} y maneja la interfaz de consulta
 * de ventas, proporcionando múltiples filtros de búsqueda y visualización de
 * resultados en formato tabular para facilitar el análisis de transacciones
 * comerciales realizadas en el sistema.
 * </p>
 * 
 * <p>
 * <strong>Funcionalidades principales:</strong>
 * </p>
 * <ul>
 * <li>Búsqueda de ventas por ID de orden específico</li>
 * <li>Filtrado por nombre parcial o completo del cliente</li>
 * <li>Filtrado por nombre parcial o completo del trabajador</li>
 * <li>Filtrado por rango de fechas (fecha inicio y fecha fin)</li>
 * <li>Combinación de múltiples filtros para búsquedas precisas</li>
 * <li>Visualización de resultados en tabla organizada por columnas</li>
 * <li>Búsqueda rápida mediante tecla Enter en cualquier campo</li>
 * </ul>
 * 
 * <p>
 * <strong>Estructura de la tabla de resultados:</strong>
 * </p>
 * <ul>
 * <li>ID de Orden: Identificador único de la transacción</li>
 * <li>Cliente: Nombre completo del cliente que realizó la compra</li>
 * <li>Trabajador: Nombre del empleado que procesó la venta</li>
 * <li>Fecha: Fecha de realización de la transacción</li>
 * </ul>
 * 
 * <p>
 * El controlador implementa validaciones robustas para garantizar que se
 * aplique
 * al menos un filtro de búsqueda antes de ejecutar consultas, optimizando el
 * rendimiento de la base de datos y proporcionando resultados relevantes.
 * </p>
 * 
 * @see Initializable
 * @see ConsultaAvanzadaSQL
 * @see VentaInfo
 * @see Personalizacion
 */
public class ConsultarVentasController implements Initializable {

    /**
     * Campo de texto para filtrar ventas por ID específico de orden.
     * 
     * <p>
     * Acepta únicamente valores numéricos enteros que correspondan a
     * identificadores válidos de órdenes de venta en el sistema.
     * Incluye validación automática de formato numérico y búsqueda
     * rápida mediante la tecla Enter.
     * </p>
     */
    @FXML
    private TextField idCompra;

    /**
     * Campo de texto para filtrar ventas por nombre del cliente.
     * 
     * <p>
     * Permite búsquedas parciales utilizando coincidencias de substring,
     * facilitando la localización de ventas cuando no se conoce el nombre
     * completo del cliente. La búsqueda es insensible a mayúsculas y
     * minúsculas para mayor flexibilidad.
     * </p>
     */
    @FXML
    private TextField nombreCliente;

    /**
     * Campo de texto para filtrar ventas por nombre del trabajador.
     * 
     * <p>
     * Permite identificar todas las ventas procesadas por un empleado
     * específico, útil para análisis de rendimiento individual y
     * seguimiento de actividades comerciales por trabajador.
     * Soporta búsquedas parciales por substring.
     * </p>
     */
    @FXML
    private TextField nombreTrabajador;

    /**
     * Selector de fecha para establecer el límite inferior del rango de búsqueda.
     * 
     * <p>
     * Define la fecha mínima desde la cual se incluirán las ventas en los
     * resultados de búsqueda. Trabaja en conjunto con {@link #fechaFin}
     * para crear rangos de fechas precisos que permitan análisis temporal
     * de las transacciones comerciales.
     * </p>
     */
    @FXML
    private DatePicker fechaInicio;

    /**
     * Selector de fecha para establecer el límite superior del rango de búsqueda.
     * 
     * <p>
     * Define la fecha máxima hasta la cual se incluirán las ventas en los
     * resultados de búsqueda. Complementa a {@link #fechaInicio} para
     * permitir consultas de ventas en períodos específicos como días,
     * semanas, meses o rangos personalizados.
     * </p>
     */
    @FXML
    private DatePicker fechaFin;

    /**
     * Tabla principal que muestra los resultados de las consultas de ventas.
     * 
     * <p>
     * Presenta los datos de ventas filtrados en formato tabular organizado,
     * permitiendo visualización clara de múltiples transacciones simultáneas.
     * Se actualiza dinámicamente cada vez que se ejecuta una nueva búsqueda
     * con los filtros aplicados.
     * </p>
     */
    @FXML
    private TableView<VentaInfo> resultado;

    /**
     * Columna de la tabla que muestra el identificador único de cada orden de
     * venta.
     * 
     * <p>
     * Presenta el ID numérico de la orden, permitiendo identificación precisa
     * de cada transacción. Utiliza la propiedad "idOrden" del modelo
     * {@link VentaInfo} para la vinculación de datos.
     * </p>
     */
    @FXML
    private TableColumn<VentaInfo, String> columnaId;

    /**
     * Columna de la tabla que muestra el nombre completo del cliente.
     * 
     * <p>
     * Presenta la información del cliente asociado a cada venta, facilitando
     * la identificación de patrones de compra y análisis de clientes frecuentes.
     * Utiliza la propiedad "nombreCliente" del modelo {@link VentaInfo}.
     * </p>
     */
    @FXML
    private TableColumn<VentaInfo, String> columnaCliente;

    /**
     * Columna de la tabla que muestra el nombre del trabajador que procesó la
     * venta.
     * 
     * <p>
     * Permite identificar qué empleado fue responsable de cada transacción,
     * útil para análisis de rendimiento, seguimiento de actividades y
     * evaluación de desempeño individual. Utiliza la propiedad
     * "nombreTrabajador" del modelo {@link VentaInfo}.
     * </p>
     */
    @FXML
    private TableColumn<VentaInfo, String> columnaTrabajador;

    /**
     * Columna de la tabla que muestra la fecha de realización de cada venta.
     * 
     * <p>
     * Presenta la fecha de la transacción en formato legible, permitiendo
     * análisis temporal de las ventas y identificación de patrones de
     * actividad comercial. Utiliza la propiedad "fechaOrden" del modelo
     * {@link VentaInfo}.
     * </p>
     */
    @FXML
    private TableColumn<VentaInfo, String> columnaFecha;

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Configura la interfaz inicial del controlador de consultas de ventas,
     * estableciendo los eventos de búsqueda rápida mediante la tecla Enter
     * en todos los campos de filtrado y configurando las columnas de la
     * tabla de resultados con sus respectivas propiedades de datos.
     * </p>
     * 
     * <p>
     * <strong>Configuraciones realizadas:</strong>
     * </p>
     * <ul>
     * <li>Configuración de eventos KeyPressed para búsqueda con Enter en campos de
     * texto</li>
     * <li>Vinculación de columnas de tabla con propiedades del modelo
     * VentaInfo</li>
     * <li>Establecimiento de PropertyValueFactory para cada columna</li>
     * <li>Preparación de la interfaz para recibir filtros de búsqueda</li>
     * </ul>
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar hacer Enter para buscar
        idCompra.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                buscarVenta();
            }
        });

        nombreCliente.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                buscarVenta();
            }
        });

        nombreTrabajador.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                buscarVenta();
            }
        });

        configurarColumnas();
    }

    /**
     * Ejecuta la búsqueda de ventas aplicando los filtros especificados por el
     * usuario.
     * 
     * <p>
     * Método principal que coordina todo el proceso de consulta de ventas,
     * incluyendo validación de filtros, procesamiento de datos de entrada,
     * ejecución de consultas a la base de datos y actualización de la
     * tabla de resultados con los datos obtenidos.
     * </p>
     * 
     * <p>
     * <strong>Proceso de búsqueda:</strong>
     * </p>
     * <ul>
     * <li>Validación de que al menos un filtro de texto esté aplicado</li>
     * <li>Conversión y validación del ID numérico si está presente</li>
     * <li>Ejecución de consulta SQL con todos los filtros combinados</li>
     * <li>Filtrado automático por empresa actual del usuario</li>
     * <li>Actualización de la tabla con los resultados obtenidos</li>
     * </ul>
     * 
     * <p>
     * <strong>Validaciones implementadas:</strong>
     * </p>
     * <ul>
     * <li>Al menos un filtro de texto debe estar especificado</li>
     * <li>El ID debe ser un número entero válido si se especifica</li>
     * <li>Manejo de excepciones de conexión a base de datos</li>
     * <li>Filtrado automático por empresa actual para seguridad</li>
     * </ul>
     */
    @FXML
    private void buscarVenta() {
        if (!validarDatos(idCompra.getText(), nombreCliente.getText(), nombreTrabajador.getText())) {
            PopUp.error("Filtros incorrectos", "Debe aplicar por lo menos un filtro de ID o nombre");
            return;
        }

        try {
            Integer id = (idCompra.getText() == null || idCompra.getText().trim().equals("")) ? null
                    : Integer.parseInt(idCompra.getText());

            List<VentaInfo> registroVentas = ConsultaAvanzadaSQL.buscarVentas(id, nombreCliente.getText(),
                    nombreTrabajador.getText(), fechaInicio.getValue(), fechaFin.getValue(),
                    Personalizacion.getEmpresaActual().getIdEmpresa());
            // Actualiza los resultados mostrados
            resultado.setItems(FXCollections.observableArrayList(registroVentas));
        } catch (NumberFormatException e) {
            PopUp.error("Filtros incorrectos", "El ID debe ser un número válido");
        } catch (SQLException e) {
            PopUp.error("Error de conexion", "Ocurrio un error con la base de datos.");
        }
    }

    @FXML
    private void clicVenta(MouseEvent event) {
        // Verificar que sea doble clic con el botón primario
        if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
            // Obtener el elemento seleccionado
            VentaInfo ventaSeleccionada = resultado.getSelectionModel().getSelectedItem();

            if (ventaSeleccionada == null) {
                return;
            }

            Optional<ButtonType> resultado = PopUp.confirmacion("Confirmacion", "Imprimir PDF",
                    "¿Desea imprimir el pdf de la orden " + ventaSeleccionada.getIdOrden() + "?");

            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                File directorioDestino = directoryChooser();
                if (directorioDestino == null) {
                    PopUp.error("Sin directorio", "Guardado de PDF cancelado");
                    return;
                }

                String fileName = "boleta_" + ventaSeleccionada.getIdOrden() + ".pdf";
                File ubicacionArchivo = new File(directorioDestino, fileName);

                try {
                    MakePdf.make(ubicacionArchivo, ventaSeleccionada.getIdOrden());
                } catch (IOException e) {
                    PopUp.error("Error al guardar", "Error al guardar el PDF");
                    e.printStackTrace();
                }
            }
        }
    }

    private File directoryChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar carpeta para guardar el archivo Excel");

        // Establece el directorio inicial en Documents
        String userHome = System.getProperty("user.home");
        File initialDirectory = new File(userHome, "Documents");
        if (initialDirectory.exists()) {
            directoryChooser.setInitialDirectory(initialDirectory);
        }

        // Mostrar el diálogo y obtener la carpeta seleccionada
        File selectedDirectory = directoryChooser.showDialog(idCompra.getScene().getWindow());

        return selectedDirectory;
    }

    /**
     * Configura las columnas de la tabla de resultados con sus respectivas
     * propiedades de datos.
     * 
     * <p>
     * Establece la vinculación entre las columnas visuales de la tabla y las
     * propiedades del modelo de datos {@link VentaInfo}, utilizando
     * PropertyValueFactory para crear un enlace automático entre los datos
     * del modelo y la presentación en la interfaz de usuario.
     * </p>
     * 
     * <p>
     * <strong>Configuraciones de columnas:</strong>
     * </p>
     * <ul>
     * <li>Columna ID: Vinculada a la propiedad "idOrden" del modelo VentaInfo</li>
     * <li>Columna Cliente: Vinculada a la propiedad "nombreCliente"</li>
     * <li>Columna Trabajador: Vinculada a la propiedad "nombreTrabajador"</li>
     * <li>Columna Fecha: Vinculada a la propiedad "fechaOrden"</li>
     * </ul>
     * 
     * <p>
     * Esta configuración permite que la tabla se actualice automáticamente
     * cuando se modifica la lista de resultados, manteniendo la sincronización
     * entre el modelo de datos y la vista sin intervención manual.
     * </p>
     */
    private void configurarColumnas() {
        columnaId.setCellValueFactory(new PropertyValueFactory<>("idOrden"));

        columnaCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));

        columnaTrabajador.setCellValueFactory(new PropertyValueFactory<>("nombreTrabajador"));

        columnaFecha.setCellValueFactory(new PropertyValueFactory<>("fechaOrden"));
    }

    /**
     * Valida que al menos uno de los filtros de texto especificados contenga datos
     * válidos.
     * 
     * <p>
     * Método utilitario que verifica que el usuario haya especificado al menos
     * un criterio de búsqueda textual antes de ejecutar consultas a la base
     * de datos. Esta validación previene consultas vacías que podrían devolver
     * grandes volúmenes de datos innecesarios y optimiza el rendimiento del
     * sistema.
     * </p>
     * 
     * <p>
     * <strong>Criterios de validación:</strong>
     * </p>
     * <ul>
     * <li>Al menos un campo debe ser diferente de null</li>
     * <li>Al menos un campo debe contener texto después del trim()</li>
     * <li>Se ignoran campos vacíos o que contienen solo espacios en blanco</li>
     * <li>La validación es independiente de los filtros de fecha</li>
     * </ul>
     * 
     * <p>
     * Los filtros de fecha son opcionales y pueden combinarse con cualquier
     * filtro de texto para refinar los resultados de búsqueda.
     * </p>
     * 
     * @param datos Campos de texto a validar (cantidad variable de parámetros)
     * @return {@code true} si al menos un campo contiene datos válidos (no nulo y
     *         no vacío),
     *         {@code false} si todos los campos son nulos o vacíos
     */
    private boolean validarDatos(String... datos) {
        for (String s : datos) {
            if (s != null && !s.trim().equals("")) {
                return true;
            }
        }
        return false;
    }
}