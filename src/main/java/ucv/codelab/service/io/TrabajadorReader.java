package ucv.codelab.service.io;

import java.util.Map;
import java.util.Set;

import ucv.codelab.model.Trabajador;

/**
 * Implementación de CsvReader para la importación de trabajadores desde
 * archivos CSV.
 * 
 * <p>
 * Este reader procesa archivos CSV que contienen información de trabajadores,
 * validando que tengan los campos obligatorios y puestos válidos antes de crear
 * los objetos Trabajador.
 * </p>
 * 
 * <p>
 * <strong>Campos del CSV:</strong>
 * </p>
 * <ul>
 * <li><strong>nombre</strong> - Nombre completo del trabajador
 * (obligatorio)</li>
 * <li><strong>dni</strong> - Documento de identidad del trabajador
 * (obligatorio)</li>
 * <li><strong>puesto</strong> - Cargo del trabajador: JEFE, SUPERVISOR o
 * TRABAJADOR (obligatorio)</li>
 * <li><strong>tipo de letra</strong> - Configuración de fuente para UI
 * (opcional)</li>
 * <li><strong>color de fondo</strong> - Color de fondo para UI (opcional)</li>
 * <li><strong>color de boton</strong> - Color de botón para UI (opcional)</li>
 * </ul>
 * 
 * @see CsvReader
 * @see Trabajador
 */
public class TrabajadorReader extends CsvReader<Trabajador> {

    /**
     * Conjunto de puestos válidos que puede tener un trabajador.
     * Los valores se almacenan en mayúsculas para facilitar la validación
     * case-insensitive.
     */
    private static final Set<String> PUESTOS_VALIDOS = Set.of("JEFE", "SUPERVISOR", "TRABAJADOR");

    /**
     * Procesa una fila del CSV y crea un objeto Trabajador.
     * 
     * <p>
     * Realiza las siguientes validaciones:
     * </p>
     * <ul>
     * <li>Nombre, DNI y puesto no pueden estar vacíos</li>
     * <li>Puesto debe ser uno de los valores válidos: JEFE, SUPERVISOR o
     * TRABAJADOR</li>
     * <li>Los campos de configuración UI son opcionales</li>
     * </ul>
     * 
     * <p>
     * El puesto se convierte automáticamente a mayúsculas para mantener
     * consistencia.
     * </p>
     * 
     * @param fila       Mapa con los datos de la fila del CSV
     * @param numeroFila Número de fila actual para logging
     * @return Trabajador creado o null si los datos no son válidos
     */
    @Override
    protected Trabajador procesarFila(Map<String, String> fila, int numeroFila) {
        // Extraer valores de la fila
        String nombre = obtenerValor(fila, "nombre");
        String dni = obtenerValor(fila, "dni");
        String puesto = obtenerValor(fila, "puesto");
        String tipoLetra = obtenerValorOpcional(fila, "tipo de letra");
        String colorFondo = obtenerValorOpcional(fila, "color de fondo");
        String colorBoton = obtenerValorOpcional(fila, "color de boton");

        // Validar campos obligatorios
        if (!sonCamposObligatoriosValidos(nombre, dni, puesto)) {
            System.out.println("Fila " + numeroFila + " omitida: campos obligatorios vacios");
            return null;
        }

        // Validar puesto
        if (!esPuestoValido(puesto)) {
            System.out.println("Fila " + numeroFila + " omitida: puesto invalido '" + puesto + "'");
            return null;
        }

        // Crear y retornar el trabajador
        return new Trabajador(-1, nombre, dni, puesto.toUpperCase(), tipoLetra, colorFondo, colorBoton);
    }

    /**
     * Valida que el puesto proporcionado sea uno de los valores permitidos.
     * 
     * <p>
     * La validación es case-insensitive, convirtiendo el puesto a mayúsculas
     * antes de verificar si está en el conjunto de puestos válidos.
     * </p>
     * 
     * @param puesto Puesto a validar
     * @return true si el puesto es válido, false en caso contrario
     * 
     * @see #PUESTOS_VALIDOS
     */
    private static boolean esPuestoValido(String puesto) {
        return puesto != null && PUESTOS_VALIDOS.contains(puesto.toUpperCase());
    }
}