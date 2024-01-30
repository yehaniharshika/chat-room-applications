package lk.ijse.controller;

import javafx.animation.ScaleTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OpenWindowFormController implements Initializable {

    @FXML
    private ImageView iconCat;

    @FXML
    private ProgressIndicator loadingBar;

    @FXML
    private AnchorPane window;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ScaleTransition zoomIn = new ScaleTransition(Duration.seconds(1.5), iconCat);
        zoomIn.setFromX(1.0);
        zoomIn.setFromY(1.0);
        zoomIn.setToX(1.5);
        zoomIn.setToY(1.5);
        zoomIn.play();

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i <= 100; i++) {
                    updateProgress(i, 55);
                    Thread.sleep(40);
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            try {
                Parent loginParent = FXMLLoader.load(getClass().getResource("/view/HomeScreen_form.fxml"));
                Scene loginScene = new Scene(loginParent);
                Stage loginStage = new Stage();
                loginStage.setResizable(false);
                loginStage.setTitle("Chatwise Live chat Application");
                loginStage.setScene(loginScene);
                loginStage.show();

                // Close the welcome stage
                ((Stage) loadingBar.getScene().getWindow()).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        new Thread(task).start();
    }

}

