package assignment7.Server;
import assignment7.DataModel.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Created by Eric on 11/29/16.
 */
public class ClientAPIHandler implements Runnable {
    private NetworkServer server;
    private ObjectInputStream reader;
    private ClientObserver writer;
    private Socket sock;
    private String name;
    private boolean stop = false;

    public ClientAPIHandler (Socket clientSocket, NetworkServer server) {
        this.sock = clientSocket;
        this.server = server;
        try {
            reader = new ObjectInputStream(sock.getInputStream());
            writer = new ClientObserver(sock.getOutputStream());
            server.addObserver(writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(!stop){
            try{
                byte type = reader.readByte();
                switch(type){
                    case MsgType.RegisterRequest:
                        register((RegisterRequest) reader.readObject());
                        break;
                    case MsgType.LoginRequest:
                        login((LoginRequest)reader.readObject());
                        break;
                    case MsgType.LogoutRequest:
                        logout((LogoutRequest)reader.readObject());
                        break;
                    case MsgType.AddFriendRequest:
                        addFriend((AddFriendRequest) reader.readObject());
                        break;
                    case MsgType.SendPrivateMessageRequest:
                        sendPrivateMessage((SendPrivateMessageRequest) reader.readObject());
                        break;
                    case MsgType.SendGroupMessageRequest:
                        sendGroupMessage((SendGroupMessageRequest) reader.readObject());
                        break;
                    case MsgType.JoinGroupRequest:
                        joinGroup((JoinGroupRequest) reader.readObject());
                        break;
                    case MsgType.LeaveGroupRequest:
                        leaveGroup((LeaveGroupRequest) reader.readObject());
                        break;
                    case MsgType.GetFriendsRequest:
                        getFriends((GetFriendsRequest) reader.readObject());
                        break;
                    case MsgType.GetOnlineGroupMembersRequest:
                        getOnlineGroupMembers((GetOnlineGroupMembersRequest) reader.readObject());
                        break;
                    case MsgType.ChangePasswordRequest:
                        changePassword((ChangePasswordRequest) reader.readObject());
                }
            } catch (Exception e) {
                if(name != null){
                    logout(null);
                }
            }

        }
        if(reader!=null){
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(writer!=null){
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(sock!=null){
            try {
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void send(byte channel, ResultBase result){
        try {
            writer.writeByte(channel);
            writer.writeObject(result);
            writer.flush();
        }
        catch(Exception e){

        }
    }

    //region APIs

    private void register(RegisterRequest req){
        if(server.accountDb.containsKey(req.username)){
            send(MsgType.RegisterResult, new RegisterResult(ErrorCode.UserAlreadyExists));
        }
        else{

            AccountInfo info = new AccountInfo();
            info.username = req.username;
            info.password = req.password;
            server.accountDb.put(req.username, info);
            server.saveDatabase();
            send(MsgType.RegisterResult, new RegisterResult(info.username, info.password));

        }
    }

    private void login(LoginRequest req){
        if(!server.accountDb.containsKey(req.username)){
            send(MsgType.LoginResult, new LoginResult(ErrorCode.UserDoesNotExist));
        }
        else{
            AccountInfo info = server.accountDb.get(req.username);
            if(req.password.equals(info.password)){
                name = req.username;
                server.onlineClients.add(req.username);
                server.addObserver(writer);
                writer.setName(name);
                send(MsgType.LoginResult, new LoginResult(req.username));
            }
            else {
                send(MsgType.LoginResult, new LoginResult(ErrorCode.WrongCredentials));
            }
        }
    }

    private void logout(LogoutRequest req){
        System.out.println(name + " logging out.");
        send(MsgType.LogoutResult, new LogoutResult());
        server.onlineClients.remove(name);
        server.removeFromAllGroups(name);
        server.deleteObserver(writer);
        stop=true;
    }

    private void addFriend(AddFriendRequest req){
        if(name == null){
            send(MsgType.AddFriendResult, new AddFriendResult(ErrorCode.NotAuthorized));
            return;
        }
        if(name.equals(req.username)){
            send(MsgType.AddFriendResult, new AddFriendResult(ErrorCode.CannotAddSelfAsFriend));
            return;
        }

        if(server.accountDb.containsKey(req.username)){
            AccountInfo info = server.accountDb.get(name);
            if(!info.friends.contains(req.username)){
                info.friends.add(req.username);
                server.saveDatabase();
            }
            send(MsgType.AddFriendResult, new AddFriendResult(req.username));
        }
        else {
            send(MsgType.AddFriendResult, new AddFriendResult(ErrorCode.UserDoesNotExist));
        }
    }

    private void sendPrivateMessage(SendPrivateMessageRequest req){
        if(name == null){
            send(MsgType.SendPrivateMessageResult, new SendPrivateMessageResult(ErrorCode.NotAuthorized));
            return;
        }
        if(name.equals(req.to)){
            send(MsgType.SendPrivateMessageResult, new SendPrivateMessageResult(ErrorCode.CannotSendMessageToSelf));
            return;
        }

        if(!server.accountDb.containsKey(req.to)){
            send(MsgType.SendPrivateMessageResult, new SendPrivateMessageResult(ErrorCode.UserDoesNotExist));
            return;
        }
        AccountInfo toInfo = server.accountDb.get(req.to);
        AccountInfo selfInfo = server.accountDb.get(name);
        if(!toInfo.friends.contains(name)){
            send(MsgType.SendPrivateMessageResult, new SendPrivateMessageResult(ErrorCode.NotAFriend));
            return;
        }
        if(!selfInfo.friends.contains(req.to)){
            send(MsgType.SendPrivateMessageResult, new SendPrivateMessageResult(ErrorCode.NotAFriend));
            return;
        }

        if(!server.onlineClients.contains(req.to)){
            send(MsgType.SendPrivateMessageResult, new SendPrivateMessageResult(ErrorCode.UserNotOnline));
            return;
        }

        //ClientAPIHandler receiver = server.onlineClients.get(req.to);
        ChatMessage chat = new ChatMessage();
        chat.from = name;
        chat.toUser = req.to;
        chat.toGroup = null;
        chat.body = req.message;
        ObservableMessage msg = new ObservableMessage(MsgType.ChatMessage, chat);
        server.notifyMessage(msg);
        send(MsgType.SendPrivateMessageResult, new SendPrivateMessageResult());
    }

    private void sendGroupMessage(SendGroupMessageRequest req){
        if(name == null){
            send(MsgType.SendGroupMessageResult, new SendGroupMessageResult(ErrorCode.NotAuthorized));
            return;
        }
        ChatMessage chat = new ChatMessage();
        chat.from = name;
        chat.toGroup = req.to;
        chat.toUser = null;
        chat.body = req.message;
        ObservableMessage msg = new ObservableMessage(MsgType.ChatMessage, chat);
        server.notifyMessage(msg);
        send(MsgType.SendGroupMessageResult, new SendGroupMessageResult());

    }

    private void joinGroup(JoinGroupRequest req){
        if(name == null){
            send(MsgType.JoinGroupResult, new JoinGroupResult(ErrorCode.NotAuthorized));
            return;
        }
        if(req.groupName.isEmpty()){
            send(MsgType.JoinGroupResult, new JoinGroupResult(ErrorCode.InvalidFormat));
            return;
        }
        server.addToGroup(name, req.groupName);
        writer.joinGroup(req.groupName);
        String[] members = server.getCurrentMembers(req.groupName);
        send(MsgType.JoinGroupResult, new JoinGroupResult(req.groupName, members));
    }

    private void leaveGroup(LeaveGroupRequest req){
        if(name == null){
            send(MsgType.LeaveGroupResult, new LeaveGroupResult(ErrorCode.NotAuthorized));
            return;
        }
        if(req.groupName.isEmpty()){
            send(MsgType.LeaveGroupResult, new LeaveGroupResult(ErrorCode.InvalidFormat));
            return;
        }
        if(writer.hasJoinedGroup(req.groupName)){
            server.removeFromGroup(name, req.groupName);
            writer.leaveGroup(req.groupName);
            send(MsgType.LeaveGroupResult, new LeaveGroupResult(req.groupName));
        } else {
            send(MsgType.LeaveGroupResult, new LeaveGroupResult(ErrorCode.GroupNotJoined));
        }
    }

    private void getFriends(GetFriendsRequest req){
        if(name == null){
            send(MsgType.GetFriendsResult, new GetFriendsResult(ErrorCode.NotAuthorized));
            return;
        }
        AccountInfo thisAccount = server.accountDb.get(name);
        ArrayList<String> onlineFriends = new ArrayList<>();
        for(String friend: thisAccount.friends){
            if(server.onlineClients.contains(friend)){
                onlineFriends.add(friend);
            }
        }
        send(MsgType.GetFriendsResult, new GetFriendsResult(thisAccount.friends.toArray(new String[0]), onlineFriends.toArray(new String[0])));
    }

    private void getOnlineGroupMembers(GetOnlineGroupMembersRequest req){
        if(name == null){
            send(MsgType.GetOnlineGroupMembersResult, new GetFriendsResult(ErrorCode.NotAuthorized));
            return;
        }
        if(req.groupName.isEmpty()){
            send(MsgType.GetOnlineGroupMembersResult, new GetOnlineGroupMembersResult(ErrorCode.InvalidFormat));
            return;
        }
        if(writer.hasJoinedGroup(req.groupName)){
            String[] members = server.getCurrentMembers(req.groupName);
            send(MsgType.GetOnlineGroupMembersResult, new GetOnlineGroupMembersResult(members));
        }else {
            send(MsgType.GetOnlineGroupMembersResult, new GetOnlineGroupMembersResult(ErrorCode.GroupNotJoined));
        }
    }

    private void changePassword(ChangePasswordRequest req){
        if(!server.accountDb.containsKey(req.username)){
            send(MsgType.ChangePasswordResult, new ChangePasswordResult(ErrorCode.UserDoesNotExist));
        }
        else{
            AccountInfo info = server.accountDb.get(req.username);
            if(req.oldPassword.equals(info.password)){
                info.password = req.newPassword;
                server.saveDatabase();
                send(MsgType.ChangePasswordResult, new ChangePasswordResult(req.username));
            }
            else {
                send(MsgType.ChangePasswordResult, new ChangePasswordResult(ErrorCode.WrongCredentials));
            }
        }
    }

    //endregion
}
