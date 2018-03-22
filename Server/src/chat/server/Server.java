package chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.logging.SocketHandler;
import chat.network.TCPConnection;
import chat.network.TCPConnectionListener;

public class Server implements TCPConnectionListener{

    private final ArrayList<TCPConnection> connections;

    public static void main(String[] args) {
        new Server();
    }

    private Server() {
        System.out.println("Server started.");
        try (ServerSocket serverSocket = new ServerSocket(23715)) {
            while(true){
                try{
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        System.out.println("Client connected: " + tcpConnection);
    }

    @Override
    public void onReceiveMessage(TCPConnection tcpConnection, String msg) {
        sendToAllConnections(msg);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        System.out.println("Client disconnected: " + tcpConnection);
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println(e);
    }

    private void sendToAllConnections(String value){
        System.out.println(value);
        connections.forEach(connection -> connection.sendMessage(value));
    }

}
