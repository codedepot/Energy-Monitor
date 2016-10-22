package com.example.ash.energymonitor.widgetLib;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;

import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Stack;
import java.util.logging.Level;

/**
 * Created by Mehrdad on 11/12/2014.
 * This is the the class would be used to draw widgets
 * the methods have parameters that specify how the widget would look
 */
public class WidgetDrawer {

    /**This method draws a simple dial that is 270 degrees of circle
     *
     * @param canvas
     * @param rectF the location of the widget
     * @param numLevels gradient that specifies the number of red to green levels
     * @param percentValue the current value in percentage
     * @param scaleValue how much bigger than the rectF to make it
     * @param paint
     */
    public static void drawDial(Canvas canvas, RectF rectF, int numLevels,float displayValue, WidgetViewConfig widgetViewConfig, Paint paint){
        float startAngle = 45;
        float endAngle = -225;
        float diffAngle = startAngle - endAngle;
        for(int i = 1; i<=numLevels; i++){
            paint.setColor(Color.rgb((int)WidgetDrawer.redToGreenTrans(i,numLevels,true),(int)WidgetDrawer.redToGreenTrans(i,numLevels,false),0));
            canvas.drawArc(rectF, endAngle + (i-1)*(diffAngle/numLevels),diffAngle/numLevels, true, paint);
        }
        //drawing the white one
        paint.setColor(Color.WHITE);
        canvas.drawOval(WidgetDrawer.scaleRectF(rectF,0.5f),paint);


        //drawing the needle:
        //first must find the final position of needle
        //this is because percentage is being used
        float maxValue=100;
        float minValue = 0;


        if(widgetViewConfig!=null) {
            maxValue = widgetViewConfig.minAndMax[1];
            minValue = widgetViewConfig.minAndMax[0];
            displayValue = widgetViewConfig.singleData;
        }
        float range = maxValue - minValue;
        float dialScale = 1.1f;
        float dialMin = maxValue - range*dialScale;
        float dialMax = minValue + range*dialScale;
        float dialRange = range*(dialScale*2-1);
        float angelAt = ((displayValue-dialMin)/(dialRange))*diffAngle;
        angelAt = angelAt +endAngle;
        //Log.d("Drawer", "The power value is " +displayValue + "The dial min and max are: " + dialMin + ", "+dialMax + "the range + " + dialRange+  ". The angelAt value: " + angelAt);
        float angelAtRad = (float) (angelAt*Math.PI)/180;

        float xCor = (float) Math.cos(angelAtRad)*(rectF.right - rectF.left)/2 + rectF.centerX() ;
        float yCor = (float) Math.sin(angelAtRad)*(rectF.right - rectF.left)/2 + rectF.centerY() ;
        //Log.d("Drawer", "xCor is :" + xCor + ". yCor is: " + yCor);
       //Log.d("wtf", "this is centerX and centerY: " + rectF.centerX() + ", " + rectF.centerY());
        //Log.d("wtf", "this is xCor and yCor: " + xCor + ", " + yCor);
        paint.setColor(Color.BLACK);
        //canvas.drawLine(xCor,yCor, rectF.centerX(),rectF.centerY(),paint);
        float needleDiffX = rectF.centerX() - xCor;
        float needleDiffY = rectF.centerY() - yCor;

        float needleAngle = (float) Math.atan(needleDiffY/needleDiffX);

        //assumes its a circle
        float radius = (rectF.right -rectF.left)/2;
        RectF needleRect = new RectF(xCor - radius, yCor - radius, xCor + radius, yCor + radius);
        float needleWidth = 10f;
        float needleAngleDeg = (float) (180*(needleAngle/(Math.PI)));
        //Log.d("Drawer", "needleAngleDeg is before if" + needleAngleDeg);
        if(needleDiffX>0){

        }
//        if(displayValue>(dialMax-dialMin)/2){
        else{
            needleAngleDeg -= 180;
        }

        //Log.d("Drawer","Needle angle deg " + needleAngleDeg);
        paint.setColor(Color.GRAY);
        canvas.drawArc(needleRect,needleAngleDeg - needleWidth/2, needleWidth, true, paint);



        //drawing one oval

        paint.setColor(Color.GRAY);
        canvas.drawOval(WidgetDrawer.scaleRectF(rectF,0.43f),paint);

        //drawing the value of the dial with a %
        paint.setColor(Color.WHITE);
        float scale = (1/100.0f)*((rectF.right - rectF.left)/2);
        //Log.d("wtf", "Scale value: " + scale);

        paint.setTextSize((int) (25*scale));
        Typeface robotoBold = Typeface.create("Roboto",Typeface.BOLD);
        paint.setTypeface(robotoBold);

        String unit ="W";
        if(displayValue>119f && displayValue<125f){
        unit = "V";
        }
        //displayValue = Math.round(displayValue);
        String printValue = "" + Math.round(displayValue);
        Log.d("Drawer","here " + ("" + displayValue).length());
        if(printValue.length() ==4){
            canvas.drawText("" + Math.round(displayValue) + unit, rectF.centerX() - 40 * scale, rectF.centerY() + 7 * scale, paint);
            Log.d("Drawer","here ");
        }else if(printValue.length() ==3){
            canvas.drawText("" + Math.round(displayValue) + unit, rectF.centerX() - 35 * scale, rectF.centerY() + 7 * scale, paint);
        }else if(printValue.length() == 2){
            canvas.drawText("" + Math.round(displayValue) + unit, rectF.centerX() - 20 * scale, rectF.centerY() + 7 * scale, paint);
        }else if(printValue.length() == 1){
            canvas.drawText("" + Math.round(displayValue) + unit, rectF.centerX() - 25 * scale, rectF.centerY() + 7 * scale, paint);
        }
        else if(displayValue < (dialMax-dialMin)/10){
            canvas.drawText("" +  Math.round(displayValue) + unit, rectF.centerX() - 25 * scale, rectF.centerY() + 7 * scale, paint);
        }else {
            canvas.drawText("" + Math.round(displayValue) + unit, rectF.centerX() - 45 * scale, rectF.centerY() + 7 * scale, paint);
        }

    }

    /**This method would draw a simple bar graph from a stack
     *
     *
     * @param canvas
     * @param rectF
     * @param data
     */
    public static void drawBarGraph(Canvas canvas, RectF rectF,  WidgetViewConfig widgetViewConfig){
        RectF outSideRectF = new RectF(rectF.left, rectF.top, rectF.right, rectF.bottom);
        rectF = new RectF(rectF.left, rectF.top, rectF.right, rectF.bottom-30);
        Paint paint = widgetViewConfig.colorList.elementAt(0);
        paint.setStyle(Paint.Style.STROKE);
        //canvas.drawRect(rectF, paint);
        //For testing purposes the arguments are being initialized here
        //DELETE THIS CODE LATER
        float tempMax = Float.MIN_VALUE;
        int size = widgetViewConfig.valueArray.length;
        for(int i =0; i<size; i++){
                if(tempMax<widgetViewConfig.valueArray[i]){
                    tempMax = widgetViewConfig.valueArray[i];
                }
        }
        widgetViewConfig.colorList.elementAt(0).setStyle(Paint.Style.FILL);
        Stack<Double> data = new Stack<Double>();
        Double max = 100.0;
        for(int i = 0; i<10; i++){
            data.add(Math.random()*tempMax);
        }
        //Log.d("BarGraph", "is it null? " + Arrays.toString(widgetViewConfig.namesArray));
        //widgetViewConfig.namesArray = new String[]{"Wed", "Thu", "Fri", "Sat", "Sun", "Mon", "Tue"};
        //canvas.drawRect(rectF.left,rectF.top, rectF.right, rectF.bottom, widgetViewConfig.colorList.elementAt(0));
        //making a simple graph
        float unit = rectF.width()/(size*3 + (size-1)*2 +1);

        float barWidth = unit*3, barDist = unit*2;
        for(int i = 0; i<size; i++){
            float height = (float) ((rectF.height()*0.8)*widgetViewConfig.valueArray[i]/tempMax);
            canvas.drawRect(rectF.left + 5 + i*(barDist + barWidth),rectF.bottom -height, rectF.left + barWidth + i*(barDist + barWidth), rectF.bottom, widgetViewConfig.colorList.elementAt(1));
            String dataNum = String.format("%.2f",widgetViewConfig.valueArray[i]);
            canvas.drawText("$" + dataNum,rectF.left + 7 + i*(barDist + barWidth),rectF.bottom -height - 5,widgetViewConfig.colorList.elementAt(2));

            canvas.drawText(widgetViewConfig.namesArray[i], rectF.left + 18 + i*(barDist + barWidth),rectF.bottom+15, widgetViewConfig.colorList.elementAt(0));
        }

        //draw the axes
       canvas.drawRect(rectF.left,rectF.top, rectF.left + 2, rectF.bottom, widgetViewConfig.colorList.elementAt(0));
        canvas.drawRect(rectF.left, rectF.bottom - 2, rectF.right, rectF.bottom, widgetViewConfig.colorList.elementAt(0));

        Log.d("myTag", "the bottom: " + rectF.height());
    }

    /** this draws a basic run graph
     *
     * @param canvas the canvas of the view
     * @param rectF the drawing area
     * @param inputData a hashtable containing two x and y coordinates
     * @param paint might be phased out soon.
     * @param widgetViewConfig it contains configurations like main colors and axis graduation
     */
    public static void drawRunGraph(Canvas canvas, RectF rectF, Hashtable<Long, Float> inputData, Paint paint, WidgetViewConfig widgetViewConfig){

        //the padding of the graph
        int padLeft = 50, padTop = 10, padRight = 10, padBottom = 50;

        //making rand data for example and setting it to the param
        Stack<Double[]> randData = new Stack<Double[]>();

        //setting the paint variables
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rectF, paint);

        //drawing the background
        RectF border = new RectF(rectF.left + padLeft, rectF.top +padTop, rectF.right - padRight, rectF.bottom - padBottom);
        canvas.drawRect(border, widgetViewConfig.colorList.elementAt(1));
        //drawing a boarder
        canvas.drawRect(border, widgetViewConfig.colorList.elementAt(0));

        float maxValue = 0f;
        float minValue = 0f;

        try {
            //scaling the data to the graph length
            float scale = ((border.width() - 1)/widgetViewConfig.valueArray.length);
            minValue = WidgetDrawer.getMinAndMax(widgetViewConfig.valueArray)[0];
            maxValue = WidgetDrawer.getMinAndMax(widgetViewConfig.valueArray)[1];
            float graphMin = maxValue - (maxValue -minValue)*1.1f;
            float graphMax = minValue + (maxValue - minValue)*1.1f;
            float graphRange = graphMax - graphMin;
            //Log.d("Proto", "graph min is " + minValue + " garph max is " + maxValue + " and this is the data " + Arrays.toString(widgetViewConfig.valueArray));

            RectF dataPointDraw;

            float curValue;
            float drawLeft, drawTop, drawRight, drawBottom;
            for(int i = 0; i<widgetViewConfig.valueArray.length;i++){
                //curValue = enumeration.nextElement();
                curValue = widgetViewConfig.valueArray[i];
                float dataHeight = curValue - graphMin;
                drawLeft = border.left + i * scale;
                drawTop = border.top + ((graphRange - dataHeight) / graphRange) * border.height();
                drawRight = border.left + scale * (i + 1);
                drawBottom = border.bottom - 1;
                dataPointDraw = new RectF(border.left + i * scale, drawTop, drawRight, drawBottom);

                canvas.drawRect(dataPointDraw, widgetViewConfig.colorList.elementAt(2));
                //Log.d("Frag", "Left: " + drawLeft + ", Top: " + drawTop + ", Right: " + drawRight + ", Bottom: " + drawBottom );
                //canvas.drawRect();

                //drawing the top highlight
                dataPointDraw = new RectF(border.left + i * scale, drawTop, drawRight, drawTop + 6);
                canvas.drawRect(dataPointDraw, widgetViewConfig.colorList.elementAt(3));


            }

            //Drawing the x axis
            int numOnXAxis = widgetViewConfig.axisGrad[0];
            //int mod = inputData.size()/numOnXAxis;
            long tempValue;
            long tempDifference = widgetViewConfig.timesArray[widgetViewConfig.timesArray.length-1] - widgetViewConfig.timesArray[0];
           // Log.d("Proto", "This is the time diff" + tempDifference);
            for(int i = 0; i <numOnXAxis; i++){
                tempValue = widgetViewConfig.timesArray[i];
                Date tempDate = new Date(widgetViewConfig.timesArray[0] + (tempDifference/numOnXAxis)*i);
                String tempDraw = "" + numToString(tempDate.getDate()) + "/" + (numToString(tempDate.getMonth() +1)) + "/" + (tempDate.getYear()+1900) + " - ";
                tempDraw += numToString(tempDate.getHours()) + ":" + numToString(tempDate.getMinutes());
                canvas.drawText(tempDraw,border.left + i*border.width()/numOnXAxis,border.bottom + 20, widgetViewConfig.colorList.elementAt(0));
                //Log.d("Proto", "This is the month " +  numToString(tempDate.getMonth()) +", " +   numToString(tempDate.getHours()) + ":" +  numToString(tempDate.getMinutes()));



                //Log.d("Proto", "The value is: " + tempValue);
            }

            //Drawing the y-axis
            for( int i =0; i<= widgetViewConfig.axisGrad[1]; i++){
                canvas.drawText("" + Math.round(i * (graphRange / widgetViewConfig.axisGrad[1]) + graphMin) + "W",border.left -45,border.bottom - i*border.height()/widgetViewConfig.axisGrad[1], widgetViewConfig.colorList.elementAt(0));
            }

        }catch(Exception e){
          Log.d("Proto", "Error on drawing runchart" + e);
        }




    }


    public static void drawCompareBins(Canvas canvas, RectF rectF, Hashtable<String,Float> data,  WidgetViewConfig widgetViewConfig){
        RectF widgetBorder =new RectF(rectF.left,rectF.top,rectF.right,rectF.bottom);
        int legendPad = 20;
        rectF = new RectF(rectF.left,rectF.top,rectF.right,rectF.bottom - legendPad);
        float ratio = 0.15f;
        int dataSize = widgetViewConfig.valueArray.length;
        float spacing = ratio*rectF.width()/dataSize;
        float size = (1- ratio)*( (rectF.width()-spacing)/dataSize);
        //Enumeration<String> names = data.keys();
        //Enumeration<Float> values = data.elements();
        float maxDeterminedValue = Float.MIN_VALUE;
        float tempValue = 0f;
        for(int i = 0; i<dataSize; i++){
            tempValue = widgetViewConfig.valueArray[i];
            if(tempValue>maxDeterminedValue){
                maxDeterminedValue = tempValue;
            }
        }

        //values = data.elements();
        maxDeterminedValue = maxDeterminedValue*1.1f;
        RectF valueBar = new RectF();
        //applying the fix
        float pad = 10;
        float binHeight = rectF.height() - pad*2;
        float initialPad = spacing;
        float binPad = spacing*0.2f;
        float maxValue = maxDeterminedValue;

        Float curValue; String curName;
        for(int i =0; i<dataSize;i++){
            curValue = widgetViewConfig.valueArray[i]; curName = widgetViewConfig.namesArray[i];
            //drawing the value bar
            valueBar = new RectF(rectF.left + i*spacing + i*size+ initialPad, rectF.top+pad+(binHeight - (curValue/maxValue)*binHeight)   ,   rectF.left + i*spacing + initialPad + (i+1)*size, rectF.bottom -pad );
            canvas.drawRect(valueBar, widgetViewConfig.colorList.elementAt(1));
            //drawing the values of the bins
            Log.d("Drawer", "This is the name: " + curName);
            if((""+widgetViewConfig.units).equals("$")){
                Paint tempPaint = new Paint();
                tempPaint.setTextSize(20);
                canvas.drawText("$" + String.format("%.2f", curValue)  , valueBar.centerX()-37, valueBar.centerY(),tempPaint);
            }else {

                canvas.drawText("" + curValue + "kWh", valueBar.centerX(), valueBar.centerY(), widgetViewConfig.colorList.elementAt(0));
           }

            //drawing a bin
            valueBar = new  RectF(rectF.left + i*spacing + i*size+ initialPad - binPad, rectF.top + binPad,rectF.left + i*spacing + initialPad + (i+1)*size + binPad, rectF.bottom -10 +binPad );
            canvas.drawRect(valueBar, widgetViewConfig.colorList.elementAt(0));




            //Drawing the legend at the bottom


            canvas.drawText(curName, valueBar.centerX(), rectF.bottom + 15,  widgetViewConfig.colorList.elementAt(0));
        }
        //Paint testPaint = widgetViewConfig.colorList.elementAt(0);
        //testPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rectF, widgetViewConfig.colorList.elementAt(0));


    }



    /** This is a helper method that allows a good transition between
     * green and red. It is a used in the dial class
     *
     * @param curLevel The percentage the dials should be at
     * @param levels the number of levels that the dials has
     * @param isRed
     * @return
     */
    public static float redToGreenTrans(float curLevel, float levels, boolean isRed){
      if(curLevel == 1){
          if(isRed){
              return 210;
          }else{
              return 0;
          }
      }
        //which color should the dial selector be
        //the choice starts with red and ends as green
        float index = 420/(levels -1)*(curLevel-1);
        if(index >= 210){
            if(isRed){
                return 420 - index;
            }else{
                return 210;
            }
        }else{
            if(isRed){
                return 210;
            }else{
                return index;
            }
        }

       //return 0;
    }

    /**A Helper method that allows a RectF to be made
     * into a smaller or bigger one. 0.5 would be half the size
     * @param rectF
     * @param ratio
     * @return
     */
    public static RectF scaleRectF(RectF rectF, float ratio){

        ratio = ratio/2;

        float rW =  rectF.right - rectF.left;
        //Log.d("wtf", "rW value div ratio " + rW*ratio);
        float rH = rectF.bottom - rectF.top;
        RectF tempRectF = new RectF(rectF.centerX() - rW*ratio,rectF.centerY() -rH*ratio, rectF.centerX() + rW*ratio,rectF.centerY() +rH*ratio);
        return tempRectF;
    }


    /** Gets the minimum and maximum values
     *
     * @param input an array of floating point data
     * @return an array of size two where the first element is
     * the min and the second is the max.
     */
    public static float[] getMinAndMax(float[] input){
        float[] output = new float[2];
        float tempMax = Float.MAX_VALUE*-1;
        float tempMin = Float.MAX_VALUE;
        for(int i = 0; i <input.length; i++){
            if(input[i]>tempMax){
                tempMax = input[i];
            }if(input[i]<tempMin){
                tempMin = input[i];
            }
        }
        output[0] = tempMin;
        output[1] = tempMax;
        return output;
    }

    public static String numToString(int input){
        String output = "" + input;
        if(output.length() ==1){
            output = "0" + output;
        }
        return output;
    }
}






