package ucv.codelab.controller.importar;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ucv.codelab.model.Producto;
import ucv.codelab.repository.BaseRepository;
import ucv.codelab.repository.ProductoRepository;
import ucv.codelab.service.io.ProductoReader;

/**
 * Controlador para la importación de productos desde archivos CSV.
 * 
 * <p>
 * Esta clase extiende {@link ImportarBase} e implementa las operaciones
 * específicas para la importación de datos de productos desde archivos CSV,
 * proporcionando funcionalidades de carga, validación y persistencia de
 * registros de productos en la base de datos.
 * </p>
 * 
 * <p>
 * <strong>Funcionalidades principales:</strong>
 * </p>
 * <ul>
 * <li>Importación de datos de productos desde archivos CSV</li>
 * <li>Vista previa de datos cargados en tabla de interfaz gráfica</li>
 * <li>Validación de duplicados por nombre en productos vigentes</li>
 * <li>Inserción masiva de productos válidos en la base de datos</li>
 * <li>Reporte de resultados de importación con conteo de éxitos y fallos</li>
 * </ul>
 * 
 * <p>
 * <strong>Columnas de tabla configuradas:</strong>
 * </p>
 * <ul>
 * <li>Nombre del producto - Nombre descriptivo del producto</li>
 * <li>Precio - Valor monetario del producto en la moneda local</li>
 * </ul>
 * 
 * <p>
 * <strong>Proceso de validación:</strong>
 * </p>
 * <ul>
 * <li>Verificación de duplicados por nombre exacto en productos vigentes</li>
 * <li>Comparación case-insensitive para evitar duplicados por diferencias de
 * mayúsculas</li>
 * <li>Solo productos con nombres únicos entre los vigentes son insertados</li>
 * <li>Los productos descontinuados no interfieren en la validación</li>
 * </ul>
 * 
 * <p>
 * La clase utiliza {@link ProductoReader} para la lectura del archivo CSV
 * y {@link ProductoRepository} para las operaciones de base de datos,
 * manteniendo la separación de responsabilidades y el bajo acoplamiento.
 * </p>
 * 
 * @see ImportarBase
 * @see Producto
 * @see ProductoRepository
 * @see ProductoReader
 */
public class ImpProductosController extends ImportarBase<Producto> {

    /**
     * Columna de tabla para mostrar el nombre del producto.
     * 
     * <p>
     * Esta columna se vincula con el atributo {@code nombreProducto}
     * del objeto Producto mediante PropertyValueFactory, mostrando
     * la denominación comercial de cada producto importado.
     * </p>
     */
    @FXML
    private TableColumn<Producto, String> columnaNombre;

    /**
     * Columna de tabla para mostrar el precio del producto.
     * 
     * <p>
     * Esta columna se vincula con el atributo {@code precio}
     * del objeto Producto, mostrando el valor monetario
     * en formato decimal para cada producto importado.
     * </p>
     */
    @FXML
    private TableColumn<Producto, Double> columnaPrecio;

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Utiliza {@link ProductoReader} para procesar el archivo CSV
     * y convertir cada fila en un objeto {@link Producto} válido.
     * El proceso incluye la validación de formato, conversión de tipos
     * de datos numéricos para precios, y manejo de errores de formato.
     * </p>
     * 
     * @param rutaArchivo Ruta completa del archivo CSV a procesar
     * @return Lista de objetos Producto extraídos del archivo CSV
     * @throws IOException Si ocurre un error durante la lectura del archivo,
     *                     formato incorrecto, conversión de tipos, o archivo no
     *                     encontrado
     */
    @Override
    protected List<Producto> cargarArchivo(String rutaArchivo) throws IOException {
        return new ProductoReader().importar(rutaArchivo);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Configura las columnas de la tabla utilizando {@link PropertyValueFactory}
     * para vincular cada columna con su atributo correspondiente en la clase
     * {@link Producto}. Esta configuración permite la visualización automática
     * de los datos importados en la interfaz de usuario.
     * </p>
     * 
     * <p>
     * <strong>Vinculaciones realizadas:</strong>
     * </p>
     * <ul>
     * <li>columnaNombre → nombreProducto - Muestra el nombre descriptivo</li>
     * <li>columnaPrecio → precio - Muestra el valor monetario con formato
     * decimal</li>
     * </ul>
     * 
     * <p>
     * La configuración utiliza nombres de atributos que coinciden exactamente
     * con los definidos en la clase {@link Producto} para garantizar
     * la correcta vinculación de datos.
     * </p>
     */
    @Override
    protected void configurarColumnas() {
        // Configurar la columna de nombre - coincide con el nombre del atributo en la
        // clase Producto
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));

        // Configurar la columna de precio - coincide con el nombre del atributo
        // en la clase Producto
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Proporciona una instancia de {@link ProductoRepository} configurada
     * con la conexión a la base de datos para realizar operaciones CRUD
     * sobre la tabla de productos durante el proceso de importación.
     * </p>
     * 
     * @return Repositorio de productos para acceso a datos
     * @throws SQLException Si ocurre un error al establecer la conexión con la base
     *                      de datos
     */
    @Override
    protected BaseRepository<Producto> repositorioBase() throws SQLException {
        return new ProductoRepository();
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Valida que el producto a importar no tenga un nombre duplicado
     * entre los productos vigentes en la base de datos. Esta validación
     * asegura la unicidad de nombres en el catálogo activo de productos,
     * evitando confusiones y manteniendo la integridad del inventario.
     * </p>
     * 
     * <p>
     * <strong>Proceso de validación:</strong>
     * </p>
     * <ul>
     * <li>Búsqueda de productos vigentes con nombre similar</li>
     * <li>Comparación case-insensitive usando {@code equalsIgnoreCase()}</li>
     * <li>Rechazo de inserción si encuentra coincidencias exactas</li>
     * <li>Aprobación de inserción si no hay duplicados en productos vigentes</li>
     * <li>Los productos descontinuados no afectan la validación</li>
     * </ul>
     * 
     * <p>
     * Esta implementación permite tener productos con el mismo nombre
     * si uno está descontinuado, pero mantiene la unicidad entre
     * productos activos para evitar ambigüedades en el sistema.
     * </p>
     * 
     * @param repository Repositorio base para consultas de validación
     * @param dato       Producto a validar antes de la inserción
     * @return {@code true} si el producto puede ser insertado (nombre único entre
     *         vigentes),
     *         {@code false} si ya existe un producto vigente con el mismo nombre
     */
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