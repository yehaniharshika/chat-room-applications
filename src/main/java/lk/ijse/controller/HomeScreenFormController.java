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


    public HomeScreenFormController() {
        clientsList = new ArrayList<>();
    }

    public void initialize() throws IOException {

//        startServer();

        serverSocket = new ServerSocket(1400);
        System.out.println("call initialize");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(socket,clientsList);
                    clientsList.add(clientHandler);
                    System.out.println("client socket accepted");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /*public void makeSocket(){
        while (!serverSocket.isClosed()){
            try {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket,clientsList);
                clientsList.add(clientHandler);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    @FXML
    void btnLoginOnAction(ActionEvent event) {

        String username = textUserName.getText();
        textUserName.clear();

        Stage primaryStage = new Stage();

        try {
            primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/Client_form.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setTitle(textUserName.getText()+"join the chat");
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
