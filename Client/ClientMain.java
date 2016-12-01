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
package assignment7.Client;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.*;

import java.io.IOException;

public class ClientMain extends Application {

	NetworkClient client;
	Stage stage;
	TextField address;
	Label status;
	@Override
	public void start(Stage primaryStage) {
		stage = primaryStage;
		primaryStage.setTitle("Connect");
		primaryStage.setMinWidth(400);


		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));


		Button connectButton = new Button("Connect");
		connectButton.setOnAction(e->{
			showLoginWindow();
		});
		address = new TextField();
		address.setPromptText(ClientConfig.address);
		address.setText(ClientConfig.address);
		address.setAlignment(Pos.CENTER);
		connectButton.setAlignment(Pos.CENTER);
		status = new Label();
		VBox hbBtn = new VBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_CENTER);
		hbBtn.getChildren().addAll(address, connectButton, status);

		grid.add(hbBtn, 0, 0);

		Scene scene = new Scene(grid, 400, 375);



		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest((t)->{
			Platform.exit();
			System.exit(0);
		});

		primaryStage.show();
	}

	private void showLoginWindow(){
		try{
			String a = address.getText();
			if (a == null || a.isEmpty()){
				a = ClientConfig.address;
			}
			client = new NetworkClient(ClientConfig.port, a);
			LoginWindow loginWindow = new LoginWindow(client);
			stage.close();
		}
		catch(IOException e){
			status.setText("Could not establish connection with server");
			client = null;
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
