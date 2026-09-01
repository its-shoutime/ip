import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import kiwi.Kiwi;

/**
 * A GUI for Kiwi using FXML.
 */
public class Main extends Application {

    private Kiwi kiwi = new Kiwi(Kiwi.DEFAULT_FILE_PATH);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setKiwi(kiwi);  // inject the Kiwi instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
