package lk.ijse;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Launcher extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        AnchorPane rootNode = FXMLLoader.load(this.getClass().getResource("/view/OpenWindow_form.fxml"));
        Scene scene=new Scene(rootNode);
        stage.setScene(scene);
        stage.setTitle("Chatwise - login");
        stage.centerOnScreen();

        stage.show();
    }
}
