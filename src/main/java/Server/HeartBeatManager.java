package Server;

public class HeartBeatManager implements Runnable {
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
