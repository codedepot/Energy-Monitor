package com.example.ash.energymonitor.dataRetrieval;

import android.util.Log;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * Created by Mehrdad on 2/10/2015.
 */
public class DataCompiler {
    //A table that represents many nodes of data
    public Hashtable<Integer, LiveData> storedLiveDataTable;





   public DataCompiler(String dataInput){
       storedLiveDataTable = new Hashtable<Integer, LiveData>();
       dataInput = dataInput.trim();
       dataInput = dataInput.substring(1, dataInput.length() -1);
       StringTokenizer tokenizer = new StringTokenizer(dataInput, "},{");
      //tokenizer.nextToken();
       //Log.d("Proto",dataInput );
       //Log.d("Proto",DataCompiler.trimQMarks("\"There is soemthing\"") );

       //Tokenizes all the data and puts it into a hashtable
       while(tokenizer.hasMoreTokens()){
           //System.out.println(tokenizer.nextToken());


           StringTokenizer fieldToken = new StringTokenizer(tokenizer.nextToken(), ":");
           fieldToken.nextToken();
           String tempId = DataCompiler.trimQMarks(fieldToken.nextToken());

           fieldToken = new StringTokenizer(tokenizer.nextToken(), ":");
           fieldToken.nextToken();
           String tempName = DataCompiler.trimQMarks(fieldToken.nextToken());

           fieldToken = new StringTokenizer(tokenizer.nextToken(), ":");
           fieldToken.nextToken();
           String tempUserId = DataCompiler.trimQMarks(fieldToken.nextToken());


           fieldToken = new StringTokenizer(tokenizer.nextToken(), ":");
           fieldToken.nextToken();
           String tempTag = DataCompiler.trimQMarks(fieldToken.nextToken());


           fieldToken = new StringTokenizer(tokenizer.nextToken(), ":");
           fieldToken.nextToken();
           String tempTime = DataCompiler.trimQMarks(fieldToken.nextToken());


           fieldToken = new StringTokenizer(tokenizer.nextToken(), ":");
           fieldToken.nextToken();
           String tempValue = DataCompiler.trimQMarks(fieldToken.nextToken());


           fieldToken = new StringTokenizer(tokenizer.nextToken(), ":");
           fieldToken.nextToken();
           String tempDataType = DataCompiler.trimQMarks(fieldToken.nextToken());


           fieldToken = new StringTokenizer(tokenizer.nextToken(), ":");
           fieldToken.nextToken();
           String tempPublic = DataCompiler.trimQMarks(fieldToken.nextToken());

           fieldToken = new StringTokenizer(tokenizer.nextToken(), ":");
           fieldToken.nextToken();
           String tempSize = DataCompiler.trimQMarks(fieldToken.nextToken());

           fieldToken = new StringTokenizer(tokenizer.nextToken(), ":");
           fieldToken.nextToken();
           String tempEngine = DataCompiler.trimQMarks(fieldToken.nextToken());
            int tempIdInt = Integer.parseInt(tempId);


            //puts the data into a hash table
            this.storedLiveDataTable.put(Integer.parseInt(tempId),new LiveData(tempId,tempName, tempUserId, tempTag, tempTime, tempValue, tempDataType, tempPublic, tempSize,tempEngine));
          // Log.d("Proto",tempId );




           //int idTemp = Integer.parseInt(tempIdString.substring(tempIdString.lastIndexOf("\"",tempIdString.length() -2) +1,tempIdString.lastIndexOf("\"")));
//            String tempName = DataCompiler.getLastQMarks(tokenizer.nextToken());
//            String tempUserId = DataCompiler.getLastQMarks(tokenizer.nextToken());
//            String tempTag = DataCompiler.getLastQMarks(tokenizer.nextToken());
//           String tempSub = tokenizer.nextToken();
//           String tempTime =tempSub.substring(tempSub.indexOf(":"),tempSub.length());
//           tempSub = tokenizer.nextToken();
//           String tempValue =tempSub.substring(tempSub.indexOf(":"),tempSub.length());
//           Log.d("Proto",tempIdString );
      }
   }

    /** This function is used to get all the names of
     *  every feed. Its used later for spinners
     * @return An array of all the feeds
     */
   public String[] getFeedNames(){
       String[] output = new String[this.storedLiveDataTable.size()];
//       if(minNodeValue!=Integer.MAX_VALUE && maxNodeValue != Integer.MIN_VALUE){
//           int j = 0;
//           for(int i = minNodeValue; i<=maxNodeValue; i++){
//               output[j] =this.storedLiveDataTable.get(i).value;
//               j++;
//           }
//       }else {
//            Enumeration<Integer> keys = this.storedLiveDataTable.keys();
//
//
//
//           Log.d("Proto", "Max is: " + maxNodeValue + " Min is:" + minNodeValue);
//            int[] keyArrayTemp = new int[this.storedLiveDataTable.size()];
//            for(int i = 0; i<keyArrayTemp.length; i++){
//                keyArrayTemp[i] = keys.nextElement();
//            }
//            Arrays.sort(keyArrayTemp);
//            for(int i= 0; i<keyArrayTemp.length;i++){
//                output[i] =  this.storedLiveDataTable.get(keyArrayTemp[i]).name;
//            }

            LiveData tempData;
            Enumeration enumeration = this.storedLiveDataTable.elements();
           for (int i = 0; i < output.length; i++) {
               tempData = (LiveData) enumeration.nextElement();
               output[i] = tempData.name;

       }


       //Log.d("Proto", "" + Arrays.toString(output));
       return output;
   }


    public int[] getFeedIds(){
        int[] output = new int[this.storedLiveDataTable.size()];
        Enumeration<Integer> keys = this.storedLiveDataTable.keys();
        for(int i = 0; i<output.length;i++){
            output[i] = keys.nextElement();
        }
        return output;
    }



    /**Searches the global variables hashtable which contains
     * all the LiveData classes.
     * @param name The name variable is what it searches the database by
     * @return returns the value variable as a String or "NA" if nothing is found
     */
    public String getValueFromName(String name){
        String output = "NA";
        Enumeration enumeration = this.storedLiveDataTable.elements();
        String temp = "";
        LiveData tempElement;
        while(enumeration.hasMoreElements()){
            tempElement = (LiveData) enumeration.nextElement();
            if(tempElement.name.equals(name)){
                output = tempElement.value;
                break;
            }
        }
        return output;
    }

    public String getValueFromId(String idVar){
        String output = "NA";
        Enumeration enumeration = this.storedLiveDataTable.elements();
        String temp = "";
        LiveData tempElement;
        while(enumeration.hasMoreElements()){
            tempElement = (LiveData) enumeration.nextElement();
            if(tempElement.idVar.equals(idVar)){
                output = tempElement.value;
                break;
            }
        }
        return output;
    }

    /**Searches the global variables hashtable which contains
     * all the LiveData classes.
     * @param name The name variable is what it searches the database by
     * @return returns the LiveData object or null if nothing is found
     */
    public LiveData getLiveDataFromName(String name){
        LiveData output = null;
        Enumeration enumeration = this.storedLiveDataTable.elements();
        String temp = "";
        LiveData tempElement;
        while(enumeration.hasMoreElements()){
            tempElement = (LiveData) enumeration.nextElement();
            if(tempElement.name.equals(name)){
                output = tempElement;
                break;
            }
        }
        return output;
    }

    /**
     * This method takes the string of words in the second
     * quotations
     * @param inputObject The String to be taken apart
     * @return the second argument
     */
    public static String getLastQMarks(String inputObject){
        //buffering it to avoid pointer behavior
        String input = inputObject;
        return input.substring(input.lastIndexOf("\"",input.length() -2) +1,input.lastIndexOf("\""));
    }

    /**Takes out the question marks from the start and end
     *  of the input
     * @param inputObject string to edit
     * @return edited string
     */
    public static String trimQMarks(String inputObject){
        //buffering it to avoid pointer behavior
        String input = inputObject;
        if((""+input.charAt(0)).equals("\"")){
           input = input.substring(1,input.length());
        }if((""+input.charAt(input.length()-1)).equals("\"")){
            input = input.substring(0,input.length() -1);
        }

        return input;
    }




    public static void main(String[] args){

        DataCompiler dataCompiler = new DataCompiler("[{\"id\":\"1\",\"name\":\"testfeed\",\"userid\":\"1\",\"tag\":\"Node:1\",\"time\":false,\"value\":null,\"datatype\":\"1\",\"public\":\"0\",\"size\":\"44\",\"engine\":\"6\"},{\"id\":\"2\",\"name\":\"Leg1Pow5s\",\"userid\":\"1\",\"tag\":\"Node:6\",\"time\":1423587631,\"value\":\"824\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"6490280\",\"engine\":\"6\"},{\"id\":\"3\",\"name\":\"Leg1volt5s\",\"userid\":\"1\",\"tag\":\"Node:6\",\"time\":1423587631,\"value\":\"12158\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"6490240\",\"engine\":\"6\"},{\"id\":\"4\",\"name\":\"Leg1temp5s\",\"userid\":\"1\",\"tag\":\"Node:6\",\"time\":1423587631,\"value\":\"-12\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"6490188\",\"engine\":\"6\"},{\"id\":\"5\",\"name\":\"Leg2pow5s\",\"userid\":\"1\",\"tag\":\"Node:7\",\"time\":1423587623,\"value\":\"856\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"6490152\",\"engine\":\"6\"},{\"id\":\"6\",\"name\":\"Leg2volt5s\",\"userid\":\"1\",\"tag\":\"Node:7\",\"time\":1423587623,\"value\":\"121.49\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"6490100\",\"engine\":\"6\"},{\"id\":\"7\",\"name\":\"Leg2temp5s\",\"userid\":\"1\",\"tag\":\"Node:7\",\"time\":1423587623,\"value\":\"-8\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"6490036\",\"engine\":\"6\"},{\"id\":\"8\",\"name\":\"dishwasher\",\"userid\":\"1\",\"tag\":\"Node:5\",\"time\":1416241967,\"value\":\"2\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"744636\",\"engine\":\"6\"},{\"id\":\"9\",\"name\":\"fancoil\",\"userid\":\"1\",\"tag\":\"Node:5\",\"time\":1416241967,\"value\":\"0\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"744612\",\"engine\":\"6\"},{\"id\":\"10\",\"name\":\"voltage\",\"userid\":\"1\",\"tag\":\"Node:5\",\"time\":1416241967,\"value\":\"125.66\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"744568\",\"engine\":\"6\"},{\"id\":\"11\",\"name\":\"leg1Pow5sts\",\"userid\":\"1\",\"tag\":\"Node:6\",\"time\":1423587631,\"value\":\"824\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"482076\",\"engine\":\"2\"},{\"id\":\"12\",\"name\":\"ERV\",\"userid\":\"1\",\"tag\":\"Node:11\",\"time\":1423587621,\"value\":\"0\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"2326848\",\"engine\":\"6\"},{\"id\":\"13\",\"name\":\"ERVvolt15s\",\"userid\":\"1\",\"tag\":\"Node:11\",\"time\":1423587621,\"value\":\"122.5\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"2326972\",\"engine\":\"6\"},{\"id\":\"15\",\"name\":\"Microwavevolt20s\",\"userid\":\"1\",\"tag\":\"Node:12\",\"time\":1423587622,\"value\":\"0\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"1868532\",\"engine\":\"6\"},{\"id\":\"27\",\"name\":\"MicrowavePowerSafeplug\",\"userid\":\"1\",\"tag\":\"Node:12\",\"time\":1423587622,\"value\":\"0\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"156396\",\"engine\":\"6\"},{\"id\":\"16\",\"name\":\"CoffeePow20s\",\"userid\":\"1\",\"tag\":\"Node:13\",\"time\":1423079504,\"value\":\"0\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"1868500\",\"engine\":\"6\"},{\"id\":\"17\",\"name\":\"CoffeeVolt20s\",\"userid\":\"1\",\"tag\":\"Node:13\",\"time\":1423079504,\"value\":\"122.75\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"1868484\",\"engine\":\"6\"},{\"id\":\"20\",\"name\":\"HouseAPower10s\",\"userid\":\"1\",\"tag\":\"Node:9\",\"time\":1423587626,\"value\":\"1283\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"161996\",\"engine\":\"6\"},{\"id\":\"19\",\"name\":\"FridgeVolt\",\"userid\":\"1\",\"tag\":\"Node:14\",\"time\":1423587621,\"value\":\"122.5\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"3230044\",\"engine\":\"6\"},{\"id\":\"21\",\"name\":\"HouseAVolt10s\",\"userid\":\"1\",\"tag\":\"Node:9\",\"time\":1423587626,\"value\":\"120.08\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"161996\",\"engine\":\"6\"},{\"id\":\"22\",\"name\":\"HouseAPowLeg210s\",\"userid\":\"1\",\"tag\":\"Node:10\",\"time\":1423587626,\"value\":\"1220\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"162400\",\"engine\":\"6\"},{\"id\":\"23\",\"name\":\"HouseALeg2Volt10s\",\"userid\":\"1\",\"tag\":\"Node:10\",\"time\":1423587626,\"value\":\"120.98\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"162400\",\"engine\":\"6\"},{\"id\":\"26\",\"name\":\"FridgePowerSafeplug\",\"userid\":\"1\",\"tag\":\"Node:14\",\"time\":1423587621,\"value\":\"0\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"156560\",\"engine\":\"6\"},{\"id\":\"24\",\"name\":\"HotWaterPowSafeplug\",\"userid\":\"1\",\"tag\":\"Node:21\",\"time\":1423587624,\"value\":\"3000\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"158712\",\"engine\":\"6\"},{\"id\":\"25\",\"name\":\"HotWaterVoltSafeplug\",\"userid\":\"1\",\"tag\":\"Node:21\",\"time\":1423587624,\"value\":\"242.907\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"158664\",\"engine\":\"6\"},{\"id\":\"28\",\"name\":\"Washing machinePow\",\"userid\":\"1\",\"tag\":\"Node:18\",\"time\":1423587623,\"value\":\"0\",\"datatype\":\"1\",\"public\":\"0\",\"size\":null,\"engine\":\"6\"},{\"id\":\"29\",\"name\":\"washingMVoltage\",\"userid\":\"1\",\"tag\":\"Node:18\",\"time\":1423587623,\"value\":\"122.5\",\"datatype\":\"1\",\"public\":\"0\",\"size\":null,\"engine\":\"6\"}]\n");
        dataCompiler.getFeedNames();
    }
    //a class that represents the values in the live data from one node
    public class LiveData{
        public String idVar, name, userId, tagVar, time, value, dataType, publicVar, size, engine;
        //int idVar,

        public static final String TEST_DATA = "[{\"id\":\"1\",\"name\":\"testfeed\",\"userid\":\"1\",\"tag\":\"Node:1\",\"time\":false,\"value\":null,\"datatype\":\"1\",\"public\":\"0\",\"size\":\"44\",\"engine\":\"6\"},{\"id\":\"2\",\"name\":\"Leg1Pow5s\",\"userid\":\"1\",\"tag\":\"Node:6\",\"time\":1423587631,\"value\":\"824\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"6490280\",\"engine\":\"6\"},{\"id\":\"3\",\"name\":\"Leg1volt5s\",\"userid\":\"1\",\"tag\":\"Node:6\",\"time\":1423587631,\"value\":\"12158\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"6490240\",\"engine\":\"6\"},{\"id\":\"4\",\"name\":\"Leg1temp5s\",\"userid\":\"1\",\"tag\":\"Node:6\",\"time\":1423587631,\"value\":\"-12\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"6490188\",\"engine\":\"6\"},{\"id\":\"5\",\"name\":\"Leg2pow5s\",\"userid\":\"1\",\"tag\":\"Node:7\",\"time\":1423587623,\"value\":\"856\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"6490152\",\"engine\":\"6\"},{\"id\":\"6\",\"name\":\"Leg2volt5s\",\"userid\":\"1\",\"tag\":\"Node:7\",\"time\":1423587623,\"value\":\"121.49\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"6490100\",\"engine\":\"6\"},{\"id\":\"7\",\"name\":\"Leg2temp5s\",\"userid\":\"1\",\"tag\":\"Node:7\",\"time\":1423587623,\"value\":\"-8\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"6490036\",\"engine\":\"6\"},{\"id\":\"8\",\"name\":\"dishwasher\",\"userid\":\"1\",\"tag\":\"Node:5\",\"time\":1416241967,\"value\":\"2\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"744636\",\"engine\":\"6\"},{\"id\":\"9\",\"name\":\"fancoil\",\"userid\":\"1\",\"tag\":\"Node:5\",\"time\":1416241967,\"value\":\"0\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"744612\",\"engine\":\"6\"},{\"id\":\"10\",\"name\":\"voltage\",\"userid\":\"1\",\"tag\":\"Node:5\",\"time\":1416241967,\"value\":\"125.66\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"744568\",\"engine\":\"6\"},{\"id\":\"11\",\"name\":\"leg1Pow5sts\",\"userid\":\"1\",\"tag\":\"Node:6\",\"time\":1423587631,\"value\":\"824\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"482076\",\"engine\":\"2\"},{\"id\":\"12\",\"name\":\"ERV\",\"userid\":\"1\",\"tag\":\"Node:11\",\"time\":1423587621,\"value\":\"0\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"2326848\",\"engine\":\"6\"},{\"id\":\"13\",\"name\":\"ERVvolt15s\",\"userid\":\"1\",\"tag\":\"Node:11\",\"time\":1423587621,\"value\":\"122.5\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"2326972\",\"engine\":\"6\"},{\"id\":\"15\",\"name\":\"Microwavevolt20s\",\"userid\":\"1\",\"tag\":\"Node:12\",\"time\":1423587622,\"value\":\"0\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"1868532\",\"engine\":\"6\"},{\"id\":\"27\",\"name\":\"MicrowavePowerSafeplug\",\"userid\":\"1\",\"tag\":\"Node:12\",\"time\":1423587622,\"value\":\"0\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"156396\",\"engine\":\"6\"},{\"id\":\"16\",\"name\":\"CoffeePow20s\",\"userid\":\"1\",\"tag\":\"Node:13\",\"time\":1423079504,\"value\":\"0\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"1868500\",\"engine\":\"6\"},{\"id\":\"17\",\"name\":\"CoffeeVolt20s\",\"userid\":\"1\",\"tag\":\"Node:13\",\"time\":1423079504,\"value\":\"122.75\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"1868484\",\"engine\":\"6\"},{\"id\":\"20\",\"name\":\"HouseAPower10s\",\"userid\":\"1\",\"tag\":\"Node:9\",\"time\":1423587626,\"value\":\"1283\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"161996\",\"engine\":\"6\"},{\"id\":\"19\",\"name\":\"FridgeVolt\",\"userid\":\"1\",\"tag\":\"Node:14\",\"time\":1423587621,\"value\":\"122.5\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"3230044\",\"engine\":\"6\"},{\"id\":\"21\",\"name\":\"HouseAVolt10s\",\"userid\":\"1\",\"tag\":\"Node:9\",\"time\":1423587626,\"value\":\"120.08\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"161996\",\"engine\":\"6\"},{\"id\":\"22\",\"name\":\"HouseAPowLeg210s\",\"userid\":\"1\",\"tag\":\"Node:10\",\"time\":1423587626,\"value\":\"1220\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"162400\",\"engine\":\"6\"},{\"id\":\"23\",\"name\":\"HouseALeg2Volt10s\",\"userid\":\"1\",\"tag\":\"Node:10\",\"time\":1423587626,\"value\":\"120.98\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"162400\",\"engine\":\"6\"},{\"id\":\"26\",\"name\":\"FridgePowerSafeplug\",\"userid\":\"1\",\"tag\":\"Node:14\",\"time\":1423587621,\"value\":\"0\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"156560\",\"engine\":\"6\"},{\"id\":\"24\",\"name\":\"HotWaterPowSafeplug\",\"userid\":\"1\",\"tag\":\"Node:21\",\"time\":1423587624,\"value\":\"3000\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"158712\",\"engine\":\"6\"},{\"id\":\"25\",\"name\":\"HotWaterVoltSafeplug\",\"userid\":\"1\",\"tag\":\"Node:21\",\"time\":1423587624,\"value\":\"242.907\",\"datatype\":\"1\",\"public\":\"0\",\"size\":\"158664\",\"engine\":\"6\"},{\"id\":\"28\",\"name\":\"Washing machinePow\",\"userid\":\"1\",\"tag\":\"Node:18\",\"time\":1423587623,\"value\":\"0\",\"datatype\":\"1\",\"public\":\"0\",\"size\":null,\"engine\":\"6\"},{\"id\":\"29\",\"name\":\"washingMVoltage\",\"userid\":\"1\",\"tag\":\"Node:18\",\"time\":1423587623,\"value\":\"122.5\",\"datatype\":\"1\",\"public\":\"0\",\"size\":null,\"engine\":\"6\"}]\n";

        //a constructor that initializes the variables
        public LiveData(String idVar, String name, String userId,String tagVar, String time, String value,String dataType, String publicVar,String size, String engine){
            this.idVar = idVar;
            this.name = name;
            this. userId = userId;
            this.tagVar = tagVar;
            this.time = time;
            this.value = value;
            this.dataType = dataType;
            this.publicVar = publicVar;
            this.size = size;
            this.engine = engine;
        }
    }

}
