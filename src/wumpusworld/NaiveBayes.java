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
	
	public boolean wumpusFound = false;
	public int [] wumpusCoordinates = {0, 0};
	
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
	
	private int getKnowns() {
		int knownTiles = 0;
		
		for (int x = 1; x <= naiveWorld.getSize(); x++) {
			for (int y = 1; y <= naiveWorld.getSize(); y++) {
				if (naiveWorld.isVisited(x, y)) {
					knownTiles++;
				}
			}
		}
		
		return knownTiles; 
	}
	
	public void findMove(int goal []) {
		double tempProb = 1;
		double bestProb = 2;

		/* Clearing structures for frontier and probability */
		this.frontierList.clear();
		this.probabilityList.clear();
		this.markedTiles = new boolean[4][4];
		
		/* Finding current frontier */
		findFrontier(this.naiveWorld.getPlayerX(), this.naiveWorld.getPlayerY());	
		
		/* Calculating probability for encountering pit/Wumpus */
		calculateProbability(PIT);
		calculateProbability(WUMPUS);
		
		for (int i = 0; i < probabilityList.size(); i++) {
			
			tempProb = (probabilityList.get(i)[WUMPUS] + probabilityList.get(i)[PIT]);
			
			if (tempProb <= bestProb) {
				bestProb = tempProb;
				goal[0] = (int)probabilityList.get(i)[2];
				goal[1] = (int)probabilityList.get(i)[3];
			}	
		}
		
		System.out.println("Current goal: (" + Integer.toString((int)goal[0]) + ", " + Integer.toString((int)goal[1]) + ")");
	}
	
	public boolean shouldShoot() {
		boolean shoot = false;
		
		if (wumpusFound) {
			
		}
		else {
			
		}
		
		return shoot;
	}
	
	public void findFrontier(int posX, int posY) {
		if (!this.naiveWorld.isValidPosition(posX, posY)) {
			return;
		}
		
		if (this.naiveWorld.isUnknown(posX, posY)) {
			if (!this.markedTiles[posX - 1][posY - 1]) {
				System.out.println("Checking out coordinates ("+posX+", "+posY+")");
				this.markedTiles[posX - 1][posY - 1] = true;
				this.frontierList.add(new int[] {posX, posY, 0});
				this.probabilityList.add(new double[] {0, 0, 0, 0});
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
	
	
	/**
	 * Function for calculating the probability of encountering a pit/Wumpus given the different combinations
	 * @param sensor Integer stating which type of sensor be used for probability calculations (PIT/WUMPUS)
	 */
	private void calculateProbability(int sensor) {
		double probability;
		System.out.println("--------Probability calculations--------");
		
		/* Setting the probability according to the sensor in question */
		if (sensor == PIT) {
			PROBABILITY_PIT = updateProbability(PIT);
			probability = PROBABILITY_PIT;
			System.out.println("Calculating probability for pit");
		}
		else if (sensor == WUMPUS) {
			PROBABILITY_WUMPUS = updateProbability(WUMPUS);
			probability = PROBABILITY_WUMPUS;
			System.out.println("Calculating probability for Wumpus");
		}
		else {
			System.out.println("Invalid sensor type");
			return;
		}
		
		/* Calculating probability for the frontier */
		for (int i = 0; i < frontierList.size(); i++) {
			/* Probability variables for true and false probabilities respectively */
			double trueProbability = 0;
			double falseProbability = 0;
			
			/* Probability after normalization */
			double normalizedProbability = 0;
			
			/* Arrays of type int holding the frontier queries with their corresponding true/false flags */
			int [] trueQuery = new int[] {frontierList.get(i)[0], frontierList.get(i)[1], 1};
			int [] falseQuery = new int[] {frontierList.get(i)[0], frontierList.get(i)[1], 0};
			
			/* Cloning list for current query */
			ArrayList<int[]> currentQuery = cloneList(frontierList);
			currentQuery.remove(i);
			
			/* Creating structure for holding combinations that will be generated inside getCombinations() */
			ArrayList<ArrayList<int []>> combinationResult = new ArrayList<ArrayList<int[]>>();
			
			/* Array holding the number of 1's (true) in each combination */
			int[] combinationCount = getCombinations(currentQuery, combinationResult);
			
			/* Calculating the probability for all combinations in the array list */
			for (int combination = 0; combination < combinationResult.size(); combination++) {
				int totalCombinations = combinationResult.size();
				
				/* Constructing the true combination query */
				ArrayList<int[]> trueCombinationQuery = cloneList(combinationResult.get(combination));
				trueCombinationQuery.add(trueQuery);
				
				/* Constructing the false combination query */
				ArrayList<int[]> falseCombinationQuery = cloneList(combinationResult.get(combination));
				falseCombinationQuery.add(falseQuery);
				
				/* If sensor in question is PIT and there are <= 3 pits OR if sensor is WUMPUS and there are <= 1 WUMPUS */
				if ((sensor == PIT && combinationCount[combination] <= 3) || (sensor == WUMPUS && combinationCount[combination] <= 1)) {
					/* Verify consistency for true and false query, if consistent - add probability */
					if (verifyConsistency(trueCombinationQuery, sensor)) {
						trueProbability += Math.pow(probability, combinationCount[combination]) * Math.pow(1 - probability, totalCombinations - combinationCount[combination]);
					}
					
					if (verifyConsistency(falseCombinationQuery, sensor)) {
						falseProbability += Math.pow(probability, combinationCount[combination]) * Math.pow(1 - probability, totalCombinations - combinationCount[combination]);
					}
				}
			}
			
			/* Calculating true and false probabilities */
			trueProbability = probability * trueProbability;
			falseProbability = (1 - probability) * falseProbability;
			
			/* Normalizing true probability */
			normalizedProbability = trueProbability / (trueProbability + falseProbability);
			
			
			/* Updating the probability in the probability list */
			double [] tempProbability = probabilityList.get(i);
			
			if (sensor == PIT) {
				tempProbability[PIT] = normalizedProbability;
				tempProbability[2] = frontierList.get(i)[0];
				tempProbability[3] = frontierList.get(i)[1];
				System.out.println("Current probability for pit: " + Double.toString(normalizedProbability * 100) + " %" + " (" + Double.toString(frontierList.get(i)[0]) + ", " + Double.toString(frontierList.get(i)[1]) + ")");
				
			}
			else if (sensor == WUMPUS) {
				tempProbability[WUMPUS] = normalizedProbability;
				tempProbability[2] = frontierList.get(i)[0];
				tempProbability[3] = frontierList.get(i)[1];
				System.out.println("Current probability for Wumpus: " + Double.toString(normalizedProbability * 100) + " %" + " (" + Double.toString(frontierList.get(i)[0]) + ", " + Double.toString(frontierList.get(i)[1]) + ")");
			}
			
			probabilityList.set(i, tempProbability);
			
			/* If Wumpus has been located with 100 % certainty, save coordinates and update Wumpus status */
			if (sensor == WUMPUS && normalizedProbability == 1) {
				wumpusFound = true;
				wumpusCoordinates[0] = frontierList.get(i)[0];
				wumpusCoordinates[1] = frontierList.get(i)[1];
				System.out.println("Found Wumpus at (" + Integer.toString(wumpusCoordinates[0]) + ", " + Integer.toString(wumpusCoordinates[1]) + ")");
				return;
			}
		}
	}

	/**
    *
    * @param xPos The current x-axis coordinate.
    * @param yPos The current y-axis coordinate. 
    */
    private void markNeighborTiles(int xPos, int yPos) {
    	// Only mark the square if it is valid and it has not already been marked.
    	
    	if(this.naiveWorld.isValidPosition(xPos + 1, yPos) && !this.markedTiles[xPos][yPos - 1]) {
        	this.markedTiles[xPos][yPos - 1] = true;
        }
        if(this.naiveWorld.isValidPosition(xPos - 1, yPos) && !this.markedTiles[xPos - 2][yPos - 1]) {
        	this.markedTiles[xPos - 2][yPos - 1] = true;
        }
        if(this.naiveWorld.isValidPosition(xPos, yPos + 1) && !this.markedTiles[xPos - 1][yPos]) {
        	this.markedTiles[xPos - 1][yPos] = true;
        }
        if(this.naiveWorld.isValidPosition(xPos, yPos - 1) && !this.markedTiles[xPos - 1][yPos - 2]) {
        	this.markedTiles[xPos - 1][yPos - 2] = true;
        }
    }

    /**
    *
    * @param frontierQuery The frontier-combination to check for consistency. Contains the coordinates for the frontier squares as well as the query with the corresponding true/false status for each coordinate.
    * @param condition Either WUMPUS or PIT. The consistency will be different depending on the condition. 
    * @return boolean
    */
    private boolean verifyConsistency(ArrayList<int[]> frontierQuery, int condition) {

        //World cw = w.cloneWorld();  /*create a world of conjecture*/
    	// [0][0] represents [1,1] in the game world which means that each index is 1 less than the world square index.
    	this.markedTiles = new boolean[4][4];
        int limit = this.naiveWorld.getSize();
        boolean consistent = true;

        if(condition==PIT)
        {
            for (int x = 1; x <= limit; x++) {
                for (int y = 1; y <= limit; y++)
                {
                    if(!this.naiveWorld.isUnknown(x,y) && this.naiveWorld.hasPit(x,y)) {
                    	/* If we know that a particular square has a pit,
                    	 *  mark all surrounding squares (they should contain a breeze).
                    	 * */
                    	markNeighborTiles(x, y);
                        //cw.markSurrounding(x,y);
                    }
                }
            }
        }


        for (int i = 0; i < frontierQuery.size(); i++) {
            int cx, cy;
            cx = frontierQuery.get(i)[0];
            cy = frontierQuery.get(i)[1];
            if (frontierQuery.get(i)[2] == 1)
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
                if (!(this.naiveWorld.isUnknown(x, y))) {

                        if(condition==PIT) {
                        	/* If we have a square that is marked (should have a breeze) but we don't find
                        	 *  a breeze on that square according to world, we have a inconsistency.
                        	 * */
                            if (!(this.naiveWorld.hasBreeze(x, y) == this.markedTiles[x-1][y-1])) {
                            	consistent = false;
                            }
                        }

                        else if(condition==WUMPUS) {
                        	/* If we have a square that is marked (should have a stench) but we don't find
                        	 *  a stench on that square according to world, we have a inconsistency.
                        	 * */
                            if (!(this.naiveWorld.hasStench(x, y) == this.markedTiles[x-1][y-1])) {
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

	/**
    *
    * @param list The list that we want to clone.
    * 
    * @return A list clone.
    */
    private ArrayList<int[]> cloneList(ArrayList<int[]> list) {
        ArrayList<int[]> listClone = new ArrayList<int[]>(list.size());
        
        // Clone each item in the input list to the clone:
        for (int listItem = 0; listItem < list.size(); listItem++) {
            listClone.add(list.get(listItem).clone());
        }
        return listClone;
    }

	/**
    *
    * @param combinationSet The set of squares that will be combined into different situations. Format: int[] { pos_x, pos_y, status }, where status 1 = true | 0 = false.
    * @param combinationResult A call by reference list of combinationSets that will store the result after the different combinations have been created.
	*
    * @return A list of the number of 1's (true) in each combination (mirrors the indexes of combinationResult). This value can be used to verify that the number of 1's set in a specific combination correspond to the number of existing pits and wumpus.
    */
    private int[] getCombinations(ArrayList<int[]> combinationSet, ArrayList<ArrayList<int[]>> combinationResult) {

	  // The bitLength should equal the total number of squares in the frontier (excluding the query square):
	  int bitLength = combinationSet.size();
	  
	  // Indicates the number of different combinations (2^n)
	  int nrOfCombinations = 1 << bitLength;
	  
	  // Holds the count of the number of 1's (true) that are set in each combination:
	  int[] trueCount = new int[nrOfCombinations];
	  
	  // Temporary list that holds the current combinations:
	  ArrayList<int[]> currentCombination;

	  for(int bit = 0; bit < nrOfCombinations; bit++) {
		  // Counting the positions whose statuses are true in the current combination:
		  int nrOfOnes = 0;
		  
		  /* Get the current combinations from the combination set. The cloning is done to
		   *  make sure that no changes are done to the combinationSet object. In each bit
		   *   iteration we need the currentCombination to be in the original state so that
		   *    all statuses are set to 0 (false), which they are per default in the
		   *     combinationSet object.
		   * */
		  currentCombination = cloneList(combinationSet);
		  
		  for(int j = 0; j < bitLength; j++) {
			  /* Use a mask to get the different combinations of true/false, example if we have 2 squares as our frontier we have 2 bits:
			   * -------------------------
			   * mask	= 1 = 01
			   * bit	= 0 = 00
			   * mask & bit == 0 (AND operation).
			   * ---
			   * mask	= 2 = 10
			   * bit	= 0 = 00
			   * mask & bit == 0.
			   * ---
			   * mask	= 1 = 01
			   * bit	= 1 = 01
			   * mask & bit == 1.
			   * ---
			   * mask	= 2 = 10
			   * bit	= 1 = 01
			   * mask & bit == 0.
			   * ---
			   * mask	= 1 = 01
			   * bit	= 2 = 10
			   * mask & bit == 0.
			   * ---
			   * mask	= 2 = 10
			   * bit	= 2 = 10
			   * mask & bit == 2.
			   * ---
			   * mask	= 1 = 01
			   * bit	= 3 = 11
			   * mask & bit == 1.
			   * ---
			   * mask	= 2 = 10
			   * bit	= 3 = 11
			   * mask & bit == 2.
			   * -------------------------
			   * If we interpret any other value than 0 as 1, we get all possible combinations:
			   * 00 (none of the squares harbors either a pit or a wumpus)
			   * 10 (only one of the squares harbors either a pit or a wumpus, 1st one)
			   * 01 (only one of the squares harbors either a pit or a wumpus, 2nd one)
			   * 11 (both of them harbors either a pit or a wumpus)
			   * */
			  int mask = 1 << j;
			  /* System.out.println(mask + " & " + bit + " == " + (mask & bit)); */
			  if((mask & bit)!= 0) {
				  // Set the status of the square to 1 (true):
				  currentCombination.get(j)[2] = 1;
				  nrOfOnes++;
			  }
		  }

		  combinationResult.add(currentCombination);
		  trueCount[bit] = nrOfOnes;
	  }

	  return trueCount;
  }

  /**
    *
    * @param sensor Either PIT or WUMPUS.
    * 
    * @return The updated probability for the given sensor.
    */
    private double updateProbability(int sensor) {
    	double probability = 1;
    	double knownSquares = getKnowns();
    	
        if(sensor == PIT) {
        	int size = naiveWorld.getSize();
        	int discoveredPits = 0;
            for (int x = 1; x <= size; x++) {
                for (int y = 1; y <= size; y++)
                {
                    if(!naiveWorld.isUnknown(x,y) && naiveWorld.hasPit(x,y)){
                    	discoveredPits++;
                    }
                }
            }
            probability = (3 - discoveredPits)/(16 - knownSquares);
        }
        else if (sensor == WUMPUS) {
        	probability = 1/(16 - knownSquares);
        }
        else {
        	System.out.println("Incorrect sesnor provided in updateProbability!");
        }
        
        return probability;
    }
	
}