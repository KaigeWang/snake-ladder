package model;

import java.io.Serializable;

public class SnakeGuard  implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int position;



	public SnakeGuard(int position) {
		this.position = position;

	}

	public int getPosition() {
		return position;
	}



}
