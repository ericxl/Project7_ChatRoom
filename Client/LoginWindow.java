package assignment7.Client;

import assignment7.DataModel.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.layout.*;

public class LoginWindow {
	private NetworkClient client;

	private Stage window;

	private TextField userTextField;
	private PasswordField pwBox;
	private Label status;
	private Button signInButton;
	private Button registerButton;
	public LoginWindow(NetworkClient client){
		this.client = client;

		start();
	}

	private void start(){
		window = new Stage();
		
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Login");
		window.setMinWidth(400);

		window.setScene(loginScene());
		window.setOnCloseRequest((t)->{
			Platform.exit();
			System.exit(0);
		});
		if(client != null) {
			client.registerHandler(MsgType.RegisterResult, result -> onRegister((RegisterResult) result));
			client.registerHandler(MsgType.LoginResult, result -> onLogin((LoginResult) result));
		}
		else{
			signInButton.setDisable(true);
			registerButton.setDisable(true);
			status.setText("Can't establish network connection to server!");
			status.setTextFill(Color.RED);
		}

		window.show();
	}

	private Scene loginScene(){
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		// username and password grid
		GridPane fieldGrid = new GridPane();
		fieldGrid.setAlignment(Pos.CENTER);
		fieldGrid.setHgap(10);
		fieldGrid.setVgap(10);
		fieldGrid.setPadding(new Insets(25, 25, 25, 25));

		Label userName = new Label("User Name:");
		userTextField = new TextField();
		Label pw = new Label("Password:");
		pwBox = new PasswordField();

		fieldGrid.add(userName, 0, 0);
		fieldGrid.add(userTextField, 1, 0);
		fieldGrid.add(pw, 0, 1);
		fieldGrid.add(pwBox, 1, 1);

		grid.add(fieldGrid, 0, 0);

		status = new Label();
		status.setTextAlignment(TextAlignment.CENTER);
		grid.add(status, 0, 1);

		signInButton = new Button("Sign in");
		signInButton.setOnAction(e->{
			String name=userTextField.getText();
			String pwd=pwBox.getText();
			client.send(MsgType.LoginRequest, new LoginRequest(name, pwd));
		});

		registerButton = new Button("Register");
		registerButton.setOnAction(e-> {
			String name=userTextField.getText();
			String pwd=pwBox.getText();
			client.send(MsgType.RegisterRequest, new RegisterRequest(name, pwd));
		});
		VBox hbBtn = new VBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_CENTER);
		hbBtn.getChildren().addAll(signInButton,registerButton);
		grid.add(hbBtn, 0, 2);

		Scene scene = new Scene(grid, 400, 375);
		return scene;
	}

	private void onRegister(RegisterResult result){
		if(result.error == null){
			Platform.runLater(() -> {
				status.setText("Registration Successful!");
				status.setTextFill(Color.GREEN);
			});
		} else {
			if(result.error == ErrorCode.UserAlreadyExists){
				Platform.runLater(() -> {
					status.setText("User Already Exists!");
					status.setTextFill(Color.RED);
				});
			}
		}
	}

	private void onLogin(LoginResult result){
		if(result.error == null){
			Platform.runLater(() -> {
				ChatWindow w = new ChatWindow(client);
				w.setClientName(result.username);
				w.start();
				window.close();
			});
		} else {
			System.out.println("wrong");
			if(result.error == ErrorCode.WrongCredentials){
				Platform.runLater(() -> {
					status.setText("Wrong Credentials!");
					status.setTextFill(Color.RED);
				});
			}
		}
	}
}
