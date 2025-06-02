package ucv.codelab.service.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ucv.codelab.util.CsvManager;

/**
 * Clase abstracta base para la importación de datos desde archivos CSV.
 * 
 * <p>
 * Esta clase implementa el patrón Template Method, donde el método
 * {@link #importar(String)}
 * define el algoritmo general de importación, delegando la lógica específica de
 * procesamiento
 * de cada fila al método abstracto {@link #procesarFila(Map, int)}.
 * </p>
 * 
 * <p>
 * Proporciona métodos utilitarios comunes para la extracción y validación de
 * datos
 * que pueden ser reutilizados por las clases hijas.
 * </p>
 * 
 * @param <T> Tipo de objeto que se creará a partir de cada fila del CSV
 */
public abstract class CsvReader<T> {

    /**
     * Importa datos desde un archivo CSV y los convierte en una lista de objetos.
     * 
     * <p>
     * Este método sigue el patrón Template Method:
     * </p>
     * <ol>
     * <li>Lee el archivo CSV usando {@link CsvManager}</li>
     * <li>Valida que existan datos</li>
     * <li>Procesa cada fila individualmente usando
     * {@link #procesarFila(Map, int)}</li>
     * <li>Maneja errores por fila sin detener el proceso completo</li>
     * <li>Retorna la lista de objetos válidos creados</li>
     * </ol>
     * 
     * @param ruta Ruta del archivo CSV a importar
     * @return Lista de objetos de tipo T creados exitosamente
     * @throws IOException Si hay error al leer el archivo
     * 
     * @see CsvManager#leerArchivo(String)
     * @see #procesarFila(Map, int)
     */
    public List<T> importar(String ruta) throws IOException {
        List<Map<String, String>> filasImportadas = CsvManager.leerArchivo(ruta);
        List<T> datosImportados = new ArrayList<>();

        // Validar que se importaron datos
        if (filasImportadas.isEmpty()) {
            System.out.println("No se encontraron datos en el archivo CSV");
            return datosImportados;
        }

        int filaNumero = 1; // Para tracking de errores

        // Para cada fila ingresada
        for (Map<String, String> fila : filasImportadas) {
            try {
                T objeto = procesarFila(fila, filaNumero);
                if (objeto != null) {
                    datosImportados.add(objeto);
                }
            } catch (Exception e) {
                System.err.println("Error procesando fila " + filaNumero + ": " + e.getMessage());
            }
            filaNumero++;
        }

        System.out.println("Datos importados exitosamente: " + datosImportados.size());
        return datosImportados;
    }

    /**
     * Procesa una fila individual del CSV y crea un objeto de tipo T.
     * 
     * <p>
     * Este método debe ser implementado por las clases hijas para definir
     * la lógica específica de conversión de los datos de la fila a un objeto.
     * </p>
     * 
     * <p>
     * <strong>Responsabilidades del método:</strong>
     * </p>
     * <ul>
     * <li>Extraer los valores necesarios de la fila usando
     * {@link #obtenerValor(Map, String)}</li>
     * <li>Validar que los datos sean correctos</li>
     * <li>Crear y retornar el objeto T, o null si los datos no son válidos</li>
     * </ul>
     * 
     * @param fila       Mapa con los datos de la fila (clave=nombre columna,
     *                   valor=dato)
     * @param numeroFila Número de fila actual (para logging de errores)
     * @return Objeto de tipo T creado, o null si los datos no son válidos
     */
    protected abstract T procesarFila(Map<String, String> fila, int numeroFila);

    /**
     * Obtiene un valor de la fila de manera case-insensitive.
     * 
     * <p>
     * Busca la clave especificada en el mapa sin importar mayúsculas/minúsculas
     * y retorna el valor limpio (sin espacios al inicio/final).
     * </p>
     * 
     * @param fila         Mapa con los datos de la fila
     * @param claveBuscada Nombre de la columna a buscar
     * @return Valor encontrado (trimmed) o cadena vacía si no existe
     */
    protected String obtenerValor(Map<String, String> fila, String claveBuscada) {
        // Entry permite iterar entre cada par clave-valor
        for (Map.Entry<String, String> entry : fila.entrySet()) {
            if (entry.getKey().toLowerCase().equals(claveBuscada.toLowerCase())) {
                String valor = entry.getValue();
                return valor != null ? valor.trim() : "";
            }
        }
        return "";
    }

    /**
     * Obtiene un valor opcional de la fila (puede retornar null).
     * 
     * <p>
     * Similar a {@link #obtenerValor(Map, String)}, pero retorna null
     * en lugar de cadena vacía cuando el valor no existe o está vacío.
     * </p>
     * 
     * @param fila         Mapa con los datos de la fila
     * @param claveBuscada Nombre de la columna a buscar
     * @return Valor encontrado o null si no existe/está vacío
     */
    protected String obtenerValorOpcional(Map<String, String> fila, String claveBuscada) {
        String valor = obtenerValor(fila, claveBuscada);
        return valor.isEmpty() ? null : valor;
    }

    /**
     * Valida que todos los campos obligatorios proporcionados no estén vacíos.
     * 
     * <p>
     * Método utilitario que acepta un número variable de argumentos
     * y verifica que ninguno sea null o cadena vacía.
     * </p>
     * 
     * @param datos Campos a validar (varargs)
     * @return true si todos los campos son válidos (no null y no vacíos), false en
     *         caso contrario
     * 
     * @example
     * 
     *          <pre>
     *          // Validar múltiples campos obligatorios
     *          if (sonCamposObligatoriosValidos(nombre, dni, email)) {
     *              // Todos los campos están completos
     *          }
     *          </pre>
     */
    protected boolean sonCamposObligatoriosValidos(String... datos) {
        for (String dato : datos) {
            if (dato == null || dato.isEmpty())
                return false;
        }
        return true;
    }
}