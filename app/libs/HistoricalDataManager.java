//import com.sun.org.apache.xpath.internal.operations.String;
package com.example.ash.energymonitor.dataRetrieval;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.Long;
import java.lang.String;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.io.Serializable;

public class HistoricalDataManager implements Runnable, Serializable{

    public static final String API_KEY = "&apikey=8a01cecbe2788824d40f2e52aafd307c";

    public static final String JSON_URL = "http://localhost:8080/emoncms/feed/data.json?";

    public static final String NODE_ID = "id=", START = "&start=", END = "&end=", DP = "&dp=";

    //number of nodes
    public static int numNodes = 29;

    public static final Long HOUR_LENGTH = new Long(3600000), DAY_LENGTH = new Long(86400000), WEEK_LENGTH = new Long(604800000), MONTH_LENGTH = new Long(2592000000L), YEAR_LENGTH = new Long(31536000000L);


    //Contains HistoricalNodeData objects for every node.
    public Hashtable<Integer, HistoricalNodeData> historicalData;

    public HistoricalDataManager(){
        this.historicalData = new Hashtable<Integer, HistoricalNodeData>();
        HistoricalNodeData historicalNodeData = new HistoricalNodeData(2);
        //going to start at 2
        for(int i = 2; i<= numNodes; i++){
            this.historicalData.put(i,new HistoricalNodeData(i));
        }
    }

    public void getNodeData(){
        for(int i =0; i<numNodes; i++){

        }

    }

    public void writeToFile(){
    }

    public void run(){

    }

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



    public class HistoricalNodeData implements Serializable{

        int nodeId;
        public Hashtable<Long, String> lastHour, lastDay, lastWeek, lastMonth, lastYear;

        public HistoricalNodeData(int nodeId){
            this.lastHour = new Hashtable<Long, String>();
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

        public Hashtable<Long, String> urlToHashtable(String id, Long start, Long end, int dp){
            String url = JSON_URL + NODE_ID + id + START + start + END + end + DP + dp +API_KEY;
            //System.out.println(url);
            String input = readFromURL(url);
            input = input.trim();
            //System.out.println(input);
            Hashtable<Long, String> output = new Hashtable<Long, String>();
            StringTokenizer tokenizer = new StringTokenizer(input, "[],");
            String tempTimeS ="";
            Long tempTime = new Long(0);
            String tempValue = "";
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
                tempValue = tokenizer.nextToken();
                //System.out.println("This is the time " + tempTimeS + ". And this is the value: " + tempValue);
                output.put(tempTime, tempValue);

            }
            //System.out.println("Size of this hashtable for id: " + id +" and the size: " + output.size());
            return output;
        }

    }



    public static void main(String[] args){
        long time = System.currentTimeMillis();

        HistoricalDataManager historicalDataManager = new HistoricalDataManager();
        System.out.println("Time Difference " + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        //File file = new File();
        try{
            //writing
            FileOutputStream fout = new FileOutputStream("../Feed Data/thequeue.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(historicalDataManager);
            oos.close();
            System.out.println("Time Next Difference " + (System.currentTimeMillis() - time) + "Now will read");

            //reading from file
            FileInputStream fin = new FileInputStream("../Feed Data/thequeue.dat");
            ObjectInputStream ois = new ObjectInputStream(fin);
            HistoricalDataManager readObject = (HistoricalDataManager)  ois.readObject();
            ois.close();
            System.out.println("Size of history hashtable: " + readObject.historicalData.size());


            Enumeration<HistoricalNodeData> enumeration = readObject.historicalData.elements();

            //HistoricalNodeData historicalNodeData = enumeration.nextElement();
            HistoricalNodeData historicalNodeData = readObject.historicalData.get(2);
            //System.out.println("The node we picked is: " + historicalNodeData.nodeId);
            Enumeration<String> enumeration1 = historicalNodeData.lastHour.elements();
            Enumeration<Long> enumeration2 = historicalNodeData.lastHour.keys();
            //System.out.println("Size of Node hashtable" + historicalNodeData.lastHour.size());
//        while(enumeration1.hasMoreElements()){
//            System.out.println("Key: " + enumeration2.nextElement() + ". Value: " + enumeration1.nextElement());
//        }
            //System.out.println("Answer: " + readObject.historicalData.containsKey(5));

        }catch(Exception e){
            System.out.println(e);
        }
    }




}