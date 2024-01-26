package lk.ijse.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lk.ijse.client.ClientHandler;
import lombok.Setter;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static lk.ijse.controller.HomeScreenFormController.clientName;

//import static lk.ijse.controller.HomeScreenFormController.clientName;

public class ClientFormController {

    @FXML
    public  ImageView iconCamera;

    @FXML
    public ImageView iconCat;

    @FXML
    public Label lblClientName;

    @FXML
    public AnchorPane pane;

    @FXML
    public ScrollPane scrollpane;

    @FXML
    public TextField txtMessage;
    public ClientHandler clientHandler;

    @FXML
    public VBox vBox;
   // private Client client;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;


//    private String clientName = "";
   /* private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;*/

    @Setter
    HomeScreenFormController homeScreenFormController;
    public String receivingMsg;

    public void initialize() {
        lblClientName.setText(clientName);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    socket = new Socket("localhost",1400);
                    System.out.println("client connected");

                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    while (socket.isConnected()){
                        String receivingMsg = dataInputStream.readUTF();
                        receivedMessage(receivingMsg,ClientFormController.this.vBox);
                    }

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();

        this.vBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                scrollpane.setVvalue((Double) newValue);
            }
        });


    }

    //handle receive message
    private void receivedMessage(String receivingMsg, VBox vBox) {
        String name = receivingMsg.split("-")[0];
        String msgFromServer = receivingMsg.split("-")[1];

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5,5,5,10));

        HBox hBoxName = new HBox();
        hBoxName.setAlignment(Pos.CENTER_LEFT);
        Text textName = new Text(name);
        TextFlow textFlow1 = new TextFlow(textName);
        hBoxName.getChildren().add(textFlow1);

        Text text = new Text(msgFromServer);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: #abb8c3; -fx-font-weight: bold; -fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5,10,5,10));
        text.setFill(Color.color(0,0,0));

        hBox.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBox.getChildren().add(hBox);

            }
        });


    }




    /*public void setImage(byte[] bytes , String  sender){
        HBox hBox = new HBox();
        Label msgLabel = new Label(sender);
        msgLabel.setStyle("-fx-background-color:   #B53471;-fx-background-radius:15;-fx-font-size: 18;-fx-font-weight: normal;-fx-text-fill: white;-fx-wrap-text: true;-fx-alignment: center;-fx-content-display: left;-fx-padding: 10;-fx-max-width: 350;");
        //hBox.setStyle("-fx-fill-height: true; -fx-min-height: 50; -fx-pref-width: 520; -fx-max-width: 520; -fx-padding: 10; " + (sender.equals(client.getClass()) ? "-fx-alignment: center-right;" : "-fx-alignment: center-left;"));

        Platform.runLater(() ->{
            ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(bytes)));
            imageView.setStyle("-fx-padding: 10px;");
            imageView.setFitHeight(200);
            imageView.setFitWidth(120);

            hBox.getChildren().addAll(msgLabel,imageView);
            vBox.getChildren().add(hBox);
        });

    }*/


    @FXML
    void btnSendOnAction(ActionEvent event) {
        sendMsg(txtMessage.getText());


    }
    private void sendMsg(String msgToSend) {
        if (!msgToSend.isEmpty()){
            if (!msgToSend.matches(".*\\.(png|jpe?g|gif)$")){

                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_RIGHT);
                hBox.setPadding(new Insets(5, 5, 5, 10));

                Text text = new Text(msgToSend);
                text.setStyle("-fx-font-size: 15");
                TextFlow textFlow = new TextFlow(text);

                textFlow.setStyle("-fx-background-color: #0693e3; -fx-font-weight: bold; -fx-color: white; -fx-background-radius: 20px");
                textFlow.setPadding(new Insets(5, 10, 5, 10));
                text.setFill(Color.color(1, 1, 1));

                hBox.getChildren().add(textFlow);

                HBox hBoxTime = new HBox();
                hBoxTime.setAlignment(Pos.CENTER_RIGHT);
                hBoxTime.setPadding(new Insets(0, 5, 5, 10));
                String stringTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                Text time = new Text(stringTime);
                time.setStyle("-fx-font-size: 8");

                hBoxTime.getChildren().add(time);

                vBox.getChildren().add(hBox);
                vBox.getChildren().add(hBoxTime);


                try {
                    dataOutputStream.writeUTF(clientName + "-" + msgToSend);
                    dataOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                txtMessage.clear();
            }
        }
    }


    @FXML
    void btnCameraIconOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("select image");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("image files","*.png", "*.jpg", "*.jpeg","*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null){
            try{
                byte[] bytes = Files.readAllBytes(selectedFile.toPath());
                HBox hBox = new HBox();
                hBox.setStyle("-fx-fill-height: true; -fx-min-height: 50; -fx-pref-width: 520; -fx-max-width: 520; -fx-padding: 10; -fx-alignment: center-right;");

                // Display the image in an ImageView or any other UI component
                ImageView imageView = new ImageView(new Image(new FileInputStream(selectedFile)));
                imageView.setStyle("-fx-padding: 10px;");
                imageView.setFitHeight(200);
                imageView.setFitWidth(120);

                hBox.getChildren().addAll(imageView);
                vBox.getChildren().add(hBox);

                clientHandler.sendImage(clientName,bytes);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void btnImojiOnAction(ActionEvent event) {

    }




    @FXML
    void txtMessageOnAction(ActionEvent event) {
        btnSendOnAction(event);
    }



}


