package tides;

import java.util.*;

/**
 * This class contains methods that provide information about select terrains 
 * using 2D arrays. Uses floodfill to flood given maps and uses that 
 * information to understand the potential impacts. 
 * Instance Variables:
 *  - a double array for all the heights for each cell
 *  - a GridLocation array for the sources of water on empty terrain 
 * 
 * @author Original Creator Keith Scharz (NIFTY STANFORD) 
 * @author Vian Miranda (Rutgers University)
 */
public class RisingTides {

    // Instance variables
    private double[][] terrain;     // an array for all the heights for each cell
    private GridLocation[] sources; // an array for the sources of water on empty terrain 

    /**
     * DO NOT EDIT!
     * Constructor for RisingTides.
     * @param terrain passes in the selected terrain 
     */
    public RisingTides(Terrain terrain) {
        this.terrain = terrain.heights;
        this.sources = terrain.sources;
    }

    /**
     * Find the lowest and highest point of the terrain and output it.
     * 
     * @return double[][], with index 0 and index 1 being the lowest and 
     * highest points of the terrain, respectively
     */
    public double[] elevationExtrema() {

        double lowestElevation = Double.POSITIVE_INFINITY;
        double highestElevation = Double.NEGATIVE_INFINITY;  
     for (int row = 0; row < terrain.length; row++) {
        for (int col = 0; col < terrain[row].length; col++) {
            double Elevation = terrain[row][col];
                lowestElevation = Math.min(lowestElevation,  Elevation);
                highestElevation = Math.max(highestElevation, Elevation);
        }
    }
    double[] extremavalue = {lowestElevation, highestElevation};
    return extremavalue;
    }

    /**
     * Implement the floodfill algorithm using the provided terrain and sources.
     * 
     * All water originates from the source GridLocation. If the height of the 
     * water is greater than that of the neighboring terrain, flood the cells. 
     * Repeat iteratively till the neighboring terrain is higher than the water 
     * height.
     * 
     * 
     * @param height of the water
     * @return boolean[][], where flooded cells are true, otherwise false
     */
    public boolean[][] floodedRegionsIn(double height) {


        int[] directionrow = {-1, 1, 0, 0};
        int[] directioncolumn = {0, 0, -1, 1};

        int numberofRows = terrain.length;    
        int numberofColumns = terrain[0].length; 
        boolean[][] floodedregion = new boolean[numberofRows][numberofColumns]; 

        Queue<GridLocation> queue = new LinkedList<>();

        for (GridLocation source : sources) {
            queue.offer(source);
            floodedregion[source.row][source.col] = true;
        }

        while(!queue.isEmpty()){
            GridLocation present = queue.poll();

        for (int i = 0; i < 4; i++) { 
            int newRow = present.row + directionrow[i];
            int newColumn = present.col + directioncolumn [i];
    
            if (newRow >= 0 && newRow < numberofRows && newColumn >= 0 && newColumn < numberofColumns) {
                if (terrain[newRow][newColumn]<= height && !floodedregion[newRow][newColumn]){
                    floodedregion[newRow][newColumn] = true;
                    queue.offer(new GridLocation(newRow, newColumn));
                }
            }
        }
    }
    return floodedregion;
    }

    /**
     * Checks if a given cell is flooded at a certain water height.
     * 
     * @param height of the water
     * @param cell location 
     * @return boolean, true if cell is flooded, otherwise false
     */
    public boolean isFlooded(double height, GridLocation cell) {
         if (floodedRegionsIn(height)[cell.row][cell.col] == true) {
            return true;
        }else {
            return false;}
        }

    /**
     * Given the water height and a GridLocation find the difference between 
     * the chosen cells height and the water height.
     * 
     * If the return value is negative, the Driver will display "meters below"
     * If the return value is positive, the Driver will display "meters above"
     * The value displayed will be positive.
     * 
     * @param height of the water
     * @param cell location
     * @return double, representing how high/deep a cell is above/below water
     */
    public double heightAboveWater(double height, GridLocation cell) {
       double groundHeight = terrain[cell.row][cell.col];
        double heightdiff = groundHeight- height;
        return heightdiff;
    }

    /**
     * Total land available (not underwater) given a certain water height.
     * 
     * @param height of the water
     * @return int, representing every cell above water
     */
    public int totalVisibleLand(double height) {
       int totalLand = 0;
       
        boolean[][] terrainlength = floodedRegionsIn(height);
        for (int row = 0; row < terrain.length; row++) {
            for (int col = 0; col < terrain[0].length; col++) {
                if (!terrainlength[row][col]) {
                    totalLand++;
                }
            }
        }
        return totalLand;

        
    } 


    /**
     * Given 2 heights, find the difference in land available at each height. 
     * 
     * If the return value is negative, the Driver will display "Will gain"
     * If the return value is positive, the Driver will display "Will lose"
     * The value displayed will be positive.
     * 
     * @param height of the water
     * @param newHeight the future height of the water
     * @return int, representing the amount of land lost or gained
     */
    public int landLost(double height, double newHeight) {
        return totalVisibleLand(newHeight)-totalVisibleLand(newHeight);
    }

  

    /**
     * Count the total number of islands on the flooded terrain.
     * 
     * Parts of the terrain are considered "islands" if they are completely 
     * surround by water in all 8-directions. Should there be a direction (ie. 
     * left corner) where a certain piece of land is connected to another 
     * landmass, this should be considered as one island. A better example 
     * would be if there were two landmasses connected by one cell. Although 
     * seemingly two islands, after further inspection it should be realized 
     * this is one single island. Only if this connection were to be removed 
     * (height of water increased) should these two landmasses be considered 
     * two separate islands.
     * 
     * @param height of the water
     * @return int, representing the total number of islands
     */
    public int numOfIslands(double height) {
       

     double[][] terrain = {
                     {1.0, 2.0, 1.5, 1.2},
                     {2.5, 1.2, 3.0, 0.8},
                     {0.8, 1.9, 2.1, 1.5},
                     {1.0, 2.0, 1.5, 1.2}
        };
          int totalIslands = 0;
                
         int[][] roadmap =  {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        int numberofRows = terrain.length;
        int numberofColumns = terrain[0].length;
        boolean [][] floodedarea = floodedRegionsIn(height);
              
                 for (int row = 0; row < numberofRows; row++) {
                     for (int col = 0; col < numberofColumns; col++) {
                         if (!floodedarea[row][col] && terrain[row][col]== 0) {
                             totalIslands++;
                             
                             Queue<int[]> queue = new LinkedList<>();
                             queue.offer(new int[]{row,col});
                             terrain[row][col]= totalIslands;

                             while(!queue.isEmpty()) {
                                int[] current = queue.poll();
                                int r = current[0];
                                int c = current[1];
                                
                                for (int[] direct: roadmap) {
                                    int newRow = r+ direct[0];
                                    int newColumn = c+ direct[1];

                                    if (newRow >= 0 && newRow < numberofRows && newColumn >= 0 && newColumn < numberofColumns && !floodedarea[newRow][newColumn] && terrain[newRow][newColumn] == 0) {
                                    queue.offer(new int[]{newRow, newColumn});
                                    terrain[newRow][newColumn] = totalIslands;
                                }
                                }
                                }
                                }
                                }
                                }
              Set<Double> numberOfIslands = new HashSet<>();
                 for (int row=0; row <numberofRows; row++) {
                        for (int column = 0; column < numberofColumns; column++)
                            if(!floodedarea[row][column]) {
                                numberOfIslands.add(terrain[row][column]);
                            }
                        }
            return numberOfIslands.size();
                } 


             
            }     

