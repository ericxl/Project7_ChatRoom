package assignment7.Client;

import assignment7.DataModel.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
/**
 * Created by Eric on 11/30/16.
 */
public class ChatWindow {

    NetworkClient client;
    enum CommandState {
        CHAT,
        TOGROUP,
        TO
    }
    CommandState state = null;
    String currentReceiver = null;
    String clientName = null;
    boolean groupChat = false;
    String[] friends = null;

    TextField friendName;
    TextField groupName;
    ComboBox<String> friendList;
    Label statusLabel;
    TextField inputField;
    TextArea ta;

    public ChatWindow(NetworkClient client){
        this.client = client;

        client.registerHandler(MsgType.AddFriendResult, result-> onAddFriend((AddFriendResult) result));
        client.registerHandler(MsgType.SendPrivateMessageResult, result-> onSendPrivateMessage((SendPrivateMessageResult) result));
        client.registerHandler(MsgType.SendGroupMessageResult, result-> onSendGroupMessage((SendGroupMessageResult) result));
        client.registerHandler(MsgType.JoinGroupResult, result-> onJoinGroup((JoinGroupResult) result));
        client.registerHandler(MsgType.ChatMessage, msg -> onReceiveChatMessage((ChatMessage) msg));
        client.registerHandler(MsgType.GetFriendsResult, result -> onGetFriends((GetFriendsResult) result));
    }

    public void start(){
        Stage primaryStage = new Stage();

        primaryStage.setOnCloseRequest((t)->{
            Platform.exit();
            System.exit(0);
        });

        BorderPane paneForTextField = new BorderPane();
        paneForTextField.setPadding(new Insets(5, 5, 5, 5));
        paneForTextField.setStyle("-fx-border-color: royalblue");
        statusLabel = new Label("Chat");
        statusLabel.setMaxWidth(100);
        statusLabel.setPrefWidth(100);
        statusLabel.setMinWidth(100);
        statusLabel.setAlignment(Pos.CENTER);
        paneForTextField.setLeft(statusLabel);

        Button enter = new Button("Send");
        enter.setOnAction(e->{
            if(!groupChat){
                sendPrivateMessage(inputField.getText());
            }else{
                sendGroupMessage(inputField.getText());
            }
            inputField.clear();
        });

        enter.setAlignment(Pos.BOTTOM_RIGHT);
        paneForTextField.setLeft(statusLabel);
        paneForTextField.setRight(enter);

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

        friendList = new ComboBox<>();
        friendList.setMinWidth(100);
        friendList.setOnShowing(e->{
            client.send(MsgType.GetFriendsRequest, null);
            if(friends!=null){
                friendList.getItems().clear();
                for(String friend:friends){
                    friendList.getItems().add(friend);
                }
            }
        });
        friendList.setOnAction(e->{
            currentReceiver=friendList.getValue();
        });

        friendName = new TextField();
        Button addFriend = new Button("Add Friend");
        addFriend.setOnAction(e->{
            client.send(MsgType.AddFriendRequest, new AddFriendRequest(friendName.getText()));
        });

        groupName = new TextField();
        Button joinGroup = new Button("Join Group");
        joinGroup.setOnAction(e->{
            client.send(MsgType.JoinGroupRequest, new JoinGroupRequest(groupName.getText()));
        });

        Button toGroup = new Button("To Group");
        toGroup.setOnAction(e->{
            groupChat=true;
        });

        VBox vb = new VBox();
        vb.getChildren().addAll(friendList,friendName,addFriend,groupName,joinGroup,toGroup);
        BorderPane mainPane = new BorderPane();

        ta = new TextArea();
        mainPane.setCenter(new ScrollPane(ta));
        mainPane.setBottom(paneForTextField);
        mainPane.setRight(vb);

        Scene scene = new Scene(mainPane, 650, 400);
        primaryStage.setTitle("Hello " + clientName + "!");
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    public void setClientName(String name){
        clientName = name;
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
                    case CHAT:
                        placeHolder = "YOUR_MESSAGE";
                        break;
                    case TOGROUP:
                        placeHolder = "GROUP_NAME";
                        break;
                    case TO:
                        placeHolder = "FRIEND_NAME";
                        break;
                }
                inputField.setPromptText(placeHolder);
            });
        }
    }
    private void ProcessCommand (String command){
        Platform.runLater(() -> {
            inputField.clear();
        });
        if(state == CommandState.CHAT){
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
    }

    private void sendPrivateMessage(String message){
        client.send(MsgType.SendPrivateMessageRequest, new SendPrivateMessageRequest(currentReceiver, message));
    }

    private void sendGroupMessage(String message){
        client.send(MsgType.SendGroupMessageRequest, new SendGroupMessageRequest(currentReceiver, message));
    }

    private void onAddFriend(AddFriendResult result){
        if(result.error == null){
            System.out.println("Add friend success: " + result.friendUsername);
        } else {
            System.out.println("add friend failed" + result.error.toString());
        }
    }

    private void onGetFriends(GetFriendsResult result){
        friends = result.friends;
    }

    private void onSendPrivateMessage (SendPrivateMessageResult result){
        if(result.error == null){
            System.out.println("send private msg success");
        } else {
            System.out.println("send private msg failed" + result.error.toString());
        }
    }

    private void onReceiveChatMessage(ChatMessage result){
        String msg = result.from+": "+result.body+"\n";
        ta.appendText(msg);
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

}
