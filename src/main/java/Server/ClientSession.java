package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSession implements Runnable {

    private final Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private boolean isSessionOpen;

    public ClientSession(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.isSessionOpen = true;
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);


            while (isSessionOpen) {
                String receivedMessage = reader.readLine();
//                System.out.println(receivedMessage);
                String[] tokens = receivedMessage.split(" ");

                switch (tokens[0]) {
                    case "GETLN":
                        String localIP = Data.getLocalIP();
                        String publicIP = Data.getPublicIP();
                        String ID = Data.getId();
                        String port = Data.getPort() + "";
                        String msg = localIP + " " + publicIP + " " + ID + " " + port;
                        writer.println(msg);
                        break;
                    case "SETLN":
                        Data.setLocalIP(tokens[1]);
                        Data.setPublicIP(tokens[2]);
                        Data.setId(tokens[3]);
                        Data.setPort(Integer.parseInt(tokens[4]));
                        break;
                }


            }

        } catch (Exception e) {
            this.isSessionOpen = false;
        } finally {
            closeSession();
        }
    }

    private void closeSession() {
        try {
            System.out.println(clientSocket.getInetAddress() + " disconnected");
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();

            }
            if (!clientSocket.isClosed()) {
                clientSocket.close();
            }

        } catch (IOException e) {
            System.err.println("Error closing session");
            e.printStackTrace();
        }

    }
}
