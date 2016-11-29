package assignment7;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ClientMain extends Application {

	Client client;

	@Override
	public void start(Stage primaryStage) {
		BorderPane paneForTextField = new BorderPane();
		paneForTextField.setPadding(new Insets(5, 5, 5, 5));
		paneForTextField.setStyle("-fx-border-color: green");
		paneForTextField.setLeft(new Label("Chat Room: "));

		TextField tf = new TextField();
		tf.setAlignment(Pos.BOTTOM_RIGHT);
		tf.setOnAction(event -> {
			client.send(MsgType.LoginRequest, new LoginRequest("user", "pass"));
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

		client = new Client(Config.port, Config.endpoint);
		client.send(MsgType.RegisterRequest, new RegisterRequest("user", "pass", "eric", "eric@gmail.com"));
		client.registerHandler(MsgType.RegisterResult, result-> OnRegister((RegisterResult)result));
		client.registerHandler(MsgType.LoginResult, result-> OnLogin((LoginResult) result));
		client.registerHandler(MsgType.AddFriendResult, result-> OnAddFriend((AddFriendResult) result));
		client.registerHandler(MsgType.SendPrivateMessageResult, result-> onReceiveMessage((SendPrivateMessageResult) result));
		client.registerHandler(MsgType.AddToGroupResult, result-> OnAddToGroup((AddToGroupResult) result));

	}

	private void OnLogin(LoginResult result){
		if(result.error == null){
			System.out.println("login success");
		} else {
			System.out.println("login failed");
		}
	}

	private void OnRegister(RegisterResult result){
		if(result.error != null){
			System.out.println("Register success");
		} else {
			System.out.println("Register failed");
		}
	}

	private void OnAddFriend(AddFriendResult result){
		if(result.error != null){
			System.out.println("Add friend success");
		} else {
			System.out.println("Register failed");
		}
	}
	
	private void onReceiveMessage(SendPrivateMessageResult result){
		if(result.error != null){
			if(result.error== ErrorCode.NotAFriend){
				System.out.println(result.to+" is not a friend!");
			}else{
				System.out.println("Chat Error!");
			}
		}else{
			System.out.println(result.from +": " +result.message);
		}
	}
	
	private void OnAddToGroup(AddToGroupResult result){
		if(result.error != null){
			System.out.println("Add "+result.name+" to group failed!");
		}else{
			System.out.println("Add "+result.name+" to group successful!");
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
