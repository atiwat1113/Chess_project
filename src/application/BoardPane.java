package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import logic.*;

import java.awt.Point;
import java.util.ArrayList;

public class BoardPane extends GridPane {
	private ObservableList<BoardCell> boardCellList = FXCollections.observableArrayList();
	private static String[] w_p = { "blank", "W_King.png", "W_Queen.png", "W_Bishop.png", "W_Knight.png", "W_Rook.png",
			"W_Pawn.png" };
	private static String[] b_p = { "blank", "B_King.png", "B_Queen.png", "B_Bishop.png", "B_Knight.png", "B_Rook.png",
			"B_Pawn.png" };
	private Cell[][] cellMap;
	private static final int row = 8;
	private static final int column = 8;
	private static final Color redTile = new Color((double) 200 / 255, (double) 200 / 255, (double) 200 / 255, 1);
	private static final Color blackTile = new Color((double) 89 / 255, (double) 89 / 255, (double) 89 / 255, 1);
	private BoardCell bc;
	private Point currentSelectedPoint;
	//private ArrayList<Point> currentSelectedMoveList;
	private Text turnText;

	public BoardPane() {
		super();
		String[][] nb = { { b_p[5], b_p[4], b_p[3], b_p[2], b_p[1], b_p[3], b_p[4], b_p[5] },
				{ b_p[6], b_p[6], b_p[6], b_p[6], b_p[6], b_p[6], b_p[6], b_p[6] },
				{ b_p[0], b_p[0], b_p[0], b_p[0], b_p[0], b_p[0], b_p[0], b_p[0] },
				{ b_p[0], b_p[0], b_p[0], b_p[0], b_p[0], b_p[0], b_p[0], b_p[0] },
				{ w_p[0], w_p[0], w_p[0], w_p[0], w_p[0], w_p[0], w_p[0], w_p[0] },
				{ w_p[0], w_p[0], w_p[0], w_p[0], w_p[0], w_p[0], w_p[0], w_p[0] },
				{ w_p[6], w_p[6], w_p[6], w_p[6], w_p[6], w_p[6], w_p[6], w_p[6] },
				{ w_p[5], w_p[4], w_p[3], w_p[2], w_p[1], w_p[3], w_p[4], w_p[5] } };
		GameController.InitializeMap(nb);
		this.turnText = new Text(GameController.getTurn().toString() + " TURN");
		turnText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 25));
		this.cellMap = GameController.getBoard().getCellMap();
		for (int i = 0; i < row; i++) {// Point (y,x) => (i,j)
			for (int j = 0; j < column; j++) {
				if ((i + j) % 2 == 0) {
					bc = new BoardCell(cellMap[i][j], new Point(i, j), redTile);
				} else {
					bc = new BoardCell(cellMap[i][j], new Point(i, j), blackTile);
				}
				this.boardCellList.add(bc);
				this.add(boardCellList.get(column * i + j), j, i);

			}
		}

		for (BoardCell bc : boardCellList) {
			bc.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					// TODO fill in this method
					try {
						addOnClickHandler(bc);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setHeaderText(null);
						alert.setContentText(e1.getMessage());
						alert.showAndWait();
					}
				}
			});
		}
	}

	private void addOnClickHandler(BoardCell myBoardCell) throws Exception {
		// TODO Auto-generated method stub
		// System.out.println("clicked");
		updateBoard(myBoardCell);
		if (myBoardCell.isMoveable()) {
			// currentSelectedPoint = new Point(myBoardCell.getP().y,myBoardCell.getP().x);
			// System.out.println(currentSelectedPoint.toString());
			// System.out.println(currentSelectedMoveList.toString());
			GameController.move(currentSelectedPoint, myBoardCell.getP());//, currentSelectedMoveList);
			updateBoard(myBoardCell);
			myBoardCell.update();
			currentSelectedPoint = null;
			//currentSelectedMoveList = null;
			GameController.nextTurn();
		} else {
			if (myBoardCell.hasEntity() && GameController.isTurn(myBoardCell.getP(), GameController.getTurn())) {
				if (!myBoardCell.isClicked()) {
					for (BoardCell bc : this.getBoardCellList()) {
						if (GameController.moveList(myBoardCell.getP()).contains(bc.getP())) {
							if (bc.hasEntity())
								bc.setBackgroundTileColor(new Image(Sprites.WALKPATH),
										new Image(bc.getMyCell().getEntity().getSymbol()));
							else
								bc.setBackgroundTileColor(new Image(Sprites.WALKPATH));
							bc.setMoveable(true);
						}
					}
					currentSelectedPoint = myBoardCell.getP();
					//currentSelectedMoveList = myBoardCell.getMyCell().getEntity().moveList(GameController.getBoard());
					myBoardCell.setClicked(true);
				} else {
					for (BoardCell bc : this.boardCellList) {
						if (myBoardCell.getMyCell().getEntity().moveList(GameController.getBoard()).contains(bc.getP())) {
							if (bc.hasEntity())
								bc.setBackgroundTileColor(new Image(bc.getMyCell().getEntity().getSymbol()));
							else
								bc.setBackgroundTileColor();
							bc.setMoveable(false);
						}
					}
					currentSelectedPoint = null;
					//currentSelectedMoveList = null;
					myBoardCell.setClicked(false);
				}
			}
		}

		if (GameController.isWin()) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("End Game");
			alert.setHeaderText(null);
			alert.setContentText(GameController.getTurn().toString() + " WIN!!!\nDo you want to exit?");
			alert.showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
					System.exit(0);
				}
			});
		}
		this.turnText.setText(GameController.getTurn().toString() + " TURN");
	}

	private void updateBoard(BoardCell myBoardCell) {
		// TODO Auto-generated method stub
		this.cellMap = GameController.getBoard().getCellMap();
		for (BoardCell bc : this.getBoardCellList()) {
			if (bc.isClicked() || bc.isMoveable())
				bc.setMyCell(cellMap[bc.getP().x][bc.getP().y]);
			if (!bc.equals(myBoardCell))
				bc.update();
		}

	}

	public ObservableList<BoardCell> getBoardCellList() {
		return boardCellList;
	}

	public Text getTurnText() {
		return turnText;
	}

}
