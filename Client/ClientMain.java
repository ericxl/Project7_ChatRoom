package assignment7.Client;

import assignment7.DataModel.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class ClientMain extends Application {
	enum CommandState {
		REG,
		LOGIN,
		ADD,
		CHAT,
		TOGROUP,
		TO,
		JOIN,
		QUIT
	}
	CommandState state = null;
	String currentReceiver = null;
	boolean groupChat = false;

	NetworkClient client;

	Label statusLabel;
	TextField inputField;
	@Override
	public void start(Stage primaryStage) {
		BorderPane paneForTextField = new BorderPane();
		paneForTextField.setPadding(new Insets(5, 5, 5, 5));
		paneForTextField.setStyle("-fx-border-color: royalblue");
		statusLabel = new Label("Chat");
		statusLabel.setMaxWidth(100);
		statusLabel.setPrefWidth(100);
		statusLabel.setMinWidth(100);
		statusLabel.setAlignment(Pos.CENTER);
		paneForTextField.setLeft(statusLabel);

		inputField = new TextField();
		inputField.setAlignment(Pos.BOTTOM_LEFT);
		inputField.setOnAction(event -> {
			ProcessCommand(inputField.getText());
		});
		inputField.textProperty().addListener((ov, oldValue, newValue) -> {
			String prompt = ov.getValue();
			if(prompt.length() > 3){
				if(prompt.charAt(prompt.length() - 1) == ' ' && prompt.charAt(0) == '-'){
					String command = prompt.substring(1, prompt.length() - 1).toUpperCase();
					try {
						switchToState(CommandState.valueOf(command));
					}
					catch (IllegalArgumentException e){
					}
					Platform.runLater(() -> {
						inputField.clear();
					});

				}
			}
		});
		paneForTextField.setCenter(inputField);

		BorderPane mainPane = new BorderPane();

		TextArea ta = new TextArea();
		mainPane.setCenter(new ScrollPane(ta));
		mainPane.setBottom(paneForTextField);

		Scene scene = new Scene(mainPane, 450, 200);
		primaryStage.setTitle("Client");
		primaryStage.setScene(scene);
		primaryStage.show();

		client = new NetworkClient(ClientConfig.port, ClientConfig.endpoint);
		client.registerHandler(MsgType.RegisterResult, result-> onRegister((RegisterResult)result));
		client.registerHandler(MsgType.LoginResult, result-> onLogin((LoginResult) result));
		client.registerHandler(MsgType.AddFriendResult, result-> onAddFriend((AddFriendResult) result));
		client.registerHandler(MsgType.SendPrivateMessageResult, result-> onSendPrivateMessage((SendPrivateMessageResult) result));
		client.registerHandler(MsgType.SendGroupMessageResult, result-> onSendGroupMessage((SendGroupMessageResult) result));
		client.registerHandler(MsgType.JoinGroupResult, result-> onJoinGroup((JoinGroupResult) result));
		client.registerHandler(MsgType.ChatMessage, msg -> onReceiveChatMessage((ChatMessage) msg));
	}
	private void switchToState(CommandState newState){
		if(state != newState){
			state = newState;
			String lowerCommand  = state.toString().toLowerCase();
			Platform.runLater(() -> {
				statusLabel.setText(Character.toUpperCase(lowerCommand.charAt(0)) + lowerCommand.substring(1));
				inputField.clear();
				String placeHolder = "";
				switch (newState){
					case ADD:
						placeHolder = "YOUR_FRIEND_USER_NAME";
						break;
					case CHAT:
						placeHolder = "YOUR_MESSAGE";
						break;
					case REG:
						placeHolder = "USERNAME PASSWORD DISPLAY_NAME EMAIL";
						break;
					case LOGIN:
						placeHolder = "USERNAME PASSWORD";
						break;
					case TOGROUP:
						placeHolder = "GROUP_NAME";
						break;
					case JOIN:
						placeHolder = "GROUP_NAME";
						break;
					case TO:
						placeHolder = "FRIEND_NAME";
						break;
				}
				inputField.setPromptText(placeHolder);
			});
			if(state == CommandState.QUIT){
				System.exit(0);
			}
		}
	}
	private void ProcessCommand (String command){
		Platform.runLater(() -> {
			inputField.clear();
		});
		if(state == CommandState.REG){
			try {
				String[] s = command.split(" ");
				client.send(MsgType.RegisterRequest, new RegisterRequest(s[0], s[1]));
				switchToState(CommandState.LOGIN);
			}catch(Throwable e){

			}
		}
		else if(state == CommandState.LOGIN){
			try {
				String[] s = command.split(" ");
				client.send(MsgType.LoginRequest, new LoginRequest(s[0], s[1]));
				switchToState(CommandState.TO);
			}catch(Throwable e){

			}
		}
		else if(state == CommandState.ADD){
			try {
				String[] s = command.split(" ");
				client.send(MsgType.AddFriendRequest, new AddFriendRequest(s[0]));
				switchToState(CommandState.TO);
			}catch(Throwable e){

			}
		}
		else if(state == CommandState.CHAT){
			try {
				if(groupChat){
					client.send(MsgType.SendGroupMessageRequest, new SendGroupMessageRequest(currentReceiver, command));
				}
				else {
					client.send(MsgType.SendPrivateMessageRequest, new SendPrivateMessageRequest(currentReceiver, command));
				}
			}catch(Throwable e){

			}
		}
		else if(state == CommandState.TOGROUP){
			groupChat = true;
			currentReceiver = command;
			switchToState(CommandState.CHAT);
		}
		else if(state == CommandState.TO){
			groupChat = false;
			currentReceiver = command;
			switchToState(CommandState.CHAT);
		}
		else if(state == CommandState.JOIN){
			client.send(MsgType.JoinGroupRequest, new JoinGroupRequest(command));
			switchToState(CommandState.TOGROUP);
		}
	}

	private void onRegister(RegisterResult result){
		if(result.error == null){
			System.out.println("Successfully registered: " + result.username);
		} else {
			System.out.println("Register failed" + result.error.toString());
		}
	}

	private void onLogin(LoginResult result){
		if(result.error == null){
			System.out.println("login success");
		} else {
			System.out.println("login failed" + result.error.toString());
		}
	}

	private void onAddFriend(AddFriendResult result){
		if(result.error == null){
			System.out.println("Add friend success: " + result.friendDisplayName);
		} else {
			System.out.println("add friend failed" + result.error.toString());
		}
	}

	private void onSendPrivateMessage (SendPrivateMessageResult result){
		if(result.error == null){
			System.out.println("send private msg success");
		} else {
			System.out.println("send private msg failed" + result.error.toString());
		}
	}
	
	private void onReceiveChatMessage(ChatMessage result){
		System.out.println("Received message from: " + result.from + " toUser: " + result.toUser + " toGroup: " + result.toGroup+ " msg: " + result.body);
	}

	private void onSendGroupMessage (SendGroupMessageResult result){
		if(result.error == null){
			System.out.println("send group msg success");
		} else {
			System.out.println("send group msg failed" + result.error.toString());
		}
	}

	private void onJoinGroup (JoinGroupResult result){
		if(result.error == null){
			System.out.println("join group: " + result.groupName + " success!");
		} else {
			System.out.println("join group failed" + result.error.toString());
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
