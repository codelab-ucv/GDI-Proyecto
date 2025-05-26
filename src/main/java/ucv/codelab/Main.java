package ucv.codelab;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ucv.codelab.util.DatabaseInitializer;

public class Main extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("principal"), 1280, 750);
        stage.setScene(scene);
        stage.setTitle("GDI");
        stage.setMinWidth(1280);
        stage.setMinHeight(750);
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
        DatabaseInitializer.initializeDatabase();
        launch();
    }
}