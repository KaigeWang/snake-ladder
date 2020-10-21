package model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;

import exception.*;

public class Board extends JPanel implements Runnable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static JFrame frame = new JFrame("SEF Assignment");
	private double factor = 0.2;
	private int XMARGIN = 20;
	private int YMARGIN = 20;
	private Dice dice;
	private String bLines[] = new String[8];
	private int bCount = 0;
	private List<Piece> pieces;
	private List<Snake> snakes;
	private List<Ladder> ladders;
	private List<SnakeGuard> snakeGuards;

	public Board() {
		this(2);
	}

	public Board(int n) {
		if (n > 4 || n < 2) {
			System.out.println("Minimum 2 players and Maximum 4 players");
			System.exit(0);
		}

		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());

		frame.getContentPane().add(this, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 520);
		frame.setVisible(true);
		new Thread(this).start();
		// r.start();

		snakes = new ArrayList<Snake>();
		ladders = new ArrayList<Ladder>();
		pieces = new ArrayList<Piece>();
		snakeGuards = new ArrayList<SnakeGuard>();

		for (int i = 0; i < n; i++) {
			Piece p = new Piece(i);
			pieces.add(p);

		}

		dice = new Dice(this);
	}

	public Board(List<Piece> p, List<Snake> s, List<SnakeGuard> sg, List<Ladder> l)
			throws SnakePlacementException, LadderPlacementException, SnakeGuardPlacementException {
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());

		frame.getContentPane().add(this, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 520);
		frame.setVisible(true);
		new Thread(this).start();

		snakes = new ArrayList<Snake>();
		ladders = new ArrayList<Ladder>();
		pieces = new ArrayList<Piece>();
		snakeGuards = new ArrayList<SnakeGuard>();

		for (Snake i : s) {
			this.add(i);
		}
		for (Ladder i : l) {
			this.add(i);
		}
		for (SnakeGuard i : sg) {
			this.add(i);
		}

		pieces = p;

		dice = new Dice(this);
		repaint();

	}

	public void run() {
		double inc = 0.05;
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
			factor += inc;
			;
			if (factor > 0.5 || factor < -0.5)
				inc = -inc;
			repaint();
		}
	}

	public void addMessage(String line) {
		if (bCount < 8) {
			if (line.length() > 20) {
				String temp = line.substring(0, 20);
				bLines[bCount++] = temp;
			} else
				bLines[bCount++] = line;
		}

		repaint();
	}

	public void clearMessages() {
		bCount = 0;
		repaint();
	}

	private int getX(int pos) {
		pos--;
		if ((pos / 10) % 2 == 0)
			return XMARGIN + 10 + pos % 10 * 40;
		else
			return XMARGIN + 370 - pos % 10 * 40;
	}

	private int getY(int pos) {
		pos--;
		return YMARGIN - 30 + 400 - pos / 10 * 40;
	}

	public void drawPieces(Graphics g) {
		if (pieces.size() > 0) {
			g.setColor(Color.WHITE);
			g.fillOval((int) getX(pieces.get(0).getPosition()) - 10, getY(pieces.get(0).getPosition()) - 10, 20, 20);
			g.setColor(Color.BLACK);
			g.drawString("1", (int) getX(pieces.get(0).getPosition()) - 5, getY(pieces.get(0).getPosition()) + 5);
		}
		if (pieces.size() > 1) {
			g.setColor(Color.RED);
			g.fillOval((int) getX(pieces.get(1).getPosition()) + 10, getY(pieces.get(1).getPosition()) - 10, 20, 20);
			g.setColor(Color.BLACK);
			g.drawString("2", (int) getX(pieces.get(1).getPosition()) + 15, getY(pieces.get(1).getPosition()) + 5);
		}
		if (pieces.size() > 2) {
			g.setColor(Color.GRAY);
			g.fillOval((int) getX(pieces.get(2).getPosition()) - 10, getY(pieces.get(2).getPosition()) + 10, 20, 20);
			g.setColor(Color.BLACK);
			g.drawString("3", (int) getX(pieces.get(2).getPosition()) - 5, getY(pieces.get(2).getPosition()) + 25);
		}
		if (pieces.size() > 3) {
			g.setColor(Color.CYAN);
			g.fillOval((int) getX(pieces.get(3).getPosition()) + 10, getY(pieces.get(3).getPosition()) + 10, 20, 20);
			g.setColor(Color.BLACK);
			g.drawString("4", (int) getX(pieces.get(3).getPosition()) + 15, getY(pieces.get(3).getPosition()) + 25);
		}
	}

	public void drawLadder(Graphics g, int bottom, int top) {
		double x1 = getX(bottom);
		double y1 = getY(bottom);
		double x2 = getX(top);
		double y2 = getY(top);

		int steps = (int) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1)) / 25 + 1;

		int xinc = 5;
		if (x1 > x2)
			xinc = -xinc;
		int yinc = 5;
		if (y1 > y2)
			yinc = -yinc;

		g.drawLine((int) (x1 - xinc), (int) (y1 + yinc), (int) (x2 - xinc), (int) (y2 + yinc));
		g.drawLine((int) (x1 - xinc) - 1, (int) (y1 + yinc), (int) (x2 - xinc) - 1, (int) (y2 + yinc));
		g.drawLine((int) (x1 - xinc), (int) (y1 + yinc) - 1, (int) (x2 - xinc), (int) (y2 + yinc) - 1);

		g.drawLine((int) (x1 + xinc), (int) (y1 - yinc), (int) (x2 + xinc), (int) (y2 - yinc));
		g.drawLine((int) (x1 + xinc) - 1, (int) (y1 - yinc), (int) (x2 + xinc) - 1, (int) (y2 - yinc));
		g.drawLine((int) (x1 + xinc), (int) (y1 - yinc) - 1, (int) (x2 + xinc), (int) (y2 - yinc) - 1);

		double xstep = (x2 - x1) / (steps + 1);
		double ystep = (y2 - y1) / (steps + 1);
		for (int i = 0; i < steps; i++) {
			x1 += xstep;
			y1 += ystep;
			g.drawLine((int) (x1 + xinc), (int) (y1 - yinc), (int) (x1 - xinc), (int) (y1 + yinc));
			g.drawLine((int) (x1 + xinc) - 1, (int) (y1 - yinc), (int) (x1 - xinc) - 1, (int) (y1 + yinc));
			g.drawLine((int) (x1 + xinc), (int) (y1 - yinc) - 1, (int) (x1 - xinc), (int) (y1 + yinc) - 1);
		}

	}

	public void drawSnake(Graphics g, int head, int tail) {
		int x1 = getX(head);
		int y1 = getY(head);
		int x2 = getX(tail);
		int y2 = getY(tail);

		int steps = (int) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1)) / 150 * 18 + 24;

		double xstep = (double) (x2 - x1) / (steps + 1);
		double ystep = (double) (y2 - y1) / (steps + 1);

		double inc;
		double x = x1, y = y1;

		boolean odd = true;
		for (int i = 0; i < steps + 1; i++) {
			if ((i % 12) % 12 == 0)
				inc = 0;
			else if ((i % 12) % 11 == 0)
				inc = 10 * factor;
			else if ((i % 12) % 10 == 0)
				inc = 13 * factor;
			else if ((i % 12) % 9 == 0)
				inc = 15 * factor;
			else if ((i % 12) % 8 == 0)
				inc = 13 * factor;
			else if ((i % 12) % 7 == 0)
				inc = 10 * factor;
			else if ((i % 12) % 6 == 0)
				inc = 0 * factor;
			else if ((i % 12) % 5 == 0)
				inc = -10 * factor;
			else if ((i % 12) % 4 == 0)
				inc = -13 * factor;
			else if ((i % 12) % 3 == 0)
				inc = -15 * factor;
			else if ((i % 12) % 2 == 0)
				inc = -13 * factor;
			else
				inc = -10 * factor;
			x += xstep;
			y += ystep;
			if (odd) {
				g.setColor(Color.BLACK);
				odd = false;
			} else {
				g.setColor(Color.GREEN);
				odd = true;
			}
			if (x2 > x1)
				g.fillOval((int) (x + inc), (int) (y - inc), 20 - 10 * i / steps, 20 - 10 * i / steps);
			else
				g.fillOval((int) (x - inc), (int) (y - inc), 20 - 10 * i / steps, 20 - 10 * i / steps);
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if ((i + j) % 2 == 0)
					g.setColor(Color.YELLOW);
				else
					g.setColor(Color.ORANGE);

				g.fillRect(XMARGIN + 40 * i, YMARGIN + 40 * j, 40, 40);

			}

		}
		g.setColor(Color.BLACK);

		for (int k = 0; k < snakeGuards.size(); k++) {
			int num = snakeGuards.get(k).getPosition();

			g.setColor(Color.CYAN);

			g.fillRect(getX(num) - 10, getY(num) - 10, 40, 40);
		}
		g.setColor(Color.BLACK);

		for (int i = 0; i < 100; i++)
			g.drawString("" + (i + 1), getX(i + 1), getY(i + 1) + 20);
		for (int i = 0; i < snakes.size(); i++) {
			drawSnake(g, snakes.get(i).getTop(), snakes.get(i).getBottom());
		}
		for (int i = 0; i < ladders.size(); i++)
			drawLadder(g, ladders.get(i).getBottom(), ladders.get(i).getTop());

		g.setColor(Color.GRAY);
		g.fill3DRect(430, 40, 200, 340, true);

		g.setColor(Color.WHITE);
		g.fill3DRect(440, 50, 180, 320, true);
		g.setColor(Color.BLACK);
		Font font1 = new Font("TimesRoman", Font.BOLD, 16);
		g.setFont(font1);
		g.drawString("Display", 490, 20);

		for (int i = 0; i < bCount; i++)
			g.drawString(bLines[i], 450, 70 + 30 * i);

		drawPieces(g);
		dice.draw(g);
	}

	public void add(Snake s) throws SnakePlacementException {

		if (snakes.size() < 10) {

			snakes.add(s);

		} else {
			throw new SnakePlacementException();
		}

	}

	public void add(Ladder l) throws LadderPlacementException {

		if (ladders.size() < 10) {

			ladders.add(l);

		} else {
			throw new LadderPlacementException();
		}

	}

	public void add(SnakeGuard sg) throws SnakeGuardPlacementException {

		if (snakeGuards.size() < 4) {

			for (SnakeGuard i : snakeGuards) {
				if (sg.getPosition() == i.getPosition()) {
					throw new SnakeGuardPlacementException();
				}
			}
			snakeGuards.add(sg);
			repaint();
		} else {
			throw new SnakeGuardPlacementException();
		}

	}

	public Dice getDice() {
		return dice;
	}

	public List<Snake> getSnakes() {
		return snakes;

	}

	public List<Ladder> getLadder() {

		return ladders;
	}

	public List<Piece> getPiece() {

		return pieces;
	}

	public Piece getPiece(int piece) {
		if (piece < 0 || piece >= pieces.size())
			return null;
		return pieces.get(piece);
	}

	public List<SnakeGuard> getSnakeGuard() {
		return snakeGuards;
	}

	public int getSnakeCounts() {

		return snakes.size();
	}

	public int getSnakeGaurdCounts() {

		return snakeGuards.size();
	}

	public void setPiece(int piece, int pos) {

		pieces.get(piece).setPosition(pos);
		repaint();
	}

	public void setSnake(int head, int pos) {

		for (Snake i : snakes) {

			if (i.getTop() == head) {
				int length = i.getTop() - i.getBottom();

				i.setHeadPosition(pos);
				i.setTailPosition(pos - length);

			}
		}

		repaint();

	}

	public void clearLadder() {
		ladders.clear();
		repaint();

	}

	public void clearSnakeGaurd() {
		snakeGuards.clear();
		repaint();

	}

	public HashSet<Integer> getCriticalPosition() {

		HashSet<Integer> cp = new HashSet<Integer>();

		for (Snake i : snakes) {
			cp.add(i.getTop());
			cp.add(i.getBottom());
		}

		for (Ladder i : ladders) {
			cp.add(i.getBottom());
			cp.add(i.getTop());
		}

		for (SnakeGuard i : snakeGuards) {
			cp.add(i.getPosition());
		}

		return cp;

	}

}