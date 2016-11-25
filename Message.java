package assignment7;

import java.io.Serializable;

public class Message implements Serializable{
	public static final int MESSAGE=0, LOGIN=1,LOGOUT=2,FRIENDS=3;
	private int type;
	private String content;
	
	public Message(int type, String content){
		this.type=type;
		this.content=content;
	}
	
	public int getType(){
		return type;
	}
	
	public String getContent(){
		return content;
	}
}
