package ucv.codelab.model;

/**
 * Representa un cliente en el sistema de gestión de órdenes.
 * 
 * <p>
 * Esta clase modela la información básica de un cliente que puede realizar
 * pedidos en el sistema. Cada cliente tiene un DNI único que lo identifica
 * de manera unívoca en la base de datos.
 * </p>
 * 
 * <p>
 * <strong>Tabla asociada:</strong> {@code cliente}
 * </p>
 * 
 * <p>
 * <strong>Campos de la tabla:</strong>
 * </p>
 * <ul>
 * <li>{@code id_cliente} - Clave primaria autoincremental</li>
 * <li>{@code nombre_cliente} - Nombre completo del cliente</li>
 * <li>{@code dni_cliente} - Documento Nacional de Identidad (único)</li>
 * <li>{@code telefono} - Número de teléfono de contacto (opcional)</li>
 * <li>{@code email_cliente} - Correo electrónico del cliente (opcional)</li>
 * </ul>
 * 
 * <p>
 * <strong>Relaciones:</strong>
 * </p>
 * <ul>
 * <li>Un cliente puede tener múltiples órdenes de compra</li>
 * <li>Cada orden está asociada a un único cliente</li>
 * </ul>
 * 
 * @see ucv.codelab.repository.ClienteRepository
 */
public class Cliente {

    /**
     * Identificador único del cliente en la base de datos.
     * Corresponde a la clave primaria autoincremental.
     */
    private int idCliente;

    /**
     * Nombre completo del cliente.
     * Campo requerido que identifica al cliente en el sistema.
     */
    private String nombreCliente;

    /**
     * Documento Nacional de Identidad del cliente.
     * Campo único y requerido que sirve como identificador natural del cliente.
     */
    private String dniCliente;

    /**
     * Número de teléfono de contacto del cliente.
     * Campo opcional para comunicación con el cliente.
     */
    private String telefono;

    /**
     * Correo electrónico del cliente.
     * Campo opcional para notificaciones y comunicación digital.
     */
    private String emailCliente;

    /**
     * Constructor que inicializa un cliente con todos sus datos.
     * 
     * <p>
     * Este constructor se utiliza principalmente al recuperar datos
     * de la base de datos, donde ya se conoce el ID del cliente.
     * </p>
     * 
     * @param idCliente     Identificador único del cliente
     * @param nombreCliente Nombre completo del cliente
     * @param dniCliente    DNI del cliente (debe ser único)
     * @param telefono      Teléfono de contacto (puede ser null)
     * @param emailCliente  Email del cliente (puede ser null)
     */
    public Cliente(int idCliente, String nombreCliente, String dniCliente, String telefono, String emailCliente) {
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.dniCliente = dniCliente;
        this.telefono = telefono;
        this.emailCliente = emailCliente;
    }

    /**
     * Obtiene el identificador único del cliente.
     * 
     * @return ID del cliente
     */
    public int getIdCliente() {
        return idCliente;
    }

    /**
     * Establece el identificador único del cliente.
     * 
     * <p>
     * Este método se utiliza principalmente después de insertar
     * un nuevo cliente en la base de datos para asignar el ID
     * generado automáticamente.
     * </p>
     * 
     * @param idCliente ID del cliente a establecer
     */
    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    /**
     * Obtiene el nombre completo del cliente.
     * 
     * @return Nombre del cliente
     */
    public String getNombreCliente() {
        return nombreCliente;
    }

    /**
     * Establece el nombre completo del cliente.
     * 
     * @param nombreCliente Nombre del cliente a establecer
     */
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    /**
     * Obtiene el DNI del cliente.
     * 
     * @return DNI del cliente
     */
    public String getDniCliente() {
        return dniCliente;
    }

    /**
     * Establece el DNI del cliente.
     * 
     * <p>
     * <strong>Importante:</strong> El DNI debe ser único en el sistema.
     * Asegúrese de validar la unicidad antes de actualizar este campo.
     * </p>
     * 
     * @param dniCliente DNI del cliente a establecer
     */
    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }

    /**
     * Obtiene el número de teléfono del cliente.
     * 
     * @return Teléfono del cliente, puede ser null si no se proporcionó
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el número de teléfono del cliente.
     * 
     * @param telefono Teléfono del cliente a establecer (puede ser null)
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Obtiene el correo electrónico del cliente.
     * 
     * @return Email del cliente, puede ser null si no se proporcionó
     */
    public String getEmailCliente() {
        return emailCliente;
    }

    /**
     * Establece el correo electrónico del cliente.
     * 
     * @param emailCliente Email del cliente a establecer (puede ser null)
     */
    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }

    public static Object[] cabecera() {
        return new Object[] { "ID", "Nombre", "Telefono", "Email" };
    }

    public Object[] registro() {
        return new Object[] { idCliente, nombreCliente, telefono, emailCliente };
    }
}