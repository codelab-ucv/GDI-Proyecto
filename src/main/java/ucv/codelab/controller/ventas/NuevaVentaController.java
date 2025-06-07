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

public class NuevaVentaController implements Initializable {

    @FXML
    private TextField dniCliente;

    @FXML
    private TextField nombreCliente;

    @FXML
    private TextField telefono;

    @FXML
    private TextField email;

    @FXML
    private TextField idProducto;

    @FXML
    private TextField nombreProducto;

    @FXML
    private Spinner<Integer> cantidad;

    @FXML
    private Button searchProduct;

    @FXML
    private Button addProduct;

    @FXML
    private Button comprar;

    @FXML
    private ListView<Producto> productos;

    @FXML
    private TableView<SubOrden> resultado;

    @FXML
    private Label precio;

    @FXML
    private TableColumn<SubOrden, String> columnaId;

    @FXML
    private TableColumn<SubOrden, String> columnaNombre;

    @FXML
    private TableColumn<SubOrden, String> columnaUnitario;

    @FXML
    private TableColumn<SubOrden, Integer> columnaCantidad;

    @FXML
    private TableColumn<SubOrden, String> columnaTotal;

    private Cliente clienteSeleccionado;

    private List<SubOrden> listaSubordenes = new ArrayList<>();
    private List<Producto> productosSeleccionados = new ArrayList<>();

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
                total += Double.parseDouble(s);
            }

            precio.setText("S/ " + new DecimalFormat("#0.00").format(total));

            if (listaSubordenes.size() > 0) {
                deshabilitarComprar(false);
            } else {
                deshabilitarComprar(true);
            }
        }
    }

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

    private Cliente cargarCliente(Optional<Cliente> optionalCliente, ClienteRepository repository) {
        // Si se tiene el cliente en la base de datos lo usa
        if (optionalCliente.isPresent()) {
            return optionalCliente.get();
        }
        // Si no lo tiene crea el cliente
        // TODO consultar por un nombre y crear el cliente
        // Si el usuario cancela la accion usa el cliente por defecto
        PopUp.informacion("Cliente cancelado", "Se canceló la creación del cliente, vuelve a intentarlo");
        return null;
    }

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

    private void deshabilitarBusqueda(boolean value) {
        idProducto.setDisable(value);
        nombreProducto.setDisable(value);
        searchProduct.setDisable(value);
    }

    private void deshabilitarAnadir(boolean value) {
        productos.setDisable(value);
        cantidad.setDisable(value);
        addProduct.setDisable(value);
    }

    private void deshabilitarComprar(boolean value) {
        comprar.setDisable(value);
    }
}