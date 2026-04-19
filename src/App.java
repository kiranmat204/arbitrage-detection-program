import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class App {

    public static void main(String[] args) throws Exception {
        App app = new App();

        System.out.println("---------------------------------------------------------------");
        System.out.println("------------------Test One - Arbitrage Cycle-------------------");
        System.out.println("---------------------------------------------------------------\n");
        // test one - arbitrage cycle
        String text_file = "../test/test1.txt";
        Graph graph = app.fileParser(text_file);
        if (graph != null){
            double matrix[][] = graph.getAdjacencyMatrix();
            app.run(graph);
            // System.out.println("After BellmanFord:");
            // app.printMatrix(graph, matrix);
        }

        System.out.println("");
        System.out.println("---------------------------------------------------------------");
        System.out.println("------------------Test Two nonArbitrage Cycle------------------");
        System.out.println("---------------------------------------------------------------\n");
        // test two - no arbitrage cycle
        String test2 = "../test/test2.txt";
        Graph graph2 = app.fileParser(test2);
        if (graph2 != null){
            double matrix2[][] = graph2.getAdjacencyMatrix();
            app.run(graph2);
            // System.out.println("After BellmanFord:");
            // app.printMatrix(graph2, matrix2);
        }

        System.out.println("");
        System.out.println("-----------------------------------------------------------------");
        System.out.println("------------------Test Three - Invalid Currency------------------");
        System.out.println("-----------------------------------------------------------------\n");
        // test three - invalid currency count
        String test3 = "../test/test3.txt";
        Graph graph3 = app.fileParser(test3);
        if (graph3 != null){
            double matrix3[][] = graph3.getAdjacencyMatrix();
            app.run(graph3);
            // System.out.println("After BellmanFord:");
            // app.printMatrix(graph3, matrix3);
        }

        System.out.println("");
        System.out.println("---------------------------------------------------------------");
        System.out.println("------------------Test Four - Missing Exchange-----------------");
        System.out.println("---------------------------------------------------------------\n");
        // test four - missing exchange
        String test4 = "../test/test4.txt";
        Graph graph4 = app.fileParser(test4);
        if (graph4 != null){
            double matrix4[][] = graph4.getAdjacencyMatrix();
            app.run(graph4);
            // System.out.println("After BellmanFord:");
            // app.printMatrix(graph4, matrix4);
        }

        System.out.println("");
        System.out.println("---------------------------------------------------------------");
        System.out.println("------------------Test Five - Nonnumeric Entry-----------------");
        System.out.println("---------------------------------------------------------------\n");
        String test5 = "../test/test5.txt";
        Graph graph5 = app.fileParser(test5);
        if (graph5 != null){
            double matrix5[][] = graph5.getAdjacencyMatrix();
            app.run(graph5);
            // System.out.println("After BellmanFord:");
            // app.printMatrix(graph4, matrix5);
        }

        System.out.println("");
        System.out.println("---------------------------------------------------------------");
        System.out.println("------------------Test Six - API Arbitrage Test----------------");
        System.out.println("---------------------------------------------------------------\n");
        // test six - api arbitrage
        Graph apiGraph = APIHandler.setRates();
        if(apiGraph != null){
            double apimatrix[][] = apiGraph.getAdjacencyMatrix();
            app.run(apiGraph);
            // System.out.println("After BellmanFord API:");
            // app.printMatrix(graph, apimatrix);
        }
    }

    private void run(Graph graph){
        if (graph != null){
            // retrieves the negative cycle
            List<String> cycle = graph.findArbitrageCycle();
            
            //if there is an arbitrage
            if (cycle != null) {
                System.out.print("Arbitrage Exists: ");
                // prints the arbitrage cycle in a neat manner
                for (int i = 0; i < cycle.size(); i++) {
                    System.out.print(cycle.get(i));
                    if (i < cycle.size() - 1) System.out.print(" -> ");
                }
                System.out.println();

                // variables
                double profit = 1.0;
                String[] currencies = graph.getCurrencyList();
                double[][] adj = graph.getAdjacencyMatrix();

                // loop helps calculate profit
                for (int i = 0; i < cycle.size() - 1; i++) {
                    // sets int from and to
                    int from = Arrays.asList(currencies).indexOf(cycle.get(i));
                    int to = Arrays.asList(currencies).indexOf(cycle.get(i + 1));

                    // retrieves the rate 
                    double rate = Math.pow(10, -adj[from][to]);
                    // prints out each rate for debugging
                    System.out.println(cycle.get(i) + " -> " + cycle.get(i+1) + ": " + rate);
                    // profit non logarithmic exchange rates multiplied
                    profit *= Math.pow(10, -adj[from][to]);
                }
                // converts profit to a percentage
                double profitPct = (profit - 1) * 100;
                // prints profit
                System.out.printf("Profit: %.4f%%\n(Note: if percentage doesn't appear, the arbitrage still exists just a miniscule number)\n", profitPct);
            } 
            //in case of no arbitrage
            else {
                System.out.println("No arbitrage opportunity found.");
                String[] currencies = graph.getCurrencyList();
                // sets the first node in the array as start
                String from = currencies[0];
                // sets the last node in the array as the end point
                String to = currencies[currencies.length-1];
                // retrieves a path
                List<String> path = graph.bestConversionRate(from, to);

                //prints path if it exists
                if(path != null){
                    System.out.println("Best conversion rate from "+from+" to "+to+": "+graph.getBestRate());
                    System.out.print("Best path: ");
                    System.out.println(String.join(" -> ", path) + ".");
                }
            }
        }
    }

    // print matrix method for debugging
    private void printMatrix(Graph graph, double[][] matrix){
        for(int i = 0; i < graph.getSize(); i++){
            for(int j = 0; j < graph.getSize(); j++){
                System.out.print(matrix[i][j]);
                if(j != graph.getSize()-1){
                    System.out.print(", ");
                }
            }
            System.out.println();
        }
    }

    // parses file and returns a graph
    private Graph fileParser(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));

        String[] firstline = br.readLine().split(",");
        int currencyCount = Integer.parseInt(firstline[0].trim());
        String[] currencyList = new String[currencyCount];
        try {

            for(int i = 0; i < currencyCount; i++){
                if(firstline[i+1].trim().isBlank()){
                    System.out.println("Invalid Input. Currency count does not match the number of nodes provided.");
                    return null;
                }
                currencyList[i] = firstline[i+1].trim();
            }
        
        } catch (Exception e) {
            System.out.println("Invalid Input. Currency count does not match the number of nodes provided.");
            return null;
        }
        

        Graph graph = new Graph(currencyCount);
        graph.setCurrencyList(currencyList);
        // goes through the matirx adding edges
        for(int i = 0; i < currencyCount; i++){
            String[] parts = br.readLine().trim().split("\\s+");
            if (parts.length < currencyCount){
                System.out.println("Incomplete row "+currencyList[i]+" for currency. Each row must have exactly "+currencyCount+" values.");
                return null;
            }
            for(int j = 0; j < currencyCount; j++){
                double exchangeRate = -1;
                try {
                    exchangeRate = Double.parseDouble(parts[j]);
                } catch (Exception e) {
                    System.out.println("Invalid numeric value in exchange matrix (Row: "+i+", Col: "+j+")");
                    return null;
                }
                
                if (exchangeRate < 0){
                    System.out.println("Invalid exchange rate detected. Rates must be positive numbers.");
                    return null;
                }
                graph.addEdge(i, j, -Math.log10(exchangeRate));
            }
        }
        br.close();
        return graph;
    }
}

