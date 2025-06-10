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

/**
 * Controlador para la gestión y configuración de datos de empresa en la
 * interfaz gráfica.
 * 
 * <p>
 * Esta clase implementa el patrón MVC como controlador JavaFX para manejar
 * la configuración de información empresarial, incluyendo datos básicos,
 * logotipo y configuraciones de personalización. Proporciona una interfaz
 * completa para crear, actualizar y gestionar los datos de la empresa activa.
 * </p>
 * 
 * <p>
 * <strong>Funcionalidades principales:</strong>
 * </p>
 * <ul>
 * <li>Configuración de datos empresariales (nombre, RUC, email, ubicación)</li>
 * <li>Selección y gestión de logotipo empresarial</li>
 * <li>Validación de campos obligatorios y formatos</li>
 * <li>Operaciones CRUD automáticas (insertar/actualizar según existencia)</li>
 * <li>Integración con sistema de personalización global</li>
 * <li>Manejo de errores y retroalimentación visual al usuario</li>
 * </ul>
 * 
 * <p>
 * <strong>Campos de la interfaz gestionados:</strong>
 * </p>
 * <ul>
 * <li><strong>Nombre de empresa</strong> - Campo obligatorio para
 * identificación</li>
 * <li><strong>RUC</strong> - Registro Único de Contribuyente (obligatorio y
 * único)</li>
 * <li><strong>Email</strong> - Correo electrónico empresarial (opcional)</li>
 * <li><strong>Ubicación</strong> - Dirección física de la empresa
 * (opcional)</li>
 * <li><strong>Logo</strong> - Imagen corporativa con selector de archivos</li>
 * </ul>
 * 
 * <p>
 * El controlador implementa {@link Initializable} para configurar
 * automáticamente
 * la interfaz con los datos de la empresa actual al cargar la vista.
 * Utiliza el patrón Repository para persistencia de datos y PopUp para
 * notificaciones al usuario.
 * </p>
 * 
 * <p>
 * <strong>Flujo de operaciones:</strong>
 * </p>
 * <ol>
 * <li>Carga automática de datos existentes al inicializar</li>
 * <li>Validación de campos obligatorios en tiempo real</li>
 * <li>Selección opcional de logotipo mediante FileChooser</li>
 * <li>Verificación de existencia para determinar operación (INSERT/UPDATE)</li>
 * <li>Persistencia en base de datos y actualización de configuración
 * global</li>
 * </ol>
 * 
 * @see Empresa
 * @see EmpresaRepository
 * @see Personalizacion
 * @see PopUp
 * @see Initializable
 */
public class EmpresaController implements Initializable {

    /**
     * Componente ImageView para mostrar el logotipo de la empresa.
     * 
     * <p>
     * Muestra una vista previa del logotipo actual de la empresa,
     * actualizándose automáticamente cuando se selecciona un nuevo archivo
     * de imagen a través del selector de archivos.
     * </p>
     */
    @FXML
    private ImageView imagenLogo;

    /**
     * Campo de texto para el nombre de la empresa.
     * 
     * <p>
     * Campo obligatorio que almacena la denominación social o razón social
     * de la empresa. Se utiliza junto con el RUC para verificar la
     * existencia de la empresa en la base de datos.
     * </p>
     */
    @FXML
    private TextField campoNombreEmpresa;

    /**
     * Campo de texto para el Registro Único de Contribuyente (RUC).
     * 
     * <p>
     * Campo obligatorio y único que identifica fiscalmente a la empresa.
     * Debe cumplir con el formato válido de RUC y se usa como parte
     * de la clave compuesta para verificar duplicados.
     * </p>
     */
    @FXML
    private TextField campoRucEmpresa;

    /**
     * Campo de texto para el correo electrónico empresarial.
     * 
     * <p>
     * Campo opcional que almacena la dirección de correo principal
     * de la empresa para comunicaciones y notificaciones del sistema.
     * </p>
     */
    @FXML
    private TextField campoEmailEmpresa;

    /**
     * Campo de texto para la ubicación física de la empresa.
     * 
     * <p>
     * Campo opcional que contiene la dirección completa o ubicación
     * de las instalaciones principales de la empresa.
     * </p>
     */
    @FXML
    private TextField campoUbicacionEmpresa;

    /**
     * Botón para abrir el selector de archivos de logotipo.
     * 
     * <p>
     * Al hacer clic, abre un FileChooser configurado para seleccionar
     * archivos de imagen (PNG, JPG) desde el directorio Pictures del usuario.
     * </p>
     */
    @FXML
    private Button botonSeleccionarLogo;

    /**
     * Campo de texto que muestra la ruta del archivo de logotipo seleccionado.
     * 
     * <p>
     * Campo de solo lectura visual que muestra la ruta completa al archivo
     * de imagen del logotipo, actualizada automáticamente cuando se
     * selecciona un nuevo archivo.
     * </p>
     */
    @FXML
    private TextField campoLogo;

    /**
     * Botón para confirmar y guardar los cambios realizados.
     * 
     * <p>
     * Ejecuta la validación de campos obligatorios y procede con
     * la operación de guardado (insertar o actualizar) según corresponda.
     * </p>
     */
    @FXML
    private Button botonConfirmar;

    /**
     * Inicializa el controlador cargando los datos de la empresa actual.
     * 
     * <p>
     * Método llamado automáticamente por JavaFX después de cargar el archivo FXML.
     * Configura la interfaz con los datos de la empresa activa obtenidos del
     * sistema de personalización, incluyendo la carga del logotipo y
     * poblado de todos los campos de texto.
     * </p>
     * 
     * <p>
     * <strong>Operaciones realizadas:</strong>
     * </p>
     * <ul>
     * <li>Carga y muestra el logotipo actual de la empresa</li>
     * <li>Popula todos los campos de texto con datos existentes</li>
     * <li>Configura el estado inicial de la interfaz</li>
     * </ul>
     * 
     * @param location  URL de ubicación del archivo FXML (no utilizado)
     * @param resources ResourceBundle con recursos localizados (no utilizado)
     */
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

    /**
     * Abre un selector de archivos configurado para imágenes de logotipo.
     * 
     * <p>
     * Crea y configura un FileChooser específicamente diseñado para
     * seleccionar archivos de imagen que servirán como logotipo empresarial.
     * El selector está optimizado para formatos de imagen comunes y
     * se abre en el directorio Pictures del usuario por defecto.
     * </p>
     * 
     * <p>
     * <strong>Configuración del FileChooser:</strong>
     * </p>
     * <ul>
     * <li>Título: "Seleccionar logo"</li>
     * <li>Formatos permitidos: PNG, JPG</li>
     * <li>Directorio inicial: {user.home}/Pictures</li>
     * <li>Ventana padre: ventana actual del botón</li>
     * </ul>
     * 
     * <p>
     * Si el directorio Pictures no existe, el selector se abrirá
     * en el directorio por defecto del sistema.
     * </p>
     * 
     * @return File seleccionado por el usuario, o null si se cancela la selección
     */
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

    /**
     * Maneja el evento de clic en el botón de selección de logotipo.
     * 
     * <p>
     * Abre el selector de archivos y procesa la selección del usuario,
     * actualizando tanto la vista previa del logotipo como los datos
     * temporales de la empresa. Si no se selecciona ningún archivo,
     * mantiene la configuración actual o muestra un mensaje indicativo.
     * </p>
     * 
     * <p>
     * <strong>Comportamiento según selección:</strong>
     * </p>
     * <ul>
     * <li><strong>Archivo seleccionado:</strong> Actualiza la ruta, la vista previa
     * y los datos temporales de la empresa</li>
     * <li><strong>Selección cancelada:</strong> Mantiene el logotipo actual si
     * existe,
     * o muestra mensaje instructivo si no hay logotipo configurado</li>
     * </ul>
     * 
     * <p>
     * Los cambios realizados son temporales hasta que se confirmen
     * mediante el botón de confirmación.
     * </p>
     */
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

    /**
     * Maneja el evento de clic en el botón de confirmación para guardar cambios.
     * 
     * <p>
     * Ejecuta el flujo completo de validación y persistencia de datos
     * empresariales.
     * Primero valida que los campos obligatorios estén completos, luego procede
     * con la operación de guardado. En caso de errores, muestra mensajes
     * informativos al usuario mediante PopUps.
     * </p>
     * 
     * <p>
     * <strong>Flujo de ejecución:</strong>
     * </p>
     * <ol>
     * <li>Validación de campos obligatorios (nombre y RUC)</li>
     * <li>Llamada al método de actualización de empresa</li>
     * <li>Manejo de excepciones y notificación de errores</li>
     * </ol>
     * 
     * <p>
     * <strong>Mensajes de error posibles:</strong>
     * </p>
     * <ul>
     * <li>"Campos Obligatorio Inválidos" - cuando nombre o RUC están vacíos</li>
     * <li>"Error de conexion" - cuando ocurre un problema con la base de datos</li>
     * </ul>
     */
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

    /**
     * Valida que los campos obligatorios contengan valores válidos.
     * 
     * <p>
     * Verifica que todos los campos pasados como parámetros tengan contenido
     * válido, considerando como inválidos los valores null, vacíos o que
     * contengan solo espacios en blanco.
     * </p>
     * 
     * <p>
     * Este método utiliza varargs para permitir validación flexible de
     * múltiples campos simultáneamente, siendo especialmente útil para
     * validar los campos obligatorios nombre y RUC de la empresa.
     * </p>
     * 
     * <p>
     * <strong>Criterios de validación:</strong>
     * </p>
     * <ul>
     * <li>No debe ser null</li>
     * <li>No debe estar vacío después de trim()</li>
     * <li>Debe contener al menos un carácter no-espacio</li>
     * </ul>
     * 
     * @param campos Array variable de strings a validar
     * @return true si todos los campos son válidos, false si alguno es inválido
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
     * Actualiza o inserta los datos de empresa en la base de datos.
     * 
     * <p>
     * Ejecuta la lógica de persistencia determinando automáticamente si debe
     * realizar una operación de actualización (UPDATE) o inserción (INSERT)
     * basándose en la existencia previa de una empresa con el mismo nombre y RUC.
     * </p>
     * 
     * <p>
     * <strong>Flujo para empresa existente (UPDATE):</strong>
     * </p>
     * <ol>
     * <li>Carga los datos actuales desde la base de datos</li>
     * <li>Actualiza solo los campos modificables (email, ubicación, logo)</li>
     * <li>Ejecuta UPDATE en la base de datos</li>
     * <li>Actualiza la configuración global de personalización</li>
     * <li>Muestra mensaje de confirmación "Datos actualizados"</li>
     * </ol>
     * 
     * <p>
     * <strong>Flujo para empresa nueva (INSERT):</strong>
     * </p>
     * <ol>
     * <li>Crea nuevo objeto Empresa con ID temporal (-1)</li>
     * <li>Ejecuta INSERT en la base de datos (genera ID automático)</li>
     * <li>Actualiza la configuración global con la nueva empresa</li>
     * <li>Muestra mensaje de confirmación "Datos insertado"</li>
     * </ol>
     * 
     * <p>
     * <strong>Campos inmutables en UPDATE:</strong> nombre_empresa, ruc<br>
     * <strong>Campos modificables:</strong> email_empresa, ubicacion, logo
     * </p>
     * 
     * @throws SQLException si ocurre un error al conectar con la base de datos
     *                      o al ejecutar las operaciones SQL
     */
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
                    campoEmailEmpresa.getText(), campoUbicacionEmpresa.getText(), campoLogo.getText());
            // Ejecuta un insert
            repository.save(empresa);
            // Actualiza la empresa local
            Personalizacion.setEmpresaActual(empresa);
            PopUp.informacion("Datos insertado", "Se insertaron los datos de la empresa con éxito");
        }
    }
}