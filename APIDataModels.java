package assignment7;

import java.io.Serializable;

abstract class RequestBase implements Serializable {}
abstract class ResultBase implements Serializable {
	public ErrorCode error;

	public ResultBase(){}
	public ResultBase(ErrorCode code){
		this.error = code;
	}
}

class RegisterRequest extends RequestBase {
	public String username;
	public String password;
	public String displayName;
	public String email;

	public RegisterRequest(){}
	public RegisterRequest(String _username, String _password, String _displayName, String _email){
		this.username = _username;
		this.password = _password;
		this.displayName = _displayName;
		this.email = _email;
	}

}

class RegisterResult extends ResultBase {
	public String username;
	public String password;
	public String displayName;
	public String email;

	public RegisterResult(){}
	public RegisterResult(ErrorCode code) {
		super(code);
	}
	public RegisterResult(String _username, String _password, String _displayName, String _email){
		this.username = _username;
		this.password = _password;
		this.displayName = _displayName;
		this.email = _email;
	}
}

class LoginRequest extends RequestBase {
	public String username;
	public String password;

	public LoginRequest(){}
	public LoginRequest(String _username, String _password){
		this.username=_username;
		this.password=_password;
	}
}
class LoginResult extends ResultBase {
	public LoginResult(){}
	public LoginResult(ErrorCode code){
		super(code);
	}
}

class LogoutRequest extends RequestBase {
	public LogoutRequest(){}
}
class LogoutResult extends ResultBase {
	public LogoutResult(){}
	public LogoutResult(ErrorCode code){
		super(code);
	}
}

class AddFriendRequest extends RequestBase {
	public String username;

	public AddFriendRequest (){}
	public AddFriendRequest (String _username) {
		this.username = _username;
	}
}
class AddFriendResult extends ResultBase {
	String friendUsername;
	String friendDisplayName;
	public AddFriendResult (){}
	public AddFriendResult(ErrorCode code){
		super(code);
	}
	public AddFriendResult (String _friendUsername, String _friendDisplayName){
		this.friendUsername = _friendUsername;
		this.friendDisplayName = _friendDisplayName;
	}
}

class SendPrivateMessageRequest extends RequestBase {
	public String from;
	public String to;
	public String message;
	public SendPrivateMessageRequest(){}
	public SendPrivateMessageRequest(String from, String _to, String _message){
		this.from = from;
		this.to = _to;
		this.message = _message;
	}
}
class SendPrivateMessageResult extends ResultBase{
	public String from;
	public String to;
	public String message;
	public SendPrivateMessageResult(){}
	public SendPrivateMessageResult(String from, String to, String message){
		this.from = from;
		this.message=message;
		this.to = to;
	}
	public SendPrivateMessageResult(ErrorCode code){
		super(code);
	}
}

class AddToGroupRequest extends RequestBase{
	String name;
	public AddToGroupRequest(){}
	public AddToGroupRequest(String name){
		this.name = name;
	}
}

class AddToGroupResult extends ResultBase{
	String name;
	public AddToGroupResult(ErrorCode code){
		super(code);
	}
	public AddToGroupResult(String name, ErrorCode code){
		this(code);
		this.name = name;
	}
	public AddToGroupResult(String name){
		this.name = name;
	}
}

class SendGroupMessageRequest extends RequestBase {
	public String from;
	public String message;
	public SendGroupMessageRequest(){}
	public SendGroupMessageRequest(String from, String _message){
		this.from=from;
		this.message = _message;
	}
}
class SendGroupMessageResult extends ResultBase{
	public String from;
	public String message;
	public SendGroupMessageResult(){}
	public SendGroupMessageResult(ErrorCode code){
		super(code);
	}
	public SendGroupMessageResult(String _from, String _message){
		this.from=_from;
		this.message = _message;
	}
}

enum ErrorCode {
	NotAuthorized,
	WrongCredentials,
	UserAlreadyExists,
	UserDoesNotExist,
	NotAFriend,
	CannotAddSelfAsFriend,
	CannotSendMessageToSelf,
	UserNotOnline,
	GroupDoesNotExists,
	NotAFriend,
}
