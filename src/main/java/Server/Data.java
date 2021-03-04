package Server;

public class Data {
    private static String lastNodeJSON;

    public static String getLastNodeJSON() {
        return lastNodeJSON;
    }

    public static void setLastNodeJSON(String lastNodeJSON) {
        Data.lastNodeJSON = lastNodeJSON;
    }
}
