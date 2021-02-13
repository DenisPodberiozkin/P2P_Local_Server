package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSession implements Runnable {

    BufferedReader reader;
    PrintWriter writer;
    private Socket clientSocket;

    public ClientSession(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);


            while (true) {
                String receivedMessage = reader.readLine();
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
                    case "DC":
                        reader.close();
                        writer.close();
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void sendMessage(String msg, boolean withVerify) {
//        writer
//    }
}
