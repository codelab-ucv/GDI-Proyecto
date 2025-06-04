package ucv.codelab;

import java.io.IOException;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ucv.codelab.model.Empresa;
import ucv.codelab.repository.EmpresaRepository;
import ucv.codelab.util.DatabaseInitializer;
import ucv.codelab.util.Personalizacion;

public class Main extends Application {

    private static Scene scene;

    private static Empresa empresaActual;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("Principal"), 1300, 780);

        // Usa los estilos base del programa
        scene.getRoot().setStyle(Personalizacion.TIPO_LETRA_ORIGINAL
                + Personalizacion.COLOR_FONDO_ORIGINAL);

        stage.setScene(scene);
        stage.setTitle("GDI");
        stage.setMinWidth(1300);
        stage.setMinHeight(780);
        stage.getIcons().add(new Image("ucv/codelab/img/logo_inicial.png"));
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Main.class.getResource("/ucv/codelab/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        // Inicia la base de datos
        DatabaseInitializer.initializeDatabase();

        // Descarga los datos de la empresa, en caso no se pueda realizar la conexion
        // retorna
        try {
            empresaActual = new EmpresaRepository().getLastId();
        } catch (SQLException e) {
            return;
        }

        // Actualiza los datos personalizables de la empresa
        if (empresaActual.getLogo() != null) {
            Personalizacion.LOGO_EMPRESA_PERSONALIZADO = empresaActual.getLogo();
        }

        launch();
    }

    public static Empresa getEmpresaActual() {
        return empresaActual;
    }

    public static void setEmpresaActual(Empresa empresaActual) {
        Main.empresaActual = empresaActual;
    }
}