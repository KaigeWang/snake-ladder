
package controller;

import exception.*;
import model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.RepaintManager;

public class HumanController {
    private Board board;

    private List <Piece> pieces;
    private List <Snake> snakes;
    private List <Ladder> ladders;
    private UIWrapper uiWrapper;


	public HumanController(Board board){
        this.board = board;
        uiWrapper =  new UIWrapper(board);
        pieces = board.getPiece();
    	snakes = board.getSnakes();
    	ladders = board.getLadder();

    }

    // for test purpose
    public void setUiWrapper(UIWrapper uiWrapper) {
        this.uiWrapper = uiWrapper;
    }

    public int rollDice() {
        uiWrapper.showInfoMessage("Click to confirm to roll the dice");
        Dice dice = board.getDice();
        return dice.roll();
    }


    //if place snake guard;
    public boolean ifPlaceGuard() {
    	if (board.getSnakeGaurdCounts() < 3) {
			int n = uiWrapper.showComfirmDialog("Do you want to place a snake guard");
			if (n == 0) {
				try {
					//get user input;
					String m = uiWrapper.promptForInput("Position: ").trim();
					int position = Integer.parseInt(m);

					//cant place guard over snake head;
					for(Snake i: snakes){
						if (position == i.getTop()||position == i.getBottom()) {
							throw new SnakeGuardPlacementException();
						}
					}
					//cant place out of the board;
					if (position <= 100 && position >= 1) {
						SnakeGuard sg = new SnakeGuard(position);
						board.add(sg);
						return true;
					} else {
						throw new SnakeGuardPlacementException();
					}

				} catch (SnakeGuardPlacementException e){
					uiWrapper.showErrorMessage("Cant place over snakes or out of board!");
					ifPlaceGuard();
				} catch (Exception e) {
					uiWrapper.showErrorMessage("Invalid input!");
					ifPlaceGuard();
				}
			}
		}
		return false;

    }

    // 1 to kill snake; 0 to kill piece; 2 to nothing;
    public void knightMove(int pieceNum) throws InvalidInputException{

    	int n = chooseDestination(pieceNum);
    	board.setPiece(pieceNum, n);


    }

    public void godMove(int piece){
    	String n = uiWrapper.promptForInput("new location: ");
    	try {
    		int m= Integer.parseInt(n);
    		board.getPiece(piece).setPosition(m);;
		} catch (Exception e) {
			uiWrapper.showErrorMessage("input integer");
			godMove(piece);
		}
    	board.repaint();

    }

    //Stage 2 move;
    public void movePieceByDice (int piece) throws BoundaryException {
    	uiWrapper.showInfoMessage("Player " +  (piece +1) + " throw dice");
    	int diceValue = board.getDice().roll();
    	try {
    		board.getPiece(piece).move(diceValue);
		} catch (BoundaryException e) {
			uiWrapper.showErrorMessage("You go out of the board!");
		}

    	board.repaint();

    }


    public int chooseDestination(int pieceNumber) throws InvalidInputException {
        while (true) {
            String input = uiWrapper.promptForInput("Please choose the position to move to");
            try {
                int destination = Integer.parseInt(input);
                if(!validateDestination(board.getPiece(pieceNumber).getPosition(), destination))
                    throw new AssertionError();
                return destination;
            } catch (NumberFormatException e) {
                uiWrapper.showErrorMessage("Not a valid input. Must be an integer!");
            } catch (AssertionError e) {
                uiWrapper.showErrorMessage("Not a valid move. Can only be a diagonal or knight move!");
            }
        }
    }

    private boolean validateDestination(int currentPosition, int destination) {
    	int i;// current row number

    	if (currentPosition%10 == 0) {
    		 i = currentPosition/10;
		} else {
			 i = currentPosition/10 + 1;
		}

        int j = i%2==0 ? i*10-currentPosition+1 : currentPosition%10; // current column number
        Set<Integer> validDest = new HashSet<>();
        for(int k=-2; k<=2; k++){
            if (k!=0){
                int destRow = i+k;
                if(destRow>=1 && destRow<=10){
                    int squareUnder = destRow%2 == 0 ? 10*destRow-j+1 : 10*(destRow-1)+j;
                    int lowerBoundary = 10*(destRow-1);
                    int upperBoundary = 10*destRow;
                    if (k==-2 || k==2) {
                        int t = squareUnder+1;
                        if (t <= upperBoundary) {
                            validDest.add(t);
                        }
                        t = squareUnder-1;
                        if (t > lowerBoundary) {
                            validDest.add(t);
                        }
                    } else {
                        for (int t=squareUnder+1; t<=squareUnder+2; t++) {
                            if (t <= upperBoundary) {
                                validDest.add(t);
                            }
                        }
                        for (int t=squareUnder-2; t<=squareUnder-1; t++) {
                            if (t > lowerBoundary) {
                                validDest.add(t);
                            }
                        }
                    }
                }
            }
        }
        return validDest.contains(destination);
    }

    //Stage 3 validation;
    public boolean stage3validatePieceLcations(){


    	List <Snake> gonnaremove = new ArrayList<Snake>();

    	for(Piece i: pieces){
    		for(Snake j: snakes){
    			if (i.getPosition() == j.getBottom()) {
    				board.addMessage("Killed a snake!");
					gonnaremove.add(j);
				}

    			if (i.getPosition() == j.getTop()) {
    				return false;
				}
    		}
    	}
    	snakes.removeAll(gonnaremove);

    	board.repaint();
    	return true;

    }

    //Stage 2 validation;
    public void stage2validatePieceLcations() throws BoundaryException{


    	//drop snake
    	for(Piece i: pieces){
    		for(Snake j: snakes){
    			if (i.getPosition() == j.getTop()) {
					i.setPosition(j.getBottom());
					i.paralyse();
					board.addMessage("Piece " + (i.getIndex()+1) + " drop to " + i.getPosition());
					board.repaint();
				}
    		}
    	}


    	//climb ladder
    	for(Piece i: pieces){
    		for(Ladder j: ladders){
    			if (i.getPosition() == j.getBottom()){
    				i.setPosition(j.getTop());
    				board.addMessage("Piece " + (i.getIndex()+1) + " clim to " + i.getPosition());
    				board.repaint();
    				i.addLadderClimb();
    			}
    		}
    	}


    }


}