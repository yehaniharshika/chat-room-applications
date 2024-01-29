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
import javafx.scene.input.MouseEvent;
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
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

//import static lk.ijse.controller.HomeScreenFormController.clientName;

public class ClientFormController {

    @FXML
    public  ImageView iconCamera;

    @FXML
    public ImageView iconCat;

    @FXML
    public Label lblClientName;

    @FXML
    private AnchorPane emojiPane;

    @FXML
    public AnchorPane pane;

    @FXML
    public ScrollPane scrollpane;

    @FXML
    public TextField txtMessage;
    public ClientHandler clientHandler;

    @FXML
    public VBox vBox;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String name;

    @Setter
    HomeScreenFormController homeScreenFormController;
    public String receivingMsg;

    public void initialize() {

        name=HomeScreenFormController.username;
        lblClientName.setText(name+ "'s chat");

        try{
            socket = new Socket("localhost",1800);
            System.out.println("client connected");

            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(name);
            dataOutputStream.flush();

            listenMessage();

        }catch (IOException e){
            e.printStackTrace();
        }


        this.vBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                scrollpane.setVvalue((Double) newValue);
            }
        });


    }

    public void listenMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()){

                    try {
                        String receivingMsg = dataInputStream.readUTF();
                        receivedMessage(receivingMsg,ClientFormController.this.vBox);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //handle receive message as image or as text message
    private void receivedMessage(String receivingMsg, VBox vBox) {
        System.out.println(receivingMsg);
        if (receivingMsg.startsWith("image is message")){
            receiveImageMessage(receivingMsg);
        }else{
            receiveTypeMessage(receivingMsg,vBox);
        }
    }

    private void receiveTypeMessage(String receivingMsg, VBox vBox) {

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5,5,5,10));

        HBox hBoxName = new HBox();
        hBoxName.setAlignment(Pos.CENTER_LEFT);
        Text textName = new Text(name);
        TextFlow textFlow1 = new TextFlow(textName);
        hBoxName.getChildren().add(textFlow1);

        Text text = new Text(receivingMsg);
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

    private void receiveImageMessage(String receivingMsg) {
        try {
            String name = receivingMsg.split("-")[1];
            //The dataInputStream.readInt() method reads the length of the image data.
            int imageDataLength = dataInputStream.readInt();
            byte[] imageData = new byte[imageDataLength];
            dataInputStream.readFully(imageData);
            saveImageToFile(imageData);
            displayReceiveImage(imageData,name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void displayReceiveImage(byte[] imageData, String name) {
        Image image = new Image(new ByteArrayInputStream(imageData));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        HBox hBox = new HBox(imageView);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5,5,0,10));

        HBox hBoxName =  new HBox();
        hBoxName.setAlignment(Pos.CENTER_LEFT);
        Text textName = new Text(" "+name+": ");
        TextFlow textFlow = new TextFlow(textName);
        textFlow.setStyle("-fx-background-color: #abb8c3; -fx-font-weight: bold; -fx-background-radius: 20px");
        hBoxName.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBox.getChildren().add(hBoxName);
                vBox.getChildren().add(hBox);
            }
        });
    }

    private void saveImageToFile(byte[] imageData) {
        try {
            // Save the image data to a temporary file (you can customize the file path)
            Path imagePath = Files.createTempFile("received_image",".png");
            Files.write(imagePath,imageData, StandardOpenOption.CREATE,StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
                text.setStyle("-fx-font-size: 14");
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
                time.setStyle("-fx-font-size: 10");

                hBoxTime.getChildren().add(time);

                vBox.getChildren().add(hBox);
                vBox.getChildren().add(hBoxTime);


                try {
                    dataOutputStream.writeUTF(name + " : " + msgToSend);
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
        fileChooser.setTitle("choose an image file");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("image files","*.png", "*.jpg", "*.jpeg","*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null){
            sendImageToServer(selectedFile);
        }
    }

    private void sendImageToServer(File selectedFile) {
        // Read the image file into a byte array
        byte[] imageData = new byte[0];
        try {
            imageData = Files.readAllBytes(selectedFile.toPath());
            // Encode the image data as Base64 for sending
            //String base64Image = Base64.getEncoder().encodeToString(imageData);
            // Send the image to the server
            dataOutputStream.writeUTF("image is message"+"-"+getClientName());
            dataOutputStream.flush();
            dataOutputStream.writeInt(imageData.length);
            dataOutputStream.flush();
            dataOutputStream.write(imageData);
            dataOutputStream.flush();

            // Display the image in the UI
            displayImage(imageData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private void displayImage(byte[] imageData) {
        Image image = new Image(new ByteArrayInputStream(imageData));
        //create an ImageView with the Image.
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        HBox hBox = new HBox(imageView);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(5,5,0,10));
        // hBox.setStyle("-fx-background-color: #7164cb; -fx-font-weight: bold; -fx-color: white; -fx-background-radius: 20px");

        HBox hBoximg = new HBox();
        hBoximg.setAlignment(Pos.CENTER_RIGHT);
        hBoximg.setPadding(new Insets(5,5,0,10));
        String stringTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH.mm"));
        Text time  = new Text(stringTime);
        time.setStyle("-fx-font-size: 10");

        hBoximg.getChildren().add(time);
        vBox.getChildren().add(hBox);
        vBox.getChildren().add(hBoximg);
    }

    private String getClientName() {
        return name;
    }


    public void btnImojiOnAction(ActionEvent actionEvent) {
        emojiPane.setVisible(!emojiPane.isVisible());
    }
    @FXML
    void txtMessageOnAction(ActionEvent event) {
        btnSendOnAction(event);
    }

    public void grinningFaceEmojiOnAction(MouseEvent mouseEvent) {
        txtMessage.appendText("\uD83D\uDE00");
        emojiPane.setVisible(false);
    }

    public void smilingFaceWithOpenHandsOnAction(MouseEvent mouseEvent) {
        txtMessage.appendText("\uD83E\uDD17");
        emojiPane.setVisible(false);

    }

    public void grinningFaceWithSweatOnAction(MouseEvent mouseEvent) {
        txtMessage.appendText("\uD83E\uDD17");
        emojiPane.setVisible(false);
    }

    public void faceWithTearsOfJoyOnAction(MouseEvent mouseEvent) {
        txtMessage.appendText("\uD83D\uDE02");
        emojiPane.setVisible(false);
    }

    public void cryingFaceOnAction(MouseEvent mouseEvent) {
        txtMessage.appendText("\uD83D\uDE22");
        emojiPane.setVisible(false);
    }

    public void sunglassesFaceOnAction(MouseEvent mouseEvent) {
        txtMessage.appendText("\uD83D\uDE0E");
        emojiPane.setVisible(false);
    }

    public void grinningSquintingOnAction(MouseEvent mouseEvent){
        txtMessage.appendText("\uD83D\uDE06");
        emojiPane.setVisible(false);
    }

    public void smilingFaceWithHeartEyesOnAction(MouseEvent mouseEvent) {
        txtMessage.appendText("\uD83D\uDE0D");
        emojiPane.setVisible(false);
    }

    public void smilingFaceWithHornsOnAction(MouseEvent mouseEvent) {
        txtMessage.appendText("\uD83D\uDE08");
        emojiPane.setVisible(false);
    }

    public void thumbsUpOnAction(MouseEvent mouseEvent) {
        txtMessage.appendText("\uD83D\uDC4D");
        emojiPane.setVisible(false);
    }
}


