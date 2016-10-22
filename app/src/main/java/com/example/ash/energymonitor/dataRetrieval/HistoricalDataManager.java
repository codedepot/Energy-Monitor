//import com.sun.org.apache.xpath.internal.operations.String;
package com.example.ash.energymonitor.dataRetrieval;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.lang.Float;
import java.lang.Long;
import java.lang.String;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.io.Serializable;

public class HistoricalDataManager implements Runnable{

    //URL variables stuff
    public static final String API_KEY = "&apikey=8a01cecbe2788824d40f2e52aafd307c";
    public static final String JSON_URL = "http://localhost:8080/emoncms/feed/data.json?";
    public static final String NODE_ID = "id=", START = "&start=", END = "&end=", DP = "&dp=";
    private static final long serialVersionUID = 584L;

    //number of nodes
    public static int numNodes = 29;

    public static int startNode = 1;


    //Time length variables in milliseconds
    public static final Long HOUR_LENGTH = new Long(3600000), DAY_LENGTH = new Long(86400000), WEEK_LENGTH = new Long(604800000), MONTH_LENGTH = new Long(2592000000L), YEAR_LENGTH = new Long(31536000000L);


    //Contains HistoricalNodeData objects for every node .
    public Hashtable<Integer, HistoricalNodeData> historicalData;

    //Character Deviders
    public static final char HISTOGRAM_DIVIDER = '(', NODE_DIVIDER = ')';




    public HistoricalDataManager(){
//    this.historicalData = new Hashtable<Integer, HistoricalNodeData>();
//    HistoricalNodeData historicalNodeData = new HistoricalNodeData(2);
//    //going to start at 2
//    for(int i = startNode; i<= numNodes; i++){
//      this.historicalData.put(i,new HistoricalNodeData(i));
//    }
    }

    /**This is the constructor for this class. It tokenizes the input
     * and initializes the library variable.
     * @param fileTextInput read from a file and to be tokenized.
     */
    public HistoricalDataManager(String fileTextInput){
        this.historicalData = new Hashtable<Integer, HistoricalNodeData>();
        StringTokenizer nodeTokens = new StringTokenizer(fileTextInput, "" + NODE_DIVIDER);
        String temp ="";
        boolean runLoop = true;
        for(int i =startNode; i<= numNodes &&runLoop; i++){
            try {
                temp = nodeTokens.nextToken();
                this.historicalData.put(i, new HistoricalNodeData(i, temp));
            }catch(Exception e){
                runLoop = false;
            }
        }


    }

    public static String getAllNodesHistory(Long timeNow){
        String output ="" + NODE_DIVIDER;
        for(int i = startNode; i<= numNodes; i++){
            //this.historicalData.put(i,new HistoricalNodeData(i));
            output += HistoricalDataManager.getAllHistoricalData(i, timeNow) + NODE_DIVIDER;
        }
        return output;
    }


    public void run(){
        try{
            while(true){
                //System.out.println("Awake");
                String hPath = "C:/Energy Monitoring Data/Dropbox/Feed Data/thequeue.dat";

                //Writing to file
                long time = System.currentTimeMillis();

                String history = HistoricalDataManager.getAllNodesHistory(time);
                //System.out.println("Time Difference " + (System.currentTimeMillis() - time));
                writeToFile(hPath, history);
                Thread.sleep(500);
            }
        }catch(Exception e){
        }
    }

    /** This function reads the text from a URL link.
     * Used to get data from the server.
     * @param url web page (json script used) link
     * @return the text from the url
     */
    public static String readFromURL(String url){
        String result = null;

        try{
            URL connection  = new URL(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.openStream()));
            String line;
            result = "";
            while((line = reader.readLine())!=null){
                result += line + "\n";

            }
        }catch(Exception e){
        }
        return result;

    }

    public static String readFromFile(String path){
        String output = "NA";
        File file = new File( path);
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String tempLine = bufferedReader.readLine();
            while(tempLine!= null){

                output =tempLine;
                tempLine = bufferedReader.readLine();
            }
        }catch(Exception e){

        }
        return output;
    }


    /**gets all the historical data needed for a node.
     * there is a custom precision for every historical view
     * @param nodeId the node id as an int
     * @param timeNow the time now that will be used
     * @return
     */
    public static String getAllHistoricalData(int nodeId, Long timeNow){
        String output = "" + HISTOGRAM_DIVIDER;
        //output += parametersToString(id, );

        output += HistoricalDataManager.parametersToString("" + nodeId, timeNow - HOUR_LENGTH, timeNow, 60) + HISTOGRAM_DIVIDER;
        output += HistoricalDataManager.parametersToString("" + nodeId, timeNow - DAY_LENGTH, timeNow, 96) + HISTOGRAM_DIVIDER;
        output += HistoricalDataManager.parametersToString("" + nodeId, timeNow - WEEK_LENGTH, timeNow, 84) + HISTOGRAM_DIVIDER;
        output += HistoricalDataManager.parametersToString("" + nodeId, timeNow - MONTH_LENGTH, timeNow, 90) + HISTOGRAM_DIVIDER;
        output += HistoricalDataManager.parametersToString("" + nodeId, timeNow - YEAR_LENGTH, timeNow, 100) + HISTOGRAM_DIVIDER;
        return output;
    }

    public static String parametersToString(String id, Long start, Long end, int dp){
        String url = JSON_URL + NODE_ID + id + START + start + END + end + DP + dp +API_KEY;
        //System.out.println(url);
        String input = readFromURL(url);
        String output = input.trim();

        return output;
    }

    /** This function sorts a string of readings and returns
     *  it in the same format.
     * @param data server data which is made of numbers and these characters: [,]
     * @return sorted data in the same format
     */
    public static String sortData(String data){
        StringTokenizer tokenizer = new StringTokenizer(data, "[],");

        Hashtable<Long, Float> tempTable = new Hashtable<Long, Float>();

        long[] timeArray = new long[tokenizer.countTokens()/2];

        String tempTimeS ="";
        Long tempTime = new Long(0);
        //String tempValue = "";
        Float tempValue = new Float(0);
        int counter = 0;
        while(tokenizer.hasMoreTokens()){
            try{
                tempTimeS = tokenizer.nextToken();
                tempTime = Long.parseLong(tempTimeS);
                tempValue = Float.parseFloat(tokenizer.nextToken());
                timeArray[counter] = tempTime;
                tempTable.put(tempTime, tempValue);
                counter++;
            }catch(Exception e){
                System.out.println("An entry didnt work" + e);
            }
            //System.out.println("This is the time " + tempTimeS);

            //System.out.println("This is the time " + tempTimeS + ". And this is the value: " + tempValue);
        }
        Arrays.sort( timeArray);
        String output = "[[";
        int i = 0;

        for(; i<timeArray.length -1 ;i++){
            output += timeArray[i] + "," + tempTable.get(timeArray[i]) + "],[";
        }

        output += timeArray[i ] + "," + tempTable.get(timeArray[i]);
        return output;
    }


    /**This is a class that represents the past data for one node.
     * There is different sets of data for different range lengths.
     *
     */
    public class HistoricalNodeData{

        public int nodeId;
        public Hashtable<Long, Float> lastHour, lastDay, lastWeek, lastMonth, lastYear;

        public Long[] lastHourTimes, lastDayTimes, LastWeekTimes, lastMonthTimes, lastYearTimes;

        public float[] lastHourValues, lastDayValues, lastWeekValues, lastMonthValues, lastYearValues;

        public static final int ID_HOUR =0, ID_DAY=1, ID_WEEK =2, ID_MONTH=3, ID_YEAR=4;

        public long[][] times = new long[5][];
        public float[][] values = new float[5][];

        public HistoricalNodeData(int nodeId){
            this.lastHour = new Hashtable<Long, Float>();
            this.nodeId = nodeId;
            try{
                Long timeNow = System.currentTimeMillis();
                this.lastHour = this.urlToHashtable("" + nodeId, timeNow - HOUR_LENGTH, timeNow, 60);
                this.lastDay = this.urlToHashtable("" + nodeId, timeNow - DAY_LENGTH, timeNow, 96);
                this.lastWeek = this.urlToHashtable("" + nodeId, timeNow - WEEK_LENGTH, timeNow, 84);
                this.lastMonth = this.urlToHashtable("" + nodeId, timeNow - MONTH_LENGTH, timeNow, 90);
                this.lastYear = this.urlToHashtable("" + nodeId, timeNow - YEAR_LENGTH, timeNow, 100);


                //String hourStringData = readFromURL()
            }catch(Exception e){
                System.out.println("Network Error for node " + nodeId + "ERROR STUFF: " + e);
            }
        }


        public HistoricalNodeData(String input){
            StringTokenizer tokenizer = new StringTokenizer(input,"" + HISTOGRAM_DIVIDER);
            String temp = tokenizer.nextToken();

            this.lastHour = this.stringToHashtable( temp);

            this.lastDay = this.stringToHashtable(  tokenizer.nextToken());
            this.lastWeek = this.stringToHashtable(   tokenizer.nextToken());
            this.lastMonth = this.stringToHashtable(   tokenizer.nextToken());

            this.lastYear = this.stringToHashtable( tokenizer.nextToken());
            //System.out.println(temp);
        }

        public HistoricalNodeData(int nodeId, String input){
            StringTokenizer tokenizer = new StringTokenizer(input,"" + HISTOGRAM_DIVIDER);
            this.nodeId = nodeId;
            int counterOuter = 0;
            while(tokenizer.hasMoreTokens()) {

                StringTokenizer innerTokenizer = new StringTokenizer(tokenizer.nextToken(), "[],");
                int numDataPoints = innerTokenizer.countTokens()/2;
                this.times[counterOuter] = new long[numDataPoints];
                this.values[counterOuter] = new float[numDataPoints];
                String tempTimeS = "";
                Long tempTime = new Long(0);
                Float tempValue = new Float(0);
                int counterInner = 0;
                while (innerTokenizer.hasMoreTokens()) {
                    try {
                        //tempTimeS = tokenizer.nextToken();
                        this.times[counterOuter][counterInner] =  Long.parseLong(innerTokenizer.nextToken());
                        //tempTime = Long.parseLong(tempTimeS);
                        this.values[counterOuter][counterInner] =   Float.parseFloat(innerTokenizer.nextToken());
                        //tempValue = Float.parseFloat(tokenizer.nextToken());


                    } catch (Exception e) {
                        System.out.println("An entry didnt work" + e);
                    }
                    counterInner++;
                }
                counterOuter++;
            }

        }


        public Hashtable<Long, Float> stringToHashtable( String input){
            Hashtable<Long, Float> output = new Hashtable<Long, Float>();
            StringTokenizer tokenizer = new StringTokenizer(input, "[],");
            String tempTimeS ="";
            Long tempTime = new Long(0);
            //String tempValue = "";
            Float tempValue = new Float(0);
            while(tokenizer.hasMoreTokens()){
                try{
                    tempTimeS = tokenizer.nextToken();
                    tempTime = Long.parseLong(tempTimeS);
                    tempValue = Float.parseFloat(tokenizer.nextToken());
                    output.put(tempTime, tempValue);
                }catch(Exception e){
                    System.out.println("An entry didnt work" + e);
                }
                //System.out.println("This is the time " + tempTimeS);

                //System.out.println("This is the time " + tempTimeS + ". And this is the value: " + tempValue);
            }
            return output;
        }



        public Hashtable<Long, Float> urlToHashtable(String id, Long start, Long end, int dp){
            String url = JSON_URL + NODE_ID + id + START + start + END + end + DP + dp +API_KEY;
            //System.out.println(url);
            String input = readFromURL(url);
            input = input.trim();
            //System.out.println(input);
            Hashtable<Long, Float> output = new Hashtable<Long, Float>();
            StringTokenizer tokenizer = new StringTokenizer(input, "[],");
            String tempTimeS ="";
            Long tempTime = new Long(0);
            Float tempValue = new Float(0);
            //assumes even number of tokens, whats going on
            //System.out.println("Num tokens: " + tokenizer.countTokens());
            //System.out.println("This is how many tokens there are: " +tokenizer.countTokens());
            while(tokenizer.hasMoreTokens()){

                tempTimeS = tokenizer.nextToken();
                try{
                    tempTime = Long.parseLong(tempTimeS);
                }catch(Exception e){
                    System.out.println("a key didnt work" + e);
                }
                //System.out.println("This is the time " + tempTimeS);
                tempValue = Float.parseFloat(tokenizer.nextToken());
                //System.out.println("This is the time " + tempTimeS + ". And this is the value: " + tempValue);
                output.put(tempTime, tempValue);

            }
            //System.out.println("Size of this hashtable for id: " + id +" and the size: " + output.size());
            return output;
        }

    }


    /**A method to write to a local file.
     * Used mainly on the server side
     * @param fileName The name of the file to write to
     * @param input What to write to file
     * @return
     */
    public static boolean writeToFile(String fileName, String input) {
        boolean didWrite = false;

        try {
            File out = new File(fileName);
            FileOutputStream fos = new FileOutputStream(out);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(input);

            writer.close();
            fos.close();
            //out.close();
            didWrite = true;


        } catch (Exception e) {
            didWrite = false;
        }

        return didWrite;
    }

    public static void main(String[] args){
        String hPath = "C:/Energy Monitoring Data/Dropbox/Feed Data/thequeue.dat";

        //Writing to file
        long time = System.currentTimeMillis();
        //System.out.println("Time Difference " + (System.currentTimeMillis() - time));
        String history = HistoricalDataManager.getAllNodesHistory(time);

        writeToFile(hPath, history);
        //System.out.println(test);
        //System.out.println( "\n" + "Time Next Difference " + (System.currentTimeMillis() - time) + "Now will read");


        //String reader = readFromFile(hPath);
        // HistoricalDataManager historicalDataManager = new HistoricalDataManager(reader);

//    try{
//      String path = "C:/Energy Monitoring Data/Dropbox/Feed Data/thequeue.dat";
        //writing




//      FileOutputStream fout = new FileOutputStream(path);
//      ObjectOutputStream oos = new ObjectOutputStream(fout);
//      oos.writeObject(historicalDataManager);
//      oos.close();
//      System.out.println("Time Next Difference " + (System.currentTimeMillis() - time) + "Now will read");
//
//        //reading from file
//      FileInputStream fin = new FileInputStream(path);
//      ObjectInputStream ois = new ObjectInputStream(fin);


        //System.out.println("Size of history hashtable: " + historicalDataManager.historicalData.size());


        //Enumeration<HistoricalNodeData> enumeration = historicalDataManager.historicalData.elements();

//        //HistoricalNodeData historicalNodeData = enumeration.nextElement();
//        HistoricalNodeData historicalNodeData = historicalDataManager.historicalData.get(2);
//        //System.out.println("The node we picked is: " + historicalNodeData.nodeId);
//        Enumeration<Float> enumeration1 = historicalNodeData.lastHour.elements();
//        Enumeration<Long> enumeration2 = historicalNodeData.lastHour.keys();
//        //System.out.println("Size of Node hashtable" + historicalNodeData.lastHour.size());
//        while(enumeration1.hasMoreElements()){
//            System.out.println("Key: " + enumeration2.nextElement() + ". Value: " + enumeration1.nextElement());
//        }
        //System.out.println("Answer: " + readObject.historicalData.containsKey(5));

//    }catch(Exception e){
//      System.out.println(e);
//    }
    }




}