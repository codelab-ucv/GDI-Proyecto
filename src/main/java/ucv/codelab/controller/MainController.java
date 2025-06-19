package ucv.codelab.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import ucv.codelab.Main;
import ucv.codelab.model.Cliente;
import ucv.codelab.model.Orden;
import ucv.codelab.model.Producto;
import ucv.codelab.model.SubOrden;
import ucv.codelab.model.Trabajador;
import ucv.codelab.repository.ClienteRepository;
import ucv.codelab.repository.OrdenRepository;
import ucv.codelab.repository.ProductoRepository;
import ucv.codelab.repository.SubOrdenRepository;
import ucv.codelab.repository.TrabajadorRepository;
import ucv.codelab.util.Personalizacion;
import ucv.codelab.util.PopUp;

/**
 * Controlador principal para la gestión de la interfaz de usuario del menú
 * principal.
 * 
 * <p>
 * Esta clase implementa {@link Initializable} y actúa como controlador maestro
 * de la aplicación, manejando la navegación entre diferentes módulos y vistas,
 * implementando un sistema de caché optimizado para mejorar el rendimiento
 * y la experiencia del usuario.
 * </p>
 * 
 * <p>
 * <strong>Funcionalidades principales:</strong>
 * </p>
 * <ul>
 * <li>Navegación dinámica entre diferentes módulos del sistema</li>
 * <li>Sistema de caché inteligente para vistas y controladores</li>
 * <li>Gestión de sesión de usuario con cierre seguro</li>
 * <li>Carga automática de vistas con anclaje responsive</li>
 * <li>Actualización automática de vistas cuando es necesario</li>
 * </ul>
 * 
 * <p>
 * <strong>Módulos disponibles:</strong>
 * </p>
 * <ul>
 * <li><strong>Ventas:</strong> Nueva venta y consulta de ventas realizadas</li>
 * <li><strong>Importar:</strong> Importación de productos, usuarios y
 * clientes</li>
 * <li><strong>Estadísticas:</strong> Análisis de productos más vendidos</li>
 * <li><strong>Configuración:</strong> Personalización, datos empresa y gestión
 * de usuarios</li>
 * </ul>
 * 
 * <p>
 * <strong>Sistema de caché:</strong>
 * </p>
 * <ul>
 * <li>Almacenamiento en memoria de vistas previamente cargadas</li>
 * <li>Preservación de estados de controladores entre navegaciones</li>
 * <li>Reducción significativa de tiempos de carga en navegación repetida</li>
 * <li>Limpieza automática de caché al cerrar sesión por seguridad</li>
 * </ul>
 * 
 * <p>
 * El controlador utiliza reflexión Java para invocar métodos de actualización
 * estándar en los controladores de vistas, permitiendo refrescar datos
 * automáticamente cuando se navega a una vista previamente cargada.
 * </p>
 * 
 * @see Initializable
 * @see AnchorPane
 * @see Personalizacion
 * @see PopUp
 */
public class MainController implements Initializable {

    /**
     * Panel principal donde se cargan dinámicamente las diferentes vistas del
     * sistema.
     * 
     * <p>
     * AnchorPane que actúa como contenedor principal para todas las vistas
     * del sistema. Las vistas se anclan a todos los bordes para proporcionar
     * un diseño responsive que se adapta automáticamente al tamaño de la ventana.
     * </p>
     */
    @FXML
    private AnchorPane contenidoPrincipal;

    /**
     * Caché estático para almacenar las vistas (Node) ya cargadas del sistema.
     * 
     * <p>
     * Map que utiliza claves String identificadoras para almacenar los nodos
     * de las vistas previamente cargadas, evitando recargas innecesarias del
     * sistema de archivos y mejorando significativamente el rendimiento.
     * </p>
     * 
     * <p>
     * <strong>Beneficios del caché de vistas:</strong>
     * </p>
     * <ul>
     * <li>Reducción de tiempo de carga en navegación repetida</li>
     * <li>Preservación del estado visual de las vistas</li>
     * <li>Menor consumo de recursos del sistema de archivos</li>
     * <li>Mejor experiencia de usuario con transiciones más rápidas</li>
     * </ul>
     */
    private static Map<String, Node> vistaCache = new HashMap<>();

    /**
     * Caché estático para almacenar los controladores de las vistas cargadas.
     * 
     * <p>
     * Map que preserva las instancias de los controladores de cada vista,
     * permitiendo mantener el estado de los datos y configuraciones entre
     * navegaciones, así como invocar métodos de actualización cuando sea necesario.
     * </p>
     * 
     * <p>
     * <strong>Ventajas del caché de controladores:</strong>
     * </p>
     * <ul>
     * <li>Mantenimiento del estado de datos entre navegaciones</li>
     * <li>Posibilidad de invocar métodos de actualización específicos</li>
     * <li>Preservación de configuraciones de usuario en cada vista</li>
     * <li>Optimización de memoria mediante reutilización de instancias</li>
     * </ul>
     */
    private static Map<String, Object> controladorCache = new HashMap<>();

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Configura la vista inicial del menú principal cargando automáticamente
     * la vista de "Nueva Venta" como pantalla de inicio predeterminada.
     * Esta configuración proporciona acceso inmediato a la funcionalidad
     * más utilizada del sistema.
     * </p>
     * 
     * <p>
     * La vista inicial se carga utilizando el sistema de caché, estableciendo
     * la base para la navegación posterior entre diferentes módulos del sistema.
     * </p>
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Mostrar vista de crear nueva venta al iniciar
        cargarVista("ventas/NuevaVenta.fxml", "nueva-venta", "Nueva Venta");
        switch (Personalizacion.getTrabajadorActual().getPuesto()) {
            case "TRABAJADOR":
                habilitarMenusSupervisor(false);
            case "SUPERVISOR":
                habilitarMenusJefe(false);
            case "JEFE":
                break;
        }
    }

    @FXML
    private Menu menuImportar;

    @FXML
    private Menu menuEstadisticas;

    @FXML
    private MenuItem menuImportarUsuarios;

    @FXML
    private MenuItem menuDatosEmpresa;

    @FXML
    private MenuItem menuGestionUsuarios;

    @FXML
    private MenuItem menuRespaldos;

    @FXML
    private SeparatorMenuItem menuSeparador1;

    private void habilitarMenusSupervisor(boolean valor) {
        menuImportar.setVisible(valor);
        menuEstadisticas.setVisible(valor);
    }

    private void habilitarMenusJefe(boolean valor) {
        // La importacion de nuevos usuarios solo la maneja el jefe
        menuImportarUsuarios.setVisible(valor);
        menuDatosEmpresa.setVisible(valor);
        menuGestionUsuarios.setVisible(valor);
        menuRespaldos.setVisible(valor);
        menuSeparador1.setVisible(valor);
    }

    // ============== MÉTODOS PARA MENÚ VENTAS ==============

    /**
     * Muestra the interfaz para crear una nueva venta en el sistema.
     * 
     * <p>
     * Carga dinámicamente la vista de nueva venta en el panel principal,
     * permitiendo al usuario registrar transacciones de venta con productos,
     * clientes y detalles de facturación.
     * </p>
     */
    @FXML
    private void mostrarNuevaVenta() {
        cargarVista("ventas/NuevaVenta.fxml", "nueva-venta", "Nueva Venta");
    }

    /**
     * Muestra la interfaz para consultar y gestionar las ventas realizadas.
     * 
     * <p>
     * Carga dinámicamente la vista de consulta de ventas, proporcionando
     * herramientas para buscar, filtrar y analizar las transacciones
     * registradas en el sistema.
     * </p>
     */
    @FXML
    private void mostrarConsultarVentas() {
        cargarVista("ventas/ConsultarVentas.fxml", "consultar-ventas", "Consultar Ventas");
    }

    // ============== MÉTODOS PARA MENÚ IMPORTAR ==============

    /**
     * Muestra la interfaz para importar productos desde archivos externos.
     * 
     * <p>
     * Carga dinámicamente la vista de importación de productos, permitiendo
     * la carga masiva de información de productos desde archivos CSV, Excel
     * u otros formatos soportados por el sistema.
     * </p>
     */
    @FXML
    private void mostrarImportarProductos() {
        cargarVista("importar/ImportarProductos.fxml", "importar-productos", "Importar Productos");
    }

    /**
     * Muestra la interfaz para importar usuarios desde archivos externos.
     * 
     * <p>
     * Carga dinámicamente la vista de importación de usuarios, facilitando
     * la administración masiva de cuentas de usuario mediante la carga
     * de archivos con información estructurada.
     * </p>
     */
    @FXML
    private void mostrarImportarUsuarios() {
        cargarVista("importar/ImportarUsuarios.fxml", "importar-usuarios", "Importar Usuarios");
    }

    /**
     * Muestra la interfaz para importar clientes desde archivos externos.
     * 
     * <p>
     * Carga dinámicamente la vista de importación de clientes, permitiendo
     * la gestión eficiente de bases de datos de clientes mediante la
     * importación de información desde fuentes externas.
     * </p>
     */
    @FXML
    private void mostrarImportarClientes() {
        cargarVista("importar/ImportarClientes.fxml", "importar-clientes", "Importar Clientes");
    }

    // ============== MÉTODOS PARA MENÚ ESTADÍSTICAS ==============

    /**
     * Muestra la interfaz de análisis de productos más vendidos del sistema.
     * 
     * <p>
     * Carga dinámicamente la vista de estadísticas de productos, proporcionando
     * análisis detallados sobre tendencias de venta, productos populares y
     * métricas de rendimiento comercial.
     * </p>
     */
    @FXML
    private void mostrarProductosMasVendidos() {
        cargarVista("estadisticas/ProductosMasVendidos.fxml", "productos-mas-vendidos", "Productos Mas Vendidos");
    }

    // ============== MÉTODOS PARA MENÚ CONFIGURACIÓN ==============

    /**
     * Muestra la interfaz para personalizar la apariencia del sistema.
     * 
     * <p>
     * Carga dinámicamente la vista de personalización de interfaz, permitiendo
     * al usuario configurar temas, colores, fuentes y otros aspectos visuales
     * del sistema según sus preferencias personales.
     * </p>
     */
    @FXML
    private void mostrarPersonalizarInterfaz() {
        cargarVista("configuracion/PersonalizarInterfaz.fxml", "personalizar-interfaz", "Personalizar Interfaz");
    }

    /**
     * Muestra la interfaz para gestionar los datos de la empresa.
     * 
     * <p>
     * Carga dinámicamente la vista de configuración empresarial, proporcionando
     * herramientas para actualizar información corporativa, logos, datos de
     * contacto y otra información institucional del sistema.
     * </p>
     */
    @FXML
    private void mostrarDatosEmpresa() {
        cargarVista("configuracion/DatosEmpresa.fxml", "datos-empresa", "Datos de la Empresa");
    }

    /**
     * Muestra la interfaz para la gestión de usuarios del sistema.
     * 
     * <p>
     * Carga dinámicamente la vista de administración de usuarios, permitiendo
     * crear, modificar, eliminar y gestionar permisos de las cuentas de
     * usuario con diferentes niveles de acceso al sistema.
     * </p>
     */
    @FXML
    private void mostrarGestionUsuarios() {
        cargarVista("configuracion/GestionUsuarios.fxml", "gestion-usuarios", "Gestion de Usuarios");
    }

    /**
     * Ejecuta el proceso completo de exportación y respaldo de la base de datos del
     * sistema.
     * 
     * <p>
     * Genera un archivo Excel (.xlsx) completo con todos los datos del sistema
     * organizados
     * en hojas separadas. El usuario puede seleccionar la ubicación donde desea
     * guardar
     * el archivo de respaldo mediante un selector de directorios.
     * </p>
     * 
     * <p>
     * <strong>Funcionalidades del respaldo:</strong>
     * </p>
     * <ul>
     * <li>Exportación completa de todas las tablas principales del sistema</li>
     * <li>Generación de archivo Excel con formato profesional</li>
     * <li>Selector de directorio con ubicación inicial en Documents</li>
     * <li>Nombre de archivo único con timestamp para evitar sobrescrituras</li>
     * <li>Manejo robusto de errores con mensajes informativos al usuario</li>
     * </ul>
     * 
     * <p>
     * <strong>Estructura del archivo generado:</strong>
     * </p>
     * <ul>
     * <li><strong>Hoja "Clientes":</strong> Información completa de clientes
     * registrados</li>
     * <li><strong>Hoja "Trabajadores":</strong> Datos de empleados y usuarios del
     * sistema</li>
     * <li><strong>Hoja "Productos":</strong> Catálogo completo de productos con
     * precios y stock</li>
     * <li><strong>Hoja "Ordenes":</strong> Registro de todas las órdenes de
     * venta</li>
     * <li><strong>Hoja "Sub Ordenes":</strong> Detalles específicos de cada orden
     * (items vendidos)</li>
     * </ul>
     * 
     * <p>
     * <strong>Formato del archivo:</strong>
     * </p>
     * <ul>
     * <li>Nombre: {@code datos_exportados_[timestamp].xlsx}</li>
     * <li>Ubicación: Directorio seleccionado por el usuario</li>
     * <li>Formato: Microsoft Excel (.xlsx) compatible con versiones modernas</li>
     * <li>Codificación: UTF-8 para caracteres especiales</li>
     * </ul>
     * 
     * <p>
     * <strong>Manejo de tipos de datos:</strong>
     * </p>
     * <ul>
     * <li><strong>String:</strong> Texto plano sin modificaciones</li>
     * <li><strong>Integer/Double:</strong> Valores numéricos con formato
     * apropiado</li>
     * <li><strong>LocalDate:</strong> Fechas convertidas a formato Excel con estilo
     * dd/mm/yyyy</li>
     * <li><strong>Boolean:</strong> Valores verdadero/falso</li>
     * <li><strong>Null:</strong> Celdas vacías para campos sin datos</li>
     * </ul>
     * 
     * <p>
     * <strong>Proceso de exportación:</strong>
     * </p>
     * <ol>
     * <li>Mostrar selector de directorio (inicializado en Documents)</li>
     * <li>Crear nuevo libro de trabajo Excel</li>
     * <li>Recuperar datos de cada repositorio de la base de datos</li>
     * <li>Crear hojas separadas para cada tipo de entidad</li>
     * <li>Llenar cada hoja con cabeceras y datos correspondientes</li>
     * <li>Aplicar formato apropiado según el tipo de dato</li>
     * <li>Guardar archivo en la ubicación seleccionada</li>
     * <li>Mostrar confirmación de éxito o mensaje de error</li>
     * </ol>
     * 
     * <p>
     * <strong>Casos de error manejados:</strong>
     * </p>
     * <ul>
     * <li><strong>IOException:</strong> Problemas de escritura o permisos de
     * archivo</li>
     * <li><strong>SQLException:</strong> Errores de conexión o consulta a la base
     * de datos</li>
     * <li><strong>Exception general:</strong> Cualquier otro error inesperado</li>
     * <li><strong>Operación cancelada:</strong> Usuario cancela el selector de
     * directorio</li>
     * </ul>
     */
    @FXML
    private void mostrarRespaldos() {
        File directorioDestino = directyChooser();
        if (directorioDestino == null) {
            return;
        }

        // Definir el nombre del archivo y la ruta completa
        String fileName = "datos_exportados_" + System.currentTimeMillis() + ".xlsx";
        File ubicacionArchivo = new File(directorioDestino, fileName);

        // Crear un nuevo libro de trabajo de Excel
        try (Workbook workbook = new XSSFWorkbook();
                FileOutputStream outputStream = new FileOutputStream(ubicacionArchivo)) {
            // Llena las 5 hojas de datos

            // 1. Hoja de Clientes
            Sheet hojaCliente = workbook.createSheet("Clientes");
            List<Cliente> listaClientes = new ClienteRepository().findAll();
            List<Object[]> dataClientes = new ArrayList<>();
            dataClientes.add(Cliente.cabecera());
            for (Cliente c : listaClientes) {
                dataClientes.add(c.registro());
            }
            llenarDatos(dataClientes, hojaCliente);

            // 2. Hoja de Trabajadores
            Sheet hojaTrabajador = workbook.createSheet("Trabajadores");
            List<Trabajador> listaTrabajadores = new TrabajadorRepository().findAll();
            List<Object[]> dataTrabajadores = new ArrayList<>();
            dataTrabajadores.add(Trabajador.cabecera());
            for (Trabajador t : listaTrabajadores) {
                dataTrabajadores.add(t.registro());
            }
            llenarDatos(dataTrabajadores, hojaTrabajador);

            // 3. Hoja de Productos
            Sheet hojaProducto = workbook.createSheet("Productos");
            List<Producto> listaProductos = new ProductoRepository().findAll();
            List<Object[]> dataProductos = new ArrayList<>();
            dataProductos.add(Producto.cabecera());
            for (Producto p : listaProductos) {
                dataProductos.add(p.registro());
            }
            llenarDatos(dataProductos, hojaProducto);

            // 4. Hoja de Órdenes
            Sheet hojaOrden = workbook.createSheet("Ordenes");
            List<Orden> listaOrdenes = new OrdenRepository().findAll();
            List<Object[]> dataOrdenes = new ArrayList<>();
            dataOrdenes.add(Orden.cabecera());
            for (Orden o : listaOrdenes) {
                dataOrdenes.add(o.registro());
            }
            llenarDatos(dataOrdenes, hojaOrden);

            // 5. Hoja de SubÓrdenes
            Sheet hojaSubOrden = workbook.createSheet("Sub Ordenes");
            List<SubOrden> listaSubOrdenes = new SubOrdenRepository().findAll();
            List<Object[]> dataSubOrdenes = new ArrayList<>();
            dataSubOrdenes.add(SubOrden.cabecera());
            for (SubOrden so : listaSubOrdenes) {
                dataSubOrdenes.add(so.registro());
            }
            llenarDatos(dataSubOrdenes, hojaSubOrden);

            // Escribir el archivo en la ubicacion indicada
            workbook.write(outputStream);
            PopUp.informacion("Archivo exportado con exito", "Datos exportados en " + ubicacionArchivo);
        } catch (IOException e) {
            PopUp.error("Error al exportar archivo", "Intentelo denuevo o contacte a un administrador.");
            e.printStackTrace();
        } catch (SQLException e) {
            PopUp.error("Error de conexion", "Ocurrio un error con la base de datos.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getCause());
        }
    }

    /**
     * Muestra un selector de directorio para que el usuario elija dónde guardar el
     * archivo de respaldo.
     * 
     * <p>
     * Configura y muestra un {@link DirectoryChooser} con configuraciones
     * predeterminadas
     * para facilitar al usuario la selección del directorio de destino para el
     * archivo
     * de exportación. Se inicializa en el directorio "Documents" del usuario por
     * conveniencia.
     * </p>
     * 
     */
    private File directyChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar carpeta para guardar el archivo Excel");

        // Establece el directorio inicial en Documents
        String userHome = System.getProperty("user.home");
        File initialDirectory = new File(userHome, "Documents");
        if (initialDirectory.exists()) {
            directoryChooser.setInitialDirectory(initialDirectory);
        }

        // Mostrar el diálogo y obtener la carpeta seleccionada
        File selectedDirectory = directoryChooser.showDialog(contenidoPrincipal.getScene().getWindow());

        return selectedDirectory;
    }

    /**
     * Llena una hoja de Excel con los datos proporcionados, aplicando formato
     * apropiado según el tipo de dato.
     * 
     * <p>
     * Método utilitario que procesa una lista de arrays de objetos y los convierte
     * en filas y celdas
     * de Excel, aplicando el formato y tipo de celda apropiado según el tipo de
     * dato de cada campo.
     * Maneja automáticamente la conversión de tipos de datos Java a tipos
     * compatibles con Excel.
     * </p>
     * 
     * <p>
     * <strong>Tipos de datos soportados:</strong>
     * </p>
     * <ul>
     * <li><strong>String:</strong> Se inserta como texto plano</li>
     * <li><strong>Integer:</strong> Se inserta como valor numérico entero</li>
     * <li><strong>Double:</strong> Se inserta como valor numérico decimal</li>
     * <li><strong>LocalDate:</strong> Se convierte a Date y se aplica formato
     * dd/mm/yyyy</li>
     * <li><strong>Boolean:</strong> Se inserta como valor booleano</li>
     * <li><strong>null:</strong> Se crea una celda vacía</li>
     * <li><strong>Otros tipos:</strong> Se convierten a String usando
     * toString()</li>
     * </ul>
     * 
     * <p>
     * <strong>Formato especial para fechas:</strong>
     * </p>
     * <p>
     * Las fechas LocalDate se convierten automáticamente al tipo Date de Excel y se
     * les
     * aplica un estilo de celda con formato "dd/mm/yyyy" para una presentación
     * consistente
     * y legible en el archivo Excel resultante.
     * </p>
     * 
     * <p>
     * <strong>Estructura de datos esperada:</strong>
     * </p>
     * <p>
     * Se espera que el primer elemento de la lista contenga las cabeceras de las
     * columnas,
     * y los elementos subsiguientes contengan los datos de cada fila. Cada array de
     * objetos
     * representa una fila, donde cada elemento del array corresponde a una celda.
     * </p>
     * 
     * @param datos Lista de arrays de objetos donde cada array representa una fila
     *              de datos.
     *              El primer elemento debe contener las cabeceras de las columnas.
     * @param hoja  Hoja de Excel donde se insertarán los datos. La hoja debe estar
     *              previamente
     *              creada y asociada a un Workbook válido.
     */
    private void llenarDatos(List<Object[]> datos, Sheet hoja) {
        int rowNum = 0;
        for (Object[] rowData : datos) {
            Row row = hoja.createRow(rowNum++);
            int colNum = 0;
            for (Object field : rowData) {
                Cell cell = row.createCell(colNum++);

                if (field == null) {
                    // Campo nulo: celda vacía
                    cell.setBlank();
                    continue;
                }

                // Manejo de diferentes tipos de datos
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                } else if (field instanceof Double) {
                    cell.setCellValue((Double) field);
                } else if (field instanceof LocalDate) {
                    // Convertir LocalDate a Date para Excel
                    LocalDate localDate = (LocalDate) field;
                    Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    cell.setCellValue(date);

                    // Formato de fecha (opcional)
                    CellStyle dateStyle = hoja.getWorkbook().createCellStyle();
                    CreationHelper createHelper = hoja.getWorkbook().getCreationHelper();
                    dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
                    cell.setCellStyle(dateStyle);
                } else if (field instanceof Boolean) {
                    cell.setCellValue((Boolean) field);
                } else {
                    // Para cualquier otro tipo, usar su representación como String
                    cell.setCellValue(field.toString());
                }
            }
        }
    }

    /**
     * Maneja el proceso completo de cierre de sesión del usuario actual.
     * 
     * <p>
     * Ejecuta un proceso seguro de cierre de sesión que incluye confirmación
     * del usuario, limpieza de caché, eliminación de datos de sesión y
     * retorno a la pantalla de login con toda la personalización aplicada.
     * </p>
     * 
     * <p>
     * <strong>Proceso de cierre de sesión:</strong>
     * </p>
     * <ul>
     * <li>Solicitud de confirmación con advertencia sobre cambios no guardados</li>
     * <li>Limpieza completa del caché de vistas y controladores</li>
     * <li>Eliminación del trabajador actual de la configuración global</li>
     * <li>Cierre de la ventana del menú principal</li>
     * <li>Apertura de la ventana de login con estilos personalizados</li>
     * </ul>
     * 
     * <p>
     * Si el usuario cancela la operación, no se realiza ningún cambio y
     * permanece en la sesión actual. En caso de error al cargar la ventana
     * de login, se muestra un mensaje de error crítico.
     * </p>
     */
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
     * Carga una vista de forma dinámica utilizando el sistema de caché optimizado.
     * 
     * <p>
     * Método principal para la gestión de vistas que implementa un sistema
     * de caché inteligente para optimizar el rendimiento. Si la vista ya
     * fue cargada anteriormente, la recupera del caché; de lo contrario,
     * la carga desde el archivo FXML y la almacena para futuras referencias.
     * </p>
     * 
     * <p>
     * <strong>Proceso de carga de vista:</strong>
     * </p>
     * <ul>
     * <li>Verificación de existencia en caché</li>
     * <li>Carga desde FXML si no existe en caché</li>
     * <li>Almacenamiento de vista y controlador en caché</li>
     * <li>Limpieza del contenedor principal</li>
     * <li>Anclaje responsive de la nueva vista</li>
     * <li>Invocación de métodos de actualización del controlador</li>
     * </ul>
     * 
     * <p>
     * El método configura automáticamente el anclaje de la vista a todos
     * los bordes del contenedor principal, garantizando un diseño responsive
     * que se adapta a diferentes tamaños de ventana.
     * </p>
     * 
     * @param fxmlFile Ruta relativa del archivo FXML a cargar desde el directorio
     *                 de vistas
     * @param cacheKey Clave única para identificar la vista en el sistema de caché
     * @param titulo   Título descriptivo de la vista para logging y debugging
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
     * Actualiza la vista seleccionada invocando métodos de actualización estándar.
     * 
     * <p>
     * Utiliza reflexión Java para intentar invocar métodos comunes de actualización
     * en los controladores de vista. Esto permite refrescar datos automáticamente
     * cuando se navega a una vista previamente cargada desde el caché.
     * </p>
     * 
     * <p>
     * <strong>Métodos de actualización intentados:</strong>
     * </p>
     * <ul>
     * <li>{@code actualizarVista()}: Actualización general de la interfaz</li>
     * <li>{@code refrescarDatos()}: Recarga de datos desde la base de datos</li>
     * </ul>
     * 
     * <p>
     * Si el controlador no implementa estos métodos, las excepciones se ignoran
     * silenciosamente, permitiendo que el sistema funcione correctamente con
     * controladores que no requieren actualización automática.
     * </p>
     * 
     * @param controlador Instancia del controlador de la vista actual, puede ser
     *                    null
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
     * Muestra la ventana de login con la configuración personalizada aplicada.
     * 
     * <p>
     * Crea y configura una nueva ventana de login aplicando automáticamente
     * las preferencias de personalización del usuario, incluyendo fuentes,
     * colores, logo corporativo y dimensiones específicas.
     * </p>
     * 
     * <p>
     * <strong>Configuraciones aplicadas:</strong>
     * </p>
     * <ul>
     * <li>Dimensiones fijas: 400x560 píxeles</li>
     * <li>Ventana no redimensionable para mantener diseño</li>
     * <li>Estilos personalizados: fuente, tamaño y color de fondo</li>
     * <li>Logo corporativo como ícono de ventana</li>
     * <li>Título con nombre de la empresa actual</li>
     * </ul>
     * 
     * @throws IOException Si ocurre un error al cargar el archivo FXML del login
     */
    private void mostrarVentanaLogin() throws IOException {

        // Crea la ventana del loguin
        Stage stage = new Stage();
        Scene scene = new Scene(loadFXML("Login"), 400, 560);

        // Usa los estilos base del programa
        scene.getRoot().setStyle(Personalizacion.getTipoLetra()
                + Personalizacion.getTamanoLetra()
                + Personalizacion.getColorFondo());

        // Establece el stage actual
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(Personalizacion.getLogo());
        stage.setTitle(Personalizacion.getEmpresaActual().getNombreEmpresa());
        stage.show();
    }

    /**
     * Carga un archivo FXML desde el directorio de vistas de la aplicación.
     * 
     * <p>
     * Método utilitario estático para cargar archivos FXML utilizando el
     * FXMLLoader con la ruta base del proyecto. Facilita la carga consistente
     * de diferentes vistas de la aplicación.
     * </p>
     * 
     * @param fxml Nombre del archivo FXML (sin extensión) a cargar
     * @return Parent root del archivo FXML cargado y listo para usar
     * @throws IOException Si el archivo FXML no existe o contiene errores de
     *                     estructura
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/ucv/codelab/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Obtiene el controlador de una vista específica del sistema de caché.
     * 
     * <p>
     * Permite acceder a las instancias de controladores previamente cargados
     * y almacenados en caché, facilitando la comunicación entre diferentes
     * vistas y la actualización de datos entre módulos.
     * </p>
     * 
     * <p>
     * Este método es útil para escenarios donde se necesita acceder a
     * funcionalidades específicas de un controlador desde otra parte
     * del sistema sin necesidad de recargar la vista completa.
     * </p>
     * 
     * @param cacheKey Clave identificadora de la vista en el sistema de caché
     * @return Instancia del controlador de la vista especificada,
     *         o {@code null} si la vista no está en caché o no tiene controlador
     */
    public Object obtenerControladorVista(String cacheKey) {
        return controladorCache.get(cacheKey);
    }

    /**
     * Limpia completamente el caché de vistas y controladores del sistema.
     * 
     * <p>
     * Elimina todas las vistas y controladores almacenados en caché,
     * forzando que las próximas navegaciones recarguen las vistas desde
     * los archivos FXML. Útil para refrescar datos globalmente o liberar
     * memoria cuando sea necesario.
     * </p>
     * 
     * <p>
     * <strong>Casos de uso recomendados:</strong>
     * </p>
     * <ul>
     * <li>Después de cambios importantes en configuración</li>
     * <li>Al detectar inconsistencias en datos</li>
     * <li>Para liberar memoria en sistemas con recursos limitados</li>
     * <li>Durante procesos de mantenimiento del sistema</li>
     * </ul>
     */
    public static void limpiarCache() {
        vistaCache.clear();
        controladorCache.clear();
        System.out.println("Cache de vistas limpiado");
    }

    /**
     * Limpia el caché de una vista específica del sistema.
     * 
     * <p>
     * Elimina únicamente la vista y controlador especificados del caché,
     * permitiendo un control granular sobre qué vistas necesitan ser
     * recargadas sin afectar el rendimiento de otras vistas cacheadas.
     * </p>
     * 
     * <p>
     * Este método es preferible a {@link #limpiarCache()} cuando solo
     * una vista específica necesita ser actualizada, manteniendo el
     * beneficio de rendimiento del caché para el resto del sistema.
     * </p>
     * 
     * @param cacheKey Clave identificadora de la vista específica a limpiar del
     *                 caché
     */
    public static void limpiarCacheVista(String cacheKey) {
        vistaCache.remove(cacheKey);
        controladorCache.remove(cacheKey);
        System.out.println("Cache de vista " + cacheKey + " limpiado");
    }
}