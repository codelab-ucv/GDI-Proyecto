package ucv.codelab.service.io;

import java.util.Map;
import java.util.Set;

import ucv.codelab.model.Trabajador;

public class TrabajadorReader extends CsvReader<Trabajador> {

    // Constantes para los puestos válidos
    private static final Set<String> PUESTOS_VALIDOS = Set.of("JEFE", "SUPERVISOR", "TRABAJADOR");

    @Override
    protected Trabajador procesarFila(Map<String, String> fila, int numeroFila) {
        // Extraer valores de la fila
        String nombre = obtenerValor(fila, "nombres");
        String dni = obtenerValor(fila, "dni");
        String puesto = obtenerValor(fila, "puesto");
        String tipoLetra = obtenerValorOpcional(fila, "tipo de letra");
        String colorFondo = obtenerValorOpcional(fila, "color de fondo");
        String colorBoton = obtenerValorOpcional(fila, "color de boton");

        // Validar campos obligatorios
        if (!sonCamposObligatoriosValidos(nombre, dni, puesto)) {
            System.out.println("Fila " + numeroFila + " omitida: campos obligatorios vacíos");
            return null;
        }

        // Validar puesto
        if (!esPuestoValido(puesto)) {
            System.out.println("Fila " + numeroFila + " omitida: puesto inválido '" + puesto + "'");
            return null;
        }

        // Crear y retornar el trabajador
        return new Trabajador(-1, nombre, dni, puesto.toUpperCase(), tipoLetra, colorFondo, colorBoton);
    }

    /**
     * Valida que el puesto sea uno de los valores permitidos.
     */
    private static boolean esPuestoValido(String puesto) {
        return puesto != null && PUESTOS_VALIDOS.contains(puesto.toUpperCase());
    }
}
