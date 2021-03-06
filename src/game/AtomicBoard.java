package game;

import java.awt.Point;
import java.util.ArrayList;
import javafx.application.Platform;

import entity.base.*;
import entity.*;
import game.base.Board;
import game.base.CheckMateAble;
import logic.Side;
import application.AppManager;

public class AtomicBoard extends Board implements CheckMateAble {
	// In this game, there are special rule that eating piece and all surrounding
	// white and black pieces other than pawns are exploded.
	public AtomicBoard(String[][] map) {
		super(map);
	}

	// win
	public boolean isWin(Side side) {
		// System.out.println(winByLosingKing(side));
		return winByLosingKing(side) || winByCheckmate(side);
	}

	public boolean winByLosingKing(Side side) {
		for (Entity entity : getAllPieces(side)) {
			if (entity instanceof King)
				return false;
		}
		return true;
	}

	public boolean winByCheckmate(Side side) {
		Entity king = getKing(side);
		if (!isEatenPoint(king.getPoint(), side)) {
			return false;
		}
		return drawCannotMove(side) || drawByLosing2King();
	}

	// draw
	public boolean isDraw(Side side) {
		if (winByLosingKing(side))
			return false;
		return drawCannotMove(side);
	}

	public boolean drawCannotMove(Side side) {
		ArrayList<Entity> allEntity = getAllPieces(side);
		for (Entity entity : allEntity) {
			if (entity == null)
				continue;
			ArrayList<Point> moveList = moveList(entity.getPoint());
			if (moveList.size() != 0)
				return false;
		}
		return true;
	}

	public boolean drawByLosing2King() {
		// System.out.println("Error");
		return winByLosingKing(Side.WHITE) && winByLosingKing(Side.BLACK);
	}

	// check
	public boolean isCheck(Side side) {
		if (winByLosingKing(side))
			return false;
		Point kingPoint = getKing(side).getPoint();
		Point anotherKingPoint = getKing(getAnotherSide(side)).getPoint();
		if (Math.abs(anotherKingPoint.x - kingPoint.x) <= 1 && Math.abs(anotherKingPoint.y - kingPoint.y) <= 1)
			return false;
		return isEatenPoint(kingPoint, side);
	}

	@Override
	public void startAnimation(Point oldPoint, Point newPoint) {
		removePoint = new ArrayList<Point>();
		Entity moveEntity = this.getEntity(oldPoint);
		if (moveEntity instanceof Castling)
			((Castling) moveEntity).setNeverMove();
		if (moveEntity instanceof Pawn) {
			if (twoWalkPawn != null && twoWalkPawn.equals(new Point(oldPoint.x, newPoint.y))) {
				remove(twoWalkPawn);
			} else if (Math.abs(oldPoint.x - newPoint.x) == 2) {
				twoWalkPawn = newPoint;
			} else
				twoWalkPawn = null;
		} else
			twoWalkPawn = null;
		if (moveEntity instanceof King && isCastlingPoint(moveEntity.getSide(), newPoint)) {
			castling(moveEntity.getSide(), oldPoint, newPoint);
		} else {
			remove(oldPoint);
			if (getEntity(newPoint) == null) {
				movePiece = moveEntity;
				movePoint = newPoint;
			} else {
				movePiece = null;
				movePoint = null;
				removePoint.add(newPoint);
				for (Point vector : KingWalk) {
					explosion(addPoint(newPoint, vector));
				}
			}
			Platform.runLater(new Runnable() {
				public void run() {
					AppManager.startAnimation(oldPoint, newPoint, moveEntity);
				}
			});
		}
		int s = (moveEntity.getSide() == Side.BLACK) ? 7 : 0;
		if (moveEntity instanceof Pawn && newPoint.x == s) {
			havePromotion(newPoint, moveEntity.getSide());
		}
	}

	@Override
	protected ArrayList<Point> editMovePoint(Point oldPoint, ArrayList<Point> movePoint) {
		Entity moveEntity = this.getEntity(oldPoint);
		Side side = moveEntity.getSide();
		ArrayList<Point> op = new ArrayList<Point>();
		op.add(oldPoint);
		ArrayList<Point> oppositeKingPoints = new ArrayList<Point>();
		ArrayList<Point> kingPoints = new ArrayList<Point>();
		Point opKingPoint = getKing(getAnotherSide(side)).getPoint();
		Point kingPoint = getKing(side).getPoint();
		for (Point vector : KingWalk) {
			kingPoints.add(addPoint(kingPoint, vector));
			oppositeKingPoints.add(addPoint(opKingPoint, vector));
		}
		if (moveEntity instanceof King) {
			for (int i = movePoint.size() - 1; i >= 0; i--) {
				if (getEntity(movePoint.get(i)) != null) {
					movePoint.remove(i);
					continue;
				}
				if (oppositeKingPoints.contains(movePoint.get(i)))
					continue;
				if (checkCannotMovePoint(op, new Point(-1, -1), movePoint.get(i), side))
					movePoint.remove(i);
			}
			for (Point p : castingPoint(side)) {// for castling
				movePoint.add(p);
			}
		} else {
			for (int i = movePoint.size() - 1; i >= 0; i--) {
				if (kingPoints.contains(movePoint.get(i))) {
					if (getEntity(movePoint.get(i)) != null) {
						movePoint.remove(i);
						continue;
					}
				}
				if (checkCannotMovePoint(op, movePoint.get(i), kingPoint, side)) {
					movePoint.remove(i);
				}
			}
			if (moveEntity instanceof Pawn) {
				if (twoWalkPawn != null) {
					if (oldPoint.x == twoWalkPawn.x && Math.abs(oldPoint.y - twoWalkPawn.y) == 1) {
						Point newPoint;
						op.add(twoWalkPawn);
						if (side == Side.BLACK)
							newPoint = new Point(twoWalkPawn.x + 1, twoWalkPawn.y);
						else
							newPoint = new Point(twoWalkPawn.x - 1, twoWalkPawn.y);
						if (!checkCannotMovePoint(op, newPoint, kingPoint, side)) {
							movePoint.add(newPoint);
						}
					}
				}
			}
		}
		return movePoint;
	}

	@Override
	protected boolean checkOther(Point newPoint, Point kingPoint, Side side) {
		Point[] blackPawnWalk = { new Point(1, 1), new Point(1, -1) };
		Point[] whitePawnWalk = { new Point(-1, 1), new Point(-1, -1) };
		Point[] pawnWalk = whitePawnWalk;
		if (side == Side.BLACK)
			pawnWalk = blackPawnWalk;
		for (Point point : pawnWalk) {
			Point checkPoint = addPoint(kingPoint, point);
			if (!isInBoard(checkPoint))
				continue;
			Entity interestingEntity = getEntity(checkPoint);
			if (interestingEntity == null)
				continue;
			if (checkPoint.equals(newPoint))
				continue;
			if (interestingEntity.getSide() == getAnotherSide(side) && interestingEntity instanceof Pawn)
				return true;
		}
		for (Point point : knightWalk) {
			Point checkPoint = addPoint(kingPoint, point);
			if (!isInBoard(checkPoint))
				continue;
			Entity interestingEntity = getEntity(checkPoint);
			if (interestingEntity == null)
				continue;
			if (checkPoint.equals(newPoint))
				continue;
			if (interestingEntity.getSide() == getAnotherSide(side) && interestingEntity instanceof Knight)
				return true;
		}
		return false;
	}

	// other
	private void explosion(Point point) {
		if (!isInBoard(point))
			return;
		if (!(getEntity(point) instanceof Pawn)) {
			if (!(getEntity(point) == null)) {
				removePoint.add(point);
			}
		}
	}

}
