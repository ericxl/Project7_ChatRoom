package assignment7;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class ServerMain extends Observable {
	private Map<String,String> login = new HashMap<>();
	Map<String,ClientHandler> activeClient = new HashMap<>();
	Map<String,String> individualChat = new HashMap<>();
	private int port = 4242;
	
	public static void main(String[] args) {
		try {
			new ServerMain().setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(port);
		InetAddress address = serverSock.getInetAddress();
		System.out.println(address.getHostAddress());
		while (true) {
			Socket clientSocket = serverSock.accept();
			Thread t = new Thread(new ClientHandler(clientSocket));
			t.start();
			System.out.println("got a connection");
		}
	}
	
	class ClientHandler implements Runnable {
		private ObjectInputStream reader;
		private ClientObserver writer;
		private Socket sock;
		private String name;
		private boolean stop= false;

		public ClientHandler(Socket clientSocket) {
			sock = clientSocket;
			try {
				reader = new ObjectInputStream(sock.getInputStream());
				writer = new ClientObserver(sock.getOutputStream());
				name = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			Message message;
			while(!stop){
				try{
					message = (Message) reader.readObject();
				}catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
					break;
				}
				
				switch(message.getType()){
				case Message.MESSAGE: 
					sendMsg(message);
					break;
				case Message.LOGIN:
					login(message);
					break;
				case Message.LOGOUT:
					logout(message);
					break;
				case Message.FRIENDS:
					break;
				case Message.NEW_LOGIN:
					addLogin(message);
					break;
				case Message.ADDTOGROUP:
					addToGroup(message);
					break;
				case Message.GROUPMESSAGE:
					groupChat(message);
					break;
				case Message.STOP:
					stop=true;
					break;
				}			
			}
			close();
		}
		
		ClientObserver getClientObserver(){
			return writer;
		}
		void close(){	
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
		
		void writeMsg(Message msg){
			if(!sock.isConnected()||name==null){
				System.out.println("Client not initialized!");
				return;
			}
			try {
				writer.writeObject(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		void login(Message msg){
			String[] cred = msg.getContent().split(" ");
			if(activeClient.containsKey(cred[0])){
				sendMsg(new Message(Message.ERROR,"This account is already logined!"));
			}else{
				if(cred[1].equals(login.get(cred[0]))){
					name = cred[0];
					activeClient.put(cred[0], this);
					sendMsg(new Message(0,"Login Successful!"));
				}else{
					sendMsg(new Message(Message.ERROR,"Username or password wrong!"));
				}
			}
		}
		
		void logout(Message msg){
			activeClient.remove(name);
			stop=true;
		}
		
		void addLogin(Message msg){
			String[] cred = msg.getContent().split(" ");
			if(cred.length>2){
				writeMsg(new Message(Message.ERROR,"No space is allowed!"));
				return;
			}
			if(cred.length<2){
				writeMsg(new Message(Message.ERROR,"Username or password can not be empty!"));
			}
			if(login.containsKey(cred[0])){
				writeMsg(new Message(Message.ERROR,"Username already exist!"));
			}else{
				login.put(cred[0], cred[1]);
			}
		}
		
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
		
	}
}

