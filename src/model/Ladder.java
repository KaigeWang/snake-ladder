package model;

import java.io.Serializable;

public class Ladder extends Entity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public Ladder(int top, int bottom) {
		this.bottom = bottom;
		this.top = top;
	}

}