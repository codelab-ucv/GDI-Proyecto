package ucv.codelab.util;

import java.io.File;
import java.sql.SQLException;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import ucv.codelab.model.Empresa;
import ucv.codelab.model.Trabajador;
import ucv.codelab.repository.EmpresaRepository;

/**
 * Clase utilitaria para la gestión de personalización de la aplicación.
 * 
 * <p>
 * Esta clase proporciona métodos estáticos para gestionar la personalización
 * visual de la aplicación, incluyendo configuraciones de empresa y trabajador
 * como logos, colores de fondo, tipos de letra y tamaños de fuente.
 * </p>
 * 
 * <p>
 * La clase mantiene referencias estáticas a la empresa y trabajador actuales,
 * permitiendo acceso global a las configuraciones de personalización a través
 * de toda la aplicación.
 * </p>
 * 
 * <p>
 * <strong>Configuraciones disponibles:</strong>
 * </p>
 * <ul>
 * <li>Logo de la empresa</li>
 * <li>Información de la empresa actual</li>
 * <li>Color de fondo personalizable</li>
 * <li>Tipo y tamaño de fuente</li>
 * <li>Trabajador actual con sus preferencias</li>
 * </ul>
 */
public class Personalizacion {

    private static Empresa empresaActual;
    private static final String LOGO_EMPRESA_ORIGINAL = "ucv/codelab/img/logo_inicial.png";

    private static Trabajador trabajadorActual;
    private static final String TIPO_LETRA_ORIGINAL = "-fx-font-family: 'System';";
    private static final String TAMANO_LETRA_ORIGINAL = "-fx-font-size: 12px;";
    public static final String COLOR_FONDO_ORIGINAL = "-fx-background: #f4f4f4;";

    /**
     * Obtiene la empresa actualmente guardada en la base de datos.
     * 
     * <p>
     * Si no hay una empresa cargada previamente, intenta obtener la última
     * empresa registrada desde la base de datos. En caso de error, retorna
     * una empresa con datos por defecto.
     * </p>
     * 
     * @return Entidad de la Empresa actual, retornará datos por defecto en caso
     *         de presentarse un error al acceder a la base de datos
     */
    public static Empresa getEmpresaActual() {
        if (empresaActual == null) {
            try {
                empresaActual = new EmpresaRepository().getLastId();
            } catch (SQLException e) {
                empresaActual = new Empresa("GDI", "20123456789");
            }
        }
        // Si ocurre algun error devolverá valores por defecto
        return empresaActual;
    }

    /**
     * Establece la empresa actual para la personalización de la aplicación.
     * 
     * @param empresaActual La empresa a establecer como actual
     */
    public static void setEmpresaActual(Empresa empresaActual) {
        Personalizacion.empresaActual = empresaActual;
    }

    /**
     * Obtiene el logo de la empresa actual.
     * 
     * <p>
     * Intenta cargar el logo personalizado de la empresa desde la ruta
     * especificada. Si la empresa no está cargada, la obtiene primero.
     * En caso de error al cargar el logo personalizado, retorna el logo
     * por defecto de la aplicación.
     * </p>
     * 
     * @return Imagen del logo de la empresa, o logo por defecto si hay error
     */
    public static Image getLogo() {
        if (empresaActual == null)
            getEmpresaActual();

        Image image;
        try {
            // Obtiene el URI en base a la ruta indicada del archivo
            String uri = new File(empresaActual.getLogo()).toURI().toString();
            // Actualiza la imagen usada
            image = new Image(uri);
        } catch (Exception e) {
            System.err.println("Error al cargar el logo personalizado: " + e.getMessage());
            image = new Image(Personalizacion.LOGO_EMPRESA_ORIGINAL);
        }
        return image;
    }

    /**
     * Obtiene el trabajador actualmente configurado en la aplicación.
     * 
     * @return El trabajador actual, o null si no hay ninguno configurado
     */
    public static Trabajador getTrabajadorActual() {
        return trabajadorActual;
    }

    /**
     * Establece el trabajador actual para la personalización de la aplicación.
     * 
     * @param trabajadorActual El trabajador a establecer como actual
     */
    public static void setTrabajadorActual(Trabajador trabajadorActual) {
        Personalizacion.trabajadorActual = trabajadorActual;
    }

    /**
     * Obtiene el estilo CSS del color de fondo configurado por el trabajador.
     * 
     * <p>
     * Si no hay trabajador configurado o no tiene color de fondo establecido,
     * retorna el color de fondo por defecto de la aplicación.
     * </p>
     * 
     * @return String con el estilo CSS del color de fondo en formato JavaFX
     */
    public static String getColorFondo() {
        // Si no se ha iniciado la variable o no hay color de fondo establecido
        if (trabajadorActual == null || trabajadorActual.getColorFondo() == null
                || trabajadorActual.getColorFondo().isEmpty()) {
            return COLOR_FONDO_ORIGINAL;
        }
        return "-fx-background: " + trabajadorActual.getColorFondo() + ";";
    }

    /**
     * Obtiene el estilo CSS del tipo de letra configurado por el trabajador.
     * 
     * <p>
     * Extrae el tipo de fuente de la configuración del trabajador que está
     * almacenada en formato "tipo/tamaño". Si no hay configuración disponible,
     * retorna el tipo de letra por defecto.
     * </p>
     * 
     * @return String con el estilo CSS del tipo de letra en formato JavaFX
     */
    public static String getTipoLetra() {
        // Si no se ha iniciado la variable o no hay tipo de letra establecido
        if (trabajadorActual == null || trabajadorActual.getTipoLetra() == null
                || trabajadorActual.getTipoLetra().isEmpty()) {
            return TIPO_LETRA_ORIGINAL;
        }
        // Separa la cadena de texto del patron tipo/tamaño
        String[] tipoLetra = trabajadorActual.getTipoLetra().split("/");
        // No se hace más validaciones ya que solo se puede cambiar la fuente segun las
        // opciones colocadas
        return "-fx-font-family: '" + tipoLetra[0] + "';";
    }

    /**
     * Obtiene el estilo CSS del tamaño de letra configurado por el trabajador.
     * 
     * <p>
     * Extrae el tamaño de fuente de la configuración del trabajador que está
     * almacenada en formato "tipo/tamaño". Si no hay configuración disponible,
     * retorna el tamaño de letra por defecto.
     * </p>
     * 
     * @return String con el estilo CSS del tamaño de letra en formato JavaFX
     */
    public static String getTamanoLetra() {
        // Si no se ha iniciado la variable o no hay tamaño de letra establecido
        if (trabajadorActual == null || trabajadorActual.getTipoLetra() == null
                || trabajadorActual.getTipoLetra().isEmpty()) {
            return TAMANO_LETRA_ORIGINAL;
        }
        // Separa la cadena de texto del patron tipo/tamaño
        String[] tamanoLetra = trabajadorActual.getTipoLetra().split("/");
        // No hace mas validaciones ya que siempre se actualiza desde la interfaz
        return "-fx-font-size: " + tamanoLetra[1] + "px;";
    }

    /**
     * Obtiene el nombre de la fuente configurada por el trabajador.
     * 
     * <p>
     * Extrae únicamente el nombre de la fuente de la configuración del trabajador
     * sin los estilos CSS adicionales. Útil para configuraciones que requieren
     * solo el nombre de la fuente.
     * </p>
     * 
     * @return Nombre de la fuente configurada, o "System" si no hay configuración
     */
    public static String getFuente() {
        if (trabajadorActual == null || trabajadorActual.getTipoLetra() == null
                || trabajadorActual.getTipoLetra().isEmpty()) {
            return "System"; // Fuente por defecto
        }
        // Separa la cadena de texto del patron tipo/tamaño
        String[] tamanoLetra = trabajadorActual.getTipoLetra().split("/");
        // Devuelve la fuente
        return tamanoLetra[0];
    }

    /**
     * Obtiene el tamaño de letra configurado por el trabajador como entero.
     * 
     * <p>
     * Extrae y convierte a entero el tamaño de fuente de la configuración
     * del trabajador. Útil para configuraciones que requieren el valor
     * numérico del tamaño.
     * </p>
     * 
     * @return Tamaño de la fuente en píxeles, o 12 si no hay configuración
     */
    public static int getTamano() {
        if (trabajadorActual == null || trabajadorActual.getTipoLetra() == null
                || trabajadorActual.getTipoLetra().isEmpty()) {
            return 12; // Tamaño de letra por defecto
        }
        // Separa la cadena de texto del patron tipo/tamaño
        String[] tamanoLetra = trabajadorActual.getTipoLetra().split("/");
        // Parsea el tamaño a Integer
        return Integer.parseInt(tamanoLetra[1]);
    }

    /**
     * Obtiene el color de fondo configurado como objeto Color de JavaFX.
     * 
     * <p>
     * Convierte el color de fondo almacenado como string hexadecimal
     * a un objeto Color de JavaFX. Útil para componentes que requieren
     * objetos Color en lugar de estilos CSS.
     * </p>
     * 
     * @return Color de fondo como objeto Color, o color por defecto si no hay
     *         configuración
     */
    public static Color getColor() {
        // Si no se ha iniciado la variable o no hay color de fondo establecido
        if (trabajadorActual == null || trabajadorActual.getColorFondo() == null
                || trabajadorActual.getColorFondo().isEmpty()) {
            return Color.web("#f4f4f4");
        }
        return Color.web(trabajadorActual.getColorFondo());
    }
}