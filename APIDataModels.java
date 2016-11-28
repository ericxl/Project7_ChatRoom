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
	public String to;
	public String message;
	public SendPrivateMessageRequest(){}
	public SendPrivateMessageRequest(String _to, String _message){
		this.to = _to;
		this.message = _message;
	}
}
class SendPrivateMessageResult extends ResultBase{
	public SendPrivateMessageResult(){}
	public SendPrivateMessageResult(ErrorCode code){
		super(code);
	}
}

class SendGroupMessageRequest extends RequestBase {
	public String to;
	public String message;
	public SendGroupMessageRequest(){}
	public SendGroupMessageRequest(String _to, String _message){
		this.to = _to;
		this.message = _message;
	}
}
class SendGroupMessageResult extends ResultBase{
	public SendGroupMessageResult(){}
	public SendGroupMessageResult(ErrorCode code){
		super(code);
	}
}

class GetActiveGroupsRequest extends RequestBase{
	public GetActiveGroupsRequest(){}
}
class GetActiveGroupsResult extends ResultBase{
	public String[] activeGroups;

	public GetActiveGroupsResult(){}
	public GetActiveGroupsResult(ErrorCode code){
		super(code);
	}
	public GetActiveGroupsResult(String[] _groups){
		this.activeGroups = _groups;
	}
}

class CreateOrJoinGroupRequest extends RequestBase{
	public String groupName;
	public CreateOrJoinGroupRequest(){}
}
class CreateOrJoinGroupResult extends ResultBase{
	public String groupName;

	public CreateOrJoinGroupResult(){}
	public CreateOrJoinGroupResult(ErrorCode code){
		super(code);
	}
	public CreateOrJoinGroupResult (String _groupName){
		this.groupName = _groupName;
	}
}


enum ErrorCode {
	NotAuthorized,
	WrongCredentials,
	UserAlreadyExists,
	UserDoesNotExist,
	CannotAddSelfAsFriend,
	CannotSendMessageToSelf,
	UserNotOnline
}