package ucv.codelab.controller.importar;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ucv.codelab.model.Cliente;
import ucv.codelab.model.Producto;
import ucv.codelab.service.io.ClienteReader;

public class ImpClientesController extends ImportarBase<Cliente> {

    @FXML
    private TableColumn<Producto, String> columnaNombre;

    @FXML
    private TableColumn<Producto, Double> columnaDni;

    @FXML
    private TableColumn<Producto, Double> columnaTelefono;

    @FXML
    private TableColumn<Producto, Double> columnaEmail;

    @Override
    protected List<Cliente> cargarArchivo(String rutaArchivo) throws IOException {
        return new ClienteReader().importar(rutaArchivo);
    }

    @Override
    protected void configurarColumnas() {
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));

        columnaDni.setCellValueFactory(new PropertyValueFactory<>("dniCliente"));

        columnaTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        columnaEmail.setCellValueFactory(new PropertyValueFactory<>("emailCliente"));
    }

}
