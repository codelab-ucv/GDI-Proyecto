package ucv.codelab.controller.gestionar;

import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import ucv.codelab.model.Trabajador;
import ucv.codelab.repository.BaseRepository;
import ucv.codelab.repository.TrabajadorRepository;

public class GestionarTrabajadores extends GestionarBase<Trabajador> {

    @FXML
    private TextField nombre;

    @FXML
    private TextField dni;

    @FXML
    private ComboBox<String> puesto;

    @Override
    protected BaseRepository<Trabajador> repositorioBase() throws SQLException {
        return new TrabajadorRepository();
    }

    @Override
    protected void mostrarLista(BaseRepository<Trabajador> repository) {
        // Aprovecha para configurar tambien el ComboBox
        puesto.getItems().setAll("JEFE", "SUPERVISOR", "TRABAJADOR");

        TrabajadorRepository repo = (TrabajadorRepository) repository;

        // Configura la lista
        lista.setCellFactory(param -> new ListCell<Trabajador>() {
            @Override
            protected void updateItem(Trabajador trabajador, boolean empty) {
                super.updateItem(trabajador, empty);
                if (empty || trabajador == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(trabajador.getPuesto() + ": " + trabajador.getNombreTrabajador());
                }
            }
        });

        // Cargar datos iniciales
        ObservableList<Trabajador> trabajadores = FXCollections.observableArrayList(repo.findAll());
        lista.setItems(trabajadores);
    }

    @Override
    protected void deshabilitarCamposEdicion(boolean value) {
        // Es lo opuesto ya quee los campos funcionan como Editable y no como Disable
        nombre.setEditable(!value);
        dni.setEditable(!value);
        // Se mantiene como deshabilitar
        puesto.setDisable(value);
    }

    @Override
    protected void actualizarResultado(Trabajador selectedItem) {
        nombre.setText(selectedItem.getNombreTrabajador());
        dni.setText(selectedItem.getDniTrabajador());
        puesto.setValue(selectedItem.getPuesto());
    }

    @Override
    protected void limpiarCampos() {
        nombre.setText(null);
        dni.setText(null);
        puesto.setValue(null);
    }

    @Override
    protected boolean validarDatos() {
        // Si no pasa las validaciones retorna false
        if (!validarString(nombre.getText(), dni.getText()) || puesto.getValue() == null) {
            return false;
        }
        // Si pasa validaciones retorna true
        return true;
    }

    private boolean validarString(String... str) {
        for (String s : str) {
            if (s == null || s.trim().equals("")) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean guardarCambios(Trabajador selectedItem, BaseRepository<Trabajador> repository) {
        selectedItem.setNombreTrabajador(nombre.getText());
        selectedItem.setDniTrabajador(dni.getText());
        selectedItem.setPuesto(puesto.getValue());

        TrabajadorRepository repo = (TrabajadorRepository) repository;
        repo.update(selectedItem);

        return true;
    }

}
