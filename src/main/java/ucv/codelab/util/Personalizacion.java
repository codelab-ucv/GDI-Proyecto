package ucv.codelab.util;

import java.io.File;
import java.sql.SQLException;

import javafx.scene.image.Image;
import ucv.codelab.model.Empresa;
import ucv.codelab.model.Trabajador;
import ucv.codelab.repository.EmpresaRepository;

public class Personalizacion {

    private static Empresa empresaActual;
    private static final String LOGO_EMPRESA_ORIGINAL = "ucv/codelab/img/logo_inicial.png";

    private static Trabajador trabajadorActual;
    private static final String TIPO_LETRA_ORIGINAL = "-fx-font-family: 'System'; -fx-font-size: 12px;";
    public static final String COLOR_FONDO_ORIGINAL = "-fx-background: #f4f4f4";

    /**
     * Obtiene la empresa actualmente guardada en la base de datos
     * 
     * @return Entidad de la Empresa actual, retornará datos por default en caso se
     *         presente un error
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

    public static void setEmpresaActual(Empresa empresaActual) {
        Personalizacion.empresaActual = empresaActual;
    }

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

    public static Trabajador getTrabajadorActual() {
        return trabajadorActual;
    }

    public static void setTrabajadorActual(Trabajador trabajadorActual) {
        Personalizacion.trabajadorActual = trabajadorActual;
    }

    public static String getTipoLetra() {
        // Si no se ha iniciado la variable o no hay tipo de letra establecido
        if (trabajadorActual == null || trabajadorActual.getTipoLetra() == null
                || trabajadorActual.getTipoLetra().isEmpty()) {
            return TIPO_LETRA_ORIGINAL;
        }
        // No se hace más validaciones ya que solo se puede cambiar la fuente segun las
        // opciones colocadas
        return trabajadorActual.getTipoLetra();
    }

    public static String getColorFondo() {
        // Si no se ha iniciado la variable o no hay color de fondo establecido
        if (trabajadorActual == null || trabajadorActual.getColorFondo() == null
                || trabajadorActual.getColorFondo().isEmpty()) {
            return COLOR_FONDO_ORIGINAL;
        }
        return "-fx-background: " + trabajadorActual.getColorFondo();
    }
}
