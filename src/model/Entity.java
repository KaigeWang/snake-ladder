package model;

import java.io.Serializable;

public abstract class Entity implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	protected int bottom;
	protected int top;

	public int getBottom() {
		return bottom;
	}

	public int getTop() {
		return top;
	}

}
