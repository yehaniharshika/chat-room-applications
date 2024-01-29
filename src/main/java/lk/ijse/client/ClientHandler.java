package lk.ijse.client;

import lk.ijse.controller.HomeScreenFormController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
    private Socket socket;
    private static List<ClientHandler> clientsList = new ArrayList<>();
    public static HomeScreenFormController homeScreenFormController;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String message = "";
    public  String clientName;
    public  String sender;

    public ClientHandler(Socket socket){
        try {
            this.socket = socket;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            clientName = dataInputStream.readUTF();
            clientsList.add(this);
            System.out.println("Server: "+ clientName +" has enter the live chat");
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (socket.isConnected()) {
                    try{
                        String messageFromClient = dataInputStream.readUTF();
                        System.out.println(messageFromClient);

                        String[] message = messageFromClient.split(" : ");
                        sender = message[0];

                        if (messageFromClient.startsWith("image is message")){
                            System.out.println("image is message");
                            String[] imgSender = messageFromClient.split("-");
                            sender = imgSender[1];
                            //for handling Image as message
                            handleImageMessage(messageFromClient);

                        }else{
                            //broadcast to each user
                            broadcastMessage(messageFromClient);
                        }

                    }catch (IOException e){
                        clientsList.remove(this);
                        System.out.println("server "+clientName + " has left the chat");
                        break;
                    }
                }

            }
        }).start();
    }

    private void handleImageMessage(String imgMessage) {
        try {
            int imageDataLength = dataInputStream.readInt();
            byte[] imageData = new byte[imageDataLength];
            dataInputStream.readFully(imageData);
            //broadcast the image to other clients
            broadcastImage(imageData,imgMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void broadcastImage(byte[] imageData, String imgMessage) {
        String clientName = imgMessage.split("-")[1];
        for (ClientHandler clientHandler : clientsList){
            if (!clientHandler.clientName.equals(sender)){
                try {
                    clientHandler.dataOutputStream.writeUTF("image is message"+"-"+clientName);
                    clientHandler.dataOutputStream.flush();
                    clientHandler.dataOutputStream.writeInt(imageData.length);
                    clientHandler.dataOutputStream.flush();
                    clientHandler.dataOutputStream.write(imageData);
                    clientHandler.dataOutputStream.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void broadcastMessage(String messageFroClient) throws IOException {
        for (ClientHandler clientHandler : clientsList) {
            if (!clientHandler.clientName.equals(sender)) {
                clientHandler.dataOutputStream.writeUTF(messageFroClient);
                clientHandler.dataOutputStream.flush();
            }
        }
    }
}

