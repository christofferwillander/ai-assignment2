package wumpusworld;

import java.util.ArrayList;

public class NaiveBayes {
	
	/* Variable that will refer to the current status of the world */
	private World naiveWorld;
	
	/* Boolean 2D-array for keeping track of marked tiles */
	private boolean markedTiles[][];
	
	/* Structures for keeping track of frontier and probability */
	ArrayList<int[]> frontierList = new ArrayList<int[]>();
	ArrayList<double[]> probabilityList = new ArrayList<double[]>();
	
	private int pitCount = 0;
	
	/* Probability of pit given 3 pits in the configuration */
	private static double PROBABILITY_PIT = 3/15;
	
	/* Probability of Wumpus given 1 in the configuration */
	private static double PROBABILITY_WUMPUS = 1/15;
	
	/* Static constants for different cases */
	private static final int PIT = 0;
	private static final int WUMPUS = 1;
	
	public NaiveBayes(World currentWorld) {
		/* Initializing the 2D boolean array for marked tiles (default false) */
		markedTiles = new boolean[4][4];
		
		/* Setting the initialWorld variable to current world */
		naiveWorld = currentWorld;
	}
	
	public void makeMove() {
		/* Clearing structures for frontier and probability */
		frontierList.clear();
		probabilityList.clear();
		markedTiles = new boolean[4][4];
		
		/* Cloning current world into clonedWorld */
		//clonedWorld = initialWorld.cloneWorld();
		findFrontier(naiveWorld.getPlayerX(), naiveWorld.getPlayerY());
	}
	
	public void findFrontier(int posX, int posY) {
		if (!naiveWorld.isValidPosition(posX, posY)) {
			return;
		}
		
		if (naiveWorld.isUnknown(posX, posY)) {
			if (!markedTiles[posX - 1][posY - 1]) {
				System.out.println("Checking out coordinates ("+posX+", "+posY+")");
				markedTiles[posX - 1][posY - 1] = true;
				frontierList.add(new int[] {posX, posY});
				probabilityList.add(new double[] {0, 0});
				System.out.println("Set coordinates ("+posX+", "+posY+") as frontier");
			}
			else {
				System.out.println("Coordinates ("+posX+", "+posY+") are already marked");
			}
		}
		else if (markedTiles[posX - 1][posY - 1]) {
			System.out.println("Coordinates ("+posX+", "+posY+") are already marked");
			return;
		}
		else {
			markedTiles[posX - 1][posY - 1] = true;
			findFrontier(posX + 1, posY);
			findFrontier(posX - 1, posY);
			findFrontier(posX, posY + 1);
			findFrontier(posX, posY - 1);
		}
	}
	
	private void calculateProbability(int sensor) {
		/* TBA */
	}
	
	private void confirmTheory() {
		/* TBA */
	}
	
	private void findMove() {
		/* TBA */
	}
	
}