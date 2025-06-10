package ucv.codelab.service.io;

import java.util.Map;
import ucv.codelab.model.Producto;

/**
 * Implementación de CsvReader para la importación de productos desde archivos
 * CSV.
 * 
 * <p>
 * Este reader procesa archivos CSV que contienen información de productos,
 * validando que tengan nombre y precio válidos antes de crear los objetos
 * Producto.
 * </p>
 * 
 * <p>
 * <strong>Campos requeridos en el CSV:</strong>
 * </p>
 * <ul>
 * <li><strong>nombre</strong> - Nombre del producto (obligatorio)</li>
 * <li><strong>precio</strong> - Precio del producto como número decimal mayor a
 * 0 (obligatorio)</li>
 * </ul>
 * 
 * @see CsvReader
 * @see Producto
 */
public class ProductoReader extends CsvReader<Producto> {

    /**
     * Procesa una fila del CSV y crea un objeto Producto.
     * 
     * <p>
     * Realiza las siguientes validaciones:
     * </p>
     * <ul>
     * <li>Nombre y precio no pueden estar vacíos</li>
     * <li>Precio debe ser un número válido</li>
     * <li>Precio debe ser mayor a 0</li>
     * </ul>
     * 
     * @param fila       Mapa con los datos de la fila del CSV
     * @param numeroFila Número de fila actual para logging
     * @return Producto creado o null si los datos no son válidos
     */
    @Override
    protected Producto procesarFila(Map<String, String> fila, int numeroFila) {
        // Extraer valores de la fila
        String nombre = obtenerValor(fila, "nombre");
        String strPrecio = obtenerValor(fila, "precio");

        double precio;

        // Validar campos obligatorios
        if (!sonCamposObligatoriosValidos(nombre, strPrecio)) {
            System.out.println("Fila " + numeroFila + " omitida: campos obligatorios vacios");
            return null;
        }

        // Validar que el precio sea un número válido
        try {
            precio = Double.parseDouble(strPrecio);
        } catch (NumberFormatException e) {
            System.out.println("Fila " + numeroFila + " omitida: precio invalido '" + strPrecio + "'");
            return null;
        }

        // Validar que el precio sea mayor a 0
        if (precio <= 0) {
            System.out.println("Fila " + numeroFila + " omitida: precio debe ser mayor a 0");
            return null;
        }

        // Crear y retornar el producto
        return new Producto(-1, nombre, precio, true);
    }
}