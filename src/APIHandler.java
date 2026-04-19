import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIHandler {
    private static String API_KEY = "4f7140f752d4c582fdf81fa0";
    private static String[] currencyChoices = {"USD","NZD","AUD","EUR","JPY"};

    public static Graph setRates(){
        int size = currencyChoices.length;
        Graph graph = new Graph(size);
        graph.setCurrencyList(currencyChoices);
        try {
            // adds api parsed edges to the matrix
            for(int i = 0; i < size; i++){
                for(int j = 0; j < size; j++){
                    // sets the edge as 0 if i and j are same (as USD -> USD would equal no change)
                    if(i == j){
                        graph.addEdge(i, j, 0);
                    }
                    // else calls apifetch method, using parsed exchange data to add an edge 
                    else{
                        double rate = fetchAPI(currencyChoices[i], currencyChoices[j]);
                        graph.addEdge(i, j, -Math.log10(rate));
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        
        return graph;
    }

    public static double fetchAPI(String from, String to) {
        String urlString = "https://v6.exchangerate-api.com/v6/"+API_KEY+"/pair/"+from+"/"+to;
        try{
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // buffered reader to read the api output
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;
            StringBuilder content = new StringBuilder();

            // reads line and then appends using the string builder instance
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            conn.disconnect();

            String response = content.toString();
            // search key to find the needed data
            String searchKey = "\"conversion_rate\":";
            int index = response.indexOf(searchKey);
            if (index != -1){
                // parses the string to double input
                int start = index + searchKey.length();
                int end = response.indexOf(",",start);
                if(end == -1) end = response.indexOf("}",start);
                String rateStr = response.substring(start, end).trim();
                // returns a parsed value extracted from the 
                return Double.parseDouble(rateStr);
            }
        }
        catch(Exception e){}
        return 1;
    }
}
