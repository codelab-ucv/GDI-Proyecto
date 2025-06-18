package ucv.codelab.model;

/**
 * Representa un trabajador en el sistema de gestión de órdenes.
 * 
 * <p>
 * Esta clase modela la información de los trabajadores que pueden crear y
 * gestionar órdenes en el sistema. Cada trabajador tiene credenciales de
 * acceso y preferencias de personalización de la interfaz como tipo de letra
 * y color de fondo.
 * </p>
 * 
 * <p>
 * <strong>Tabla asociada:</strong> {@code trabajador}
 * </p>
 * 
 * <p>
 * <strong>Campos de la tabla:</strong>
 * </p>
 * <ul>
 * <li>{@code id_trabajador} - Clave primaria autoincremental</li>
 * <li>{@code nombre_trabajador} - Nombre completo del trabajador</li>
 * <li>{@code dni_trabajador} - Documento Nacional de Identidad (único)</li>
 * <li>{@code puesto} - Cargo o posición del trabajador</li>
 * <li>{@code tipo_letra} - Preferencia de fuente para la interfaz
 * (opcional)</li>
 * <li>{@code color_fondo} - Preferencia de color de fondo para la interfaz
 * (opcional)</li>
 * <li>{@code password} - Contraseña de acceso (por defecto "password")</li>
 * </ul>
 * 
 * <p>
 * <strong>Restricciones:</strong>
 * </p>
 * <ul>
 * <li>El DNI del trabajador debe ser único en el sistema</li>
 * <li>La contraseña por defecto es "password"</li>
 * </ul>
 * 
 * <p>
 * <strong>Relaciones:</strong>
 * </p>
 * <ul>
 * <li>Un trabajador puede crear múltiples órdenes</li>
 * <li>Cada orden está asociada a un único trabajador que la creó</li>
 * </ul>
 * 
 * @see ucv.codelab.repository.TrabajadorRepository
 * @see ucv.codelab.model.Orden
 */
public class Trabajador {

    /**
     * Identificador único del trabajador en la base de datos.
     * Corresponde a la clave primaria autoincremental.
     */
    private int idTrabajador;

    /**
     * Nombre completo del trabajador.
     * Campo requerido que identifica al trabajador en el sistema.
     */
    private String nombreTrabajador;

    /**
     * Documento Nacional de Identidad del trabajador.
     * Campo único y requerido que sirve como identificador natural del trabajador.
     */
    private String dniTrabajador;

    /**
     * Cargo o posición del trabajador en la empresa.
     * Campo requerido que describe la función del trabajador.
     */
    private String puesto;

    /**
     * Preferencia de tipo de letra para la interfaz del usuario.
     * Campo opcional que permite personalizar la apariencia de la aplicación.
     */
    private String tipoLetra;

    /**
     * Preferencia de color de fondo para la interfaz del usuario.
     * Campo opcional que permite personalizar la apariencia de la aplicación.
     */
    private String colorFondo;

    /**
     * Contraseña de acceso del trabajador al sistema.
     * Campo transient para evitar serialización accidental.
     * Por defecto se establece como "password".
     */
    private transient String password;

    /**
     * Constructor que inicializa un trabajador con todos sus datos.
     * 
     * <p>
     * Este constructor se utiliza principalmente al recuperar datos
     * de la base de datos, donde ya se conocen todos los campos del
     * trabajador, incluyendo sus preferencias de interfaz.
     * </p>
     * 
     * @param idTrabajador     Identificador único del trabajador
     * @param nombreTrabajador Nombre completo del trabajador
     * @param dniTrabajador    DNI del trabajador (debe ser único)
     * @param puesto           Cargo o posición del trabajador
     * @param tipoLetra        Preferencia de fuente (puede ser null)
     * @param colorFondo       Preferencia de color de fondo (puede ser null)
     * @param password         Contraseña de acceso
     */
    public Trabajador(int idTrabajador, String nombreTrabajador, String dniTrabajador, String puesto, String tipoLetra,
            String colorFondo, String password) {
        this.idTrabajador = idTrabajador;
        this.nombreTrabajador = nombreTrabajador;
        this.dniTrabajador = dniTrabajador;
        this.puesto = puesto;
        this.tipoLetra = tipoLetra;
        this.colorFondo = colorFondo;
        this.password = password;
    }

    /**
     * Obtiene el identificador único del trabajador.
     * 
     * @return ID del trabajador
     */
    public int getIdTrabajador() {
        return idTrabajador;
    }

    /**
     * Establece el identificador único del trabajador.
     * 
     * <p>
     * Este método se utiliza principalmente después de insertar
     * un nuevo trabajador en la base de datos para asignar el ID
     * generado automáticamente.
     * </p>
     * 
     * @param idTrabajador ID del trabajador a establecer
     */
    public void setIdTrabajador(int idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    /**
     * Obtiene el nombre completo del trabajador.
     * 
     * @return Nombre del trabajador
     */
    public String getNombreTrabajador() {
        return nombreTrabajador;
    }

    /**
     * Establece el nombre completo del trabajador.
     * 
     * @param nombreTrabajador Nombre del trabajador a establecer
     */
    public void setNombreTrabajador(String nombreTrabajador) {
        this.nombreTrabajador = nombreTrabajador;
    }

    /**
     * Obtiene el DNI del trabajador.
     * 
     * @return DNI del trabajador
     */
    public String getDniTrabajador() {
        return dniTrabajador;
    }

    /**
     * Establece el DNI del trabajador.
     * 
     * <p>
     * <strong>Importante:</strong> El DNI debe ser único en el sistema.
     * Asegúrese de validar la unicidad antes de actualizar este campo.
     * </p>
     * 
     * @param dniTrabajador DNI del trabajador a establecer
     */
    public void setDniTrabajador(String dniTrabajador) {
        this.dniTrabajador = dniTrabajador;
    }

    /**
     * Obtiene el puesto del trabajador.
     * 
     * @return Cargo o posición del trabajador
     */
    public String getPuesto() {
        return puesto;
    }

    /**
     * Establece el puesto del trabajador.
     * 
     * @param puesto Cargo o posición del trabajador a establecer
     */
    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    /**
     * Obtiene la preferencia de tipo de letra del trabajador.
     * 
     * @return Tipo de letra preferido, puede ser null si no se especificó
     */
    public String getTipoLetra() {
        return tipoLetra;
    }

    /**
     * Establece la preferencia de tipo de letra del trabajador.
     * 
     * <p>
     * Esta configuración se utiliza para personalizar la apariencia
     * de la interfaz de usuario según las preferencias del trabajador.
     * </p>
     * 
     * @param tipoLetra Tipo de letra a establecer (puede ser null)
     */
    public void setTipoLetra(String tipoLetra) {
        this.tipoLetra = tipoLetra;
    }

    /**
     * Obtiene la preferencia de color de fondo del trabajador.
     * 
     * @return Color de fondo preferido, puede ser null si no se especificó
     */
    public String getColorFondo() {
        return colorFondo;
    }

    /**
     * Establece la preferencia de color de fondo del trabajador.
     * 
     * <p>
     * Esta configuración se utiliza para personalizar la apariencia
     * de la interfaz de usuario según las preferencias del trabajador.
     * </p>
     * 
     * @param colorFondo Color de fondo a establecer (puede ser null)
     */
    public void setColorFondo(String colorFondo) {
        this.colorFondo = colorFondo;
    }

    /**
     * Obtiene la contraseña del trabajador.
     * 
     * @return Contraseña del trabajador
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña del trabajador.
     * 
     * @param password Contraseña del trabajador a establecer
     */
    public void setPassword(String password) {
        this.password = password;
    }
}