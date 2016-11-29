package assignment7;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerMain extends Observable {
	private Map<String, AccountInfo> accountDb;
	Map<String,ClientAPIHandler> onlineClients = new HashMap<>();

	public static void main(String[] args) {
		try {
			ServerMain mainServer = new ServerMain();
			mainServer.setUpDatabase();
			mainServer.setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpDatabase()  {
		try {
			FileInputStream fis = new FileInputStream(Config.databaseFileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			accountDb = (Map<String, AccountInfo>) ois.readObject();
			ois.close();
		}
		catch(Exception ex){
			accountDb = new HashMap<>();
		}
	}

	private void saveDatabase(){
		try {
			FileOutputStream fos = new FileOutputStream(Config.databaseFileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(accountDb);
			oos.close();
		}
		catch(Exception ex){
		}
	}

	private void setUpNetworking() throws Exception {
		ServerSocket serverSock = new ServerSocket(Config.port);
		InetAddress address = serverSock.getInetAddress();
		System.out.println(address.getHostAddress());

		while (true) {
			try {
				Socket clientSocket = serverSock.accept();
				System.out.println("got a connection");
				Thread t = new Thread(new ClientAPIHandler(clientSocket));
				t.start();
			} catch (Exception e) {
			}
		}
	}
	
	class ClientAPIHandler implements Runnable {
		private ObjectInputStream reader;
		private ClientObserver writer;
		private Socket sock;
		private String name;
		private boolean stop = false;

		public ClientAPIHandler (Socket clientSocket) {
			sock = clientSocket;
			try {
				reader = new ObjectInputStream(sock.getInputStream());
				writer = new ClientObserver(sock.getOutputStream());
				addObserver(writer);
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
						case MsgType.AddToGroupRequest:
							addTogroup((AddToGroupRequest) reader.readObject());
							break;
						case MsgType.SendGroupMessageRequest:
							sendGroupMessage((SendGroupMessageRequest) reader.readObject());
							break;
//						case MsgType.GetActiveGroupsRequest:
//							getActiveGroups((GetActiveGroupsRequest) reader.readObject());
//							break;
//						case MsgType.CreateOrJoinGroupRequest:
//							createOrJoinGroup((CreateOrJoinGroupRequest) reader.readObject());
//							break;
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

		/*
		void sendMsg(Message msg){
			if(msg.getRecipient()==null){
				writeMsg(new Message(Message.ERROR,"Please specify recipient!"));
			}
			if(activeClient.containsKey(msg.getRecipient())){
				String appendName = name+": "+msg.getContent();
				activeClient.get(msg.getRecipient()).writeMsg(new Message(msg.getType(),appendName));
			}else{
				writeMsg(new Message(Message.ERROR,"Recipient not found!"));
			}
		}

*/
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

		void register(RegisterRequest req){
			if(accountDb.containsKey(req.username)){
				send(MsgType.RegisterResult, new RegisterResult(ErrorCode.UserAlreadyExists));
			}
			else{

				AccountInfo info = new AccountInfo();
				info.username = req.username;
				info.displayName = req.displayName;
				info.email = req.email;
				info.password = req.password;
				accountDb.put(req.username, info);
				saveDatabase();
				send(MsgType.RegisterResult, new RegisterResult(info.username, info.password, info.displayName, info.email));

			}
		}

		void login(LoginRequest req){
			if(!accountDb.containsKey(req.username)){
				send(MsgType.LoginResult, new LoginResult(ErrorCode.UserDoesNotExist));
			}
			else{
				AccountInfo info = accountDb.get(req.username);
				if(req.password.equals(info.password)){
					name = req.username;
					onlineClients.put(req.username, this);
					send(MsgType.LoginResult, new LoginResult());
				}
				else {
					send(MsgType.LoginResult, new LoginResult(ErrorCode.WrongCredentials));
				}
			}
		}

		void logout(LogoutRequest req){
			System.out.println(name + " logging out.");
			onlineClients.remove(name);
			stop=true;
		}

		void addFriend(AddFriendRequest req){
			if(name == null){
				send(MsgType.AddFriendResult, new AddFriendResult(ErrorCode.NotAuthorized));
				return;
			}
			if(name.equals(req.username)){
				send(MsgType.AddFriendResult, new AddFriendResult(ErrorCode.CannotAddSelfAsFriend));
				return;
			}

			if(accountDb.containsKey(req.username)){
				AccountInfo info = accountDb.get(name);
				if(!info.friends.contains(req.username)){
					info.friends.add(req.username);
					saveDatabase();
				}
				send(MsgType.AddFriendResult, new AddFriendResult());
			}
			else {
				send(MsgType.AddFriendResult, new AddFriendResult(ErrorCode.UserDoesNotExist));
			}
		}

		void sendPrivateMessage(SendPrivateMessageRequest req){
			if(name == null){
				send(MsgType.SendPrivateMessageResult, new SendPrivateMessageResult(ErrorCode.NotAuthorized));
				return;
			}
			if(name.equals(req.to)){
				send(MsgType.SendPrivateMessageResult, new AddFriendResult(ErrorCode.CannotSendMessageToSelf));
				return;
			}
			if(!onlineClients.containsKey(req.to)){
				send(MsgType.SendPrivateMessageResult, new AddFriendResult(ErrorCode.UserNotOnline));
				return;
			}
			else {
				AccountInfo ai = accountDb.get(name);
				if(ai.friends.contains(req.to)){
					ClientAPIHandler receiver = onlineClients.get(req.to);
					receiver.send(MsgType.SendPrivateMessageResult,new SendPrivateMessageResult(req.from,req.to,req.message));
				}else{
					send(MsgType.SendPrivateMessageResult, new SendPrivateMessageResult(ErrorCode.NotAFriend));
				}
			}
		}
		
		void addTogroup(AddToGroupRequest req){
			if(!onlineClients.containsKey(req.name)){
				send(MsgType.AddToGroupResult, new SendGroupMessageResult(ErrorCode.UserNotOnline));
			}else{
				ClientAPIHandler handler = onlineClients.get(req.name);
				handler.addTogroup(new AddToGroupRequest(name));
				writer.addToGroup(req.name);
			}
		}

		void sendGroupMessage(SendGroupMessageRequest req){
			if(name == null){
				send(MsgType.SendGroupMessageResult, new SendGroupMessageResult(ErrorCode.NotAuthorized));
				return;
			}
			setChanged();
			notifyObservers(new SendGroupMessageResult(req.from,req.message));
		}
/*
		void getActiveGroups (GetActiveGroupsRequest req){
			if(name == null){
				send(MsgType.GetActiveGroupsResult, new GetActiveGroupsResult(ErrorCode.NotAuthorized));
				return;
			}
			//missing implementation
		}

		void createOrJoinGroup (CreateOrJoinGroupRequest req){
			if(name == null){
				send(MsgType.CreateOrJoinGroupResult, new CreateOrJoinGroupResult(ErrorCode.NotAuthorized));
				return;
			}
			//missing implementation
		}*/

		//endregion

		/*
		
		void groupChat(Message msg){
			setChanged();
			notifyObservers(msg);
		}
		
		void addToGroup(Message msg){
			if(msg==null){
				writeMsg(new Message(Message.ERROR,"No account selected!"));
				return;
			}
			addObserver(writer);
			String[] names = msg.getContent().split(" ");
			for(String name: names){
				if(!activeClient.containsKey(name)){
					writeMsg(new Message(Message.ERROR,"Account "+name+" is not online!"));
				}else{
					ClientHandler ch = activeClient.get(name);
					addObserver(activeClient.get(name).getClientObserver());
				}
			}
		}
		*/
	}
}

