package assignment7.Server;

import java.io.*;
import java.util.*;
import assignment7.DataModel.*;

public class ClientObserver extends ObjectOutputStream implements Observer {
	private List<String> group = new ArrayList<>();
	public ClientObserver(OutputStream out) throws IOException {
		super(out);
	}
	
	public boolean addToGroup(String name){
		if(group.contains(name)) return false;
		group.add(name);
		return true;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		ChatMessage message = (ChatMessage) arg;
		if(group.contains(message.from)){
			try {
				this.writeObject(arg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}