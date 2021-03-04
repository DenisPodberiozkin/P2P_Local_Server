package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class ClientSession implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ClientSession.class.getName());
    private final Socket clientSocket;
    //    private BufferedReader reader;
//    private PrintWriter writer;
    private boolean isSessionOpen;
    private final HeartBeatManager heartBeatManager;

    public ClientSession(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.isSessionOpen = true;
        this.heartBeatManager = new HeartBeatManager(this);
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
            new Thread(heartBeatManager).start();
            while (isSessionOpen) {

                String receivedMessage = reader.readLine();
                if (receivedMessage != null) {
                    LOGGER.info("Server received message " + receivedMessage + " from " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                    String[] tokens = receivedMessage.split(" ");
                    int sessionId = Integer.parseInt(tokens[0]);
                    final String msg;
                    switch (tokens[1]) {
                        case "GETLN":
                            msg = sessionId + " LN " + Data.getLastNodeJSON();
                            writer.println(msg);
                            LOGGER.info("Reply " + msg + " was sent to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                            break;
                        case "SETLN":
                            Data.setLastNodeJSON(tokens[2]);
                            msg = sessionId + " SETLN  OK";
                            writer.println(msg);
                            LOGGER.info("Reply " + msg + " was sent to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                            break;
                        case "PING":
                            heartBeatManager.pingReceived();
                            msg = sessionId + " PING OK";
                            writer.println(msg);
                            LOGGER.info("Reply " + msg + " was sent to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                            break;
                        default:
                            LOGGER.warning("Unexpected token " + tokens[1] + " in message " + receivedMessage);
                            writer.println(sessionId + " ERROR - Unexpected token " + tokens[1] + " in message " + receivedMessage);
                    }
                } else {
                    this.isSessionOpen = false;
                }


            }


        } catch (Exception e) {
            LOGGER.info("Reason for closing: " + e.toString());
            this.isSessionOpen = false;
        } finally {
            closeSession();
        }
    }

    public void closeSession() {
        try {
            LOGGER.info(clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " disconnected");

            if (!clientSocket.isClosed()) {
                clientSocket.close();
            }


        } catch (IOException e) {
            LOGGER.severe("Error closing session");
            e.printStackTrace();
        }

    }

    public boolean isSessionOpen() {
        return isSessionOpen;
    }
}
