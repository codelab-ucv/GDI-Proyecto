package ucv.codelab.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ucv.codelab.Main;
import ucv.codelab.util.Personalizacion;
import ucv.codelab.util.PopUp;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private AnchorPane contenidoPrincipal;

    // Cache para almacenar las vistas ya cargadas (carga dinámica)
    private Map<String, Node> vistaCache = new HashMap<>();
    private Map<String, Object> controladorCache = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Mostrar vista de crear nueva venta al iniciar
        cargarVista("ventas/NuevaVenta.fxml", "nueva-venta", "Nueva Venta");

    }

    // ============== MÉTODOS PARA MENÚ VENTAS ==============

    @FXML
    private void mostrarNuevaVenta() {
        cargarVista("ventas/NuevaVenta.fxml", "nueva-venta", "Nueva Venta");
    }

    @FXML
    private void mostrarConsultarVentas() {
        cargarVista("ventas/ConsultarVentas.fxml", "consultar-ventas", "Consultar Ventas");
    }

    // ============== MÉTODOS PARA MENÚ IMPORTAR ==============

    @FXML
    private void mostrarImportarProductos() {
        cargarVista("importar/ImportarProductos.fxml", "importar-productos", "Importar Productos");
    }

    @FXML
    private void mostrarImportarUsuarios() {
        cargarVista("importar/ImportarUsuarios.fxml", "importar-usuarios", "Importar Usuarios");
    }

    @FXML
    private void mostrarImportarClientes() {
        cargarVista("importar/ImportarClientes.fxml", "importar-clientes", "Importar Clientes");
    }

    // ============== MÉTODOS PARA MENÚ ESTADÍSTICAS ==============

    @FXML
    private void mostrarProductosMasVendidos() {
        cargarVista("estadisticas/ProductosMasVendidos.fxml", "productos-mas-vendidos", "Productos Mas Vendidos");
    }

    // ============== MÉTODOS PARA MENÚ CONFIGURACIÓN ==============

    @FXML
    private void mostrarPersonalizarInterfaz() {
        cargarVista("configuracion/PersonalizarInterfaz.fxml", "personalizar-interfaz", "Personalizar Interfaz");
    }

    @FXML
    private void mostrarDatosEmpresa() {
        cargarVista("configuracion/DatosEmpresa.fxml", "datos-empresa", "Datos de la Empresa");
    }

    @FXML
    private void mostrarGestionUsuarios() {
        cargarVista("configuracion/GestionUsuarios.fxml", "gestion-usuarios", "Gestion de Usuarios");
    }

    @FXML
    private void mostrarRespaldos() {
        // TODO Código para generar un respaldo en la ubicacion indicada
    }

    @FXML
    private void cerrarSesion() {
        Optional<ButtonType> resultado = PopUp.confirmacion("Cerrar Sesión",
                "¿Está seguro que desea cerrar sesión?",
                "Se perderán los cambios no guardados.");

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // Limpiar cache
            vistaCache.clear();
            controladorCache.clear();

            // Borra el usuario actual
            Personalizacion.setTrabajadorActual(null);

            // Cierra la ventana actual
            Stage stageActual = (Stage) contenidoPrincipal.getScene().getWindow();
            stageActual.close();

            // Muestra la ventana del loguin
            try {
                mostrarVentanaLogin();
            } catch (IOException exception) {
                PopUp.error("Error al cargar ventanas",
                        "Error crítico al cargar el menu, vuelva a intentarlo o contacte un administrador");
            }
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

            // Limpiar el contenido actual del AnchorPane
            contenidoPrincipal.getChildren().clear();

            // Agregar la nueva vista
            contenidoPrincipal.getChildren().add(vista);

            // Anclar la vista a todos los lados para que ocupe todo el espacio
            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);

            // Si el controlador tiene un método de actualización, lo llamamos
            actualizarVistaSeleccionada(controlador);

        } catch (IOException e) {
            PopUp.error("Error al cargar la vista: " + titulo, e.getMessage());
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

    private void mostrarVentanaLogin() throws IOException {

        // Crea la ventana del loguin
        Stage stage = new Stage();
        Scene scene = new Scene(loadFXML("Login"), 400, 560);

        // Usa los estilos base del programa
        scene.getRoot().setStyle(Personalizacion.getTipoLetra()
                + Personalizacion.getColorFondo());

        // Establece el stage actual
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(Personalizacion.getLogo());
        stage.setTitle(Personalizacion.getEmpresaActual().getNombreEmpresa());
        stage.show();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/ucv/codelab/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
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

}