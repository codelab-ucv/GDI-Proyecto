package ucv.codelab.controller.importar;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ucv.codelab.model.Producto;
import ucv.codelab.model.Trabajador;
import ucv.codelab.service.io.TrabajadorReader;

public class ImpTrabajadoresController extends ImportarBase<Trabajador> {

    @FXML
    private TableColumn<Producto, String> columnaNombre;

    @FXML
    private TableColumn<Producto, Double> columnaDni;

    @FXML
    private TableColumn<Producto, Double> columnaPuesto;

    @Override
    protected List<Trabajador> cargarArchivo(String rutaArchivo) throws IOException {
        return new TrabajadorReader().importar(rutaArchivo);
    }

    @Override
    protected void configurarColumnas() {
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombreTrabajador"));

        columnaDni.setCellValueFactory(new PropertyValueFactory<>("dniTrabajador"));

        columnaPuesto.setCellValueFactory(new PropertyValueFactory<>("puesto"));
    }

}