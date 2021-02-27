package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class Server implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
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
        LOGGER.info("Server " + serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort() + " started");
        while (isWorking) {
            try {
                Socket clientSocket = serverSocket.accept();
                LOGGER.info(clientSocket.getInetAddress() + " has connected");
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
