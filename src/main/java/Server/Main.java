package Server;

public class Main {
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
    }

    public static void main(String[] args) {
        Server server = new Server(4444);
        new Thread(server).start();


    }
}
