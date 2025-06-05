package ucv.codelab.controller.configuracion;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ucv.codelab.model.Empresa;
import ucv.codelab.repository.EmpresaRepository;
import ucv.codelab.util.Personalizacion;
import ucv.codelab.util.PopUp;

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

    @FXML
    private Button botonConfirmar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Se actualiza la imagen según el logo indicado
        imagenLogo.setImage(Personalizacion.getLogo());

        Empresa empresa = Personalizacion.getEmpresaActual();

        // Se actualiza los recuadros de texto
        campoNombreEmpresa.setText(empresa.getNombreEmpresa());
        campoRucEmpresa.setText(empresa.getRuc());
        campoEmailEmpresa.setText(empresa.getEmailEmpresa());
        campoUbicacionEmpresa.setText(empresa.getUbicacion());
        campoLogo.setText(empresa.getLogo());
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
            if (Personalizacion.getEmpresaActual().getLogo() == null
                    || Personalizacion.getEmpresaActual().getLogo().isEmpty()) {
                campoLogo.setText("Selecciona la ubicacion del logo");
            } else {
                campoLogo.setText(Personalizacion.getEmpresaActual().getLogo());
            }
            return;
        }

        // Si es un archivo válido actualiza temporalmente la imagen
        campoLogo.setText(imagen.getAbsolutePath());
        Personalizacion.getEmpresaActual().setLogo(imagen.getAbsolutePath());

        // Actualiza el logo mostrado
        imagenLogo.setImage(Personalizacion.getLogo());
    }

    @FXML
    private void clicConfirmar() {
        if (!validarCamposObligatorios(campoNombreEmpresa.getText(), campoRucEmpresa.getText())) {
            PopUp.error("Campos Obligatorio Inválidos", "Verifique el nombre de la empresa y el RUC");
            return;
        }

        try {
            actualizarEmpresa();
        } catch (Exception e) {
            PopUp.error("Error de conexion", "Ocurrio un error con a la base de datos.");
            return;
        }
    }

    // Verifica si los campos obligatorios tienen valores, si alguno no está
    // completado retorna false
    private boolean validarCamposObligatorios(String... campos) {
        for (String campo : campos) {
            if (campo == null || campo.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void actualizarEmpresa() throws SQLException {
        // Realiza la conexion con la base de datos
        EmpresaRepository repository = new EmpresaRepository();
        // Consulta si la empresa existe
        Optional<Empresa> optional = repository.empresaExiste(campoNombreEmpresa.getText(),
                campoRucEmpresa.getText());

        // Si la empresa existe
        if (optional.isPresent()) {
            // Descarga los ultimos datos almacenados en la base de datos
            Personalizacion.setEmpresaActual(optional.get());
            // Modifica los atributos modificables
            Personalizacion.getEmpresaActual().setEmailEmpresa(campoEmailEmpresa.getText());
            Personalizacion.getEmpresaActual().setUbicacion(campoUbicacionEmpresa.getText());
            Personalizacion.getEmpresaActual().setLogo(campoLogo.getText());
            // Ejecuta un update
            repository.update(Personalizacion.getEmpresaActual());
            PopUp.informacion("Datos actualizados", "Se actualizaron los datos de la empresa con éxito");
        }
        // De lo contrario crea la empresa en local y ejecuta un insert
        else {
            // Crea la empresa en local
            Empresa empresa = new Empresa(-1, campoNombreEmpresa.getText(), campoRucEmpresa.getText(),
                    campoRucEmpresa.getText(), campoUbicacionEmpresa.getText(), campoLogo.getText());
            // Ejecuta un insert
            repository.save(empresa);
            // Actualiza la empresa local
            Personalizacion.setEmpresaActual(empresa);
            PopUp.informacion("Datos insertado", "Se insertaron los datos de la empresa con éxito");
        }
    }
}
