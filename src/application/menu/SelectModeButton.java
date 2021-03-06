package application.menu;

import Resource.Resource;
import application.*;
import game.base.Games;
import application.board.*;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

public class SelectModeButton extends MyButton {

	private String gameType;

	public SelectModeButton(String text, double fontSize) {
		super(text, fontSize);
		this.setPrefWidth(300);
		setBackgroundWithImage(new Image(Resource.BUTTON_FRAME));
		switch (text) {
		case "Normal":
			this.gameType = Games.NORMAL;
			break;
		case "Atomic":
			this.gameType = Games.ATOMIC;
			break;
		case "King of the hill":
			this.gameType = Games.KINGOFTHEHILL;
			break;
		case "Three check":
			this.gameType = Games.THREECHECK;
			break;
		case "Chess960":
			this.gameType = Games.CHESS960;
			break;
		case "Horde":
			this.gameType = Games.HORDE;
			break;
		default:
		}

		setSelectListener();
	}

	private void setSelectListener() {
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				setGame();
			}
		});
	}

	private void setGame() {
		SoundManager.playClickingSound();
		AppManager.setGameType(gameType);
		AppManager.setBoardPane(new BoardPane(gameType));
		AppManager.showTimeSelect();
	}
}
