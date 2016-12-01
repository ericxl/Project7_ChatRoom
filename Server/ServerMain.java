/* EE422C Project 7 submission by
 * Xiaoyong Liang
 * XL5432
 * 16480
 * Yuankai Yue
 * yy7347
 * 16465
 * Slip days used: <1>
 * Fall 2016
 */
package assignment7.Server;

import java.util.*;

public class ServerMain extends Observable {

	public static void main(String[] args) {
		NetworkServer mainServer = new NetworkServer(ServerConfig.port);
		mainServer.setUpDatabase();
		mainServer.setUpNetworking();
	}

}

