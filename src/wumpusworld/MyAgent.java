package wumpusworld;
import java.util.ArrayList;

/**
 * Contains starting code for creating your own Wumpus World agent.
 * Currently the agent only make a random decision each turn.
 * 
 * @author Johan Hagelb√§ck
 */
public class MyAgent implements Agent
{
    private World w;
    int rnd;
    int goal [] = new int [2];
    NaiveBayes bayesEngine;
    boolean goalSet = false;
    boolean shootWumpus = false;
    
    ArrayList<Node> currentPath;
    
    
    /**
     * Creates a new instance of your solver agent.
     * 
     * @param world Current world state 
     */
    public MyAgent(World world)
    {
        w = world;
        bayesEngine = new NaiveBayes(w);
    }
    
    private static class Node 
    {
  	  final int x;
  	  final int y;
  	  final int direction;

	  public Node(int x, int y, int direction) 
	  {
	    this.x = x;
	    this.y = y;
	    this.direction = direction;
	  }
	  
	  public Node clone() {
		  return new Node(x, y, direction);
	  }
     }
   
    
            
    /**
     * Asks your solver agent to execute an action.
     */

    public void doAction()
    {		
    	if (!w.gameOver()) {
	        //Location of the player
	        int cX = w.getPlayerX();
	        int cY = w.getPlayerY();   
	        //Basic action:
	        //Grab Gold if we can.
	        if (w.hasGlitter(cX, cY))
	        {
	            w.doAction(World.A_GRAB);
	            return;
	        }
	        
	        //Basic action:
	        //We are in a pit. Climb up.
	        if (w.isInPit())
	        {
	            w.doAction(World.A_CLIMB);
	            return;
	        }
	        
	        //Test the environment
	        if (w.hasBreeze(cX, cY))
	        {
	            System.out.println("I am in a Breeze");
	        }
	        if (w.hasStench(cX, cY))
	        {
	            System.out.println("I am in a Stench");
	        }
	        if (w.hasPit(cX, cY))
	        {
	            System.out.println("I am in a Pit");
	        }
	        if (w.getDirection() == World.DIR_RIGHT)
	        {
	            System.out.println("I am facing Right");
	        }
	        if (w.getDirection() == World.DIR_LEFT)
	        {
	            System.out.println("I am facing Left");
	        }
	        if (w.getDirection() == World.DIR_UP)
	        {
	            System.out.println("I am facing Up");
	        }
	        if (w.getDirection() == World.DIR_DOWN)
	        {
	            System.out.println("I am facing Down");
	        }
	        if (w.hasGlitter(cX, cY)) {
	        	w.doAction(w.A_GRAB);
	        }
	       
	       /* If goal was reached, set goalSet to false and currentPath to NULL */
	       if ((cX == goal[0] && cY == goal[1]) || shootWumpus) {
	    	   goalSet = false;
	    	   currentPath = null;
	    	   shootWumpus = false;
	       }
	       
	       
	       
	       /* If a new goal has to be found */ 
	       if (!goalSet) {
	    	   
	    	   /* Find goal using NaÔve Bayes model */
	    	   bayesEngine.findMove(goal); 
	    	   goalSet = true;
	    	   
	    	   /* If Wumpus was found, set this as our new goal */
	    	   if (bayesEngine.wumpusFound) {
	    		   goal[0] = bayesEngine.wumpusCoordinates[0];
	        	   goal[1] = bayesEngine.wumpusCoordinates[1];
	    	   }  
	       }
	       
	       /* Checking if the player is adjacent to the Wumpus before shooting */
	       if (bayesEngine.wumpusFound && ((cX == goal[0] && (cY == goal[1] - 1 || cY == goal[1] + 1)) || ((cX == goal[0] - 1 || cX == goal[0] + 1) && cY == goal[1]))) {
			   shootWumpus = true;
			   
	    	   if (w.hasPit(cX, cY)) {
				   w.doAction(w.A_CLIMB);
			   }
		   }
	       
	       /* Find the best path to our goal */
	       if (goalSet && currentPath == null) {
	    	   currentPath = findBestPath(cX, cY);
	       }
	       
	       if (bayesEngine.probabilityList.size() == 2 && (bayesEngine.probabilityList.get(0)[1] == bayesEngine.probabilityList.get(1)[1]) && (bayesEngine.probabilityList.get(0)[1] > 0)) {
	    	   shootWumpus = true;
	       }
	       
	       /* Move towards our goal using the determined path */
	       if (goalSet && currentPath != null) {
	    	   while (w.getDirection() != currentPath.get(0).direction) {
	    		   w.doAction(w.A_TURN_LEFT);
	    	   }
	    	   
	    	   if (!shootWumpus) {
	    		   w.doAction(w.A_MOVE);
	    	   }
	    	   else {
	        	   w.doAction(w.A_SHOOT);
	        	   bayesEngine.wumpusFound = false;
	    	   }
	    	   currentPath.remove(0);
	       }
    	}
    }    
    
     /**
     * Generates a random instruction for the Agent.
     */
    public int decideRandomMove()
    {
      return (int)(Math.random() * 4);
    }
    
    private ArrayList<Node> cloneList(ArrayList<Node> list) {
        ArrayList<Node> listClone = new ArrayList<Node>(list.size());
        
        // Clone each item in the input list to the clone:
        for (int listItem = 0; listItem < list.size(); listItem++) {
            listClone.add(list.get(listItem).clone());
        }
        return listClone;
    }
    
    private boolean[][] cloneTrail(boolean[][] trail) {
    	int dimensionOne = trail.length;
    	int dimensionTwo = trail[0].length;
    	boolean[][] clonedTrail = new boolean[dimensionOne][dimensionTwo];
    	
    	for (int i = 0; i < dimensionOne; i++) {
    		for (int j = 0; j < dimensionTwo; j++) {
    			clonedTrail[i][j] = trail[i][j];
    		}
    	}
    	
    	return clonedTrail;
    }
    
    public ArrayList<Node> findBestPath(int posX, int posY) {
    	int shortestPath = -1;
    	
    	/* Array of Nodes for holding the BEST path */
    	ArrayList<Node> bestPath = new ArrayList<Node>();
    	
    	/* Temporary pointer to current ArrayList<Node> */
    	ArrayList<Node> curPath;
    	
    	/* Structure for holding ALL paths */
    	ArrayList<ArrayList<Node>> allPaths = new ArrayList<ArrayList<Node>>();
    	
    	/* Initialized ArrayList<Node> structure for buildPath() function */
    	ArrayList<Node> initialPath = new ArrayList<Node>();
    	boolean[][] initialTrail = new boolean[4][4];
    	
    	/* Investigating moves in each axis */
    	buildPath(posX + 1, posY, w.DIR_RIGHT, initialPath, allPaths, initialTrail);
    	buildPath(posX - 1, posY, w.DIR_LEFT, initialPath, allPaths, initialTrail);
    	buildPath(posX, posY + 1, w.DIR_UP, initialPath, allPaths, initialTrail);
    	buildPath(posX, posY - 1, w.DIR_DOWN, initialPath, allPaths, initialTrail);
    	
    	/* Finding the shortest path out of those available */
    	for (int i = 0; i < allPaths.size(); i++) {
    		curPath = allPaths.get(i);
    		
    		/* If shortestPath has not yet been set and current trail has > 0 nodes */
    		if (shortestPath == -1 && curPath.size() > 0) {
    			shortestPath = curPath.size();
    			bestPath = curPath;
    		}
    		else if (curPath.size() < shortestPath && curPath.size() > 0) { /* Current path is shorter than previous path */
    			shortestPath = curPath.size();
    			bestPath = curPath;
    		}
    	}
    	
    	return bestPath;
    }
    
    private void buildPath(int posX, int posY, int direction, ArrayList<Node> initialPath, ArrayList<ArrayList<Node>> allPaths, boolean[][] trail) {
    	/* Cloning currentPath and currenTrail for each recursive call */
    	ArrayList<Node> currentPath = cloneList(initialPath);
    	boolean [][] currentTrail = cloneTrail(trail);
    	
    	/* If tile has been visited and is safe (i.e. no pit or Wumpus) */
    	if (w.isVisited(posX, posY) && !w.hasPit(posX, posY) && !w.hasWumpus(posX, posY) && !currentTrail[posX - 1][posY - 1]) {
    		/* Adding current position to path */
    		currentPath.add(new Node(posX, posY, direction));
    		
    		/* Setting current position as visited */
    		currentTrail[posX - 1][posY - 1] = true;
    		
    		/* Recursively finding valid moved for each axis */
    		buildPath(posX + 1, posY, w.DIR_RIGHT, currentPath, allPaths, currentTrail);
    		buildPath(posX - 1, posY, w.DIR_LEFT, currentPath, allPaths, currentTrail);
    		buildPath(posX, posY + 1, w.DIR_UP, currentPath, allPaths, currentTrail);
    		buildPath(posX, posY - 1, w.DIR_DOWN, currentPath, allPaths, currentTrail);
    	}
    	else if (posX == goal[0] && posY == goal[1] && !currentTrail[posX - 1][posY - 1]) { /* If end of trail was reached */
    		/* Adding current position to path */
    		currentPath.add(new Node(posX, posY, direction));
    		
    		/* Setting current position as visited */
    		currentTrail[posX - 1][posY - 1] = true;
    		
    		/* Adding path to allPaths ArrayList */
    		allPaths.add(currentPath);
    	}
    	
    	return;
    }
    
}




