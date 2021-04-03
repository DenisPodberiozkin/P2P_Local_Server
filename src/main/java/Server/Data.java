package Server;

import Util.FixedStack;


public class Data {
    private static final FixedStack<String> lastConnectedNodes = new FixedStack<>(5);

    public static synchronized String getLastNodeJSON() {
        return lastConnectedNodes.peek();
    }

    public static synchronized void addLastConnectedNode(String lastNodeJSON) {
        lastConnectedNodes.push(lastNodeJSON);
    }

    public static synchronized void removeOldestConnection(String removeNodeJson) {
        String currentOldestNodeJson = getLastNodeJSON();
        if (removeNodeJson.equals(currentOldestNodeJson)) {
            lastConnectedNodes.pop();
        }
    }
}
