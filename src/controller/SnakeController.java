package controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import exception.SnakePlacementException;
import exception.snakeMoveException;
import model.Board;
import model.Snake;
import model.SnakeGuard;

public class SnakeController {

	private Board board;

	private List<Snake> snakes;

	public SnakeController(Board board) {
		this.board = board;
		snakes = board.getSnakes();
	}


	public static int getRondomNumBetweenRange(int min, int max) {

		int x = (int) ((Math.random() * ((max - min) + 1)) + min);

		return x;
	}


	public void moveSnake() throws snakeMoveException {

		for (Snake s : snakes) {
			snakeRandomMove(s);
		}

	}

	public void snakeRandomMove(Snake s) throws snakeMoveException {

		try {

			int head = s.getTop();
			int tail = s.getBottom();
			int length = head - tail;
			int y = head % 10; // Column;
			int x = head / 10; // row;
			Direction direction = null;

			// set for validate snake head
			HashSet<Integer> bannedPositions = new HashSet<Integer>();
			bannedPositions = board.getCriticalPosition();
			bannedPositions.remove(head);

			// set for validate snake tail
			HashSet<Integer> headPositions = new HashSet<Integer>();
			for (Snake i : snakes) {
				headPositions.add(i.getTop());
			}
			headPositions.remove(head);

			HashSet<Integer> snakeGuardPositions = new HashSet<Integer>();
			for (SnakeGuard i : board.getSnakeGuard()) {
				snakeGuardPositions.add(i.getPosition());
			}

			while (true) {

				// random direction
				int directionNo =  getRondomNumBetweenRange(0, 3);

				direction = Direction.values()[directionNo];

				// split head position to 2 digits int;
				// xy is the pos =====> pos = 10*x + y;
				int pos = 0;

				if ((y != 0) && (y != 1)) {

					switch (direction) {

					case topLeft:
						pos = 10 * (x + 1) + (10 - y);

						break;
					case topRight:
						pos = 10 * (x + 1) + (10 - y) + 2;

						break;
					case bottomLeft:
						pos = x * 10 - y;

						break;
					case bottomRight:
						pos = x * 10 - y + 2;

						break;
					}

				} else if (y == 0) {

					switch (direction) {

					case topLeft:
						pos = head + 2;

						break;
					case topRight:
						pos = head + 2;

						break;
					case bottomLeft:
						pos = head - 18;

						break;
					case bottomRight:
						pos = head - 18;

						break;
					}

				} else {

					switch (direction) {

					case topLeft:
						pos = head + 18;

						break;
					case topRight:
						pos = head + 18;

						break;
					case bottomLeft:
						pos = head - 2;

						break;
					case bottomRight:
						pos = head - 2;

						break;
					}

				}

				int tailPos = pos - length;

				if (pos >= 100 || pos <= 1 || tailPos <= 1 || tailPos >= 100) {
					continue;
				} else if (bannedPositions.contains(pos)) {

					throw new snakeMoveException("Entity overlaps");

				} else if (headPositions.contains(tailPos)) {
					throw new snakeMoveException("Entity overlaps");

				} else if (snakeGuardPositions.contains(tailPos)) {
					throw new snakeMoveException("Entity overlaps");
				} else {
					board.setSnake(head, pos);
					break;
				}

			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
