package com.example.ash.energymonitor.dataRetrieval;

import android.util.Log;

import com.example.ash.energymonitor.widgetLib.WidgetViewConfig;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mehrdad on 3/13/2015.
 */
public class CostCalc {
    public static float OFF_PEAK_RATE = 6.5f*0.01f;
    public static float MID_PEAK_RATE = 10f*0.01f;
    public static float ON_PEAK_RATE = 11.7f*0.01f;
    public static float CONSTANT_RATE = 4.066f*0.01f;

    public static final long MILLIS_IN_DAY=86400000;

    public static float getTotalCost(long[] times, float[] values){
        float sumCost = 0f;
        for(int i =0; i<times.length; i++){
            sumCost += CostCalc.getRateByTime(times[i])*values[i]/1000f;
            Log.d("Cost", "" +values[i]);
        }
        Log.d("Cost", "ad " +times.length);
        float aveCost = sumCost/times.length;
        long delta = times[times.length -1] - times[0] + (times[1] - times[0]);

        //float totalCost = aveCost*0.001f*24f*30f;
        float totalCost = (aveCost)*(delta/1000f/3600f);
        //Log.d("Cost", "total cost " +totalCost);
        return totalCost;
    }



    public static float getRateByTime(long time){
        Date inputDate = new Date(time);
        int day = inputDate.getDay();
        int hours = inputDate.getHours();
        int month = inputDate.getMonth();
        int minutes = inputDate.getMinutes();
        if(day == 6 ||day == 0){
            return OFF_PEAK_RATE + CONSTANT_RATE;
        }else{
            if(hours<= 6 || hours>=19){


            return OFF_PEAK_RATE+ CONSTANT_RATE;
            }
            //if Winter
            if(month<= 3 || month>=10) {
                 //if in morning and evening
                 if (hours >= 7 && hours <10 || hours<=17 && hours<18) {
                    return ON_PEAK_RATE + CONSTANT_RATE;
                } else if(hours<=11 && hours<16) {
                    return MID_PEAK_RATE + CONSTANT_RATE;
                }else if(hours ==6){
                    return CostCalc.linInterpolate( OFF_PEAK_RATE+ CONSTANT_RATE, ON_PEAK_RATE + CONSTANT_RATE, minutes);
                 }else if(hours ==10){
                     return CostCalc.linInterpolate( ON_PEAK_RATE + CONSTANT_RATE, MID_PEAK_RATE + CONSTANT_RATE, minutes);
                 }else if(hours ==16){
                     return CostCalc.linInterpolate( MID_PEAK_RATE + CONSTANT_RATE ,ON_PEAK_RATE + CONSTANT_RATE, minutes);
                 }else if(hours ==18){
                     return CostCalc.linInterpolate(ON_PEAK_RATE + CONSTANT_RATE  , OFF_PEAK_RATE + CONSTANT_RATE, minutes);
                 }
            }else{
            //if Summer
                //if in morning and evening
                if (hours >= 7 && hours <10 || hours<=17 && hours<18) {
                    return MID_PEAK_RATE + CONSTANT_RATE;
                } else if(hours<=11 && hours<16) {
                    return ON_PEAK_RATE + CONSTANT_RATE;
                }else if(hours ==6){
                    return CostCalc.linInterpolate( OFF_PEAK_RATE+ CONSTANT_RATE, MID_PEAK_RATE + CONSTANT_RATE, minutes);
                }else if(hours ==10){
                    return CostCalc.linInterpolate( MID_PEAK_RATE + CONSTANT_RATE, ON_PEAK_RATE + CONSTANT_RATE, minutes);
                }else if(hours ==16){
                    return CostCalc.linInterpolate( ON_PEAK_RATE + CONSTANT_RATE ,MID_PEAK_RATE + CONSTANT_RATE, minutes);
                }else if(hours ==18){
                    return CostCalc.linInterpolate(MID_PEAK_RATE + CONSTANT_RATE  , OFF_PEAK_RATE + CONSTANT_RATE, minutes);
                }
            }
        }
       return MID_PEAK_RATE + CONSTANT_RATE;
    }


    public static float[] getWeeklyCost(long times[], float[] values, WidgetViewConfig widgetViewConfig){

        Calendar[] week = new Calendar[7];
        int[] readingsCount = new int[7];
        float[] costOutput = new float[7];
        widgetViewConfig.namesArray = new String[7];
        for(int i  = 0; i <week.length; i++){
            week[i] = Calendar.getInstance();
            week[i].setTimeInMillis(times[0] + i*MILLIS_IN_DAY);

            switch(week[i].get(Calendar.DAY_OF_WEEK)){
                case Calendar.SATURDAY:
                    widgetViewConfig.namesArray[i] = "Sat";
                    break;
                case Calendar.SUNDAY:
                    widgetViewConfig.namesArray[i] = "Sun";
                    break;

                case Calendar.MONDAY:
                    widgetViewConfig.namesArray[i] = "Mon";
                    break;

                case Calendar.TUESDAY:
                    widgetViewConfig.namesArray[i] = "Tue";
                    break;

                case Calendar.WEDNESDAY:
                    widgetViewConfig.namesArray[i] = "Wed";
                    break;

                case Calendar.THURSDAY:
                    widgetViewConfig.namesArray[i] = "Thu";
                    break;

                case Calendar.FRIDAY:
                    widgetViewConfig.namesArray[i] = "Fri";
                    break;

                default:
                    break;
            }

            //days[i] = week[i].get(Calendar.DAY_OF_WEEK);
        }
        Log.d("BarGraph", "WTF MAN, week values are: " + Arrays.toString(values)) ;
        try {
            Calendar tempStamp = Calendar.getInstance();
            for (int i = 0; i < times.length; i++) {
                tempStamp.setTimeInMillis(times[i]);

                if (week[0].get(Calendar.DAY_OF_WEEK) == tempStamp.get(Calendar.DAY_OF_WEEK)) {
                    costOutput[0] += (values[i] / 1000f) * CostCalc.getRateByTime(times[i]);
                    readingsCount[0]++;
                } else if (week[1].get(Calendar.DAY_OF_WEEK) == tempStamp.get(Calendar.DAY_OF_WEEK)) {
                    costOutput[1] += (values[i] / 1000f) * CostCalc.getRateByTime(times[i]);
                    readingsCount[1]++;
                } else if (week[2].get(Calendar.DAY_OF_WEEK) == tempStamp.get(Calendar.DAY_OF_WEEK)) {
                    costOutput[2] += (values[i] / 1000f) * CostCalc.getRateByTime(times[i]);
                    readingsCount[2]++;
                } else if (week[3].get(Calendar.DAY_OF_WEEK) == tempStamp.get(Calendar.DAY_OF_WEEK)) {
                    costOutput[3] += (values[i] / 1000f) * CostCalc.getRateByTime(times[i]);
                    readingsCount[3]++;
                } else if (week[4].get(Calendar.DAY_OF_WEEK) == tempStamp.get(Calendar.DAY_OF_WEEK)) {
                    costOutput[4] += (values[i] / 1000f) * CostCalc.getRateByTime(times[i]);
                    readingsCount[4]++;
                } else if (week[5].get(Calendar.DAY_OF_WEEK) == tempStamp.get(Calendar.DAY_OF_WEEK)) {
                    costOutput[5] += (values[i] / 1000f) * CostCalc.getRateByTime(times[i]);
                    readingsCount[5]++;
                } else if (week[6].get(Calendar.DAY_OF_WEEK) == tempStamp.get(Calendar.DAY_OF_WEEK)) {
                    costOutput[6] += (values[i] / 1000f) * CostCalc.getRateByTime(times[i]);
                    //Log.d("BarGraph", "Values " + values[i]);
                    readingsCount[6]++;
                } else {

                }


            }
        }catch(Exception e){
            Log.d("BarGraph", "error is " + e);
        }
        float[] output = new float[costOutput.length];
        for(int i= 0; i<costOutput.length; i++){
            output[i] = (costOutput[i]/readingsCount[i])*24;
        }


        return output;
    }
    //a method to test whether the pricing system works


    public static float linInterpolate(float lowerB, float upperB, float minValue){



        return (upperB-lowerB)*(minValue/60)+lowerB;

    }
    public static void testCostCalc(){
        Calendar tempCal = Calendar.getInstance();
        tempCal.set(2015, 2,15, 13,37);
        float tempRate = CostCalc.getRateByTime(tempCal.getTimeInMillis());
        Log.d("CostTest", "sunday afternoon" + tempRate);

        tempCal.set(2015, 2,16, 6,30);
        tempRate = CostCalc.getRateByTime(tempCal.getTimeInMillis());
        Log.d("CostTest", tempCal.get(Calendar.DAY_OF_WEEK) + " morning " +  + tempRate);

        tempCal.set(2015, 2,16, 7,30);
        tempRate = CostCalc.getRateByTime(tempCal.getTimeInMillis());
        Log.d("CostTest", tempCal.get(Calendar.DAY_OF_WEEK) + " 7:30 " +  + tempRate);

        tempCal.set(2015, 2,16, 12,30);
        tempRate = CostCalc.getRateByTime(tempCal.getTimeInMillis());
        Log.d("CostTest", tempCal.get(Calendar.DAY_OF_WEEK) + " 12:30 " +  + tempRate);

        tempCal.set(2015, 2,16, 17,1);
        tempRate = CostCalc.getRateByTime(tempCal.getTimeInMillis());
        Log.d("CostTest", tempCal.get(Calendar.DAY_OF_WEEK) + " 5:01 " +  + tempRate);

        tempCal.set(2015, 2,14, 17,1);
        tempRate = CostCalc.getRateByTime(tempCal.getTimeInMillis());
        Log.d("CostTest", tempCal.get(Calendar.DAY_OF_WEEK) + " tester sat " +  + tempRate);

        tempCal.set(2015, 2,13, 17,1);
        tempRate = CostCalc.getRateByTime(tempCal.getTimeInMillis());
        Log.d("CostTest", tempCal.get(Calendar.DAY_OF_WEEK) + " tester fri " +  + tempRate);
    }
}
