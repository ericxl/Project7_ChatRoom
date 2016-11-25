package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class ServerMain extends Observable {
	private Map<String,String> login = new HashMap<>();
	Map<String,Socket> aliveSocket = new HashMap<>();
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
		ServerSocket serverSock = new ServerSocket(4242);
		while (true) {
			Socket clientSocket = serverSock.accept();
			ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
			Thread t = new Thread(new ClientHandler(clientSocket));
			t.start();
			this.addObserver(writer);
			System.out.println("got a connection");
		}
	}
	
	private boolean addClient(String name, String pwd){
		if(login.containsKey(name)) return false;
		login.put(name, pwd);
		return true;
	}
	
	class ClientHandler implements Runnable {
		private BufferedReader reader;
		private PrintWriter writer;
		private Socket sock;
		private String name;

		public ClientHandler(Socket clientSocket) {
			sock = clientSocket;
			try {
				reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				writer = new PrintWriter(sock.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			String message;
			StringBuilder history=new StringBuilder();
			try {
				//Create new login
				if(reader.readLine().equals("New login.")){
					String[] newLogin=reader.readLine().split(" ");
					System.out.println(newLogin[0]+"----"+ newLogin[1]);
					addClient(newLogin[0],newLogin[1]);
					writer.println("New login created.");
					writer.flush();
				}
				//login
				while ((message = reader.readLine()) != null) {
					System.out.println(message);
					String[] clientLogin=message.split(" ");
					if(clientLogin.length==2){
						if(login.get(clientLogin[0]).equals(clientLogin[1])){
							aliveSocket.put(clientLogin[0], sock);
							name = clientLogin[0];
							writer.println("Login successful.");
							writer.flush();
							break;
						}
					}
					writer.println("1");
					writer.flush();
					System.out.println("Login information wrong!");
				}
				//build relation between chat partners
				if(!individualChat.containsKey(name)){
					while ((message = reader.readLine()) != null) {
						if(aliveSocket.containsKey(message)){
							individualChat.put(message, name);
							individualChat.put(name,message);
						}
					}
				}
				
				// read message
				while ((message = reader.readLine()) != null) {
					history.append(message+"\n");
					System.out.println("server read "+message);
					setChanged();
					notifyObservers(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

