package ucv.codelab.model;

/**
 * Representa una empresa en el sistema de gestión de órdenes.
 * 
 * <p>
 * Esta clase modela la información de las empresas que participan en el sistema
 * de gestión de órdenes. Cada empresa se identifica únicamente por la
 * combinación
 * de su nombre y RUC (Registro Único de Contribuyente).
 * </p>
 * 
 * <p>
 * <strong>Tabla asociada:</strong> {@code empresa}
 * </p>
 * 
 * <p>
 * <strong>Campos de la tabla:</strong>
 * </p>
 * <ul>
 * <li>{@code id_empresa} - Clave primaria autoincremental</li>
 * <li>{@code nombre_empresa} - Nombre de la empresa</li>
 * <li>{@code ruc} - Registro Único de Contribuyente</li>
 * <li>{@code email_empresa} - Correo electrónico de la empresa (opcional)</li>
 * <li>{@code ubicacion} - Dirección física de la empresa (opcional)</li>
 * <li>{@code logo} - Ruta o referencia al logotipo de la empresa
 * (opcional)</li>
 * </ul>
 * 
 * <p>
 * <strong>Restricciones:</strong>
 * </p>
 * <ul>
 * <li>La combinación de nombre_empresa + ruc debe ser única</li>
 * <li>El sistema incluye una empresa por defecto "GDI" con RUC
 * "20123456789"</li>
 * </ul>
 * 
 * <p>
 * <strong>Relaciones:</strong>
 * </p>
 * <ul>
 * <li>Una empresa puede estar asociada a múltiples órdenes</li>
 * <li>Cada orden pertenece a una única empresa</li>
 * </ul>
 * 
 * @see ucv.codelab.repository.EmpresaRepository
 */
public class Empresa {

    /**
     * Identificador único de la empresa en la base de datos.
     * Corresponde a la clave primaria autoincremental.
     */
    private int idEmpresa;

    /**
     * Nombre de la empresa.
     * Campo requerido que forma parte de la clave única junto con el RUC.
     */
    private String nombreEmpresa;

    /**
     * Registro Único de Contribuyente de la empresa.
     * Campo requerido que forma parte de la clave única junto con el nombre.
     */
    private String ruc;

    /**
     * Correo electrónico de contacto de la empresa.
     * Campo opcional para comunicación empresarial.
     */
    private String emailEmpresa;

    /**
     * Dirección física o ubicación de la empresa.
     * Campo opcional que describe la localización de la empresa.
     */
    private String ubicacion;

    /**
     * Ruta o referencia al logotipo de la empresa.
     * Campo opcional que puede contener la ruta del archivo de imagen o URL del
     * logo.
     */
    private String logo;

    /**
     * Constructor simplificado para crear una nueva empresa con datos básicos.
     * 
     * <p>
     * Este constructor es útil para crear empresas nuevas antes de insertarlas
     * en la base de datos, proporcionando solo los campos obligatorios.
     * Los campos opcionales se inicializan como null y el ID se establece como -1
     * hasta que la empresa sea persistida en la base de datos.
     * </p>
     * 
     * @param nombreEmpresa Nombre de la empresa
     * @param ruc           RUC de la empresa
     */
    public Empresa(String nombreEmpresa, String ruc) {
        this(-1, nombreEmpresa, ruc, null, null, null);
    }

    /**
     * Constructor completo que inicializa una empresa con todos sus datos.
     * 
     * <p>
     * Este constructor se utiliza principalmente al recuperar datos de la
     * base de datos, donde todos los campos están disponibles, o al crear
     * empresas con información completa.
     * </p>
     * 
     * @param idEmpresa     Identificador único de la empresa
     * @param nombreEmpresa Nombre de la empresa
     * @param ruc           RUC de la empresa
     * @param emailEmpresa  Email de contacto (puede ser null)
     * @param ubicacion     Dirección física (puede ser null)
     * @param logo          Ruta del logotipo (puede ser null)
     */
    public Empresa(int idEmpresa, String nombreEmpresa, String ruc, String emailEmpresa, String ubicacion,
            String logo) {
        this.idEmpresa = idEmpresa;
        this.nombreEmpresa = nombreEmpresa;
        this.ruc = ruc;
        this.emailEmpresa = emailEmpresa;
        this.ubicacion = ubicacion;
        this.logo = logo;
    }

    /**
     * Obtiene el identificador único de la empresa.
     * 
     * @return ID de la empresa
     */
    public int getIdEmpresa() {
        return idEmpresa;
    }

    /**
     * Establece el identificador único de la empresa.
     * 
     * <p>
     * Este método se utiliza principalmente después de insertar
     * una nueva empresa en la base de datos para asignar el ID
     * generado automáticamente.
     * </p>
     * 
     * @param idEmpresa ID de la empresa a establecer
     */
    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    /**
     * Obtiene el nombre de la empresa.
     * 
     * @return Nombre de la empresa
     */
    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    /**
     * Establece el nombre de la empresa.
     * 
     * <p>
     * <strong>Importante:</strong> La combinación de nombre + RUC debe ser
     * única en el sistema. Asegúrese de validar la unicidad antes de
     * actualizar este campo.
     * </p>
     * 
     * @param nombreEmpresa Nombre de la empresa a establecer
     */
    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    /**
     * Obtiene el RUC de la empresa.
     * 
     * @return RUC de la empresa
     */
    public String getRuc() {
        return ruc;
    }

    /**
     * Establece el RUC de la empresa.
     * 
     * <p>
     * <strong>Importante:</strong> La combinación de nombre + RUC debe ser
     * única en el sistema. Asegúrese de validar la unicidad antes de
     * actualizar este campo.
     * </p>
     * 
     * @param ruc RUC de la empresa a establecer
     */
    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    /**
     * Obtiene el correo electrónico de la empresa.
     * 
     * @return Email de la empresa, puede ser null si no se proporcionó
     */
    public String getEmailEmpresa() {
        return emailEmpresa;
    }

    /**
     * Establece el correo electrónico de la empresa.
     * 
     * @param emailEmpresa Email de la empresa a establecer (puede ser null)
     */
    public void setEmailEmpresa(String emailEmpresa) {
        this.emailEmpresa = emailEmpresa;
    }

    /**
     * Obtiene la ubicación de la empresa.
     * 
     * @return Ubicación de la empresa, puede ser null si no se proporcionó
     */
    public String getUbicacion() {
        return ubicacion;
    }

    /**
     * Establece la ubicación de la empresa.
     * 
     * @param ubicacion Ubicación de la empresa a establecer (puede ser null)
     */
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    /**
     * Obtiene la ruta del logotipo de la empresa.
     * 
     * @return Ruta del logo, puede ser null si no se proporcionó
     */
    public String getLogo() {
        return logo;
    }

    /**
     * Establece la ruta del logotipo de la empresa.
     * 
     * @param logo Ruta del logotipo a establecer (puede ser null)
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }
}