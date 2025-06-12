package ucv.codelab.controller.ventas;

import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import ucv.codelab.model.Cliente;
import ucv.codelab.model.Orden;
import ucv.codelab.model.Producto;
import ucv.codelab.model.SubOrden;
import ucv.codelab.repository.ClienteRepository;
import ucv.codelab.repository.OrdenRepository;
import ucv.codelab.repository.ProductoRepository;
import ucv.codelab.repository.SubOrdenRepository;
import ucv.codelab.util.Personalizacion;
import ucv.codelab.util.PopUp;

/**
 * Controlador para la gestión del proceso de creación de nuevas ventas.
 * 
 * <p>
 * Esta clase implementa {@link Initializable} y maneja la interfaz completa
 * para el registro de nuevas transacciones de venta, proporcionando
 * funcionalidades
 * de búsqueda de clientes, selección de productos, cálculo automático de
 * totales
 * y confirmación de compras en el sistema.
 * </p>
 * 
 * <p>
 * <strong>Funcionalidades principales:</strong>
 * </p>
 * <ul>
 * <li>Búsqueda y creación automática de clientes por DNI</li>
 * <li>Búsqueda de productos por ID o nombre con filtros dinámicos</li>
 * <li>Gestión de carrito de compras con cantidades variables</li>
 * <li>Cálculo automático de precios unitarios y totales</li>
 * <li>Validación de stock y cantidades disponibles</li>
 * <li>Confirmación y persistencia de órdenes de venta completas</li>
 * <li>Interfaz intuitiva con navegación mediante tecla Enter</li>
 * </ul>
 * 
 * <p>
 * <strong>Flujo de trabajo del proceso de venta:</strong>
 * </p>
 * <ul>
 * <li>Búsqueda o creación del cliente mediante DNI</li>
 * <li>Búsqueda y selección de productos disponibles</li>
 * <li>Configuración de cantidades para cada producto</li>
 * <li>Visualización del carrito con cálculos automáticos</li>
 * <li>Confirmación y procesamiento de la venta completa</li>
 * <li>Generación de orden y subórdenes en la base de datos</li>
 * </ul>
 * 
 * <p>
 * El controlador maneja automáticamente la creación de clientes nuevos cuando
 * no se encuentran en la base de datos, utilizando DNI por defecto "00000000"
 * para ventas sin cliente específico, y mantiene la integridad referencial
 * entre órdenes, subórdenes y productos seleccionados.
 * </p>
 * 
 * @see Initializable
 * @see Cliente
 * @see Producto
 * @see Orden
 * @see SubOrden
 * @see Personalizacion
 */
public class NuevaVentaController implements Initializable {

    /**
     * Campo de texto para el ingreso del DNI del cliente.
     * 
     * <p>
     * Acepta el documento de identidad del cliente que realizará la compra.
     * Si se deja vacío, utiliza automáticamente "00000000" como DNI por defecto
     * para ventas genéricas. Incluye funcionalidad de búsqueda rápida mediante
     * la tecla Enter.
     * </p>
     */
    @FXML
    private TextField dniCliente;

    /**
     * Campo de texto que muestra el nombre completo del cliente seleccionado.
     * 
     * <p>
     * Se actualiza automáticamente cuando se encuentra un cliente existente
     * en la base de datos o se crea uno nuevo. Campo de solo lectura que
     * refleja la información del cliente asociado a la venta actual.
     * </p>
     */
    @FXML
    private TextField nombreCliente;

    /**
     * Campo de texto que muestra el teléfono del cliente seleccionado.
     * 
     * <p>
     * Información opcional del cliente que se actualiza automáticamente
     * al cargar los datos desde la base de datos. Puede estar vacío si
     * el cliente no proporcionó esta información durante su registro.
     * </p>
     */
    @FXML
    private TextField telefono;

    /**
     * Campo de texto que muestra el email del cliente seleccionado.
     * 
     * <p>
     * Información opcional del cliente que se carga automáticamente desde
     * la base de datos. Campo informativo que puede estar vacío si no se
     * registró email durante la creación del cliente.
     * </p>
     */
    @FXML
    private TextField email;

    /**
     * Campo de texto para filtrar productos por su identificador numérico
     * específico.
     * 
     * <p>
     * Permite búsqueda exacta de productos utilizando su ID único. Incluye
     * validación automática de formato numérico y búsqueda rápida mediante
     * la tecla Enter. Se utiliza en conjunto con {@link #nombreProducto}
     * para filtros combinados.
     * </p>
     */
    @FXML
    private TextField idProducto;

    /**
     * Campo de texto para filtrar productos por nombre parcial o completo.
     * 
     * <p>
     * Permite búsquedas flexibles utilizando coincidencias de substring
     * en el nombre del producto. Facilita la localización de productos
     * cuando no se conoce el ID exacto. Incluye búsqueda rápida con Enter.
     * </p>
     */
    @FXML
    private TextField nombreProducto;

    /**
     * Control spinner para especificar la cantidad del producto seleccionado.
     * 
     * <p>
     * Permite seleccionar cantidades entre 0 y 99 unidades del producto
     * a añadir al carrito de compras. Una cantidad de 0 elimina el producto
     * del carrito, mientras que cantidades mayores añaden o actualizan
     * la cantidad existente.
     * </p>
     */
    @FXML
    private Spinner<Integer> cantidad;

    /**
     * Botón para ejecutar la búsqueda de productos con los filtros aplicados.
     * 
     * <p>
     * Se habilita automáticamente cuando se selecciona un cliente válido.
     * Ejecuta búsquedas basadas en los valores de {@link #idProducto} y
     * {@link #nombreProducto}, mostrando resultados en la lista de productos.
     * </p>
     */
    @FXML
    private Button searchProduct;

    /**
     * Botón para añadir el producto seleccionado al carrito de compras.
     * 
     * <p>
     * Se habilita cuando hay productos disponibles en la lista de resultados.
     * Procesa la adición, modificación o eliminación de productos del carrito
     * según la cantidad especificada en el spinner.
     * </p>
     */
    @FXML
    private Button addProduct;

    /**
     * Botón para confirmar y procesar la compra completa.
     * 
     * <p>
     * Se habilita únicamente cuando hay productos en el carrito de compras.
     * Ejecuta el proceso completo de confirmación, creación de orden y
     * subórdenes, y persistencia en la base de datos.
     * </p>
     */
    @FXML
    private Button comprar;

    /**
     * Lista visual que muestra los productos disponibles según los filtros
     * aplicados.
     * 
     * <p>
     * Presenta los productos encontrados en formato personalizado mostrando
     * ID, nombre y precio. Permite selección individual para añadir al carrito.
     * Se actualiza dinámicamente con cada búsqueda realizada.
     * </p>
     */
    @FXML
    private ListView<Producto> productos;

    /**
     * Tabla principal que muestra el carrito de compras actual.
     * 
     * <p>
     * Presenta los productos seleccionados con sus cantidades, precios
     * unitarios y totales calculados. Se actualiza automáticamente cada vez
     * que se añaden, modifican o eliminan productos del carrito.
     * </p>
     */
    @FXML
    private TableView<SubOrden> resultado;

    /**
     * Label que muestra el precio total de todos los productos en el carrito.
     * 
     * <p>
     * Se actualiza automáticamente cada vez que se modifica el carrito,
     * mostrando la suma total de todos los productos con sus respectivas
     * cantidades. Formato de moneda peruana (S/).
     * </p>
     */
    @FXML
    private Label precio;

    /**
     * Columna de la tabla que muestra el identificador único de cada producto en el
     * carrito.
     * 
     * <p>
     * Presenta el ID numérico del producto, permitiendo identificación precisa
     * de cada item en el carrito. Utiliza la propiedad "idProducto" del modelo
     * {@link SubOrden} para la vinculación de datos.
     * </p>
     */
    @FXML
    private TableColumn<SubOrden, String> columnaId;

    /**
     * Columna de la tabla que muestra el nombre completo del producto.
     * 
     * <p>
     * Presenta el nombre descriptivo del producto obtenido mediante búsqueda
     * en la lista de productos seleccionados. Utiliza lógica personalizada
     * para mapear el ID del producto con su información completa.
     * </p>
     */
    @FXML
    private TableColumn<SubOrden, String> columnaNombre;

    /**
     * Columna de la tabla que muestra el precio unitario de cada producto.
     * 
     * <p>
     * Presenta el precio individual del producto formateado con dos decimales
     * en moneda peruana. Se obtiene mediante búsqueda en la cache de productos
     * seleccionados utilizando el ID como referencia.
     * </p>
     */
    @FXML
    private TableColumn<SubOrden, String> columnaUnitario;

    /**
     * Columna de la tabla que muestra la cantidad seleccionada de cada producto.
     * 
     * <p>
     * Presenta la cantidad de unidades del producto que se incluirán en la venta.
     * Utiliza la propiedad "cantidad" del modelo {@link SubOrden} para mostrar
     * el valor numérico directamente.
     * </p>
     */
    @FXML
    private TableColumn<SubOrden, Integer> columnaCantidad;

    /**
     * Columna de la tabla que muestra el precio total por producto (cantidad ×
     * precio unitario).
     * 
     * <p>
     * Calcula y presenta automáticamente el subtotal de cada producto
     * multiplicando la cantidad por el precio unitario. Formateado con
     * dos decimales y actualizado dinámicamente con cada cambio.
     * </p>
     */
    @FXML
    private TableColumn<SubOrden, String> columnaTotal;

    /**
     * Instancia del cliente actualmente seleccionado para la venta.
     * 
     * <p>
     * Mantiene la referencia al objeto {@link Cliente} que realizará la compra.
     * Se inicializa mediante búsqueda por DNI o creación de cliente nuevo.
     * Utilizado para asociar la orden de venta con el cliente correspondiente.
     * </p>
     */
    private Cliente clienteSeleccionado;

    /**
     * Lista de subórdenes que representan los productos en el carrito de compras.
     * 
     * <p>
     * Contiene las instancias de {@link SubOrden} con los productos seleccionados
     * y sus cantidades. Esta lista se utiliza para generar la orden completa
     * y se persiste en la base de datos al confirmar la compra.
     * </p>
     */
    private List<SubOrden> listaSubordenes = new ArrayList<>();

    /**
     * Cache de productos seleccionados para optimizar consultas de información.
     * 
     * <p>
     * Mantiene una copia local de los objetos {@link Producto} seleccionados
     * para evitar consultas repetitivas a la base de datos al mostrar nombres
     * y precios en la tabla del carrito. Se sincroniza con
     * {@link #listaSubordenes}.
     * </p>
     */
    private List<Producto> productosSeleccionados = new ArrayList<>();

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Configura la interfaz inicial del controlador de nuevas ventas,
     * estableciendo los eventos de búsqueda rápida mediante Enter, configurando
     * el rango de valores del spinner de cantidades, personalizando la
     * visualización de la lista de productos y preparando las columnas de la tabla.
     * </p>
     * 
     * <p>
     * <strong>Configuraciones realizadas:</strong>
     * </p>
     * <ul>
     * <li>Eventos KeyPressed para búsqueda con Enter en campos de cliente y
     * producto</li>
     * <li>Configuración del spinner con rango de 0 a 99 unidades</li>
     * <li>Personalización de la visualización de productos en ListView</li>
     * <li>Configuración de columnas de tabla con PropertyValueFactory</li>
     * <li>Restablecimiento inicial de la interfaz a estado deshabilitado</li>
     * </ul>
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Al presionar Enter en el DNI intenta buscar el cliente
        dniCliente.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                buscarCliente();
            }
        });

        // Al presionar Enter en el ID del producto intenta buscarlo
        idProducto.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                buscarProducto();
            }
        });

        // Al presionar Enter en el Nombre del producto intenta buscarlo
        nombreProducto.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                buscarProducto();
            }
        });

        // Configura el rango de valores del spinner
        SpinnerValueFactory.IntegerSpinnerValueFactory factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,
                99, 0);
        cantidad.setValueFactory(factory);

        // Configura la lista de productos
        productos.setCellFactory(param -> new ListCell<Producto>() {
            @Override
            protected void updateItem(Producto producto, boolean empty) {
                super.updateItem(producto, empty);
                if (empty || producto == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(producto.getIdProducto() + ": " + producto.getNombreProducto() + " - S/ "
                            + producto.getPrecio());
                }
            }
        });

        // Configura las columnas del resultado
        configurarColumnas();

        // Deshabilita los recuadros y coloca los valores iniciales
        restablecerTodo();
    }

    /**
     * Busca un cliente en la base de datos utilizando el DNI especificado.
     * 
     * <p>
     * Ejecuta la búsqueda de cliente por DNI, utilizando "00000000" como valor
     * por defecto si no se especifica ninguno. Si el cliente no existe,
     * inicia el proceso de creación de cliente nuevo. Una vez localizado o
     * creado el cliente, actualiza los campos de información y habilita
     * los controles de búsqueda de productos.
     * </p>
     * 
     * <p>
     * <strong>Proceso de búsqueda:</strong>
     * </p>
     * <ul>
     * <li>Asignación de DNI por defecto si el campo está vacío</li>
     * <li>Búsqueda en la base de datos por DNI especificado</li>
     * <li>Creación de cliente nuevo si no se encuentra registro</li>
     * <li>Actualización de campos informativos del cliente</li>
     * <li>Habilitación de controles de búsqueda de productos</li>
     * </ul>
     * 
     * <p>
     * En caso de error de conexión con la base de datos, muestra mensaje
     * de error y detiene el proceso sin realizar cambios en la interfaz.
     * </p>
     */
    @FXML
    private void buscarCliente() {
        try {
            ClienteRepository clienteRepository = new ClienteRepository();

            // Si no hay valores ingresados usa el DNI del usuario por defecto
            if (dniCliente.getText() == null || dniCliente.getText().trim().equals("")) {
                dniCliente.setText("00000000");
            }

            Optional<Cliente> optionalCliente = clienteRepository.findByDni(dniCliente.getText());

            clienteSeleccionado = cargarCliente(optionalCliente, clienteRepository);

            if (clienteSeleccionado == null) {
                return;
            }

            // Actualiza los datos mostrados
            nombreCliente.setText(clienteSeleccionado.getNombreCliente());
            telefono.setText(clienteSeleccionado.getTelefono());
            email.setText(clienteSeleccionado.getEmailCliente());

            // Habilita los botones de busqueda nuevamente
            deshabilitarBusqueda(false);
        } catch (SQLException e) {
            PopUp.error("Error de conexion", "Ocurrio un error con la base de datos.");
            return;
        }
    }

    /**
     * Busca productos en la base de datos utilizando filtros por ID o nombre.
     * 
     * <p>
     * Ejecuta búsquedas flexibles de productos utilizando el ID específico
     * si está disponible, el nombre parcial como segunda opción, o devuelve
     * todos los productos vigentes si no se especifican filtros. Los resultados
     * se muestran en la lista de productos y se habilita el botón de añadir.
     * </p>
     * 
     * <p>
     * <strong>Jerarquía de búsqueda:</strong>
     * </p>
     * <ul>
     * <li>Búsqueda exacta por ID si el campo contiene un número válido</li>
     * <li>Búsqueda por nombre parcial si el campo nombre no está vacío</li>
     * <li>Listado completo de productos vigentes si no hay filtros</li>
     * <li>Lista vacía si el ID no es un número válido</li>
     * </ul>
     * 
     * <p>
     * El método incluye validación de formato numérico para el ID y manejo
     * de excepciones, mostrando listas vacías en caso de errores de parseo.
     * Todos los productos mostrados están marcados como vigentes en el sistema.
     * </p>
     */
    @FXML
    private void buscarProducto() {
        // Si alguno de los cuadrantes estan vacios
        if (idProducto.getText() == null) {
            idProducto.setText("");
        }
        if (nombreProducto.getText() == null) {
            idProducto.setText("");
        }

        try {
            ProductoRepository repository = new ProductoRepository();

            // Carga los datos a mostrar en la lista producto
            ObservableList<Producto> coincidencias;
            // Si el ID del producto es diferente a un valor vacío intenta buscarlo por ID
            if (!idProducto.getText().equals("")) {
                // En caso de errores al parsear el ID no hace cambios
                Optional<Producto> p = repository.findByIdVigentes(Integer.parseInt(idProducto.getText()));

                // Si no se encontraron resultados usa una lista vacia
                if (p.isEmpty()) {
                    coincidencias = FXCollections.observableArrayList();
                } else {
                    // De lo contrario actualiza la lista
                    coincidencias = FXCollections.observableArrayList(p.get());
                }
            }
            // Si el nombre del producto es diferente a un valor vacio
            else if (!nombreProducto.getText().equals("")) {
                coincidencias = FXCollections
                        .observableArrayList(repository.findByNombreVigentes(nombreProducto.getText()));
            }
            // Si no se aplicaron filtros devuelve todo
            else {
                coincidencias = FXCollections.observableArrayList(repository.findVigentes());
            }
            // Actualiza las coincidencias
            productos.setItems(coincidencias);
            // Habilita el boton de añadir
            deshabilitarAnadir(false);
        } catch (SQLException e) {
            PopUp.error("Error de conexion", "Ocurrio un error con la base de datos.");
            return;
        } catch (NumberFormatException e) {
            // Si hay un error al parsear el ID retorna y borra los resultados
            ObservableList<Producto> coincidencias = FXCollections.observableArrayList();
            productos.setItems(coincidencias);
            return;
        }
    }

    /**
     * Añade, modifica o elimina el producto seleccionado del carrito de compras.
     * 
     * <p>
     * Procesa la selección del producto actual y la cantidad especificada,
     * realizando las operaciones correspondientes en el carrito según el
     * estado actual del producto y la cantidad ingresada. Actualiza
     * automáticamente la tabla de resultados y el precio total.
     * </p>
     * 
     * <p>
     * <strong>Lógica de procesamiento:</strong>
     * </p>
     * <ul>
     * <li>Validación de selección de producto en la lista</li>
     * <li>Verificación de existencia previa del producto en el carrito</li>
     * <li>Procesamiento de cambios según cantidad y estado actual</li>
     * <li>Actualización de la tabla y recálculo del precio total</li>
     * <li>Habilitación/deshabilitación del botón de compra</li>
     * </ul>
     * 
     * <p>
     * El método maneja automáticamente la suma de subtotales, formato de
     * moneda y sincronización entre el carrito y la cache de productos.
     * </p>
     */
    @FXML
    private void seleccionarProducto() {
        if (productos.getSelectionModel().getSelectedItem() == null) {
            PopUp.informacion("Selecciona un producto", "Debes seleccionar un producto primero.");
            return;
        }

        // Producto seleccionado
        Producto p = productos.getSelectionModel().getSelectedItem();

        // Busca el item en la lista de compras
        int indexCarrito = enCarrito(p.getIdProducto());

        // Verifica si se realizan cambios
        if (realizarCambios(p, indexCarrito)) {
            // Actualiza la vista
            ObservableList<SubOrden> datosObservables = FXCollections.observableArrayList(listaSubordenes);
            resultado.setItems(datosObservables);

            // Obtiene los montos totales mostrados de cada SubOrden
            List<String> valores = resultado.getItems().stream().map(item -> columnaTotal.getCellData(item))
                    .collect(Collectors.toList());

            double total = 0;
            for (String s : valores) {
                total += Double.parseDouble(s.replaceAll(",", "."));
            }

            precio.setText("S/ " + new DecimalFormat("#0.00").format(total));

            if (listaSubordenes.size() > 0) {
                deshabilitarComprar(false);
            } else {
                deshabilitarComprar(true);
            }
        }
    }

    /**
     * Procesa la confirmación y finalización de la compra completa.
     * 
     * <p>
     * Ejecuta el proceso completo de confirmación de compra, incluyendo
     * validación del usuario, creación de la orden principal, generación
     * de todas las subórdenes asociadas y persistencia en la base de datos.
     * Una vez completado, restablece la interfaz para una nueva venta.
     * </p>
     * 
     * <p>
     * <strong>Proceso de confirmación:</strong>
     * </p>
     * <ul>
     * <li>Solicitud de confirmación del usuario mediante diálogo</li>
     * <li>Creación de orden principal con datos del trabajador y cliente</li>
     * <li>Generación y persistencia de todas las subórdenes del carrito</li>
     * <li>Asociación automática de subórdenes con la orden creada</li>
     * <li>Notificación de éxito y restablecimiento de la interfaz</li>
     * </ul>
     * 
     * <p>
     * La orden se crea automáticamente con la fecha actual, el trabajador
     * logueado, el cliente seleccionado y la empresa actual del sistema.
     * En caso de error de base de datos, se muestra mensaje de error sin
     * realizar cambios.
     * </p>
     */
    @FXML
    private void confirmarCompra() {
        Optional<ButtonType> confirmacion = PopUp.confirmacion("Confirmar compra",
                "¿Desea continuar con la compra?",
                "Una vez enviado no se puede modificar, verifique los productos comprados");
        if (confirmacion.isPresent() && confirmacion.get() == ButtonType.OK) {
            try {
                // Inicia los repositorios
                OrdenRepository ordenRepository = new OrdenRepository();
                SubOrdenRepository subOrdenRepository = new SubOrdenRepository();

                // Guarda la orden actual
                Orden ordenActual = new Orden(-1,
                        Personalizacion.getTrabajadorActual().getIdTrabajador(),
                        clienteSeleccionado.getIdCliente(),
                        Personalizacion.getEmpresaActual().getIdEmpresa(),
                        LocalDate.now());
                ordenRepository.save(ordenActual);

                // Guarda todas las SubOrdenes
                for (SubOrden subOrden : listaSubordenes) {
                    subOrden.setIdOrden(ordenActual.getIdOrden());
                    subOrdenRepository.save(subOrden);
                }

                PopUp.informacion("Gracias por su compra", "Registro de venta realizado");

                // Restablece todo al estado inicial
                restablecerTodo();
            } catch (SQLException e) {
                PopUp.error("Error de conexion", "Ocurrio un error con la base de datos.");
            }
        }
    }

    /**
     * Configura las columnas de la tabla de resultados con sus respectivos formatos
     * y vinculaciones.
     * 
     * <p>
     * Establece las configuraciones de las columnas de la tabla del carrito de
     * compras,
     * incluyendo el formato de valores monetarios, vinculación de propiedades del
     * modelo
     * y cálculos automáticos para precios totales. Utiliza PropertyValueFactory
     * para
     * columnas directas y SimpleStringProperty para cálculos personalizados.
     * </p>
     * 
     * <p>
     * <strong>Configuraciones por columna:</strong>
     * </p>
     * <ul>
     * <li>Columna ID: Vinculación directa con la propiedad idProducto</li>
     * <li>Columna Nombre: Búsqueda en cache de productos por ID</li>
     * <li>Columna Precio Unitario: Búsqueda en cache con formato decimal</li>
     * <li>Columna Cantidad: Vinculación directa con la propiedad cantidad</li>
     * <li>Columna Total: Cálculo automático (precio × cantidad)</li>
     * </ul>
     * 
     * <p>
     * Todos los valores monetarios se formatean con dos decimales utilizando
     * DecimalFormat. Las búsquedas en cache manejan casos donde no se encuentra
     * el producto, mostrando valores por defecto apropiados.
     * </p>
     */
    private void configurarColumnas() {
        // Crear el formateador decimal para 2 decimales
        DecimalFormat df = new DecimalFormat("#0.00");

        // Columna ID
        columnaId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));

        // Columna Nombre
        columnaNombre.setCellValueFactory(cellData -> {
            int idProducto = cellData.getValue().getIdProducto();
            for (Producto producto : productosSeleccionados) {
                if (producto.getIdProducto() == idProducto) {
                    return new SimpleStringProperty(producto.getNombreProducto());
                }
            }
            return new SimpleStringProperty("No encontrado");
        });

        // Columna Precio Unitario
        columnaUnitario.setCellValueFactory(cellData -> {
            int idProducto = cellData.getValue().getIdProducto();
            for (Producto producto : productosSeleccionados) {
                if (producto.getIdProducto() == idProducto) {
                    String precioFormateado = df.format(producto.getPrecio());
                    return new SimpleStringProperty(precioFormateado);
                }
            }
            return new SimpleStringProperty(df.format(0.0));
        });

        // Columna Cantidad
        columnaCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        // Columna Total (precio unitario * cantidad)
        columnaTotal.setCellValueFactory(cellData -> {
            int idProducto = cellData.getValue().getIdProducto();
            int cantidad = cellData.getValue().getCantidad();

            for (Producto producto : productosSeleccionados) {
                if (producto.getIdProducto() == idProducto) {
                    double total = producto.getPrecio() * cantidad;
                    String totalFormateado = df.format(total);
                    return new SimpleStringProperty(totalFormateado);
                }
            }
            return new SimpleStringProperty(df.format(0.0));
        });
    }

    /**
     * Carga un cliente existente desde la base de datos o inicia el proceso de
     * creación de uno nuevo.
     * 
     * <p>
     * Procesa el resultado de la búsqueda de cliente, devolviendo el cliente
     * existente
     * si se encuentra en la base de datos, o iniciando el flujo de creación de
     * cliente
     * nuevo mediante diálogos de entrada. Incluye validación de datos opcionales y
     * persistencia automática del cliente creado.
     * </p>
     * 
     * <p>
     * <strong>Flujo de creación de cliente nuevo:</strong>
     * </p>
     * <ul>
     * <li>Solicitud del nombre del cliente (obligatorio)</li>
     * <li>Solicitud del teléfono (opcional)</li>
     * <li>Solicitud del email (opcional)</li>
     * <li>Creación y persistencia del cliente en la base de datos</li>
     * <li>Retorno del cliente creado para uso inmediato</li>
     * </ul>
     * 
     * <p>
     * Los campos opcionales se establecen como null si se ingresan valores vacíos.
     * Si el usuario cancela la creación del cliente, retorna null y muestra mensaje
     * informativo.
     * </p>
     * 
     * @param optionalCliente El Optional que contiene el cliente si fue encontrado
     *                        en la base de datos
     * @param repository      El repositorio de clientes para realizar operaciones
     *                        de persistencia
     * @return El cliente cargado desde la base de datos, el cliente recién creado,
     *         o null si se cancela la operación
     */
    private Cliente cargarCliente(Optional<Cliente> optionalCliente, ClienteRepository repository) {
        // Si se tiene el cliente en la base de datos lo usa
        if (optionalCliente.isPresent()) {
            return optionalCliente.get();
        }
        // Si no lo tiene crea el cliente
        else {
            String nombre = PopUp.inputDialog("Cliente Nuevo", "Ingrese el nombre: ");
            if (!nombre.equals("")) {
                // Obtiene los datos opcionales
                String telefono = PopUp.inputDialog("Cliente Nuevo", "Ingrese el teléfono (opcional): ");
                String email = PopUp.inputDialog("Cliente Nuevo", "Ingrese el email (opcional): ");
                if (telefono.trim().equals("")) {
                    telefono = null;
                }
                if (email.trim().equals("")) {
                    email = null;
                }
                // Crea el cliente nuevo y lo guarda en la base de datos
                Cliente clienteNuevo = new Cliente(-1, nombre, dniCliente.getText(), telefono, email);
                repository.save(clienteNuevo);

                return clienteNuevo;
            }
        }
        // Si el usuario cancela la accion usa el cliente por defecto
        PopUp.informacion("Cliente cancelado", "Se canceló la creación del cliente, vuelve a intentarlo");
        return null;
    }

    /**
     * Verifica si un producto específico ya está presente en el carrito de compras.
     * 
     * <p>
     * Recorre la lista de subórdenes actual para determinar si el producto
     * identificado por el ID proporcionado ya está incluido en el carrito.
     * Utiliza comparación directa de IDs para localizar coincidencias.
     * </p>
     * 
     * <p>
     * Este método es fundamental para la lógica de adición y modificación
     * de productos en el carrito, permitiendo distinguir entre operaciones
     * de adición de nuevos productos y actualización de cantidades existentes.
     * </p>
     * 
     * @param idProducto El identificador único del producto a buscar en el carrito
     * @return El índice del producto en la lista de subórdenes si está presente, -1
     *         si no está en el carrito
     */
    private int enCarrito(int idProducto) {
        for (int i = 0; i < listaSubordenes.size(); i++) {
            // Si el ID del producto está en la lista de subOrdenes
            if (listaSubordenes.get(i).getIdProducto() == idProducto) {
                // Regresa el indice donde está el producto
                return i;
            }
        }
        // Si no está en el carrito retorna -1
        return -1;
    }

    /**
     * Ejecuta los cambios necesarios en el carrito según el producto seleccionado y
     * la cantidad especificada.
     * 
     * <p>
     * Procesa las operaciones de adición, modificación o eliminación de productos
     * en el carrito de compras basándose en la cantidad especificada y el estado
     * actual del producto en el carrito. Mantiene sincronización entre la lista
     * de subórdenes y la cache de productos seleccionados.
     * </p>
     * 
     * <p>
     * <strong>Lógica de operaciones:</strong>
     * </p>
     * <ul>
     * <li>Cantidad = 0 + Producto en carrito: Eliminación del producto</li>
     * <li>Cantidad > 0 + Producto en carrito: Actualización de cantidad</li>
     * <li>Cantidad > 0 + Producto no en carrito: Adición de nuevo producto</li>
     * <li>Cantidad = 0 + Producto no en carrito: Muestra mensaje de error</li>
     * </ul>
     * 
     * <p>
     * Las operaciones de eliminación remueven tanto la suborden como el producto
     * de la cache. Las operaciones de adición crean nuevas instancias de SubOrden
     * y añaden el producto a la cache para consultas posteriores.
     * </p>
     * 
     * @param productoSeleccionado El producto que se desea procesar en el carrito
     * @param indexCarrito         El índice del producto en el carrito (-1 si no
     *                             está presente)
     * @return true si se realizaron cambios en el carrito, false si no se
     *         procesaron cambios
     */
    private boolean realizarCambios(Producto productoSeleccionado, int indexCarrito) {
        // Si la cantidad es 0 y esta en la lista
        if (cantidad.getValue() == 0 && indexCarrito != -1) {
            // Quita el item del carrito
            listaSubordenes.remove(indexCarrito);
            // Quita el item de la cache
            for (int i = 0; i < productosSeleccionados.size(); i++) {
                // Si encuentra el item en la cache
                if (productosSeleccionados.get(i).getIdProducto() == productoSeleccionado.getIdProducto()) {
                    // Borra el item y termina el bucle
                    productosSeleccionados.remove(i);
                    break;
                }
            }
            return true;
        }
        // Si la cantidad no es 0 y esta en la lista
        else if (cantidad.getValue() != 0 && indexCarrito != -1) {
            // Crea una nueva suborden
            SubOrden subOrdenAntigua = listaSubordenes.get(indexCarrito);
            SubOrden subOrdenNueva = new SubOrden(
                    subOrdenAntigua.getIdSubOrden(),
                    subOrdenAntigua.getIdOrden(),
                    subOrdenAntigua.getIdProducto(),
                    cantidad.getValue());

            // Reemplaza la suborden en la lista
            listaSubordenes.set(indexCarrito, subOrdenNueva);
            return true;
        }
        // Si la cantidad no es 0 y no esta en la lista
        else if (cantidad.getValue() != 0 && indexCarrito == -1) {
            // Añade el producto a la lista de productos seleccionados
            productosSeleccionados.add(productoSeleccionado);
            // Añade la suborden a la lista
            listaSubordenes.add(new SubOrden(-1, -1, productoSeleccionado.getIdProducto(), cantidad.getValue()));
            return true;
        }
        // Si la cantidad es 0 y no esta en la lista
        else {
            PopUp.informacion("Cantidad inválida", "Ingrese una cantidad mayor a 0 para añadir a las compras");
            return false;
        }
    }

    /**
     * Restablece la interfaz completa a su estado inicial para una nueva venta.
     * 
     * <p>
     * Reinicia todos los campos de entrada, limpia las estructuras de datos,
     * vacía las tablas y listas visuales, y restablece el estado de habilitación
     * de todos los controles. Este método se ejecuta al inicializar el controlador
     * y después de confirmar una compra exitosa.
     * </p>
     * 
     * <p>
     * <strong>Operaciones de restablecimiento:</strong>
     * </p>
     * <ul>
     * <li>Limpieza de todos los campos de texto de cliente y producto</li>
     * <li>Reinicio del spinner de cantidad a valor 0</li>
     * <li>Vaciado de listas de subórdenes y cache de productos</li>
     * <li>Limpieza de tablas visuales y etiqueta de precio</li>
     * <li>Deshabilitación de todos los controles de búsqueda y compra</li>
     * </ul>
     * 
     * <p>
     * Después de ejecutar este método, la interfaz queda lista para iniciar
     * un nuevo proceso de venta, requiriendo la búsqueda de cliente como
     * primer paso obligatorio.
     * </p>
     */
    private void restablecerTodo() {
        // Reinicia los cuadros de texto mostrados
        dniCliente.setText("");
        nombreCliente.setText("");
        telefono.setText("");
        email.setText("");
        idProducto.setText("");
        nombreProducto.setText("");
        cantidad.getValueFactory().setValue(0);

        // Borra la cache
        clienteSeleccionado = null;
        listaSubordenes.clear();
        productosSeleccionados.clear();

        // Limpia las tablas y resultado
        productos.setItems(FXCollections.observableArrayList());
        resultado.setItems(FXCollections.observableArrayList());
        precio.setText("S/ 0.00");

        // Deshabilita todo
        deshabilitarBusqueda(true);
        deshabilitarAnadir(true);
        deshabilitarComprar(true);
    }

    /**
     * Controla el estado de habilitación de los controles de búsqueda de productos.
     * 
     * <p>
     * Habilita o deshabilita simultáneamente todos los controles relacionados
     * con la búsqueda de productos, incluyendo los campos de filtro por ID
     * y nombre, así como el botón de búsqueda. Se utiliza para controlar
     * el flujo de la interfaz según el estado del proceso de venta.
     * </p>
     * 
     * <p>
     * Los controles se habilitan únicamente después de seleccionar un cliente
     * válido, y se deshabilitan durante el restablecimiento de la interfaz
     * o cuando ocurren errores en el proceso de búsqueda de cliente.
     * </p>
     * 
     * @param value true para deshabilitar los controles, false para habilitarlos
     */
    private void deshabilitarBusqueda(boolean value) {
        idProducto.setDisable(value);
        nombreProducto.setDisable(value);
        searchProduct.setDisable(value);
    }

    /**
     * Controla el estado de habilitación de los controles de adición de productos
     * al carrito.
     * 
     * <p>
     * Habilita o deshabilita simultáneamente todos los controles relacionados
     * con la selección y adición de productos al carrito, incluyendo la lista
     * de productos, el spinner de cantidad y el botón de añadir. Se utiliza
     * para mantener la coherencia del flujo de interfaz.
     * </p>
     * 
     * <p>
     * Los controles se habilitan después de realizar una búsqueda exitosa
     * de productos, y se deshabilitan durante el restablecimiento o cuando
     * no hay productos disponibles para mostrar.
     * </p>
     * 
     * @param value true para deshabilitar los controles, false para habilitarlos
     */
    private void deshabilitarAnadir(boolean value) {
        productos.setDisable(value);
        cantidad.setDisable(value);
        addProduct.setDisable(value);
    }

    /**
     * Controla el estado de habilitación del botón de confirmación de compra.
     * 
     * <p>
     * Habilita o deshabilita el botón de compra según la presencia de productos
     * en el carrito de compras. Este control asegura que solo se puedan confirmar
     * compras cuando hay al menos un producto seleccionado con cantidad mayor a
     * cero.
     * </p>
     * 
     * <p>
     * El botón se habilita automáticamente cuando se añade el primer producto
     * al carrito y se deshabilita cuando se elimina el último producto del mismo.
     * También se deshabilita durante el restablecimiento de la interfaz.
     * </p>
     * 
     * @param value true para deshabilitar el botón, false para habilitarlo
     */
    private void deshabilitarComprar(boolean value) {
        comprar.setDisable(value);
    }
}