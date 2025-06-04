package ucv.codelab.controller.importar;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ucv.codelab.model.Producto;
import ucv.codelab.model.Trabajador;
import ucv.codelab.repository.BaseRepository;
import ucv.codelab.repository.TrabajadorRepository;
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

    @Override
    protected BaseRepository<Trabajador> repositorioBase(Connection connection) {
        return new TrabajadorRepository(connection);
    }

    @Override
    protected boolean validar(BaseRepository<Trabajador> repository, Trabajador dato) {
        TrabajadorRepository repo = (TrabajadorRepository) repository;
        // Buscar todas las coincidencias con DNI
        Optional<Trabajador> coincidencias = repo.findByDni(dato.getDniTrabajador());
        // Si no hay coincidencias aprueba el insert
        return coincidencias.isEmpty();
    }
}