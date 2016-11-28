package assignment7;

import java.io.*;
import java.net.*;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ClientMain extends Application {
	ObjectOutputStream toServer;
	ObjectInputStream fromServer;

	@Override // Override the start method in the Application class
	public void start(Stage primaryStage) {
		// Panel p to hold the label and text field
		BorderPane paneForTextField = new BorderPane();
		paneForTextField.setPadding(new Insets(5, 5, 5, 5));
		paneForTextField.setStyle("-fx-border-color: green");
		paneForTextField.setLeft(new Label("Enter a radius: "));

		TextField tf = new TextField();
		tf.setAlignment(Pos.BOTTOM_RIGHT);
		tf.setOnAction(event -> {
			try {
				toServer.writeByte(MsgType.LoginRequest);
				toServer.writeObject(new LoginRequest("user", "testPass"));
			}
			catch (IOException ex) {
				System.err.println(ex);
			}
		});
		paneForTextField.setCenter(tf);

		BorderPane mainPane = new BorderPane();

		TextArea ta = new TextArea();
		mainPane.setCenter(new ScrollPane(ta));
		mainPane.setTop(paneForTextField);


		Scene scene = new Scene(mainPane, 450, 200);
		primaryStage.setTitle("Client");
		primaryStage.setScene(scene);
		primaryStage.show();

		try {
			Socket socket = new Socket(Config.endpoint, Config.port);
			toServer = new ObjectOutputStream(socket.getOutputStream());
			fromServer = new ObjectInputStream(socket.getInputStream());
			Thread t = new Thread(new ServerHandler());
			t.start();
			System.out.println("io ready");
		}
		catch (IOException ex) {
			ta.appendText(ex.toString() + '\n');
		}
	}

	private void OnLogin(LoginResult result){
		if(result.success){
			System.out.println("login success");
		} else {
			System.out.println("login failed");
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	class ServerHandler implements Runnable {

		public void run() {
			while(true){
				try{
					byte type = fromServer.readByte();
					ResultBase result;
					switch(type){
						case MsgType.LoginResult:
							result = (LoginResult) fromServer.readObject();
							OnLogin((LoginResult)result);
							break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}

			}
		}

		private void OnLogin(LoginResult result){
			if(result.success){
				System.out.println("login success");
			} else {
				System.out.println("login failed");
			}
		}
	}
}
