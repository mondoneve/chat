package chat.network;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TCPConnection {
    private final TCPConnectionListener eventListener;
    private final Socket socket;
    private final Thread rxThread;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPConnection(TCPConnectionListener eventListener, String ipAddress, int port) throws IOException{
        this(eventListener, new Socket(InetAddress.getByName(ipAddress), port));
    }

    public TCPConnection(TCPConnectionListener listener, Socket socket) throws IOException {
        eventListener = listener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        rxThread = new Thread(()->{
            try {
                eventListener.onConnectionReady(TCPConnection.this);
                while (!TCPConnection.this.rxThread.isInterrupted()) {
                    String msg = in.readLine();
                    eventListener.onReceiveMessage(TCPConnection.this, msg);
                }
            } catch (IOException e) {
                eventListener.onException(TCPConnection.this, e);
            } finally {
                eventListener.onDisconnect(TCPConnection.this);
            }
        });
        rxThread.start();
    }

    public synchronized void sendMessage(String msg) {
        try {
            out.write(msg+"\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(this, e);
            disconnect();
        }
    }

    public synchronized void disconnect(){
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection - "+socket.getInetAddress()+":"+socket.getPort();
    }
}
