package application.console;

import application.board.*;
import application.console.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class GamePane extends HBox{
	
	public GamePane() {
		this.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
		this.setPadding(new Insets(10));
		this.setPrefHeight(500);
		this.setSpacing(10);	
		this.setAlignment(Pos.CENTER);
	}
	
	public static VBox getConsole(BoardPane boardPane, PromotionPane promotionPane, SettingPane setting) {
		Text turn = boardPane.getTurnText();
		VBox console = new VBox();
		HBox topBox = new HBox();
		topBox.getChildren().addAll(promotionPane, setting);
		topBox.setSpacing(10);
		
		console.getChildren().addAll(topBox,turn);
		console.setPrefHeight(500);
		console.setSpacing(10);
		return console;
	}
}
