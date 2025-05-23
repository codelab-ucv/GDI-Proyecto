package ucv.codelab.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private StackPane contenidoPrincipal;

    // Cache para almacenar las vistas ya cargadas (carga dinámica)
    private Map<String, Node> vistaCache = new HashMap<>();
    private Map<String, Object> controladorCache = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Mostrar vista de crear nueva venta al iniciar
        cargarVista("NuevaVenta.fxml", "nueva-venta", "Nueva Venta");
    }

    // ============== MÉTODOS PARA MENÚ VENTAS ==============

    @FXML
    private void mostrarNuevaVenta() {
        cargarVista("NuevaVenta.fxml", "nueva-venta", "Nueva Venta");
    }

    @FXML
    private void mostrarConsultarVentas() {
        cargarVista("ConsultarVentas.fxml", "consultar-ventas", "Consultar Ventas");
    }

    @FXML
    private void mostrarHistorialVentas() {
        cargarVista("HistorialVentas.fxml", "historial-ventas", "Historial de Ventas");
    }

    // ============== MÉTODOS PARA MENÚ IMPORTAR ==============

    @FXML
    private void mostrarImportarProductos() {
        cargarVista("ImportarProductos.fxml", "importar-productos", "Importar Productos");
    }

    @FXML
    private void mostrarImportarUsuarios() {
        cargarVista("ImportarUsuarios.fxml", "importar-usuarios", "Importar Usuarios");
    }

    @FXML
    private void mostrarImportarClientes() {
        cargarVista("ImportarClientes.fxml", "importar-clientes", "Importar Clientes");
    }

    // ============== MÉTODOS PARA MENÚ ESTADÍSTICAS ==============

    @FXML
    private void mostrarVentasRegistradas() {
        cargarVista("VentasRegistradas.fxml", "ventas-registradas", "Ventas Registradas");
    }

    @FXML
    private void mostrarProductosMasVendidos() {
        cargarVista("ProductosMasVendidos.fxml", "productos-mas-vendidos", "Productos Más Vendidos");
    }

    // ============== MÉTODOS PARA MENÚ CONFIGURACIÓN ==============

    @FXML
    private void mostrarPersonalizarInterfaz() {
        cargarVista("PersonalizarInterfaz.fxml", "personalizar-interfaz", "Personalizar Interfaz");
    }

    @FXML
    private void mostrarDatosEmpresa() {
        cargarVista("DatosEmpresa.fxml", "datos-empresa", "Datos de la Empresa");
    }

    @FXML
    private void mostrarGestionUsuarios() {
        cargarVista("GestionUsuarios.fxml", "gestion-usuarios", "Gestión de Usuarios");
    }

    @FXML
    private void mostrarRespaldos() {
        cargarVista("Respaldos.fxml", "respaldos", "Respaldos");
    }

    @FXML
    private void cerrarSesion() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cerrar Sesión");
        confirmacion.setHeaderText("¿Está seguro que desea cerrar sesión?");
        confirmacion.setContentText("Se perderán los cambios no guardados.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // Limpiar cache
            vistaCache.clear();
            controladorCache.clear();

            // Cerrar ventana actual y mostrar login
            Stage stageActual = (Stage) contenidoPrincipal.getScene().getWindow();
            stageActual.close();

            // TODO Aquí podrías abrir la ventana de login nuevamente
            // mostrarVentanaLogin();
        }

    }

    // ============== MÉTODOS AUXILIARES ==============

    /**
     * Carga una vista de forma dinámica y la almacena en cache
     * 
     * @param fxmlFile Nombre del archivo FXML
     * @param cacheKey Clave para el cache
     * @param titulo   Título para logging/debugging
     */
    private void cargarVista(String fxmlFile, String cacheKey, String titulo) {
        try {
            Node vista = vistaCache.get(cacheKey);
            Object controlador = null;

            // Si la vista no está en cache, la cargamos
            if (vista == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ucv/codelab/view/" + fxmlFile));
                vista = loader.load();
                controlador = loader.getController();

                // Guardamos en cache para futuras cargas
                vistaCache.put(cacheKey, vista);
                if (controlador != null) {
                    controladorCache.put(cacheKey, controlador);
                }

                System.out.println("Vista " + titulo + " cargada por primera vez");
            } else {
                controlador = controladorCache.get(cacheKey);
                System.out.println("Vista " + titulo + " cargada desde cache");
            }

            // Limpiamos el contenido actual y agregamos la nueva vista
            contenidoPrincipal.getChildren().clear();
            contenidoPrincipal.getChildren().add(vista);

            // Si el controlador tiene un método de actualización, lo llamamos
            actualizarVistaSeleccionada(controlador);

        } catch (IOException e) {
            mostrarError("Error al cargar la vista: " + titulo, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Actualiza la vista seleccionada si tiene métodos de actualización
     * 
     * @param controlador Controlador de la vista
     */
    private void actualizarVistaSeleccionada(Object controlador) {
        if (controlador == null)
            return;

        try {
            // Intentar llamar método de actualización común
            var metodoActualizar = controlador.getClass().getMethod("actualizarVista");
            metodoActualizar.invoke(controlador);
        } catch (Exception e) {
            // No todos los controladores tendrán este método, ignorar
        }

        try {
            // Intentar llamar método de refrescar datos
            var metodoRefrescar = controlador.getClass().getMethod("refrescarDatos");
            metodoRefrescar.invoke(controlador);
        } catch (Exception e) {
            // No todos los controladores tendrán este método, ignorar
        }
    }

    /**
     * Obtiene el controlador de una vista específica del cache
     * 
     * @param cacheKey Clave del cache
     * @return Controlador de la vista o null si no existe
     */
    public Object obtenerControladorVista(String cacheKey) {
        return controladorCache.get(cacheKey);
    }

    /**
     * Limpia el cache de vistas (útil para refrescar datos)
     */
    public void limpiarCache() {
        vistaCache.clear();
        controladorCache.clear();
        System.out.println("Cache de vistas limpiado");
    }

    /**
     * Limpia el cache de una vista específica
     * 
     * @param cacheKey Clave de la vista a limpiar
     */
    public void limpiarCacheVista(String cacheKey) {
        vistaCache.remove(cacheKey);
        controladorCache.remove(cacheKey);
        System.out.println("Cache de vista " + cacheKey + " limpiado");
    }

    /**
     * Muestra un diálogo de error
     * 
     * @param titulo  Título del error
     * @param mensaje Mensaje del error
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un diálogo informativo
     * 
     * @param titulo  Título de la información
     * @param mensaje Mensaje informativo
     */
    public void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}