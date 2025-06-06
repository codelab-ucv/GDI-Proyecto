package ucv.codelab.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import ucv.codelab.model.Trabajador;
import ucv.codelab.repository.TrabajadorRepository;
import ucv.codelab.util.Personalizacion;
import ucv.codelab.util.PopUp;

public class LoginController implements Initializable {

    @FXML
    private Label titulo;

    @FXML
    private ImageView logo;

    @FXML
    private TextField usuario;

    @FXML
    private PasswordField password;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titulo.setText(Personalizacion.getEmpresaActual().getNombreEmpresa());
        logo.setImage(Personalizacion.getLogo());
        titulo.requestFocus();
    }

    @FXML
    private void clicIngresar() {
        try {
            TrabajadorRepository repository = new TrabajadorRepository();

            // Si NO pasa la autenticacion
            if (!verificarCredenciales(repository)) {
                PopUp.error("Credenciales inválidas",
                        "Verifique que se haya colocado correctamente el DNI y contraseña");
                return;
            }
            // Si se está usando la contraseña por defecto
            if (Personalizacion.getTrabajadorActual().getPassword().equals("password")) {
                // TODO mostar una pestaña para cambio de contraseña
            }
            // TODO carga la ventana principal con la configuracion del usuario
        } catch (SQLException e) {
            PopUp.error("Error de conexion", "Ocurrio un error con a la base de datos.");
        }
    }

    private boolean verificarCredenciales(TrabajadorRepository repository) {
        Optional<Trabajador> optional = repository.login(usuario.getText(), password.getText());
        // Si es que existe un trabajador
        if (optional.isPresent()) {
            Personalizacion.setTrabajadorActual(optional.get());
            return true;
        }
        return false;
    }
}
