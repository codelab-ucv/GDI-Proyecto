package ucv.codelab.controller.ventas;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import ucv.codelab.model.auxiliar.VentaInfo;
import ucv.codelab.service.ConsultaAvanzadaSQL;
import ucv.codelab.util.Personalizacion;
import ucv.codelab.util.PopUp;

public class ConsultarVentasController implements Initializable {

    @FXML
    private TextField idCompra;

    @FXML
    private TextField nombreCliente;

    @FXML
    private TextField nombreTrabajador;

    @FXML
    private DatePicker fechaInicio;

    @FXML
    private DatePicker fechaFin;

    @FXML
    private TableView<VentaInfo> resultado;

    @FXML
    private TableColumn<VentaInfo, String> columnaId;

    @FXML
    private TableColumn<VentaInfo, String> columnaCliente;

    @FXML
    private TableColumn<VentaInfo, String> columnaTrabajador;

    @FXML
    private TableColumn<VentaInfo, String> columnaFecha;

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

    private void configurarColumnas() {
        columnaId.setCellValueFactory(new PropertyValueFactory<>("idOrden"));

        columnaCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));

        columnaTrabajador.setCellValueFactory(new PropertyValueFactory<>("nombreTrabajador"));

        columnaFecha.setCellValueFactory(new PropertyValueFactory<>("fechaOrden"));
    }

    // Verifica que los filtros de texto sean válidos
    private boolean validarDatos(String... datos) {
        for (String s : datos) {
            if (s != null && !s.trim().equals("")) {
                return true;
            }
        }
        return false;
    }
}
