package application.menu;

import Resource.Resource;
import application.*;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MenuPane extends StackPane implements UnchangeableBackground{

	public MenuPane() {
		VBox menu = new VBox();
		Canvas title = new Canvas();
		GraphicsContext gc = title.getGraphicsContext2D();
		MyButton playButton = new MyButton("Play", 20);
		MyButton settingButton = new MyButton("Setting", 20);
		MyButton exitButton = new MyButton("Exit", 20);

		this.setPrefSize(750, 600);
		setBackgroundWithImage();

		menu.setAlignment(Pos.CENTER);
		menu.setSpacing(15);
		menu.setTranslateY(-50);

		title.setHeight(300);
		title.setWidth(300);
		setTitleImage(gc);

		playButton.setPrefWidth(200);
		settingButton.setPrefWidth(200);
		exitButton.setPrefWidth(200);
		playButton.setBackgroundWithImage(new Image(Resource.BUTTON_FRAME));
		settingButton.setBackgroundWithImage(new Image(Resource.BUTTON_FRAME));
		exitButton.setBackgroundWithImage(new Image(Resource.BUTTON_FRAME));

		setPlayButtonListener(playButton);
		setSettingButtonListener(settingButton);
		setExitButtonListener(exitButton);

		menu.getChildren().addAll(title, playButton, settingButton, exitButton);
		this.getChildren().add(menu);
	}

	private void setTitleImage(GraphicsContext gc) {
		gc.drawImage(new Image(Resource.ICON), 0, 0, 300, 300);
	}

	public void setBackgroundWithImage() {
		BackgroundSize bgSize = new BackgroundSize(this.getPrefWidth(), this.getPrefHeight(), false, false, false, false);
		BackgroundImage bgImg = new BackgroundImage(new Image(Resource.BACKGROUND), null, null, null, bgSize);
		BackgroundImage[] bgImgA = { bgImg };
		this.setBackground(new Background(bgImgA));
	}

	private void setExitButtonListener(MyButton exitButton) {
		exitButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				SoundManager.playClickingSound();
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Exit");
				alert.setHeaderText(null);
				alert.setContentText("Do you want to exit?");
				alert.showAndWait().ifPresent(response -> {
					if (response == ButtonType.OK) {
						System.exit(0);
					}
				});
			}
		});

	}

	private void setPlayButtonListener(MyButton playButton) {
		playButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				// TODO Auto-generated method stub
				SoundManager.playClickingSound();
				AppManager.showSelectMode();
			}
		});
	}

	private void setSettingButtonListener(MyButton settingButton) {
		settingButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				// TODO Auto-generated method stub
				SoundManager.playClickingSound();
				AppManager.updateSettingMenu();
				AppManager.showSettingMenu();
				AppManager.setSliderStyle();
			}
		});
	}
}