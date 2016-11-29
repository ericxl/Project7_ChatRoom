package assignment7;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

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

	Client client;

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

		client = new Client(Config.port, Config.endpoint);
		client.registerHandler(MsgType.RegisterResult, result-> OnRegister((RegisterResult)result));
		client.registerHandler(MsgType.LoginResult, result-> OnLogin((LoginResult) result));
		client.registerHandler(MsgType.AddFriendResult, result-> OnAddFriend((AddFriendResult) result));
		client.registerHandler(MsgType.SendPrivateMessageResult, result-> OnSendPrivateMessage((SendPrivateMessageResult) result));
		client.registerHandler(MsgType.SendGroupMessageResult, result-> OnSendGroupMessage((SendGroupMessageResult) result));
		client.registerHandler(MsgType.GetActiveGroupsResult, result-> OnGetActiveGroups((GetActiveGroupsResult) result));
		client.registerHandler(MsgType.CreateOrJoinGroupResult, result-> OnCreateOrJoinGroup((CreateOrJoinGroupResult) result));
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
				client.send(MsgType.RegisterRequest, new RegisterRequest(s[0], s[1], s[2], s[3]));
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
		}
		else if(state == CommandState.TO){
			groupChat = false;
			currentReceiver = command;
		}
		else if(state == CommandState.JOIN){
			client.send(MsgType.CreateOrJoinGroupRequest, new CreateOrJoinGroupRequest());
			switchToState(CommandState.TOGROUP);
		}
	}


	private void OnRegister(RegisterResult result){
		if(result.error == null){
			System.out.println("Successfully registered: " + result.username);
		} else {
			System.out.println("Register failed" + result.error.toString());
		}
	}

	private void OnLogin(LoginResult result){
		if(result.error == null){
			System.out.println("login success");
		} else {
			System.out.println("login failed" + result.error.toString());
		}
	}

	private void OnAddFriend(AddFriendResult result){
		if(result.error == null){
			System.out.println("Add friend success: " + result.friendDisplayName);
		} else {
			System.out.println("add friend failed" + result.error.toString());
		}
	}

	private void OnSendPrivateMessage (SendPrivateMessageResult result){
		if(result.error == null){
			System.out.println("send private msg success");
		} else {
			System.out.println("send private msg failed" + result.error.toString());
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

	private void OnSendGroupMessage (SendGroupMessageResult result){
		if(result.error == null){
			System.out.println("send group msg success");
		} else {
			System.out.println("send group msg failed" + result.error.toString());
		}
	}

	private void OnGetActiveGroups (GetActiveGroupsResult result){
		if(result.error == null){
			System.out.println("get active groups success: ");
			for (String group:result.activeGroups) {
				System.out.println(group);
			}
		} else {
			System.out.println("get active groups failed" + result.error.toString());
		}
	}

	private void OnCreateOrJoinGroup (CreateOrJoinGroupResult result){
		if(result.error == null){
			System.out.println("create or join group success: " + result.groupName);
		} else {
			System.out.println("create or join group failed" + result.error.toString());
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
