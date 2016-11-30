package assignment7.Server;

import java.util.*;

public class ServerMain extends Observable {

	public static void main(String[] args) {
		NetworkServer mainServer = new NetworkServer(ServerConfig.port);
		mainServer.setUpDatabase();
		mainServer.setUpNetworking();
	}

}

