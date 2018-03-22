package chat.client;

import chat.network.TCPConnection;
import chat.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Client extends JFrame implements TCPConnectionListener{
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 23715;
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;
    private TCPConnection connection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            new Client();
        });
    }

    private Client() {
        setWindow();
        try {
            connection = new TCPConnection(this, IP_ADDRESS, PORT);
        } catch (IOException e) {
            printMessage(e.toString());
        }
    }

    private final JTextArea log = new JTextArea();
    private final JTextField name = new JTextField("New User");
    private final JTextField messageInput = new JTextField();

    private void setWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        log.setEditable(false);
        log.setLineWrap(true);
        messageInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = messageInput.getText();
                if (msg.equals("")) return;
                messageInput.setText(null);
                connection.sendMessage(name.getText() + " says: " + msg);
            }
        });
        System.out.println("g");
        add(log, BorderLayout.CENTER);
        add(name, BorderLayout.NORTH);
        add(messageInput, BorderLayout.SOUTH);
        setVisible(true);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connected to " + tcpConnection.toString());
    }

    @Override
    public void onReceiveMessage(TCPConnection tcpConnection, String msg) {
        printMessage(msg);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {

    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println(e);
    }

    private synchronized void printMessage(String msg) {
        SwingUtilities.invokeLater(()->{
            log.append(msg + "\n");
            log.setCaretPosition(log.getDocument().getLength());
        });
    }
}
