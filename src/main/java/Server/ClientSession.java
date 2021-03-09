package Server;

import Encryption.DH;
import Encryption.EncryptionController;
import Encryption.IEncryptionController;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Logger;

public class ClientSession implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ClientSession.class.getName());
    private final Socket clientSocket;
    private final HeartBeatManager heartBeatManager;
    private final IEncryptionController encryptionController = EncryptionController.getInstance();
    private boolean isSessionOpen;
    private SecretKey secretKey;

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

                    if (secretKey != null) {
                        String payload = tokens[1];
                        LOGGER.info(payload);
                        payload = encryptionController.decryptStringByAES(secretKey, payload);
                        LOGGER.info(payload);
                        tokens = payload.split(" ");
                    } else {
                        tokens = Arrays.stream(tokens, 1, tokens.length).toArray(String[]::new);
                    }

                    String msg;
                    String encryptedMessage;
                    switch (tokens[0]) {
                        case "GETLN":
                            msg = "LN " + Data.getLastNodeJSON();
                            encryptedMessage = encryptionController.encryptStringByAES(secretKey, msg);
                            msg = sessionId + " " + encryptedMessage;
                            writer.println(msg);
                            break;
                        case "SETLN":
                            Data.addLastConnectedNode(tokens[1]);
                            msg = "SETLN  OK";
                            encryptedMessage = encryptionController.encryptStringByAES(secretKey, msg);
                            msg = sessionId + " " + encryptedMessage;
                            writer.println(msg);
                            break;
                        case "PING":
                            heartBeatManager.pingReceived();
                            msg = " PING OK";
                            encryptedMessage = encryptionController.encryptStringByAES(secretKey, msg);
                            msg = sessionId + " " + encryptedMessage;
                            writer.println(msg);
                            break;
                        case "REMOVE":
                            String nodeToRemoveJson = tokens[1];
                            Data.removeOldestConnection(nodeToRemoveJson);
                            msg = "REMOVE OK";
                            encryptedMessage = encryptionController.encryptStringByAES(secretKey, msg);
                            msg = sessionId + " " + encryptedMessage;
                            writer.println(msg);
                            break;
                        case "CSC":
                            try {
                                final String receivedPublicKey64 = tokens[1];
                                System.out.println(receivedPublicKey64);
                                final byte[] receivedPublicKeyData = Base64.getDecoder().decode(receivedPublicKey64);
                                DH dh = new DH();
                                final PublicKey receivedPublicKey = DH.getDHPublicKeyFromData(receivedPublicKeyData);
                                final PublicKey publicKeyToSend = dh.initReceiver(receivedPublicKey);
                                this.secretKey = dh.initSecretKey(receivedPublicKey);
                                final String publicKeyToSend64 = Base64.getEncoder().encodeToString(publicKeyToSend.getEncoded());
                                msg = sessionId + " CSC " + publicKeyToSend64;
                                writer.println(msg);
                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                                LOGGER.warning(" ERROR. Unable to create secure channel in connection " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " Reason " + e.toString());
                                msg = "CSC ERROR. Unable to create secure channel in connection " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " Reason " + e.toString();
                                this.isSessionOpen = false;
                            }
                            break;
                        default:
                            msg = sessionId + " ERROR - Unexpected token " + tokens[0] + " in message " + receivedMessage;
                            LOGGER.warning("Unexpected token " + tokens[0] + " in message " + receivedMessage);
                            writer.println(msg);
                    }
                    LOGGER.info("Reply " + msg + " was sent to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                } else {
                    this.isSessionOpen = false;
                }


            }


        } catch (Exception e) {
            LOGGER.info("Reason for closing: " + e.toString());
            e.printStackTrace();
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
