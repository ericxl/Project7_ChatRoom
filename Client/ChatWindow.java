package assignment7.Client;

import assignment7.DataModel.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
/**
 * Created by Eric on 11/30/16.
 */
public class ChatWindow {
    private NetworkClient client;
    String recipient;
    String self;
    boolean isGroup;

    Stage primaryStage;
    TextField inputField;
    TextArea ta;
    public ChatWindow(NetworkClient _client, String self, String to, boolean _isGroup){
        this.client = _client;
        this.isGroup = _isGroup;
        this.self = self;
        this.recipient = to;

        if(isGroup){
            client.registerHandler(MsgType.SendGroupMessageResult, result-> onSendGroupMessage((SendGroupMessageResult) result));
            client.registerHandler(MsgType.JoinedGroupMessage, result -> onReceiveJoinedGroupMessage((JoinedGroupMessage) result));
        }
        else{
            client.registerHandler(MsgType.SendPrivateMessageResult, result-> onSendPrivateMessage((SendPrivateMessageResult) result));
        }
        client.registerHandler(MsgType.ChatMessage, msg -> onReceiveChatMessage((ChatMessage) msg));
    }

    public void start(){
        primaryStage = new Stage();

        BorderPane paneForTextField = new BorderPane();
        paneForTextField.setPadding(new Insets(5, 5, 5, 5));
        paneForTextField.setStyle("-fx-border-color: royalblue");

        Button enter = new Button("Send");
        enter.setOnAction(e->{
            if(isGroup){
                client.send(MsgType.SendGroupMessageRequest, new SendGroupMessageRequest(recipient, inputField.getText()));
            }else {
                client.send(MsgType.SendPrivateMessageRequest, new SendPrivateMessageRequest(recipient, inputField.getText()));
            }
            inputField.clear();
        });

        enter.setAlignment(Pos.BOTTOM_RIGHT);
        paneForTextField.setRight(enter);

        inputField = new TextField();
        inputField.setAlignment(Pos.BOTTOM_LEFT);
        inputField.setOnAction(event -> {
            if(isGroup){
                client.send(MsgType.SendGroupMessageRequest, new SendGroupMessageRequest(recipient, inputField.getText()));
            }else {
                client.send(MsgType.SendPrivateMessageRequest, new SendPrivateMessageRequest(recipient, inputField.getText()));
            }
            inputField.clear();
        });
        paneForTextField.setCenter(inputField);

        BorderPane mainPane = new BorderPane();

        ta = new TextArea();
        mainPane.setCenter(new ScrollPane(ta));
        mainPane.setBottom(paneForTextField);

        Scene scene = new Scene(mainPane, 650, 400);
        primaryStage.setTitle(recipient);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void show(){
        primaryStage.show();
    }

    private void onSendPrivateMessage (SendPrivateMessageResult result){
        if(result.error == null){
            String text = "Send private msg success";
            System.out.println(text);
        } else {
            String text = "Send private msg failed" + result.error.toString();
            System.out.println(text);
        }
    }

    private void onSendGroupMessage (SendGroupMessageResult result){
        if(result.error == null){
            String text = "Send group msg success";
            System.out.println(text);
        } else {
            String text = "Send group msg failed" + result.error.toString();
            System.out.println(text);
        }
    }

    private void onReceiveChatMessage(ChatMessage result){
        if(isGroup){
            if (result.toGroup.equals(this.recipient)) {
                String msg = result.from+": "+result.body+"\n";
                ta.appendText(msg);
                System.out.println("Received message from: " + result.from + " toUser: " + result.toUser + " toGroup: " + result.toGroup+ " msg: " + result.body);
            }
        }else{
            if (result.toUser != null && result.toUser.equals(recipient) || result.toUser.equals(this.self)) {
                String msg = result.from+": "+result.body+"\n";
                ta.appendText(msg);
                System.out.println("Received message from: " + result.from + " toUser: " + result.toUser + " toGroup: " + result.toGroup+ " msg: " + result.body);
            }
        }
    }

    private void onReceiveJoinedGroupMessage(JoinedGroupMessage result){
        if(recipient.equals(result.joinedGroup)){
            String msg = result.username+" just joined this group. Say hello!\n";
            ta.appendText(msg);
        }
        System.out.println("Received joined group message from " + result.username);
    }
}
