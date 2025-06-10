package ucv.codelab.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase utilitaria para la gestión de archivos CSV.
 * 
 * <p>
 * Esta clase proporciona métodos estáticos para leer y procesar archivos CSV,
 * especialmente aquellos generados por Microsoft Excel. Maneja automáticamente
 * problemas comunes como el BOM (Byte Order Mark) y utiliza configuraciones
 * optimizadas para archivos CSV con delimitador de punto y coma.
 * </p>
 * 
 * <p>
 * <strong>Características principales:</strong>
 * </p>
 * <ul>
 * <li>Lectura de archivos CSV con codificación UTF-8</li>
 * <li>Manejo automático del BOM de Excel</li>
 * <li>Soporte para delimitador de punto y coma (;)</li>
 * <li>Retorno de datos en formato Map para fácil acceso</li>
 * <li>Extracción de nombres de columnas</li>
 * </ul>
 * 
 * <p>
 * <strong>Formato CSV soportado:</strong>
 * </p>
 * <ul>
 * <li>Primera fila como cabecera</li>
 * <li>Delimitador: punto y coma (;)</li>
 * <li>Codificación: UTF-8</li>
 * <li>Espacios en blanco recortados automáticamente</li>
 * </ul>
 */
public class CsvManager {

    /**
     * Lee un archivo CSV y retorna una lista de mapas con los datos.
     * 
     * <p>
     * La primera fila se usa como cabecera (nombres de columnas).
     * Cada fila subsecuente se convierte en un Map donde las claves
     * son los nombres de las columnas y los valores son los datos
     * de cada celda.
     * </p>
     * 
     * <p>
     * El método maneja automáticamente:
     * </p>
     * <ul>
     * <li>Limpieza del BOM si está presente</li>
     * <li>Recorte de espacios en blanco</li>
     * <li>Ignorar líneas vacías</li>
     * <li>Manejo de valores nulos</li>
     * </ul>
     * 
     * @param rutaArchivo Ruta completa al archivo CSV a leer
     * @return Lista de mapas donde cada mapa representa una fila del CSV
     *         con el formato columna->valor. Retorna lista vacía si hay error
     */
    public static List<Map<String, String>> leerArchivo(String rutaArchivo) {
        List<Map<String, String>> registros = new ArrayList<>();

        CSVFormat formato = CSVFormat.Builder.create()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .setDelimiter(';') // Delimitador de ; usado en Excel
                .build();

        try (FileReader reader = new FileReader(rutaArchivo, StandardCharsets.UTF_8);
                CSVParser parser = new CSVParser(reader, formato)) {

            for (CSVRecord record : parser) {
                // Guarda un mapa por cada fila segun el patron Columna-Valor
                Map<String, String> fila = new HashMap<>();

                // Para cada columna del archivo
                for (String columna : parser.getHeaderMap().keySet()) {
                    String valor = record.get(columna);

                    // Limpiar BOM del nombre de la columna si existe (problema común de Excel)
                    String columnaSinBOM = limpiarBOM(columna);

                    fila.put(columnaSinBOM, valor != null ? valor.trim() : "");
                }

                registros.add(fila);
            }
        } catch (IOException e) {
            System.err.println("Ocurrio un error al leer el archivo CSV: " + e.getMessage());
        }

        return registros;
    }

    /**
     * Obtiene solo los nombres de las columnas del archivo CSV.
     * 
     * <p>
     * Lee únicamente la primera fila del archivo CSV para extraer
     * los nombres de las columnas. Útil para validar la estructura
     * del archivo antes de procesarlo completamente o para mostrar
     * las columnas disponibles al usuario.
     * </p>
     * 
     * <p>
     * Los nombres de columnas son automáticamente limpiados de BOM
     * y espacios en blanco adicionales.
     * </p>
     * 
     * @param rutaArchivo Ruta completa al archivo CSV a analizar
     * @return Lista con los nombres de las columnas limpiados de BOM,
     *         o lista vacía si hay error al leer el archivo
     */
    public static List<String> obtenerColumnas(String rutaArchivo) {
        CSVFormat formato = CSVFormat.Builder.create()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build();

        try (FileReader reader = new FileReader(rutaArchivo, StandardCharsets.UTF_8);
                CSVParser parser = new CSVParser(reader, formato)) {

            List<String> columnas = new ArrayList<>();
            for (String columna : parser.getHeaderMap().keySet()) {
                columnas.add(limpiarBOM(columna));
            }
            return columnas;
        } catch (IOException e) {
            System.err.println("Ocurrio un error al leer el archivo CSV: " + e.getMessage());
        }
        // Retorna un ArrayList vacio
        return new ArrayList<>();
    }

    /**
     * Limpia el BOM (Byte Order Mark) del inicio de una cadena si existe.
     * 
     * <p>
     * Microsoft Excel suele agregar BOM al exportar archivos CSV en UTF-8,
     * lo que puede causar problemas al procesar los nombres de las columnas.
     * Este método detecta y remueve el carácter BOM (\uFEFF) si está presente
     * al inicio de la cadena.
     * </p>
     * 
     * @param texto Cadena de texto que puede contener BOM al inicio
     * @return Cadena sin BOM, o la cadena original si no contiene BOM
     */
    private static String limpiarBOM(String texto) {
        if (texto != null && texto.length() > 0 && texto.charAt(0) == '\uFEFF') {
            return texto.substring(1);
        }
        return texto;
    }
}