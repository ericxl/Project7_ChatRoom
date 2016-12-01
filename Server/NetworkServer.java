/* EE422C Project 7 submission by
 * Xiaoyong Liang
 * XL5432
 * 16480
 * Yuankai Yue
 * yy7347
 * 16465
 * Slip days used: <1>
 * Fall 2016
 */
package assignment7.Server;

import java.io.*;
import java.net.*;
import java.util.*;
import assignment7.DataModel.*;

public class NetworkServer extends Observable{
    ServerSocket serverSock;
    Map<String, AccountInfo> accountDb;
    Set<String> onlineClients = new HashSet<>();
    Map<String, Set<String>> activeGroups = new HashMap<>();

    public NetworkServer(int port) {
        try {
            serverSock = new ServerSocket(port);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void setUpDatabase()  {
        try {
            FileInputStream fis = new FileInputStream(ServerConfig.databaseFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            accountDb = (Map<String, AccountInfo>) ois.readObject();
            ois.close();
        }
        catch(Exception ex){
            accountDb = new HashMap<>();
        }
    }

    public void saveDatabase(){
        try {
            FileOutputStream fos = new FileOutputStream(ServerConfig.databaseFileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(accountDb);
            oos.close();
        }
        catch(Exception ex){
        }
    }

    public void setUpNetworking() {
        while (true) {
            try {
                Socket clientSocket = serverSock.accept();
                System.out.println("got a connection");
                Thread t = new Thread(new ClientAPIHandler(clientSocket, this));
                t.start();
            } catch (Exception e) {

            }
        }
    }

    public void addToGroup(String userName, String groupName){
        if(!activeGroups.containsKey(groupName)){
            HashSet<String> members = new HashSet<>();
            members.add(userName);
            activeGroups.put(groupName, members);
        } else{
            Set<String> members  = activeGroups.get(groupName);
            members.add(userName);
        }
    }
    public void removeFromGroup(String userName, String groupName){
        if(activeGroups.containsKey(groupName)){
            Set<String> members = activeGroups.get(groupName);
            if(members.contains(userName)){
                members.remove(userName);
            }
            if (members.size() <= 0){
                activeGroups.remove(groupName);
            }
        }
    }
    public void removeFromAllGroups(String userName){
        ArrayList<String> emptyGroups = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : activeGroups.entrySet()) {
            Set<String> members = entry.getValue();
            if (members.contains(userName)) {
                members.remove(userName);
            }
            if(members.size() <= 0){
                emptyGroups.add(entry.getKey());
            }
        }
        for (String emptyGroup:emptyGroups) {
            activeGroups.remove(emptyGroup);
        }
    }
    public String[] getCurrentMembers(String groupName){
        if(activeGroups.containsKey(groupName)){
            Set<String> members = activeGroups.get(groupName);
            String[] returnValue = members.toArray(new String[members.size()]);
            return returnValue;
        } else {
            return null;
        }
    }

    public void notifyMessage(ObservableMessage message){
        this.setChanged();
        this.notifyObservers(message);
    }

}
