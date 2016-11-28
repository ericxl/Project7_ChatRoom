package assignment7;
import java.io.*;
import java.net.*;
import java.util.*;
/**
 * Created by Eric on 11/27/16.
 */
public class Client {
    Socket socket;
    ObjectOutputStream toServer;
    ObjectInputStream fromServer;

    interface OnResult {
        void observe(ResultBase result);
    }
    private Map<Byte, OnResult> observers = new HashMap<>();

    public void registerHandler (byte channel, OnResult observer) {
        observers.put(new Byte(channel), observer);
    }

    public Client(int port, String url){
        try {
            socket = new Socket(url, port);
            toServer = new ObjectOutputStream(socket.getOutputStream());
            fromServer = new ObjectInputStream(socket.getInputStream());
            Thread t = new Thread(new ServerHandler());
            t.start();
            System.out.println("io ready");
        }
        catch (IOException ex) {
        }
    }

    public void send(byte channel, RequestBase request){
        try {
            toServer.writeByte(channel);
            toServer.writeObject(request);
            toServer.flush();
        }
        catch(Exception e){

        }
    }

    class ServerHandler implements Runnable {

        public void run() {
            while(true){
                try{
                    byte type = fromServer.readByte();
                    observers.get(new Byte(type)).observe((ResultBase) fromServer.readObject());
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }

            }
        }

    }
}