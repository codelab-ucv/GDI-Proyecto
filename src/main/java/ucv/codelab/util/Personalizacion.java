package ucv.codelab.util;

import java.io.File;
import java.sql.SQLException;

import javafx.scene.image.Image;
import ucv.codelab.model.Empresa;
import ucv.codelab.repository.EmpresaRepository;

public class Personalizacion {
    public static final String TIPO_LETRA_ORIGINAL = "-fx-font-family: 'System'; -fx-font-size: 12px;";
    public static final String COLOR_FONDO_ORIGINAL = "-fx-background: #f4f4f4";

    private static final String LOGO_EMPRESA_ORIGINAL = "ucv/codelab/img/logo_inicial.png";

    public static String TIPO_LETRA_PERSONALIZADO = "";
    public static String COLOR_FONDO_PERSONALIZADO = "";

    private static Empresa empresaActual;

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
}
