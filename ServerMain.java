package assignment7;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerMain extends Observable {
	private Map<String,String> login = new HashMap<>();
	private Map<String, AccountInfo> registrations;
	Map<String,ClientHandler> activeClient = new HashMap<>();
	Map<String,String> individualChat = new HashMap<>();

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
			registrations = (Map<String, AccountInfo>) ois.readObject();
			ois.close();
		}
		catch(Exception ex){
			registrations = new HashMap<>();
		}
	}

	private void saveDatabase(){
		try {
			FileOutputStream fos = new FileOutputStream(Config.databaseFileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(registrations);
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
				Thread t = new Thread(new ClientHandler(clientSocket));
				t.start();
			} catch (Exception e) {
			}
		}
	}
	
	class ClientHandler implements Runnable {
		private ObjectInputStream reader;
		private ClientObserver writer;
		private Socket sock;
		private String name;
		private boolean stop = false;

		public ClientHandler (Socket clientSocket) {
			sock = clientSocket;
			try {
				reader = new ObjectInputStream(sock.getInputStream());
				writer = new ClientObserver(sock.getOutputStream());
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

		void register(RegisterRequest req){
			if(registrations.containsKey(req.username)){
				send(MsgType.RegisterResult, new RegisterResult(false));
			}
			else{
				AccountInfo info = new AccountInfo();
				info.username = req.username;
				info.displayName = req.displayName;
				info.email = req.email;
				info.password = req.password;
				registrations.put(req.username, info);
				saveDatabase();
				send(MsgType.RegisterResult, new RegisterResult(true));
			}
		}

		void login(LoginRequest req){
			if(!registrations.containsKey(req.username)){
				send(MsgType.LoginResult, new LoginResult(false));
			}
			else{
				AccountInfo info = registrations.get(req.username);
				if(req.password.equals(info.password)){
					name = req.username;
					activeClient.put(req.username, this);
					send(MsgType.LoginResult, new LoginResult(true));
				}
				else {
					send(MsgType.LoginResult, new LoginResult(false));
				}
			}
		}
		
		void logout(LogoutRequest req){
			System.out.println(name + " logging out.");
			activeClient.remove(name);
			stop=true;
		}

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

