package Server;

import java.util.logging.Logger;

public class HeartBeatManager implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(HeartBeatManager.class.getName());
    private final ClientSession clientSession;
    private boolean isRunning;
    private boolean isPingReceived;

    public HeartBeatManager(ClientSession clientSession) {
        this.clientSession = clientSession;
        this.isRunning = true;
    }

    @Override
    public void run() {
        try {

            while (isRunning) {
                Thread.sleep(15000);
                if (!isPingReceived) {
                    this.isRunning = false;
                } else {
                    this.isPingReceived = false;
                }
            }
            if (clientSession.isSessionOpen()) {
                clientSession.closeSession();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pingReceived() {
        this.isPingReceived = true;
    }

}
