package assignment7;

import java.io.Serializable;

public class Message implements Serializable{
	public static final int MESSAGE=0, LOGIN=1,LOGOUT=2,FRIENDS=3,NEW_LOGIN=4,STOP=5,ERROR=6,GROUPMESSAGE=7,ADDTOGROUP=8;
	private int type;
	private String content;
	private String recipient;
	
	public Message(int type, String content){
		this(type,null,content);
	}
	
	public Message(int type,String recipient,String content){
		this.type=type;
		this.content=content;
		this.recipient=recipient;
	}
	
	public int getType(){
		return type;
	}
	
	public String getContent(){
		return content;
	}
	
	public String getRecipient(){
		return recipient;
	}
}
