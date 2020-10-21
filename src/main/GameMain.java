
package main;


import controller.*;
import exception.*;
import model.*;
import javax.swing.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class GameMain implements UserInput  {
    private HumanController humanController;
    private Board board;
    private SnakeController snakeController;
    private Admin admin;
    static private int stage;
    static boolean godMode = false;
    private UIWrapper uiWrapper;
    private static int rounds;




    public GameMain() {

    	int numPieces = getInt("How many player want to join this game", 2, 4);

        board = new Board(numPieces);
        this.humanController = new HumanController(this.board);
        this.snakeController = new SnakeController(this.board);
        this.admin = new Admin(this.board, this);
        stage = 1;
        uiWrapper = new UIWrapper(board);
        rounds = 0;

    }

    public GameMain(List <Piece> p, List<Snake>s,List<SnakeGuard>sg, List <Ladder>l) throws SnakePlacementException, LadderPlacementException, SnakeGuardPlacementException{
    	board = new Board(p, s, sg, l);
    	this.humanController = new HumanController(this.board);
        this.snakeController = new SnakeController(this.board);
        this.admin = new Admin(this.board, this);
        uiWrapper = new UIWrapper(board);

    }


    public void setUp() {
        // placeholder
        for (int i = 0; i < 5; i++) {
            //plainMessage("Admin please put snake" + (i + 1));
            while (true) {
                try {

                    int head = ThreadLocalRandom.current().nextInt(1, 100);
                    int length = ThreadLocalRandom.current().nextInt(1, 31);
                    int tail = head - length;
                    admin.doAddSnake(head, tail);
                    break;
                } catch (Exception e) {
                    //   plainMessage(e.getMessage());
                }
            }
        }


        for (int i = 0; i < 5; i++) {
            while (true) {
                try {
                    int top = ThreadLocalRandom.current().nextInt(1, 100);
                    int length = ThreadLocalRandom.current().nextInt(1, 31);
                    int bottom = top - length;
                    admin.doAddLadder(top, bottom);
                    break;
                } catch (Exception e) {
                    //  plainMessage(e.getMessage());
                }
            }
        }
        stage = 2;
    }

    public void secondStage() throws SnakeGuardPlacementException, BoundaryException, snakeMoveException, FileNotFoundException, IOException  {
    	Boolean ifwin = false;
    	List <Piece> pieces = board.getPiece();

    	//loop 50 rounds;

    	for (int round = rounds; round<50; round++){
    		//piece turn;
    		int quit = uiWrapper.showOptionMessage("Round " + (round + 1));

    		if (quit == 1) {
    			rounds = round;
    			uiWrapper.showInfoMessage("Game saved!");
				save();
				System.exit(100);
			}

    		board.clearMessages();
    		board.addMessage("Round " + (round + 1));

    		for (int i = 0; i < pieces.size(); i++) {
    			board.addMessage("Piece " + (i+1) + " climbed: " + pieces.get(i).getLadderClimb());

    		}

    		for (int i = 0; i < pieces.size(); i++) {
    			if (pieces.get(i).isParalyse()) {
    				board.addMessage("Player " + (i + 1) + " is paralysed!");
    			}
			}


    		for (int i = 0; i < pieces.size(); i++) {
    			uiWrapper.showInfoMessage("Player " + (i + 1) + "'s turn.");

    			if (pieces.get(i).isParalyse()) {
    				uiWrapper.showInfoMessage("Player " + (i + 1) + " is paralysed!");
    				pieces.get(i).decrementParalyseDuration();
					continue;
				}

    			if (humanController.ifPlaceGuard()) {
					continue;
				}



    			humanController.stage2validatePieceLcations();
    			if (godMode == true) {
    				humanController.godMove(i);
				} else {
					humanController.movePieceByDice(i);
				}

        		humanController.stage2validatePieceLcations();
        		//board.addMessage("Player " + (i+1) + " ladder: " + pieces.get(i).getLadderClimb());
        		if (pieces.get(i).getPosition() == 100) {
        			if (pieces.get(i).getLadderClimb() >= 3) {
        				ifwin = true;
    					break;
					} else {
						uiWrapper.showInfoMessage("You have to start from 1!");
						pieces.get(i).setPosition(1);
					}

				}
			}

    		if (ifwin == true) {
				break;
			}
    		//snake turn;
    		uiWrapper.showInfoMessage("Snakes move");
    		snakeController.moveSnake();
    		humanController.stage2validatePieceLcations();

    	}

    	if (ifwin == true) {
			uiWrapper.showInfoMessage("Stage 2 wins!");
			board.clearMessages();
			rounds = 0;
			stage = 3;
		} else {
			uiWrapper.showInfoMessage("Sorry you lost!");
			end();
		}



    }


    private void save() throws FileNotFoundException, IOException {

    	ObjectOutputStream snakegardsave = new ObjectOutputStream(new FileOutputStream("snakegard.ser"));
    	snakegardsave.writeObject(board.getSnakeGuard());

    	ObjectOutputStream snakesave = new ObjectOutputStream(new FileOutputStream("snake.ser"));
    	snakesave.writeObject(board.getSnakes());

    	ObjectOutputStream piecesave = new ObjectOutputStream(new FileOutputStream("piece.ser"));
    	piecesave.writeObject(board.getPiece());

    	ObjectOutputStream laddersave = new ObjectOutputStream(new FileOutputStream("ladder.ser"));
    	laddersave.writeObject(board.getLadder());

    	ObjectOutputStream roundsave = new ObjectOutputStream(new FileOutputStream("rounds.ser"));
    	roundsave.writeObject(rounds);

    	ObjectOutputStream stagesave = new ObjectOutputStream(new FileOutputStream("stage.ser"));
    	stagesave.writeObject(stage);


    	roundsave.close();
    	stagesave.close();
    	piecesave.close();
    	laddersave.close();
    	snakegardsave.close();
    	snakesave.close();
	}


	public void thirdStage() throws InvalidInputException, snakeMoveException, Exception, IOException{
    	board.clearLadder();
    	board.clearSnakeGaurd();

    	List<Piece> pieces = board.getPiece();

    	for ( int round = rounds; round < 20; round++) {

    		int quit = uiWrapper.showOptionMessage("Round " + (round + 1));

    		if (quit == 1) {
    			rounds = round;
    			uiWrapper.showInfoMessage("Game saved!");
				save();
				System.exit(100);
			}

    		board.clearMessages();
    		board.addMessage("Round " + (round + 1));


    		for (int j = 0; j < pieces.size(); j++) {
    			uiWrapper.showInfoMessage("Player " + (j + 1) + "'s turn.");
    			if (godMode == true) {
    				if (uiWrapper.showComfirmDialog("Do you want test knight move?") == 0) {
    					humanController.knightMove(j);
					} else {
						humanController.godMove(j);
					}

				} else {
					humanController.knightMove(j);
				}



				if (humanController.stage3validatePieceLcations() == false) {
					uiWrapper.showInfoMessage("Sorry! you lost!");
					end();
				};

				if (board.getSnakeCounts() == 0) {
					uiWrapper.showInfoMessage("Conguatraltion! you win!");
					end();
				}

			}


    		snakeController.moveSnake();
    		humanController.stage3validatePieceLcations();


		}

    }

	public void end() throws FileNotFoundException, IOException{
		ObjectOutputStream snakegardsave = new ObjectOutputStream(new FileOutputStream("snakegard.ser"));
    	ObjectOutputStream snakesave = new ObjectOutputStream(new FileOutputStream("snake.ser"));
    	ObjectOutputStream piecesave = new ObjectOutputStream(new FileOutputStream("piece.ser"));
    	ObjectOutputStream laddersave = new ObjectOutputStream(new FileOutputStream("ladder.ser"));
    	ObjectOutputStream roundsave = new ObjectOutputStream(new FileOutputStream("rounds.ser"));
    	ObjectOutputStream stagesave = new ObjectOutputStream(new FileOutputStream("stage.ser"));
    	roundsave.close();
    	stagesave.close();
    	piecesave.close();
    	laddersave.close();
    	snakegardsave.close();
    	snakesave.close();
    	System.exit(0);
	}


    // A method to print a message and to read an int value in the range specified
    @Override
    public int getInt(String message, int from, int to) {
        String s;
        int n = 0;
        boolean invalid;
        do {
            invalid = false;
            s = (String) JOptionPane.showInputDialog(
                    this.board, message, "Customized Dialog",
                    JOptionPane.PLAIN_MESSAGE);
            try {
                n = Integer.parseInt(s);
                if (n < from || n > to)
                    plainMessage("Re-enter: Input not in range " + from + " to " + to);
            } catch (NumberFormatException nfe) {
                plainMessage("Re-enter: Invalid number");
                invalid = true;
            }
        } while (invalid || n < from || n > to);
        return n;
    }

    // A method to print a message and to read a String
    public String getString(String message) {
        String s = (String) JOptionPane.showInputDialog(
                this.board, message, "Customized Dialog",
                JOptionPane.PLAIN_MESSAGE);
        return s;
    }

    // A method to print a message
    public void plainMessage(String message) {
        JOptionPane.showMessageDialog(this.board,
                message, "A prompt message",
                JOptionPane.PLAIN_MESSAGE);
    }



    // This method constructs a SLGame object and calls its control method
    public static void main(String args[]) throws Exception {


		try {

			String code = JOptionPane.showInputDialog(null, "Developer code: ");

	    	if (code == null) {
	    		godMode = false;
			} else if (code.equals("123")) {
				godMode = true;
			} else {
				godMode = false;
			}

			String[] options = {"OK", "Load Game"};
			int x = JOptionPane.showOptionDialog(null, "Do you want to start a new game?",
	                "Information",
	                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

			if (x==0) {

				throw new Exception();
			}

			if (x==-1) {
				System.exit(0);
			}

			ObjectInputStream in1 = new ObjectInputStream
	                (new FileInputStream("snakegard.ser"));
	    	ObjectInputStream in2 = new ObjectInputStream
	                (new FileInputStream("snake.ser"));
	    	ObjectInputStream in3 = new ObjectInputStream
	                (new FileInputStream("piece.ser"));
	    	ObjectInputStream in4 = new ObjectInputStream
	                (new FileInputStream("ladder.ser"));
	    	ObjectInputStream in5 = new ObjectInputStream
	                (new FileInputStream("rounds.ser"));
	    	ObjectInputStream in6 = new ObjectInputStream
			        (new FileInputStream("stage.ser"));
	    	List<SnakeGuard>sg = (List<SnakeGuard>) in1.readObject();
	    	List<Snake>s = (List<Snake>) in2.readObject();
	    	List<Piece>p = (List<Piece>) in3.readObject();
	    	List<Ladder>l = (List<Ladder>) in4.readObject();
	    	rounds = (Integer) in5.readObject();
	    	stage = (Integer) in6.readObject();
	    	GameMain gameMain = new GameMain(p, s, sg, l);

	    	if (stage == 2) {
	    		gameMain.secondStage();
			}

	        if (stage == 3) {
	        	gameMain.thirdStage();
			}

	        System.exit(0);

		} catch (Exception e){

			GameMain gameMain = new GameMain();
	        gameMain.setUp();
	        gameMain.secondStage();

	        if (stage == 3) {
	        	gameMain.thirdStage();
			}

	        System.exit(0);

		}


    }
}