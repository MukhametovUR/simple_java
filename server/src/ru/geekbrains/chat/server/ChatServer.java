package ru.geekbrains.chat.server;

import ru.geekbrains.chat.network.TCPConnection;
import ru.geekbrains.chat.network.TCPConnectionListener;
//import sun.rmi.transport.tcp.TCPConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener {

    public static void main(String[] args){
        new ChatServer();//Создаем экземпляр класса - объект
    }
    //private final ArrayList<ru.geekbrains.chat.network.TCPConnection> connections = new ArrayList<>();
    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private  ChatServer(){
        System.out.println("Server Running...");
        //Создаем сервер socket, который слушает порт 8189
        try(ServerSocket serverSocket = new ServerSocket(8189)){
            while (true){
                try{
                    new TCPConnection(this, serverSocket.accept());//Передаем метод soketa
                }catch (IOException e){
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        }catch (IOException e){
            throw new RuntimeException(e); //Поднимим поднимим классом RuntimeException и "уроним"
        }

    }

    @Override
    public synchronized void onConnectionReady(ru.geekbrains.chat.network.TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendAllConnections("Client connected: " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(ru.geekbrains.chat.network.TCPConnection tcpConnection, String value) {
        sendAllConnections(value);

    }

    @Override
    public synchronized void onDisconnect(ru.geekbrains.chat.network.TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendAllConnections("Client disconnected: " + tcpConnection);

    }

    @Override
    public synchronized void onException(ru.geekbrains.chat.network.TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    private void sendAllConnections(String value){
        System.out.println(value);
        final int cnt = connections.size();
        for(int i =0; i < cnt; i++)  connections.get(i).sendString(value);

        }

}
