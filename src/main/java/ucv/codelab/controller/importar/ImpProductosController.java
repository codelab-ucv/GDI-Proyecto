package ucv.codelab.controller.importar;

import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ucv.codelab.model.Producto;
import ucv.codelab.service.io.ProductoReader;

public class ImpProductosController extends ImportarBase<Producto> {

    @FXML
    private TableColumn<Producto, String> columnaNombre;

    @FXML
    private TableColumn<Producto, Double> columnaPrecio;

    @Override
    protected List<Producto> cargarArchivo(String rutaArchivo) throws IOException {
        return new ProductoReader().importar(rutaArchivo);
    }

    // Método para configurar las columnas de la tabla
    @Override
    protected void configurarColumnas() {
        // Configurar la columna de nombre - coincide con el nombre del atributo en la
        // clase Producto
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));

        // Configurar la columna de precio - coincide con el nombre del atributo
        // en la clase Producto
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
    }
}