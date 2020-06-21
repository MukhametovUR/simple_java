package ru.geekbrains.chat.client;

import ru.geekbrains.chat.network.TCPConnection;
import ru.geekbrains.chat.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class CientWindow extends JFrame implements ActionListener, TCPConnectionListener {

    private static final String IP_ADDR ="127.0.0.1";
    private static final int PORT =8189;
    private static final int WIDTH = 600;
    private static final int HEIGTH = 400;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CientWindow();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickname = new JTextField("Администратор");
    private final JTextField fieldInput = new JTextField();
    private TCPConnection connection;

    private CientWindow(){
      setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//Кнопка закрытия
        setSize(WIDTH,HEIGTH);//Размерность окна
      setLocationRelativeTo(null);//Окно по середине
        setAlwaysOnTop(true);//Окно по верх других окон
        log.setEditable(false); //Запрет редактирования сообщения
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);

        fieldInput.addActionListener(this);//для перехвата нажатия Enter
        add(fieldInput, BorderLayout.SOUTH);//Поле, в которое будем писать
        add(fieldNickname, BorderLayout.NORTH);// Поле с нNickname

        setVisible(true);
        try {
           connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            //e.printStackTrace();
            printMsg("Connection exception: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String msg = fieldInput.getText();
        if (msg.equals("")) return;
        fieldInput.setText(null);
        connection.sendString(fieldNickname.getText() + ": " +msg);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMsg("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMsg(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMsg("Connection close.");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMsg("Connection exception: " + e);
    }
    private synchronized void printMsg(String msg){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
