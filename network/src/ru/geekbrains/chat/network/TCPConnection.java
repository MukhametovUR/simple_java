package ru.geekbrains.chat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {
    private final Socket socket;   //Socket для соединения
    private final Thread rxThread; //Thread поток, который слушает входящее соединение (сообщение)
    private final TCPConnectionListener eventListener;//Слушатель событий
    private final BufferedReader in;    //Потоки ввода
    private final BufferedWriter out;   //Поток вывода

    public TCPConnection(TCPConnectionListener eventListener, String ipAddr, int port) throws  IOException{
        this(eventListener, new Socket(ipAddr,port));//Вызов конструктора TCPConnection
    }

    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        in  = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));//Генерирует исключение IO Exception
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThread = new Thread(new Runnable() {//Анонимный класс Runnable
            @Override //Переопределение метода и написать свою реализацию - Полиморфизм
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!rxThread.isInterrupted()) {
                        eventListener.onReceiveString(TCPConnection.this, in.readLine());
                    }
                } catch (IOException e) {
                    eventListener.onException(TCPConnection.this, e);
                } finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });//Поток, который слушает всё входящее
        rxThread.start();//Запускаем поток
    }
    public synchronized void sendString(String value){
        try{
        out.write(value + "\r\n");//Передаем строку
        out.flush();//Запись строки, если сообщение не передалось
        }catch (IOException e){             //Перехватывает исключение
            eventListener.onException(TCPConnection.this, e);//Что-то делает с исключением
            disconnect();//Разрываем соединение, если получили исключение
        }

    }
    public synchronized void disconnect(){  //Метод отключения

        rxThread.interrupt();               //Прерываем поток
        try {
            socket.close();                 //Закрыть socket
        }catch (IOException e){             //Перехватывает исключение
            eventListener.onException(TCPConnection.this, e);//Что-то делает с исключением
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}

