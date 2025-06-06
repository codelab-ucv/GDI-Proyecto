package ucv.codelab.util;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class PopUp {

    public static Optional<ButtonType> confirmacion(String titulo, String header, String mensaje) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        setAlertProperties(confirmacion, titulo, header, mensaje);
        return confirmacion.showAndWait();
    }

    public static String inputDialog(String titulo, String mensaje) {
        // Crear el diálogo
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ingresar datos solicitados");
        dialog.setHeaderText(titulo);
        dialog.setContentText(mensaje);

        // Configura el tipo de letra
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getScene().getRoot().setStyle(Personalizacion.getTipoLetra()
                + Personalizacion.getTamanoLetra()
                + Personalizacion.getColorFondo());

        // Intenta usar el icono seleccionado por el usuario
        try {
            stage.getIcons().add(Personalizacion.getLogo());
        }
        // Si ocurre un problema usa el logo original e informa por cmd
        catch (Exception e) {
            System.err.println("Error al cargar el ícono: " + e.getMessage()
                    + "\nSe usaran datos por defecto");
            stage.getIcons().add(Personalizacion.getLogo());
        }

        // Mostrar el diálogo y esperar respuesta
        Optional<String> result = dialog.showAndWait();

        // Procesar la respuesta
        return result.isEmpty() ? "" : result.get();
    }

    /**
     * Muestra un diálogo de información con el título y mensaje especificados.
     * 
     * @param titulo  El título del diálogo
     * @param mensaje El mensaje a mostrar
     */
    public static void informacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        setAlertProperties(alert, "Información", titulo, mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un diálogo de alerta de error con el título y mensaje especificados.
     * 
     * @param titulo  El título del diálogo de error
     * @param mensaje El mensaje detallado del error
     */
    public static void error(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        setAlertProperties(alert, "Error", titulo, mensaje);
        alert.showAndWait();
    }

    private static void setAlertProperties(Alert alert, String title, String header, String content) {
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

        stage.getScene().getRoot().setStyle(Personalizacion.getTipoLetra()
                + Personalizacion.getTamanoLetra()
                + Personalizacion.getColorFondo());

        // Intenta usar el icono seleccionado por el usuario
        try {
            stage.getIcons().add(Personalizacion.getLogo());
        }
        // Si ocurre un problema usa el logo original e informa por cmd
        catch (Exception e) {
            System.err.println("Error al cargar el ícono: " + e.getMessage()
                    + "\nSe usaran datos por defecto");
            stage.getIcons().add(Personalizacion.getLogo());
        }
    }
}
