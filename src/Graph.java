import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Graph {
   private int numVertices;
   private double[][] adjMatrix;
   private String[] currencyList;
   private double bestRate;

   public Graph(int numVertices) {
      this.numVertices = numVertices;
      this.adjMatrix = new double[numVertices][numVertices];

      for(int i = 0; i < numVertices; i++) {
         for(int j = 0; j < numVertices; j++) {
            if (i == j) {
               this.adjMatrix[i][j] = 0;
            } else {
               this.adjMatrix[i][j] = Double.POSITIVE_INFINITY;
            }
         }
      }
   }

   public int getSize() {
      return this.numVertices;
   }

   public double[][] getAdjacencyMatrix() {
      return this.adjMatrix;
   }

   public void setCurrencyList(String[] currencyList) {
      this.currencyList = currencyList;
   }

   public String[] getCurrencyList() {
      return currencyList;
   }

   public double getBestRate(){
      return this.bestRate;
   }

   public void addEdge(int from, int to, double weight) {
      this.adjMatrix[from][to] = weight;
   }

   // BellmanFord algorithm combined with arbitrage cycle detection
   public List<String> findArbitrageCycle() {
      // dist array to store shortest paths cost
      double[] dist = new double[numVertices];
      // stores predecessors to later construct a path using it
      int[] pred = new int[numVertices];
      // fill arrays
      Arrays.fill(dist, Double.POSITIVE_INFINITY);
      Arrays.fill(pred, -1);
      dist[0] = 0;
      
      // relax edges (n-1) to find the shortest path
      for (int k = 0; k < numVertices - 1; k++) {
         for (int i = 0; i < numVertices; i++) {
            // skips if a vetrice is unreachable
            if (dist[i] == Double.POSITIVE_INFINITY) continue; 
            for (int j = 0; j < numVertices; j++) {
               if (adjMatrix[i][j] != Double.POSITIVE_INFINITY) { 
                  // checks if path from i to j is shorter than the current path
                  if (dist[i] + adjMatrix[i][j] < dist[j]) {
                        dist[j] = dist[i] + adjMatrix[i][j]; 
                        pred[j] = i; // tracks the predecessor
                  }
               }
            }
         }
      }
      
      // Check for negative cycle, if there is a chance to improve distance after n-1 iterations
      for (int i = 0; i < numVertices; i++) {
         if (dist[i] == Double.POSITIVE_INFINITY) continue;
         for (int j = 0; j < numVertices; j++) {
               if (adjMatrix[i][j] != Double.POSITIVE_INFINITY) {
                  if (dist[i] + adjMatrix[i][j] < dist[j]) {
                     // update predecessor to include cycle edge
                     pred[j] = i;
                     // Extract and return the arbitrage cycle
                     return extractCycle(j, pred);
                  }
               }
         }
      }
      // returns null if no arbitrage is found
      return null;
   }

   private List<String> extractCycle(int vertex, int[] pred) {
      // walks back through the predecessors
      int current = vertex;
      for (int i = 0; i < numVertices; i++) {
         current = pred[current];
      }
      
      List<String> cycle = new ArrayList<>();
      int start = current;
      do {
         cycle.add(currencyList[current]); // add the current currency to the cycle
         current = pred[current]; // move to the predecessor node
      } while (current != start);
      cycle.add(currencyList[start]); // finish the cycle with the loop back currency
      
      Collections.reverse(cycle); // reverse cycle
      return cycle; // return
   }


   public List<String> bestConversionRate(String from, String to){
      // staring index
      int fromIndex = getCurrencyIndex(from);
      // next index
      int toIndex = getCurrencyIndex(to);

      if(fromIndex == -1 || toIndex == -1){
         System.out.println("Invalid Currency.");
         return null;
      }

      double[] dist = new double[numVertices];
      int[] pred = new int[numVertices];
      Arrays.fill(dist, Double.POSITIVE_INFINITY);
      Arrays.fill(pred, -1);
      dist[fromIndex] = 0;
      
      // relax edges (n-1) to find the shortest path
      for (int k = 0; k < numVertices - 1; k++) {
         for (int i = 0; i < numVertices; i++) {
            if (dist[i] == Double.POSITIVE_INFINITY) continue;
            for (int j = 0; j < numVertices; j++) {
               if (adjMatrix[i][j] != Double.POSITIVE_INFINITY) {
                  // checks if path from i to j is shorter than the current path
                  if (dist[i] + adjMatrix[i][j] < dist[j]) {
                        dist[j] = dist[i] + adjMatrix[i][j];
                        pred[j] = i; // tracks the predecessor
                  }
               }
            }
         }
      }

      // convert log transformation to rate
      this.bestRate = Math.pow(10, -dist[toIndex]);

      // loops through paths in order
      List<String> path = new ArrayList<>();
      int current = toIndex;
      while(current != -1){
         path.add(0, currencyList[current]);
         current = pred[current];
      }
      return path;
   }

   // returns currency index
   private int getCurrencyIndex(String x){
      for(int i = 0; i < currencyList.length; i++){
         if(currencyList[i].equals(x)) return i;
      }
      return -1;
   }
}
