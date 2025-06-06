package ucv.codelab.controller;

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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import ucv.codelab.Main;
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

        // Al presionar Enter intentar loguear
        password.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                clicIngresar();
            }
        });
    }

    @FXML
    private void clicIngresar() {
        try {
            TrabajadorRepository repository = new TrabajadorRepository();

            // Valida si los campos de usuario y contraseña estan vacios
            if (!validarCamposObligatorios(usuario.getText(), password.getText())) {
                PopUp.error("Datos vacíos", "Usuario y/o contraseña vacíos.");
                return;
            }

            // Verifica si hay un jefe y si no lo crea
            validarJefe(repository);
            // al finalizar ya se llama user

            // Si NO pasa la autenticacion
            if (!verificarCredenciales(repository)) {
                PopUp.error("Credenciales inválidas",
                        "Verifique que se haya colocado correctamente el DNI y contraseña");
                return;
            }

            // Valida que la contraseña no sea password
            if (!validarContra(repository)) {
                PopUp.error("Contraseña inválida", "Debe cambiar la contraseña por defecto");
                return;
            }

            // Carga la ventana principal con la configuración del usuario
            try {
                cargarMenu();
            } catch (Exception e) {
                PopUp.error("Error al cargar ventanas",
                        "Error crítico al cargar el menu, vuelva a intentarlo o contacte un administrador");
            }
        } catch (SQLException e) {
            PopUp.error("Error de conexion", "Ocurrio un error con a la base de datos.");
        }
    }

    /**
     * Verifica que las cadenas de texto indicados son diferentes de null y no estan
     * vacias
     * 
     * @param campos Textos a verificar
     * @return {@code true} si todos los campos son válidos
     */
    private boolean validarCamposObligatorios(String... campos) {
        for (String campo : campos) {
            if (campo == null || campo.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica que el usuario y contraseña coincidan en la base de datos
     * 
     * @param repository Repositorio de la tabla trabajador
     * @return {@code true} si existen las credenciales en la base de datos
     */
    private boolean verificarCredenciales(TrabajadorRepository repository) {
        Optional<Trabajador> optional = repository.login(usuario.getText(), password.getText());
        // Si es que existe un trabajador
        if (optional.isPresent()) {
            Personalizacion.setTrabajadorActual(optional.get());
            return true;
        }
        return false;
    }

    /**
     * Comprueba si hay un usuario con el cargo JEFE en la base de datos, en caso no
     * haya ninguno se crea uno con los datos actualmente ingresados
     * 
     * @param repository Repositorio de la tabla trabajador
     */
    private void validarJefe(TrabajadorRepository repository) {
        // Verifica que no haya usuarios con permisos de jefe
        if (repository.sinJefe()) {
            // Crea el usuario del jefe
            Trabajador jefe = new Trabajador(-1, "admin", usuario.getText(),
                    "JEFE", null, null, password.getText());
            // Lo sube a la base de datos
            repository.save(jefe);

            // Guarda el trabajador en datos locales
            Personalizacion.setTrabajadorActual(jefe);

            PopUp.informacion("Usuario creado", "Se ha creado un usuario administrador con la información indicada."
                    + " Puedes actualizar los datos en la configuración.");
        }
    }

    /**
     * Verifica que la contraseña actual no sea password (contraseña por defecto) y
     * obliga a cambiar la contraseña
     * 
     * @param repository Repositorio de la tabla trabajador
     * @return {@code true} si la nueva contraseña es diferente a password, en caso
     *         que no se ingrese nada o sea password retorna {@code false}
     */
    private boolean validarContra(TrabajadorRepository repository) {
        // Si se está usando la contraseña por defecto
        if (Personalizacion.getTrabajadorActual().getPassword().equals("password")) {
            String password = PopUp.inputDialog("Ingrese una nueva contraseña", "Nueva contraseña:").trim();
            // Si no se ingresa nueva contraseña o sigue siendo la misma
            if (password.equals("") || password.equalsIgnoreCase("password")) {
                return false;
            }
            // De lo contrario actualiza la contraseña actual
            Personalizacion.getTrabajadorActual().setPassword(password);
            repository.update(Personalizacion.getTrabajadorActual());
        }
        // SI se completó todo el proceso o la contraseña no era por defecto
        return true;
    }

    /**
     * Cierra la ventana del login y abre uno nuevo con el menu principal
     * 
     * @throws IOException
     */
    private void cargarMenu() throws IOException {
        // Cierra el menu del login actual
        Stage loginStage = (Stage) titulo.getScene().getWindow();
        loginStage.close();

        // Crea la ventana del menu
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

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/ucv/codelab/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }
}
