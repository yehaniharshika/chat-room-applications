package lk.ijse.client;

import lk.ijse.controller.HomeScreenFormController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ClientHandler {
    private Socket socket;
    private List<ClientHandler> clientsList;
    public static HomeScreenFormController homeScreenFormController;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String message = "";
    public  String clientName;

    public ClientHandler(Socket socket,List<ClientHandler> clientsList){

        try {
            this.socket = socket;
            this.clientsList = clientsList;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
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
                            if (messageFromClient.equals("*image*")){
                                receiveImage();
                            }else{
                                for (ClientHandler clientHandler : clientsList) {
                                    if (!clientHandler.clientName.equals(clientName)) {
                                        clientHandler.broadcastMessage(clientName,messageFromClient);
                                    }
                                }
                            }

                        }catch (IOException e){
                           clientsList.remove(this);
                           System.out.println("server"+clientName+ "has left the chat");
                           break;
                        }

                }


            }
        }).start();

    }

    private void broadcastMessage(String sender , String messageFroClient) throws IOException {
        dataOutputStream.writeUTF(sender + ": "+messageFroClient);
        dataOutputStream.flush();
    }

    private void receiveImage() throws IOException {
        int size = dataInputStream.readInt();
        byte[] bytes = new byte[size];
        dataInputStream.read(bytes);

        for (ClientHandler clientHandler : clientsList){
            if (clientHandler.clientName.equals(clientName)){
                clientHandler.sendImage(clientName,bytes);
            }
        }
    }

    public void sendImage(String clientName, byte[] bytes) throws IOException {
        dataOutputStream.writeUTF("*image*");
        dataOutputStream.writeUTF(clientName);
        dataOutputStream.writeInt(bytes.length);
        dataOutputStream.write(bytes);
        dataOutputStream.flush();
    }








   /* @Override
    public void run() {

        while (socket.isConnected()){
            try {
                message= dataInputStream.readUTF();

                if (message.equals("*image*")){
                    receivedImage();
                }else {
                    for (ClientHandler clientHandler : clientsList){
                        if (clientHandler.socket.getPort() == socket.getPort()){
                            if (clientHandler.clientName.equals(clientName)){
                                clientHandler.sendingMessage(clientName,message);

                            }
                        }else{
                            System.out.println("not equal port");
                        }

                    }
                }
            } catch (IOException e) {
                clientsList.remove(this);
                break;
            }
        }
    }*/

   /* private void receivedImage() throws IOException {
        int size = dataInputStream.readInt();
        byte[] bytes = new byte[size];
        dataInputStream.read(bytes);

        for (ClientHandler clientHandler : clientsList){
            if (clientHandler.clientName.equals(clientName)){
                clientHandler.sendingImage(clientName,bytes);
            }
        }
    }

    private void sendingImage(String clientName, byte[] bytes) throws IOException {
        dataOutputStream.writeUTF("*image*");
        dataOutputStream.writeUTF(clientName);
        dataOutputStream.writeInt(bytes.length);
        dataOutputStream.write(bytes);
        dataOutputStream.flush();
    }

    private void sendingMessage(String clientName, String message) throws IOException {
        dataOutputStream.writeUTF(clientName + ": "+message);
        dataOutputStream.flush();
    }*/

}

