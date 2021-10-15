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
		this.markedTiles = new boolean[4][4];
		
		/* Setting the initialWorld variable to current world */
		this.naiveWorld = currentWorld;
	}
	
	public void makeMove() {
		/* Clearing structures for frontier and probability */
		this.frontierList.clear();
		this.probabilityList.clear();
		this.markedTiles = new boolean[4][4];
		
		/* Cloning current world into clonedWorld */
		findFrontier(this.naiveWorld.getPlayerX(), this.naiveWorld.getPlayerY());
	}
	
	public void findFrontier(int posX, int posY) {
		if (!this.naiveWorld.isValidPosition(posX, posY)) {
			return;
		}
		
		if (this.naiveWorld.isUnknown(posX, posY)) {
			if (!this.markedTiles[posX - 1][posY - 1]) {
				System.out.println("Checking out coordinates ("+posX+", "+posY+")");
				this.markedTiles[posX - 1][posY - 1] = true;
				this.frontierList.add(new int[] {posX, posY});
				this.probabilityList.add(new double[] {0, 0});
				System.out.println("Set coordinates ("+posX+", "+posY+") as frontier");
			}
			else {
				System.out.println("Coordinates ("+posX+", "+posY+") are already marked");
			}
		}
		else if (this.markedTiles[posX - 1][posY - 1]) {
			System.out.println("Coordinates ("+posX+", "+posY+") are already marked");
			return;
		}
		else {
			this.markedTiles[posX - 1][posY - 1] = true;
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

	/**
    *
    * @param xPos The current x-axis coordinate.
    * @param yPos The current y-axis coordinate. 
    */
    private void markNeighborTiles(int xPos, int yPos) {
    	// Only mark the square if it is valid and it has not already been marked.
    	
    	if(this.w.isValidPosition(xPos + 1, yPos) && !this.markedTiles[xPos][yPos - 1]) {
        	this.markedTiles[xPos][yPos - 1] = true;
        }
        if(this.w.isValidPosition(xPos - 1, yPos) && !this.markedTiles[xPos - 2][yPos - 1]) {
        	this.markedTiles[xPos - 2][yPos - 1] = true;
        }
        if(this.w.isValidPosition(xPos, yPos + 1) && !this.markedTiles[xPos - 1][yPos]) {
        	this.markedTiles[xPos - 1][yPos] = true;
        }
        if(this.w.isValidPosition(xPos, yPos - 1) && !this.markedTiles[xPos - 1][yPos - 2]) {
        	this.markedTiles[xPos - 1][yPos - 2] = true;
        }
    }

    /**
    *
    * @param fronteirQuery The frontier-combination to check for consistency. Contains the coordinates for the frontier squares as well as the query with the corresponding true/false status for each coordinate.
    * @param condition Either WUMPUS or PIT. The consistency will be different depending on the condition. 
    * @return boolean
    */
    private boolean verifyConsistency(ArrayList<int[]> fronteirQuery, int condition) {

        //World cw = w.cloneWorld();  /*create a world of conjecture*/
    	// [0][0] represents [1,1] in the game world which means that each index is 1 less than the world square index.
    	this.markedTiles = new boolean[4][4];
        int limit = this.w.getSize();
        boolean consistent = true;

        if(condition==PIT)
        {
            for (int x = 1; x <= limit; x++) {
                for (int y = 1; y <= limit; y++)
                {
                    if(!this.w.isUnknown(x,y) && this.w.hasPit(x,y)) {
                    	/* If we know that a particular square has a pit,
                    	 *  mark all surrounding squares (they should contain a breeze).
                    	 * */
                    	markNeighborTiles(x, y);
                        //cw.markSurrounding(x,y);
                    }
                }
            }
        }


        for (int i = 0; i < fronteirQuery.size(); i++) {
            int cx, cy;
            cx = fronteirQuery.get(i)[0];
            cy = fronteirQuery.get(i)[1];
            if (fronteirQuery.get(i)[2] == 1)
            {
            	/* Mark all the nearby squares as they should contain either a breeze or a stench if 
            	 *  the target square is True (= 1). If a frontier coordinate has a status set to True,
            	 *   we assume that it harbors either a pit or a wumpus, therefore all neighbor squares
            	 *    should contain either a breeze or a stench depending on the condition.
            	 * */
            	markNeighborTiles(cx, cy);
                //cw.markSurrounding(cx,cy);
            }
        }

        // Here we check for any inconsistencies with the marked squares against the real world:
        for (int x = 1; x <= limit; x++) {
            for (int y = 1; y <= limit; y++) {
                if (!(this.w.isUnknown(x, y))) {

                        if(condition==PIT) {
                        	/* If we have a square that is marked (should have a breeze) but we don't find
                        	 *  a breeze on that square according to world, we have a inconsistency.
                        	 * */
                            if (!(this.w.hasBreeze(x, y) == this.markedTiles[x-1][y-1])) {
                            	consistent = false;
                            }
                        }

                        else if(condition==WUMPUS) {
                        	/* If we have a square that is marked (should have a stench) but we don't find
                        	 *  a stench on that square according to world, we have a inconsistency.
                        	 * */
                            if (!(this.w.hasStench(x, y) == this.markedTiles[x-1][y-1])) {
                            	consistent = false;
                            }
                        }
                }

                if (!consistent) {
                	/* We only have to find one inconsistency for the frontier combination to be invalid.
                	 *  This means that we don't have to keep checking the other frontier squares, so we
                	 *   make an early return without finishing the loop.
                	 * */ 
                    return consistent;
                }
            }
        }
        return consistent;
    }
	
}