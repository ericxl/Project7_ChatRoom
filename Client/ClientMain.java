package assignment7.Client;

import javafx.application.*;
import javafx.stage.*;

import java.io.IOException;

public class ClientMain extends Application {

	NetworkClient client;

	@Override
	public void start(Stage primaryStage) {
		try{
			client = new NetworkClient(ClientConfig.port, ClientConfig.endpoint);
		}
		catch(IOException e){
			client = null;
		}
		LoginWindow loginWindow = new LoginWindow(client);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
