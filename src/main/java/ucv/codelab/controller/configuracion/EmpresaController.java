package ucv.codelab.controller.configuracion;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ucv.codelab.util.Personalizacion;

public class EmpresaController implements Initializable {

    @FXML
    private ImageView imagenLogo;

    @FXML
    private TextField campoNombreEmpresa;

    @FXML
    private TextField campoRucEmpresa;

    @FXML
    private TextField campoEmailEmpresa;

    @FXML
    private TextField campoUbicacionEmpresa;

    @FXML
    private Button botonSeleccionarLogo;

    @FXML
    private TextField campoLogo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Se actualiza la imagen según el logo indicado
        refrescarLogo();

        // TODO EDITAR PARA QUE USE EL PERSONALIZADO
    }

    private void refrescarLogo() {
        Image image;
        try {
            image = new Image(Personalizacion.LOGO_EMPRESA_PERSONALIZADO);
        } catch (Exception e) {
            System.err.println("Error al cargar el logo personalizado: " + e.getMessage()
                    + "\nSe usaran datos por defecto");
            image = new Image(Personalizacion.LOGO_EMPRESA_ORIGINAL);
        }
        imagenLogo.setImage(image);
    }

    private File selectorArchivo() {
        // Crear una instancia de FileChooser
        FileChooser fileChooser = new FileChooser();

        // Configurar el título del diálogo
        fileChooser.setTitle("Seleccionar logo");

        // Permite solo los archivos de imagen
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg"));

        // Establece el directorio inicial en Pictures
        String userHome = System.getProperty("user.home");
        File initialDirectory = new File(userHome, "Pictures");
        if (initialDirectory.exists()) {
            fileChooser.setInitialDirectory(initialDirectory);
        }

        // Obtener la ventana actual
        Stage stage = (Stage) botonSeleccionarLogo.getScene().getWindow();

        // Mostrar el diálogo de selección de archivo
        return fileChooser.showOpenDialog(stage);
    }

    @FXML
    private void clicSeleccionarLogo() {
        File imagen = selectorArchivo();

        // Si el logo indicado es nulo no hace cambios en el logo mostrado
        if (imagen == null) {
            // Si no se selecciono ningun archivo intentara usar el logo personalizado
            if (Personalizacion.LOGO_EMPRESA_PERSONALIZADO.isEmpty()) {
                campoLogo.setText("Selecciona la ubicacion del logo");
            } else {
                campoLogo.setText(Personalizacion.LOGO_EMPRESA_PERSONALIZADO);
            }
            return;
        }

        // Si es un archivo válido actualiza temporalmente la imagen
        String uri = imagen.toURI().toString();

        campoLogo.setText(imagen.getAbsolutePath());
        Personalizacion.LOGO_EMPRESA_PERSONALIZADO = uri;

        // Actualiza el logo mostrado
        refrescarLogo();
    }
}
