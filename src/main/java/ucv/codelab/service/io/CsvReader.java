package ucv.codelab.service.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ucv.codelab.util.CsvManager;

public abstract class CsvReader<T> {

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

    protected abstract T procesarFila(Map<String, String> fila, int numeroFila);

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

    protected String obtenerValorOpcional(Map<String, String> fila, String claveBuscada) {
        String valor = obtenerValor(fila, claveBuscada);
        return valor.isEmpty() ? null : valor;
    }

    protected boolean sonCamposObligatoriosValidos(String... campos) {
        for (String campo : campos) {
            if (campo == null || campo.isEmpty())
                return false;
        }
        return true;
    }
}
