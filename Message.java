package assignment7;

import java.io.Serializable;

abstract class RequestBase implements Serializable {}
abstract class ResultBase implements Serializable {}

class RegisterRequest extends RequestBase {
	public String username;
	public String password;
	public String displayName;
	public String email;

	public RegisterRequest(String _username, String _password, String _displayName, String _email){
		this.username = _username;
		this.password = _password;
		this.displayName = _displayName;
		this.email = _email;
	}
}

class RegisterResult extends ResultBase {
	public boolean success;

	public RegisterResult(boolean _success){
		this.success=_success;
	}
}

class LoginRequest extends RequestBase {
	public String username;
	public String password;

	public LoginRequest(String _username, String _password){
		this.username=_username;
		this.password=_password;
	}
}
class LoginResult extends ResultBase {
	public boolean success;

	public LoginResult(boolean _success){
		this.success=_success;
	}
}

class LogoutRequest extends RequestBase {
}
class LogoutResult extends RequestBase {
}