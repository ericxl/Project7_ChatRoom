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

    private final List<OnRegisterObserver> onRegisterObservers = new ArrayList<>();
    private final List<OnLoginObserver> onLoginObservers = new ArrayList<>();

    public void OnRegister(OnRegisterObserver observer){
        this.onRegisterObservers.add(observer);
    }
    public void OnLogin(OnLoginObserver observer){
        this.onLoginObservers.add(observer);
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

                    if(type == MsgType.RegisterResult){
                        RegisterResult result = (RegisterResult) fromServer.readObject();

                        for (final OnRegisterObserver observer : onRegisterObservers) {
                            observer.observeRegister(result);
                        }
                    }
                    else if(type == MsgType.LoginResult){
                        LoginResult result = (LoginResult) fromServer.readObject();

                        for (final OnLoginObserver observer : onLoginObservers) {
                            observer.observeLogin(result);
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