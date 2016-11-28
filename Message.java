package assignment7;

import java.io.Serializable;
/*
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
*/

abstract class RequestBase implements Serializable {}
abstract class ResultBase implements Serializable {}

class LoginRequest extends RequestBase{
	public String username;
	public String password;

	public LoginRequest(String _username, String _password){
		this.username=_username;
		this.password=_password;
	}
}
class LoginResult extends ResultBase{
	public boolean success;

	public LoginResult(boolean _success){
		this.success=_success;
	}
}

class LogoutRequest extends RequestBase {
}
class LogoutResult extends RequestBase {
}