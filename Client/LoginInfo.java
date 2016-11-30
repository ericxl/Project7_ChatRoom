package assignment7.Client;

public class LoginInfo {
	boolean register = false;
	public String serverAddress = "localhost";
	public String userName;
	public String passWord;
	
	public LoginInfo(String address, String name, String pwd, boolean register){
		serverAddress = address;
		userName=name;
		passWord =pwd;
		this.register=register;
	}
}
