package ucv.codelab.controller.importar;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ucv.codelab.model.Producto;
import ucv.codelab.repository.BaseRepository;
import ucv.codelab.repository.ProductoRepository;
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

    @Override
    protected void configurarColumnas() {
        // Configurar la columna de nombre - coincide con el nombre del atributo en la
        // clase Producto
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));

        // Configurar la columna de precio - coincide con el nombre del atributo
        // en la clase Producto
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
    }

    @Override
    protected BaseRepository<Producto> repositorioBase(Connection connection) {
        return new ProductoRepository(connection);
    }

    @Override
    protected boolean validar(BaseRepository<Producto> repository, Producto dato) {
        ProductoRepository repo = (ProductoRepository) repository;
        // Buscar todos los productos coincidentes vigentes
        List<Producto> coincidencias = repo.findByNombreVigentes(dato.getNombreProducto());
        for (Producto producto : coincidencias) {
            // Si encuentra un producto vigente con exactamente el mismo nombre no lo añade
            if (producto.getNombreProducto().equalsIgnoreCase(dato.getNombreProducto())) {
                return false;
            }
        }
        // De lo contrario continua con la inserción
        return true;
    }
}