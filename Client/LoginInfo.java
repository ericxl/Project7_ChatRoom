package assignment7.Client;

public class LoginInfo {
	boolean register = false;
	public String userName;
	public String passWord;
	
	public LoginInfo(String name, String pwd, boolean register){
		userName=name;
		passWord =pwd;
		this.register=register;
	}
}
