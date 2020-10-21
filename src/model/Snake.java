package model;

import java.io.Serializable;

public class Snake extends Entity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public Snake(int top, int bottom) {
		this.top = top;
		this.bottom = bottom;
	}

	public void setHeadPosition(int pos) {
		top = pos;
	}

	public void setTailPosition(int pos) {
		bottom = pos;
	}
}
