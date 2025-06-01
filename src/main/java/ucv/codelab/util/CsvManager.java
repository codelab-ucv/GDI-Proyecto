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

public class CsvManager {

    /**
     * Lee un archivo CSV y retorna una lista de mapas con los datos.
     * La primera fila se usa como cabecera (nombres de columnas).
     * 
     * @param rutaArchivo Ruta al archivo CSV
     * @return Lista de mapas donde cada mapa representa una fila del CSV
     * @throws IOException Si hay error al leer el archivo
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
                Map<String, String> fila = new HashMap<>();

                for (String columna : parser.getHeaderMap().keySet()) {
                    String valor = record.get(columna);
                    fila.put(columna, valor != null ? valor : "");
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
     * @param rutaArchivo Ruta al archivo CSV
     * @return Lista con los nombres de las columnas
     * @throws IOException Si hay error al leer el archivo
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

            return new ArrayList<>(parser.getHeaderMap().keySet());
        } catch (IOException e) {
            System.err.println("Ocurrio un error al leer el archivo CSV: " + e.getMessage());
        }
        // Retorna un ArrayList vacio
        return new ArrayList<>();
    }
}