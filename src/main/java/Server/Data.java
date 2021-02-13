package Server;

public class Data {
    private static String id;
    private static String localIP;
    private static String publicIP;
    private static int port;


    public static String getId() {
        return id;
    }

    public static synchronized void setId(String id) {
        Data.id = id;
    }

    public static String getLocalIP() {
        return localIP;
    }

    public static synchronized void setLocalIP(String localIP) {
        Data.localIP = localIP;
    }

    public static String getPublicIP() {
        return publicIP;
    }

    public static synchronized void setPublicIP(String publicIP) {
        Data.publicIP = publicIP;
    }

    public static synchronized int getPort() {
        return port;
    }

    public static synchronized void setPort(int port) {
        Data.port = port;
    }
}
