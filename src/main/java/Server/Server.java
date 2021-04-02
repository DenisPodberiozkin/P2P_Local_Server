package Server;

import java.io.IOException;
import java.net.*;
import java.util.logging.Logger;

public class Server implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private final int port;
    private String serverIP;
    private ServerSocket serverSocket;
    private boolean isWorking;

    public Server(int port) {
        this.port = port;
        this.isWorking = true;
        iniIp();
        openServerSocket();
    }

    @Override
    public void run() {
        isWorking = true;

        LOGGER.info("Server " + serverIP + ":" + serverSocket.getLocalPort() + " started");
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

    private void iniIp() {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            this.serverIP = socket.getLocalAddress().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
