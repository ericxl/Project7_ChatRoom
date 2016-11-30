package assignment7.Client;

import assignment7.DataModel.*;
import java.io.*;
import java.net.*;
import java.util.*;

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
    private Map<Byte, ArrayList<OnResult>> handlers = new HashMap<>();

    public NetworkClient(int port, String url) throws IOException{
        socket = new Socket(url, port);
        toServer = new ObjectOutputStream(socket.getOutputStream());
        fromServer = new ObjectInputStream(socket.getInputStream());
        Thread t = new Thread(new ServerHandler());
        t.start();
    }

    public void registerHandler (byte channel, OnResult observer) {
        Byte c = new Byte(channel);
        if(handlers.containsKey(c)){
            ArrayList<OnResult> r = handlers.get(c);
            r.add(observer);
        } else {
            ArrayList<OnResult> r = new ArrayList<>();
            r.add(observer);
            handlers.put(c, r);
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
                    ServerMessageBase base = (ServerMessageBase) fromServer.readObject();
                    Byte channel = new Byte(type);
                    if(handlers.containsKey(channel)){
                        ArrayList<OnResult> obs = handlers.get(channel);
                        for (OnResult r : obs) {
                            r.observe(base);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

    }
}