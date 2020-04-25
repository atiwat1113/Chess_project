package application;

import application.menu.*;
import application.board.*;
import application.console.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppManager {

	private static Stage stage;
	private static Scene scene;
	private static MenuPane menuPane;
	private static SelectModePane selectModePane;
	private static GamePane gamePane;
	private static BoardPane boardPane;
	private static PromotionPane promotionPane;
	private static SettingPane setting;
	private static String gameType;
	
	
	public static void setStage(Stage stage) {
		AppManager.stage = stage;
	}
	
	public static void setScene(Scene scene) {
		AppManager.scene = scene;
	}
	
	public static void setMenuPane(MenuPane menuPane) {
		AppManager.menuPane = menuPane;
	}

	public static void showMenu() {
		scene.setRoot(menuPane);
		stage.sizeToScene();
	}
	
	public static void hidePromotion() {
		AppManager.promotionPane.hidePromotionPane();
	}
	
	public static void showPromotion() {
		AppManager.promotionPane.showPromotionPane();
	}

	public static void setGamePaneNode() {
		gamePane.getChildren().addAll(boardPane,GamePane.getConsole(boardPane, promotionPane, setting));
	}
	
	public static void showSelectMode() {
		scene.setRoot(selectModePane);
		stage.sizeToScene();
	}
	
	public static void showGamePane() {
		scene.setRoot(gamePane);
		stage.sizeToScene();
	}

	public static void setGamePane(GamePane gamePane) {
		AppManager.gamePane = gamePane;
	}

	public static void setPromotionListener(String text) {
		boardPane.promotion(text);
	}
	
	public static BoardPane getBoardPane() {
		return boardPane;
	}

	public static void setBoardPane(BoardPane boardPane) {
		AppManager.boardPane = boardPane;
	}

	public static PromotionPane getPromotionPane() {
		return promotionPane;
	}

	public static void setPromotionPane(PromotionPane promotionPane) {
		AppManager.promotionPane = promotionPane;
	}

	public static SettingPane getSetting() {
		return setting;
	}

	public static void setSetting(SettingPane setting) {
		AppManager.setting = setting;
	}
	
	public static SelectModePane getSelectModePane() {
		return selectModePane;
	}

	public static void setSelectModePane(SelectModePane selectModePane) {
		AppManager.selectModePane = selectModePane;
	}

	public static void displayMessage(String message) {
		promotionPane.setMessage(message);
	}

	public static String getGameType() {
		return gameType;
	}

	public static void setGameType(String gameType) {
		AppManager.gameType = gameType;
	}
	
	public static void setRotate(boolean rotate) {
		boardPane.setRotate(rotate);
	}
	
	public static void rotateBoard() {
		boardPane.rotateBoard();
	}
	
}
