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

    public ClientSession(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.isSessionOpen = true;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {


            while (isSessionOpen) {

                String receivedMessage = reader.readLine();
                LOGGER.info("Server received message " + receivedMessage + " from " + clientSocket.getInetAddress() + ":" + clientSocket.getLocalPort());
                String[] tokens = receivedMessage.split(" ");
                int sessionId = Integer.parseInt(tokens[0]);
                final String msg;
                switch (tokens[1]) {
                    case "GETLN":
                        String localIP = Data.getLocalIP();
                        String publicIP = Data.getPublicIP();
                        String ID = Data.getId();
                        String port = Data.getPort() + "";
                        msg = sessionId + " LN " + localIP + " " + publicIP + " " + ID + " " + port;
                        writer.println(msg);
                        LOGGER.info("Reply " + msg + " was sent to " + clientSocket.getInetAddress() + ":" + clientSocket.getLocalPort());
                        break;
                    case "SETLN":
                        Data.setLocalIP(tokens[2]);
                        Data.setPublicIP(tokens[3]);
                        Data.setId(tokens[4]);
                        Data.setPort(Integer.parseInt(tokens[5]));
                        msg = sessionId + " SETLN  OK";
                        writer.println(msg);
                        LOGGER.info("Reply " + msg + " was sent to " + clientSocket.getInetAddress() + ":" + clientSocket.getLocalPort());

                        break;
                    default:
                        LOGGER.warning("Unexpected token " + tokens[1] + " in message " + receivedMessage);
                }


            }


        } catch (Exception e) {
            LOGGER.info("Reason for closing: " + e.toString());
        } finally {
            closeSession();
        }
    }

    private void closeSession() {
        this.isSessionOpen = false;
        try {
            LOGGER.info(clientSocket.getInetAddress() + " disconnected");

            if (!clientSocket.isClosed()) {
                clientSocket.close();
            }

        } catch (IOException e) {
            LOGGER.severe("Error closing session");
            e.printStackTrace();
        }

    }
}
