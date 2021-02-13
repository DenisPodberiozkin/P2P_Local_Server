package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private int serverIP;
    private int port;
    private ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
        openServerSocket();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientSession clientSession = new ClientSession(clientSocket);
                new Thread(clientSession).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
