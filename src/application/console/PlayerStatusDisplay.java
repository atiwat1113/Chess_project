package application.console;

import Resource.Resource;
import application.AppManager;
import application.SoundManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import logic.GameController;
import logic.Side;

public class PlayerStatusDisplay extends VBox {

	private Label turn;
	private Side side;
	private int spareTime;
	private int timePerTurn;
	private boolean hasTimeLimit;
	private Thread timerThread;
	private Text spareTimeText;
	private Text timePerTurnText;
	private HBox timePane;

	public PlayerStatusDisplay(Side side) {
		turn = new Label(side.toString());
		this.side = side;
		spareTime = AppManager.getSpareTime();
		hasTimeLimit = (spareTime == 0 ? false : true);
		timePerTurn = 30;
		spareTimeText = new Text();
		timePerTurnText = new Text();
		if (hasTimeLimit) {
			setSpareTimeText();
			setTimePerTurnText();
		} else {
			spareTimeText.setText("-----");
			timePerTurnText.setText("-----");
		}

		turn.setFont(Font.loadFont(Resource.ROMAN_FONT, 25));
		turn.setTextFill(Color.BLACK);
		spareTimeText.setFont(Font.loadFont(Resource.ROMAN_FONT, 25));
		timePerTurnText.setFont(Font.loadFont(Resource.ROMAN_FONT, 25));

		timePane = new HBox();
		timePane.getChildren().addAll(spareTimeText, timePerTurnText);
		timePane.setSpacing(30);
		timePane.setAlignment(Pos.CENTER);
		timePane.setPrefWidth(100);

		this.setPrefSize(100, 75);
		this.setAlignment(Pos.CENTER);
		this.setPadding(new Insets(10));
		this.setSpacing(5);
		this.setBorder(
				new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(15), BorderWidths.DEFAULT)));
		this.getChildren().addAll(turn, timePane);

	}

	public void rotateDisplay() {
		this.getChildren().add(this.getChildren().remove(0));

	}

	public void startTurn() {
		setTurnText(true);
		if (hasTimeLimit) {
			timerThread = new Thread(() -> {
				while (true) {
					try {
						Thread.sleep(1000);
						if (timePerTurn != 0) {
							if (spareTime <= 30 || timePerTurn <= 10) {
								SoundManager.playClockTick();
							}
							timePerTurn -= 1;
						} else {
							if (spareTime > 30) {
								SoundManager.stopClockTick();
							}
							spareTime -= 1;
						}
						if (spareTime < 0) {
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									AppManager.getBoardPane().showEndGameWindow(GameController.getTurn() + " time out!\n"
											+ GameController.getAnotherSide(GameController.getTurn()) + " Win!!!");
								}
							});
							stop();
						} else {
							update();
						}
					} catch (Exception e) {
						SoundManager.stopClockTick();
						break;
					}
				}
			});
			timerThread.start();
		}
	}

	public void endTurn() {
		if (hasTimeLimit) {
			try {
				stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
			timePerTurn = 30;
			update();
		}
		setTurnText(false);
	}

	public void stop() throws Exception {
		this.timerThread.interrupt();
	}

	public void update() {
		setSpareTimeText();
		setTimePerTurnText();
	}

	private void setSpareTimeText() {
		if (spareTime < 60) {
			if (spareTime >= 10) {
				spareTimeText.setText("00:" + spareTime);
			} else {
				spareTimeText.setText("00:0" + spareTime);
			}
		} else {
			if (spareTime / 60 >= 10) {
				if (spareTime % 60 >= 10) {
					spareTimeText.setText(spareTime / 60 + ":" + spareTime % 60);
				} else {
					spareTimeText.setText(spareTime / 60 + ":0" + spareTime % 60);
				}
			} else {
				if (spareTime % 60 >= 10) {
					spareTimeText.setText("0" + spareTime / 60 + ":" + spareTime % 60);
				} else {
					spareTimeText.setText("0" + spareTime / 60 + ":0" + spareTime % 60);
				}
			}
		}
	}

	private void setTimePerTurnText() {
		if (timePerTurn >= 10) {
			timePerTurnText.setText("00:" + timePerTurn);
		} else {
			timePerTurnText.setText("00:0" + timePerTurn);
		}
	}

	public void setTurnText(boolean isTurn) {
		if (isTurn) {
			turn.setText(this.side + " TURN");
		} else {
			turn.setText(this.side.toString());
		}
	}

}
