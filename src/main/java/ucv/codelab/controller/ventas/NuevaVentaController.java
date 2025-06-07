package ucv.codelab.controller.ventas;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import ucv.codelab.model.Cliente;
import ucv.codelab.model.Producto;
import ucv.codelab.repository.ClienteRepository;
import ucv.codelab.repository.ProductoRepository;
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
    private TableView resultado;

    @FXML
    private Label precio;

    private Cliente clienteSeleccionado;
    @SuppressWarnings("unused")
    private Producto productoSeleccionado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Al iniciar deshabilita los recuadros que no deben ser editados
        // Los datos del cliente ya estan siempre deshabilitados por defecto
        deshabilitarBusqueda(true);
        deshabilitarAnadir(true);
        deshabilitarComprar(true);

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
                    setText(producto.getIdProducto() + ": " + producto.getNombreProducto());
                }
            }
        });
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
            if (idProducto.getText() != "") {
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
            else if (nombreProducto.getText() != "") {
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