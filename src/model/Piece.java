package model;

import java.io.Serializable;

import exception.BoundaryException;

public class Piece implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int position;
	private int paralyseDuration;
	private int ladderClimb;
	private int index;

	public int getIndex() {
		return index;
	}

	public Piece(int index) {
		position = 1;
		paralyseDuration = 0;
		ladderClimb = 0;
		this.index = index;
	}

	public int getLadderClimb() {
		return ladderClimb;
	}

	public void addLadderClimb() {
		this.ladderClimb ++;
	}

	public void setPosition(int n) {
		position = n;
	}

	public int getPosition() {
		return position;
	}

	public void move(int n) throws BoundaryException {

		if ((position + n) > 100) {
			throw new BoundaryException();
		} else {
			position += n;
		}

	}

	public void paralyse() {
		paralyseDuration = 2;
	}




	public void decrementParalyseDuration(){
		if (paralyseDuration == 0) {
			return;
		}
		paralyseDuration--;
	}

	public boolean isParalyse() {
		if (paralyseDuration != 0) {
			return true;
		} else {
			return false;
		}


	}



}

