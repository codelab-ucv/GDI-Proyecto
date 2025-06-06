package ucv.codelab.util;

import java.io.File;
import java.sql.SQLException;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import ucv.codelab.model.Empresa;
import ucv.codelab.model.Trabajador;
import ucv.codelab.repository.EmpresaRepository;

public class Personalizacion {

    private static Empresa empresaActual;
    private static final String LOGO_EMPRESA_ORIGINAL = "ucv/codelab/img/logo_inicial.png";

    private static Trabajador trabajadorActual;
    private static final String TIPO_LETRA_ORIGINAL = "-fx-font-family: 'System';";
    private static final String TAMANO_LETRA_ORIGINAL = "-fx-font-size: 12px;";
    public static final String COLOR_FONDO_ORIGINAL = "-fx-background: #f4f4f4;";

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

    public static String getColorFondo() {
        // Si no se ha iniciado la variable o no hay color de fondo establecido
        if (trabajadorActual == null || trabajadorActual.getColorFondo() == null
                || trabajadorActual.getColorFondo().isEmpty()) {
            return COLOR_FONDO_ORIGINAL;
        }
        return "-fx-background: " + trabajadorActual.getColorFondo() + ";";
    }

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

    public static Color getColor() {
        // Si no se ha iniciado la variable o no hay color de fondo establecido
        if (trabajadorActual == null || trabajadorActual.getColorFondo() == null
                || trabajadorActual.getColorFondo().isEmpty()) {
            return Color.web("#f4f4f4");
        }
        return Color.web(trabajadorActual.getColorFondo());
    }
}
