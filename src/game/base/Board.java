package game.base;

import logic.*;
import entity.base.*;
import game.Chess960Board;
import entity.*;
import java.awt.*;
import java.util.ArrayList;

import Resource.Sprites;
import application.AppManager;

//import java.lang.Math;
//remain: win, draw
public abstract class Board {
	protected Point twoWalkPawn;
	private Cell[][] cellMap;
	private Entity whiteKing, blackKing;
	protected ArrayList<Point> removePoint;
	protected Entity movePiece, castlingRook = null;
	protected Point movePoint, newRookPoint = null;
	protected static final Point[] knightWalk = { new Point(1, 2), new Point(2, 1), new Point(2, -1), new Point(1, -2),
			new Point(-1, -2), new Point(-2, -1), new Point(-2, 1), new Point(-1, 2) };
	protected static final Point[] KingWalk = { new Point(1, 1), new Point(1, 0), new Point(1, -1), new Point(0, -1),
			new Point(-1, -1), new Point(-1, 0), new Point(-1, 1), new Point(0, 1) };
	private int width, height;

	// private Game game;//enum game
	public Board(String[][] map) {
		twoWalkPawn = null;
		int column = map[0].length;
		int row = map.length;
		whiteKing = null;
		blackKing = null;
		width = column;
		height = row;
		cellMap = new Cell[row][column];
		for (int i = 0; i < row; i++) {// Point (y,x) => (i,j)
			for (int j = 0; j < column; j++) {
				Point p = new Point(i, j);
				cellMap[i][j] = new Cell();
				switch (map[i][j]) {// W B --- K Q R B N P --- 0
				case "WKing":
					whiteKing = new King(p, Side.WHITE);
					addEntity(whiteKing, p);
					break;
				case "WQueen":
					addEntity(new Queen(p, Side.WHITE), p);
					break;
				case "WRook":
					addEntity(new Rook(p, Side.WHITE), p);
					break;
				case "WBishop":
					addEntity(new Bishop(p, Side.WHITE), p);
					break;
				case "WKnight":
					addEntity(new Knight(p, Side.WHITE), p);
					break;
				case "WPawn":
					addEntity(new Pawn(p, Side.WHITE), p);
					break;
				case "BKing":
					blackKing = new King(p, Side.BLACK);
					addEntity(blackKing, p);
					break;
				case "BQueen":
					addEntity(new Queen(p, Side.BLACK), p);
					break;
				case "BRook":
					addEntity(new Rook(p, Side.BLACK), p);
					break;
				case "BBishop":
					addEntity(new Bishop(p, Side.BLACK), p);
					break;
				case "BKnight":
					addEntity(new Knight(p, Side.BLACK), p);
					break;
				case "BPawn":
					addEntity(new Pawn(p, Side.BLACK), p);
					break;
				case Sprites.BLANK:
					break;
				default:
					System.out.println("Error create board");
				}
			}
		}
	}

	// iswin
	public abstract boolean isWin(Side side);// opposite side win

	public abstract boolean isDraw(Side side);

	public abstract boolean isCheck(Side side);

	// move
	public abstract void move(Point oldPoint, Point newPoint);
	public abstract void startAnimation(Point oldPoint, Point newPoint);
	public void continueMove() {
		if (castlingRook!=null) {
			castlingRook.setPoint(newRookPoint);
			addEntity(castlingRook, newRookPoint);
			castlingRook = null;
		}
		movePiece.setPoint(movePoint);
		addEntity(movePiece, movePoint);
		for(Point point : removePoint) {
			remove(point);
		}
	}
	
	// complete moveList to display
	public ArrayList<Point> moveList(Point point) {
		Entity moveEntity = getEntity(point);
		ArrayList<Point> movePoint = moveEntity.moveList(this);
		// GameController.printPointList(movePoint);
		return editMovePoint(point, movePoint);
	}

	// moveList
	protected abstract ArrayList<Point> editMovePoint(Point oldPoint, ArrayList<Point> movePoint);

	public boolean checkCannotMovePoint(ArrayList<Point> oldPoint, Point newPoint, Point kingPoint, Side side) {
		// System.out.println(print(oldPoint)+"->"+print(newPoint)+"K"+print(kingPoint));
		Point[] rookVector = { new Point(1, 0), new Point(-1, 0), new Point(0, 1), new Point(0, -1) };
		Point[] bishopVector = { new Point(1, 1), new Point(-1, 1), new Point(1, -1), new Point(-1, -1) };
		if (checkOther(newPoint, kingPoint, side))
			return true;
		for (Point vector : rookVector) {
			if (checkRook(kingPoint, vector, oldPoint, newPoint, side))
				return true;
		}
		for (Point vector : bishopVector) {// check bishop and queen
			if (checkBishop(kingPoint, vector, oldPoint, newPoint, side))
				return true;
		}
		return false;
	}

	public boolean checkOther(Point newPoint, Point kingPoint, Side side) {
		Point[] blackPawnWalk = { new Point(1, 1), new Point(1, -1) };
		Point[] whitePawnWalk = { new Point(-1, 1), new Point(-1, -1) };
		Point[] pawnWalk = (side == Side.BLACK) ? blackPawnWalk : whitePawnWalk;
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
		for (Point point : KingWalk) {
			Point checkPoint = addPoint(kingPoint, point);
			if (!isInBoard(checkPoint))
				continue;
			Entity interestingEntity = getEntity(checkPoint);
			if (interestingEntity == null)
				continue;
			if (checkPoint.equals(newPoint))
				continue;
			if (interestingEntity.getSide() == getAnotherSide(side) && interestingEntity instanceof King)
				return true;
		}
		return false;
	}

	public boolean checkRook(Point point, Point vector, ArrayList<Point> oldPoint, Point newPoint, Side side) {
		Point nextPoint = addPoint(point, vector);
		if (!isInBoard(nextPoint)) {
			return false;
		}
		if (nextPoint.equals(newPoint)) {
			return false;
		}
		if (oldPoint.contains(nextPoint) || getEntity(nextPoint) == null) {
			return checkRook(nextPoint, vector, oldPoint, newPoint, side);
		}
		if (getEntity(nextPoint).getSide() != side) {
			if (getEntity(nextPoint) instanceof Rook || getEntity(nextPoint) instanceof Queen) {
				return true;
			}
		}
		return false;
	}

	public boolean checkBishop(Point point, Point vector, ArrayList<Point> oldPoint, Point newPoint, Side side) {
		Point nextPoint = addPoint(point, vector);
		if (!isInBoard(nextPoint)) {
			return false;
		}
		if (nextPoint.equals(newPoint)) {
			return false;
		}
		if (oldPoint.contains(nextPoint) || getEntity(nextPoint) == null) {
			return checkBishop(nextPoint, vector, oldPoint, newPoint, side);
		}
		if (getEntity(nextPoint).getSide() != side) {
			if (getEntity(nextPoint) instanceof Bishop || getEntity(nextPoint) instanceof Queen) {
				return true;
			}
		}
		return false;
	}

	// other
	public void havePromotion(Point point, Side side) {
		GameController.setPromotion(point, side);
	}

	public void promotion(Point point, Side side, String piece) {
		switch (piece.charAt(0)) {
		case 'Q':
			addEntity(new Queen(point, side, true), point);
			return;
		case 'R':
			addEntity(new Rook(point, side, true), point);
			return;
		case 'B':
			addEntity(new Bishop(point, side, true), point);
			return;
		case 'K':
			addEntity(new Knight(point, side, true), point);
			return;
		default:
		}
	}

	public boolean isEatenPoint(Point point, Side side) {
		ArrayList<Entity> allEntity = getAllPieces(getAnotherSide(side));
		for (Entity e : allEntity) {
			ArrayList<Point> moveablePoint = e.moveList(this);
			for (Point p : moveablePoint) {
				if (p.x == point.x && p.y == point.y) {
					return true;
				}
			}
		}
		return false;
	}

	public void remove(Point p) {
		addEntity(null, p);
	}

	public boolean addEntity(Entity e, Point p) {
		return cellMap[p.x][p.y].setEntity(e);
	}

	public Entity getEntity(Point p) {
		if (!isInBoard(p) || cellMap[p.x][p.y].IsEmpty()) {
			return null;
		}
		return cellMap[p.x][p.y].getEntity();
	}

	public boolean isInBoard(Point p) {
		if (p.x < 0 || p.x >= height)
			return false;
		if (p.y < 0 || p.y >= width)
			return false;
		return true;
	}

	public ArrayList<Entity> getAllPieces(Side side) {
		ArrayList<Entity> returnList = new ArrayList<Entity>();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				Entity e = getEntity(new Point(i, j));
				if (e == null)
					continue;
				// System.out.println(e);
				if (e.getSide() == side) {
					returnList.add(e);
				}
			}
		}
		return returnList;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Entity getKing(Side side) {
		if (side == Side.WHITE)
			return whiteKing;
		return blackKing;
	}

	public Side getAnotherSide(Side side) {
		if (side == Side.BLACK)
			return Side.WHITE;
		return Side.BLACK;
	}

	public static Point[] getKnightWalk() {
		return knightWalk;
	}

	public static Point[] getKingWalk() {
		return KingWalk;
	}

	public static Point addPoint(Point p1, Point p2) {
		return new Point(p1.x + p2.x, p1.y + p2.y);
	}

	public Cell[][] getCellMap() {
		return cellMap;
	}

	// castling
	public ArrayList<Point> castingPoint(Side side) {
		ArrayList<Point> returnPoint = new ArrayList<Point>();
		Entity king = getKing(side);
		int s = (side == Side.BLACK) ? 0 : 7;
		if (!((King) king).isNeverMove()) {
			return returnPoint;
		}
		Point rightRook = null;
		Point leftRook = null;
		Point temp = king.getPoint();
		while (true) {
			temp = addPoint(temp, new Point(0, 1));
			if (!isInBoard(temp))
				break;
			if (getEntity(temp) instanceof Rook) {
				rightRook = temp;
				break;
			}
		}
		temp = king.getPoint();
		while (true) {
			temp = addPoint(temp, new Point(0, -1));
			if (!isInBoard(temp))
				break;
			if (getEntity(temp) instanceof Rook) {
				leftRook = temp;
				break;
			}
		}
		if (rightRook != null) {
			Point newKing = new Point(s, 6);
			Point newRook = new Point(s, 5);
			if (isNull(king.getPoint(), rightRook, newKing, newRook) && isFree(king.getPoint(), newKing, side)) {
				returnPoint.add(newKing);
				returnPoint.add(rightRook);
			}
		}
		if (leftRook != null) {
			Point newKing = new Point(s, 2);
			Point newRook = new Point(s, 3);
			if (isNull(king.getPoint(), leftRook, newKing, newRook) && isFree(king.getPoint(), newKing, side)) {
				returnPoint.add(newKing);
				returnPoint.add(leftRook);
			}
		}
		return returnPoint;
	}
	public boolean isCastlingPoint(Side side, Point point) {
		//Entity entity = getEntity(point);
		return Math.abs(point.y-4)>1;
	}

	public int min(int i1, int i2, int i3, int i4) {
		return Math.min(i1, Math.min(i2, Math.min(i3, i4)));
	}

	public int max(int i1, int i2, int i3, int i4) {
		return Math.max(i1, Math.max(i2, Math.max(i3, i4)));
	}

	public boolean isFree(Point start, Point stop, Side side) {// king -> king
		for (int i = Math.min(start.y, stop.y); i <= Math.max(start.y, stop.y); i++) {
			Point point = new Point(start.x, i);
			if (isEatenPoint(point, side)) {
				return false;
			}
		}
		return true;
	}

	public boolean isNull(Point oldKing, Point oldRook, Point newKing, Point newRook) {
		if (oldKing.x != oldRook.x && oldRook.x != newKing.x && newKing.x != newRook.x) {
			System.out.println("error");
		}
		for (int i = min(oldKing.y, oldRook.y, newKing.y, newRook.y); i <= max(oldKing.y, oldRook.y, newKing.y,
				newRook.y); i++) {
			Point point = new Point(oldKing.x, i);
			if (point.equals(oldRook) || point.equals(oldKing))
				continue;
			if (getEntity(point) != null)
				return false;
		}
		return true;
	}

	public void castling(Side side, Point oldPoint, Point newPoint) {
		Entity moveEntity = getEntity(oldPoint);
		int s = (side == Side.BLACK) ? 0 : 7;
		int ss =  (oldPoint.y > newPoint.y) ? 0 : 7;
		Point rookPoint = (this instanceof Chess960Board) ? newPoint : new Point(s, ss);
		System.out.println(GameController.print(rookPoint));
		castlingRook = getEntity(rookPoint);
		remove(oldPoint);
		remove(rookPoint);
		Point moveKing;
		if (oldPoint.y > newPoint.y) {
			moveKing = new Point(s, 2);
			newRookPoint = new Point(s, 3);
		} else {
			moveKing = new Point(s, 6);
			newRookPoint = new Point(s, 5);
		}
		AppManager.startCastlingAnimation(oldPoint, moveKing, moveEntity, rookPoint, newRookPoint, castlingRook);
		//AppManager.startCastlingAnimation(Point startKing, Point endKing, Entity king, Point startRook, Point endRook, Entity rook);
		
	}
}
