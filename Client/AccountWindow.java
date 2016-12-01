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
package assignment7.Client;

import assignment7.DataModel.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.text.Text;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;

import java.io.*;
import java.util.*;

/**
 * Created by Eric on 11/30/16.
 */
public class AccountWindow {
    private static final String groupChatsHistroyFileName = "_groupChats.his";
    private static final String privateChatsHistroyFileName = "_privateChats.his";

    NetworkClient client;

    String clientName = null;
    String[] friends = new String[0];
    String[] onlineFriends=new String[0];
    String[] groups = new String[0];

    TextField friendName;
    TextField groupName;
    ComboBox<Text> friendList;
    ComboBox<String> groupList;
    Label statusBar;

    Map<String, ChatWindow> privateChats = new HashMap<>();
    Map<String, ChatWindow> groupChats = new HashMap<>();

    Map<String, ArrayList<ChatMessage>> privateHistory = new HashMap<>();
    Map<String, ArrayList<ChatMessage>> groupHistory = new HashMap<>();

    public AccountWindow(NetworkClient client){
        this.client = client;

        client.registerHandler(MsgType.AddFriendResult, result-> onAddFriend((AddFriendResult) result));
        client.registerHandler(MsgType.JoinGroupResult, result-> onJoinGroup((JoinGroupResult) result));
        client.registerHandler(MsgType.GetFriendsResult, result -> onGetFriends((GetFriendsResult) result));
        client.registerHandler(MsgType.ChatMessage, result -> onReceiveChatMessage((ChatMessage) result));
    }

    public void start(){
        Stage primaryStage = new Stage();

        primaryStage.setOnCloseRequest((t)->{
            Platform.exit();
            System.exit(0);
        });

        statusBar=new Label();

        friendList = new ComboBox<>();
        friendList.setMinWidth(100);
        friendList.setOnAction(e->{
            String friendName = friendList.getValue().getText();
            if(friendName == null || friendName.isEmpty()){
                return;
            }

            if(privateChats.containsKey(friendName)){
                ChatWindow w = privateChats.get(friendName);
                w.show();
            }
            else {
                ArrayList<ChatMessage> his = new ArrayList<>();
                if(privateHistory.containsKey(friendName)){
                    his = privateHistory.get(friendName);
                }
                ChatWindow w = new ChatWindow(client, clientName, friendName, false);

                w.start(his);
                privateChats.put(friendName, w);
            }
        });

        friendName = new TextField();
        Button addFriend = new Button("Add Friend");
        addFriend.setOnAction(e->{
            client.send(MsgType.AddFriendRequest, new AddFriendRequest(friendName.getText()));
        });


        groupList = new ComboBox<>();
        groupList.setMinWidth(100);
        groupList.setOnAction(e->{
            String groupName = groupList.getValue();
            if(groupName == null || groupName.isEmpty()){
                return;
            }

            if(groupChats.containsKey(groupName)){
                ChatWindow w = groupChats.get(groupName);
                w.show();
            }
            else {
                ArrayList<ChatMessage> his = new ArrayList<>();
                if(groupHistory.containsKey(groupName)){
                    his = groupHistory.get(groupName);
                }
                ChatWindow w = new ChatWindow(client, clientName, groupName, true);
                w.start(his);
                privateChats.put(groupName, w);
            }
        });

        groupName = new TextField();
        Button joinGroup = new Button("Join Group");
        joinGroup.setOnAction(e->{
            client.send(MsgType.JoinGroupRequest, new JoinGroupRequest(groupName.getText()));
        });

        VBox vb = new VBox();
        vb.setPadding(new Insets(5, 5, 5, 5));
        Label blank = new Label();
        vb.getChildren().addAll(statusBar,friendList,friendName,addFriend,blank,groupList,groupName,joinGroup);
        BorderPane mainPane = new BorderPane();

        mainPane.setCenter(vb);

        Scene scene = new Scene(mainPane, 650, 400);
        primaryStage.setTitle("Hello " + clientName + "!");
        primaryStage.setScene(scene);
        primaryStage.show();

        client.send(MsgType.GetFriendsRequest, new GetFriendsRequest());

        try {
            FileInputStream fis = new FileInputStream(clientName + groupChatsHistroyFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            groupHistory = (Map<String, ArrayList<ChatMessage>>) ois.readObject();
            ois.close();
        }
        catch(Exception ex){
            groupHistory = new HashMap<>();
        }
        try {
            FileInputStream fis = new FileInputStream(clientName + privateChatsHistroyFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            privateHistory = (Map<String, ArrayList<ChatMessage>>) ois.readObject();
            ois.close();
        }
        catch(Exception ex){
            privateHistory = new HashMap<>();
        }
    }

    public void setClientName(String name){
        clientName = name;
    }

    private void onAddFriend(AddFriendResult result){
        if(result.error == null){
        	String text = "Add friend success: " + result.friendUsername;
        	Platform.runLater(()->{
            	statusBar.setText(text);
            	statusBar.setTextFill(Color.GREEN);
        	});
            System.out.println(text);

        } else {
        	String text = "Add friend failed" + result.error.toString();
        	Platform.runLater(()->{
            	statusBar.setText(text);
            	statusBar.setTextFill(Color.RED);
        	});
            System.out.println(text);
        }
    }

    private void onGetFriends(GetFriendsResult result){
        friends = result.friends;
        onlineFriends=result.onlineFriends;

        if(friends!=null){
            friendList.getItems().clear();
            try{
                for(String friend:friends){
                    Text currentText = new Text(friend);
                    ArrayList<String> onlineAL = new ArrayList<>(Arrays.asList(onlineFriends));
                    if(onlineAL.contains(friend)){
                        currentText.setFill(Color.GREEN);
                    }
                    friendList.getItems().add(currentText);
                }
            }catch (Exception E){

            }
        }
    }

    private void onJoinGroup (JoinGroupResult result){
        if(result.error == null){
        	String text = "join group: " + result.groupName + " success!";
            ArrayList<String> currentGroups = new ArrayList<>(Arrays.asList(groups));
        	if(!currentGroups.contains(result.groupName)){
        	    currentGroups.add(result.groupName);
            }
            groups = currentGroups.toArray(new String[0]);

        	Platform.runLater(()->{
                groupList.getItems().clear();
                for(String group:groups){
                    groupList.getItems().add(group);
                }

            	statusBar.setText(text);
            	statusBar.setTextFill(Color.GREEN);
        	});
        } else {
        	String text = "join group failed" + result.error.toString();
        	Platform.runLater(()->{
            	statusBar.setText(text);
            	statusBar.setTextFill(Color.RED);
        	});
        }
    }

    private void onReceiveChatMessage(ChatMessage msg){
        if(msg.toGroup != null){
            if(groupHistory.containsKey(msg.toGroup)){
                ArrayList<ChatMessage> h = groupHistory.get(msg.toGroup);
                h.add(msg);
            }
            else {
                ArrayList<ChatMessage> h = new ArrayList<>();
                h.add(msg);
                groupHistory.put(msg.toGroup, h);
            }

            try {
                FileOutputStream fos = new FileOutputStream(clientName + groupChatsHistroyFileName);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(groupHistory);
                oos.close();
            }
            catch(Exception ex){
            }
        } else if(msg.toUser != null){
            String other = msg.toUser;
            if(other.equals(clientName)){
                other = msg.from;
            }

            if(privateHistory.containsKey(other)){
                ArrayList<ChatMessage> h = privateHistory.get(other);
                h.add(msg);
            }
            else {
                ArrayList<ChatMessage> h = new ArrayList<>();
                h.add(msg);
                privateHistory.put(other, h);
            }

            try {
                FileOutputStream fos = new FileOutputStream(clientName  + privateChatsHistroyFileName);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(privateHistory);
                oos.close();
            }
            catch(Exception ex){
            }
        }
        //save
    }
}
