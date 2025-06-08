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

public class StatsProductosController implements Initializable {

    @FXML
    private ComboBox<Trabajador> filtroTrabajador;

    @FXML
    private DatePicker filtroInicio;

    @FXML
    private DatePicker filtroFin;

    @FXML
    private BarChart<String, Integer> resultado;

    @FXML
    private CategoryAxis ejeX;

    @FXML
    private NumberAxis ejeY;

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
