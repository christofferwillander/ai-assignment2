package wumpusworld;

import java.util.ArrayList;

public class NaiveBayes {
	
	private World bayesWorld;
	
	// Two dimensional array that keeps track on which positions that are marked or not.
	private boolean marked[][];
	
	ArrayList<int[]> frontierList = new ArrayList<int[]>();
	ArrayList<double[]> probabilityList = new ArrayList<double[]>();
	
	// Initial probability of PIT (3/15):
	private double P_PIT = 0.2;
	// Initial probability of Wumpus (1/15):
	private double P_WUMPUS = 0.0667;
	
	// WUMPUS and PIT definitions:
	private static final int WUMPUS = 0;
	private static final int PIT = 1;
	
	/**
     * Constructor of the NaiveBayes object.
     * 
     * @param world The world on which to use the NaiveBayes algorithm on.
     */
	public NaiveBayes(World world) {
		bayesWorld = world;

		// Initialize the marked two dimensional boolean (default values are false). The world is 4x4 in size:
		marked = new boolean[4][4];
	}
	
	/**
     * Sets the frontier of the world using recursion.
     * 
     * @param xPos The position of x.
     * @param yPos The position of y.
     * @return None
     */
	private void findFrontier(int xPos, int yPos) {
		
		// Return back when you are out of bounds:
		if(!bayesWorld.isValidPosition(xPos, yPos)) {
			return;
		}
		
		if(bayesWorld.isUnknown(xPos, yPos)) {
			if(!marked[xPos][yPos]) {
				// Add the position to the frontier:
				frontierList.add(new int[] {xPos, yPos, 0});
				// At the same time, add the corresponding entry into the probability list:
				probabilityList.add(new double[] {0,0});
				// Set the position as marked:
				marked[xPos][yPos] = true;
				// This position is now set as being a part of the frontier.
			}
			else {
				// The position is already marked.
			}
			return;
		}
		else if(marked[xPos][yPos]) {
			// The position is known and already marked, return:
			return;
		}
		else {
			// Set the position as marked and search through the nearby positions:
			marked[xPos][yPos] = true;
			findFrontier(xPos + 1, yPos);
			findFrontier(xPos - 1, yPos);
			findFrontier(xPos, yPos + 1);
			findFrontier(xPos, yPos - 1);
		}
		
	}
	
	/**
     * Updates the probability that there is a pit in the query square
     * 
     * @return None
     */
	private void updatePitProbability() {
		int worldSize = bayesWorld.getSize();
		int knownCount = 0;
		int pitCount = 0;

		// Find all pits and all known squares to update the probability of a pit:
		for(int x = 1; x <= worldSize; x++) {
			for(int y = 1; y <= worldSize; y++) {
				if(bayesWorld.hasPit(x, y)) {
					pitCount += 1;
				}
				else if(!bayesWorld.isUnknown(x, y)) {
					knownCount += 1;
				}
			}
		}
		
		P_PIT = (3-pitCount)/(16-knownCount);
	}
	
	/**
     * Calculates the current amount of known squares.
     * 
     * @param worldSize The size of the world.
     * @return The current amount of known squares in the world.
     */
	/*private int getKnownSquares(int worldSize) {
		int knownSquares = 0;
		
		for(int x = 1; x <= worldSize; x++) {
			for(int y = 1; y <= worldSize; y++) {
				if(!bayesWorld.isUnknown(x, y)) {
					knownSquares += 1;
				}
			}
		}
		return knownSquares;
	}*/
	
}