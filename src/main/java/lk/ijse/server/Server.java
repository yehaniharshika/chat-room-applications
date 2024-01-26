package lk.ijse.server;

import lk.ijse.client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private Socket socket;
    private static Server server;
    private List<ClientHandler> clientsList = new ArrayList<>();

    private Server() throws IOException {
        serverSocket = new ServerSocket(1300);
    }

    public static Server getInstance() throws IOException {
        return server == null ? server = new Server() : server;
    }


}



    /*static DataInputStream dataInputStream;
    static DataOutputStream dataOutputStream;
    public  static ArrayList<Socket> socketArrayList = new ArrayList<>();
    public static int index = 0;
    public static ArrayList<Thread> threadList = new ArrayList<>();


    public static  void startServer(){
        try {
            ServerSocket serverSocket = new ServerSocket(5000);

            while (!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                socketArrayList.add(socket);
                System.out.println("A new client has connected");

                for (Socket s : socketArrayList){

                }

                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeUTF(HomeScreenFormController.clientNames + " " + "has join the chat");
                dataOutputStream.flush();

                handleTheClient(socket);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void handleTheClient(Socket socket) {
        new Thread(() ->{
            String clientMessage =  "";

            while (true){
                try {
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    clientMessage = dataInputStream.readUTF();

                    if (clientMessage.equals("<Image>")){
                        HandleReceivedImages(dataInputStream,socket);
                    }else{
                        sendMessageToOthers(clientMessage,socket);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();

    }

    private static void sendMessageToOthers(String clientMessage, Socket socket) {
        for (Socket s : socketArrayList){
            try {
                if (s.getPort() == socket.getPort()){
                    continue;
                }
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeUTF(HomeScreenFormController.clientNames +clientMessage+ ": " + clientMessage );
                dataOutputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static void HandleReceivedImages(DataInputStream dataInputStream, Socket socket) {
    }*/



