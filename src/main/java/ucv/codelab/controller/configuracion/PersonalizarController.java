package ucv.codelab.controller.configuracion;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import ucv.codelab.Main;
import ucv.codelab.controller.MainController;
import ucv.codelab.repository.TrabajadorRepository;
import ucv.codelab.util.Personalizacion;
import ucv.codelab.util.PopUp;

public class PersonalizarController implements Initializable {

    @FXML
    private ColorPicker color;

    @FXML
    private ComboBox<String> tipoLetra;

    @FXML
    private Spinner<Integer> tamanoLetra;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configura el valor inicial del ColorPicker
        color.setValue(Personalizacion.getColor());
        // Configura el rango de valores del spinner
        SpinnerValueFactory.IntegerSpinnerValueFactory factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(10,
                18, Personalizacion.getTamano());
        tamanoLetra.setValueFactory(factory);

        // Configura los valores del comboBox
        tipoLetra.getItems().addAll("System", "Arial", "Times New Roman", "Verdana", "Courier New", "Tahoma", "Georgia",
                "Trebuchet MS", "Impact", "Lucida Console", "Consolas");
        tipoLetra.getSelectionModel().select(Personalizacion.getFuente());
    }

    @FXML
    private void clicGuardarActualizar() {
        Optional<ButtonType> resultado = PopUp.confirmacion("Actualizar Interfaz",
                "¿Está seguro que desea modificar la interfaz?",
                "Se perderán los cambios no guardados.");
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

            try {
                TrabajadorRepository repository = new TrabajadorRepository();

                // Actualiza los datos localmente
                String hexColor = "#" + color.getValue().toString().substring(2, 8);
                Personalizacion.getTrabajadorActual().setColorFondo(hexColor);
                String letra = tipoLetra.getValue() + "/" + tamanoLetra.getValue();
                Personalizacion.getTrabajadorActual().setTipoLetra(letra);

                // Actualiza en la bdd
                repository.update(Personalizacion.getTrabajadorActual());

                // Actualiza el programa
                recargarPrograma();
            } catch (IOException e) {
                PopUp.error("Error al cargar ventanas",
                        "Error crítico al cargar el menu, vuelva a intentarlo o contacte un administrador");
            } catch (SQLException e) {
                PopUp.error("Error de conexion", "Ocurrio un error con a la base de datos.");
                return;
            }
        }
    }

    private void recargarPrograma() throws IOException {
        // Borra las ventanas en memoria
        MainController.limpiarCache();

        // Cierra la ventana actual
        Stage stageActual = (Stage) color.getScene().getWindow();
        stageActual.close();

        // Vuelve a abrir el programa en la ventana principal
        Stage stage = new Stage();
        Scene scene = new Scene(loadFXML("MenuPrincipal"), 1300, 780);

        // Usa los estilos base del programa
        scene.getRoot().setStyle(Personalizacion.getTipoLetra()
                + Personalizacion.getTamanoLetra()
                + Personalizacion.getColorFondo());

        stage.setScene(scene);
        stage.setMinWidth(1300);
        stage.setMinHeight(780);
        stage.getIcons().add(Personalizacion.getLogo());
        stage.setTitle(Personalizacion.getEmpresaActual().getNombreEmpresa());
        stage.show();
    }

    private Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/ucv/codelab/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }
}
