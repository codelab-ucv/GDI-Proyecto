package ucv.codelab.service.io;

import java.util.Map;
import ucv.codelab.model.Cliente;

/**
 * Implementación de CsvReader para la importación de clientes desde archivos
 * CSV.
 * 
 * <p>
 * Este reader procesa archivos CSV que contienen información de clientes,
 * validando que tengan los campos obligatorios antes de crear los objetos
 * Cliente.
 * </p>
 * 
 * <p>
 * <strong>Campos del CSV:</strong>
 * </p>
 * <ul>
 * <li><strong>nombre</strong> - Nombre completo del cliente (obligatorio)</li>
 * <li><strong>dni</strong> - Documento de identidad del cliente
 * (obligatorio)</li>
 * <li><strong>telefono</strong> - Número de teléfono (opcional)</li>
 * <li><strong>email</strong> - Correo electrónico (opcional)</li>
 * </ul>
 * 
 * @see CsvReader
 * @see Cliente
 */
public class ClienteReader extends CsvReader<Cliente> {

    /**
     * Procesa una fila del CSV y crea un objeto Cliente.
     * 
     * <p>
     * Realiza las siguientes validaciones:
     * </p>
     * <ul>
     * <li>Nombre no puede estar vacío</li>
     * <li>DNI no puede estar vacío</li>
     * <li>Teléfono y email son opcionales</li>
     * </ul>
     * 
     * @param fila       Mapa con los datos de la fila del CSV
     * @param numeroFila Número de fila actual para logging
     * @return Cliente creado o null si los datos no son válidos
     */
    @Override
    protected Cliente procesarFila(Map<String, String> fila, int numeroFila) {
        // Extraer valores de la fila
        String nombre = obtenerValor(fila, "nombre");
        String dni = obtenerValor(fila, "dni");
        String telefono = obtenerValorOpcional(fila, "telefono");
        String email = obtenerValorOpcional(fila, "email");

        // Validar campos obligatorios
        if (!sonCamposObligatoriosValidos(nombre, dni)) {
            System.out.println("Fila " + numeroFila + " omitida: campos obligatorios vacios");
            return null;
        }

        // Crear y retornar el cliente
        return new Cliente(-1, nombre, dni, telefono, email);
    }
}