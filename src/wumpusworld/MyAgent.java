package wumpusworld;

/**
 * Contains starting code for creating your own Wumpus World agent.
 * Currently the agent only make a random decision each turn.
 * 
 * @author Johan Hagelbäck
 */
public class MyAgent implements Agent
{
    private World w;
    int rnd;
    int goal [] = new int [2];
    NaiveBayes bayesEngine;
    boolean goalSet = false;
    
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
    
    
   
    
            
    /**
     * Asks your solver agent to execute an action.
     */

    public void doAction()
    {	
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
        
        
       if (cX == goal[0] && cY == goal[1]) {
    	   goalSet = false;
       }
        
       if (!goalSet) {
    	   bayesEngine.findMove(goal); 
    	   goalSet = true;
    	   
    	   if (bayesEngine.wumpusFound) {
    		   goal[0] = bayesEngine.wumpusCoordinates[0];
        	   goal[1] = bayesEngine.wumpusCoordinates[1];
        	   findDirection(); 
    	   }  
       }
       
       /* Checking if the player is adjacent to the Wumpus before shooting */
	   
       if (bayesEngine.wumpusFound && ((cX == goal[0] && (cY == goal[1] - 1 || cY == goal[1] + 1)) || ((cX == goal[0] - 1 || cX == goal[0] + 1) && cY == goal[1]))) {
		   if (w.hasPit(cX, cY)) {
			   w.doAction(w.A_CLIMB);
		   }
    	   w.doAction(w.A_SHOOT);
    	   bayesEngine.wumpusFound = false;
    	   goalSet = false;
	   }	  
       
       if (goalSet) {
    	   findDirection();
    	   w.doAction(w.A_MOVE);
       }
       
       
       
        
        
//        if (move==0)
//        {
//            w.doAction(World.A_TURN_LEFT);
//            w.doAction(World.A_MOVE);
//        }
//        
//        if (move==1)
//        {
//            w.doAction(World.A_MOVE);
//        }
//                
//        if (move==2)
//        {
//            w.doAction(World.A_TURN_LEFT);
//            w.doAction(World.A_TURN_LEFT);
//            w.doAction(World.A_MOVE);
//        }
//                        
//        if (move==3)
//        {
//            w.doAction(World.A_TURN_RIGHT);
//            w.doAction(World.A_MOVE);
//        }
//                
    }    
    
     /**
     * Generates a random instruction for the Agent.
     */
    public int decideRandomMove()
    {
      return (int)(Math.random() * 4);
    }
    
    public void findDirection() {
    	boolean directionSet = false;
    	int distanceX = goal[0] - w.getPlayerX();
    	int distanceY = goal[1] - w.getPlayerY();
    	
    	if (distanceX != 0) {
    		if (distanceX > 0) {
	    		if (w.isVisited(w.getPlayerX() + 1, w.getPlayerY()) || (w.getPlayerX() + 1) == goal[0]) {
					while (w.getDirection() != w.DIR_RIGHT) {
						w.doAction(w.A_TURN_RIGHT);
					}
					directionSet = true;
				}
    		}
    		else {
				if (w.isVisited(w.getPlayerX() - 1, w.getPlayerY()) || (w.getPlayerX() - 1) == goal[0]) {
					while (w.getDirection() != w.DIR_LEFT) {
						w.doAction(w.A_TURN_LEFT);
					}
					directionSet = true;
				}
    		}
    	}
    	
		if (distanceY > 0 && !directionSet) {
			if (w.isVisited(w.getPlayerX(), w.getPlayerY() + 1) || (w.getPlayerY() + 1) == goal[1]) {
				while (w.getDirection() != w.DIR_UP) {
					w.doAction(w.A_TURN_LEFT);
				}
				directionSet = true;
			}	
		}
		else {
			if (w.isVisited(w.getPlayerX(), w.getPlayerY() - 1) || (w.getPlayerY() - 1) == goal[1]) {
				while (w.getDirection() != w.DIR_DOWN) {
					w.doAction(w.A_TURN_RIGHT);
				}
				directionSet = true;
			}
		}
	}
    	
}

