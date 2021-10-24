# ai-assignment2
Assignment 2 in course Applied Artificial Intelligence (DV2557). AI implementation for Wumpus World with a Naive Bayes agent.

## Authors
The solution agent was created by Christoffer Willander (chal17@student.bth.se) and Oliver Byström (olby16@student.bth.se).

Credits to *Johan Hagelbäck* for the Java application and the game solution.

## Functionality
The solution agent solves the Wumpus world using a **Naive Bayes** approach.

### NaiveBayes.java
This is the file that contains the NaiveBayes class. It uses the `findFrontier()` function to map out the frontier that is then used by the `getModels()` function to get all the possible combinations/models for that frontier. The `calculateProbability()` function then uses the the `verifyModel()` function to filter out any gathered models that are inconsistent. Only the probabilities of the models that are consistent with the observed world are used to calculate the "true" probability.

### MyAgent.java
The agent class that uses the NaiveBayes object to determine the probability for each query model and then tries to find a path to the goal.

The agent finds the shortest path to the goal using the `findBestPath()` function. The `buildPath()` function is called within it to fill a ArrayList that contains all possible paths. Then selects the best path from this list of paths (the path with the least cost) and moves square by square to the goal until it has reached it.