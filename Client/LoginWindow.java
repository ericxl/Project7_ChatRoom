package assignment7.Client;
import javafx.stage.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
 
public class LoginWindow {
	private String name,pwd;
	private boolean register;
	private Stage window;

	public LoginInfo display(){
		
		window = new Stage();
		
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Login");
		window.setMinWidth(400);
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		Text scenetitle = new Text("Sign In");
		grid.add(scenetitle, 0, 0, 2, 1);
		
		Label serverAddress = new Label("Server Address:");
		grid.add(serverAddress, 0, 1);

		Label userName = new Label("User Name:");
		grid.add(userName, 0, 2);

		TextField userTextField = new TextField();
		grid.add(userTextField, 1, 2);

		Label pw = new Label("Password:");
		grid.add(pw, 0, 3);

		PasswordField pwBox = new PasswordField();
		grid.add(pwBox, 1, 3);
		
		Button btn1 = new Button("Sign in");
		btn1.setOnAction(e->{
			name=userTextField.getText();
			pwd=pwBox.getText();
			register=false;
			window.close();
		});
		
		Button btn2 = new Button("Register");
		btn2.setOnAction(e->{
			window.setScene(registerScene());
		});
		VBox hbBtn = new VBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().addAll(btn1,btn2);
		grid.add(hbBtn, 1, 5);
		
		Scene scene = new Scene(grid, 400, 375);

		window.setScene(scene);
		window.showAndWait();
		return new LoginInfo(name,pwd,register);
	}
	
	private Scene registerScene(){
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		
		Text scenetitle = new Text("Register");
		grid.add(scenetitle, 0, 0, 2, 1);
		
		Label serverAddress = new Label("Server Address:");
		grid.add(serverAddress, 0, 1);
		TextField serverTextField = new TextField();
		grid.add(serverTextField, 1, 1);
		
		Label userName = new Label("User Name:");
		grid.add(userName, 0, 2);

		TextField userTextField = new TextField();
		grid.add(userTextField, 1, 2);

		Label pw = new Label("Password:");
		grid.add(pw, 0, 3);

		PasswordField pwBox = new PasswordField();
		grid.add(pwBox, 1, 3);
		
		Button btn = new Button("Register");
		btn.setOnAction(e->{
			name=userTextField.getText();
			pwd=pwBox.getText();
			register=true;
			window.close();
		});
		VBox hbBtn = new VBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().addAll(btn);
		grid.add(hbBtn, 1, 5);
		Scene scene = new Scene(grid,400,375);
		return scene;
	}
}
