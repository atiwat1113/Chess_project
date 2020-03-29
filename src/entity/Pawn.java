package entity;

import java.awt.Point;
import java.util.ArrayList;
import entity.base.Entity;
import logic.*;
import game.base.Board;

public class Pawn extends Entity {
	private boolean twoMove;
	public Pawn(Point p, Side side) {
		super(p, side);
	}

	@Override
	public Point getSymbol() {
		// TODO Auto-generated method stub
		if (this.side == Side.BLACK) {
			return Sprites.B_PAWN;
		}
		return Sprites.W_PAWN;
	}

	@Override
	public ArrayList<Point> moveList(Board board) {
		return null;
	}
	
	@Override
	public ArrayList<Point> eatList(Board board) { 
		return null;
	}
}
