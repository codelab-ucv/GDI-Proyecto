package ucv.codelab.controller.estadisticas;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;
import ucv.codelab.model.Trabajador;
import ucv.codelab.model.auxiliar.MayorVenta;
import ucv.codelab.repository.TrabajadorRepository;
import ucv.codelab.service.ConsultaAvanzadaSQL;
import ucv.codelab.util.Personalizacion;
import ucv.codelab.util.PopUp;

/**
 * Controlador para la gestión de estadísticas de productos más vendidos.
 * 
 * <p>
 * Esta clase implementa {@link Initializable} y maneja la interfaz de usuario
 * para generar y visualizar estadísticas de los productos con mayor volumen de
 * ventas, proporcionando filtros avanzados por trabajador y rango de fechas.
 * </p>
 * 
 * <p>
 * <strong>Funcionalidades principales:</strong>
 * </p>
 * <ul>
 * <li>Visualización gráfica de los top 5 productos más vendidos</li>
 * <li>Filtrado por trabajador específico o todos los trabajadores</li>
 * <li>Filtrado por rango de fechas personalizable</li>
 * <li>Generación automática de gráficos de barras interactivos</li>
 * <li>Carga dinámica de datos desde la base de datos</li>
 * </ul>
 * 
 * <p>
 * <strong>Componentes de la interfaz:</strong>
 * </p>
 * <ul>
 * <li>ComboBox para selección de trabajador</li>
 * <li>DatePickers para definir rango de fechas</li>
 * <li>BarChart para visualización de resultados</li>
 * <li>Ejes configurables para el gráfico</li>
 * </ul>
 * 
 * <p>
 * El controlador utiliza {@link ConsultaAvanzadaSQL} para obtener datos
 * estadísticos avanzados y presenta los resultados de forma visual e intuitiva
 * mediante componentes JavaFX especializados en gráficos.
 * </p>
 * 
 * @see Initializable
 * @see ConsultaAvanzadaSQL
 * @see MayorVenta
 * @see Trabajador
 */
public class StatsProductosController implements Initializable {

    /**
     * ComboBox para seleccionar el trabajador específico del cual se desean
     * visualizar las estadísticas de productos vendidos.
     * 
     * <p>
     * Permite filtrar los resultados por un trabajador específico o mostrar
     * datos consolidados de todos los trabajadores si no se selecciona ninguno.
     * </p>
     */
    @FXML
    private ComboBox<Trabajador> filtroTrabajador;

    /**
     * DatePicker para seleccionar la fecha de inicio del rango de consulta.
     * 
     * <p>
     * Define el límite inferior del período para el cual se generarán las
     * estadísticas de productos vendidos.
     * </p>
     */
    @FXML
    private DatePicker filtroInicio;

    /**
     * DatePicker para seleccionar la fecha de fin del rango de consulta.
     * 
     * <p>
     * Define el límite superior del período para el cual se generarán las
     * estadísticas de productos vendidos.
     * </p>
     */
    @FXML
    private DatePicker filtroFin;

    /**
     * BarChart que muestra gráficamente los productos más vendidos.
     * 
     * <p>
     * Presenta de forma visual los top 5 productos con mayor cantidad de ventas,
     * utilizando barras horizontales donde la altura representa la cantidad
     * vendida.
     * </p>
     */
    @FXML
    private BarChart<String, Integer> resultado;

    /**
     * Eje X del gráfico de barras que representa los nombres de los productos.
     * 
     * <p>
     * Configurable para mostrar etiquetas rotadas cuando los nombres de
     * productos son extensos, mejorando la legibilidad del gráfico.
     * </p>
     */
    @FXML
    private CategoryAxis ejeX;

    /**
     * Eje Y del gráfico de barras que representa las cantidades vendidas.
     * 
     * <p>
     * Escala numérica que se ajusta automáticamente según el rango de
     * valores de las cantidades de productos vendidos.
     * </p>
     */
    @FXML
    private NumberAxis ejeY;

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Inicializa los componentes de la interfaz configurando el ComboBox de
     * trabajadores con un convertidor personalizado para mostrar solo los nombres,
     * carga la lista de trabajadores disponibles y genera una visualización
     * inicial de estadísticas.
     * </p>
     * 
     * <p>
     * <strong>Configuraciones realizadas:</strong>
     * </p>
     * <ul>
     * <li>StringConverter personalizado para el ComboBox de trabajadores</li>
     * <li>Carga inicial de todos los trabajadores disponibles</li>
     * <li>Generación automática del gráfico inicial con animación</li>
     * </ul>
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Muestra solo el nombre del trabajador en el ComboBox
        filtroTrabajador.setConverter(new StringConverter<Trabajador>() {
            @Override
            public String toString(Trabajador trabajador) {
                return trabajador != null ? trabajador.getNombreTrabajador() : "";
            }

            @Override
            public Trabajador fromString(String string) {
                return null;
            }
        });
        // Carga los valores de los trabajadores
        cargarTrabajadores();
        // Genera una animacion inicial
        clicFiltrar();
    }

    /**
     * Maneja el evento de filtrado y actualización del gráfico de estadísticas.
     * 
     * <p>
     * Obtiene los datos de los productos más vendidos según los filtros aplicados
     * (trabajador y rango de fechas) y actualiza el gráfico de barras con los
     * resultados. Limpia los datos anteriores antes de mostrar la nueva
     * información.
     * </p>
     * 
     * <p>
     * <strong>Proceso de filtrado:</strong>
     * </p>
     * <ul>
     * <li>Extrae el ID del trabajador seleccionado (null si no hay selección)</li>
     * <li>Obtiene las fechas de inicio y fin del rango seleccionado</li>
     * <li>Consulta los top 5 productos más vendidos usando
     * {@link ConsultaAvanzadaSQL}</li>
     * <li>Actualiza el gráfico con los nuevos datos y configuraciones</li>
     * </ul>
     * 
     * <p>
     * <strong>Configuraciones del gráfico:</strong>
     * </p>
     * <ul>
     * <li>Desactiva animaciones durante la actualización para mejor
     * rendimiento</li>
     * <li>Configura títulos descriptivos para ejes y gráfico</li>
     * <li>Rota etiquetas del eje X para mejorar legibilidad</li>
     * </ul>
     */
    @FXML
    private void clicFiltrar() {
        try {
            // Obtiene la lista de los X mayores productos vendidos según los filtros
            Integer idTrabajador = (filtroTrabajador.getValue() == null) ? null
                    : filtroTrabajador.getValue().getIdTrabajador();
            List<MayorVenta> topVentas = ConsultaAvanzadaSQL.mayorVentaCantidad(idTrabajador, filtroInicio.getValue(),
                    filtroFin.getValue(), 5, Personalizacion.getEmpresaActual().getIdEmpresa());

            // Limpiar datos anteriores del gráfico
            resultado.setAnimated(false);
            resultado.getData().clear();

            // Crear una serie de datos para el BarChart
            XYChart.Series<String, Integer> series = new XYChart.Series<>();
            series.setName("Top Productos Vendidos");

            // Agregar los datos de topVentas a la serie
            for (MayorVenta venta : topVentas) {
                series.getData().add(new XYChart.Data<>(venta.getNombreProducto(), venta.getCantidadVendida()));
            }

            // Agregar la serie al gráfico
            resultado.getData().add(series);

            // Configurar títulos de los ejes
            ejeX.setLabel("Productos");
            ejeY.setLabel("Cantidad Vendida");
            resultado.setTitle("Top 5 Productos Más Vendidos");

            // Opcional: Rotar etiquetas del eje X si son muy largas
            ejeX.setTickLabelRotation(45);
        } catch (SQLException e) {
            PopUp.error("Error de conexion", "Ocurrio un error con la base de datos.");
        }
    }

    /**
     * Carga la lista de trabajadores disponibles en el ComboBox de filtros.
     * 
     * <p>
     * Obtiene todos los trabajadores registrados en la base de datos mediante
     * {@link TrabajadorRepository} y los configura como opciones disponibles
     * en el ComboBox de filtros, permitiendo al usuario seleccionar un trabajador
     * específico para generar estadísticas personalizadas.
     * </p>
     * 
     * <p>
     * Los trabajadores se cargan como una {@link ObservableList} que se mantiene
     * sincronizada automáticamente con el ComboBox, garantizando que cualquier
     * cambio en la lista se refleje inmediatamente en la interfaz de usuario.
     * </p>
     * 
     * <p>
     * En caso de error de conexión a la base de datos, se muestra un mensaje
     * de error al usuario mediante {@link PopUp#error}.
     * </p>
     */
    private void cargarTrabajadores() {
        try {
            ObservableList<Trabajador> trabajadores = FXCollections
                    .observableArrayList(new TrabajadorRepository().findAll());
            filtroTrabajador.setItems(trabajadores);
        } catch (SQLException e) {
            PopUp.error("Error de conexion", "Ocurrio un error con la base de datos.");
        }
    }
}