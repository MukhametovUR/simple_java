package ru.geekbrains.chat.network;

public interface TCPConnectionListener {

    void onConnectionReady(TCPConnection tcpConnection);//Метод соединения
    void onReceiveString(TCPConnection tcpConnection, String value);  //Метод приняло строку сообщения
    void onDisconnect(TCPConnection tcpConnection);     //Метод если разрыв соединения
    void onException(TCPConnection tcpConnection, Exception e);//Метод исключения
}
