package assignment7;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

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