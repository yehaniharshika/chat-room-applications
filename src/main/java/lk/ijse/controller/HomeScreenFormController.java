package lk.ijse.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class HomeScreenFormController {

    @FXML
    private JFXButton btnLogin;

    @FXML
    private ImageView imgUser;

    @FXML
    private AnchorPane root;

    @FXML
    private TextField textUserName;
    public  static String clientName;
    public static ArrayList<ClientHandler> clientsList;
    private ServerSocket serverSocket;
    public static String username;
    public ClientHandler clientHandler;


    public HomeScreenFormController() {
        clientsList = new ArrayList<>();
    }

    public void initialize() throws IOException {

        serverSocket = new ServerSocket(1800);
        System.out.println("call initialize");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!serverSocket.isClosed()) {
                        Socket socket = serverSocket.accept();
                        System.out.println("client socket accepted");
                        clientHandler = new ClientHandler(socket);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @FXML
    void btnLoginOnAction(ActionEvent event) {

        username = textUserName.getText();
        textUserName.clear();

        Stage primaryStage = new Stage();
        if (username.isEmpty()){
            new Alert(Alert.AlertType.INFORMATION,"please enter your username !!!").show();
        }else {
            try {
                primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/Client_form.fxml"))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            primaryStage.setTitle(username);
            primaryStage.centerOnScreen();
            primaryStage.show();
            primaryStage.setOnCloseRequest(Event ->{
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("confirmation");
                alert.setContentText("Are you sure you want to exit the chatroom?");

                ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
                if (result == ButtonType.OK){
                    primaryStage.close();
                }
                else {
                    event.consume();
                }
            });
        }


    }


}
