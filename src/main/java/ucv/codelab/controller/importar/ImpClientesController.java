package ucv.codelab.controller.importar;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ucv.codelab.model.Cliente;
import ucv.codelab.model.Producto;
import ucv.codelab.repository.BaseRepository;
import ucv.codelab.repository.ClienteRepository;
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

    @Override
    protected BaseRepository<Cliente> repositorioBase(Connection connection) {
        return new ClienteRepository(connection);
    }

    @Override
    protected boolean validar(BaseRepository<Cliente> repository, Cliente dato) {
        ClienteRepository repo = (ClienteRepository) repository;
        // Buscar todas las coincidencias con DNI
        Optional<Cliente> coincidencias = repo.findByDni(dato.getDniCliente());
        // Si no hay coincidencias aprueba el insert
        return coincidencias.isEmpty();
    }
}
