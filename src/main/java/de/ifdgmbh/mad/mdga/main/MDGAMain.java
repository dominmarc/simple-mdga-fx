package de.ifdgmbh.mad.mdga.main;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MDGAMain extends Application{
	private double xOffset = 0;
	private double yOffset = 0;

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("MDGA.fxml"));
		Scene myScene = new Scene(root);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		myScene.setFill(Color.TRANSPARENT);
		root.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = event.getSceneX();
				yOffset = event.getSceneY();
			}
		});
		root.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (yOffset < 28) {
					primaryStage.setX(event.getScreenX() - xOffset);
					primaryStage.setY(event.getScreenY() - yOffset);
				}
			}
		});

		primaryStage.setScene(myScene);
		primaryStage.getIcons()
		.add(new Image(getClass().getResource("/de/ifdgmbh/mad/mdga/images/gamefield.png").toString()));
		primaryStage.getScene().getStylesheets().add(getClass().getResource("StyleFile.css").toString());
		primaryStage.setTitle("Man Don't Get Angry");
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
