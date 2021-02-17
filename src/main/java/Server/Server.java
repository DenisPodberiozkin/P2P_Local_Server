package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private final int port;
    private int serverIP;
    private ServerSocket serverSocket;
    private boolean isWorking;

    public Server(int port) {
        this.port = port;
        this.isWorking = true;
        openServerSocket();
    }

    @Override
    public void run() {
        isWorking = true;
        while (isWorking) {
            try {
//                System.out.println("Waiting for client connection");
                Socket clientSocket = serverSocket.accept();
                System.out.println(clientSocket.getInetAddress() + " has connected");
                ClientSession clientSession = new ClientSession(clientSocket);
                new Thread(clientSession).start();
            } catch (IOException e) {
                closeServer();
                e.printStackTrace();
            }
        }
    }

    public void closeServer() {
        try {
            this.isWorking = false;
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
