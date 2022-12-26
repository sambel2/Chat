import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiServer extends Application{


	TextField s1,s2,s3,s4, c1;
	Button serverChoice,clientChoice,b1;
	HashMap<String, Scene> sceneMap;
	GridPane grid;
	HBox buttonBox;
	VBox clientBox;
	Scene startScene;
	BorderPane startPane;
	Server serverConnection;
	Client clientConnection;

	ListView<String> listItems, listItems2;
	ListView clientList = new ListView();
	ListView receivers = new ListView();
	messageInfo info = new messageInfo();
	ArrayList<Integer> selectClients = new ArrayList<Integer>();
	boolean listclicked = false;


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("The Networked Client/Server GUI Example");

		this.serverChoice = new Button("Server");
		this.serverChoice.setStyle("-fx-pref-width: 300px");
		this.serverChoice.setStyle("-fx-pref-height: 300px");

		this.serverChoice.setOnAction(e->{ primaryStage.setScene(sceneMap.get("server"));
			primaryStage.setTitle("This is the Server");
			serverConnection = new Server(data -> {
				Platform.runLater(()->{
					info = new messageInfo();
					info = (messageInfo) data;

					listItems.getItems().add(info.msg);
				});

			});

		});


		this.clientChoice = new Button("Client");
		this.clientChoice.setStyle("-fx-pref-width: 300px");
		this.clientChoice.setStyle("-fx-pref-height: 300px");

		this.clientChoice.setOnAction(e-> {primaryStage.setScene(sceneMap.get("client"));
			primaryStage.setTitle("This is a client");
			clientConnection = new Client(data->{
				Platform.runLater(()->{
					listclicked = false;
					info = (messageInfo) data;
					info.listclicked = false;

					// flag to check if a client should be removed or added from the list //

					listItems2.getItems().add(info.msg);

					// Add all clients
					clientList.getItems().clear();
					info.clickedClients.clear();
					for(int id: info.allClients) {
						clientList.getItems().add(id);
					}
				});
			});

			clientConnection.start();
		});


		this.buttonBox = new HBox(400, serverChoice, clientChoice);
		startPane = new BorderPane();
		startPane.setPadding(new Insets(70));
		startPane.setCenter(buttonBox);

		startScene = new Scene(startPane, 800,800);

		listItems = new ListView<String>();
		listItems2 = new ListView<String>();

		c1 = new TextField();
		c1.setPromptText("type your msg here:");
		b1 = new Button("Send");
		b1.setOnAction(e->{
			info.msg = c1.getText();
			// Check if listView is clicked or not
			// Update the info.clients accordingly
			if(listclicked) {
				info.listclicked = true;
				for(int i: info.clickedClients) {
					receivers.getItems().add("Clients " + info.clickedClients +" received your message!");
				}
			} else {
				receivers.getItems().add("All clients received your message");
			}
			clientConnection.send(info);
			c1.clear();
			info.clickedClients.clear();
		});

		sceneMap = new HashMap<String, Scene>();

		sceneMap.put("server",  createServerGui());
		sceneMap.put("client",  createClientGui());

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});



		primaryStage.setScene(startScene);
		primaryStage.show();

	}

	public Scene createServerGui() {

		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setStyle("-fx-background-color: coral");

		pane.setCenter(listItems);

		return new Scene(pane, 500, 400);


	}

	public Scene createClientGui() {

		Label otherClients = new Label("List of all the Clients Connected:");
		otherClients.setMinSize(30, 30);

		Label Receivers = new Label("Sent to:");
		VBox vbox = new VBox(10, Receivers, receivers);
		clientBox = new VBox(10, c1,b1,listItems2);
		HBox hbox = new HBox(10, clientBox, vbox);
		VBox v2 = new VBox(10, hbox, otherClients, clientList);
		clientBox.setStyle("-fx-background-color: blue");

		clientList.setOnMouseClicked(e->{
			Integer clickOnList = (Integer) clientList.getSelectionModel().getSelectedItem();

			info.clickedClients.add(clickOnList);
			listclicked = true;
		});

		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(20));
		pane.setStyle("-fx-background-color: lightblue");
		pane.setCenter(v2);

		return new Scene(pane, 450, 350);

	}

}