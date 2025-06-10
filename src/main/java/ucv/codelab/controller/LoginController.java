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

/**
 * Controlador para la gestión del proceso de autenticación de usuarios.
 * 
 * <p>
 * Esta clase implementa {@link Initializable} y maneja la interfaz de login
 * del sistema, proporcionando autenticación segura, validación de credenciales
 * y configuración inicial del sistema para nuevos usuarios administradores.
 * </p>
 * 
 * <p>
 * <strong>Funcionalidades principales:</strong>
 * </p>
 * <ul>
 * <li>Autenticación de usuarios mediante DNI y contraseña</li>
 * <li>Creación automática de usuario administrador inicial</li>
 * <li>Validación y cambio forzado de contraseñas por defecto</li>
 * <li>Carga automática del menú principal tras autenticación exitosa</li>
 * <li>Aplicación de personalización visual según preferencias del usuario</li>
 * </ul>
 * 
 * <p>
 * <strong>Proceso de autenticación:</strong>
 * </p>
 * <ul>
 * <li>Validación de campos obligatorios (usuario y contraseña)</li>
 * <li>Verificación de existencia de usuario administrador</li>
 * <li>Autenticación contra la base de datos</li>
 * <li>Validación de contraseña no sea la predeterminada</li>
 * <li>Carga de la interfaz principal personalizada</li>
 * </ul>
 * 
 * <p>
 * El controlador maneja la lógica de bootstrapping del sistema, creando
 * automáticamente el primer usuario administrador cuando no existe ninguno,
 * y garantizando que se cambien las contraseñas por defecto por seguridad.
 * </p>
 * 
 * @see Initializable
 * @see TrabajadorRepository
 * @see Trabajador
 * @see Personalizacion
 */
public class LoginController implements Initializable {

    /**
     * Label que muestra el nombre de la empresa como título de la aplicación.
     * 
     * <p>
     * Se inicializa automáticamente con el nombre de la empresa actual
     * obtenido desde {@link Personalizacion#getEmpresaActual()}.
     * </p>
     */
    @FXML
    private Label titulo;

    /**
     * ImageView que muestra el logo de la empresa en la pantalla de login.
     * 
     * <p>
     * Se configura automáticamente con la imagen corporativa obtenida
     * desde {@link Personalizacion#getLogo()}.
     * </p>
     */
    @FXML
    private ImageView logo;

    /**
     * Campo de texto para el ingreso del nombre de usuario (DNI).
     * 
     * <p>
     * Acepta el documento de identidad del trabajador que será utilizado
     * como identificador único para la autenticación.
     * </p>
     */
    @FXML
    private TextField usuario;

    /**
     * Campo de contraseña para el ingreso de credenciales de acceso.
     * 
     * <p>
     * Campo enmascarado que oculta los caracteres ingresados por seguridad.
     * Incluye funcionalidad de login mediante la tecla Enter.
     * </p>
     */
    @FXML
    private PasswordField password;

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Configura la interfaz inicial del login estableciendo el título con el
     * nombre de la empresa, cargando el logo corporativo y configurando el
     * comportamiento del campo de contraseña para permitir login con Enter.
     * </p>
     * 
     * <p>
     * <strong>Configuraciones realizadas:</strong>
     * </p>
     * <ul>
     * <li>Establecimiento del título con el nombre de la empresa</li>
     * <li>Carga del logo corporativo desde la configuración</li>
     * <li>Configuración del evento KeyPressed para login con Enter</li>
     * </ul>
     */
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

    /**
     * Maneja el proceso completo de autenticación del usuario.
     * 
     * <p>
     * Ejecuta una secuencia de validaciones y verificaciones para autenticar
     * al usuario, incluyendo la creación automática del primer administrador
     * del sistema si no existe ninguno, y el cambio forzado de contraseñas
     * por defecto por razones de seguridad.
     * </p>
     * 
     * <p>
     * <strong>Secuencia de validaciones:</strong>
     * </p>
     * <ul>
     * <li>Validación de campos obligatorios no vacíos</li>
     * <li>Verificación/creación de usuario administrador inicial</li>
     * <li>Autenticación de credenciales contra la base de datos</li>
     * <li>Validación de contraseña no sea la predeterminada</li>
     * <li>Carga del menú principal con configuración personalizada</li>
     * </ul>
     * 
     * <p>
     * En caso de error en cualquier paso del proceso, se muestra un mensaje
     * de error apropiado al usuario y se detiene la ejecución.
     * </p>
     */
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
     * Verifica que las cadenas de texto indicadas son diferentes de null y no están
     * vacías.
     * 
     * <p>
     * Método utilitario que valida múltiples campos de texto de forma simultánea,
     * verificando que ninguno sea nulo o contenga únicamente espacios en blanco.
     * </p>
     * 
     * <p>
     * La validación incluye el uso de {@code trim()} para eliminar espacios
     * en blanco al inicio y final, asegurando que campos con solo espacios
     * sean considerados como vacíos.
     * </p>
     * 
     * @param campos Textos a verificar (cantidad variable de parámetros)
     * @return {@code true} si todos los campos son válidos (no nulos y no vacíos),
     *         {@code false} si al menos uno es nulo o vacío
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
     * Verifica que el usuario y contraseña coincidan en la base de datos.
     * 
     * <p>
     * Utiliza el repositorio de trabajadores para verificar las credenciales
     * ingresadas contra los registros almacenados. Si las credenciales son
     * válidas, establece el trabajador autenticado en la configuración global
     * del sistema.
     * </p>
     * 
     * <p>
     * El método actualiza automáticamente
     * {@link Personalizacion#setTrabajadorActual}
     * con los datos del trabajador autenticado, permitiendo que el resto de la
     * aplicación acceda a la información del usuario logueado.
     * </p>
     * 
     * @param repository Repositorio de la tabla trabajador para consultas de
     *                   autenticación
     * @return {@code true} si existen las credenciales en la base de datos y son
     *         válidas,
     *         {@code false} si las credenciales no coinciden con ningún registro
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
     * haya ninguno se crea uno con los datos actualmente ingresados.
     * 
     * <p>
     * Este método implementa la lógica de bootstrapping del sistema, creando
     * automáticamente el primer usuario administrador cuando la base de datos
     * no contiene ningún trabajador con permisos de JEFE.
     * </p>
     * 
     * <p>
     * <strong>Proceso de creación del administrador inicial:</strong>
     * </p>
     * <ul>
     * <li>Verifica la ausencia de usuarios con cargo JEFE</li>
     * <li>Crea un nuevo trabajador con los datos ingresados en el formulario</li>
     * <li>Asigna automáticamente el nombre "admin" y cargo "JEFE"</li>
     * <li>Persiste el nuevo usuario en la base de datos</li>
     * <li>Establece el usuario como trabajador actual del sistema</li>
     * <li>Notifica al usuario sobre la creación exitosa</li>
     * </ul>
     * 
     * @param repository Repositorio de la tabla trabajador para operaciones CRUD
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
     * Verifica que la contraseña actual no sea "password" (contraseña por defecto)
     * y
     * obliga a cambiar la contraseña.
     * 
     * <p>
     * Por razones de seguridad, el sistema fuerza a los usuarios a cambiar
     * la contraseña por defecto "password" antes de permitir el acceso completo
     * al sistema. Este método maneja todo el proceso de validación y cambio.
     * </p>
     * 
     * <p>
     * <strong>Proceso de validación y cambio:</strong>
     * </p>
     * <ul>
     * <li>Detecta si la contraseña actual es "password"</li>
     * <li>Solicita al usuario ingresar una nueva contraseña</li>
     * <li>Valida que la nueva contraseña no sea vacía ni "password"</li>
     * <li>Actualiza la contraseña en el objeto trabajador</li>
     * <li>Persiste el cambio en la base de datos</li>
     * </ul>
     * 
     * @param repository Repositorio de la tabla trabajador para actualización de
     *                   datos
     * @return {@code true} si la nueva contraseña es diferente a "password" y se
     *         actualizó correctamente,
     *         {@code false} si no se ingresa nada, sigue siendo "password", o se
     *         cancela el proceso
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
     * Cierra la ventana del login y abre el menú principal con la configuración
     * personalizada.
     * 
     * <p>
     * Maneja la transición entre la pantalla de login y el menú principal de la
     * aplicación, aplicando automáticamente la configuración visual personalizada
     * del usuario autenticado (colores, fuentes, tamaños, etc.).
     * </p>
     * 
     * <p>
     * <strong>Configuraciones aplicadas al menú principal:</strong>
     * </p>
     * <ul>
     * <li>Dimensiones de ventana: 1300x780 píxeles mínimo</li>
     * <li>Estilos personalizados: fuente, tamaño y color de fondo</li>
     * <li>Icono de la aplicación desde configuración empresarial</li>
     * <li>Título de ventana con nombre de la empresa</li>
     * </ul>
     * 
     * <p>
     * El método utiliza {@link Personalizacion} para obtener todos los elementos
     * de personalización visual y aplicarlos automáticamente a la nueva ventana.
     * </p>
     * 
     * @throws IOException Si ocurre un error al cargar el archivo FXML del menú
     *                     principal
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

    /**
     * Carga un archivo FXML desde el directorio de vistas de la aplicación.
     * 
     * <p>
     * Método utilitario para cargar archivos FXML utilizando el FXMLLoader
     * con la ruta base del proyecto. Facilita la carga de diferentes vistas
     * de la aplicación de forma consistente.
     * </p>
     * 
     * @param fxml Nombre del archivo FXML (sin extensión) a cargar
     * @return Parent root del archivo FXML cargado
     * @throws IOException Si el archivo FXML no existe o hay errores en su
     *                     estructura
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/ucv/codelab/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }
}