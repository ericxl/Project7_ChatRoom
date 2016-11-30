package assignment7.Client;

import java.io.*;
import java.net.*;
import java.util.*;
import assignment7.DataModel.*;
/**
 * Created by Eric on 11/27/16.
 */
public class NetworkClient {
    Socket socket;
    ObjectOutputStream toServer;
    ObjectInputStream fromServer;

    interface OnResult {
        void observe(ServerMessageBase result);
    }
    private Map<Byte, OnResult> handlers = new HashMap<>();

    public void registerHandler (byte channel, OnResult observer) {
        handlers.put(new Byte(channel), observer);
    }

    public NetworkClient(int port, String url){
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
                    Byte channel = new Byte(type);
                    if(handlers.containsKey(channel)){
                        handlers.get(new Byte(type)).observe((ServerMessageBase) fromServer.readObject());
                    }
                    else {
                        fromServer.readObject();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }

            }
        }

    }
}